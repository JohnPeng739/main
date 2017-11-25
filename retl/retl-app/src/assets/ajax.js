import {ajax, logger} from 'mx-app-utils'
import { Message } from 'element-ui'

const defaultError = errorMessage => {
  if (errorMessage.response) {
    logger.debug(errorMessage.response.status)
    let msg = '访问服务器错误，错误号：' + errorMessage.response.status + ', 错误信息：' + errorMessage.response.statusText + '。'
    Message({type: 'error', message: msg})
  } else {
    Message({type: 'error', message: errorMessage})
  }
}

const preurl = url => {
  let user = JSON.parse(sessionStorage.getItem('auth.user'))
  if (user) {
    let userCode = user.code
    if (url.indexOf('?') >= 0) {
      url = url + '&userCode=' + userCode
    } else {
      url = url + '?userCode=' + userCode
    }
  }
  return url
}

const get = (url, success) => ajax.get(preurl(url), success, defaultError)

const getWithError = (url, success, error) => ajax.get(preurl(url), success, error)

const post = (url, data, success) => ajax.post(preurl(url), data, success, defaultError)

const postWithError = (url, data, success, error) => ajax.post(preurl(url), data, success, error)

const put = (url, data, success) => ajax.put(preurl(url), data, success, defaultError)

const putWithError = (url, data, success, error) => ajax.put(preurl(url), data, success, error)

const del = (url, success) => ajax.del(preurl(url), success, defaultError)

const delWithError = (rul, success, error) => ajax.delete(preurl(url), success, error)

export {get, post, put, del, getWithError, postWithError, putWithError, delWithError, defaultError}
