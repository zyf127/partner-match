<template>
  <van-cell center :title="isMatchMode ? '匹配模式' : '普通模式'" :style="{'--van-cell-text-color': isMatchMode ? '#1989fa' : '#323233'}">
    <template #right-icon>
      <van-switch v-model="isMatchMode" @change="loadData" />
    </template>
  </van-cell>
  <van-search
      v-model="searchText"
      show-action
      placeholder="请输入用户关键词"
      @search="onSearch"
      @clear="onCancel"
  >
    <template #action>
      <div @click="onSearch">搜索</div>
    </template>
  </van-search>
  <UserCardList :user-list="userList" v-if="!isLoadData"/>
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
  <van-empty v-if="!isLoadData && (!userList || userList.length < 1)" description="数据为空" />
</template>

<script setup>
import myAxios from "../plugins/myAxios.js"
import {ref, onMounted, watch } from "vue";
import {showFailToast } from "vant";
import UserCardList from "../components/UserCardList.vue";

let userList = ref([]);

const isMatchMode = ref(false);

const isLoadData = ref(false);

const searchText = ref('');

onMounted(async () => {
  await loadData();
});

const onSearch = async () => {
  searchText.value = searchText.value.trim();
  if (searchText.value === '') {
    await loadData();
  } else {
    const res = await myAxios.get('/user/search', {
      params: {
        searchText: searchText.value
      }
    });
    if (res['code'] === 0) {
      userList.value = res.data;
      userList.value.forEach(user => {
        if (user.tagNames) {
          user.tagNames = JSON.parse(user.tagNames);
        }
      });
    } else {
      showFailToast('搜索用户失败');
    }
  }
};
const onCancel = () => {
  searchText.value = ''
  loadData();
};

const loadData = async () => {
  isLoadData.value = true;
  if (!isMatchMode.value) {
    const res = await myAxios.get(`/user/recommend`, {
      params: {
        pageSize: 30,
        pageNum: 1
      }
    });
    if (res['code'] === 0) {
      userList.value = res.data;
      userList.value.forEach(user => {
        if (user.tagNames) {
          user.tagNames = JSON.parse(user.tagNames);
        }
      });
    } else {
      showFailToast('推荐用户失败');
    }
  } else {
    const num = 30;
    const res = await myAxios.get(`/user/match`, {
      params: {
        num
      }
    });
    if (res['code'] === 0) {
      userList.value = res.data;
      userList.value.forEach(user => {
        if (user.tagNames) {
          user.tagNames = JSON.parse(user.tagNames);
        }
      });
    } else {
      showFailToast(res['description']);
    }
  }
  isLoadData.value = false;
}

watch(searchText,  () => {
  if (searchText.value === '') {
    loadData();
  }
})

</script>

<style scoped>

</style>