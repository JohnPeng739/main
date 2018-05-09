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

const int2ByteArray = function (i) {
    if (i > Math.pow(2, 32)) {
        throw new Error('The number ' + i + ' bigger than int.')
    }
    let bytes = new Array(4)
    for (let index = 0; index < 4; index++) {
        bytes[index] = ((i >>> (32 - (index + 1) * 8)) & 0xff)
    }
    return bytes
}

const long2ByteArray = function (l) {
    if (l > Math.pow(2, 64)) {
        throw new Error('The number ' + l + ' bigger than long.')
    }
    let bytes = new Array(8)
    let low = l & 0xffffffff
    let up = (l / Math.pow(2, 32)) >>> 0
    for (let index = 0; index < 4; index++) {
        bytes[index] = ((low >>> (64 - (index + 1) * 8)) & 0xff)
    }
    for (let index = 0; index < 4; index++) {
        bytes[index + 4] = ((up >>> (64 - (index + 1) * 8)) & 0xff)
    }
    return bytes
}

const byteArray2Int = function (bytes) {
    if (bytes === null || bytes === undefined) {
        return 0
    }
    let i = 0
    for (let index = 0; index < Math.min(bytes.length, 4); index++) {
        i = (((i << 8) >>> 0) | (bytes[index] & 0xff)) >>> 0
    }
    return i
}

const byteArray2Long = function (bytes) {
    if (bytes === null || bytes === undefined) {
        return 0
    }
    let low = 0
    let up = 0
    for (let index = 0; index < Math.min(bytes.length, 4); index++) {
        low = (((low << 8) >>> 0) | (bytes[index] & 0xff)) >>> 0
    }
    for (let index = 4; index < Math.min(bytes.length, 8); index++) {
        up = (((up << 8) >>> 0) | (bytes[index] & 0xff)) >>> 0
    }
    return low + (up * Math.pow(2, 32))
}

export {
    ajax,
    logger,
    formatter,
    parser,
    createWsClient,
    timestamp,
    mixin,
    clone,
    cloneData,
    round,
    int2ByteArray,
    long2ByteArray,
    byteArray2Int,
    byteArray2Long,
    AjaxAxios,
    WsClient}