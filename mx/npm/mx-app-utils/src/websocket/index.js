import {logger} from '../index'

const CONNECTING = 0
const OPEN = 1
const CLOSING = 2
const CLOSED = 3

const initClient = function (uri, cbConfig) {
    let ws = new WebSocket(uri)
    if (!cbConfig || !(cbConfig instanceof Object)) {
        cbConfig = {}
    }
    let {afterConnect, beforeClose, hasError, receiveMessage} = cbConfig
    ws.onopen = function (event) {
        logger.debug('On open.')
        if (afterConnect && typeof afterConnect === 'function') {
            afterConnect()
        } else {
            logger.debug('No open callback function.')
        }
    }
    ws.onclose = function (event) {
        let {code, reason} = event
        logger.debug('On close, code: %d, reason: %s.', event)
        if (beforeClose && typeof beforeClose === 'function') {
            beforeClose({code, reason})
        } else {
            logger.debug('No close callback function.')
        }
    }
    ws.onerror = function (event) {
        logger.debug('On error.')
        if (hasError && typeof hasError === 'function') {
            hasError()
        } else {
            logger.debug('No error callback function.')
        }
    }
    ws.onmessage = function (event) {
        logger.debug('On message, data: %s.', event.data)
        if (receiveMessage && typeof receiveMessage === 'function') {
            receiveMessage(event.data)
        } else {
            logger.debug('No message callback function.')
        }
    }
    return ws
}

const createWsClient = function (uri, reconnected, cbConfig) {
    let myWebSocket = {}
    myWebSocket.uri = uri
    myWebSocket.reconnected = reconnected
    myWebSocket.cbConfig = cbConfig
    myWebSocket.ws = initClient(uri, cbConfig)
    myWebSocket.state = function () {
        let ws = myWebSocket.ws
        if (ws && ws.readyState >= 0) {
            return ws.readyState
        } else {
            return -1
        }
    }
    myWebSocket.close = function () {
        myWebSocket.reconnected = false
        if (myWebSocket.interval) {
            clearInterval(myWebSocket.interval)
        }
        let ws = myWebSocket.ws
        if (!ws) {
            return
        }
        if (ws.readyState === CLOSING || ws.readyState === CLOSED) {
            return
        }
        // close code必须是1000，或者介于3000-4999
        ws.close(1000, 'Client request close.')
        myWebSocket.ws = null
        logger.debug('Client close the session successfully.')
    }
    myWebSocket.send = function (json) {
        let ws = myWebSocket.ws
        if (ws.readyState === 1) {
            ws.send(JSON.stringify(json))
            logger.debug('Send message successfully, message: %j.', json)
        } else {
            logger.debug('The WebSocket\' state is not OPEN, state: %d.', ws.readyState)
        }
    }
    myWebSocket.reconnect = function () {
        let {uri, ws, reconnected, cbConfig} = myWebSocket
        if (ws && (ws.readyState === CONNECTING || ws.readyState === OPEN)) {
            return
        } else if (reconnected) {
            myWebSocket.ws = initClient(uri, cbConfig)
            logger.debug('reconnect websocket server successfully.')
        }
    }
    setTimeout(() => {
        myWebSocket.interval = setInterval(() => {
            myWebSocket.reconnect()
        }, 15000)
    }, 1000)
    return myWebSocket
}

export default createWsClient
