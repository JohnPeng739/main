import Vue from 'vue'
import Router from 'vue-router'
import Home from '@/sample/pages/home'
import TestNotify from '@/sample/pages/test-notify'

Vue.use(Router)

export default new Router({
  routes: [{
    path: '/',
    name: 'Home',
    component: Home
  }, {
    path: '/notify',
    name: 'TestNotify',
    component: TestNotify
  }]
})
