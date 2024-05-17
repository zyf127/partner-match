<template>
  <div class="page-title">
    {{username}}
  </div>
  <Chat :userId="userId" :teamId="-1"/>
</template>

<script setup lang="ts">
import {useRoute} from "vue-router";

const route = useRoute();
const userId = route.query.userId;
const username = route.query.username;
</script>

<style scoped>
.page-title {
  text-align: center;
  font-size: 18px;
  color: #333;
  margin-bottom: 10px;
}
</style>