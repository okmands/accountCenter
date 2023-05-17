<!--suppress ALL -->
<template>
  <div class="layoutDiv" :style="{ height: `${layoutDivHeight}px` }">
    <a-layout>
      <a-layout-header :style="{ paddingBottom: '50px' }">
        <a-row>
          <a-col :span="5">
            <a-card
              :style="{
                width: '300px',
                height: '120px',
                borderRadius: '10px',
              }"
            >
              <template #title>
                <a-typography-text style="font-weight: 500">{{
                  userProperty.organ
                }}</a-typography-text>
                <a-tag style="float: right" color="#C5224C">{{
                  userProperty.post
                }}</a-tag>
              </template>
              <a-card-meta style="margin-left: 2%">
                <template #description>
                  <a-typography-text bold>{{
                    userProperty.workid
                  }}</a-typography-text>
                  <br />
                  <a-typography-text bold>{{
                    userProperty.name
                  }}</a-typography-text>
                </template>
              </a-card-meta>
            </a-card>
          </a-col>
          <a-col :span="12">
            <div id="he-plugin-standard"></div>
          </a-col>
        </a-row>
      </a-layout-header>
      <a-layout-content class="layoutContent">
        <a-row>
          <a-col
            v-for="item in userProperty.authSystemList"
            :key="item"
            :span="3"
          >
            <a
              :href="item.loginUrl"
              target="_blank"
              style="text-decoration: none"
            >
              <a-card hoverable :style="{ width: '185px' }">
                <template #cover>
                  <div
                    :style="{
                      height: '110px',
                      padding: '5px',
                    }"
                  >
                    <img
                      :style="{ width: '100%', height: '100%' }"
                      alt="dessert"
                      src="/src/assets/images/XiYaLogo.png"
                    />
                  </div>
                </template>
                <a-card-meta
                  :title="item.systemName"
                  :style="{ padding: '0px' }"
                >
                  <template #description>
                    <icon-wifi
                      :size="24"
                      :style="{
                        color: '#00b42a',
                        position: 'absolute',
                        bottom: '8%',
                        right: '5%',
                      }"
                    />
                  </template>
                </a-card-meta>
              </a-card>
            </a>
          </a-col>
        </a-row>
      </a-layout-content>
      <a-layout-footer>
        <a-space direction="vertical" fill>
          <a-row>
            <a-col :span="2">
              <a-tag color="#00b42a" size="large">考勤机状态</a-tag>
            </a-col>
            <!--            <a-col v-for="item in systemCardList" :key="item" :span="1">-->
            <!--              <a-tooltip content="延迟200毫秒">-->
            <!--                <a-button-->
            <!--                  ><icon-wifi-->
            <!--                    :size="16"-->
            <!--                    :style="{ color: '#00b42a' }"-->
            <!--                  />潢川丽宝#1</a-button-->
            <!--                >-->
            <!--              </a-tooltip>-->
            <!--            </a-col>-->
          </a-row>
        </a-space>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<script lang="ts" setup>
  import { onMounted, ref } from 'vue';
  import { getUserProperty, userPropertyRecord } from '@/api/index';

  const userProperty = ref<userPropertyRecord>({
    workId: '',
    name: '',
    organ: '',
    post: '',
    authSystemList: [],
  });

  const layoutDivHeight = ref();

  const getInit = () => {
    layoutDivHeight.value = window.innerHeight;
  };

  onMounted(() => {
    window.addEventListener('resize', getInit);
    window.WIDGET = {
      CONFIG: {
        layout: '1',
        width: 450,
        height: 120,
        background: '1',
        dataColor: 'FFFFFF',
        borderRadius: '10',
        key: '4ac3475f4b5841558d98ceb106275681',
      },
    };
    const script = document.createElement('script');
    script.type = 'text/javascript';
    script.src =
      'https://widget.qweather.net/standard/static/js/he-standard-common.js?v=2.0';
    document.getElementsByTagName('head')[0].appendChild(script);
  });

  const fetchUserProperty = async () => {
    const { data } = await getUserProperty();
    userProperty.value = data;
  };

  fetchUserProperty();
  getInit();
</script>

<script lang="ts">
  export default {
    name: 'Index',
  };
</script>

<style scoped>
  ::v-deep #he-plugin-standard .wv-lt-refresh {
    display: none;
  }
  .layoutDiv {
    padding: 2% 5%;
    background: url('../src/assets/images/0975.jpg') no-repeat;
    background-size: cover;
  }
  .layoutContent {
    min-height: 600px;
  }
  ::v-deep .arco-card-body {
    padding: 10px 10px;
  }
</style>
