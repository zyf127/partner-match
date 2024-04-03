<template>
  <UserCardList :user-list="userList"/>
  <van-empty v-if="!userList || userList.length < 1" description="搜索结果为空" />
</template>

<script setup>
  import {useRoute} from "vue-router";
  import myAxios from "../plugins/myAxios.js"
  import {ref, onMounted} from "vue";
  import qs from 'qs';
  import {showFailToast, showSuccessToast} from "vant";
  import UserCardList from "../components/UserCardList.vue";

  const route = useRoute();
  const selectedTagNames = route.query.selectedTagNames;
  let userList = ref([]);

  onMounted(async () => {
    const userListData = await myAxios.get('/user/search/tagNames', {
      params: {
        tagNameList: selectedTagNames
      },
      paramsSerializer: params => {
        return qs.stringify(params, {indices: false})
      }
    })
        .then(function (response) {
          // 处理成功情况
          console.log('/user/search/tagNames success', response);
          showSuccessToast('请求成功');
          return response?.data;
        })
        .catch(function (error) {
          // 处理错误情况
          console.log('/user/search/tagNames error', error);
          showFailToast('请求失败');
        });
    if (userListData) {
      userListData.forEach(user => {
        if (user.tagNames) {
          console.log(user.tagNames)
          user.tagNames = JSON.parse(user.tagNames);
          console.log(user.tagNames);
        }
      });
      userList.value = userListData;
    }
  });
</script>

<style scoped>

</style>