import { DEFAULT_LAYOUT } from '@/router/constants';

export default {
  path: '/config',
  name: 'Config',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.config',
    requiresAuth: true,
    icon: 'icon-command',
    order: 2,
  },
  children: [
    {
      path: 'role-list', // The midline path complies with SEO specifications
      name: 'RoleList',
      component: () => import('@/views/config/role-list/index.vue'),
      meta: {
        icon: 'icon-skin',
        locale: 'menu.config.roleList',
        requiresAuth: true,
      },
    },
    {
      path: 'module-list', // The midline path complies with SEO specifications
      name: 'ModuleList',
      component: () => import('@/views/config/module-list/index.vue'),
      meta: {
        icon: 'icon-codepen',
        locale: 'menu.config.moduleList',
        requiresAuth: true,
      },
    },
  ],
};
