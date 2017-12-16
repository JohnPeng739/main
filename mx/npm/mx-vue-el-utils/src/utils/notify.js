import {Message, MessageBox, Notification} from 'element-ui'

let error = (message) => Message({type: 'error', message})

let warn = (message) => Message({type: 'warning', message})

let info = (message, duration) => Notification({title: '提示', message, duration: duration || 3000})

let confirm = (message, fnOk, fnCancel) => {
  MessageBox.confirm(message, '请确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
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

let formValidateWarn = (message) => warn(message || '表单数据校验失败，请检查输入的数据。')

export default {error, warn, info, confirm, formValidateWarn}
