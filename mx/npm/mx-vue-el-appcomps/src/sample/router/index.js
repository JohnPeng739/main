import Vue from 'vue'
import Router from 'vue-router'
import LoginPage from '../pages/login.vue'
import {
  MxAccountManage,
  MxAccreditManage,
  MxDepartmentManage,
  MxLoginHistoryManage,
  MxOperateLogManage,
  MxPrivilegeManage,
  MxRoleManage,
  MxUserManage
} from '../../../dist/mx-vue-el-appcomps.min'

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
    path: '/login',
    component: LoginPage
  }, {
    path: '/home',
    component: resolve => require(['../layout.vue'], resolve),
    children: [{
      path: '',
      component: MxOperateLogManage
    }, {
      path: '/manage/user',
      component: MxUserManage,
      meta: {needAuth: true}
    }, {
      path: '/manage/account',
      component: MxAccountManage,
      meta: {needAuth: true}
    }, {
      path: '/manage/role',
      component: MxRoleManage,
      meta: {needAuth: true}
    }, {
      path: '/manage/privilege',
      component: MxPrivilegeManage,
      meta: {needAuth: true}
    }, {
      path: '/manage/department',
      component: MxDepartmentManage,
      meta: {needAuth: true}
    }, {
      path: '/manage/login',
      component: MxLoginHistoryManage
    }, {
      path: '/manage/logs',
      component: MxOperateLogManage
    }, {
      path: '/manage/accredit',
      component: MxAccreditManage,
      meta: {needAuth: true}
    }]
  }]
})

router.beforeEach((to, from, next) => {
  if (to.matched.some(record => record.meta.needAuth)) {
    // 需要认证
    let user = JSON.parse(sessionStorage.getItem('auth.user'))
    if (!user && to.path !== '/login') {
      next({path: '/login'})
    } else {
      next()
    }
  } else {
    // 不需要认证
    next()
  }
})

export default router
