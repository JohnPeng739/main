import logger from '../logger'

const CONNECTING = 0
const OPEN = 1
const CLOSING = 2
const CLOSED = 3

const _prepare = function (url, cbConfig) {
    let ws = new WebSocket(uri)
    let {afterConnect, afterClose, hasError, hasText, hasBinary} = cbConfig

    ws.onopen = function (event) {
        logger.debug('WebSocket onopen.')
        if (afterConnect && typeof afterConnect === 'function') {
            afterConnect()
        }
    }
    ws.onclose = function (event) {
        let {code, reason} = event
        logger.debug('WebSocket onclose, code: %d, reason: %s.', code, reason)
        if (afterClose && typeof afterClose === 'function') {
            afterClose(code, reason)
        }
    }
    ws.onerror = function (event) {
        logger.debug('WebSocket onerror.')
        if (hasError && typeof hasError === 'function') {
            hasError()
        }
    }
    ws.onmessage = function (event) {
        if (typeof event.data === 'string') {
            // has text message
            logger.debug('WebSocket onmessage, text: %s.', event.data)
            if (hasText && typeof hasText === 'function') {
                hasText(event.data)
            }
        } else {
            // has binary message, blob or ArrayBuffer
            logger.debug('WebSocket onmessage, binary total: %d.', event.data.length)
            if (hasBinary && typeof hasBinary === 'function') {
                hasBinary(event.data)
            }
        }
    }

    return ws
}

const _send = function (ws, message) {
    if (typeof message === 'string') {
        ws.send(message)
        logger.debug('Send text message successfully, message: %s.', message)
    } else if (message instanceof Blob || message instanceof ArrayBuffer) {
        ws.send(message)
        logger.debug('Send binary message successfully, length: %d.', message.length)
    } else if (message instanceof Object) {
        ws.send(JSON.stringify(message))
        logger.debug('Send json object successfully, data: %j.', message)
    }
}

class WsClient {
    constructor(uri, reconnected, testCycleSecs, cbConfig) {
        this._uri = undefined
        this._reconnected = false
        this._cbConfig = {}
        this._ws = undefined
        this._reconnectedInterval = undefined
        this._testCycleSecs = 3 // 默认为每3秒钟进行一次重连检测

        this.uri = uri
        this.reconnected = reconnected
        this.testCycleSecs = testCycleSecs
        this.cbConfig = cbConfig
    }

    get uri() {
        return this._uri
    }

    set uri(uri) {
        if (uri && typeof uri === 'string' && uri.length > 0) {
            this._uri = uri
        } else {
            throw new Error('The uri of WebSocket is blank or not a string.')
        }
    }

    get reconnected() {
        return this._reconnected
    }

    set reconnected(reconnected) {
        if (reconnected !== undefined && typeof reconnected === 'boolean') {
            this._reconnected = reconnected
        }
    }

    get testCycleSecs() {
        return this._testCycleSecs
    }

    set testCycleSecs(testCycleSecs) {
        if (testCycleSecs > 0) {
            this._testCycleSecs = testCycleSecs
        }
    }

    set cbConfig(cbConfig) {
        if (cbConfig && cbConfig instanceof Object) {
            this._cbConfig = cbConfig
        } else {
            throw new Error('The callback config is null or not a Object.')
        }
    }

    get state() {
        let ws = this._ws
        if (ws && ws.readyState >= CONNECTING && ws.readyState <= CLOSED) {
            return ws.readyState
        } else {
            return -1
        }
    }

    init() {
        this._ws = _prepare(this.uri, this._cbConfig)
        if (this.reconnected && this.testCycleSecs > 0) {
            // 正常连接5秒钟之后开始重连检测
            setTimeout(() => {
                this._reconnectedInterval = setInterval(() => {
                    if (this.state === CLOSED) {
                        // 连接状态为关闭，需要重连操作
                        this._ws = _prepare(this._uri, this._cbConfig)
                        logger.debug('Reconnect successfully for the WsClient.')
                    }
                }, this.testCycleSecs * 1000)
            }, 5000)
        }
        logger.debug('Init the WsClient successfully.')
    }

    close() {
        // 手动关闭连接，不允许重连
        this.reconnected = false
        let reconnectedInterval = this._reconnectedInterval
        if (reconnectedInterval) {
            clearInterval(reconnectedInterval)
            this._reconnectedInterval = undefined
        }
        let state = this.state
        if (state === CONNECTING || state === OPEN) {
            // code必须是1000，表示正常退出；或者介于3000-4999，表示有异常情况。
            this._ws.close(1000, 'Client normal close request.')
            this._ws = undefined
        }
        logger.debug('Close the WsClient successfully.')
    }

    send(message, errorForReconnected) {
        let state = this.state
        if (state !== OPEN) {
            if (errorForReconnected) {
                // 进行重连后发送，仅进行一次重连尝试
                let cbConfig = this._cbConfig
                let afterConnect = function (event) {
                    if (cbConfig.afterConnect && typeof cbConfig.afterConnect === 'function') {
                        let fnPrev = cbConfig.afterConnect
                        fnPrev()
                    }
                    // 再次连接成功后发送
                    _send(this._ws, message)
                }
                cbConfig.afterConnect = afterConnect
                this._ws = _prepare(this.uri, cbConfig)
            } else {
                throw new Error('The WsClient is not open.')
            }
        } else {
            _send(this._ws, message)
        }
    }
}

export default WsClient
