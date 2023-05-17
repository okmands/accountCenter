<!--suppress ALL -->
<template>
  <a-form
    ref="addFormRef"
    label-align="left"
    :model="addForm"
    @submit="handleSubmit"
  >
    <a-form-item
      field="workid"
      label="工号"
      hide-asterisk
      :rules="workidRule"
      :validate-trigger="['change', 'input']"
    >
      <a-input
        v-model="addForm.workid"
        placeholder="请输入工号"
        @blur="trimLoginid"
      />
    </a-form-item>
    <a-form-item field="loginid" label="登录号" disabled>
      <a-input v-model="addForm.loginid" placeholder="请输入登录号" />
    </a-form-item>
    <a-form-item
      field="enddate"
      label="过期时间"
      hide-asterisk
      :rules="enddateRule"
      :validate-trigger="['change', 'input']"
    >
      <a-date-picker v-model="addForm.enddate" placeholder="请输入过期时间" />
    </a-form-item>
    <a-form-item label="授权系统">
      <a-space direction="vertical" :size="-10" style="display: block">
        <a-row
          v-for="(item, index) of addForm.authSystemList"
          :key="index"
          :gutter="8"
        >
          <a-col :span="11">
            <a-form-item hide-label :label="`${index}-system`">
              <a-select
                v-model="addForm.authSystemList[index].system"
                :options="option"
                placeholder="请选择系统"
                :style="{ width: '180px' }"
              >
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="11">
            <a-form-item hide-label>
              <a-tree-select
                v-model="addForm.authSystemList[index].roleName"
                :style="{ width: '180px' }"
                allow-clear
                :field-names="{
                  key: 'id',
                  title: 'text',
                  children: 'item',
                }"
                :data="roleOption[addForm.authSystemList[index].system]"
                placeholder="请选择系统角色权限"
                :load-more="loadMore"
              />
            </a-form-item>
          </a-col>
          <a-col :span="2">
            <a-button status="danger" shape="circle" @click="delSystem(index)">
              <icon-minus />
            </a-button>
          </a-col>
        </a-row>
        <a-row>
          <a-button type="primary" @click="addSystem">
            <template #icon>
              <icon-plus />
            </template>
          </a-button>
        </a-row>
      </a-space>
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

  const props = defineProps({
    option: {
      type: Object as PropType<any>,
      default() {
        return [];
      },
    },
    roleOption: {
      type: Object as PropType<any>,
      default() {
        return [];
      },
    },
  });

  const addForm = computed(() => form);
  const form = reactive({
    loginid: '',
    workid: '',
    enddate: '',
    authSystemList: [] as any[],
  });

  const loadMore = (nodeData: any) => {
    nodeData.item = nodeData.children;
  };

  const trimLoginid = (e: any) => {
    addForm.value.loginid = e.target.value.replace('A', '');
  };

  const workidRule = [
    { required: true, message: '请输入工号' },
    { maxLength: 8, message: '工号长度不可达大于8位' },
  ];

  const enddateRule = [{ required: true, message: '请填写过期时间' }];
  const addFormRef = ref<any>();
  const handleSubmit = () => {
    return addFormRef.value.validate();
  };
  const handleCancel = () => {
    addForm.value.authSystemList = [];
    addFormRef.value.resetFields();
  };

  const addSystem = () => {
    addForm.value.authSystemList.push({
      system: '',
      role: '',
      roleName: '',
    });
  };

  const delSystem = (index: number) => {
    addForm.value.authSystemList.splice(index, 1);
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
