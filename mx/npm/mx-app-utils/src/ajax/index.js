/**
 * Created by john.peng on 2017/6/9.
 *
 */
import axios from 'axios'

let fnSuccess = (success, res, error) => {
    let data = res.data
    let pagination = null
    if (data.errorCode && data.errorCode !== 0) {
        fnError(error, data.errorMessage)
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

let fnError = (error, err) => {
    if (error && typeof error === 'function') {
        error(err)
    }
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
    get: (url, success, error) => {
        axios.get(url)
            .then(res => fnSuccess(success, res, error))
            .catch(err => fnError(error, err))
    },

    post: (url, data, success, error) => {
        axios.post(url, data)
            .then(res => fnSuccess(success, res, error))
            .catch(err => fnError(error, err))
    },

    put: (url, data, success, error) => {
        axios.put(url, data)
            .then(res => fnSuccess(success, res, error))
            .catch(err => fnError(error, err))
    },
    del: (url, success, error) => {
        axios.delete(url)
            .then(res => fnSuccess(success, res, error))
            .catch(err => fnError(error, err))
    }
}
