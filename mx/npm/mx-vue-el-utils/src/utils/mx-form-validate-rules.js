import { logger, parser } from 'mx-app-utils'
import {MxLocale} from './mx-locale'

const i18n = MxLocale.i18n()

const _processExtremeInteger = function (min, max) {
  if (!min || !Number.isInteger(min)) {
    min = -1
  }
  if (!max || !Number.isInteger(max)) {
    max = -1
  }
  return {minValue: min, maxValue: max}
}

const _rangeStringRule = function (min, max, msg, trigger) {
  let rule = {type: 'string'}
  if (!msg && msg !== '') {
    rule.message = msg
  }
  let {minValue, maxValue} = _processExtremeInteger(min, max)
  if (minValue !== -1 && maxValue !== -1) {
    // min max
    rule.min = minValue
    rule.max = maxValue
    if (!rule.message) {
      rule.message = i18n.t('message.validate.stringRangeBetween', [minValue, maxValue])
    }
  } else if (minValue !== -1) {
    // min
    rule.min = minValue
    if (!rule.message) {
      rule.message = i18n.t('message.validate.stringRangeLarge', [minValue])
    }
  } else if (maxValue !== -1) {
    // max
    rule.max = maxValue
    if (!rule.message) {
      rule.message = i18n.t('message.validate.stringRangeSmall', [maxValue])
    }
  }
  if (trigger !== null && trigger !== undefined && trigger !== '') {
    rule.trigger = trigger
  } else {
    rule.trigger = 'blur'
  }
  logger.debug('create a string range rule: %j', rule)
  return rule
}

const _rangeNumberRule = function (min, max, msg, trigger) {
  let {minValue, maxValue} = _processExtremeInteger(min, max)
  let numberValidator = (rule, value, callback) => {
    if (minValue !== -1 && maxValue !== -1) {
      if (value > maxValue || value < minValue) {
        let message = (!msg && msg !== '') ? msg : i18n.t('message.validate.numberRangeBetween', [minValue, maxValue])
        callback(new Error(message))
      }
    } else if (minValue !== -1) {
      if (value < minValue) {
        let message = (!msg && msg !== '') ? msg : i18n.t('message.validate.numberRangeLarge', [minValue])
        callback(new Error(message))
      }
    } else if (maxValue !== -1) {
      if (value > maxValue) {
        let message = (!msg && msg !== '') ? msg : i18n.t('message.validate.numberRangeSmall', [maxValue])
        callback(new Error(message))
      }
    }
    callback()
  }
  let rule = {type: 'number', validator: numberValidator}
  if (trigger && typeof trigger === 'string' && trigger !== '') {
    rule.trigger = trigger
  } else {
    rule.trigger = 'blur'
  }
  logger.debug('create a number range rule: %j', rule)
  return rule
}

const _processExtremeDate = function (min, max) {
  if (min && typeof min === 'string') {
    min = parser.parseDate(min)
  } else {
    min = undefined
  }
  if (max && typeof max === 'string') {
    max = parser.parseDate(max)
  } else {
    max = undefined
  }
  return {minValue: min, maxValue: max}
}

const _rangeDateRule = function (min, max, msg, trigger) {
  let {minValue, maxValue} = _processExtremeDate(min, max)
  let dateValidator = (rule, value, callback) => {
    if (minValue !== null && maxValue !== null) {
      if (value.getTime() > maxValue.getTime() || value.getTime() < minValue.getTime()) {
        let message = (!msg && msg !== '') ? msg : i18n.t('message.validate.dateRangeBetween', [min, max])
        callback(new Error(message))
      }
    } else if (minValue !== null) {
      if (value.getTime() < minValue.getTime()) {
        let message = (!msg && msg !== '') ? msg : i18n.t('message.validate.dateRangeLarge', [min])
        callback(new Error(message))
      }
    } else if (maxValue !== null) {
      if (value.getTime() > maxValue.getTime()) {
        let message = (!msg && msg !== '') ? msg : i18n.t('message.validate.dateRangeSmall', [max])
        callback(new Error(message))
      }
    }
    callback()
  }
  let rule = {type: 'date', validator: dateValidator}
  if (trigger && typeof trigger === 'string' && trigger !== '') {
    rule.trigger = trigger
  } else {
    rule.trigger = 'blur'
  }
  logger.debug('create a date range rule: %j.', rule)
  return rule
}

const _rangeArrayRule = function (min, max, msg, trigger) {
  let {minValue, maxValue} = _processExtremeInteger(min, max)
  let arrayValidator = (rule, value, callback) => {
    if (minValue !== -1 && maxValue !== -1) {
      if (value.length > maxValue || value.length < minValue) {
        let message = (!msg && msg !== '') ? msg : i18n.t('message.validate.arrayRangeBetween', [minValue, maxValue])
        callback(new Error(message))
      }
    } else if (minValue !== -1) {
      if (value.length < minValue) {
        let message = (!msg && msg !== '') ? msg : i18n.t('message.validate.arrayRangeLarge', [minValue])
        callback(new Error(message))
      }
    } else if (maxValue !== -1) {
      if (value.length > maxValue) {
        let message = (!msg && msg !== '') ? msg : i18n.t('message.validate.arrayRangeSmall', [maxValue])
        callback(new Error(message))
      }
    }
    callback()
  }
  let rule = {type: 'array', validator: arrayValidator}
  if (trigger && typeof trigger === 'string' && trigger !== '') {
    rule.trigger = trigger
  } else {
    rule.trigger = 'blur'
  }
  logger.debug('create a array range rule: %j.', rule)
  return rule
}

class MxFormValidateRules {
  static requiredRule (param) {
    let {type, msg, trigger} = param || {}
    let rule = {required: true}
    if (type && typeof type === 'string' && type !== '') {
      rule.type = type
    }
    if (msg && typeof msg === 'string' && msg !== '') {
      rule.message = msg
    } else {
      rule.message = i18n.t('message.validate.required', [''])
    }
    if (trigger && typeof trigger === 'string' && trigger !== '') {
      rule.trigger = trigger
    } else {
      rule.trigger = 'blur'
    }
    logger.debug('create a required rule: %j', rule)
    return rule
  }

  static rangeRule (param) {
    let {type, min, max, msg, trigger} = param || {}
    if (!min && !max) {
      throw new Error('You need set the min or max value for the range rule.')
    }
    if (!type) {
      type = 'string'
    }
    switch (type) {
      case 'number':
        return _rangeNumberRule(min, max, msg, trigger)
      case 'date':
        return _rangeDateRule(min, max, msg, trigger)
      case 'array':
        return _rangeArrayRule(min, max, msg, trigger)
      case 'string':
      default:
        return _rangeStringRule(min, max, msg, trigger)
    }
  }

  static emailRule (param) {
    let {msg, trigger} = param || {}
    let rule = {type: 'email'}
    if (msg && typeof msg === 'string' && msg !== '') {
      rule.message = msg
    } else {
      rule.message = i18n.t('message.validate.email')
    }
    if (trigger && typeof trigger === 'string' && trigger !== '') {
      rule.trigger = trigger
    } else {
      rule.trigger = 'blur'
    }
    return rule
  }

  static customRule (param) {
    let {validator, trigger} = param || {}
    let rule = {}
    if (validator && typeof validator === 'function') {
      rule.validator = validator
    } else {
      throw new Error('You need define a custom validator for the custom rule.')
    }
    if (trigger !== null && trigger !== undefined && trigger !== '') {
      rule.trigger = trigger
    } else {
      rule.trigger = 'blur'
    }
    return rule
  }
}

export default MxFormValidateRules

export { MxFormValidateRules }
