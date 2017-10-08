import {logger} from 'dsutils'
import {post} from '../../../../assets/ajax'

export const LOGIN = 'LOGIN'
export const LOGOUT = 'LOGOUT'

const state = {
  loginUser: null
}

let refreshState = () => {
  state.loginUser = JSON.parse(sessionStorage.getItem('auth.user'))
}

const getters = {
  authenticated: state => {
    let code = state.loginUser.code
    return code !== null && code !== undefined && code !== ''
  },
  code: state => {
    if (state.loginUser === null) {
      refreshState()
    }
    return state.loginUser.code
  },
  name: state => {
    if (state.loginUser === null) {
      refreshState()
    }
    return state.loginUser.name
  },
  tools: state => {
    if (state.loginUser === null) {
      refreshState()
    }
    return state.loginUser.tools
  },
  roles: state => {
    if (state.loginUser === null) {
      refreshState()
    }
    return state.loginUser.roles
  }
}

const actions = {
  login ({commit, state}, {user, password, success}) {
    let url = '/rest/login'
    logger.debug('send POST "%s".', url)
    post(url, {user, password}, data => {
      let {code, name, tools, roles} = data
      logger.info('POST "%s" success, user code: %s, user name: %s, role: %s.', url, code, name, roles)
      commit(LOGIN, {code, name, tools, roles})
      success()
    })
  },
  logout ({commit, state}, {success}) {
    let url = '/rest/logout'
    logger.debug('send POST "%s".', url)
    post(url, {
      user: state.loginUser.code
    }, data => {
      commit(LOGOUT)
      success()
    })
  }
}

const mutations = {
  LOGIN (state, {code, name, tools, roles}) {
    state.loginUser = {code, name, tools, roles}
    sessionStorage.setItem("auth.user", JSON.stringify(state.loginUser))
  },
  LOGOUT (state) {
    state.loginUser = JSON.parse(sessionStorage.getItem('auth.user'))
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}
