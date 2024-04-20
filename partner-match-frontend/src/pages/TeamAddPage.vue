<template>
  <div id="teamAddPage">
    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field
            v-model="teamData.teamName"
            label="队伍名"
            placeholder="请输入队伍名"
            :rules="[{ required: true, message: '请填写队伍名'}, {validator: value => value.length <= 20, message: '队伍名不超过 20 个字符'}]"
        />
        <van-field
            v-model="teamData.description"
            rows="3"
            autosize
            label="队伍描述"
            type="textarea"
            placeholder="请输入队伍描述"
            :rules="[{validator: value => value.length <= 512, message: '队伍描述不超过 512 个字符'}]"
        />
        <van-field name="stepper" label="最大人数">
          <template #input>
            <van-stepper v-model="teamData.maxNum" label="队伍名" min="2" max="20"/>
          </template>
        </van-field>
        <van-field
            v-model="teamData.expireTime"
            is-link
            readonly
            label="过期时间"
            placeholder="请选择过期时间"
            @click="showPicker = true"
            :rules="[{validator: value => Date.now() < new Date(value).getTime(), message: '过期时间必须晚于当前时间'}]"
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
            <van-radio-group v-model="teamData.status" direction="horizontal">
              <van-radio name="0">公开</van-radio>
              <van-radio name="1">私有</van-radio>
              <van-radio name="2">加密</van-radio>
            </van-radio-group>
          </template>
        </van-field>
        <van-field
            v-if="Number(teamData.status) === 2"
            v-model="teamData.password"
            type="password"
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
  import {ref} from 'vue'
  import {useRouter} from "vue-router";
  import myAxios from "../plugins/myAxios";
  import {showFailToast, showSuccessToast} from "vant";


  const router = useRouter();

  const initTeamData = {
    "teamName": "",
    "description": "",
    "maxNum": 2,
    "expireTime": "",
    "status": "0",
    "password": ""
  }

  const teamData = ref({...initTeamData});

  const nowDate = new Date();
  const expireDate = ref(['', '', '']);
  const expireTime = ref(['', '']);
  const showPicker = ref(false);

  const onConfirm = () => {
    teamData.value.expireTime =  `${expireDate.value.join('-')}  ${expireTime.value.join(':')}`;
    showPicker.value = false;
  };

  const onSubmit = async () => {
    let teamPostData = {
      ...teamData.value,
      status: Number(teamData.value.status),
      expireTime: new Date(teamData.value.expireTime)
    };

    const res = await myAxios.post('/team/add', teamPostData);
    if (res['code'] === 0 && res.data) {
      showSuccessToast('添加成功');
      router.push({
        path: '/team',
        replace: true
      })
    } else {
      showFailToast('添加失败');
    }
  }
</script>

<style scoped>

</style>