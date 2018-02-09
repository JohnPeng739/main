import Vue from 'vue'
import Router from 'vue-router'
import TestHome from '../pages/home.vue'
import TestNotify from '../pages/test-notify.vue'
import TestTag from '../pages/test-tag.vue'
import TestPage from '../pages/test-page.vue'
import TestEcharts from '../pages/test-echarts.vue'
import TestDialog from '../pages/test-dialog.vue'
import TestChoose from '../pages/test-choose.vue'
import TestWebsocket from '../pages/test-ws.vue'

export const navData = [{
  path: '/',
  icon: 'apps',
  name: '首页'
}, {
  path: '/tests',
  icon: 'apps',
  name: '测试',
  role: 'admin',
  children: [{
    path: '/tests/notify',
    icon: 'apps',
    name: '提示信息',
    role: 'admin'
  }, {
    path: '/tests/tag',
    icon: 'apps',
    name: '标签信息',
    role: 'admin'
  }, {
    path: '/tests/page',
    icon: 'apps',
    name: '分页'
  }, {
    path: '/tests/echarts',
    icon: 'apps',
    name: 'Echarts'
  }, {
    path: '/tests/dialog',
    icon: 'apps',
    name: '对话框'
  }, {
    path: '/tests/chooseInput',
    icon: 'apps',
    name: '可选择输入框'
  }, {
    path: '/tests/websocket',
    icon: 'apps',
    name: 'WebSocket'
  }]
}]

Vue.use(Router)
const router = new Router({
  routes: [{
    path: '/',
    redirect: '/home'
  }, {
    path: '',
    component: resolve => require(['../layout.vue'], resolve),
    children: [{
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
      path: '/tests/echarts',
      component: TestEcharts
    }, {
      path: '/tests/dialog',
      component: TestDialog
    }, {
      path: '/tests/chooseInput',
      component: TestChoose
    }, {
      path: '/tests/websocket',
      component: TestWebsocket
    }, {
      path: '',
      component: TestHome
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
