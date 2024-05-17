<template>
  <van-card @click="showUserDetail(user.id)"
            v-for="user in userList"
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
    <van-dialog v-model:show="show" title="队伍密码" show-cancel-button @confirm="joinTeamPost" @cancel="show = false">
      <van-field
          v-model="teamJoinRequest.teamPassword"
          type="password"
          placeholder="请输入队伍密码"
          :rules="[{ required: true, message: '请填写密码'}, {validator: value => value.length <= 32, message: '密码不超过 32 个字符'}]"
      />
    </van-dialog>
  </van-card>
</template>

<script setup lang="ts">
  //@ts-nocheck
  import {UserType} from "../models/user";
  import defaultUserAvatar from "../assets/avatar/defaultUserAvatar.jpg"
  import {avatarBaseURL} from "../constants/avatar.ts";
  import {useRouter} from "vue-router";

  interface  UserCardListProps {
    userList: UserType[]
  }

  const props = withDefaults(defineProps<UserCardListProps>(), {
    userList: [{}, {}, {}]
  });

  const router = useRouter();

  const showUserDetail = (userId) => {
    router.push({
      path: '/user/detail',
      query: {
        userId
      }
    });
  }

</script>

<style scoped>
</style>