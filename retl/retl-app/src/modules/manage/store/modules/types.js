import {logger} from 'dsutils'
import {get} from '../../../../assets/ajax'

const LOAD_TYPES = 'LOAD_TYPES'

const state = {
  types: {}
}

const getters = {
  topologyTypes: state => state.types.topologyTypes,
  spoutTypes: state => state.types.spoutTypes,
  boltTypes: state => state.types.boltTypes,
  jdbcDriverTypes: state => state.types.jdbcDriverTypes
}

const actions = {
  loadTypes({commit, state}) {
    commit(LOAD_TYPES)
  }
}

const mutations = {
  LOAD_TYPES(state) {
    let url = '/rest/topology/types'
    logger.debug('send GET "%s"', url)
    get(url, data => {
      state.types = data
      console.log(state.types)
    })
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}
