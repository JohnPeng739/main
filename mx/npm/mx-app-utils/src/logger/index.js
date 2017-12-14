/**
 * Created by john on 2017/6/9.
 */
import util from 'util'
import {format, timestamp} from '../index'

let log = function (level) {
    let args = Array.prototype.slice.call(arguments, 1)
    let msg = format.strFormat.apply(this, args)
    msg = util.format('%s %s %s', timestamp(), level.toUpperCase(), msg)
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
