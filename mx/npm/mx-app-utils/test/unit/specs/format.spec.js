import formatter from '../../../src/formatter'

describe('formatter default', () => {
    it('formatter for string', () => {
        expect(formatter.format('This is a test message.')).toBe('This is a test message.')
    })
    it('formatter for string parameter', () => {
        expect(formatter.format('This is a test message, a: %s, b: %s.', 'a', 'b'))
            .toBe('This is a test message, a: a, b: b.')
    })
    it('formatter for int parameter', () => {
        expect(formatter.format('This is a test message, a: %d, b: %d.', 10, 22))
            .toBe('This is a test message, a: 10, b: 22.')
    })
    it('formatter for json parameter', () => {
        let json = {a: 'a', b: 12}
        expect(formatter.format('This is a test message, json: %j.', json))
            .toBe('This is a test message, json: {"a":"a","b":12}.')
    })
    it('formatter for %', () => {
        expect(formatter.format('This is a test message, %.'))
            .toBe('This is a test message, %.')
    })
    it('formatter for mixture', () => {
        let json = {a: 'a', b: 22}
        expect(formatter.format('This is a test message, a: %s, b: %d, json: %j, %%.', 'a', 12, json))
            .toBe('This is a test message, a: a, b: 12, json: {"a":"a","b":22}, %.')
    })
})

let longDatetime = 1513317489005
describe('formatter date', () => {
    it('formatter date', () => {
        expect(formatter.formatDate(longDatetime)).toBe('2017/12/15')
        expect(formatter.formatDatetime(longDatetime)).toBe('2017/12/15 13:58:09')
        expect(formatter.formatTimestamp(longDatetime)).toBe('2017/12/15 13:58:09.005')
    })
})

describe('format fixed length string', () => {
    it('format split', () => {
        expect(formatter.formatFixedLen(12345, 0)).toBe('')
        expect(formatter.formatFixedLen(12345, 1)).toBe('1')
        expect(formatter.formatFixedLen(12345, 2)).toBe('12')
        expect(formatter.formatFixedLen(12345, 3)).toBe('123')
        expect(formatter.formatFixedLen(12345, 5)).toBe('12345')
    })
    it('format fill', () => {
        expect(formatter.formatFixedLen(12, 2)).toBe('12')
        expect(formatter.formatFixedLen(12, 3)).toBe('012')
        expect(formatter.formatFixedLen(12, 4)).toBe('0012')
        expect(formatter.formatFixedLen(12, 5)).toBe('00012')
        expect(formatter.formatFixedLen(12, 6)).toBe('000012')
    })
})