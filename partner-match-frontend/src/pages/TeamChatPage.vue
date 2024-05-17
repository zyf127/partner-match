<template>
  <div class="page-title">
    {{teamName}}
  </div>
  <Chat :userId="-1" :teamId="teamId"/>
</template>

<script setup lang="ts">
  import {useRoute} from "vue-router";

  const route = useRoute();
  const teamId = route.query.teamId;
  const teamName = route.query.teamName;
</script>

<style scoped>
.page-title {
  text-align: center;
  font-size: 18px;
  color: #333;
  margin-bottom: 10px;
}
</style>