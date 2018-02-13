import Vue from 'vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

import {changeLanguage, MxLocale} from "./locale/index"

import App from './App'
import router from './router'
import store from './store'
import mock from './mock'

Vue.config.productionTip = false

changeLanguage('zh-CN')
Vue.use(ElementUI, {
  i18n: (k, v) => MxLocale.i18n.t(k, v)
})

let mocked = false

if (process.env.NODE_ENV !== 'production' && mocked) {
  mock()
}

/* eslint-disable no-new */
new Vue({
  el: '#app',
  i18n: MxLocale.i18n,
  router,
  store,
  components: {App},
  template: '<App/>'
})
