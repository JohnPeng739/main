/**
 * Created by john on 2017/6/10.
 */
import ajax from './ajax'
import AjaxAxios from './ajax/ajax-axios'
import logger from './logger'
import formatter from './formatter'
import parser from './parser'
import createWsClient from './websocket'
import WsClient from './websocket/ws-client'

const clone = function (obj) {
    //
    // We only need to clone reference types (Object)
    //
    let copy = {}

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

    for (let i in obj) {
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

const cloneData = function (data) {
    return JSON.parse(JSON.stringify(data))
}

const mixin = function (dest, src) {
    if (dest === null || dest === undefined) {
        dest = {}
    }
    if (src === null || src === undefined || typeof src !== 'object') {
        return
    }
    for (let key in src) {
        let tar = dest[key]
        if (tar === null || tar === undefined) {
            dest[key] = src[key]
        } else if (tar instanceof Array) {
            dest[key] = dest[key].concat(src[key])
        } else if (dest[key] instanceof Object) {
            mixin(dest[key], src[key])
        } else {
            dest[key] = src[key]
        }
    }
}

const timestamp = function () {
    return new Date().toISOString()
}

const round = function (value, digits) {
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

export {ajax, logger, formatter, parser, createWsClient, timestamp, mixin, clone, cloneData, round, AjaxAxios, WsClient}