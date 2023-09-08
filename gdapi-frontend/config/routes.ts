export default [
  { path: '/', name: '主页', icon: 'smile', component: './Index' },
  {
    path: '/interface_info/:id',
    name: '查看接口',
    icon: 'smile',
    component: './interfaceInfo',
    hideInMenu: true,
  },
  {
    path: '/user',
    layout: false,
    routes: [
      { name: '登录', path: '/user/login', component: './User/Login' },
      { name: '注册', path: '/user/register', component: './User/Register' },
    ],
  },
  {
    path: '/admin',
    name: '管理页',
    icon: 'crown',
    access: 'canAdmin',
    routes: [
      {
        name: '接口管理',
        icon: 'table',
        path: '/admin/interface_info',
        component: './Admin/InterfaceInfo',
      },
      {
        name: '接口分析',
        icon: 'analysis',
        path: '/admin/interface_analysis',
        component: './Admin/InterfaceAnalysis',
      },
    ],
  },

  { path: '*', layout: false, component: './404' },
  {
    path: '/order',
    name: '订单',
    icon: 'containerOutlined',
    component: './Order',
  },
  {
    path: '/myInterface',
    name: '我的接口',
    icon: 'appstoreOutlined',
    component: './User/MyInterface',
  },
  {
    path: '/user',
    routes: [{ name: '个人信息', path: '/user/info', component: './User/Info' }],
  },
];
