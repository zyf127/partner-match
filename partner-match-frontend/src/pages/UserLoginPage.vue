<template>
  <van-form @submit="onSubmit" style="margin-top: 50px">
    <van-cell-group inset>
      <van-field
          v-model="userAccount"
          name="userAccount"
          label="账号"
          placeholder="请输入账号"
          :rules="[{ required: true, message: '不少于4位' }]"
      />
      <van-field
          v-model="userPassword"
          type="password"
          name="userPassword"
          label="密码"
          placeholder="请输入密码"
          :rules="[{ required: true, message: '不少于8位' }]"
      />
    </van-cell-group>
    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit">
        登录
      </van-button>
    </div>
    <van-cell title="" to="/user/register" value="还没有账号？点击注册"></van-cell>
    <van-loading class="loading" type="spinner" color="#1989fa" v-if="isLogin"/>
  </van-form>
</template>

<script setup lang="ts">
  import {ref} from "vue";
  // @ts-ignore
  import myAxios from "../plugins/myAxios";
  import {showFailToast, showSuccessToast} from "vant";
  import {useRouter} from "vue-router";

  const userAccount = ref('');
  const userPassword = ref('');
  const router = useRouter();

  const isLogin = ref(false);
  const onSubmit = async () => {
    isLogin.value = true;
    const res = await myAxios.post('/user/login', {
      userAccount: userAccount.value,
      userPassword: userPassword.value
    });
    if (res["code"] === 0 && res.data) {
      showSuccessToast('登录成功');
      router.back();
    } else {
      showFailToast('登录失败');
    }
    isLogin.value = false;
  };
</script>

<style scoped>
.loading {
  position: absolute;
  top: 22%;
  left: 45%;
}
</style>