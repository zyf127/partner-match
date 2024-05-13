<template>
  <template v-if="user">
    <div style="text-align: center; padding: 20px;">
      <van-uploader
          :before-read="beforeRead" :after-read="afterRead" :max-count="1"
          :max-size="5 * 1024 * 1024" @oversize="onOversize">
        <van-image
            style="box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);"
            width="125px"
            height="125px"
            :src="user.avatarUrl != null ? `http://${avatarBaseURL}${user.avatarUrl}` : defaultUserAvatar"
            radius="20%"
            fit="cover"
        />
      </van-uploader>
    </div>
    <van-cell title="昵称" is-link :value="user.username" @click="toEdit('username', '昵称', user.username)"/>
    <van-cell title="账号" :value="user.userAccount"/>
    <van-cell title="性别" is-link :value="user.gender === 0 ? '女' : '男'" @click="toEdit('gender', '性别', user.gender)"/>
    <van-cell title="手机号" is-link :value="getSafetyPhone(user.phone)" @click="toEdit('phone', '手机号', user.phone)"/>
    <van-cell title="邮箱" is-link :value="getSafeEmail(user.email)" @click="toEdit('email', '邮箱', user.email)"/>
    <van-cell title="我的标签" is-link value="点击查看" @click="showMyTags"/>
    <van-cell title="注册时间" :value="formatDateTime(user.createTime)"/>
    <div style="padding: 10px;">
      <van-button type="primary" block @click="logout">退 出 登 录</van-button>
    </div>
  </template>
</template>

<script setup lang="ts">
  import {onMounted, ref} from "vue";
  import {useRouter} from "vue-router";
  import {getCurrentUser} from "../services/user.ts";
  import {formatDateTime} from "../services/team.ts";
  // @ts-ignore
  import myAxios from "../plugins/myAxios";
  import {showFailToast, showSuccessToast} from "vant";
  import defaultUserAvatar from "../assets/defaultUserAvatar.jpg"
  import {avatarBaseURL} from "../constants/avatar.ts";

  const router = useRouter();
  const user = ref();

  onMounted(async () => {
    const currentUser = await getCurrentUser();
    user.value = currentUser;
  });

  const toEdit = (editKey: string, editName: string, currentValue: string) => {
    router.push({
      path: '/user/edit',
      query: {
        editKey,
        editName,
        currentValue
      }
    });
  };

  const logout = async () => {
    const res = await myAxios.post('/user/logout');
    if (res['code'] === 0) {
      showSuccessToast('退出登录成功');
      location.reload();
    } else {
      showFailToast('退出登录失败');
    }
  }

  const getSafetyPhone = (s: string) => {
    let charArray = s.split('');
    for (let i = 0; i < s.length; i++) {
      if (i >= 3 && i <= 6) {
        charArray[i] = '*';
      }
    }
    return charArray.join('');
  }

  const getSafeEmail = (s: string) => {
    let charArray = s.split('');
    let i;
    for (i = 0; i < s.length; i++) {
      if (charArray[i] === '@') {
        break;
      }
    }
    for (let j =  Math.floor(i / 2); j < i; j++) {
      charArray[j] = '*';
    }
    return charArray.join('');
  }

  const showMyTags = () => {
    router.push({
      path: '/search',
      query: {
        userId: user.value.id,
        myTagNameList: user.value.tagNames
      }
    });
  }

  const beforeRead = (file: any) => {
    if (file.type === '') {
      showFailToast('请上传图片');
      return false;
    }
    return true;
  };


  const afterRead = async (file: any) => {
    const formData = new FormData();
    formData.append('avatarFile', file.file);
    const res = await myAxios.post('/user/avatar', formData);
    if (res.code === 0) {
      location.reload();
    } else {
      showFailToast('更换头像失败');
    }
  }

  const onOversize = () => {
    showFailToast('头像大小不能超过 5MB');
  }
</script>

<style scoped>

</style>