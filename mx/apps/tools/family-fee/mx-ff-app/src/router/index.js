import Vue from 'vue'
import Router from 'vue-router'
import LoginPage from '../pages/login.vue'
import SignUpPage from '../pages/sign-up.vue'

import FamilyPage from '../pages/user/family.vue'
// import AboutPage from '../pages/about.vue'

const navData = [{
  path: '/home',
  icon: 'home',
  name: 'nav.home'
}, {
  path: '/user',
  icon: 'home',
  name: 'nav.user',
  children: [{
    path: '/user/family',
    icon: 'home',
    name: 'nav.user.family'
  }, {
    path: '/user/income',
    icon: 'home',
    name: 'nav.user.income'
  }, {
    path: '/user/spend',
    icon: 'home',
    name: 'nav.user.spend'
  }, {
    path: '/user/course',
    icon: 'home',
    name: 'nav.user.course'
  }, {
    path: '/user/budget',
    icon: 'home',
    name: 'nav.user.budget'
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
    path: '/signUp',
    component: SignUpPage
  }, {
    path: '/home',
    component: resolve => require(['../layout.vue'], resolve),
    meta: {needAuth: true},
    children: [{
      path: '',
      redirect: '/user/family'
    }, {
      path: '/user/family',
      component: FamilyPage
    }, {
      path: '/user/income'
    }, {
      path: '/user/spend'
    }, {
      path: '/user/course'
    }, {
      path: '/user/budget'
    }]
  }]
})

// TODO 添加新的路由和菜单项

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

export { navData }
