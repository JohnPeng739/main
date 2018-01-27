import Vue from 'vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import MxVueElUtils from 'mx-vue-el-utils'
import MxAppComps from '../../dist/mx-vue-el-appcomps.min'

import App from './App'
import router from './router'
import store from './store'
import { MyLanguage, setLanguage } from './lang'

import mock from './mock'

Vue.config.productionTip = false

setLanguage('zh-CN')
Vue.use(ElementUI, {locale: MyLanguage.elLang})
Vue.use(MxVueElUtils, {locale: MyLanguage.mxLang})
Vue.use(MxAppComps, {locale: MyLanguage.appLang})

mock()

console.log(MyLanguage.myI18n.t('test'))

/* eslint-disable no-new */
new Vue({
  i18n: MyLanguage.myI18n,
  el: '#app',
  router,
  store,
  template: '<App/>',
  components: {App}
})
