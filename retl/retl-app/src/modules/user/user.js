import Vue from 'vue'
import AppUser from './app-user.vue'

Vue.config.productionTip = false

new Vue({
  el: '#user',
  render: h => h(AppUser)
})
