<script setup lang="ts">
  import { ref } from 'vue';
  import {showToast} from "vant";
  import Index from "../pages/Index.vue";
  import Team from "../pages/Team.vue";
  import User from "../pages/User.vue";
  const onClickLeft = () => alert('返回');
  const onClickRight = () => alert('按钮');
  const active = ref('index');
  const onChange = (index) => showToast(`标签 ${index}`);
</script>

<template>
  <van-nav-bar
      title="标题"
      left-text="返回"
      right-text="按钮"
      left-arrow
      @click-left="onClickLeft"
      @click-right="onClickRight">
    <template #right>
      <van-icon name="search" size="18" />
    </template>
  </van-nav-bar>
  <template v-if="active === 'index'">
    <Index />
  </template>
  <template v-if="active === 'team'">
    <Team />
  </template>
  <template v-if="active === 'user'">
    <User />
  </template>
  <van-tabbar v-model="active" @change="onChange">
    <van-tabbar-item icon="home-o" name="index">首页</van-tabbar-item>
    <van-tabbar-item icon="search" name="team">组队</van-tabbar-item>
    <van-tabbar-item icon="friends-o" name="user">个人</van-tabbar-item>
  </van-tabbar>
</template>

<style scoped>

</style>