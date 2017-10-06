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
    icon: 'format_align_left',
    title: '添加...',
    role: 'manager'
  }, {
    path: '/tasks/list',
    icon: 'access_alarm',
    title: '所有任务',
    role: 'all'
  }]
}
