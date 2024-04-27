<template>
  <div v-if="team.id">
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
  </div>
</template>

<script setup lang="ts">
//@ts-nocheck
import {useRoute} from "vue-router";
import {TeamType} from "../models/team";
import {onMounted, ref} from "vue";
import myAxios from "../plugins/myAxios";
import {showFailToast} from "vant";
import defaultAvatar from "../assets/defaultAvatar.png"

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