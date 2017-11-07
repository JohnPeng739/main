import {logger} from 'dsutils'

const CACHE = 'CACHE'
const SETDATA = 'SETDATA'

const TOPOLOGY='current-topology'

export const topologySample = function () {
  return {
    id: null,
    name: 'SampleTopology',
    description: '',
    type: 'retl',
    debug: false,
    messageTimeoutSecs: 3,
    maxSpoutPending: 1,
    tarDestinateNames: ['tar-sample'],
    tarIsTopic: false,
    zookeepers: [],
    jdbcDataSources: [],
    jmsDataSources: [],
    caches: [],
    spouts: [],
    columns: [],
    validates: {},
    transforms: {},
    persist: {}
  }
}

const state = {
  topology: topologySample
}

const getters = {
  topology: state => state.topology,
  zookeepers: state => state.topology.zookeepers,
  jdbcDataSources: state => state.topology.jdbcDataSources,
  jmsDataSources: state => state.topology.jmsDataSources,
  caches: state => state.topology.caches,
  spouts: state => state.topology.spouts,
  columns: state => state.topology.columns,
  validates: state => state.topology.validates,
  transforms: state => state.topology.transforms,
  persist: state => state.topology.persist
}

const actions = {
  cacheSave({commit, state}) {
    commit(CACHE, 'save')
  },
  cacheLoad({commit, state}) {
    commit(CACHE, 'load')
  },
  cacheClean({commit, state}) {
    commit(CACHE, 'clean')
    commit(CACHE, 'load')
  },
  setTopology({commit, state}, topology) {
    commit(SETDATA, {operate: 'topology', data: topology})
    commit(CACHE, 'save')
  },
  setBaseInfo({commit, state}, baseInfo) {
    commit(SETDATA, {operate: 'baseInfo', data: baseInfo})
  },
  setZookeepers({commit, state}, zookeepers) {
    commit(SETDATA, {operate: 'zookeepers', data: zookeepers})
  },
  setJdbcDataSources({commit, state}, dataSources) {
    commit(SETDATA, {operate: 'jdbcDataSources', data: dataSources})
  },
  setJmsDataSources({commit, state}, dataSources) {
    commit(SETDATA, {operate: 'jmsDataSources', data: dataSources})
  },
  setCaches({commit, state}, caches) {
    commit(SETDATA, {operate: 'caches', data: caches})
  },
  setSpouts({commit, state}, spouts) {
    commit(SETDATA, {operate: 'spouts', data: spouts})
  },
  setColumns({commit, state}, columns) {
    commit(SETDATA, {operate: 'columns', data: columns})
  },
  setValidates({commit, state}, validates) {
    commit(SETDATA, {operate: 'validates', data: validates})
  },
  setTransforms({commit, state}, transforms) {
    commit(SETDATA, {operate: 'transforms', data: transforms})
  },
  setPersist({commit, state}, persist) {
    commit(SETDATA, {operate: 'persist', data: persist})
  }
}

const mutations = {
  CACHE(state, operate) {
    switch (operate) {
      case 'save':
        localStorage.setItem(TOPOLOGY, JSON.stringify(state.topology))
        logger.debug('Cache topology into local storage success, topology: %j.', state.topology)
        break
      case 'clean':
        state.topology = {}
        localStorage.removeItem(TOPOLOGY)
        logger.debug('Clean topology success.')
      case 'load':
        let str = localStorage.getItem(TOPOLOGY)
        if (str && str.length > 0) {
          state.topology = JSON.parse(str)
          logger.debug('Load topology from local storage success, topology: %s.', str)
        } else {
          state.topology = topologySample()
          logger.debug('Init topology success, topology: %j.', state.topology)
        }
        break
      default:
        break
    }
  },
  SETDATA(state, {operate, data}) {
    switch (operate) {
      case 'topology':
        state.topology = data
        break
      case 'baseInfo':
        let {name, type, description, debug, messageTimeoutSecs, maxSpoutPending, tarDestinateNames, tarIsTopic} = data
        let topology = state.topology
        topology.name = name
        topology.description = description
        topology.type = type
        topology.debug = debug
        topology.messageTimeoutSecs = messageTimeoutSecs
        topology.maxSpoutPending = maxSpoutPending
        topology.tarDestinateNames = tarDestinateNames
        topology.tarIsTopic = tarIsTopic
        state.topology = topology
        break
      case 'zookeepers':
        state.topology.zookeepers = data
        break
      case 'jdbcDataSources':
        state.topology.jdbcDataSources = data
        break
      case 'jmsDataSources':
        state.topology.jmsDataSources = data
        break
      case 'caches':
        state.topology.caches = data
        break
      case 'spouts':
        state.topology.spouts = data
        break
      case 'columns':
        state.topology.columns = data
        break
      case 'validates':
        state.topology.validates = data
        break
      case 'transforms':
        state.topology.transforms = data
        break
      case 'persist':
        state.topology.validates = {}
        state.topology.transforms = {}
        state.topology.persist = data
      default:
        return
    }
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}
