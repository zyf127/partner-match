<template>
  <UserCardList :user-list="userList" v-if="!isLoadingData"/>
  <van-skeleton v-else v-for="i in [1, 2, 3, 4]" style="margin-top: 5px; height: 120px;">
    <template #template>
      <div :style="{ display: 'flex', width: '100%' }">
        <van-skeleton-image />
        <div :style="{ flex: 1, marginLeft: '16px' }">
          <van-skeleton-paragraph row-width="60%" />
          <van-skeleton-paragraph />
          <van-skeleton-paragraph />
          <van-skeleton-paragraph />
        </div>
      </div>
    </template>
  </van-skeleton>
  <van-empty v-if="!isLoadingData && (!userList || userList.length < 1)" description="搜索结果为空" />
</template>

<script setup>
  import {useRoute} from "vue-router";
  import myAxios from "../plugins/myAxios.js"
  import {ref, onMounted} from "vue";
  import qs from "qs"
  import {showFailToast} from "vant";
  import UserCardList from "../components/UserCardList.vue";

  const route = useRoute();
  const selectedTagNames = route.query.selectedTagNames;
  let userList = ref([]);
  const isLoadingData = ref(false);

  onMounted(async () => {
    isLoadingData.value = true;
    const res = await myAxios.get('/user/search/tagNames', {
      params: {
        tagNameList: selectedTagNames
      },
      paramsSerializer: params => {
        return qs.stringify(params, {indices: false})
      }
    });
    if (res['code'] === 0) {
      userList = res.data;
      if (userList) {
        userList.forEach(user => {
          if (user.tagNames) {
            user.tagNames = JSON.parse(user.tagNames);
          }
        });
      }
    } else {
      showFailToast('搜索失败');
    }
    isLoadingData.value = false;
  });
</script>

<style scoped>

</style>