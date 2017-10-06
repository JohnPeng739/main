import Vue from 'vue'
import Router from 'vue-router'
import {taskNavData, taskRouters} from './task-routers'

const summaryNavData = {
  path: '/summary',
  icon: 'home',
  title: '总览',
  role: 'all'
}

export const navData = {summaryNavData, taskNavData}

let findNavNode = (list, path) => {
  let found = null
  for (let index = 0; index < list.length; index++) {
    if (list[index].path === path) {
      found = list[index]
      break
    } else if (list[index].children !== undefined) {
      found = findNavNode(list[index].children, path)
      if (found !== null) {
        break
      }
    }
  }
  return found
}

export function getNavTitle(path) {
  let found = findNavNode(navData, path)
  return found == null ? '' : found.title
}

export function getNavIcon(path) {
  let found = findNavNode(navData, path)
  return found == null ? '' : found.icon
}

export function getNavRole(path) {
  let found = findNavNode(navData, path)
  return found == null ? '' : found.role
}


Vue.use(Router)
const router = new Router({
  routes: [{
    path: '/',
    redirect: '/summary'
  }, {
    path: '/login',
    component: resolve => require(['../../../pages/login.vue'], resolve)
  }, {
    path: '/summary',
    component: resolve => require(['../view/index.vue'], resolve),
    children: [
      ...taskRouters,
      {
        path: '',
        component: resolve => require(['../../../pages/summary.vue'], resolve)
      }]
  }]
})

router.beforeEach((to, from, next) => {
  if (to.matched.some(record => record.meta.donotNeedAuth)) {
    // 不需要认证
    next()
  } else {
    // 需要认证
    let user = JSON.parse(sessionStorage.getItem('auth.user'))
    if (!user && to.path !== '/login') {
      next({path: '/login'})
    } else {
      next()
    }
  }
})

export default router
