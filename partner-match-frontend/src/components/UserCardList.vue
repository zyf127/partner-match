<template>
  <van-card v-for="user in userList"
            :desc="user.userProfile != null ? ('简介：' + user.userProfile) : '简介：这个人很懒，什么都没有留下~'"
            :title="user.username"
            :thumb="user.avatarUrl != null ? `${avatarBaseURL}${user.avatarUrl}` : defaultUserAvatar"
            :tag="user.gender != null ? (user.gender === 0 ? '女' : '男') : '未知'">
    <template #tags>
      <div style="margin-bottom: 12px"></div>
      <div>标签：</div>
      <div v-if="user.tagNames?.length >= 1">
        <van-tag plain type="primary" v-for="tagName in user.tagNames" style="margin-right: 12px; margin-top: 5px">{{tagName}}</van-tag>
      </div>
      <div v-else>这个人很懒，没有设置标签~</div>
    </template>
    <template #footer>
      <van-button size="small">联系我</van-button>
    </template>

  </van-card>
</template>

<script setup lang="ts">
  //@ts-nocheck
  import {UserType} from "../models/user";
  import defaultUserAvatar from "../assets/avatar/defaultUserAvatar.jpg"
  import {avatarBaseURL} from "../constants/avatar.ts";

  interface  UserCardListProps {
    userList: UserType[]
  }

  const props = withDefaults(defineProps<UserCardListProps>(), {
    userList: [{}, {}, {}]
  })
</script>

<style scoped>
</style>