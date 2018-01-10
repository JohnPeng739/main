import logger from '../logger'

function init (uri, receiveMessage, afterConnect, beforeClose, hasError) {
  let ws = new WebSocket(uri)
  ws.onopen = function (event) {
    console.log(event)
    if (afterConnect && typeof afterConnect === 'function') {
      afterConnect.apply(this, event)
    }
  }
  ws.onclose = function (event) {
    console.log(event)
    if (beforeClose && typeof beforeClose === 'function') {
      beforeClose.apply(this, event)
    }
  }
  ws.onerror = function (event) {
    console.log(event)
    if (hasError && typeof hasError === 'function') {
      hasError.apply(this, event)
    }
  }
  ws.onmessage = function (event) {
    if (receiveMessage && typeof receiveMessage === 'function') {
      receiveMessage(event.data)
    }
  }
  return ws
}

export default {
  createNew: function (uri, receiveMessage, afterConnect, beforeClose, hasError) {
    let myWebSocket = {}
    myWebSocket.ws = init(uri, receiveMessage, afterConnect, beforeClose, hasError)
    myWebSocket.state = function () {
      let ws = myWebSocket.ws
      if (ws && ws.readyState >= 0) {
        return ws.readyState
      } else {
        return -1
      }
    }
    myWebSocket.close = function () {
      if (this.state === 1) {
        this.ws.close(0)
        logger.debug('Client close the session successfully.')
      }
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
    return myWebSocket
  }
}
