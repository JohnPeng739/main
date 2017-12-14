import util from 'util'
import {timestamp} from "../index"

let formatRegExp = /%[sdj%]/g

let strFormat = function () {
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

export default {
    strFormat: strFormat
}