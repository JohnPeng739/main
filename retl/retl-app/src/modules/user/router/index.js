import Vue from 'vue'
import Router from 'vue-router'

const summaryNavData = {
  path: '/summary',
  icon: 'home',
  title: '总览',
  role: 'all'
}

const tasksNavData = {
  path: '/tasks/list',
  icon: '',
  title: '计算任务列表',
  role: 'all'
}

export const navData = {summaryNavData, tasksNavData}

Vue.use(Router)
const router = new Router({
  routes: [{
    path: '/',
    redirect: '/summary'
  }, {
    path: '/summary',
    component: resolve => require(['../../../pages/summary.vue'], resolve)
  }, {
    path: '/tasks/list',
    component: resolve => require(['../../../pages/task/tasks-list-by-users.vue'], resolve)
  }, {
    path: '/tasks/task/:topologyId',
    component: resolve => require(['../../../pages/task/task-detail.vue'],resolve)
  }, {
    path: '/retl-statistic',
    component: resolve => require(['../../../pages/st-retl-statistic.vue'], resolve)
  }]
})
export default router
