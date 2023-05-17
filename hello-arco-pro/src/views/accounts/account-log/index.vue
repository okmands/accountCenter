<!--suppress ALL -->
<template>
  <div class="container">
    <a-card class="general-card">
      <a-row>
        <a-col :flex="1">
          <a-form
            :model="formModel"
            :label-col-props="{ span: 4 }"
            :wrapper-col-props="{ span: 16 }"
            label-align="left"
          >
            <a-row align="center">
              <a-col :span="7">
                <a-form-item
                  field="loginid"
                  label="登录号"
                  style="margin-bottom: 0"
                >
                  <a-input
                    v-model="formModel.loginid"
                    placeholder="请输入登录号"
                    @keyup.enter="search"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="7">
                <a-form-item
                  field="systemList"
                  label="系统类型"
                  style="margin-bottom: 0"
                >
                  <a-select
                    v-model="formModel.systemList"
                    :multiple="true"
                    placeholder="请选择系统类型"
                    :field-names="{ value: 'syscode', label: 'sysname' }"
                    :options="systemOption"
                    @change="search"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="10" :flex="1">
                <a-button type="primary" size="small" @click="search">
                  <template #icon>
                    <icon-search />
                  </template>
                  {{ '查询' }}
                </a-button>
                <a-button size="small" @click="reset">
                  <template #icon>
                    <icon-refresh />
                  </template>
                  {{ '重置' }}
                </a-button>
              </a-col>
            </a-row>
          </a-form>
        </a-col>
      </a-row>
      <a-table
        size="small"
        row-key="id"
        :stripe="true"
        :loading="loading"
        :pagination="pagination"
        :data="renderData"
        page-position="bottom"
        @page-change="onPageChange"
      >
        <template #columns>
          <a-table-column title="登录号" data-index="loginid" align="center">
          </a-table-column>
          <a-table-column title="系统类型" data-index="syscode" align="center">
            <template #cell="{ record }">
              <a-tag
                v-if="record.action === '修改密码'"
                color="rgb(var(--arcoblue-6))"
              >
                {{ '全部' }}
              </a-tag>
              <a-tag v-else color="rgb(var(--arcoblue-6))">
                {{ record.syscode }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column title="行为" data-index="action" align="center">
            <template #cell="{ record }">
              <a-tag
                v-if="record.action === '登录'"
                color="rgb(var(--green-6))"
              >
                {{ record.action }}
              </a-tag>
              <a-tag
                v-if="record.action === '修改密码'"
                color="rgb(var(--red-6))"
              >
                {{ record.action }}
              </a-tag>
              <a-tag
                v-if="record.action === '登出'"
                color="rgb(var(--arcoblue-6))"
              >
                {{ record.action }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column
            title="详情"
            data-index="actionContent"
            align="center"
          >
            <template #cell="{ record }">
              <a-tag
                v-if="record.actionContent.includes('错误')"
                color="rgb(var(--red-6))"
              >
                {{ record.actionContent }}
              </a-tag>
              <a-tag
                v-else-if="record.actionContent.includes('成功')"
                color="rgb(var(--green-6))"
              >
                {{ record.actionContent }}
              </a-tag>
              <a-tag v-else>
                {{ record.actionContent }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column
            title="时间"
            data-index="actionTime"
            align="center"
          ></a-table-column>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script lang="ts" setup>
  import { reactive, ref } from 'vue';
  import useLoading from '@/hooks/loading';
  import { getSystemTypeList, systemTypeRecord } from '@/api/system';
  import { Pagination } from '@/types/global';
  import {
    accountLogParams,
    accountLogRecord,
    getAccountLog,
  } from '@/api/accountLog';
  import { accountParams } from '@/api/accounts';

  const generateFormModel = () => {
    return {
      loginid: '',
      systemList: [],
    };
  };

  const { loading, setLoading } = useLoading(true);
  const systemOption = ref<systemTypeRecord[]>([]);
  const renderData = ref<accountLogRecord[]>([]);
  const formModel = ref(generateFormModel());

  const basePagination: Pagination = {
    current: 1,
    pageSize: 18,
  };

  const pagination = reactive({
    ...basePagination,
  });

  const fetchData = async (params: accountLogParams = basePagination) => {
    setLoading(true);
    try {
      const { data } = await getAccountLog(params);
      renderData.value = data.list;
      pagination.current = params.current;
      pagination.total = data.total;
    } catch (err) {
      // you can report use errorHandler or other
    } finally {
      setLoading(false);
    }
  };

  const fetchSystemType = async () => {
    const { data } = await getSystemTypeList();
    systemOption.value = data;
  };

  const search = () => {
    fetchData({
      ...basePagination,
      ...formModel.value,
    } as unknown as accountParams);
  };

  const onPageChange = (current: number) => {
    fetchData({
      ...basePagination,
      current,
      ...formModel.value,
    } as unknown as accountParams);
  };

  fetchSystemType();
  fetchData();

  const reset = () => {
    formModel.value = generateFormModel();
  };
</script>

<script lang="ts"></script>

<style lang="less" scoped>
  .container {
    padding: 10px 10px 10px 10px;
  }
  :deep(.arco-table-th) {
    &:last-child {
      .arco-table-th-item-title {
        margin-left: 16px;
      }
    }
  }
  :deep(.arco-card) {
    padding-top: 15px;
  }
</style>
