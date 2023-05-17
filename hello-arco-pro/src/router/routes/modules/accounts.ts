import { DEFAULT_LAYOUT } from '@/router/constants';

export default {
  path: '/accounts',
  name: 'Accounts',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.accounts',
    requiresAuth: true,
    icon: 'icon-common',
    order: 2,
  },
  children: [
    {
      path: 'account-list', // The midline path complies with SEO specifications
      name: 'AccountList',
      component: () => import('@/views/accounts/account-list/index.vue'),
      meta: {
        icon: 'icon-list',
        locale: 'menu.accounts.accountList',
        requiresAuth: true,
      },
    },
    {
      path: 'account-log', // The midline path complies with SEO specifications
      name: 'AccountLog',
      component: () => import('@/views/accounts/account-log/index.vue'),
      meta: {
        icon: 'icon-drive-file',
        locale: 'menu.accounts.accountLog',
        requiresAuth: true,
      },
    },
  ],
};
