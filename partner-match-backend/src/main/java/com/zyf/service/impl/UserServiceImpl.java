package com.zyf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zyf.common.ErrorCode;
import com.zyf.config.MinioConfig;
import com.zyf.constant.AvatarConstant;
import com.zyf.constant.UserConstant;
import com.zyf.model.domain.User;
import com.zyf.exception.BusinessException;
import com.zyf.mapper.UserMapper;
import com.zyf.service.UserService;
import com.zyf.utils.AlgorithmUtils;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.zyf.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务
 *
 * @author zyf
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {
    /**
     * 账户正则，检验账户是否合法
     */
    private static final String VALID_PATTERN = "^[A-Za-z0-9]+$";

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "zyf";

    /**
     * 用户 Mapper
     */
    @Resource
    private UserMapper userMapper;

    /**
     * 操作 Redis 缓存
     */
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * Minio 配置项
     */
    @Resource
    private MinioConfig minioConfig;

    /**
     * Minio 客户端
     */
    @Resource
    private MinioClient minioClient;

    @Override
    public long userRegister(String username, String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(username, userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (username.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户昵称过短");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 账号不包含特殊字符
        Matcher matcher = Pattern.compile(VALID_PATTERN).matcher(userAccount);
        if (!matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号包含特殊字符");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码和校验密码不一致");
        }
        // 昵称和账号不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.eq("user_account", userAccount);
        int count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "昵称或账号重复");
        }
        // 2. 密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3. 插入数据
        User user = new User();
        user.setUsername(username);
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        int saveResult = userMapper.insert(user);
        if (saveResult != 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册用户失败");
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1.检验账户和密码是否合法
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 账户不包含特殊字符
        Matcher matcher = Pattern.compile(VALID_PATTERN).matcher(userAccount);
        if (!matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号包含特殊字符");
        }
        // 2.校验密码是否正确
        // 密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        queryWrapper.eq("user_password", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号和密码不匹配");
        }
        // 3.用户信息脱敏
        User safetyUser = getSafetyUser(user);
        // 4.记录登录态信息
        HttpSession session = request.getSession();
        session.setAttribute(USER_LOGIN_STATE, safetyUser);
        // 5.返回脱敏后的用户信息
        return safetyUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User loginUser = (User) userObj;
        if (loginUser == null) {
            return null;
        }
        long userId = loginUser.getId();
        User user = userMapper.selectById(userId);
        User safetyUser = this.getSafetyUser(user);
        return safetyUser;
    }

    public User getSafetyUser(User user) {
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "脱敏前的用户信息为空");
        }
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserProfile(user.getUserProfile());
        safetyUser.setTagNames(user.getTagNames());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setUserRole(user.getUserRole());
        safetyUser.setCreateTime(user.getCreateTime());
        return safetyUser;
    }


    @Override
    public List<User> searchUsersByTagNames(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 在数据库中进行条件查询
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        for (String tagName : tagNameList) {
            queryWrapper.like("tag_names", tagName);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }


    @Override
    public int updateUser(User user, User loginUser) {
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        // 检验要修改的用户
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long userId = user.getId();

        // 是否登录
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long loginUserId = loginUser.getId();
        // 登录的用户和修改的用户是否一致
        if (userId != loginUserId) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        // 获取修改前的用户
        User oldUser = userMapper.selectById(userId);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return userMapper.updateById(user);
    }

    @Override
    public List<User> recommendUsers(long pageSize, long pageNum, User loginUser) {
        String redisKey = null;
        if (loginUser != null && loginUser.getId() != null) {
            redisKey = String.format("partner-match:user:recommend:%s", loginUser.getId());
        } else {
            redisKey = String.format("partner-match:user:recommend:%s", -1);
        }
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        // 如果有缓存，直接读缓存
        Page<User> userPage = (Page<User>) valueOperations.get(redisKey);
        if (userPage == null) {
           // 如果没有缓存，查数据库
            if (loginUser == null) {
                userPage = this.page(new Page<>(pageNum, pageSize));
            } else {
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                queryWrapper.ne("id", loginUser.getId());
                userPage = this.page(new Page<>(pageNum, pageSize), queryWrapper);
            }
        }
        try {
            valueOperations.set(redisKey, userPage, 30000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("redis set key error");
        }
        List<User> userList = userPage.getRecords();
        return userList.stream().map((user) -> this.getSafetyUser(user)).collect(Collectors.toList());
    }

    /**
     * 获取指定队伍中的用户
     *
     * @param teamId 队伍id
     * @return 用户列表
     */
    @Override
    public List<User> getUsersByTeamId(Long teamId) {
        List<User> userList = userMapper.selectUsersByTeamId(teamId);
        List<User> safeUserList = userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
        return safeUserList;
    }

    @Override
    public boolean isAdmin(User loginUser) {
        if (loginUser == null || loginUser.getUserRole() != UserConstant.ADMIN_ROLE) {
            return false;
        }
        return true;
    }

    @Override
    public List<User> matchUsers(long num, User loginUser) {
        // 1. 条件判断
        if (num < 1 || num > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "匹配数量不符合要求");
        }
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "请先登录账号");
        }
        Long loginUserId = loginUser.getId();
        String redisKey = String.format("partner-match:user:match:%s", loginUserId);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        // 如果有缓存，直接读缓存
        List<User> finalUserList = (List<User>) valueOperations.get(redisKey);
        if (finalUserList == null) {
            // 如果没有缓存，查数据库
            String loginUserTagNames = loginUser.getTagNames();
            if (loginUserTagNames == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "请先设置标签");
            }
            Gson gson = new Gson();
            List<String> loginUserTagList = gson.fromJson(loginUserTagNames, new TypeToken<List<String>>() {
            }.getType());

            // 2. 查询数据库中有标签的用户
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("id", "tag_names");
            queryWrapper.ne("id", loginUserId);
            queryWrapper.isNotNull("tag_names");
            List<User> userList = this.list(queryWrapper);

            // 3. 筛选出标签最相似的 num 个 用户
            PriorityQueue<Pair<Long, Integer>> priorityQueue = new PriorityQueue<>((int)num, (p1, p2) -> p2.getValue() - p1.getValue());
            for (int i = 0; i < userList.size(); i++) {
                User user = userList.get(i);
                String tagNames = user.getTagNames();
                List<String> tagList = gson.fromJson(tagNames, new TypeToken<List<String>>() {
                }.getType());
                // 去除两个用户相同的标签
                List<String> tempLoginUserTagList = loginUserTagList.stream().filter(tag -> !tagList.contains(tag)).collect(Collectors.toList());
                List<String> tempTagList = tagList.stream().filter(tag -> !loginUserTagList.contains(tag)).collect(Collectors.toList());
                int similarity = AlgorithmUtils.minDistance(tempLoginUserTagList, tempTagList);
                if (priorityQueue.size() < num) {
                    priorityQueue.add(new Pair<>(user.getId(), similarity));
                } else {
                    if (similarity < priorityQueue.peek().getValue()) {
                        priorityQueue.poll();
                        priorityQueue.add(new Pair<>(user.getId(), similarity));
                    }
                }
            }
            List<Long> userIdList = new ArrayList<>();
            while (!priorityQueue.isEmpty()) {
                userIdList.add(priorityQueue.poll().getKey());
            }

            queryWrapper = new QueryWrapper<>();
            queryWrapper.in("id", userIdList);
            Map<Long, List<User>> userIdUserListMap = this.list(queryWrapper)
                    .stream()
                    .map(this::getSafetyUser)
                    .collect(Collectors.groupingBy(User::getId));
            finalUserList = new ArrayList<>();
            for (int i = userIdList.size() - 1; i >= 0; i--) {
                finalUserList.add(userIdUserListMap.get(userIdList.get(i)).get(0));
            }
        }
        try {
            valueOperations.set(redisKey, finalUserList, 30000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("redis set key error");
        }
        return finalUserList;
    }

    @Override
    public Boolean updateUserAvatar(MultipartFile avatarFile, User loginUser) {
        // 1. 对头像进行校验
        // 判断头像大小是否符合要求
        if (avatarFile.getSize() > AvatarConstant.AVATAR_MAX_SIZE) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "头像大小超出限制");
        }
        // 判断头像类型是否为图片
        String contentType = avatarFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "头像类型不符合要求");
        }

        // 2. 上传头像到对象存储服务器中
        String avatarUrl =  "user/" + UUID.randomUUID() + avatarFile.getOriginalFilename();
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioConfig.getBucket()) // 存储桶
                    .object(avatarUrl) // 文件名
                    .stream(avatarFile.getInputStream(), avatarFile.getSize(), -1) // 文件内容
                    .contentType(avatarFile.getContentType()) // 文件类型
                    .build());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "头像上传失败");
        }

        User oldUser = userMapper.selectById(loginUser.getId());
        String oldAvatarUrl = oldUser.getAvatarUrl();

        // 3. 更新用户的 avatarUrl
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("avatar_url", avatarUrl);
        updateWrapper.eq("id", loginUser.getId());
        boolean result = this.update(updateWrapper);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新用户失败");
        }

        // 4. 删除之前的头像
        if (StringUtils.isNotBlank(oldAvatarUrl)) {
            try {
                minioClient.removeObject(RemoveObjectArgs.builder().bucket(minioConfig.getBucket()).object(oldAvatarUrl).build());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }

    /**
     * 根据标签名称搜索用户（内存过滤版）
     *
     * @param tagNameList 标签名称列表
     * @return 搜索到的用户
     */
    @Deprecated
    private List<User> searchUsersByTagNamesByMemory(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 先查出所有数据，之后在内存中进行过滤
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        return userList.stream().filter((user) -> {
            String tagNameStr = user.getTagNames();
            if (StringUtils.isBlank(tagNameStr)) {
                return false;
            }
            Gson gson = new Gson();
            Set<String> tagNameSet = gson.fromJson(tagNameStr, new TypeToken<Set<String>>(){}.getType());
            for (String tagName : tagNameList) {
                if (!tagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }
}




