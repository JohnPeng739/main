import Mock from 'mockjs'

import {ajax} from '../../../src/index'

let checked = {a: 'a', b: 'b', c: 123, d: false}
let errorMsg = "This is a test error."

beforeEach(() => {
    Mock.mock('/rest/get/success', 'get', {errorCode: 0, data: checked})
    Mock.mock('/rest/get/error', 'get', {errorCode: 1, errorMessage: errorMsg})
    Mock.mock('/rest/post', 'post', {errorCode: 0, data: checked})
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
            expect(1).toBe(0)
            done()
        })
        ajax.get('/rest/get/success', fnSuccess, fnError)
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
        ajax.get('/rest/get/error', fnSuccess, fnError)
    })
    it('test post method', (done) => {
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
        ajax.post('/rest/post', checked, fnSuccess, fnError)
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
        ajax.put('/rest/put', checked, fnSuccess, fnError)
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
        ajax.del('/rest/delete', fnSuccess, fnError)
    })
})