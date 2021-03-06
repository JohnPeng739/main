import Vue from 'vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

import {changeLanguage, MxLocale} from "./locale/index"

import App from './App'
import router from './router'
import store from './store'
import mock from './mock'

Vue.config.productionTip = false

let lang = 'en'
if (window && window.localStorage) {
  let l = window.localStorage.getItem('locale')
  if (l && typeof l === 'string' && l.length > 0) {
    lang = l
  }
}

changeLanguage(lang)
Vue.use(ElementUI, {
  i18n: (k, v) => MxLocale.i18n.t(k, v)
})

let mocked = true

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
