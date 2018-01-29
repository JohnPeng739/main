const merge = require('webpack-merge')
const UglifyJsPlugin = require('uglifyjs-webpack-plugin')
const common = require('./webpack.common')

module.exports = merge(common, {
    devtool: 'source-map',
    externals: {
        axios: {
            root: 'Axios',
            commonjs: 'axios',
            commonjs2: 'axios',
            amd: 'axios'
        },
        util: {
            root: 'Util',
            commonjs: 'util',
            commonjs2: 'util',
            amd: 'util'
        }
    },
    plugins: [
        new UglifyJsPlugin({
            sourceMap: true
        })
    ]
})