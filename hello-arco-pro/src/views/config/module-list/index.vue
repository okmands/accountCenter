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
              @click="add"
            >
              <template #icon>
                <icon-plus />
              </template>
              {{ '新增模块' }}
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
              {{ '删除模块' }}
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
          <a-table-column title="模块标签" data-index="title" align="center" />
          <a-table-column title="模块名称" data-index="name" align="center" />
          <a-table-column title="模块链接" data-index="link" align="center" />
          <a-table-column title="排序" data-index="order" align="center" />
          <a-table-column title="操作" align="center" :width="200">
            <template #cell="{ record }">
              <a-button
                v-permission="['admin']"
                type="text"
                size="mini"
                @click="edit(record)"
              >
                <icon-edit />
                编辑模块
              </a-button>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-card>
    <a-modal
      v-model:visible="editShow"
      :fullscreen="false"
      title="编辑模块"
      :on-before-ok="editModuleBefore"
      @cancel="cancelEditModule"
    >
      <EditModule
        ref="editData"
        :module="moduleItem"
        :module-tree="moduleTree"
      />
    </a-modal>
    <a-modal
      v-model:visible="addShow"
      :fullscreen="false"
      title="新增模块"
      :on-before-ok="addModuleBefore"
      @cancel="cancelAddModule"
    >
      <AddModule ref="addData" :module-tree="moduleTree" />
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
  import {
    getModule,
    moduleRecord,
    moduleParams,
    addModule,
    deleteModule,
    updateModule,
  } from '@/api/module';
  import { reactive, ref } from 'vue';
  import { Pagination } from '@/types/global';
  import useLoading from '@/hooks/loading';
  import { Message, Notification } from '@arco-design/web-vue';

  const addShow = ref(false);

  const editShow = ref(false);

  const { loading, setLoading } = useLoading(true);

  const addData = ref<any>(null);

  const editData = ref<any>(null);

  const moduleTree = ref<any>(null);

  const moduleItem = ref<moduleRecord>();

  const renderData = ref<moduleRecord[]>([]);

  const basePagination: Pagination = {
    current: 1,
    pageSize: 20,
  };

  const pagination = reactive({
    ...basePagination,
  });

  const fetchModule = async (
    params: moduleParams = { current: 1, pageSize: 20 }
  ) => {
    setLoading(true);
    try {
      const { data } = await getModule(params);
      renderData.value = data.list;
      pagination.current = params.current;
      pagination.total = data.total;
    } catch (e) {
      console.log(e);
    } finally {
      setLoading(false);
    }
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
    fetchModule({
      ...basePagination,
      current,
    } as unknown as moduleParams);
  };

  fetchModule({
    ...basePagination,
  });

  const add = () => {
    moduleTree.value = [];
    renderData.value.forEach((item: moduleRecord) => {
      moduleTree.value.push({
        key: item.guid,
        title: item.title,
        children: item.children ? getChildrenTree(item.children) : [],
      });
    });
    addShow.value = true;
  };

  const addModuleBefore = (done: any) => {
    addData.value.handleSubmit().then((valid: any) => {
      if (!valid) {
        const { addForm } = addData.value;
        addModule(addForm).then((resp: any) => {
          if (resp.code === 20000) {
            Notification.success(resp.msg);
            addData.value.handleCancel();
            fetchModule();
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

  const cancelAddModule = () => {
    addData.value.handleCancel();
  };

  const edit = (module: moduleRecord) => {
    moduleTree.value = [];
    renderData.value.forEach((item: moduleRecord) => {
      moduleTree.value.push({
        key: item.guid,
        title: item.title,
        children: item.children ? getChildrenTree(item.children) : [],
      });
    });
    moduleItem.value = module;
    editShow.value = true;
  };

  const editModuleBefore = (done: any) => {
    editData.value.handleSubmit().then((valid: any) => {
      if (!valid) {
        const { editForm } = editData.value;
        updateModule(editForm).then((resp: any) => {
          if (resp.code === 20000) {
            Notification.success(resp.msg);
            fetchModule();
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

  const cancelEditModule = () => {
    editData.value.handleCancel();
  };

  const deleted = () => {
    if (idList.value.length === 0) {
      Message.warning('你没有选择任何要操作的项目，请先选择！');
    } else {
      deleteModule(idList.value).then((resp: any) => {
        if (resp.code === 20000) {
          Notification.success(resp.msg);
          fetchModule();
        } else {
          Notification.error(resp.msg);
        }
      });
    }
  };

  const getChildrenTree = (children: moduleRecord[]) => {
    const tempTree = ref([] as any);
    children.forEach((item: moduleRecord) => {
      tempTree.value.push({
        key: item.guid,
        title: item.title,
        children: item.children ? getChildrenTree(item.children) : [],
      });
    });
    return tempTree;
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
