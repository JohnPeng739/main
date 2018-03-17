import axios from 'axios'
import {logger, mixin} from '../index'

const _prepareParams = function (params, token) {
    if (!params || !(params instanceof Object)) {
        throw new Error('The ajax parameters is null or not a Object.')
    }
    let {url, config} = params
    if (!url || typeof url !== 'string' || url.length <= 0) {
        throw new Error('The ajax url is blank or not a string.')
    }
    if (!config) {
        config = {}
        if (!config.headers) {
            config.headers = {}
        }
    }
    if (!(config && config.headers && config.headers['Content-Type'])) {
        // 没有设置ContentType，默认使用application/json
        config.headers['Content-Type'] = 'application/json'
    }
    if (token && typeof token === 'string' && token.length > 0) {
        config.headers['Authorization'] = token
    }
    params.config = config
    return params
}

const _error = function (fnError, err) {
    logger.debug('Any error, fnError: %s, err: %j.', fnError, err)
    if (fnError && typeof fnError === 'function') {
        if (err.response) {
            // The request was made and the server responded with a status code that falls out of the range of 2xx
            fnError(err.response)
        } else if (err.request) {
            // The request was made but no response was received
            // `error.request` is an instance of XMLHttpRequest in the browser and an instance of
            fnError(err.response)
        } else if (err.message) {
            // Something happened in setting up the request that triggered an Error
            fnError(err.message)
        } else {
            fnError(err)
        }
    }
}

const _success = function (fnSuccess, fnError, res) {
    let data = res.data
    let pagination = undefined
    if (data.errorCode && !(data.errorCode === 0)) {
        _error(fnError, data.errorMessage)
        return
    }
    if (data.pagination && data.pagination instanceof Object) {
        pagination = data.pagination
    }
    if (data.data) {
        data = data.data
    }
    if (fnSuccess && typeof fnSuccess === 'function') {
        if (pagination !== undefined) {
            logger.debug('Response paginated data, pagination: %j, data: %j.', pagination, data)
            fnSuccess(pagination, data)
        } else {
            logger.debug('Response data: %j.', data)
            fnSuccess(data)
        }
    }
}

class AjaxAxios {
    constructor(token) {
        this._token = undefined

        this.token = token
    }

    get token() {
        return this._token
    }

    set token(token) {
        if (!token || typeof token !== 'string' || token.length <= 0) {
            logger.warn('The token is blank or not a string.')
            return
        }
        let prefix = 'Bearer '
        if (token.search(prefix) !== 0) {
            token = prefix + token
        }
        this._token = token
    }

    get(params) {
        let {url, config, fnSuccess, fnError} = _prepareParams(params, this.token)
        axios.get(url, config)
            .then(res => _success(fnSuccess, fnError, res))
            .catch(err => _error(fnError, err))
    }

    post(params) {
        let {url, data, config, fnSuccess, fnError} = _prepareParams(params, this.token)
        if (data && data instanceof Object) {
            axios.post(url, data, config)
                .then(res => _success(fnSuccess, fnError, res))
                .catch(err => _error(fnError, err))
        } else {
            throw new Error('The post data is null or not a Object.')
        }
    }

    put(params) {
        let {url, data, config, fnSuccess, fnError} = _prepareParams(params, this.token)
        if (data && data instanceof Object) {
            axios.put(url, data, config)
                .then(res => _success(fnSuccess, fnError, res))
                .catch(err => _error(fnError, err))
        } else {
            throw new Error('The put data is null or not a Object.')
        }
    }

    del(params) {
        let {url, config, fnSuccess, fnError} = _prepareParams(params, this.token)
        axios.delete(url, config)
            .then(res => _success(fnSuccess, fnError, res))
            .catch(err => _error(fnError, err))
    }

    head(params) {
        let {url, config, fnSuccess, fnError} = _prepareParams(params, this.token)
        axios.head(url, config)
            .then(res => _success(fnSuccess, fnError, res))
            .catch(err => _error(fnError, err))
    }

    patch(params) {
        let {url, data, config, fnSuccess, fnError} = _prepareParams(params, this.token)
        axios.patch(url, data, config)
            .then(res => _success(fnSuccess, fnError, res))
            .catch(err => _error(fnError, err))
    }
}

export default AjaxAxios
