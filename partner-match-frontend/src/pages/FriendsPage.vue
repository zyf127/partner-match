<template>
  <van-tabs v-model:active="activeName" @change="doTabChange" @rendered="queryData">
    <van-tab title="我的好友">
      <template #title>
        <van-icon name="friends-o" size="16px"/>
        我的好友
      </template>
    </van-tab>
    <van-tab title="收到的好友申请">
      <template #title>
        <van-icon name="envelop-o" size="16px"/>
        收到的好友申请
      </template>
    </van-tab>
  </van-tabs>
  <van-card @click="showUserDetail(user.id)"
            v-for="user in userList"
            :desc="user.userProfile != null ? ('简介：' + user.userProfile) : '简介：这个人很懒，什么都没有留下~'"
            :title="user.username"
            :thumb="user.avatarUrl != null ? `${avatarBaseURL}${user.avatarUrl}` : defaultUserAvatar"
            :tag="user.gender != null ? (user.gender === 0 ? '女' : '男') : '未知'">
    <template #tags>
      <div style="margin-bottom: 12px"></div>
      <div>标签：</div>
      <div v-if="user.tagNames?.length >= 1">
        <van-tag plain type="primary" v-for="tagName in user.tagNames" style="margin-right: 12px; margin-top: 5px">{{tagName}}</van-tag>
      </div>
      <div v-else>这个人很懒，没有设置标签~</div>
    </template>
    <template #footer v-if="activeName === 0">
      <van-button size="small" type="primary" plain @click.stop="joinPrivateChat(user.id, user.username)">与 TA 聊天</van-button>
      <van-button size="small" type="danger" plain @click.stop="removeFriend(user.id)">删除好友</van-button>
    </template>
    <template #footer v-if="activeName === 1">
      <van-button size="small" type="success" plain @click.stop="agree(user.friendshipId)">同 意</van-button>
      <van-button size="small" type="danger" plain @click.stop="refuse(user.friendshipId)">拒 绝</van-button>
    </template>
  </van-card>
  <van-empty v-if="!userList || userList.length < 1" description="数据为空" />
</template>

<script setup lang="ts">
import {useRouter} from "vue-router";
import {ref} from "vue";
import {showConfirmDialog, showFailToast, showSuccessToast} from "vant";
// @ts-ignore
import myAxios from "../plugins/myAxios";
import {getCurrentUser} from "../services/user.ts";
import {avatarBaseURL} from "../constants/avatar.ts";
import defaultUserAvatar from "../assets/avatar/defaultUserAvatar.jpg";
// @ts-ignore
import qs from "qs";

const router = useRouter();

const activeName: any = ref(Number(sessionStorage.getItem('activeName')));
const userList: any = ref([]);


const showOne = async () => {
  userList.value = [];
  const res = await myAxios.get('/user/get/friends');
  if (res['code'] === 0) {
    userList.value = res.data;
  }
  userList.value.forEach((user: any) => {
    if (user.tagNames) {
      user.tagNames = JSON.parse(user.tagNames);
    }
  });
}

const showTwo = async () => {
  userList.value = [];
  let res = await myAxios.get('/friendship/get/to');
  if (res['code'] === 0) {
    const friendships: any = res.data;
    const friendshipIdList: any = [];
    const userIdList: any = [];
    if (friendships) {
      friendships.forEach((friendship: any) => {
        if (friendship.friendshipStatus === 0) {
          userIdList.push(friendship.fromId);
          friendshipIdList.push(friendship.id);
        }
      });
      if (userIdList.length > 0) {
        res = await myAxios.get('/user/get/ids', {
          params: {
            userIdList: userIdList,
            friendshipIdList: friendshipIdList,
          },
          paramsSerializer: (params: any) => {
            return qs.stringify(params, {indices: false})
          }
        });
        if (res['code'] === 0) {
          userList.value = res.data;
        } else {
          showFailToast('加载失败');
        }
      }
    }
  } else {
    showFailToast('加载失败');
  }
  userList.value.forEach((user: any) => {
    if (user.tagNames) {
      user.tagNames = JSON.parse(user.tagNames);
    }
  });
}

const doTabChange = async () => {
  if (activeName.value === 0) {
    await showOne();
  } else if (activeName.value === 1) {
    await showTwo();
  }
}


const queryData = async () => {
  loadData()
      .then(() => {
        doTabChange();
      });
}

const loadData = async () => {
  await getCurrentUser()
}

const showUserDetail = (userId: any) => {
  sessionStorage.setItem('activeName', activeName.value);
  router.push({
    path: '/user/detail',
    query: {
      userId
    }
  });
}

const joinPrivateChat = (userId: number, username: string) => {
  router.push({
    path: '/chat/private',
    query: {
      userId,
      username,
    }
  });
}

const removeFriend = (friendId: number) => {
  showConfirmDialog({
    message: '您确定删除吗?',
  }).then(async () => {
    sessionStorage.setItem('activeName', activeName.value);
    const res = await myAxios.post('/user/remove/friend', {
      friendId: friendId
    });
    if (res['code'] === 0) {
      showSuccessToast('已删除');
      await queryData();
    } else {
      showFailToast('操作失败');
    }
  }).catch(() => {
    showSuccessToast("取消成功");
  });
}

const agree = (id: number) => {
  showConfirmDialog({
    message: '您确定同意吗?',
  }).then(async () => {
    sessionStorage.setItem('activeName', activeName.value);
    const res = await myAxios.post('/friendship/update', {
      id: id,
      friendshipStatus: 1
    });
    if (res['code'] === 0) {
      showSuccessToast('已同意');
      await queryData();
    } else {
      showFailToast('操作失败');
    }
  }).catch(() => {
    showSuccessToast("取消成功");
  });
}

const refuse = (id: number) => {
  showConfirmDialog({
    message: '您确定拒绝吗?',
  }).then(async () => {
    sessionStorage.setItem('activeName', activeName.value);
    const res = await myAxios.post('/friendship/update', {
      id: id,
      friendshipStatus: 2
    });
    if (res['code'] === 0) {
      showSuccessToast('已拒绝');
      await queryData();
    } else {
      showFailToast('操作失败');
    }
  }).catch(() => {
    showSuccessToast("取消成功");
  });
}
</script>

<style scoped>

</style>