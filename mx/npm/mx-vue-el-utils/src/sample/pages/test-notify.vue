<template>
  <div>
    <el-button @click="handleAjaxDefaultError">Ajax Default error</el-button>
    <el-button @click="handleWarnClick">Click for Warn</el-button>
    <el-button @click="handleErrorClick">Click for Error</el-button>
    <br/><br/>
    <el-input-number v-model="duration" :min="1000" :max="10000" :step="500"></el-input-number><span>毫秒</span>
    <el-button @click="handleInfoClick">Click for Info</el-button>
    <br/><br/>
    <el-radio-group v-model="confirmType">
      <el-radio-button label="none">无处理函数</el-radio-button>
      <el-radio-button label="ok">OK处理函数</el-radio-button>
      <el-radio-button label="cancel">CANCEL处理函数</el-radio-button>
      <el-radio-button label="both">OK和CANCEL函数</el-radio-button>
    </el-radio-group>
    <el-button @click="handleConfirmClick">Click for Confirm</el-button>
    <br/><br/>
    <el-input v-model="formValidateWarnMessage" style="width: 400px;"></el-input>
    <el-button @click="handleFormValidateWarnClick">Click for Form Validate Warn</el-button>
    <br/><br/>
    <el-date-picker v-model="date"></el-date-picker>
  </div>
</template>

<script>
  import {formatter} from 'mx-app-utils'
  import {MxNotify, MxAjax} from '../../../dist/mx-vue-el-utils.min'

  export default {
    name: 'test-notify-page',
    data () {
      return {
        duration: 3000,
        confirmType: 'none',
        formValidateWarnMessage: 'This is a test form validate warn message.',
        date: ''
      }
    },
    methods: {
      handleAjaxDefaultError () {
        MxAjax.get('/rest/abcd/none', data => {
          console.log(data)
        })
      },
      handleInfoClick () {
        let duration = this.duration
        MxNotify.info(formatter.format('This is a test information, duration: %d ms.', duration), duration)
      },
      handleWarnClick () {
        MxNotify.warn('This is a test warning message.')
      },
      handleErrorClick () {
        MxNotify.error('This is a test error message.')
      },
      handleConfirmClick () {
        let type = this.confirmType
        switch (type) {
          case 'none':
            MxNotify.confirm('This is a test confirm message.')
            break
          case 'ok':
            MxNotify.confirm('This is a test confirm message, with ok function.',
              () => MxNotify.info('You click the ok button.'))
            break
          case 'cancel':
            MxNotify.confirm('This is a test confirm message, with ok function.', null,
              () => MxNotify.info('You click the cancel button.'))
            break
          case 'both':
            MxNotify.confirm('This is a test confirm message, with ok function.',
              () => MxNotify.info('You click the ok button.'),
              () => MxNotify.info('You click the cancel button.'))
            break
        }
      },
      handleFormValidateWarnClick () {
        let msg = this.formValidateWarnMessage
        MxNotify.formValidateWarn(msg)
      }
    }
  }
</script>
