/**
 * Created by john on 2017/6/9.
 */
import util from 'util'
import {timestamp} from '../index'

let formatRegExp = /%[sdj%]/g

let log = function (level) {
  let args = Array.prototype.slice.call(arguments, 1)
  while (args[args.length - 1] === null) {
    args.pop()
  }
  let hasFormat = args && args[0] && args[0].match && args[0].match(formatRegExp) !== null
  let tokens = (hasFormat) ? args[0].match(formatRegExp) : []
  let ptokens = tokens.filter(t => t === '%%')
  let msg
  let meta = null
  let validMeta = false
  if (((args.length - 1) - (tokens.length - ptokens.length)) > 0 || args.length === 1) {
    // last arg is meta
    meta = args[args.length - 1] || args
    let metaType = Object.prototype.toString.call(meta)
    validMeta = metaType === '[object Object]' || metaType === '[object Error]' || metaType === '[object Array]'
    meta = validMeta ? args.pop() : null
  }
  msg = util.format.apply(null, args)

  msg = util.format('%s %s %s', timestamp(), level.toUpperCase(), msg)
  if (meta) {
    msg = util.format('%s\nMeta data: %j', msg, meta)
  }
  console.log(msg)
}

export default {
  debug: function () {
    if (process.env.NODE_ENV !== 'development') {
      return
    }
    let args = Array.prototype.slice.call(arguments)
    args.unshift('debug')
    log.apply(this, args)
  },

  info: function () {
    if (process.env.NODE_ENV !== 'development') {
      return
    }
    let args = Array.prototype.slice.call(arguments)
    args.unshift('info')
    log.apply(this, args)
  },

  warn: function () {
    let args = Array.prototype.slice.call(arguments)
    args.unshift('warn')
    log.apply(this, args)
  },

  error: function () {
    let args = Array.prototype.slice.call(arguments)
    args.unshift('error')
    log.apply(this, args)
  }
}
