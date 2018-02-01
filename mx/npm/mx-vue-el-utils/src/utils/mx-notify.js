import { Message, MessageBox, Notification } from 'element-ui'
import locale from '@/utils/mx-locale'

const i18n = locale.i18n

let error = (message) => Message({type: 'error', message})

let warn = (message) => Message({type: 'warning', message})

let info = (message, duration) => Notification({title: i18n.t('message.title.prompt'), message, duration: duration || 3000})

let confirm = (message, fnOk, fnCancel) => {
  MessageBox.confirm(message, i18n.t('message.title.confirm'), {
    confirmButtonClass: 'button',
    confirmButtonText: i18n.t('button.ok'),
    cancelButtonClass: 'button',
    cancelButtonText: i18n.t('button.cancel'),
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
