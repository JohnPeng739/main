// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import Vuetify from 'vuetify'
import VueI18n from 'vue-i18n'
import 'vuetify/dist/vuetify.css'
import App from './App'
import router from './router'
import en from './assets/lang/en-US'
import zh from './assets/lang/zh-CN'

Vue.config.productionTip = false

Vue.use(Vuetify)
Vue.use(VueI18n)

let i18n = new VueI18n({
  locale: 'zh-CN',
  messages: {'en-US': en, 'zh-CN': zh}
})

/* eslint-disable no-new */
new Vue({
  i18n,
  el: '#app',
  router,
  components: { App },
  template: '<App/>'
})
