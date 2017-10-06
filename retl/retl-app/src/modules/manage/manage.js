import Vue from 'vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-default/index.css'
import VueMarkDown from 'vue-markdown'
import Mock from 'mockjs'
import config from './config'
import router from './router'
import store from './store'
import AppManage from './app-manage.vue'
import mocks from '../../mock'

Vue.config.productionTip = false

Vue.use(ElementUI)
Vue.use(VueMarkDown)

if (process.env.NODE_ENV === 'development' && config.mock) {
  mocks.forEach(m => {
    m.forEach(item => Mock.mock(item.path, item.type, item.data))
  })
}

new Vue({
  el: '#manage',
  router,
  store,
  render: h => h(AppManage)
})
