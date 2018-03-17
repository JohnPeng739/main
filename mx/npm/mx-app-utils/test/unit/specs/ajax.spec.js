import Mock from 'mockjs'

import {ajax} from '../../../src/index'
import AjaxAxios from '../../../src/ajax/ajax-axios'

// import {ajax} from '../../../dist/mx-app-utils.min'

let pagination = {total: 100, size: 20, page: 3}
let checked = {a: 'a', b: 'b', c: 123, d: false}
let errorMsg = "This is a test error."

beforeEach(() => {
    Mock.mock('/rest/get/success', 'get', {errorCode: 0, data: checked})
    Mock.mock('/rest/get/error', 'get', {errorCode: 1, errorMessage: errorMsg})
    Mock.mock('/rest/post', 'post', {errorCode: 0, errorMessage: '', pagination: pagination, data: checked})
    Mock.mock('/rest/put', 'put', {errorCode: 0, data: checked})
    Mock.mock('/rest/delete', 'delete', {errorCode: 0, data: checked})
})

describe('test ajax', () => {
    it('test get success method', (done) => {
        let fnSuccess = jest.fn(data => {
            expect(data).toBeDefined()
            expect(data.a).toBe(checked.a)
            expect(data.b).toBe(checked.b)
            expect(data.c).toBe(checked.c)
            expect(data.d).toBe(checked.d)
            done()
        })
        let fnError = jest.fn(err => {
            console.log(err)
            expect(1).toBe(0)
            done()
        })
        let token = 'abcdefg'
        ajax.setToken(token)
        expect(ajax.getToken()).toBe('Bearer ' + token)
        token = 'Bearer ' + 'abcdefg'
        ajax.setToken(token)
        expect(ajax.getToken()).toBe(token)
        ajax.get({url: '/rest/get/success', fnSuccess, fnError})
    })
    it('test get error method', (done) => {
        let fnSuccess = jest.fn(data => {
            expect(1).toBe(0)
            done()
        })
        let fnError = jest.fn(err => {
            expect(err).toBeDefined()
            expect(err).toBe(errorMsg)
            done()
        })
        ajax.get({url: '/rest/get/error', fnSuccess, fnError})
    })
    it('test post method', (done) => {
        let fnSuccess = jest.fn((pagination, data) => {
            expect(pagination).toBeDefined()
            expect(pagination.total).toBe(100)
            expect(pagination.size).toBe(20)
            expect(pagination.page).toBe(3)
            expect(data).toBeDefined()
            expect(data.a).toBe(checked.a)
            expect(data.b).toBe(checked.b)
            expect(data.c).toBe(checked.c)
            expect(data.d).toBe(checked.d)
            done()
        })
        let fnError = jest.fn(err => {
            expect(1).toBe(0)
            done()
        })
        ajax.post({url: '/rest/post', data: checked, fnSuccess, fnError})
    })
    it('test put method', (done) => {
        let fnSuccess = jest.fn(data => {
            expect(data).toBeDefined()
            expect(data.a).toBe(checked.a)
            expect(data.b).toBe(checked.b)
            expect(data.c).toBe(checked.c)
            expect(data.d).toBe(checked.d)
            done()
        })
        let fnError = jest.fn(err => {
            expect(1).toBe(0)
            done()
        })
        ajax.put({url: '/rest/put', data: checked, fnSuccess, fnError})
    })
    it('test del method', (done) => {
        let fnSuccess = jest.fn(data => {
            expect(data).toBeDefined()
            expect(data.a).toBe(checked.a)
            expect(data.b).toBe(checked.b)
            expect(data.c).toBe(checked.c)
            expect(data.d).toBe(checked.d)
            done()
        })
        let fnError = jest.fn(err => {
            console.log(err)
            expect(1).toBe(0)
            done()
        })
        ajax.del({url: '/rest/delete', fnSuccess, fnError})
    })
})

describe('test the ajax 2', () => {
    it('test class', () => {
        let ajax1 = new AjaxAxios()
        expect(ajax1.token).toBeUndefined()

        let ajax2 = new AjaxAxios('my token')
        expect(ajax2.token).toBe('Bearer my token')
    })
    let ajax1 = new AjaxAxios('my token')
    it('test get success method', (done) => {
        let fnSuccess = jest.fn(data => {
            expect(data).toBeDefined()
            expect(data.a).toBe(checked.a)
            expect(data.b).toBe(checked.b)
            expect(data.c).toBe(checked.c)
            expect(data.d).toBe(checked.d)
            done()
        })
        let fnError = jest.fn(err => {
            console.log(err)
            expect(1).toBe(0)
            done()
        })
        expect(ajax1.token).toBe('Bearer my token')
        ajax1.get({url: '/rest/get/success', fnSuccess, fnError})
    })
    it('test get error method', (done) => {
        let fnSuccess = jest.fn(data => {
            expect(1).toBe(0)
            done()
        })
        let fnError = jest.fn(err => {
            expect(err).toBeDefined()
            expect(err).toBe(errorMsg)
            done()
        })
        ajax1.get({url: '/rest/get/error', fnSuccess, fnError})
    })
    it('test post method', (done) => {
        let fnSuccess = jest.fn((pagination, data) => {
            expect(pagination).toBeDefined()
            expect(pagination.total).toBe(100)
            expect(pagination.size).toBe(20)
            expect(pagination.page).toBe(3)
            expect(data).toBeDefined()
            expect(data.a).toBe(checked.a)
            expect(data.b).toBe(checked.b)
            expect(data.c).toBe(checked.c)
            expect(data.d).toBe(checked.d)
            done()
        })
        let fnError = jest.fn(err => {
            expect(1).toBe(0)
            done()
        })
        ajax1.post({url: '/rest/post', data: checked, fnSuccess, fnError})
    })
    it('test put method', (done) => {
        let fnSuccess = jest.fn(data => {
            expect(data).toBeDefined()
            expect(data.a).toBe(checked.a)
            expect(data.b).toBe(checked.b)
            expect(data.c).toBe(checked.c)
            expect(data.d).toBe(checked.d)
            done()
        })
        let fnError = jest.fn(err => {
            expect(1).toBe(0)
            done()
        })
        ajax1.put({url: '/rest/put', data: checked, fnSuccess, fnError})
    })
    it('test del method', (done) => {
        let fnSuccess = jest.fn(data => {
            expect(data).toBeDefined()
            expect(data.a).toBe(checked.a)
            expect(data.b).toBe(checked.b)
            expect(data.c).toBe(checked.c)
            expect(data.d).toBe(checked.d)
            done()
        })
        let fnError = jest.fn(err => {
            console.log(err)
            expect(1).toBe(0)
            done()
        })
        ajax1.del({url: '/rest/delete', fnSuccess, fnError})
    })
})