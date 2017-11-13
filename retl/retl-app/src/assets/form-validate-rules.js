import {logger} from 'mx-app-utils'
import {parseDate} from './date-utils'

export const requiredRule = ({type, msg, trigger}) => {
  let rule = {required: true}
  if (type && type !== '') {
    rule.type = type
  }
  if (msg !== null && msg !== undefined && msg !== '') {
    rule.message = msg
  } else {
    rule.message = '数据字段为必填'
  }
  if (trigger !== null && trigger !== undefined && trigger !== '') {
    rule.trigger = trigger
  } else {
    rule.trigger = 'blur'
  }
  logger.debug('创建了必填校验规则： %j', rule)
  return rule
}

const processExtremeInteger = (min, max) => {
  if (!min || !Number.isInteger(min)) {
    min = -1
  }
  if (!max || !Number.isInteger(max)) {
    max = -1
  }
  return {minValue: min, maxValue: max}
}

const rangeStringRule = (min, max, msg, trigger) => {
  let rule = {type: 'string'}
  if (!msg && msg !== '') {
    rule.message = msg
  }
  let {minValue, maxValue} = processExtremeInteger(min, max)
  if (minValue !== -1 && maxValue !== -1) {
    // min max
    rule.min = minValue
    rule.max = maxValue
    if (!rule.message) {
      rule.message = '数据字段的长度应介于[' + minValue + ' - ' + maxValue + ']之间'
    }
  } else if (minValue !== -1) {
    // min
    rule.min = minValue
    if (!rule.message) {
      rule.message = '数据字段的长度应大于[' + minValue + ']'
    }
  } else if (maxValue !== -1) {
    // max
    rule.max = maxValue
    if (!rule.message) {
      rule.message = '数据字段的长度应小于[' + max + ']'
    }
  }
  if (trigger !== null && trigger !== undefined && trigger !== '') {
    rule.trigger = trigger
  } else {
    rule.trigger = 'blur'
  }
  logger.debug('创建一个文本长度范围校验规则： %j', rule)
  return rule
}

const rangeNumberRule = (min, max, msg, trigger) => {
  let {minValue, maxValue} = processExtremeInteger(min, max)
  let numberValidator = (rule, value, callback) => {
    if (minValue !== -1 && maxValue !== -1) {
      if (value > maxValue || value < minValue) {
        let message = (!msg && msg !== '') ? msg : '数据字段的值应介于[' + minValue + ' - ' + maxValue + ']'
        callback(new Error(message))
      }
    } else if (minValue !== -1) {
      if (value < minValue) {
        let message = (!msg && msg !== '') ? msg : '数据字段的值应大于[' + minValue + ']'
        callback(new Error(message))
      }
    } else if (maxValue !== -1) {
      if (value > maxValue) {
        let message = (!msg && msg !== '') ? msg : '数据字段的值应小于[' + maxValue + ']'
        callback(new Error(message))
      }
    }
    callback()
  }
  let rule = {type: 'number', validator: numberValidator}
  if (!trigger && trigger !== '') {
    rule.trigger = trigger
  } else {
    rule.trigger = 'blur'
  }
  logger.debug('创建了一个数值范围规则： %j', rule)
  return rule
}

const processExtremeDate = (min, max) => {
  if (!min || min === '') {
    min = null
  } else {
    min = parseDate(min)
  }
  if (!max || max === '') {
    max = null
  } else {
    max = parseDate(max)
  }
  return {minValue: min, maxValue: max}
}

const rangeDateRule = (min, max, msg, trigger) => {
  let {minValue, maxValue} = processExtremeDate(min, max)
  let dateValidator = (rule, value, callback) => {
    if (minValue !== null && maxValue !== null) {
      if (value.getTime() > maxValue.getTime() || value.getTime() < minValue.getTime()) {
        let message = (!msg && msg !== '') ? msg : '数据字段的值应介于[' + minValue + ' - ' + maxValue + ']'
        callback(new Error(message))
      }
    } else if (minValue !== null) {
      if (value.getTime() < minValue.getTime()) {
        let message = (!msg && msg !== '') ? msg : '数据字段的值应晚于[' + minValue + ']'
        callback(new Error(message))
      }
    } else if (maxValue !== null) {
      if (value.getTime() > maxValue.getTime()) {
        let message = (!msg && msg !== '') ? msg : '数据字段的值应早于[' + maxValue + ']'
        callback(new Error(message))
      }
    }
    callback()
  }
  let rule = {type: 'date', validator: dateValidator}
  if (!trigger && trigger !== '') {
    rule.trigger = trigger
  } else {
    rule.trigger = 'blur'
  }
  logger.debug('创建了一个日期范围规则： ', rule)
  return rule
}

const rangeArrayRule = (min, max, msg, trigger) => {
  let {minValue, maxValue} = processExtremeInteger(min, max)
  let arrayValidator = (rule, value, callback) => {
    if (minValue !== -1 && maxValue !== -1) {
      if (value.length > maxValue || value.length < minValue) {
        let message = (!msg && msg !== '') ? msg : '数据字段的值个数应介于[' + minValue + ' - ' + maxValue + ']'
        callback(new Error(message))
      }
    } else if (minValue !== -1) {
      if (value.length < minValue) {
        let message = (!msg && msg !== '') ? msg : '数据字段的值个数应大于[' + minValue + ']'
        callback(new Error(message))
      }
    } else if (maxValue !== -1) {
      if (value.length > maxValue) {
        let message = (!msg && msg !== '') ? msg : '数据字段的值个数应小于[' + maxValue + ']'
        callback(new Error(message))
      }
    }
    callback()
  }
  let rule = {type: 'array', validator: arrayValidator}
  if (!trigger && trigger !== '') {
    rule.trigger = trigger
  } else {
    rule.trigger = 'blur'
  }
  logger.debug('创建了一个数组个数范围规则： ', rule)
  return rule
}

export const rangeRule = ({type, min, max, msg, trigger}) => {
  if (!min && !max) {
    throw new Error('范围校验规则至少要设定最大值或者最小值之间的一个')
  }
  if (!type) {
    type = 'string'
  }
  switch (type) {
    case 'number':
      return rangeNumberRule(min, max, msg, trigger)
    case 'date':
      return rangeDateRule(min, max, msg, trigger)
    case 'array':
      return rangeArrayRule(min, max, msg, trigger)
    case 'string':
    default:
      return rangeStringRule(min, max, msg, trigger)
  }
}

export const emailRule = ({msg, trigger}) => {
  let rule = {type: 'email'}
  if (msg !== null && msg !== undefined && msg !== '') {
    rule.message = msg
  } else {
    rule.message = '电子邮件格式错误'
  }
  if (trigger !== null && trigger !== undefined && trigger !== '') {
    rule.trigger = trigger
  } else {
    rule.trigger = 'blur'
  }
  return rule
}

export const customRule = ({validator, trigger}) => {
  if (!validator || (typeof validator !== 'function')) {
    throw new Error('自定义校验规则需要输入自定义的校验函数')
  }
  let rule = {validator: validator}
  if (trigger !== null && trigger !== undefined && trigger !== '') {
    rule.trigger = trigger
  } else {
    rule.trigger = 'blur'
  }
  return rule
}
