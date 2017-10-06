let error =(owner, message) => {
  owner.$message({type: 'error', message})
}

let warn = (owner, message) => {
  owner.$message({type: 'warning', message})
}

let info = (owner, message, duration) => {
  if (duration === undefined) {
    duration = 3000
  }
  owner.$notify({title: '提示', message, duration: 0})
}

let formValidateWarn = owner => warn(owner, '表单数据校验失败，请检查输入的数据。')

export {error, warn, info, formValidateWarn}
