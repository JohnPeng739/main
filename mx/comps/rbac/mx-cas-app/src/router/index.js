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
} from 'mx-vue-el-appcomps'

export const navData = [{
  path: '/home',
  icon: 'home',
  name: 'nav.home'
}, {
  path: '/manage',
  icon: 'view_module',
  name: 'nav.manage.value',
  role: 'admin',
  children: [{
    path: '/manage/user',
    icon: 'person',
    name: 'nav.manage.user'
  }, {
    path: '/manage/account',
    icon: 'account_box',
    name: 'nav.manage.account'
  }, {
    path: '/manage/role',
    icon: 'group',
    name: 'nav.manage.role'
  }, {
    path: '/manage/privilege',
    icon: 'folder_special',
    name: 'nav.manage.privilege'
  }, {
    path: '/manage/department',
    icon: 'group_work',
    name: 'nav.manage.department'
  }, {
    path: '/manage/accredit',
    icon: 'folder_shared',
    name: 'nav.manage.accredit'
  }, {
    path: '/manage/logs',
    icon: 'history',
    name: 'nav.manage.logs'
  }, {
    path: '/manage/loginHistory',
    icon: 'access_time',
    name: 'nav.manage.loginHistory'
  }]
}, {
  path: '/personal',
  icon: 'favorite',
  name: 'nav.personal',
  role: 'user',
  children: [{
    path: '/personal/changePassword',
    icon: 'lock',
    name: 'changePassword'
  }, {
    path: '/personal/mySetting',
    icon: 'settings',
    name: 'mySetting'
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
    meta: {needAuth: true},
    children: [{
      path: '',
      component: MxOperateLogManage,
      meta: {needAuth: true}
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
      path: '/manage/loginHistory',
      component: MxLoginHistoryManage
    }, {
      path: '/manage/logs',
      component: MxOperateLogManage
    }, {
      path: '/manage/accredit',
      component: MxAccreditManage,
      meta: {needAuth: true}
    }, {
      path: '/personal/changePassword',
      // TODO
      component: MxAccreditManage,
      meta: {needAuth: true}
    }, {
      path: '/personal/mySetting',
      component: MxAccreditManage,
      meta: {needAuth: true}
    }]
  }]
})

router.beforeEach((to, from, next) => {
  let user = JSON.parse(sessionStorage.getItem('auth.user'))
  if (user) {
    router.app.$options.store.dispatch('setLoginUser', user)
  }
  if (to.matched.some(record => record.meta.needAuth)) {
    // 需要认证
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
