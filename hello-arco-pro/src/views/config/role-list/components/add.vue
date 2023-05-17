<!--suppress ALL -->
<template>
  <a-form
    ref="addFormRef"
    label-align="left"
    :model="addForm"
    @submit="handleSubmit"
  >
    <a-form-item
      field="roleName"
      label="角色名称"
      hide-asterisk
      :validate-trigger="['change', 'input']"
      :rules="[{ required: true, message: '请输入角色名称' }]"
    >
      <a-input v-model="addForm.roleName" placeholder="请输入模块标签" />
    </a-form-item>
    <a-form-item
      field="role"
      label="角色"
      hide-asterisk
      :validate-trigger="['change', 'input']"
      :rules="[{ required: true, message: '请选择一个角色' }]"
    >
      <a-select
        v-model="addForm.role"
        :options="roleSelect"
        placeholder="请选择角色"
      />
    </a-form-item>
    <a-form-item
      field="permission"
      label="授权模块"
      hide-asterisk
      :validate-trigger="['change', 'input']"
      :rules="[{ required: true, message: '请至少选择一个授权模块' }]"
    >
      <a-tree-select
        v-model="addForm.permission"
        :data="moduleTree"
        :field-names="{
          key: 'name',
          title: 'title',
          children: 'children',
        }"
        :max-tag-count="3"
        :allow-search="true"
        :allow-clear="true"
        :tree-checkable="true"
        :tree-check-strictly="true"
        placeholder="请选择授权模块"
      />
    </a-form-item>
    <a-form-item
      field="organ"
      label="授权架构"
      hide-asterisk
      :validate-trigger="['change', 'input']"
    >
      <a-select
        v-model="addForm.organ"
        multiple
        :max-tag-count="3"
        :allow-clear="true"
        :allow-search="true"
        :field-names="{
          value: 'id',
          label: 'cname',
        }"
        :options="organList"
        placeholder="请选择授权架构"
      />
    </a-form-item>
    <a-form-item
      field="system"
      label="授权系统"
      hide-asterisk
      :validate-trigger="['change', 'input']"
    >
      <a-select
        v-model="addForm.system"
        multiple
        :max-tag-count="3"
        :allow-clear="true"
        :allow-search="true"
        :field-names="{
          value: 'syscode',
          label: 'sysname',
        }"
        :options="systemList"
        placeholder="请选择授权系统"
      />
    </a-form-item>
  </a-form>
</template>

<script lang="ts" setup>
  import {
    computed,
    getCurrentInstance,
    onMounted,
    PropType,
    reactive,
    ref,
  } from 'vue';
  import { moduleRecord } from '@/api/module';

  const props = defineProps({
    moduleTree: {
      type: Object as PropType<any>,
      default() {
        return [];
      },
    },
    organList: {
      type: Object as PropType<any>,
      default() {
        return [];
      },
    },
    systemList: {
      type: Object as PropType<any>,
      default() {
        return [];
      },
    },
  });

  const addForm = computed(() => form);

  const form = reactive({
    pid: null,
    title: '',
    name: '',
    path: '',
    icon: '',
    requireAuth: '',
    locale: '',
    order: '',
  });

  const roleSelect = [
    { value: 'admin', label: '管理员' },
    { value: 'operator', label: '操作员' },
    { value: 'maintain', label: '维护员' },
    { value: 'user', label: '普通用户' },
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
