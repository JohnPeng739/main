import {Message} from 'element-ui'
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

let formValidateWarn = _ => warn('表单数据校验失败，请检查输入的数据。')

export {error, warn, info, formValidateWarn}
