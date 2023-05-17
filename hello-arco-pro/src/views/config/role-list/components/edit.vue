<!--suppress ALL -->
<template>
  <a-form
    ref="editFormRef"
    label-align="left"
    :model="editForm"
    @submit="handleSubmit"
  >
    <a-form-item
      field="roleName"
      label="角色名称"
      hide-asterisk
      :validate-trigger="['change', 'input']"
      :rules="[{ required: true, message: '请输入角色名称' }]"
    >
      <a-input v-model="editForm.roleName" placeholder="请输入角色名称" />
    </a-form-item>
    <a-form-item
      field="role"
      label="角色"
      hide-asterisk
      :validate-trigger="['change', 'input']"
      :rules="[{ required: true, message: '请选择一个角色' }]"
    >
      <a-select
        v-model="editForm.role"
        :options="roleSelect"
        placeholder="请输入模块名称"
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
        v-model="editForm.permission"
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
        v-model="editForm.organ"
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
        v-model="editForm.system"
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
  import { computed, onMounted, PropType, reactive, ref } from 'vue';
  import { roleRecord } from '@/api/role';

  const props = defineProps({
    role: {
      type: Object as PropType<roleRecord>,
      default() {
        return [];
      },
    },
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

  const editForm = computed(() => {
    return props.role;
  });

  const roleSelect = [
    { value: 'admin', label: '管理员' },
    { value: 'operator', label: '操作员' },
    { value: 'maintain', label: '维护员' },
    { value: 'user', label: '普通用户' },
  ];

  const editFormRef = ref<any>();

  const handleSubmit = () => {
    return editFormRef.value.validate();
  };

  const handleCancel = () => {
    editFormRef.value.clearValidate();
  };

  defineExpose({
    editForm,
    handleSubmit,
    handleCancel,
  });
</script>

<style scoped></style>
