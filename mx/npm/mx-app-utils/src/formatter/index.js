import util from 'util'
import {timestamp} from "../index"

let formatRegExp = /%[sdj%]/g

function format() {
    let args = arguments
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
    return msg
}

function long2Date(longDatetime) {
    if (longDatetime < 0) {
        throw new Error('Invalidate date time value, need a long value up to 0.')
    }
    return new Date(longDatetime)
}

function formatFixedLenNumber(number, bits) {
    let str = '' + number
    let len = str.length
    if (len > bits) {
        return str.substr(0, bits)
    } else if (len < bits) {
        return '0'.repeat(bits - len) + str
    } else {
        return str
    }
}

function formatDate(longDatetime) {
    let date = long2Date(longDatetime)
    let year = date.getFullYear()
    let month = date.getMonth() + 1
    let day = date.getDate()
    return year + '/' + formatFixedLenNumber(month, 2) + '/' + formatFixedLenNumber(day, 2)
}

function formatDatetime(longDatetime) {
    let date = long2Date(longDatetime)
    let hours = date.getHours()
    let minute = date.getMinutes()
    let second = date.getSeconds()
    return formatDate(longDatetime) + ' ' + formatFixedLenNumber(hours, 2) + ':' +formatFixedLenNumber(minute, 2)
        + ':' + formatFixedLenNumber(second, 2)
}

function formatTimestamp(longDatetime) {
    let date = long2Date(longDatetime)
    let mm = date.getMilliseconds()
    return formatDatetime(longDatetime) + '.' + formatFixedLenNumber(mm, 3)
}

export default {
    format: format,
    formatDate: formatDate,
    formatDatetime: formatDatetime,
    formatTimestamp: formatTimestamp,
    formatFixedLen: formatFixedLenNumber
}