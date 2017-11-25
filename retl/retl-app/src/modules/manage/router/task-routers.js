export const taskRouters = [{
  path: '/tasks/list',
  component: resolve => require(['../../../pages/task/tasks-list.vue'], resolve)
}, {
  path: '/tasks/add',
  component: resolve => require(['../../../pages/task/task-operate.vue'], resolve)
}, {
  path: '/tasks/edit',
  component: resolve => require(['../../../pages/task/task-operate.vue'], resolve)
}, {
  path: '/tasks/task/:topologyId',
  component: resolve => require(['../../../pages/task/task-detail.vue'],resolve)
}]

export const taskNavData = {
  path: '/tasks',
  icon: 'access_alarm',
  title: '任务',
  role: 'all',
  children: [{
    path: '/tasks/list',
    icon: 'list',
    title: '所有任务',
    role: 'all'
  }]
}
