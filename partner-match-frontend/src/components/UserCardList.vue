<template>
  <van-card v-for="user in userList"
            :desc="user.profile"
            :title="user.username"
            :thumb="user.avatarUrl"
            :tag="user.gender == 0 ? '女' : '男'">
    <template #tags>
      <van-tag plain type="primary" v-for="tagName in user.tagNames" style="margin-right: 12px; margin-top: 5px">{{tagName}}</van-tag>
    </template>
    <template #footer>
      <van-button size="small">联系我</van-button>
    </template>
  </van-card>
</template>

<script setup lang="ts">
  import {UserType} from "../models/user";

  interface  UserCardListProps {
    userList: UserType[]
  }

  const props = withDefaults(defineProps<UserCardListProps>(), {
    userList: []
  })
</script>

<style scoped>

</style>