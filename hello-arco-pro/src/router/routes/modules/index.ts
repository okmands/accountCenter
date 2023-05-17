export default {
  path: '/index',
  name: 'Index',
  component: () => import('@/views/index/index.vue'),
  meta: {
    icon: 'icon-home',
    locale: 'index',
  },
};
