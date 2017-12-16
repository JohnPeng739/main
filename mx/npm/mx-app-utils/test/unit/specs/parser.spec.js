// import {parser} from '../../../src/index'
import {parser} from '../../../dist/mx-app-utils.min'

describe('test parser', () => {
    it('test parse date', () => {
        let dateStr ='2017/12/16 12:34:22.888'
        let date = parser.parseDate(dateStr)
        expect(date).toBeDefined()
        expect(date.getFullYear()).toBe(2017)
        expect(date.getMonth()).toBe(12 -1)
        expect(date.getDate()).toBe(16)

        date = parser.parseDate(dateStr, /\d{1,4}\/\d{1,2}\/\d{1,2} \d{1,2}:\d{1,2}:\d{1,2}/)
        expect(date).toBeDefined()
        expect(date.getFullYear()).toBe(2017)
        expect(date.getMonth()).toBe(12 -1)
        expect(date.getDate()).toBe(16)
        expect(date.getHours()).toBe(12)
        expect(date.getMinutes()).toBe(34)
        expect(date.getSeconds()).toBe(22)

        date = parser.parseDate(dateStr, /\d{1,4}\/\d{1,2}\/\d{1,2} \d{1,2}:\d{1,2}:\d{1,2}.\d{1,3}/)
        expect(date).toBeDefined()
        expect(date.getFullYear()).toBe(2017)
        expect(date.getMonth()).toBe(12 -1)
        expect(date.getDate()).toBe(16)
        expect(date.getHours()).toBe(12)
        expect(date.getMinutes()).toBe(34)
        expect(date.getSeconds()).toBe(22)
        expect(date.getMilliseconds()).toBe(888)
    })

    it('test parse json', () => {
        let jsonStr = '{"a": "a", "b": 123, "c": false, "d": 123.123456789123456789, "f": 1.123456789123456789}'
        let json = parser.parseJson(jsonStr)
        expect(json).toBeDefined()
        expect(json.a).toBe('a')
        expect(json.b).toBe(123)
        expect(json.c).toBeFalsy()
        expect(json.d + 0.1).toBeCloseTo(123.223457, 6)
        expect(json.d).toBeCloseTo(123.123, 3)
        expect(json.f + 0.1).toBeCloseTo(1.223456789, 9)
    })
})