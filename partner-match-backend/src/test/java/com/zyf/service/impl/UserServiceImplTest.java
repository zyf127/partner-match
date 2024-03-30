package com.zyf.service.impl;

import com.zyf.domain.User;
import com.zyf.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
        String userAccount = "liunian";
        String userPassword = "12345678";
        String checkPassword = "";
        Assertions.assertEquals(-1, userService.userRegister(userAccount, userPassword, checkPassword));

        userAccount = "zyf";
        userPassword = "123345678";
        checkPassword = "12345678";
        Assertions.assertEquals(-1, userService.userRegister(userAccount, userPassword, checkPassword));

        userAccount = "liunian";
        userPassword = "1234567";
        checkPassword = "1234567";
        Assertions.assertEquals(-1, userService.userRegister(userAccount, userPassword, checkPassword));

        userAccount = "266520";
        userPassword = "12345678";
        checkPassword = "12345678";
        Assertions.assertEquals(-1, userService.userRegister(userAccount, userPassword, checkPassword));

        userAccount = "liu nian";
        userPassword = "12345678";
        checkPassword = "12345678";
        Assertions.assertEquals(-1, userService.userRegister(userAccount, userPassword, checkPassword));

        userAccount = "liunian";
        userPassword = "12345678";
        checkPassword = "123456789";
        Assertions.assertEquals(-1, userService.userRegister(userAccount, userPassword, checkPassword));

        userAccount = "zyf127";
        userPassword = "12345678";
        checkPassword = "12345678";
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertTrue(result > 0);
    }

    @Test
    void testSearchUsersByTagNames() {
        List<String> tagNameList = Arrays.asList("Java", "C++");
        List<User> userList = userService.searchUsersByTagNames(tagNameList);
        Assertions.assertNotNull(userList);
    }
}