import {format} from '../../../src/index'

describe('format', () => {
    it('format for string', () => {
        expect(format.strFormat('This is a test message.')).toBe('This is a test message.')
    })
    it('format for string parameter', () => {
        expect(format.strFormat('This is a test message, a: %s, b: %s.', 'a', 'b'))
            .toBe('This is a test message, a: a, b: b.')
    })
    it('format for int parameter', () => {
        expect(format.strFormat('This is a test message, a: %d, b: %d.', 10, 22))
            .toBe('This is a test message, a: 10, b: 22.')
    })
    it('format for json parameter', () => {
        let json = {a: 'a', b: 12}
        expect(format.strFormat('This is a test message, json: %j.', json))
            .toBe('This is a test message, json: {"a":"a","b":12}.')
    })
    it('format for %', () => {
        expect(format.strFormat('This is a test message, %.'))
            .toBe('This is a test message, %.')
    })
    it('format for mixture', () => {
        let json = {a: 'a', b: 22}
        expect(format.strFormat('This is a test message, a: %s, b: %d, json: %j, %%.', 'a', 12, json))
            .toBe('This is a test message, a: a, b: 12, json: {"a":"a","b":22}, %.')
    })
})