import {logger} from 'mx-app-utils'
import {get} from '../../../../assets/ajax'

const LOAD_TYPES = 'LOAD_TYPES'

const state = {
  types: {
    topologyTypes: [], spoutTypes: [], boltTypes: [], jdbcDrvierTypes: [], jmsTypes: [], validateTypes: [],
    validateRuleTypes: [], transformTypes: []
  }
}

export const getTypeLabel = (listName, value) => {
  let list = state.types[listName]
  if (list && list.length > 0 && value) {
    let name = ''
    list.forEach(type => {
      if (type && type.value === value) {
        name = type.label
        return
      }
    })
    return name
  }
}

const getters = {
  topologyTypes: state => state.types.topologyTypes,
  spoutTypes: state => state.types.spoutTypes,
  boltTypes: state => state.types.boltTypes,
  jdbcDriverTypes: state => state.types.jdbcDriverTypes,
  jmsTypes: state => state.types.jmsTypes,
  validateTypes: state => state.types.validateTypes,
  validateRuleTypes: state => state.types.validateRuleTypes,
  transformTypes: state => state.types.transformTypes
}

const actions = {
  loadTypes({commit, state}) {
    commit(LOAD_TYPES)
  }
}

const mutations = {
  LOAD_TYPES(state) {
    let typesUrl = '/rest/topology/supported'
    logger.debug('send GET "%s"', typesUrl)
    get(typesUrl, data => {
      logger.debug('response from "%s": %j.', typesUrl, data)
      state.types = data
    })
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}
