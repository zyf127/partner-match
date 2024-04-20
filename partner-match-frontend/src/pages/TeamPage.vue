<template>
  <div id="teamPage">
    <van-button type="primary" @click="doAddTeam">创建队伍</van-button>
  </div>
</template>

<script setup lang="ts">
  import {useRouter} from "vue-router";

  const router = useRouter();
  const doAddTeam = () => {
    router.push('/team/add');
  }
</script>

<style scoped>

</style>