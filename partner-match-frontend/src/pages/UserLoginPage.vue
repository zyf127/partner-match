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
  </van-form>
</template>

<script setup lang="ts">
  import {ref} from 'vue';
  import myAxios from "../plugins/myAxios";
  import {showFailToast, showSuccessToast} from "vant";
  import {useRouter} from "vue-router";

  const userAccount = ref('');
  const userPassword = ref('');
  const router = useRouter();
  const onSubmit = async () => {
    const res = await myAxios.post('/user/login', {
      userAccount: userAccount.value,
      userPassword: userPassword.value
    });
    if (res["code"] === 0 && res.data) {
      showSuccessToast('登录成功');
      router.replace('/user');
    } else {
      showFailToast('登录失败');
    }
  };
</script>

<style scoped>

</style>