const path = require('path')
const CleanWebpackPlugin = require('clean-webpack-plugin')
const ManifestPlugin = require('webpack-manifest-plugin')

module.exports = {
    entry: {
        "mx-app-utils": path.resolve(__dirname, 'src/index.js'),
    },
    plugins: [
        new CleanWebpackPlugin(['dist']),
        new ManifestPlugin()
    ],
    output: {
        path: path.resolve(__dirname, 'dist'),
        filename: '[name].min.js',
        chunkFilename: '[id].min.js',
        libraryTarget: 'umd'
    }
}