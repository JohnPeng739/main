import {formatter} from '../index'

let throwFormatError = (str, reg) => {
    throw new Error(formatter.format('The date format not matched, date: %s, format: %s.', str, reg))
}

/**
 * 根据正则表达式对字符串进行日期解析
 * @param dateStr 日期字符串
 * @param formatReg 日期格式正则表达式，默认为： /\d{2,4}\/\d{1,2}\/\d{1,2}/
 * @returns {Date} 转换后的日期对象，如果转换不成功，将抛出异常
 */
const parseDate = (dateStr, formatReg) => {
    if (!dateStr) {
        throw new Error('The date string is blank.')
    }
    let reg = new RegExp(formatReg || /\d{2,4}\/\d{1,2}\/\d{1,2}/, 'g')
    let matched = dateStr.match(reg)
    if (!matched) {
        throwFormatError(dateStr, reg)
    }
    matched = matched[0].match(/\d{1,}/g)
    if (matched.length === 3) {
        return new Date(matched[0], matched[1] - 1, matched[2], 0, 0, 0)
    } else if (matched.length === 6) {
        return new Date(matched[0], matched[1] - 1, matched[2], matched[3], matched[4], matched[5])
    } else if (matched.length === 7) {
        return new Date(matched[0], matched[1] - 1, matched[2], matched[3], matched[4], matched[5], matched[6])
    }
    throwFormatError(dateStr, reg)
}

/**
 * 将字符串转换为JSON对象
 * @param jsonStr JSON格式的字符串
 * @returns {JSONObject} JSON对象
 */
const parseJson = (jsonStr) => JSON.parse(jsonStr)

export default {parseDate, parseJson}