// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import 'mx-vue-el-utils/dist/css/index.min.css'
import App from './App'
import router from './router'

import Mock from 'mockjs'
import mocks from './mock'

Vue.config.productionTip = false

Vue.use(ElementUI)

if (mocks && mocks.length > 0) {
  mocks.forEach(m => {
    if (m && m.length > 0) {
      m.forEach(item => {
        if (item) {
          let {path, type, data} = item
          if (path && data) {
            if (!type || typeof type !== 'string') {
              type = 'get'
            }
            Mock.mock(path, type, data)
          }
        }
      })
    }
  })
}

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  template: '<App/>',
  components: { App }
})
