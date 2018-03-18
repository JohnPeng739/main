import { Message, MessageBox, Notification } from 'element-ui'
import {MxLocale} from './mx-locale'

const i18n = MxLocale.i18n()

class MxNotify {
  static error (message) {
    Message({type: 'error', message})
  }

  static warn (message) {
    Message({type: 'warn', message})
  }

  static info (message, duration) {
    Notification({title: i18n.t('message.notify.prompt'), message, duration: duration || 3000})
  }

  static confirm (message, fnOk, fnCancel) {
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

  static formValidateWarn (message) {
    MxNotify.warn(message || i18n.t('message.validate.default'))
  }
}

export default MxNotify

export { MxNotify }
