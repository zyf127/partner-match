<template>
  <van-card v-for="user in userList"
      :desc="user.profile"
      :title="user.username"
      :thumb="user.avatarUrl"
      :tag="user.gender">
    <template #tags>
      <van-tag plain type="primary" v-for="tagName in user.tagNames" style="margin-right: 12px; margin-top: 5px">{{tagName}}</van-tag>
    </template>
    <template #footer>
      <van-button size="small">联系我</van-button>
    </template>
  </van-card>
</template>

<script setup lang="ts">
  import {useRoute} from "vue-router";

  const route = useRoute();
  const selectedTagNames = route.query.selectedTagNames;
  const userList = [
    {
      id: 1,
      username: '流年',
      userAccount: '255520',
      avatarUrl: 'https://images.zsxq.com/Fr5fi8-x1V7G7cXCQO3-eMonxIsf?e=1714492799&token=kIxbL07-8jAj8w1n4s9zv64FuZZNEATmlU_Vm6zD:rNB-iPf8HwoEX6txskdKq8IEwIM=',
      gender: '男',
      phone: '1986325045',
      email: 'zyf1982@qq.com',
      profile: '我是一名精神小伙，立正了！阿巴阿巴阿巴阿巴阿巴阿巴阿巴阿巴阿巴',
      tagNames: ['Java', 'emo'],
      createTime: new Date()
    }
  ];
</script>

<style scoped>

</style>