import { RouteLocationNormalized, RouteRecordRaw } from 'vue-router';
import { useUserStore } from '@/store';

const modules = import.meta.glob('../views/**/*.vue');
const layout = import.meta.glob('../layout/*.vue');

export default function usePermission() {
  const userStore = useUserStore();
  return {
    accessRouter(route: RouteLocationNormalized | RouteRecordRaw) {
      return (
        !route.meta?.requiresAuth ||
        //! route.meta?.roles ||
        // route.meta?.roles?.includes('*') ||
        // route.meta?.roles?.includes(userStore.role) ||
        userStore.permission.includes(route.name?.valueOf() as never)
      );
    },
    findFirstPermissionRoute(_routers: any, role = 'admin') {
      const cloneRouters = [..._routers];
      while (cloneRouters.length) {
        const firstElement = cloneRouters.shift();
        if (
          firstElement?.meta?.roles?.find((el: string[]) => {
            return el.includes('*') || el.includes(role);
          })
        )
          return { name: firstElement.name };
        if (firstElement?.children) {
          cloneRouters.push(...firstElement.children);
        }
      }
      return null;
    },
    getAsyncRoutes(_routes: RouteRecordRaw[]) {
      const res: RouteRecordRaw[] = [];
      if (_routes) {
        _routes.forEach((item: any) => {
          const newItem: RouteRecordRaw = {
            name: item.name,
            path: item.path,
            component:
              item.component === 'DEFAULT_LAYOUT'
                ? layout['../layout/default-layout.vue']
                : modules[`../views${item.component}.vue`],
            redirect: '',
            children: this.getAsyncRoutes(item.children),
            meta: {
              requiresAuth: true,
              icon: 'icon-apps',
              roles: ['*'],
            },
          };
          res.push(newItem);
        });
      }
      return res;
    },
    // You can add any rules you want
  };
}
