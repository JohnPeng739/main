import Vue from 'vue'
import Vuex from 'vuex'
import {MxStoreAccount} from '../../../dist/mx-vue-el-appcomps.min'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    account: MxStoreAccount
  }
})
