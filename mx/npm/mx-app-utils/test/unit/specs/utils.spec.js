import {round, mixin, clone, cloneData} from '../../../src/index'
// import {round, mixin, clone} from '../../../dist/mx-app-utils.min'

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
    it('test mixin', () => {
        let src1 = {a: 'aaaa', b: {b1: 'b1'}, d: ['a1', 'a2']}
        let src2 = {}
        expect(src2.a).toBeUndefined()
        expect(src2.b).toBeUndefined()
        mixin(src2, src1)
        expect(src2.a).toBe('aaaa')
        expect(src2.b.b1).toBe('b1')
        src2 = {a: 'a123', b: {b2: 'b2'}, c: 'ccc'}
        expect(src2.a).toBe('a123')
        expect(src2.b.b2).toBe('b2')
        expect(src2.c).toBe('ccc')
        mixin(src2, src1)
        expect(src2.a).toBe('aaaa')
        expect(src2.b.b1).toBe('b1')
        expect(src2.b.b2).toBe('b2')
        expect(src2.c).toBe('ccc')
        expect(src2.d.length).toBe(2)
        expect(src2.d[0]).toBe('a1')
        expect(src2.d[1]).toBe('a2')
        let src3 = {d: ['b1', 'b2']}
        mixin(src2, src3)
        expect(src2.a).toBe('aaaa')
        expect(src2.b.b1).toBe('b1')
        expect(src2.b.b2).toBe('b2')
        expect(src2.c).toBe('ccc')
        expect(src2.d.length).toBe(4)
        expect(src2.d[0]).toBe('a1')
        expect(src2.d[1]).toBe('a2')
        expect(src2.d[2]).toBe('b1')
        expect(src2.d[3]).toBe('b2')
    })
    it('test clone', () => {
        let src = {
            code: 'code',
            age: 10,
            date: new Date(),
            members: [{
                code: 'key1',
                age: 4
            }, {
                code: 'key2',
                age:5
            }]
        }
        let tar = clone(src)
        expect(src.code).toBe(tar.code)
        expect(src.age).toBe(tar.age)
        expect(src.date.getTime()).toBe(tar.date.getTime())
        expect(src.members.length).toBe(tar.members.length)
        expect(src.members[0].code).toBe(tar.members[0].code)
        expect(src.members[0].age).toBe(tar.members[0].age)
        expect(src.members[1].code).toBe(tar.members[1].code)
        expect(src.members[1].age).toBe(tar.members[1].age)

        delete tar.code
        delete tar.members
        expect(src.code).toBe('code')
        expect(src.members.length).toBe(2)
        expect(src.members[0].code).toBe('key1')
        expect(src.members[0].age).toBe(4)
        expect(src.members[1].code).toBe('key2')
        expect(src.members[1].age).toBe(5)
        expect(tar.code).toBe(undefined)
        expect(tar.members).toBe(undefined)
    })
    it('test clone data', () => {
        let src = {
            code: 'code',
            age: 10,
            date: new Date().getTime(),
            members: [{
                code: 'key1',
                age: 4
            }, {
                code: 'key2',
                age:5
            }]
        }
        let tar = cloneData(src)
        expect(src.code).toBe(tar.code)
        expect(src.age).toBe(tar.age)
        expect(src.date).toBe(tar.date)
        expect(src.members.length).toBe(tar.members.length)
        expect(src.members[0].code).toBe(tar.members[0].code)
        expect(src.members[0].age).toBe(tar.members[0].age)
        expect(src.members[1].code).toBe(tar.members[1].code)
        expect(src.members[1].age).toBe(tar.members[1].age)

        delete tar.code
        delete tar.members
        expect(src.code).toBe('code')
        expect(src.members.length).toBe(2)
        expect(src.members[0].code).toBe('key1')
        expect(src.members[0].age).toBe(4)
        expect(src.members[1].code).toBe('key2')
        expect(src.members[1].age).toBe(5)
        expect(tar.code).toBe(undefined)
        expect(tar.members).toBe(undefined)
    })
})