/**
 * Created by john.peng on 2017/6/9.
 *
 */
import axios from 'axios'

axios.defaults.headers.common['Content-Type'] = 'application/json'

const _fnSuccess = (success, res, error) => {
    let data = res.data
    let pagination = null
    if (data.errorCode && data.errorCode !== 0) {
        _fnError(error, data.errorMessage)
        return
    }
    if (data.pagination) {
        pagination = data.pagination
    }
    if (data.data) {
        data = data.data
    }
    if (success && typeof success === 'function') {
        if (pagination) {
            success(pagination, data)
        } else {
            success(data)
        }
    }
}

const _fnError = (error, err) => {
    if (error && typeof error === 'function') {
        error(err)
    }
}

const _fnPrepareParams = (params) => {
    if (!params || !params instanceof Object) {
        throw new Error('The ajax parameters object is invalid.')
    }
    let {url} = params
    if (!url || typeof url !== 'string' || url.length <= 0) {
        throw new Error('The ajax url is invalid.')
    }
    return params
}

export default {
    setToken: token => {
        if (token && typeof token === 'string') {
            let prefix = 'Bearer '
            if (token.search(prefix) !== 0) {
                token = prefix + token
            }
            axios.defaults.headers.common['Authorization'] = token
        }
    },
    getToken: () => axios.defaults.headers.common['Authorization'],
    get: (params) => {
        let {url, config, fnSuccess, fnError} = _fnPrepareParams(params)
        axios.get(url, config)
            .then(res => _fnSuccess(fnSuccess, res, fnError))
            .catch(err => _fnError(fnError, err))
    },

    post: (params) => {
        let {url, data, config, fnSuccess, fnError} = _fnPrepareParams(params)
        axios.post(url, data, config)
            .then(res => _fnSuccess(fnSuccess, res, fnError))
            .catch(err => _fnError(fnError, err))
    },

    put: (params) => {
        let {url, data, config, fnSuccess, fnError} = _fnPrepareParams(params)
        axios.put(url, data, config)
            .then(res => _fnSuccess(fnSuccess, res, fnError))
            .catch(err => _fnError(fnError, err))
    },
    del: (params) => {
        let {url, config, fnSuccess, fnError} = _fnPrepareParams(params)
        axios.delete(url, config)
            .then(res => _fnSuccess(fnSuccess, res, fnError))
            .catch(err => _fnError(fnError, err))
    }
}
