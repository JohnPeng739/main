import { Message, MessageBox, Notification } from 'element-ui'
import locale from './mx-locale'

const i18n = locale.i18n

let error = (message) => Message({type: 'error', message})

let warn = (message) => Message({type: 'warning', message})

let info = (message, duration) => Notification({title: i18n.t('message.notify.prompt'), message, duration: duration || 3000})

let confirm = (message, fnOk, fnCancel) => {
  MessageBox.confirm(message, i18n.t('message.notify.confirm'), {
    confirmButtonClass: 'button',
    confirmButtonText: i18n.t('common.ok'),
    cancelButtonClass: 'button',
    cancelButtonText: i18n.t('common.cancel'),
    type: 'warning'
  }).then(() => {
    if (fnOk && typeof fnOk === 'function') {
      fnOk()
    }
  }).catch(() => {
    if (fnCancel && typeof fnCancel === 'function') {
      fnCancel()
    }
  })
}

let formValidateWarn = (message) => {
  warn(message || i18n.t('message.validate.default'))
}

let MxNotify = {
  error, warn, info, confirm, formValidateWarn
}

export default MxNotify

export { MxNotify }
