const path = require('path')
const CleanWebpackPlugin = require('clean-webpack-plugin')
const ManifestPlugin = require('webpack-manifest-plugin')

module.exports = {
    entry: {
        "mx-app-utils": path.resolve(__dirname, 'src/index.js')
    },
    plugins: [
        new CleanWebpackPlugin(['dist']),
        new ManifestPlugin()
    ],
    output: {
        filename: '[name].bundle.js',
        path: path.resolve(__dirname, 'dist')
    }
}