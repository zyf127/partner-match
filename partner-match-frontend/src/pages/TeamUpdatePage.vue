<template>
  <div id="teamUpdatePage">
    <div style="text-align: center; padding: 20px;">
      <van-uploader
          :before-read="beforeRead" :after-read="afterRead" :max-count="1"
          :max-size="10 * 1024 * 1024" @oversize="onOversize" image-fit="fill">
        <van-image
            style="box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);"
            width="125px"
            height="125px"
            radius="20%"
            fit="cover"
            :src="teamData.avatarUrl != null ? `${avatarBaseURL}${teamData.avatarUrl}` : defaultTeamAvatar"
        />
      </van-uploader>
      <van-loading class="loading" type="spinner" color="#1989fa" v-if="isUpload"/>
    </div>
    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field
            v-model="teamData.teamName"
            label="队伍名"
            placeholder="请输入队伍名"
            :rules="[{ required: true, message: '请填写队伍名'}, {validator: value => value.length <= 20, message: '队伍名不超过 20 个字符'}]"
        />
        <van-field
            v-model="teamData.teamDescription"
            rows="3"
            autosize
            label="队伍描述"
            type="textarea"
            placeholder="请输入队伍描述"
            :rules="[{validator: value => value.length <= 512, message: '队伍描述不超过 512 个字符'}]"
        />
        <van-field
            v-model="showDateTime"
            is-link
            readonly
            label="过期时间"
            placeholder="请选择过期时间"
            @click="showPicker = true"
            :rules="[{validator: value => Date.now() < new Date(teamData.expireTime).getTime(), message: '过期时间必须晚于当前时间'}]"
        />
        <van-popup v-model:show="showPicker" position="bottom">
          <van-picker-group
              title="过期时间"
              :tabs="['选择日期', '选择时间']"
              next-step-text="下一步"
              @confirm="onConfirm"
              @cancel="showPicker = false"
          >
            <van-date-picker
                v-model="expireDate"
                :min-date="nowDate"
            />
            <van-time-picker v-model="expireTime" />
          </van-picker-group>
        </van-popup>

        <van-field name="radio" label="队伍状态">
          <template #input>
            <van-radio-group v-model="teamData.teamStatus" direction="horizontal">
              <van-radio name="0">公开</van-radio>
              <van-radio name="1">私有</van-radio>
              <van-radio name="2">加密</van-radio>
            </van-radio-group>
          </template>
        </van-field>
        <van-field
            v-if="Number(teamData.teamStatus) === 2"
            v-model="teamData.teamPassword"
            type="teamPassword"
            label="密码"
            placeholder="请输入密码"
            :rules="[{ required: true, message: '请填写密码'}, {validator: value => value.length <= 32, message: '密码不超过 32 个字符'}]"
        />
      </van-cell-group>
      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit">
          提交
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup lang="ts">
//@ts-nocheck
import {onMounted, ref} from 'vue'
import {useRoute, useRouter} from "vue-router";
import myAxios from "../plugins/myAxios";
import {showFailToast, showSuccessToast} from "vant";
import {formatDateTime} from "../services/datetime.ts";
import {avatarBaseURL} from "../constants/avatar.ts";
import defaultTeamAvatar from "../assets/avatar/defaultTeamAvatar.png";


const router = useRouter();
const route = useRoute();
const teamId = route.query.teamId;
const teamData = ref({});

const nowDate = new Date();
const expireDate = ref(['', '', '']);
const expireTime = ref(['', '']);
const showPicker = ref(false);
const showDateTime = ref('');
const isUpload = ref(false);

onMounted(async () => {
  await load();
})

const load = async () => {
  const res = await myAxios.get('/team/get', {
    params: {
      teamId
    }
  });
  if (res['code'] === 0) {
    teamData.value = res.data;
    teamData.value.teamStatus = String(teamData.value.teamStatus);
    showDateTime.value = formatDateTime(teamData.value.expireTime);
  } else {
    showFailToast('加载失败');
    router.back();
  }
}

const onConfirm = () => {
  teamData.value.expireTime =  `${expireDate.value.join('-')} ${expireTime.value.join(':')}`;
  showDateTime.value = formatDateTime(teamData.value.expireTime);
  showPicker.value = false;
};

const onSubmit = async () => {
  let teamPostData = {
    ...teamData.value,
    teamId: teamData.value.id,
    teamStatus: Number(teamData.value.teamStatus),
    expireTime: new Date(teamData.value.expireTime),
  };

  const res = await myAxios.post('/team/update', teamPostData);
  if (res['code'] === 0 && res.data) {
    showSuccessToast('更新成功');
    router.back();
  } else {
    showFailToast('更新失败');
  }
}

const beforeRead = (file: any) => {
  if (file.type === '') {
    showFailToast('请上传图片');
    return false;
  }
  return true;
};

const afterRead = async (file: any) => {
  isUpload.value = true;
  const formData = new FormData();
  formData.append('avatarFile', file.file);
  formData.append('teamId', teamId);
  const res = await myAxios.post('/team/avatar', formData);
  if (res.code === 0) {
    load();
  } else {
    showFailToast('更换头像失败');
  }
  isUpload.value = false;
}

const onOversize = () => {
  showFailToast('头像大小不能超过 10MB');
}
</script>

<style scoped>

</style>