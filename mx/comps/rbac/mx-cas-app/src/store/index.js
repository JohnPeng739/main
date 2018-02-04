import Vue from 'vue'
import Vuex from 'vuex'
import {MxStoreAccount} from 'mx-vue-el-appcomps'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    account: MxStoreAccount
  }
})
