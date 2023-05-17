<!--suppress ALL -->
<template>
  <div class="container">
    <!--    <Breadcrumb :items="['menu.accounts', 'menu.accounts.accountList']" />-->
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
                  field="search"
                  label="精确查询"
                  style="margin-bottom: 0"
                >
                  <a-input
                    v-model="formModel.search"
                    placeholder="请输入工号/登录号/姓名"
                    @keyup.enter="search"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="7">
                <a-form-item
                  field="authSystemList"
                  label="授权系统"
                  style="margin-bottom: 0"
                >
                  <a-select
                    v-model="formModel.authSystemList"
                    :multiple="true"
                    placeholder="请选择已授权登录的系统类型"
                    :field-names="{ value: 'syscode', label: 'sysname' }"
                    :options="authSystemOption"
                    @change="search"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="10">
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
      <a-row style="margin-bottom: 12px">
        <a-col :span="16">
          <a-space>
            <a-button
              v-permission="['admin', 'operator']"
              :disabled="authSystemOption.length === 0"
              type="primary"
              size="small"
              @click="addAccount"
            >
              <template #icon>
                <icon-plus />
              </template>
              {{ '新建账号' }}
            </a-button>
            <a-button
              v-permission="['admin', 'operator']"
              type="primary"
              status="warning"
              size="small"
              @click="handle"
            >
              <template #icon>
                <icon-delete />
              </template>
              {{ '删除账号' }}
            </a-button>
            <a-button
              v-permission="['admin', 'operator']"
              type="primary"
              status="warning"
              size="small"
              @click="handle"
            >
              <template #icon>
                <icon-sync />
              </template>
              {{ '重置密码' }}
            </a-button>
          </a-space>
        </a-col>
        <a-col :span="8" style="text-align: right">
          <a-button v-permission="['admin', 'operator']">
            <template #icon>
              <icon-download />
            </template>
            {{ '导出' }}
          </a-button>
        </a-col>
      </a-row>
      <a-table
        size="small"
        row-key="guid"
        :stripe="true"
        :loading="loading"
        :pagination="pagination"
        :data="renderData"
        page-position="bottom"
        :row-selection="rowSelection"
        @page-change="onPageChange"
        @selection-change="tableSelection"
      >
        <template #columns>
          <a-table-column
            title="已授权登录的系统"
            data-index="authSystemList"
            align="center"
          >
            <template #cell="{ record }">
              <a-space>
                <a-tag
                  v-for="(r, i) in record.authSystemList"
                  :key="i"
                  size="small"
                  color="#165dff"
                >
                  {{ r.system }}
                </a-tag>
              </a-space>
            </template>
          </a-table-column>
          <a-table-column
            title="登录号"
            data-index="loginid"
            align="center"
            :width="100"
          />
          <a-table-column
            title="状态"
            data-index="status"
            align="center"
            :width="80"
          >
            <template #cell="{ record }">
              <span
                v-if="record.status === '禁用'"
                class="circle"
                style="
                  margin-bottom: 5%;
                  background-color: rgb(var(--warning-6));
                "
              ></span>
              <span v-else class="circle pass" style="margin-bottom: 5%"></span>
              {{ record.status }}
            </template>
          </a-table-column>
          <a-table-column
            title="工号"
            data-index="workid"
            align="center"
            :width="100"
          />
          <a-table-column
            title="姓名"
            data-index="name"
            align="center"
            :width="100"
          />
          <a-table-column
            title="行政部门"
            data-index="organ"
            align="center"
            :width="300"
          />
          <a-table-column
            title="岗位"
            data-index="post"
            align="center"
            :width="150"
          />
          <a-table-column
            title="过期时间"
            data-index="enddate"
            align="center"
            :width="150"
          />
          <a-table-column title="操作" align="center" :width="200">
            <template #cell="{ record }">
              <a-button
                v-permission="['admin', 'operator']"
                :disabled="authSystemOption.length === 0"
                type="text"
                size="mini"
                @click="editAccount(record)"
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
      title="编辑账号"
      :on-before-ok="editAccountBefore"
      width="30%"
      @cancel="cancelEditAccount"
    >
      <EditAccount
        ref="editData"
        :account="accountItem"
        :option="authSystemListOption"
        :role-option="authSystemRoleListOption"
      />
    </a-modal>
    <a-modal
      v-model:visible="addShow"
      :fullscreen="false"
      title="新增账号"
      :on-before-ok="addAccountBefore"
      width="30%"
      @cancel="cancelAddAccount"
    >
      <AddAccount
        ref="addData"
        :option="authSystemListOption"
        :role-option="authSystemRoleListOption"
      />
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
  import { computed, ref, reactive, onMounted } from 'vue';
  import useLoading from '@/hooks/loading';
  import {
    getAccount,
    accountRecord,
    accountParams,
    updateAccount,
    saveAccount,
    deleteAccount,
    resetPassword,
    verifyWorkid,
  } from '@/api/accounts';
  import { getSystemTypeList, systemTypeRecord } from '@/api/system';
  import { Pagination } from '@/types/global';
  import { Notification, Message } from '@arco-design/web-vue';

  const generateFormModel = () => {
    return {
      search: '',
      authSystemList: [],
    };
  };

  const editShow = ref(false);
  const addShow = ref(false);
  const { loading, setLoading } = useLoading(true);
  const authSystemListOption = ref([{}]);
  const authSystemOption = ref<systemTypeRecord[]>([]);
  const authSystemRoleListOption = [];
  const accountItem = ref<accountRecord>();
  const renderData = ref<accountRecord[]>([]);
  const formModel = ref(generateFormModel());
  const addData = ref<any>(null);
  const editData = ref<any>(null);

  const rowSelection = ref<any>({
    type: 'checkbox',
    showCheckedAll: true,
    onlyCurrent: true,
  });

  const basePagination: Pagination = {
    current: 1,
    pageSize: 18,
  };

  const pagination = reactive({
    ...basePagination,
  });

  const fetchSystemType = async () => {
    const { data } = await getSystemTypeList();
    authSystemOption.value = JSON.parse(JSON.stringify(data));
  };

  const fetchData = async (params: accountParams = basePagination) => {
    setLoading(true);
    try {
      const { data } = await getAccount(params);
      renderData.value = data.list;
      pagination.current = params.current;
      pagination.total = data.total;
    } catch (err) {
      // you can report use errorHandler or other
    } finally {
      setLoading(false);
    }
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

  const addAccount = () => {
    addShow.value = true;
    authSystemListOption.value = [];
    authSystemOption.value.forEach((e: any) => {
      authSystemListOption.value.push({
        label: e.sysname,
        value: e.syscode,
      });
      authSystemRoleListOption[e.syscode] = e.role ? e.role : [];
    });
  };

  const addAccountBefore = (done: any) => {
    addData.value.handleSubmit().then((valid: any) => {
      if (!valid) {
        const { addForm } = addData.value;
        addForm.authSystemList.forEach((item: any, index: number) => {
          if (item.system === '') {
            Message.warning('请至少授权一个系统');
            done(false);
            throw new Error();
          } else if (item.roleName) {
            addForm.authSystemList[index].role = item.roleName.value;
            addForm.authSystemList[index].roleName = item.roleName.label;
          }
        });
        verifyWorkid(addForm)
          .then((resp: any) => {
            if (resp.code === 20000 && resp.data) {
              addForm.name = resp.data.sname;
              saveAccount(addForm).then((resp: any) => {
                if (resp.code === 20000) {
                  Notification.success(resp.msg);
                  addData.value.handleCancel();
                } else {
                  Notification.error(resp.msg);
                }
              });
              done();
              search();
            } else if (resp.code === 20000 && !resp.data) {
              Message.warning(resp.msg);
              done(false);
            }
          })
          .catch((resp: any) => {
            Message.warning(resp.msg);
            done(false);
          });
      }
      done(false);
    });
  };

  const cancelAddAccount = () => {
    addData.value.handleCancel();
  };

  const editAccount = (account: accountRecord) => {
    editShow.value = true;
    authSystemListOption.value = [];
    accountItem.value = JSON.parse(JSON.stringify(account));
    authSystemOption.value.forEach((e: any) => {
      authSystemListOption.value.push({
        label: e.sysname,
        value: e.syscode,
      });
      authSystemRoleListOption[e.syscode] = e.role ? e.role : [];
    });
  };

  const editAccountBefore = (done: any) => {
    editData.value.handleSubmit().then((valid: any) => {
      if (!valid) {
        const { editForm } = editData.value;
        editForm.authSystemList.forEach((item: any, index: number) => {
          if (item.system === '') {
            Message.warning('请至少授权一个系统');
            done(false);
            throw new Error();
          } else if (item.roleName) {
            editForm.authSystemList[index].role = item.roleName.value;
            editForm.authSystemList[index].roleName = item.roleName.label;
          }
        });
        updateAccount(editForm).then((resp: any) => {
          if (resp.code === 20000) {
            Notification.success(resp.msg);
            done();
            search();
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

  const cancelEditAccount = () => {
    editData.value.handleCancel();
  };

  const guidList = ref<any>([]);
  const tableSelection = (guid: any) => {
    guidList.value = { guids: guid };
  };

  const handle = (e: any) => {
    if (guidList.value.length === 0) {
      Message.warning('你没有选择任何要操作的账号，请先选择！');
    } else {
      switch (e.target.innerText.trim()) {
        case '重置密码':
          resetPassword(guidList.value).then((resp: any) => {
            if (resp.code === 20000) {
              Notification.success(resp.msg);
            } else {
              Notification.error(resp.msg);
            }
          });
          break;
        case '删除账号':
          deleteAccount(guidList.value).then((resp: any) => {
            if (resp.code === 20000) {
              search();
              Notification.success(resp.msg);
            } else {
              Notification.error(resp.msg);
            }
          });
          break;
        default:
          break;
      }
    }
  };
</script>

<script lang="ts">
  export default {
    name: 'Index',
  };
</script>

<style scoped lang="less">
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
