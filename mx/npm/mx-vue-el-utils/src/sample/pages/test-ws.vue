<template>
  <div>
    <el-row type="flex" justify="center">
      <el-col :span="6">
        <el-input v-model="wsUri" placeholder="WebSocket URI"></el-input>
      </el-col>
      <el-col :span="6">
        <el-input v-model="srcIp" placeholder="tar ip"></el-input>
      </el-col>
      <el-col :span="6">
        <el-input v-model="deviceId" placeholder="Device ID"></el-input>
      </el-col>
      <el-col :span="6">
        <el-input v-model="deviceState" placeholder="Device state"></el-input>
      </el-col>
    </el-row>
    <el-row type="flex" justify="center">
      <el-col :span="6">
        {{state()}}
        <el-button @click="handleClose">关闭</el-button>
        <el-button @click="handleConnect">连接</el-button>
      </el-col>
    </el-row>
    <el-row type="flex" justify="center">
      <el-col :span="20">
        <el-input type="textarea" v-model="message" :rows="4"></el-input>
      </el-col>
      <el-col :span="4">
        <el-button @click="handleSendMessage">发送</el-button>
      </el-col>
    </el-row>
    <el-row v-for="item in items" :key="item" type="flex" justify="left">
      <el-col :span="24">
        {{item}}
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import { logger, WsClient } from 'mx-app-utils'

  export default {
    name: 'test-websocket-page',
    data () {
      return {
        wsUri: 'ws://192.168.0.248:9997/notify',
        myWebSocket: null,
        interval: null,
        srcIp: '::1',
        deviceId: '',
        deviceState: '',
        message: '',
        items: []
      }
    },
    methods: {
      state () {
        // return this.myWebSocket ? this.myWebSocket.state() : -1
        return this.myWebSocket ? this.myWebSocket.state : -1
      },
      send (json) {
        if (this.myWebSocket) {
          this.myWebSocket.send(json)
        }
      },
      handleConnect () {
        let receiveMessage = message => {
          if (typeof message === 'string') {
            message = message + ', time: ' + new Date()
            logger.debug('Receive a text message: %s.', message)
            this.items.unshift(message)
          } else {
            let reader = new FileReader()
            logger.debug('Receive a blob message: %s.', reader.readAsBinaryString(message))
            reader.close()
          }
        }
        let device = {deviceId: this.deviceId, state: this.deviceState, longitude: 123.22, latitude: 33.123}
        let afterConnect = () => {
          this.send({command: 'registry', type: 'system', data: device})
          logger.debug('Send registry command.')
        }
        // this.myWebSocket = createWsClient(this.wsUri, true, {receiveMessage, afterConnect})
        this.myWebSocket = new WsClient(this.wsUri, true, 3, {afterConnect, hasText: receiveMessage})
        this.myWebSocket.init()
        this.interval = setInterval(() => {
          device.longitude = Math.random() * 180
          device.latitude = Math.random() * 90
          this.send({command: 'ping', type: 'system', data: device})
        }, 5000)
      },
      handleClose () {
        if (this.interval) {
          clearInterval(this.interval)
        }
        if (this.myWebSocket) {
          logger.info('client close the session.')
          this.send({command: 'unregistry', type: 'system', data: {deviceId: this.deviceId}})
          this.myWebSocket.close()
          this.myWebSocket = null
        }
      },
      handleSendMessage () {
        console.log(this.srcIp)
        let notify = {
          src: this.deviceId,
          tarType: 'IPs',
          tar: this.srcIp,
          expiredTime: -1,
          needAck: true,
          message: {message: this.message}
        }
        this.send({command: 'notify', type: 'user', data: notify})
      }
    },
    destroyed () {
      this.handleClose()
    }
  }
</script>
