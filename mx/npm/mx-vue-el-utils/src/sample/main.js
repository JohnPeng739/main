import Vue from 'vue'
import VueI18n from 'vue-i18n'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import App from './App'
import router from './router'
import locale from '@/assets/lang'
// import {locale} from '../../dist/mx-vue-el-utils.min'
import en from './lang/en'
import zhCN from './lang/zhCN'

Vue.config.productionTip = false

Vue.use(VueI18n)

let messages = {en: en, zhCN: zhCN}
locale.setLanguage('zhCN', messages)

Vue.use(ElementUI, {locale: locale.elLocale})

console.log(locale.i18n.t('message.tag.existed', {tag: 'abcdefg'}))
console.log(locale.i18n.t('test'))

/* eslint-disable no-new */
new Vue({
  i18n: locale.i18n,
  el: '#app',
  router,
  template: '<App/>',
  components: { App }
})
