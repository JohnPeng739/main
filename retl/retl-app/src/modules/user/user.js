import Vue from 'vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-default/index.css'
import router from './router'
import AppUser from './app-user.vue'

Vue.config.productionTip = false

Vue.use(ElementUI)

new Vue({
  el: '#user',
  router,
  render: h => h(AppUser)
})
