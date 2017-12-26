import Vue from 'vue'
import Router from 'vue-router'
import UserManage from '@/components/rbac/user-manage.vue'
import RoleManage from '@/components/rbac/role-manage.vue'
import DepartManage from '@/components/rbac/department-manage.vue'
import PrivilegeManage from '@/components/rbac/privilege-manage.vue'
import LoginHistory from '@/components/rbac/login-history-manage.vue'
import AccreditManage from '@/components/rbac/accredit-manage.vue'
import OperateLog from '@/components/rbac/operate-log-manage.vue'

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
    component: RoleManage
  }, {
    path: '/manage/privilege',
    component: PrivilegeManage
  }, {
    path: '/manage/department',
    component: DepartManage
  }, {
    path: '/manage/logs',
    component: OperateLog
  }, {
    path: '/manage/accredit',
    component: AccreditManage
  }, {
    path: '/manage/login',
    component: LoginHistory
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
