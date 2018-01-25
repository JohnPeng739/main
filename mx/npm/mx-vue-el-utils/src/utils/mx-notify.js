import { Message, MessageBox, Notification } from 'element-ui'
import { t } from '../locale'

let error = (message) => Message({type: 'error', message})

let warn = (message) => Message({type: 'warning', message})

let info = (message, duration) => Notification({title: t('message.title.prompt'), message, duration: duration || 3000})

let confirm = (message, fnOk, fnCancel) => {
  MessageBox.confirm(message, t('message.title.confirm'), {
    confirmButtonText: t('button.ok'),
    cancelButtonText: t('button.cancel'),
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
  warn(message || t('message.validate.default'))
}

let MxNotify = {
  error, warn, info, confirm, formValidateWarn
}

export default MxNotify

export { MxNotify }
