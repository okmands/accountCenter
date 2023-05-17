<!--suppress ALL -->
<template>
  <a-form ref="editFormRef" label-align="left" :model="editForm">
    <a-form-item label="工号">
      <a-input :model-value="account.workid" disabled />
    </a-form-item>
    <a-form-item label="姓名">
      <a-input :model-value="account.name" disabled />
    </a-form-item>
    <a-form-item label="行政部门">
      <a-input :model-value="account.organ" disabled />
    </a-form-item>
    <a-form-item label="岗位">
      <a-input :model-value="account.post" disabled />
    </a-form-item>
    <a-form-item field="loginid" label="登录号">
      <a-input v-model="editForm.loginid" placeholder="请输入登录号" disabled />
    </a-form-item>
    <a-form-item
      field="enddate"
      label="过期时间"
      hide-asterisk
      :rules="enddateRule"
    >
      <a-date-picker v-model="editForm.enddate" placeholder="请输入过期时间" />
    </a-form-item>
    <a-form-item field="status" label="状态">
      <a-radio-group
        v-model="editForm.status"
        :options="statusOption"
      ></a-radio-group>
    </a-form-item>
    <a-form-item field="authSystemList" label="授权登录系统">
      <a-space direction="vertical" :size="-10" style="display: block">
        <a-row
          v-for="(item, index) of editForm.authSystemList"
          :key="index"
          :gutter="8"
        >
          <a-col :span="11">
            <a-form-item hide-label :label="`${index}-system`">
              <a-select
                v-model="editForm.authSystemList[index].system"
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
                v-model="editForm.authSystemList[index].roleName"
                :style="{ width: '180px' }"
                allow-clear
                :field-names="{
                  key: 'id',
                  title: 'text',
                  children: 'item',
                }"
                :label-in-value="true"
                :data="roleOption[editForm.authSystemList[index].system]"
                placeholder="请选择系统角色权限"
                :load-more="loadMore"
              >
              </a-tree-select>
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
  import { computed, onMounted, PropType, reactive, ref } from 'vue';
  import { accountRecord } from '@/api/accounts';
  import { systemTypeRecord } from '@/api/system';

  const props = defineProps({
    account: {
      type: Object as PropType<accountRecord>,
      default() {
        return [];
      },
    },
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
  const editForm = computed(() => {
    return props.account;
  });
  const enddateRule = [{ required: true, message: '请填写过期时间' }];
  const statusOption = [
    { value: '正常', label: '正常' },
    { value: '禁用', label: '禁用' },
  ];
  const editFormRef = ref<any>();
  const loadMore = (nodeData: any) => {
    nodeData.item = nodeData.children;
  };

  const handleSubmit = () => {
    return editFormRef.value.validate();
  };
  const handleCancel = () => {
    editFormRef.value.clearValidate();
  };

  const addSystem = () => {
    editForm.value.authSystemList.push({
      system: '',
      role: '',
      roleName: '',
    });
  };

  const delSystem = (index: number) => {
    editForm.value.authSystemList.splice(index, 1);
  };
  defineExpose({
    editForm,
    handleSubmit,
    handleCancel,
  });
</script>

<style scoped></style>
