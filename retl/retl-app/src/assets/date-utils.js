export const formatDate = (date) => {
  if (!date) {
    throw new Error('输入的日期为空')
  }
  let year = date.getFullYear()
  let month = date.getMonth() + 1
  let day = date.getDate()
  return year + '/' + format2Len(month) + '/' + format2Len(day)
}

export const parseDate = (dateString) => {
  if (!dateString) {
    throw new Error('输入的日期字符串为空')
  }
  let split = dateString.split('/')
  return new Date(split[0], split[1] - 1, split[2], 0, 0, 0)
}

let format2Len = v => {
  return (v >= 10 ? '' : '0') + v
}

export const formatDateTime = (datetime) => {
  if (!datetime) {
    throw new Error('输入的日期为空')
  }
  let date = formatDate(datetime)
  let hour = datetime.getHours()
  let minute = datetime.getMinutes()
  let second = datetime.getSeconds()
  return date + ' ' + format2Len(hour) + ':' + format2Len(minute) + ':' + format2Len(second)
}
