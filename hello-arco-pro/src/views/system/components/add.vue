<!--suppress ALL -->
<template>
  <a-form
    ref="addFormRef"
    label-align="left"
    :model="addForm"
    @submit="handleSubmit"
  >
    <a-form-item
      field="sysname"
      label="系统名称"
      :rules="sysnameRule"
      :validate-trigger="['change', 'input']"
    >
      <a-input v-model="addForm.sysname" placeholder="请填写系统名称" />
    </a-form-item>
    <a-form-item
      field="syscode"
      label="系统代号"
      :rules="syscodeRule"
      :validate-trigger="['change', 'input']"
    >
      <a-input v-model="addForm.syscode" placeholder="请填写系统代号" />
    </a-form-item>
    <a-form-item
      field="status"
      label="状态"
      :rules="statusRule"
      :validate-trigger="['change', 'input']"
    >
      <a-radio-group
        v-model="addForm.status"
        :options="statusOption"
      ></a-radio-group>
    </a-form-item>
    <a-form-item
      field="loginUrl"
      label="登录地址"
      :rules="loginurlRule"
      :validate-trigger="['change', 'input']"
    >
      <a-input v-model="addForm.loginUrl" placeholder="请填写登录地址" />
    </a-form-item>
    <a-form-item field="roleApi" label="角色接口">
      <a-input v-model="addForm.roleApi" placeholder="请填写角色接口" />
    </a-form-item>
    <a-form-item field="apiParameter" label="接口参数">
      <a-input v-model="addForm.apiParameter" placeholder="请填写接口参数" />
    </a-form-item>
  </a-form>
</template>

<script lang="ts" setup>
  import { computed, reactive, ref } from 'vue';

  const addForm = computed(() => form);
  const form = reactive({
    loginid: '',
    sysname: '',
    syscode: '',
    loginUrl: '',
    status: '',
    roleApi: '',
    apiParameter: '',
  });

  const sysnameRule = [{ required: true, message: '请填写系统名称' }];
  const syscodeRule = [{ required: true, message: '请填写系统代号' }];
  const statusRule = [{ required: true, message: '请选择状态' }];
  const loginurlRule = [{ required: true, message: '请填写登录地址' }];
  const statusOption = [
    { value: '0', label: '正常' },
    { value: '1', label: '维护' },
    { value: '2', label: '故障' },
  ];

  const addFormRef = ref<any>();

  const handleSubmit = () => {
    return addFormRef.value.validate();
  };

  const handleCancel = () => {
    addFormRef.value.resetFields();
  };

  defineExpose({
    addForm,
    handleSubmit,
    handleCancel,
  });
</script>

<script lang="ts">
  export default {
    name: 'Add',
  };
</script>

<style scoped></style>
