import {Message, MessageBox} from 'element-ui'
import {Notification} from 'element-ui'

let error =(message) => {
  Message({type: 'error', message})
}

let warn = (message) => {
  Message({type: 'warning', message})
}

let info = (message, duration) => {
  if (duration === undefined) {
    duration = 3000
  }
  Notification({title: '提示', message, duration: duration})
}

let confirm = (message, fnOk) => {
  MessageBox.confirm(message, '请确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    if (fnOk && typeof fnOk === "function") {
      fnOk()
    }
  }).catch(() => {})
}

let formValidateWarn = _ => warn('表单数据校验失败，请检查输入的数据。')

export {error, warn, info, confirm, formValidateWarn}
