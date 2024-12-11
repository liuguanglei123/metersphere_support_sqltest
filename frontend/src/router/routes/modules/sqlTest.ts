import { SqlTestRouteEnum } from '@/enums/routeEnum';

import { DEFAULT_LAYOUT } from '../base';
import type { AppRouteRecordRaw } from '../types';

const SqlTest: AppRouteRecordRaw = {
  path: '/sql-test',
  name: SqlTestRouteEnum.SQL_TEST,
  redirect: '/sql-test/debug',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.sqlTest',
    collapsedLocale: 'menu.sqlTestShort',
    icon: 'icon-icon_sql_outlined1',
    order: 3,
    hideChildrenInMenu: true,
    roles: [
      'PROJECT_API_DEBUG:READ',
      'PROJECT_API_DEFINITION:READ',
      'PROJECT_API_DEFINITION_CASE:READ',
      'PROJECT_API_DEFINITION_MOCK:READ',
      'PROJECT_API_SCENARIO:READ',
      'PROJECT_API_REPORT:READ',
    ],
  },
  children: [
    {
      path: 'debug',
      name: SqlTestRouteEnum.SQL_TEST_DEBUG_MANAGEMENT,
      component: () => import('@/views/sql-test/debug/index.vue'),
      meta: {
        locale: 'menu.sqlTest.debug',
        roles: ['PROJECT_API_DEBUG:READ'],
        isTopMenu: true,
      },
    },
  ],
};

export default SqlTest;
