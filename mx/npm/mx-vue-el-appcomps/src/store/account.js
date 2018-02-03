import { formatter, logger } from 'mx-app-utils'
import { MxAjax } from 'mx-vue-el-utils'

const LOGIN = 'LOGIN'
const LOGOUT = 'LOGOUT'
const SET_LOGIN_USER = 'SET_LOGIN_USER'

const loginUrl = '/rest/login'
const logoutUrl = '/rest/logout/{id}'

const state = {
  loginUser: null
}

function authenticated (user) {
  return user && user.code && typeof user.code === 'string' && user.code.length > 0
}

const getters = {
  authenticated: state => {
    let user = state.loginUser
    return authenticated(user)
  },
  loginUser: state => {
    return state.loginUser
  }
}

const actions = {
  login ({commit, state}, {code, password, forced, success}) {
    let url = loginUrl
    logger.debug('send POST "%s", data: %j.', url, {code, password})
    MxAjax.post(url, {accountCode: code, password, forcedReplace: forced}, data => {
      if (data && data.account) {
        let {id, code, name, favorityTools, role} = data.account
        commit(LOGIN, {id, code, name, favorityTools, role})
        if (success && typeof success === 'function') {
          success(data)
        }
        logger.info('Account[%s] login successfully, user: %j.', code, data)
      }
    })
  },
  logout ({commit, state}, {success}) {
    if (!authenticated(state.loginUser)) {
      throw new Error('The user does not login the system.')
    }
    let url = logoutUrl
    let {id, code, name} = state.loginUser
    url = formatter.formatObj(url, {id})
    logger.debug('send GET "%s".', url)
    MxAjax.get(url, data => {
      if (data && data.account) {
        commit(LOGOUT)
        if (success && typeof success === 'function') {
          success(data)
        }
        logger.info('Account[%s] logout successfully, user: %j.', {code, name})
      }
    })
  },
  setLoginUser ({commit, state}, loginUser) {
    commit(SET_LOGIN_USER, loginUser)
  }
}

const mutations = {
  LOGIN (state, {id, code, name, role, favorityTools}) {
    let user = {id, code, name, role, favorityTools}
    state.loginUser = user
  },
  LOGOUT (state) {
    state.loginUser = null
  },
  SET_LOGIN_USER (state, loginUser) {
    state.loginUser = loginUser
  }
}

export default {state, getters, actions, mutations}
