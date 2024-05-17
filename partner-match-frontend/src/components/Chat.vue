<template>
  <div class="chat-container" ref="scrollContainer">
    <div class="content" v-for="chatMessage in chatMessages">
      <div class="message self" v-if="chatMessage.fromUser.id === currentUser.id">
        <div class="myInfo info">
          <img :alt="chatMessage.fromUser.username" class="avatar"
               :src="chatMessage.fromUser.avatarUrl != null ? `${avatarBaseURL}${chatMessage.fromUser.avatarUrl}` : defaultUserAvatar">
        </div>
        <div class="myMessage">
          <span
              class="username">{{ formatDateTime(chatMessage.createTime.toString()) }}&nbsp;&nbsp;&nbsp;&nbsp;{{ chatMessage.fromUser.username.length < 10 ? chatMessage.fromUser.username : chatMessage.fromUser.username.slice(0, 10) }}</span>
          <div>
            <p class="text">{{ chatMessage.messageContent }}</p>
          </div>
        </div>
      </div>
      <div class="message other" v-else>
        <img :alt="chatMessage.fromUser.username" class="avatar"
             :src="chatMessage.fromUser.avatarUrl != null ? `${avatarBaseURL}${chatMessage.fromUser.avatarUrl}` : defaultUserAvatar">
        <div class="info">
          <span class="username">{{
              chatMessage.fromUser.username.length < 10 ? chatMessage.fromUser.username : chatMessage.fromUser.username.slice(0, 10)
            }}&nbsp;&nbsp;&nbsp;&nbsp;{{ formatDateTime(chatMessage.createTime.toString()) }}</span>
          <p class="text">{{ chatMessage.messageContent }}</p>
        </div>
      </div>
    </div>
  </div>
  <div class="message-input-container">
    <van-field
        v-model="inputMessage"
        placeholder="请输入消息..."
        type="textarea"
        class="message-input"
    />
    <van-button type="primary" @click="sendMessage" class="send-button">发送</van-button>
  </div>

</template>

<script setup lang="ts">

import {onMounted, ref, nextTick, defineProps} from 'vue';
import defaultUserAvatar from '../assets/avatar/defaultUserAvatar.jpg'
import {getCurrentUser} from "../services/user.ts";
import {ChatMessageType} from "../models/chat-message";
import {avatarBaseURL} from "../constants/avatar.ts";
import {formatDateTime} from "../services/datetime.ts";
// @ts-ignore
import myAxios from "../plugins/myAxios";
import {showFailToast} from "vant";

const {userId, teamId} = defineProps(['userId', 'teamId']);

// 创建 WebSocket 对象
const ws = new WebSocket(`ws://localhost:8080/api/chat/${userId}/${teamId}`);
const currentUser: any = ref({});
const inputMessage = ref('');
const chatMessages = ref<ChatMessageType[]>([]);
// 使用 ref 获取滚动容器的 DOM 元素
const scrollContainer: any = ref(null);

// 建立 WebSocket 连接时调用
ws.onopen = function(event) {
  console.log('WebSocket connection opened:', event);
};

// 发送消息到服务端
const sendMessage = () => {
  const messageContent = inputMessage.value.trim();
  if (messageContent) {
    const chatMessage = {messageContent: messageContent, messageType: 2};
    ws.send(JSON.stringify(chatMessage));
    inputMessage.value = '';
  }
};

// 接收到来自服务端的消息时调用
ws.onmessage = function(event) {
  console.log('Received message:', event.data);
  if (event.data) {
    const newMessage: ChatMessageType = JSON.parse(event.data);
    chatMessages.value.push(newMessage);
    scrollToBottom();
  }
};

// 断开 WebSocket 连接时调用
ws.onclose = function(event) {
  console.log('WebSocket connection closed:', event);
};

// 滚动容器到底部的方法
const scrollToBottom = () => {
  nextTick(() => {
    const child = document.querySelector(`.chat-container`) // 需要滚动的元素
    window.scrollTo({
      top: child?.scrollHeight ,
    })
  })
};

onMounted(async () => {
  currentUser.value = await getCurrentUser();
  if (userId === -1 && teamId === -1) {
    const res = await myAxios.get('/chatMessage/get/public');
    if (res['code'] === 0) {
      chatMessages.value = res.data;
      await nextTick(() => {
        scrollToBottom();
      });
    } else {
      showFailToast("加载聊天记录失败");
    }
  } else if (userId === -1 && teamId !== -1) {
    const res = await myAxios.get('/chatMessage/get/team', {
      params: {
        teamId: teamId
      }
    });
    if (res['code'] === 0) {
      chatMessages.value = res.data;
      await nextTick(() => {
        scrollToBottom();
      });
    } else {
      showFailToast("加载聊天记录失败");
    }
  } else if (userId !== -1 && teamId === -1) {
    const res = await myAxios.get('/chatMessage/get/private', {
      params: {
        friendId: userId
      }
    });
    if (res['code'] === 0) {
      chatMessages.value = res.data;
      await nextTick(() => {
        scrollToBottom();
      });
    } else {
      showFailToast("加载聊天记录失败");
    }
  }
});

</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  padding: 10px;
  overflow-y: auto;
  margin-bottom: 90px;
}

.content {
  padding-top: 5px;
  padding-bottom: 5px;
  display: flex;
  flex-direction: column;
}

.message {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.self {
  align-self: flex-end;
}

.info {
  display: flex;
  flex-direction: column;
  order: 2;
}

.myInfo {
  align-self: flex-start;
}

.avatar {
  align-self: flex-start;
  width: 35px;
  height: 35px;
  border-radius: 50%;
  margin-right: 10px;
  margin-left: 10px;
}

.text {
  margin: 0px 0px;
  padding: 10px;
  border-radius: 10px;
  background-color: #eee;
  word-wrap: break-word;
  word-break: break-all;
}

.myMessage {
  text-align: right;
}

.self .text {
  background-color: #0084ff;
  color: #fff;
  display: inline-block;
  text-align: left;
}

.other .text {
  align-self: flex-start;
}

.username {
  align-self: flex-start;
  text-align: center;
  max-width: 200px;
  font-size: 12px;
  color: #999;
  padding-bottom: 4px;
  white-space: nowrap;
  overflow: visible;
  background-color: #fff;
}

.message-input-container {
  display: flex;
  align-items: center;
  margin-top: 10px;
  padding: 10px;
  border-top: 1px solid #ccc;
  position: fixed;
  /* 开发 */
  bottom: 48px;
  /* 上线 */
  /*bottom: 76px;*/
  left: 0;
  width: 95%;
  background-color: #fffffe;
}

.message-input {
  flex: 1;
  margin-right: 10px;
  border: 1px solid #ccc;
  border-radius: 8px;
}

.send-button {
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 16px;
  border: 1px solid #007bff;
}

</style>