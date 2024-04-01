<template>
  <form action="/">
    <van-search
        v-model="searchText"
        show-action
        placeholder="请输入要搜索的标签"
        @search="onSearch"
        @cancel="onCancel"
    />
  </form>
  <van-divider content-position="left">已选标签</van-divider>
  <div v-if="activeIds.length === 0">请选择标签</div>
  <van-row :gutter="16">
    <van-col v-for="activeId in activeIds">
      <van-tag type="primary" closeable size="medium" :show="true" @close="doClose(activeId)">
        {{ activeId }}
      </van-tag>
    </van-col>
  </van-row>
  <van-divider content-position="left">选择标签</van-divider>
  <van-tree-select
      v-model:active-id="activeIds"
      v-model:main-active-index="activeIndex"
      :items="tagNameList"
  />
  <div style="padding: 12px">
    <van-button block type="primary" @click="doSearchResult">搜索</van-button>
  </div>
</template>

<script setup lang="ts">
  import {ref} from 'vue';
  import {useRouter} from "vue-router";

  const router = useRouter();

  const activeIds = ref([]);
  const activeIndex = ref(0);
  const originTagNameList = [
    {
      text: '编程语言',
      children: [
        { text: 'Java', id: 'Java' },
        { text: 'C++', id: 'C++' },
      ],
    },
    {
      text: '年级',
      children: [
        { text: '大一', id: '大一' },
        { text: '大二', id: '大二' },
        { text: '大三', id: '大三' },
        { text: '大四', id: '大四' },
      ],
    },
  ];
  let tagNameList = ref(originTagNameList);
  const searchText = ref('');
  const onSearch = () => {
    tagNameList.value = originTagNameList.map(parentTagName => {
      const tempChildren = [...parentTagName.children];
      const tempParentTagName = {...parentTagName};
      tempParentTagName.children = tempChildren.filter(item => item.text.includes(searchText.value));
      return tempParentTagName;
    })
  };
  const onCancel = () => {
    searchText.value = '';
    tagNameList.value = originTagNameList;
  }
  const doClose = (activeId) => {
    activeIds.value = activeIds.value.filter((item) => item !== activeId);
  };

  const doSearchResult = () => {
    const selectedTagNames = activeIds.value;
    router.push({
      path: '/user/list',
      query: {
        selectedTagNames
      }
    });
  }

</script>

<style scoped>

</style>