import Vue from 'vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import MxVueElUtils from '../index'

import myEn from './lang/en.json'
import myZhCN from './lang/zh-CN.json'

import App from './App.vue'
import router from './router'

Vue.config.productionTip = false

MxVueElUtils.MxLocale.mergeMessages({
  en: myEn,
  'zh-CN': myZhCN
})

Vue.use(MxVueElUtils)

Vue.use(ElementUI, {
  i18n: (k, v) => MxVueElUtils.MxLocale.i18n().t(k, v)
})

console.log(MxVueElUtils.MxLocale.i18n().t('test'))

/* eslint-disable no-new */
new Vue({
  i18n: MxVueElUtils.MxLocale.i18n(),
  el: '#app',
  router,
  template: '<App/>',
  components: {App}
})
