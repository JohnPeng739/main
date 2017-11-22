import Vue from 'vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-default/index.css'
import Mock from 'mockjs'
import router from './router'
import config from '../config'
import mocks from '../../mock'
import AppUser from './app-user.vue'

Vue.config.productionTip = false

Vue.use(ElementUI)

if (config.mock) {
  mocks.forEach(m => {
    m.forEach(item => Mock.mock(item.path, item.type, item.data))
  })
}

new Vue({
  el: '#user',
  router,
  render: h => h(AppUser)
})
