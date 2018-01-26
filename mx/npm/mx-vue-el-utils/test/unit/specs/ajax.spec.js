import Mock from 'mockjs'
// import ajax from '../../../src/utils/ajax'
import { MxAjax } from '../../../dist/mx-vue-el-utils.min'

let pagination = {total: 100, size: 20, page: 3}
let checked = {a: 'a', b: 'b', c: 123, d: false}
let errorMsg = 'This is a test error.'

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
    MxAjax.get('/rest/get/success', fnSuccess)
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
    MxAjax.get('/rest/get/error', fnSuccess, fnError)
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
    MxAjax.post('/rest/post', checked, fnSuccess)
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
    MxAjax.put('/rest/put', checked, fnSuccess)
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
    MxAjax.del('/rest/delete', fnSuccess)
  })
})
