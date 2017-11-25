import {getNavTitle} from '../../router'

export const SET_PATH_NAME = 'SET_PATH_NAME'

const state = {
  path: '',
  name: ''
}

const getters = {
  path: state => state.code,
  pathName: state => state.name
}

const actions = {
  goto({commit, state}, {owner, path, name}) {
    if (!path) {
      return
    }
    commit(SET_PATH_NAME, {path, name})
    owner.$router.push(path)
  },
  setPathName({commit, state}, {path, name}) {
    if (!path) {
      return
    }
    commit(SET_PATH_NAME, {path, name})
  }
}

const mutations = {
  SET_PATH_NAME(state, {path, name}) {
    state.path = path
    if (name) {
      state.name = name
    } else {
      state.name = getNavTitle(path)
    }
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}
