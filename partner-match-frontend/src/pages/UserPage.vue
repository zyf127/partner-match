<template>
  <van-cell title="昵称" is-link :value="user.username" @click="toEdit('username', '昵称', user.username)"/>
  <van-cell title="账号" :value="user.userAccount"/>
  <van-cell title="头像" is-link >
    <img :src="user.avatarUrl" />
  </van-cell>
  <van-cell title="性别" is-link :value="user.gender"/>
  <van-cell title="手机号" is-link :value="user.phone"/>
  <van-cell title="邮箱" is-link :value="user.email"/>
  <van-cell title="注册时间" :value="user.createTime.toISOString()"/>
</template>

<script setup lang="ts">
  import {useRouter} from "vue-router";

  const user = {
    id: 1,
    username: '流年',
    userAccount: '255520',
    avatarUrl: 'http://www.baid.com/dfa.png',
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