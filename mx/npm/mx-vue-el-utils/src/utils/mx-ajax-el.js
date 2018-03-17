import {AjaxAxios} from 'mx-app-utils'
import MxNotify from './mx-notify'
import locale from './mx-locale'

const i18n = locale.i18n
let token
if (window.sessionStorage) {
  token = window.sessionStorage.getItem('auth.token')
}
let ajax = new AjaxAxios(token)

const _defaultError = function (errorMessage) {
  let response = errorMessage.response
  if (response) {
    let msg = i18n.t('message.ajax.default', {status: response.status, text: response.statusText})
    MxNotify.error(msg)
  } else {
    MxNotify.error(errorMessage)
  }
}

const _preurl = function (url) {
  if (window.sessionStorage) {
    let user = JSON.parse(window.sessionStorage.getItem('auth.user'))
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
const head = (params) => ajax.head(_prepareParams(params))
const patch = (params) => ajax.patch(_prepareParams(params))

let MxAjaxEl = {get, post, put, del, head, patch}

export default MxAjaxEl
