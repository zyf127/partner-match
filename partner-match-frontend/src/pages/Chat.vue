<template>

</template>

<script setup lang="ts">
// 创建WebSocket对象
const socket = new WebSocket('ws://example.com/socket');

// 建立连接
socket.onopen = function(event) {
  console.log('WebSocket connection opened:', event);
};

// 发送消息
socket.send('Hello, Server!');

// 接收
socket.onmessage = function(event) {
  console.log('Received message:', event.data);
};


// 关闭
socket.onclose = function(event) {
  console.log('WebSocket connection closed:', event);
};


</script>

<style scoped>

</style>