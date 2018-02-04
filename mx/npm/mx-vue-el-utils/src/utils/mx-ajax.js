import { ajax } from 'mx-app-utils'
import MxNotify from './mx-notify'
import locale from './mx-locale'

const i18n = locale.i18n

const defaultError = (errorMessage) => {
  let response = errorMessage.response
  console.log(response)
  if (response) {
    let msg = i18n.t('message.ajax.default', {status: response.status, text: response.statusText})
    MxNotify.error(msg)
  } else {
    MxNotify.error(errorMessage)
  }
}

const preurl = (url) => {
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

const get = (url, success, error) => ajax.get(preurl(url), success, error || defaultError)
const post = (url, data, success, error) => ajax.post(preurl(url), data, success, error || defaultError)
const put = (url, data, success, error) => ajax.put(preurl(url), data, success, error || defaultError)
const del = (url, success, error) => ajax.del(preurl(url), success, error || defaultError)
const setToken = (token) => ajax.setToken(token)
const getToken = () => ajax.getToken()

let MxAjax = {get, post, put, del, setToken, getToken}

export default MxAjax
export { MxAjax }
