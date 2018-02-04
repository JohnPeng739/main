/*
import {MxStoreAccount} from 'mx-vue-el-appcomps'

const state = () => (MxStoreAccount.state)
const getters = MxStoreAccount.getters
const actions = MxStoreAccount.actions
const mutations = MxStoreAccount.mutations

export {state, getters, actions, mutations}
*/
import {logger} from 'mx-app-utils'
import {MxAjax} from 'mx-vue-el-utils'

function authenticated (user) {
  return user && user.id && user.code && user.name
}

export const state = () => ({
  loginUser: null
})

export const getters = {
  authenticated: state => {
    let user = state.loginUser
    return authenticated(user)
  },
  loginUser: state => {
    return state.loginUser
  }
}

export const mutations = {
  SET_LOGIN_USER: function (state, user) {
    state.loginUser = user
  }
}

export const actions = {
  nuxtServerInit({commit}, {req}) {
    if (req.session && req.session.authUser) {
      console.log('***************************************')
      console.log(req)
      commit('SET_LOGIN_USER', req.session.authUser)
      logger.debug('Nuxt server init invoked.')
    }
  },
  login({commit, state}, {code, password, forced, success}) {
    MxAjax.post('/rest/login', {accountCode: code, password, forcedReplace: forced}, data => {
      if (data && data.account) {
        let {id, code, name, favorityTools, role} = data.account
        let authUser = {id, code, name, favorityTools, role}
        commit('SET_LOGIN_USER', authUser)
        if (success && typeof success === 'function') {
          success(data)
        }
        logger.debug('Account[%s, %s, %s] login successfully.', id, code, name)
      }
    })
  },
  logout({commit, state}, {success}) {
    let user = state.loginUser
    if (!authenticated(user)) {
      throw new Error('The user does not be authority.')
    }
    let {id, code, name} = state.loginUser
    MxAjax.get('/rest/logout/' + id, data => {
      if (data && data.account) {
        commit('SET_LOGIN_USER', null)
        if (success && typeof success === 'function') {
          success(data)
        }
        logger.debug('Account[%s, %s, %s] logout successfully.', id, code, name)
      }
    })
  }
}
