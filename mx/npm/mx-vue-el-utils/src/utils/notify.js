import {Message, MessageBox, Notification} from 'element-ui'
import locale from '@/assets/lang'

let error = (message) => Message({type: 'error', message})

let warn = (message) => Message({type: 'warning', message})

let info = (message, duration) => Notification({title: locale.i18n.t('message.title.prompt'), message, duration: duration || 3000})

let confirm = (message, fnOk, fnCancel) => {
  MessageBox.confirm(message, locale.i18n.t('message.title.confirm'), {
    confirmButtonText: locale.i18n.t('button.ok'),
    cancelButtonText: locale.i18n.t('button.cancel'),
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
  warn(message || locale.i18n.t('message.validate.default'))
}

export default {error, warn, info, confirm, formValidateWarn}
