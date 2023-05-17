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
                  field="search"
                  label="查询"
                  style="margin-bottom: 0"
                >
                  <a-input
                    v-model="formModel.search"
                    placeholder="请输入角色名称"
                    @keyup.enter="search"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="7">
                <a-form-item
                  field="role"
                  label="角色类型"
                  style="margin-bottom: 0"
                >
                  <a-select
                    v-model="formModel.role"
                    :multiple="true"
                    placeholder="请选择角色"
                    :options="roleSelect"
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
      <a-row style="margin-bottom: 16px">
        <a-col>
          <a-space>
            <a-button
              v-permission="['admin']"
              :disabled="
                organList.length === 0 ||
                moduleTree.length === 0 ||
                systemList.length === 0
              "
              type="primary"
              size="small"
              @click="add"
            >
              <template #icon>
                <icon-plus />
              </template>
              {{ '新增角色' }}
            </a-button>
            <a-button
              v-permission="['admin']"
              type="primary"
              status="warning"
              size="small"
              @click="deleted"
            >
              <template #icon>
                <icon-delete />
              </template>
              {{ '删除角色' }}
            </a-button>
          </a-space>
        </a-col>
      </a-row>
      <a-table
        size="small"
        row-key="guid"
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
          <a-table-column
            title="角色名称"
            data-index="roleName"
            align="center"
          />
          <a-table-column title="角色" data-index="role" align="center" option>
            <template #cell="{ record }">
              <a-tag size="small" color="#165dff">
                {{ getRoleName(record.role) }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column title="操作" align="center" :width="200">
            <template #cell="{ record }">
              <a-button
                v-permission="['admin']"
                :disabled="
                  organList.length === 0 ||
                  moduleTree.length === 0 ||
                  systemList.length === 0
                "
                type="text"
                size="mini"
                @click="edit(record)"
              >
                <icon-edit />
                编辑角色
              </a-button>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-card>
    <a-modal
      v-model:visible="editShow"
      :fullscreen="false"
      title="编辑角色"
      :on-before-ok="editRoleBefore"
      @cancel="cancelEditRole"
    >
      <EditRole
        ref="editData"
        :role="roleItem"
        :module-tree="moduleTree"
        :organ-list="organList"
        :system-list="systemList"
      />
    </a-modal>
    <a-modal
      v-model:visible="addShow"
      :fullscreen="false"
      title="新增角色"
      :on-before-ok="addRoleBefore"
      @cancel="cancelAddRole"
    >
      <AddRole
        ref="addData"
        :module-tree="moduleTree"
        :organ-list="organList"
        :system-list="systemList"
      />
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
  import {
    getRole,
    getOrgan,
    getModule,
    getSystem,
    roleRecord,
    roleParams,
    addRole,
    deleteRole,
    updateRole,
  } from '@/api/role';
  import { reactive, ref, render } from 'vue';
  import { Pagination } from '@/types/global';
  import useLoading from '@/hooks/loading';
  import { Message, Notification } from '@arco-design/web-vue';

  const addShow = ref(false);

  const editShow = ref(false);

  const { loading, setLoading } = useLoading(true);

  const addData = ref<any>(null);

  const editData = ref<any>(null);

  const moduleTree = ref<any>([]);

  const organList = ref<any>([]);

  const systemList = ref<any>([]);

  const roleItem = ref<roleRecord>();

  const renderData = ref<roleRecord[]>([]);

  const generateFormModel = () => {
    return {
      search: '',
      role: [],
    };
  };

  const roleSelect = [
    { value: 'admin', label: '管理员' },
    { value: 'operator', label: '操作员' },
    { value: 'maintain', label: '维护员' },
    { value: 'user', label: '普通用户' },
  ];

  const formModel = ref(generateFormModel());

  const basePagination: Pagination = {
    current: 1,
    pageSize: 20,
  };

  const pagination = reactive({
    ...basePagination,
  });

  const fetchRole = async (
    params: roleParams = { current: 1, pageSize: 20 }
  ) => {
    setLoading(true);
    try {
      const { data } = await getRole(params);
      data.list.forEach((item: any) => {
        item.permission = item.permission ? JSON.parse(item.permission) : [];
        item.organ = item.organ ? JSON.parse(item.organ) : [];
        item.system = item.system ? JSON.parse(item.system) : [];
      });
      renderData.value = data.list;
      pagination.current = params.current;
      pagination.total = data.total;
    } catch (e) {
      console.log(e);
    } finally {
      setLoading(false);
    }
  };

  const fetchOrgan = async () => {
    try {
      const { data } = await getOrgan();
      organList.value = data;
    } catch (e) {
      console.log(e);
    }
  };

  const fetchModule = async () => {
    try {
      const { data } = await getModule();
      moduleTree.value = data;
    } catch (e) {
      console.log(e);
    }
  };

  const fetchSystem = async () => {
    try {
      const { data } = await getSystem();
      systemList.value = data;
    } catch (e) {
      console.log(e);
    }
  };

  const search = () => {
    fetchRole({
      ...basePagination,
      ...formModel.value,
    } as unknown as roleParams);
  };

  const reset = () => {
    formModel.value = generateFormModel();
  };

  const rowSelection = ref<any>({
    type: 'checkbox',
    showCheckedAll: true,
    onlyCurrent: true,
  });

  const idList = ref<any>([]);

  const tableSelection = (id: any) => {
    idList.value = { idList: id };
  };

  const onPageChange = (current: number) => {
    fetchRole({
      ...basePagination,
      current,
    } as unknown as roleParams);
  };

  fetchRole({
    ...basePagination,
  });
  fetchOrgan();
  fetchModule();
  fetchSystem();

  const add = () => {
    addShow.value = true;
  };

  const addRoleBefore = (done: any) => {
    addData.value.handleSubmit().then((valid: any) => {
      if (!valid) {
        const { addForm } = JSON.parse(JSON.stringify(addData.value));
        addForm.permission = JSON.stringify(addForm.permission);
        addForm.organ = JSON.stringify(addForm.organ);
        addForm.system = JSON.stringify(addForm.system);
        addRole(addForm).then((resp: any) => {
          if (resp.code === 20000) {
            Notification.success(resp.msg);
            addData.value.handleCancel();
            fetchRole();
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

  const cancelAddRole = () => {
    addData.value.handleCancel();
  };

  const edit = (module: roleRecord) => {
    roleItem.value = JSON.parse(JSON.stringify(module));
    editShow.value = true;
  };

  const editRoleBefore = (done: any) => {
    editData.value.handleSubmit().then((valid: any) => {
      if (!valid) {
        const { editForm } = JSON.parse(JSON.stringify(editData.value));
        editForm.permission = JSON.stringify(editForm.permission);
        editForm.organ = JSON.stringify(editForm.organ);
        editForm.system = JSON.stringify(editForm.system);
        updateRole(editForm).then((resp: any) => {
          if (resp.code === 20000) {
            Notification.success(resp.msg);
            fetchRole();
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

  const cancelEditRole = () => {
    editData.value.handleCancel();
  };

  const deleted = () => {
    if (idList.value.length === 0) {
      Message.warning('你没有选择任何要操作的项目，请先选择！');
    } else {
      deleteRole(idList.value).then((resp: any) => {
        if (resp.code === 20000) {
          Notification.success(resp.msg);
          fetchRole();
        } else {
          Notification.error(resp.msg);
        }
      });
    }
  };

  const getRoleName = (role: string) => {
    let roleName;
    roleSelect.forEach((item) => {
      if (item.value === role) {
        roleName = item.label;
      }
    });
    return roleName;
  };
</script>

<script lang="ts">
  export default {
    name: 'index.vue',
  };
</script>

<style scoped>
  .container {
    padding: 10px 10px 10px 10px;
  }
  :deep(.arco-card) {
    padding-top: 15px;
  }
</style>
