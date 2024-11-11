<template>
  <van-form @submit="onSubmit" style="margin-top: 50px">
    <van-cell-group inset>
      <van-field
          v-model="username"
          name="username"
          label="昵称"
          placeholder="请输入昵称"
          :rules="[{ required: true, message: '请填写昵称' }]"
      />
      <van-field
          v-model="userAccount"
          name="userAccount"
          label="账号"
          placeholder="请输入账号"
          :rules="[{ required: true, message: '请填写账号'}, {validator: value => value.length >= 4, message: '账号不少于 4 位'}]"
      />
      <van-field
          v-model="userPassword"
          type="password"
          name="userPassword"
          label="密码"
          placeholder="请输入密码"
          :rules="[{ required: true, message: '请填写密码'}, {validator: value => value.length >= 8, message: '密码不少于 8 位'}]"
      />
      <van-field
          v-model="checkPassword"
          type="password"
          name="checkPassword"
          label="确认密码"
          placeholder="请输入确认密码"
          :rules="[{ required: true, message: '请填写确认密码'}, {validator: value => value == userPassword, message: '密码和确认密码必须相同'}]"
      />
    </van-cell-group>
    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit">
        注册
      </van-button>
    </div>
    <van-cell title="" to="/user/login" value="已有账号？点击登录"></van-cell>
  </van-form>
  <van-loading class="loading" type="spinner" color="#1989fa" v-if="isRegister"/>
</template>

<script setup lang="ts">
import {ref} from "vue";
// @ts-ignore
import myAxios from "../plugins/myAxios";
import {showFailToast, showSuccessToast} from "vant";
import {useRouter} from "vue-router";

const username = ref('');
const userAccount = ref('');
const userPassword = ref('');
const checkPassword = ref('');
const router = useRouter();
const isRegister = ref(false);
const onSubmit = async () => {
  isRegister.value = true;
  const res = await myAxios.post('/user/register', {
    username: username.value,
    userAccount: userAccount.value,
    userPassword: userPassword.value,
    checkPassword: checkPassword.value
  });
  if (res["code"] === 0 && res.data) {
    showSuccessToast('注册成功');
    setTimeout(() => {
      router.push('/user');
    }, 1000);
  } else {
    showFailToast('注册失败');
  }
  isRegister.value = false;
};
</script>

<style scoped>
.loading {
  position: absolute;
  top: 35%;
  left: 45%;
}
</style>