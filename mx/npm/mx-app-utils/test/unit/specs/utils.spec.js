// import {round} from '../../../src/index'
import {round} from '../../../dist/mx-app-utils.min'

describe('test utils', () => {
    it('test round', () => {
        expect(round(1)).toBe(1)
        expect(round(1.123)).toBe(1)
        expect(round(1.4121)).toBe(1)
        expect(round(1.456)).toBe(1)
        expect(round(1.52342)).toBe(2)
        expect(round(1.55)).toBe(2)
        expect(round(1.59)).toBe(2)

        expect(round(1, 1)).toBe(1.0)
        expect(round(1.123, 1)).toBe(1.1)
        expect(round(1.4121, 1)).toBe(1.4)
        expect(round(1.456, 1)).toBe(1.5)
        expect(round(1.52342, 1)).toBe(1.5)
        expect(round(1.55, 1)).toBe(1.6)
        expect(round(1.59, 1)).toBe(1.6)

        expect(round(1, 2)).toBe(1.00)
        expect(round(1.123, 2)).toBe(1.12)
        expect(round(1.4121, 2)).toBe(1.41)
        expect(round(1.456, 2)).toBe(1.46)
        expect(round(1.52342, 2)).toBe(1.52)
        expect(round(1.55, 2)).toBe(1.55)
        expect(round(1.59, 2)).toBe(1.59)
    })
})