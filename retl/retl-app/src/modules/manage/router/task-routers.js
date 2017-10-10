export const taskRouters = [{
  path: '/tasks/list',
  component: resolve => require(['../../../pages/task/tasks-list.vue'], resolve)
}, {
  path: '/tasks/add',
  component: resolve => require(['../../../pages/task/task-add.vue'], resolve)
}]

export const taskNavData = {
  path: '/tasks',
  icon: 'access_alarm',
  title: '任务',
  role: 'all',
  children: [{
    path: '/tasks/add',
    icon: 'fiber_new',
    title: '添加任务...',
    role: 'manager'
  }, {
    path: '/tasks/list',
    icon: 'list',
    title: '所有任务',
    role: 'all'
  }]
}
