import {ajax, logger} from 'mx-app-utils'
import locale from '@/assets/lang'
import notify from './notify'

let defaultError = (errorMessage) => {
  if (errorMessage.response) {
    logger.debug(errorMessage.response.status)
    let msg = locale.i18n.t('error.ajax.default', {stats: errorMessage.response.status, text: errorMessage.response.statusText})
    notify.error(msg)
  } else {
    notify.error(errorMessage)
  }
}

let preurl = (url) => {
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

let get = (url, success, error) => ajax.get(preurl(url), success, error || defaultError)

let post = (url, data, success, error) => ajax.post(preurl(url), data, success, error || defaultError)

let put = (url, data, success, error) => ajax.put(preurl(url), data, success, error || defaultError)

let del = (url, success, error) => ajax.del(preurl(url), success, error || defaultError)

export default {get, post, put, del}
