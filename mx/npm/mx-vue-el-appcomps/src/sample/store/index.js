import Vue from 'vue'
import Vuex from 'vuex'
import account from '../../store/account'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    account
  }
})
