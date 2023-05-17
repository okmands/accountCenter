import { DEFAULT_LAYOUT } from '@/router/constants';

export default {
  path: '/system',
  name: 'System',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.system',
    requiresAuth: true,
    icon: 'icon-relation',
    order: 2,
  },
  children: [
    {
      path: 'system-list', // The midline path complies with SEO specifications
      name: 'SystemList',
      component: () => import('@/views/system/index.vue'),
      meta: {
        icon: 'icon-desktop',
        locale: 'menu.system.systemList',
        requiresAuth: true,
      },
    },
  ],
};
