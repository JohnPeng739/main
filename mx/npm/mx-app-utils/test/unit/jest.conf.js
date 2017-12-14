const path = require('path')

module.exports = {
    rootDir: path.resolve(__dirname, '../../'),
    moduleFileExtensions: ['js'],
    // moduleNameMapper: {
    //    '^@/(.*)$': '<rootDir>/src/$1'
    // },
    transform: {
        '^.+\\.js$': '<rootDir>/node_modules/babel-jest'
    },
    testPathIgnorePatterns: ['<rootDir>/test/e2e'],
    // setupFiles: ['<rootDir>/test/unit/setup'],
    mapConverage: true,
    coverageDirectory: '<rootDir>/test/unit/coverage',
    collectCoverageFrom: ['src/**/*.js', '!src/index.js', '!**/node_modules/**']
}