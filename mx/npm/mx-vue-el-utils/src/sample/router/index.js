import Vue from 'vue'
import Router from 'vue-router'
import TestHome from '../pages/home.vue'
import TestNotify from '../pages/test-notify.vue'
import TestTag from '../pages/test-tag.vue'
import TestPage from '../pages/test-page.vue'

export const navData = [{
  path: '/',
  icon: 'apps',
  name: '首页'
}, {
  path: '/tests',
  icon: 'apps',
  name: '测试',
  children: [{
    path: '/tests/notify',
    icon: 'apps',
    name: '提示信息'
  }, {
    path: '/tests/tag',
    icon: 'apps',
    name: '标签信息'
  }, {
    path: '/tests/page',
    icon: 'apps',
    name: '分页'
  }]
}]

Vue.use(Router)
const router = new Router({
  routes: [{
    path: '/',
    redirect: '/home'
  }, {
    path: '/home',
    component: TestHome
  }, {
    path: '/tests/notify',
    component: TestNotify
  }, {
    path: '/tests/tag',
    component: TestTag
  }, {
    path: '/tests/page',
    component: TestPage
  }, {
    path: '',
    component: TestHome
  }
  ]
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
