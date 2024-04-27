package com.zyf.service.impl;

import com.zyf.model.domain.User;
import com.zyf.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class UserServiceImplTest {
    @Resource
    private UserService userService;
    @Test
    void testAddUser() {
        User user = new User();
        user.setUsername("zyf127");
        user.setUserAccount("266520");
        user.setAvatarUrl("http://123.com");
        user.setGender(0);
        user.setUserPassword("12345678");
        user.setPhone("18765432311");
        user.setEmail("123@qq.com");
        user.setUserStatus(0);
        user.setTagNames("[\"Java\", \"C++\", \"Vue\"]");
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setIsDelete(0);
        userService.save(user);
    }

    @Test
    void testUserRegister() {
        String username = "流年";
        String userAccount = "liunian";
        String userPassword = "12345678";
        String checkPassword = "";
        Assertions.assertEquals(-1, userService.userRegister(username, userAccount, userPassword, checkPassword));

        userAccount = "zyf";
        userPassword = "123345678";
        checkPassword = "12345678";
        Assertions.assertEquals(-1, userService.userRegister(username, userAccount, userPassword, checkPassword));

        userAccount = "liunian";
        userPassword = "1234567";
        checkPassword = "1234567";
        Assertions.assertEquals(-1, userService.userRegister(username, userAccount, userPassword, checkPassword));

        userAccount = "266520";
        userPassword = "12345678";
        checkPassword = "12345678";
        Assertions.assertEquals(-1, userService.userRegister(username, userAccount, userPassword, checkPassword));

        userAccount = "liu nian";
        userPassword = "12345678";
        checkPassword = "12345678";
        Assertions.assertEquals(-1, userService.userRegister(username, userAccount, userPassword, checkPassword));

        userAccount = "liunian";
        userPassword = "12345678";
        checkPassword = "123456789";
        Assertions.assertEquals(-1, userService.userRegister(username, userAccount, userPassword, checkPassword));

        username = "练习生";
        userAccount = "zyf127";
        userPassword = "12345678";
        checkPassword = "12345678";
        long result = userService.userRegister(username, userAccount, userPassword, checkPassword);
        Assertions.assertTrue(result > 0);
    }

    @Test
    void testSearchUsersByTagNames() {
        List<String> tagNameList = Arrays.asList("Java", "C++");
        List<User> userList = userService.searchUsersByTagNames(tagNameList);
        Assertions.assertNotNull(userList);
    }

    @Test
    void testList() {
        List<String> tagList1 = Arrays.asList("Java", "大一", "跑步");
        List<String> tagList2 = Arrays.asList("跑步", "大二", "Java");
        List<String> tempTagList1 = tagList1.stream().filter(tag -> !tagList2.contains(tag)).collect(Collectors.toList());
        List<String> tempTagList2 = tagList2.stream().filter(tag -> !tagList1.contains(tag)).collect(Collectors.toList());
        System.out.println(tempTagList1);
        System.out.println(tempTagList2);
    }
}