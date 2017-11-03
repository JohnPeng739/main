export const manageRouters = [{
  path: '/manage/logs',
  component: resolve => require(['../../../pages/manage/logs-list.vue'], resolve)
}, {
  path: '/manage/users',
  component: resolve => require(['../../../pages/manage/users-list.vue'], resolve)
}, {
  path: '/manage/local',
  component: resolve => require(['../../../pages/manage/local-server-config.vue'], resolve)
}, {
  path: '/manage/servers',
  component: resolve => require(['../../../pages/manage/servers.vue'], resolve)
}]

export const manageNavData = {
  path: '/manage',
  icon: 'business_center',
  title: '管理',
  role: 'manager',
  children: [{
    path: '/manage/logs',
    icon: 'assignment',
    title: '操作日志',
    role: 'manager',
  }, {
    path: '/manage/users',
    icon: 'supervisor_account',
    title: '用户列表',
    role: 'manager',
  }, {
    path: '/manage/local',
    icon: 'computer',
    title: '本机设置',
    role: 'manager',
  }, {
    path: '/manage/servers',
    icon: 'domain',
    title: '服务器集群',
    role: 'manager',
  }]
}
