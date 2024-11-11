<template>
  <template v-if="user">
    <div style="text-align: center; padding: 20px;">
      <van-image
          style="box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);"
          width="125px"
          height="125px"
          :src="user.avatarUrl != null ? `${avatarBaseURL}${user.avatarUrl}` : defaultUserAvatar"
          radius="20%"
          fit="cover"
      />
    </div>
    <van-cell title="昵称" :value="user.username" />
    <van-cell title="账号" :value="user.userAccount"/>
    <van-cell title="性别" :value="user.gender != null ? (user.gender === 0 ? '女' : '男') : '未知'"/>
    <van-cell title="联系方式" :value="user.contactInfo" />
    <van-cell title="邮箱" :value="user.email" />
    <van-cell title="个人简介" :value="user.userProfile" />
    <van-cell title="标签">
      <div v-if="user.tagNames?.length >= 1">
        <van-tag plain type="primary" v-for="tagName in user.tagNames" style="margin-right: 12px; margin-top: 5px">{{tagName}}</van-tag>
      </div>
    </van-cell>
    <div style="padding: 10px;" v-if="status === 0">
      <van-button type="primary" block @click="sendFriendship">添 加 好 友</van-button>
    </div>
    <div style="padding: 10px;" v-else-if="status === 1">
      <van-button type="primary" block disabled>您已发送好友申请</van-button>
    </div>
    <div style="padding: 10px;" v-else-if="status === 2">
      <van-button type="primary" block disabled>TA 向您发送了好友申请</van-button>
    </div>
    <div style="padding: 10px;" v-else-if="status === 3">
      <van-button type="primary" block @click="joinPrivateChat(user.id, user.username)">与 TA 聊天</van-button>
    </div>
    <van-dialog v-model:show="show" title="备注信息" show-cancel-button @confirm="sendFriendshipPost" @cancel="show = false">
      <van-field
          v-model="friendshipMessage"
          type="textarea"
          placeholder="填写验证信息"
      />
    </van-dialog>
  </template>
</template>

<script setup lang="ts">
import {onMounted, ref} from "vue";
import {useRoute, useRouter} from "vue-router";
import {getCurrentUser} from "../services/user.ts";
// @ts-ignore
import myAxios from "../plugins/myAxios";
import {showFailToast, showSuccessToast} from "vant";
import defaultUserAvatar from "../assets/avatar/defaultUserAvatar.png"
import {avatarBaseURL} from "../constants/avatar.ts";

const user: any = ref({});
const currentUser: any = ref({});
const route = useRoute();
const show = ref(false);
const friendshipMessage = ref('');
const router = useRouter();

let status = ref(0);

onMounted(async () => {
  currentUser.value = await getCurrentUser();
  friendshipMessage.value = '我是' + currentUser.value.username;
  const userId = route.query.userId;
  let res = await myAxios.get('/user/get/id', {
    params: {
      userId: userId
    }
  });
  if (res['code'] === 0) {
    user.value = res.data;
    user.value.tagNames = JSON.parse(user.value.tagNames);
  } else {
    showFailToast('查看用户详情失败');
  }

  // 判断是否已经是好友
  const friendIds = currentUser.value.friendIds;
  const friendIdList = JSON.parse(friendIds);
  if (friendIdList && friendIdList.length > 0) {
    friendIdList.forEach((friendId: any) => {
      if (friendId === user.value.id) {
        status.value = 3;
      }
    })
  }


  // 判断自己是否已经发送了好友申请
  if (status.value === 0) {
    res = await myAxios.get('/friendship/get/from');
    if (res['code'] === 0) {
      const friendships = res.data;
      if (friendships && friendships.length > 0) {
        friendships.forEach((friendship: any) => {
          if (friendship.toId === user.value.id && friendship.friendshipStatus === 0) {
            status.value = 1;
          }
        })
      }
    }
  }

  // 判断对方是否已经发送好友申请
  if (status.value === 0) {
    res = await myAxios.get('/friendship/get/to');
    if (res['code'] === 0) {
      const friendships = res.data;
      if (friendships && friendships.length > 0) {
        friendships.forEach((friendship: any) => {
          if (friendship.fromId === user.value.id && friendship.friendshipStatus === 0) {
            status.value = 2;
          }
        })
      }
    }
  }
});

const sendFriendship = () => {
  show.value = true;
}

const sendFriendshipPost = async () => {
  const res = await myAxios.post('/friendship/send', {
    toId: user.value.id,
    friendshipMessage: friendshipMessage.value
  });
  if (res['code'] === 0) {
    showSuccessToast('等待对方接受');
    status.value = 1;
  } else {
    showFailToast('好友申请失败');
  }
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
</script>

<style scoped>

</style>