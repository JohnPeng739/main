import util from 'util'

let formatRegExp = /%[sdj%]/g

/**
 * 根据参数格式化字符串，支持：%s（字符串）、%d（数字）、%j（JSON对象）。
 * @returns {string} 格式化后的字符串
 */
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

/**
 * 根据数字创建一个指定长度的字符串
 * @param number 数字
 * @param bits 长度，默认2位
 * @param filledChar 填充字符，默认为'0'
 * @returns {string} 转换后的字符串
 */
function formatFixedLenNumber(number, bits, filledChar) {
    if (filledChar) {
        filledChar = '' + filledChar
        if (filledChar.length > 1) {
            filledChar = filledChar.substr(0, 1)
        }
    } else {
        filledChar = '0'
    }
    bits = (bits >= 0) ? bits : 2
    let str = '' + number
    let len = str.length
    if (len > bits) {
        return str.substr(0, bits)
    } else if (len < bits) {
        return filledChar.repeat(bits - len) + str
    } else {
        return str
    }
}

/**
 * 将指定的long值转换为"yyyy/MM/dd"格式的日期字符串
 * @param longDatetime long值
 * @returns {string} 日期字符串
 */
function formatDate(longDatetime) {
    let date = long2Date(longDatetime)
    let year = date.getFullYear()
    let month = date.getMonth() + 1
    let day = date.getDate()
    return year + '/' + formatFixedLenNumber(month, 2) + '/' + formatFixedLenNumber(day, 2)
}

/**
 * 将指定的long值转换为"yyyy/MM/dd HH:mm:ss"格式的时间字符串
 * @param longDatetime long值
 * @returns {string} 时间字符串
 */
function formatDatetime(longDatetime) {
    let date = long2Date(longDatetime)
    let hours = date.getHours()
    let minute = date.getMinutes()
    let second = date.getSeconds()
    return formatDate(longDatetime) + ' ' + formatFixedLenNumber(hours, 2) + ':' +formatFixedLenNumber(minute, 2)
        + ':' + formatFixedLenNumber(second, 2)
}

/**
 * 将指定的long值转换为"yyyy/MM/dd HH:mm:ss.SSS"格式的时间戳字符串
 * @param longDatetime long值
 * @returns {string} 时间戳字符串
 */
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