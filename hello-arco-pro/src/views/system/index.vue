<!--suppress ALL -->
<template>
  <div class="container">
    <a-card class="general-card">
      <a-row style="margin-bottom: 16px">
        <a-col>
          <a-space>
            <a-button
              v-permission="['admin']"
              type="primary"
              size="small"
              @click="addSystem"
            >
              <template #icon>
                <icon-plus />
              </template>
              {{ '新增系统' }}
            </a-button>
            <a-button
              v-permission="['admin']"
              type="primary"
              status="warning"
              size="small"
              @click="deleteSystem"
            >
              <template #icon>
                <icon-delete />
              </template>
              {{ '删除系统' }}
            </a-button>
          </a-space>
        </a-col>
      </a-row>
      <a-table
        size="small"
        row-key="id"
        :stripe="true"
        :loading="loading"
        :pagination="pagination"
        :data="renderData"
        :row-selection="rowSelection"
        page-position="bottom"
        @page-change="onPageChange"
        @selection-change="tableSelection"
      >
        <template #columns>
          <a-table-column title="名称" data-index="sysname" align="center" />
          <a-table-column title="代号" data-index="syscode" align="center" />
          <a-table-column title="用户数" data-index="usercount" align="center">
            <template #cell="{ record }">
              <a-statistic
                :value="record.usercount"
                :precision="0"
                :value-from="0"
                :start="true"
                animation
              >
              </a-statistic>
            </template>
          </a-table-column>
          <a-table-column title="状态" data-index="status" align="center">
            <template #cell="{ record }">
              <span
                v-if="record.status === '1'"
                class="circle"
                style="
                  background-color: rgb(var(--warning-6));
                  margin-bottom: 1.5%;
                "
              >
              </span>
              <span
                v-else-if="record.status === '2'"
                class="circle"
                style="
                  background-color: rgb(var(--danger-7));
                  margin-bottom: 1.5%;
                "
              >
              </span>
              <span
                v-else
                class="circle pass"
                style="margin-bottom: 1.5%"
              ></span>
              {{ record.status === '2' ? '故障' : '' }}
              {{ record.status === '1' ? '维护' : '' }}
              {{ record.status === '0' ? '正常' : '' }}
            </template>
          </a-table-column>
          <a-table-column title="登录地址" data-index="loginUrl" align="center">
            <template #cell="{ record }">
              <a-link :href="record.loginUrl" status="success" icon>{{
                record.loginUrl
              }}</a-link>
            </template>
          </a-table-column>
          <a-table-column title="操作" align="center" :width="200">
            <template #cell="{ record }">
              <a-button
                v-permission="['admin']"
                type="text"
                size="mini"
                @click="editSystem(record)"
              >
                <icon-edit />
                编辑信息
              </a-button>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-card>
    <a-modal
      v-model:visible="editShow"
      :fullscreen="false"
      title="编辑系统"
      :on-before-ok="editSystemBefore"
      @cancel="cancelEditSystem"
    >
      <EditSystem ref="editData" :system="systemItem" />
    </a-modal>
    <a-modal
      v-model:visible="addShow"
      :fullscreen="false"
      title="新增系统"
      :on-before-ok="addSystemBefore"
      @cancel="cancelAddSystem"
    >
      <AddSystem ref="addData" />
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
  import { computed, reactive, ref } from 'vue';
  import { Pagination } from '@/types/global';
  import {
    addSystemType,
    deleteSystemType,
    getSystemType,
    systemParams,
    systemTypeRecord,
    updateSystemType,
  } from '@/api/system';
  import useLoading from '@/hooks/loading';
  import { Message, Notification } from '@arco-design/web-vue';

  const addShow = ref(false);
  const editShow = ref(false);
  const { loading, setLoading } = useLoading(true);
  const renderData = ref<systemTypeRecord[]>([]);
  const addData = ref<any>(null);
  const editData = ref<any>(null);
  const systemItem = ref<systemTypeRecord>();

  const rowSelection = ref<any>({
    type: 'checkbox',
    showCheckedAll: true,
    onlyCurrent: true,
  });

  const basePagination: Pagination = {
    current: 1,
    pageSize: 20,
  };

  const pagination = reactive({
    ...basePagination,
  });

  const fetchData = async (params: systemParams = basePagination) => {
    setLoading(true);
    try {
      const { data } = await getSystemType(params);
      renderData.value = data.list;
      pagination.current = params.current;
      pagination.total = data.total;
    } catch (err) {
      // you can report use errorHandler or other
    } finally {
      setLoading(false);
    }
  };

  const onPageChange = (current: number) => {
    fetchData({
      ...basePagination,
      current,
    } as unknown as systemParams);
  };

  fetchData({
    ...basePagination,
  } as unknown as systemParams);

  const addSystem = () => {
    addShow.value = true;
  };

  const addSystemBefore = (done: any) => {
    addData.value.handleSubmit().then((valid: any) => {
      if (!valid) {
        const { addForm } = addData.value;
        addSystemType(addForm).then((resp: any) => {
          if (resp.code === 20000) {
            Notification.success(resp.msg);
            fetchData();
            done();
          } else {
            Message.warning(resp.msg);
            done(false);
          }
        });
      } else {
        done(false);
      }
    });
  };

  const cancelAddSystem = () => {
    addData.value.handleCancel();
  };

  const editSystem = (system: systemTypeRecord) => {
    editShow.value = true;
    systemItem.value = system;
  };

  const editSystemBefore = (done: any) => {
    editData.value.handleSubmit().then((valid: any) => {
      if (!valid) {
        const { editForm } = editData.value;
        updateSystemType(editForm).then((resp: any) => {
          if (resp.code === 20000) {
            Notification.success(resp.msg);
            fetchData();
            done();
          } else {
            Message.warning(resp.msg);
            done(false);
          }
        });
      } else {
        done(false);
      }
    });
  };

  const cancelEditSystem = () => {
    editData.value.handleCancel();
  };

  const idList = ref<any>([]);

  const tableSelection = (id: any) => {
    idList.value = { idList: id };
  };

  const deleteSystem = () => {
    if (idList.value.length === 0) {
      Message.warning('你没有选择任何要操作的项目，请先选择！');
    } else {
      deleteSystemType(idList.value).then((resp: any) => {
        if (resp.code === 20000) {
          Notification.success(resp.msg);
          fetchData();
        } else {
          Notification.error(resp.msg);
        }
      });
    }
  };
</script>

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
  :deep(.arco-statistic-value) {
    font-size: 16px !important;
    font-weight: normal !important;
    .arco-statistic-value-integer {
      font-size: 16px !important;
    }
  }
</style>
