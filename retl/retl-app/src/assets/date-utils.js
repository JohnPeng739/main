export const formatDate = (date) => {
  if (!date) {
    throw new Error('输入的日期为空')
  }
  let year = date.getFullYear()
  let month = date.getMonth() + 1
  let day = date.getDate()
  return year + '/' + (month < 10 ? '0' : '') + month + '/' + (day < 10 ? '0' : '') + day
}

export const parseDate = (dateString) => {
  if (!dateString) {
    throw new Error('输入的日期字符串为空')
  }
  let split = dateString.split('/')
  return new Date(split[0], split[1] - 1, split[2], 0, 0, 0)
}
