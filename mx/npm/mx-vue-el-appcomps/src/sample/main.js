import Vue from 'vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import {MxLocale, changeLanguage} from './lang'

import App from './App'
import router from './router'
import store from './store'

import mock from './mock'

Vue.config.productionTip = false

changeLanguage('en')
Vue.use(ElementUI, {
  i18n: (k, v) => MxLocale.i18n.t(k, v)
})

mock()

console.log(MxLocale.i18n.t('test'))

/* eslint-disable no-new */
new Vue({
  i18n: MxLocale.i18n,
  el: '#app',
  router,
  store,
  template: '<App/>',
  components: {App}
})
