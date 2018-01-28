import Vue from 'vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import MxVueElUtils from '../../dist/mx-vue-el-utils.min'
// import MxVueElUtils from '@/index'

import App from './App'
import router from './router'
import {MyLanguage, setLanguage} from './lang'

Vue.config.productionTip = false

setLanguage('en')
Vue.use(ElementUI, {locale: MyLanguage.elLang})
Vue.use(MxVueElUtils, {locale: MyLanguage.mxLang})

console.log(MyLanguage.myI18n.t('test'))

/* eslint-disable no-new */
new Vue({
  i18n: MyLanguage.myI18n,
  el: '#app',
  router,
  template: '<App/>',
  components: { App }
})
