<template>
  <van-form @submit="onSubmit" style="margin-top: 20px;">
    <van-cell-group inset>

      <div v-if="editInfo.editKey === 'gender'" style="display: flex; justify-content: center;">
        <van-radio-group v-model="editInfo.currentValue" direction="horizontal">
          <van-radio name="1">男</van-radio>
          <van-radio name="0">女</van-radio>
        </van-radio-group>
      </div>
      <template v-if="editInfo.editKey !== 'gender'">
        <van-field
            v-model="editInfo.currentValue"
            :name="editInfo.editKey"
            :label="editInfo.editName"
            :placeholder="`请输入${editInfo.editName}`"
            :rules="[{ required: true, message: `请填写${editInfo.editName}` }]"
        />
      </template>

      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit">
          提交
        </van-button>
      </div>
    </van-cell-group>
  </van-form>
</template>

<script setup lang="ts">
  //@ts-nocheck
  import {ref} from 'vue';
  import {useRoute, useRouter} from "vue-router";
  import myAxios from "../plugins/myAxios";
  import {getCurrentUser} from "../services/user.ts";
  import {showFailToast, showSuccessToast} from "vant";

  const route = useRoute();
  const router = useRouter();

  const editInfo = ref({
    editKey: route.query.editKey,
    editName: route.query.editName,
    currentValue: route.query.currentValue
  });

  const onSubmit = async () => {
    const currentUser = await getCurrentUser();
    const res = await myAxios.post('/user/update', {
      'id': currentUser["id"],
      [editInfo.value.editKey]: editInfo.value.currentValue
    });
    if (res['code'] === 0 && res.data > 0) {
      showSuccessToast('修改成功');
      router.back();
    } else {
      showFailToast('修改失败');
    }
  };
</script>

<style scoped>

</style>