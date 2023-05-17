<!--suppress ALL -->
<template>
  <a-form
    ref="addFormRef"
    label-align="left"
    :model="addForm"
    @submit="handleSubmit"
  >
    <a-form-item
      field="pid"
      label="上级模块"
      hide-asterisk
      :validate-trigger="['change', 'input']"
    >
      <a-tree-select
        v-model="addForm.pid"
        :data="moduleTree"
        :style="{ width: '180px' }"
        allow-clear
        placeholder="请选择上级模块"
      />
    </a-form-item>
    <a-form-item
      field="title"
      label="模块标签"
      hide-asterisk
      :validate-trigger="['change', 'input']"
      :rules="[{ required: true, message: '请输入模块标签' }]"
    >
      <a-input v-model="addForm.title" placeholder="请输入模块标签" />
    </a-form-item>
    <a-form-item
      field="name"
      label="模块名称"
      hide-asterisk
      :validate-trigger="['change', 'input']"
      :rules="[{ required: true, message: '请输入模块名称' }]"
    >
      <a-input v-model="addForm.name" placeholder="请输入模块名称" />
    </a-form-item>
    <a-form-item field="path" label="模块链接" hide-asterisk>
      <a-input v-model="addForm.path" placeholder="请输入模块链接" />
    </a-form-item>
    <a-form-item
      field="order"
      label="排序"
      hide-asterisk
      :rules="[{ required: true, message: '请输入排序编号' }]"
      :validate-trigger="['change', 'input']"
    >
      <a-input v-model="addForm.order" placeholder="请输入排序编号" />
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
  });

  const addForm = computed(() => form);

  const form = reactive({
    pid: null,
    title: '',
    name: '',
    link: '',
    order: '',
  });

  const requireAuthOption = [
    { value: 1, label: '是' },
    { value: 0, label: '否' },
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
