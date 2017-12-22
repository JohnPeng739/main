import Vue from 'vue'
import Router from 'vue-router'
import UserManage from '@/components/rbac/user-manage.vue'

UserManage.tableMaxHeight = 500

export const navData = [{
  path: '/',
  icon: 'apps',
  name: '首页'
}, {
  path: '/manage',
  icon: 'apps',
  name: '管理',
  children: [{
    path: '/manage/user',
    icon: 'apps',
    name: '用户管理'
  }, {
    path: '/manage/account',
    icon: 'apps',
    name: '账户管理'
  }, {
    path: '/manage/role',
    icon: 'apps',
    name: '角色管理'
  }, {
    path: '/manage/privilege',
    icon: 'apps',
    name: '特权管理'
  }, {
    path: '/manage/department',
    icon: 'apps',
    name: '部门管理'
  }, {
    path: '/manage/accredit',
    icon: 'apps',
    name: '授权管理'
  }, {
    path: '/manage/logs',
    icon: 'apps',
    name: '日志管理'
  }, {
    path: '/manage/login',
    icon: 'apps',
    name: '登陆历史管理'
  }]
}]

Vue.use(Router)
const router = new Router({
  routes: [{
    path: '/',
    redirect: '/home'
  }, {
    path: '/home',
    redirect: '/manage/user'
  }, {
    path: '/manage/user',
    component: UserManage
  }, {
    path: '/manage/account',
    component: UserManage
  }, {
    path: '/manage/role',
    component: UserManage
  }, {
    path: '/manage/privilege',
    component: UserManage
  }, {
    path: '/manage/department',
    component: UserManage
  }, {
    path: '/manage/accredit',
    component: UserManage
  }, {
    path: '/manage/login',
    component: UserManage
  }]
})

/*
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
*/
export default router
