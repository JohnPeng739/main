/**
 * Created by john on 2017/6/9.
 */
import util from 'util'
import {timestamp, formatter} from '../index'

let log = function (level) {
    let args = Array.prototype.slice.call(arguments, 1)
    let msg = formatter.format.apply(this, args)
    msg = util.format('%s %s %s', timestamp(), level.toUpperCase(), msg)
    console.log(msg)
}

var defaultLevel = 'debug'

export default {
    setLevel: function (level) {
        defaultLevel = level
    },
    debug: function () {
        if ('debug' === defaultLevel) {
            let args = Array.prototype.slice.call(arguments)
            args.unshift('debug')
            log.apply(this, args)
        }
    },

    info: function () {
        if (['info', 'debug'].indexOf(defaultLevel) >= 0) {
            let args = Array.prototype.slice.call(arguments)
            args.unshift('info')
            log.apply(this, args)
        }
    },

    warn: function () {
        if (['warn', 'info', 'debug'].indexOf(defaultLevel) >= 0) {
            let args = Array.prototype.slice.call(arguments)
            args.unshift('warn')
            log.apply(this, args)
        }
    },

    error: function () {
        if (['error', 'warn', 'info', 'debug'].indexOf(defaultLevel) >= 0) {
            let args = Array.prototype.slice.call(arguments)
            args.unshift('error')
            log.apply(this, args)
        }
    }
}
