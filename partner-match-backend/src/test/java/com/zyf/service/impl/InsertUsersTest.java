package com.zyf.service.impl;

import com.zyf.model.domain.User;
import com.zyf.mapper.UserMapper;
import com.zyf.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
public class InsertUsersTest {
    @Resource
    private UserMapper userMapper;

    /**
     * for循环插入用户数据
     */
    @Test
    public void doInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 10000条数据耗时41秒
        int insert_num = 10000;
        for (int i = 0; i < insert_num; i++) {
            User user = new User();
            user.setUsername("假流年");
            user.setUserAccount("zyf" + i);
            user.setAvatarUrl("https://images.zsxq.com/Fr5fi8-x1V7G7cXCQO3-eMonxIsf?e=1714492799&token=kIxbL07-8jAj8w1n4s9zv64FuZZNEATmlU_Vm6zD:rNB-iPf8HwoEX6txskdKq8IEwIM=");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("123");
            user.setEmail("123@qq.com");
            user.setTagNames("[\"Java\"]");
            user.setUserStatus(0);
            user.setUserRole(0);
            userMapper.insert(user);
        }
        stopWatch.stop();
        System.out.println("for循环插入耗时：" + stopWatch.getTotalTimeMillis() + "ms");
    }

    @Resource
    private UserService userService;

    /**
     * 批量插入用户数据
     */
    @Test
    public void doBatchInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 批量插入10000条数据耗时1.7秒
//        int insert_num = 10000;
        // 批量插入1000000条数据耗时100秒
        int insert_num = 1000000;
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < insert_num; i++) {
            User user = new User();
            user.setUsername("假流年");
            user.setUserAccount("zyf" + i);
            user.setAvatarUrl("https://images.zsxq.com/Fr5fi8-x1V7G7cXCQO3-eMonxIsf?e=1714492799&token=kIxbL07-8jAj8w1n4s9zv64FuZZNEATmlU_Vm6zD:rNB-iPf8HwoEX6txskdKq8IEwIM=");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("123");
            user.setEmail("123@qq.com");
            user.setTagNames("[\"Java\"]");
            user.setUserStatus(0);
            user.setUserRole(0);
            userList.add(user);
        }
        userService.saveBatch(userList, 1000);
        stopWatch.stop();
        System.out.println("批量插入耗时：" + stopWatch.getTotalTimeMillis() + "ms");
    }

    private ExecutorService executorService = new ThreadPoolExecutor(20, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));

    /**
     * 并发批量插入数据
     */
    @Test
    public void doConcurrencyBatchInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 并发批量插入1000000条数据耗时39秒
        int insert_num = 1000000;
        int j = 0;
        int batchSize = insert_num / 20;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            List<User> userList = new ArrayList<>();
            while (true) {
                j++;
                User user = new User();
                user.setUsername("假流年");
                user.setUserAccount("zyf" + j);
                user.setAvatarUrl("https://images.zsxq.com/Fr5fi8-x1V7G7cXCQO3-eMonxIsf?e=1714492799&token=kIxbL07-8jAj8w1n4s9zv64FuZZNEATmlU_Vm6zD:rNB-iPf8HwoEX6txskdKq8IEwIM=");
                user.setGender(0);
                user.setUserPassword("12345678");
                user.setPhone("123");
                user.setEmail("123@qq.com");
                user.setTagNames("[\"Java\"]");
                user.setUserStatus(0);
                user.setUserRole(0);
                userList.add(user);
                if (j % batchSize == 0) {
                    break;
                }
            }
            // 异步执行
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("threadName: " + Thread.currentThread().getName());
                userService.saveBatch(userList, batchSize);
            });
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println("并发批量插入耗时：" + stopWatch.getTotalTimeMillis() + "ms");
    }

}
