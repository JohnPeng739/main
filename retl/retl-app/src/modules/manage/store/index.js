import Vue from 'vue'
import Vuex from 'vuex'
import account from './modules/account'
import breadcrumb from './modules/breadcrumb'
import topology from './modules/topology'

Vue.use(Vuex)

const store = new Vuex.Store({
  modules: {
    account,
    breadcrumb,
    topology
  }
})

export default store
