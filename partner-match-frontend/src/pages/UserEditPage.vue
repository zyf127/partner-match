<template>
  <van-form @submit="onSubmit">
    <van-cell-group inset>
      <van-field
          v-model="editInfo.currentValue"
          :name="editInfo.editKey"
          :label="editInfo.editName"
          :placeholder="`请输入${editInfo.editName}`"
          :rules="[{ required: true, message: `请填写${editInfo.editName}` }]"
      />
      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit">
          提交
        </van-button>
      </div>
    </van-cell-group>
  </van-form>
</template>

<script setup lang="ts">
  import {ref} from 'vue';
  import {useRoute} from "vue-router";

  const route = useRoute();
  const editInfo = ref({
    editKey: route.query.editKey,
    editName: route.query.editName,
    currentValue: route.query.currentValue
  });

  const onSubmit = () => {
    // TODO: 发送要修改的信息给后端
    console.log('edit info success: ' + editInfo.value.currentValue);
  };
</script>

<style scoped>

</style>