<!--suppress ALL -->
<template>
  <a-form
    ref="editFormRef"
    label-align="left"
    :model="editForm"
    @submit="handleSubmit"
  >
    <a-form-item
      field="pid"
      label="上级模块"
      hide-asterisk
      :validate-trigger="['change', 'input']"
    >
      <a-tree-select
        v-model="editForm.pid"
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
      <a-input v-model="editForm.title" placeholder="请输入模块标签" />
    </a-form-item>
    <a-form-item
      field="name"
      label="模块名称"
      hide-asterisk
      :rules="[{ required: true, message: '请输入模块名称' }]"
      :validate-trigger="['change', 'input']"
    >
      <a-input v-model="editForm.name" placeholder="请输入模块名称" />
    </a-form-item>
    <a-form-item field="path" label="模块链接">
      <a-input v-model="editForm.path" placeholder="请输入模块链接" />
    </a-form-item>
    <a-form-item
      field="order"
      label="排序"
      hide-asterisk
      :rules="[{ required: true, message: '请输入排序编号' }]"
      :validate-trigger="['change', 'input']"
    >
      <a-input v-model="editForm.order" placeholder="请输入排序编号" />
    </a-form-item>
  </a-form>
</template>

<script lang="ts" setup>
  import { computed, onMounted, PropType, reactive, ref } from 'vue';
  import { moduleRecord } from '@/api/module';

  const props = defineProps({
    module: {
      type: Object as PropType<moduleRecord>,
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
  });

  const editForm = computed(() => {
    return props.module;
  });

  const enddateRule = [{ required: true, message: '请填写过期时间' }];

  const requireAuthOption = [
    { value: '1', label: '是' },
    { value: '0', label: '否' },
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
