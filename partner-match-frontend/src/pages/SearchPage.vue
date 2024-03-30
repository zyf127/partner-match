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
</template>

<script setup lang="ts">
  import {ref} from 'vue';

  const activeIds = ref([]);
  const activeIndex = ref(0);
  const originTagNameList = [
    {
      text: '性别',
      children: [
        { text: '男', id: '男' },
        { text: '女', id: '女' },
        { text: '大', id: '大'},
      ],
    },
    {
      text: '年级',
      children: [
        { text: '大一', id: '大一' },
        { text: '大二', id: '大二' },
        { text: '大三', id: '大三' },
        { text: '大四', id: '大四' },
        { text: '大五', id: '大五' },
        { text: '大六', id: '大六' },
        { text: '大七', id: '大七' },
        { text: '大八', id: '大八' },
        { text: '大九', id: '大九' },
        { text: '大十', id: '大十' },
        { text: '大十一', id: '大十一' },
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

</script>

<style scoped>

</style>