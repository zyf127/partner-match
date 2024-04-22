<template>
  <UserCardList :user-list="userList"/>
  <van-empty v-if="!userList || userList.length < 1" description="数据为空" />
</template>

<script setup>
import myAxios from "../plugins/myAxios.js"
import {ref, onMounted} from "vue";
import {showFailToast, showSuccessToast} from "vant";
import UserCardList from "../components/UserCardList.vue";

let userList = ref([]);

onMounted(async () => {
  const userListData = await myAxios.get('/user/recommend', {
    params: {
      pageSize: 8,
      pageNum: 1
    }
  })
      .then(function (response) {
        // 处理成功情况
        showSuccessToast('推荐用户成功');
        return response?.data;
      })
      .catch(function (error) {
        // 处理错误情况
        console.log('/user/search/tagNames error', error);
        showFailToast('推荐用户失败');
      });
  if (userListData) {
    userListData.forEach(user => {
      if (user.tagNames) {
        user.tagNames = JSON.parse(user.tagNames);
      }
    });
    userList.value = userListData;
  }
});
</script>

<style scoped>

</style>