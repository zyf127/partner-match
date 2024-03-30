<template>
  <van-cell title="昵称" is-link :value="user.username" @click="toEdit('username', '昵称', user.username)"/>
  <van-cell title="账号" :value="user.userAccount"/>
  <van-cell title="头像" is-link>
    <img :src="user.avatarUrl" style="width: 50px; height: 50px"/>
  </van-cell>
  <van-cell title="性别" is-link :value="user.gender" @click="toEdit('gender', '性别', user.gender)"/>
  <van-cell title="手机号" is-link :value="user.phone" @click="toEdit('phone', '手机号', user.phone)"/>
  <van-cell title="邮箱" is-link :value="user.email" @click="toEdit('email', '邮箱', user.email)"/>
  <van-cell title="注册时间" :value="user.createTime.toISOString()"/>
</template>

<script setup lang="ts">
  import {useRouter} from "vue-router";

  const user = {
    id: 1,
    username: '流年',
    userAccount: '255520',
    avatarUrl: 'https://images.zsxq.com/Fr5fi8-x1V7G7cXCQO3-eMonxIsf?e=1714492799&token=kIxbL07-8jAj8w1n4s9zv64FuZZNEATmlU_Vm6zD:rNB-iPf8HwoEX6txskdKq8IEwIM=',
    gender: '男',
    phone: '1986325045',
    email: 'zyf1982@qq.com',
    createTime: new Date()
  };

  const router = useRouter();

  const toEdit = (editKey: string, editName: string, currentValue: string) => {
    router.push({
      path: 'user/edit',
      query: {
        editKey,
        editName,
        currentValue
      }
    });
  };
</script>

<style scoped>

</style>