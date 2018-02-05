import { ajax } from 'mx-app-utils'
import MxNotify from './mx-notify'
import locale from './mx-locale'

const i18n = locale.i18n

const _defaultError = (errorMessage) => {
  let response = errorMessage.response
  if (response) {
    let msg = i18n.t('message.ajax.default', {status: response.status, text: response.statusText})
    MxNotify.error(msg)
  } else {
    MxNotify.error(errorMessage)
  }
}

const _preurl = (url) => {
  if (sessionStorage) {
    let user = JSON.parse(sessionStorage.getItem('auth.user'))
    if (user) {
      let userCode = user.code
      if (url.indexOf('?') >= 0) {
        url = url + '&userCode=' + userCode
      } else {
        url = url + '?userCode=' + userCode
      }
    }
  }
  return url
}

const _prepareParams = (params) => {
  if (!params || !(params instanceof Object)) {
    throw new Error('The ajax parameters object is invalid.')
  }
  let {url, fnError} = params
  if (!url || typeof url !== 'string' || url.length <= 0) {
    throw new Error('The ajax url is invalid.')
  }
  params.url = _preurl(url)
  if (!fnError || typeof fnError !== 'function') {
    params.fnError = _defaultError
  }
  return params
}

const get = (params) => ajax.get(_prepareParams(params))
const post = (params) => ajax.post(_prepareParams(params))
const put = (params) => ajax.put(_prepareParams(params))
const del = (params) => ajax.del(_prepareParams(params))
const setToken = (token) => ajax.setToken(token)
const getToken = () => ajax.getToken()

let MxAjax = {get, post, put, del, setToken, getToken}

export default MxAjax
export { MxAjax }
