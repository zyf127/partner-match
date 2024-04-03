<template>
  <template v-if="user">
    <van-cell title="昵称" is-link :value="user.username" @click="toEdit('username', '昵称', user.username)"/>
    <van-cell title="账号" :value="user.userAccount"/>
    <van-cell title="头像" is-link>
      <img :src="user.avatarUrl" style="width: 50px; height: 50px"/>
    </van-cell>
    <van-cell title="性别" is-link :value="user.gender === 0 ? '女' : '男'" @click="toEdit('gender', '性别', user.gender)"/>
    <van-cell title="手机号" is-link :value="user.phone" @click="toEdit('phone', '手机号', user.phone)"/>
    <van-cell title="邮箱" is-link :value="user.email" @click="toEdit('email', '邮箱', user.email)"/>
    <van-cell title="注册时间" :value="user.createTime"/>
  </template>
</template>

<script setup lang="ts">
  import {onMounted, ref} from "vue";
  import {useRouter} from "vue-router";
  import {getCurrentUser} from "../services/user.ts";

  const router = useRouter();
  const user = ref();

  onMounted(async () => {
    const currentUser = await getCurrentUser();
    user.value = currentUser;
  });

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