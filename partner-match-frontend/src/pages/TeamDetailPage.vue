<template>
  <div v-if="team.id">
    <div style="text-align: center; padding: 20px;">
      <van-image
          style="box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);"
          width="125px"
          height="125px"
          :src="team.avatarUrl != null ? `http://${avatarBaseURL}${team.avatarUrl}` : defaultTeamAvatar"
          radius="20%"
          fit="cover"
      />
    </div>
    <van-divider>{{ team.teamName }}</van-divider>
    <van-cell title="队伍名" :value="team.teamName"/>
    <van-cell title="队伍描述" :value="team.teamDescription"/>
    <van-cell title="队伍人数" :value="team.userList.length + '/' + team.maxNum"/>
    <van-cell title="队长" :value="team.userList[0].username"/>
    <van-divider :style="{ color: '#1989fa', borderColor: '#1989fa', padding: '0 16px' }">队员</van-divider>
    <UserCardList :user-list="team.userList"/>
  </div>
</template>

<script setup lang="ts">
//@ts-nocheck
import {useRoute} from "vue-router";
import {TeamType} from "../models/team";
import {onMounted, ref} from "vue";
import myAxios from "../plugins/myAxios";
import {showFailToast} from "vant";
import defaultTeamAvatar from "../assets/defaultTeamAvatar.png"
import {avatarBaseURL} from "../constants/avatar.ts";

const route = useRoute();
const teamId = route.query.teamId;
let team = ref<TeamType>({})

onMounted(async () => {
  const res = await myAxios.get('/team/get', {
    params: {
      teamId
    }
  });
  if (res['code'] === 0) {
    team.value = res.data;
    team.value.userList.forEach(user => {
      if (user.tagNames) {
        user.tagNames = JSON.parse(user.tagNames);
      }
    });
  } else {
    showFailToast('查看失败');
  }
})

</script>

<style scoped>

</style>