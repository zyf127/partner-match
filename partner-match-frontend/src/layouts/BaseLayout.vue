<template>
  <van-nav-bar
      :title="title"
      left-arrow
      left-text="返回"
      @click-left="onClickLeft"
      @click-right="onClickRight"
      :fixed="true">
    <template #right>
      <van-icon name="search" size="18" /><span style="color: #2f9dfb">标签</span>
    </template>
  </van-nav-bar>
  <div id="content">
    <router-view/>
  </div>
  <van-tabbar v-model="active" @change="change">
    <van-tabbar-item replace to="/" name="index">
      <span>首页</span>
      <template #icon="props">
        <img :src="props.active ? activeIndex : inactiveIndex"/>
      </template>
    </van-tabbar-item>
    <van-tabbar-item replace to="/team" name="team">
      <span>组队</span>
      <template #icon="props">
        <img :src="props.active ? activeTeam : inactiveTeam"/>
      </template>
    </van-tabbar-item>
    <van-tabbar-item replace to="/chat/public" name="chat">
      <span>聊天室</span>
      <template #icon="props">
        <img :src="props.active ? activeChat : inactiveChat"/>
      </template>
    </van-tabbar-item>
    <van-tabbar-item replace to="/friends" name="friends">
      <span>好友</span>
      <template #icon="props">
        <img :src="props.active ? activeFriends : inactiveFriends"/>
      </template>
    </van-tabbar-item>
    <van-tabbar-item replace to="/user" name="user">
      <span>个人</span>
      <template #icon="props">
        <img :src="props.active ? activeUser : inactiveUser"/>
      </template>
    </van-tabbar-item>
  </van-tabbar>
</template>

<script setup>
  import {ref, onMounted } from "vue";
  import {useRouter} from "vue-router";
  import {routes} from "../router/index.ts";
  import activeIndex from "../assets/icon/activeIndex.png"
  import inactiveIndex from "../assets/icon/inactiveIndex.png"
  import activeTeam from "../assets/icon/activeTeam.png"
  import inactiveTeam from "../assets/icon/inactiveTeam.png"
  import activeChat from "../assets/icon/activeChat.png"
  import inactiveChat from "../assets/icon/inactiveChat.png"
  import activeUser from "../assets/icon/activeUser.png"
  import inactiveUser from "../assets/icon/inactiveUser.png"
  import activeFriends from "../assets/icon/activeFriends.png"
  import inactiveFriends from "../assets/icon/inactiveFriends.png"


  const router = useRouter()

  const DEFAULT_TITLE = '伙伴匹配';
  const title = ref(DEFAULT_TITLE);

  const active = ref('index');

  onMounted(() => {
    const newActive = sessionStorage.getItem('active')
    if (newActive) {
      active.value = newActive;
    }
  });

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

  const change = () => {
    sessionStorage.setItem('active', active.value);
  }
</script>

<style scoped>
  #content {
    padding-top: 45px;
    /* 开发 */
    padding-bottom: 55px;
    /* 上线 */
    /*padding-bottom: 83px;*/
  }
</style>