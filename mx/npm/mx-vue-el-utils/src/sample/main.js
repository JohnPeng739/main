import Vue from 'vue'
import VueI18n from 'vue-i18n'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import App from './App'
import router from './router'
import {locale} from '../../dist/mx-vue-el-utils.min'

Vue.config.productionTip = false

Vue.use(VueI18n)

locale.setLanguage('en')

Vue.use(ElementUI, {locale: locale.elLocale})

/* eslint-disable no-new */
new Vue({
  i18n: locale.i18n,
  el: '#app',
  router,
  template: '<App/>',
  components: { App }
})
