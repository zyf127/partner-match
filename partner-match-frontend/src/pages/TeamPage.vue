<template>
  <div id="teamPage">
    <van-button type="primary" @click="doAddTeam">创建队伍</van-button>
    <TeamCardList/>
  </div>
</template>

<script setup lang="ts">
  import {useRouter} from "vue-router";
  import {getCurrentUser} from "../services/user.ts";

  const router = useRouter();

  const doAddTeam = async () => {
    const currentUser = await getCurrentUser();
    if (currentUser) {
      router.push('/team/add');
    }
  }

</script>

<style scoped>

</style>