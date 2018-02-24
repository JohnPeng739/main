import Vue from 'vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import {MxLocale, changeLanguage} from './lang/index'

import App from './App'
import router from './router'

Vue.config.productionTip = false

changeLanguage('zh-CN')

Vue.use(ElementUI, {
  i18n: (k, v) => MxLocale.i18n.t(k, v)
})

console.log(MxLocale.i18n.t('test'))

/* eslint-disable no-new */
new Vue({
  i18n: MxLocale.i18n,
  el: '#app',
  router,
  template: '<App/>',
  components: {App}
})
