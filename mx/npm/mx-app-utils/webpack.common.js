const path = require('path')
const CleanWebpackPlugin = require('clean-webpack-plugin')
const ManifestPlugin = require('webpack-manifest-plugin')

module.exports = {
    entry: {
        "index": path.resolve(__dirname, 'src/index.js'),
    },
    plugins: [
        new CleanWebpackPlugin(['dist']),
        new ManifestPlugin()
    ],
    output: {
        path: path.resolve(__dirname, 'dist'),
        filename: 'mx-app-utils.min.js',
        libraryTarget: 'umd'
    },
}