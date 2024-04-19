package com.zyf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zyf.common.ErrorCode;
import com.zyf.constant.UserConstant;
import com.zyf.model.domain.User;
import com.zyf.exception.BusinessException;
import com.zyf.mapper.UserMapper;
import com.zyf.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;
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

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
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
        // 账号不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        int count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号重复");
        }
        // 2. 密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3. 插入数据
        User user = new User();
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
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = currentUser.getId();
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
        safetyUser.setProfile(user.getProfile());
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

    @Override
    public int updateUser(User user, User loginUser) {
        // 检验要修改的用户
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // TODO 补充校验，如果用户没有传任何要更新的值，就直接报错，不用执行 update 语句
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
        Long userId = loginUser.getId();
        String redisKey = null;
        if (userId != null) {
            redisKey = String.format("partner-match:user:recommend:%s", userId);
        } else {
            redisKey = String.format("partner-match:user:recommend:%s", -1);
        }
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        // 如果有缓存，直接读缓存
        Page<User> userPage = (Page<User>) valueOperations.get(redisKey);
        if (userPage == null) {
           // 如果没有缓存，查数据库
            userPage = this.page(new Page<>(pageNum, pageSize));

            try {
                valueOperations.set(redisKey, userPage, 30000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                log.error("redis set key error");
            }
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

    /**
     * 根据标签名称搜索用户（SQL 查询版）
     *
     * @param tagNameList 标签名称列表
     * @return 搜索到的用户
     */
    @Deprecated
    private List<User> searchUsersByTagNamesBySQL(List<String> tagNameList) {
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
}




