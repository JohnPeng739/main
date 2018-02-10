import Vue from 'vue'
import Router from 'vue-router'
import LoginPage from '../pages/login.vue'
import ChangePasswordPage from '../pages/change-password.vue'
import ChangeMySetting from '../pages/change-my-setting.vue'
import {
  MxAccountManage,
  MxAccreditManage,
  MxDepartmentManage,
  MxLoginHistoryManage,
  MxOperateLogManage,
  MxPrivilegeManage,
  MxRoleManage,
  MxUserManage
} from '../../index' // '../../../dist/mx-vue-el-appcomps.min'

export const navData = [{
  path: '/home',
  icon: 'apps',
  name: 'nav.home'
}, {
  path: '/manage',
  icon: 'apps',
  name: 'nav.manage.value',
  role: 'admin',
  children: [{
    path: '/manage/user',
    icon: 'apps',
    name: 'nav.manage.user'
  }, {
    path: '/manage/account',
    icon: 'apps',
    name: 'nav.manage.account'
  }, {
    path: '/manage/role',
    icon: 'apps',
    name: 'nav.manage.role'
  }, {
    path: '/manage/privilege',
    icon: 'apps',
    name: 'nav.manage.privilege'
  }, {
    path: '/manage/department',
    icon: 'apps',
    name: 'nav.manage.department'
  }, {
    path: '/manage/accredit',
    icon: 'apps',
    name: 'nav.manage.accredit'
  }, {
    path: '/manage/logs',
    icon: 'apps',
    name: 'nav.manage.logs'
  }, {
    path: '/manage/loginHistory',
    icon: 'apps',
    name: 'nav.manage.loginHistory'
  }]
}, {
  path: '/personal',
  icon: 'apps',
  name: 'nav.manage.value',
  role: 'user',
  children: [{
    path: '/personal/changePassword',
    icon: 'apps',
    name: 'button.changePassword'
  }, {
    path: '/personal/mySetting',
    icon: 'apps',
    name: 'button.mySetting'
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
      component: ChangePasswordPage,
      meta: {needAuth: true}
    }, {
      path: '/personal/mySetting',
      component: ChangeMySetting,
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
