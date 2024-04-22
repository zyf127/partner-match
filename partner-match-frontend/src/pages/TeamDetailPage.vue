<template>
  <template v-if="team">
    <div class="center">
      <img :alt="team.teamName" class="img" :src="defaultAvatar">
    </div>
    <van-divider>{{ team.teamName }}</van-divider>
    <van-cell title="队伍名" :value="team.teamName"/>
    <van-cell title="队伍描述" :value="team.teamDescription"/>
    <van-cell title="队伍人数" :value="team.userList.length + '/' + team.maxNum"/>
    <van-cell title="队长" :value="team.userList[0].username"/>
    <van-divider :style="{ color: '#1989fa', borderColor: '#1989fa', padding: '0 16px' }">队员</van-divider>
    <UserCardList :user-list="team.userList"/>
  </template>
</template>

<script setup lang="ts">
import {reactive} from "vue";
import {useRoute} from "vue-router";
import defaultAvatar from "../assets/defaultAvatar.png";
import UserCardList from "../components/UserCardList.vue";
import {TeamType} from "../models/team";
import {onMounted} from "vue";

const route = useRoute();
const team: TeamType = reactive(JSON.parse(decodeURIComponent(route.query.teamString)));

onMounted(() => {
  team.userList.forEach(user => {
    if (user.tagNames) {
      user.tagNames = JSON.parse(user.tagNames);
    }
  });
})

</script>

<style scoped>
.img {
  box-shadow: 0 0 14px rgba(0, 0, 0, 0.5);
  border-radius: 10%;
  height: 135px;
  width: 135px;
}

.center {
  margin-top: 10px;
  display: flex;
  justify-content: center;
}

</style>