<template>
  <van-nav-bar
      :title="title"
      left-arrow
      left-text="返回"
      @click-left="onClickLeft"
      @click-right="onClickRight">
    <template #right>
      <van-icon name="search" size="18" /><span style="color: #2f9dfb">标签</span>
    </template>
  </van-nav-bar>
  <div id="content">
    <router-view />
  </div>
  <van-tabbar>
    <van-tabbar-item replace to="/" icon="home-o" name="index">首页</van-tabbar-item>
    <van-tabbar-item replace to="/team" icon="search" name="team">组队</van-tabbar-item>
    <van-tabbar-item replace to="/user" icon="friends-o" name="user">个人</van-tabbar-item>
  </van-tabbar>
</template>

<script setup>
  import {ref} from "vue";
  import {useRouter} from "vue-router";
  import {routes} from "../router/index.ts";

  const router = useRouter()

  const DEFAULT_TITLE = '伙伴匹配';
  const title = ref(DEFAULT_TITLE)

  router.beforeEach((to) => {
    let toRoute = {};
    routes.forEach((route) => {
      if (to.path === route.path) {
        toRoute = route;
      }
    });
    if (toRoute && toRoute['title']) {
      title.value = toRoute['title'];
    } else {
      title.value = DEFAULT_TITLE;
    }
  })

  const onClickLeft = () => router.back();
  const onClickRight = () => router.push('/search');
</script>

<style scoped>
  #content {
    padding-bottom: 55px;
  }
</style>