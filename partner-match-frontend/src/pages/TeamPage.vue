<template>
  <div id="teamPage" style="padding-bottom: 50px;">
    <TeamCardList/>
    <van-button type="primary" @click="doAddTeam" icon="plus" class="add-button" />
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