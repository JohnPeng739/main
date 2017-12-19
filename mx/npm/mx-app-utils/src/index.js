/**
 * Created by john on 2017/6/10.
 */
import ajax from './ajax'
import logger from './logger'
import formatter from './formatter'
import parser from './parser'

function clone(obj) {
    //
    // We only need to clone reference types (Object)
    //
    var copy = {}

    if (obj instanceof Error) {
        // With potential custom Error objects, this might not be exactly correct,
        // but probably close-enough for purposes of this lib.
        copy = {message: obj.message}
        Object.getOwnPropertyNames(obj).forEach(function (key) {
            copy[key] = obj[key]
        })
        return copy
    } else if (!(obj instanceof Object)) {
        return obj
    } else if (obj instanceof Date) {
        return new Date(obj.getTime())
    }

    for (var i in obj) {
        if (Array.isArray(obj[i])) {
            copy[i] = obj[i].slice(0)
        } else if (obj[i] instanceof Buffer) {
            copy[i] = obj[i].slice(0)
        } else if (typeof obj[i] !== 'function') {
            copy[i] = obj[i] instanceof Object ? exports.clone(obj[i]) : obj[i]
        } else if (typeof obj[i] === 'function') {
            copy[i] = obj[i]
        }
    }

    return copy
}

function timestamp() {
    return new Date().toISOString()
}

function round(value, digits) {
    if (value && typeof value === 'number') {
        if (!digits || typeof digits !== 'number' || digits < 0) {
            digits = 0
        }
        let v = Math.pow(10, digits)
        return Math.round(value * v) / v
    } else {
        return 0
    }
}

export {ajax, logger, formatter, parser, timestamp, clone, round}