import Vue from 'vue'
import Router from 'vue-router'

import LoginPage from '@/pages/Login'
import AboutPage from '@/pages/About'
import HelloWorld from '@/components/HelloWorld'

export const navData = [{
  path: '/index',
  icon: 'contacts',
  text: 'nav.home'
}, {
  icon: 'keyboard_arrow_up',
  'icon-alt': 'keyboard_arrow_down',
  text: 'nav.dataManage',
  model: true,
  children: [{
    path: '/login',
    icon: 'add',
    text: 'nav.personList'
  }, {
    path: '/about',
    icon: 'add',
    text: 'nav.accountList'
  }, {
    path: '/hello',
    icon: 'add',
    text: 'nav.roleList'
  }, {
    icon: 'add',
    text: 'nav.privilegeList'
  }, {
    icon: 'add',
    text: 'nav.accreditList'
  }]
}, {
  icon: 'keyboard_arrow_up',
  'icon-alt': 'keyboard_arrow_down',
  text: 'nav.authManage',
  model: false,
  children: [{
    path: '/logs',
    icon: 'add',
    text: 'nav.logsList'
  }, {
    icon: 'notifications',
    text: 'nav.loginHistories'
  }]
}]

function findNav(list, found) {
  if (list && list instanceof Array && list.length > 0) {
    for (let index = 0; index < list.length; index++) {
      let item = list[index]
      if (item.path === found.path) {
        found.nav = item
      }
      if (found.nav === undefined || found.nav === null) {
        findNav(item.children, found)
      }
    }
  }
}

export function getNavName(path) {
  let found = {path, nav: undefined}
  findNav(navData, found)
  return found.nav ? found.nav.text : 'na'
}

Vue.use(Router)
const router = new Router({
  routes: [{
    path: '/',
    redirect: '/index'
  }, {
    path: '/login',
    component: LoginPage
  }, {
    path: '/index',
    component: resolve => require(['../components/Layout.vue'], resolve),
    children: [{
      path: '',
      component: AboutPage
    }, {
      path: '/about',
      component: AboutPage
    }, {
      path: '/hello',
      component: HelloWorld,
      meta: {
        needAuth: true
      }
    }, {
      path: '/logs',
      component: HelloWorld
    }]
  }]
})

router.beforeEach((to, from, next) => {
  if (to.matched.some(item => item.meta.needAuth)) {
    // 需要认证
    let token = localStorage.getItem('token')
    if (!token && typeof token !== 'string' && to.path !== '/login') {
      console.log('redirect to login.')
      next({
        path: '/login?originTo=' + to.path
      })
    } else {
      // 已经认证过了
      next()
    }
  } else {
    // 不需要认证
    next()
  }
})

export default router
