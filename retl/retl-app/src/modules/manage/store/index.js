import Vue from 'vue'
import Vuex from 'vuex'
import account from './modules/account'
import breadcrumb from './modules/breadcrumb'

Vue.use(Vuex)

const store = new Vuex.Store({
  modules: {
    account,
    breadcrumb
  }
})

export default store
