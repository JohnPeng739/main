import Vue from 'vue'
import VueI18n from 'vue-i18n'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import {locale} from 'mx-vue-el-utils'
import App from './App'
import router from './router'

import Mock from 'mockjs'
import mocks from './mock'

import en from '@/assets/lang/en'
import zhCN from '@/assets/lang/zhCN'

Vue.config.productionTip = false

Vue.use(VueI18n)
locale.setLanguage('zhCN', {en, zhCN})
Vue.use(ElementUI, {locale: locale.elLocale})

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
  i18n: locale.i18n,
  el: '#app',
  router,
  template: '<App/>',
  components: { App }
})
