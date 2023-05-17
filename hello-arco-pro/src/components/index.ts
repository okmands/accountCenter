import { App } from 'vue';
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { BarChart, LineChart, PieChart, RadarChart } from 'echarts/charts';
import {
  GridComponent,
  TooltipComponent,
  LegendComponent,
  DataZoomComponent,
  GraphicComponent,
} from 'echarts/components';
import Chart from './chart/index.vue';
import Breadcrumb from './breadcrumb/index.vue';
import EditAccount from '../views/accounts/account-list/components/edit.vue';
import AddAccount from '../views/accounts/account-list/components/add.vue';
import EditSystem from '../views/system/components/edit.vue';
import AddSystem from '../views/system/components/add.vue';
import AddModule from '../views/config/module-list/components/add.vue';
import EditModule from '../views/config/module-list/components/edit.vue';
import AddRole from '../views/config/role-list/components/add.vue';
import EditRole from '../views/config/role-list/components/edit.vue';

// Manually introduce ECharts modules to reduce packing size

use([
  CanvasRenderer,
  BarChart,
  LineChart,
  PieChart,
  RadarChart,
  GridComponent,
  TooltipComponent,
  LegendComponent,
  DataZoomComponent,
  GraphicComponent,
]);

export default {
  install(Vue: App) {
    Vue.component('Chart', Chart);
    Vue.component('Breadcrumb', Breadcrumb);
    Vue.component('EditAccount', EditAccount);
    Vue.component('AddAccount', AddAccount);
    Vue.component('EditSystem', EditSystem);
    Vue.component('AddSystem', AddSystem);
    Vue.component('AddModule', AddModule);
    Vue.component('EditModule', EditModule);
    Vue.component('AddRole', AddRole);
    Vue.component('EditRole', EditRole);
  },
};
