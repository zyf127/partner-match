<template>
  <van-nav-bar
      title="标题"
      right-text="按钮"
      left-arrow
      @click-left="onClickLeft"
      @click-right="onClickRight">
    <template #right>
      <van-icon name="search" size="18" />
    </template>
  </van-nav-bar>
  <router-view />
  <van-tabbar @change="onChange">
    <van-tabbar-item replace to="/" icon="home-o" name="index">首页</van-tabbar-item>
    <van-tabbar-item replace to="/team" icon="search" name="team">组队</van-tabbar-item>
    <van-tabbar-item replace to="/user" icon="friends-o" name="user">个人</van-tabbar-item>
  </van-tabbar>
</template>

<script setup lang="ts">
  import {showToast} from "vant";
  import {useRouter} from "vue-router";

  const router = useRouter()
  const onClickLeft = () => router.back();
  const onClickRight = () => router.push('/search');
  const onChange = (index) => showToast(`标签 ${index}`);
</script>

<style scoped>

</style>