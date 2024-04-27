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
  <div id="tage">
    <van-empty v-if="activeTagNameList.length === 0" :image-size="[35, 20]" description="未选择标签"/>
    <div v-else class="span-tag" style="margin-left: 20px; margin-right: 10px;">
      <van-row>
        <van-col v-for="activeTagName in activeTagNameList">
          <van-tag type="primary" class="van-tag" closeable size="large" :show="true" @close="doClose(activeTagName)">
            {{ activeTagName }}
          </van-tag>
        </van-col>
      </van-row>
    </div>
  </div>
  <van-divider />
  <van-tree-select
      v-model:active-id="activeTagNameList"
      v-model:main-active-index="activeIndex"
      :items="tagItems"
  />
  <div style="padding: 12px">
    <van-button v-if="myTagNames" block type="primary" @click="doSearchResult">修改</van-button>
    <van-button v-else block type="primary" @click="doSearchResult">搜索</van-button>
  </div>
</template>

<script setup>
  import {ref, onMounted} from 'vue';
  import {useRouter, useRoute} from "vue-router";
  import myAxios from "../plugins/myAxios";
  import {showFailToast, showSuccessToast} from "vant";

  const router = useRouter();
  const route = useRoute();
  let myTagNames = route.query.myTagNameList;
  let tagList = [];
  const activeTagNameList = ref([]);
  const activeIndex = ref(0);
  let originTagItems = [];
  let tagItems = ref([]);
  const searchText = ref('');

  onMounted(async () => {
    const res = await myAxios.get('/tag/get');
    if (res['code'] === 0) {
      tagList = res.data;
      const parentTagList = tagList.filter(tag => tag['isParent'] === 1);
      parentTagList.forEach(parentTag => {
        const children = tagList.filter(childTag => childTag.parentId === parentTag.id);
        const childrenItems = children.map(child => ({ text: child.tagName, id: child.tagName }));
        originTagItems.push({ text: parentTag.tagName, children: childrenItems });
      });
      tagItems.value = [...originTagItems];
    } else {
      showFailToast('加载标签失败');
    }
    if (myTagNames && myTagNames.length > 0) {
      activeTagNameList.value = JSON.parse(myTagNames);
    }
  });

  const onSearch = () => {
    const searchedIndexList = [];
    tagItems.value = originTagItems.map((parentTag, index) => {
      const tempChildren = [...parentTag.children];
      const tempParentTag = {...parentTag};
      tempParentTag.children = tempChildren.filter(item => {
        if (item.text.includes(searchText.value)) {
          searchedIndexList.push(index);
          return true;
        }
      });
      return tempParentTag;
    });
    activeIndex.value = searchedIndexList[0];
  };
  const onCancel = () => {
    searchText.value = '';
    tagItems.value = [...originTagItems]
    activeIndex.value = 0;
  }
  const doClose = (activeTagName) => {
    activeTagNameList.value = activeTagNameList.value.filter(item => item !== activeTagName);
  };

  const doSearchResult = async () => {
    const selectedTagNameList = activeTagNameList.value;
    if (myTagNames) {
      myTagNames = activeTagNameList.value;
      const res = await myAxios.post('/user/update/tagNames', {
        tagNames: JSON.stringify(myTagNames)
      });
      if (res['code'] === 0) {
        showSuccessToast('修改成功');
        router.push({
          path: '/user',
          replace: true
        });
      } else {
        showFailToast('修改失败');
      }
    } else {
      if (selectedTagNameList.length > 0) {
        router.push({
          path: '/user/list',
          query: {
            selectedTagNames: selectedTagNameList
          }
        });
      } else {
        showFailToast('未选择标签');
      }
    }
  }

</script>

<style scoped>
#tage {
  width: 100%;
  height: 100px;
}

.van-tag {
  margin: 1px
}

.span-tag {
  padding: 1px;
}
</style>