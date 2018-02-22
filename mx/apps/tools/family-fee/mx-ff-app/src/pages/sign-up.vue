<template>
  <div class="layout-login">
    <div class="layout-title">
      <span class="title">
        {{$t('pages.login.title.signUp')}}
      </span>
    </div>
    <el-form ref="formSign" :model="formSign" :rules="rulesSign" label-width="130px">
      <el-form-item prop="code" :label="$t('pages.login.code')">
        <el-input ref="inputCode" autofocus v-model="formSign.code"></el-input>
      </el-form-item>
      <el-form-item prop="name" :label="$t('pages.login.name')">
        <el-input v-model="formSign.name"></el-input>
      </el-form-item>
      <el-form-item prop="password" :label="$t('pages.login.password')">
        <mx-password v-model="formSign.password"></mx-password>
      </el-form-item>
      <el-form-item prop="confirm" :label="$t('pages.login.confirm')">
        <mx-password v-model="formSign.confirm"></mx-password>
      </el-form-item>
      <el-form-item>
        <div class="login-buttons">
          <el-button class="button reset" @click="handleReset">{{$t('button.reset')}}</el-button>
          <el-button class="button login" @click="handleClickOK">{{$t('button.ok')}}</el-button>
          <el-button class="text-button" type="text" size="mini" @click="$router.push('/login')" plain>
            {{$t('pages.login.signIn')}}
          </el-button>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
  import { logger } from 'mx-app-utils'
  import { MxFormValidateRules, MxNotify, MxAjax } from 'mx-vue-el-utils'

  export default {
    data () {
      let passwordMatchValidator = (rule, value, cb) => {
        let {password, confirm} = this.formSign
        if (password && confirm && password === confirm) {
          cb()
        } else {
          cb(new Error(this.$t('pages.login.message.passwordNotMatch')))
        }
      }
      let formValidateTips = (field) => {
        return this.$t('pages.common.message.form.required', [this.$t('pages.login.' + field)])
      }
      return {
        signUp: false,
        formSign: {
          code: '',
          name: '',
          password: '',
          confirm: ''
        },
        rulesSign: {
          code: [MxFormValidateRules.requiredRule({msg: formValidateTips('code')})],
          name: [MxFormValidateRules.requiredRule({msg: formValidateTips('name')})],
          password: [MxFormValidateRules.requiredRule({msg: formValidateTips('password')})],
          confirm: [MxFormValidateRules.requiredRule({msg: formValidateTips('confirm')}),
            MxFormValidateRules.customRule({validator: passwordMatchValidator})]
        }
      }
    },
    methods: {
      handleClickOK () {
        this.$refs['formSign'].validate(valid => {
          if (valid) {
            let {code, name, password} = this.formSign
            let url = '/rest/signUp'
            logger.debug('send POST "%s"', url)
            let fnSuccess = (data) => {
              MxNotify.info(this.$t('pages.login.message.signUpSuccess', [data.account.name]))
              this.$router.push('/')
            }
            MxAjax.post({url, data: {code, name, password}, fnSuccess})
          } else {
            MxNotify.formValidateWarn()
          }
        })
      },
      handleReset () {
        this.$refs['formSign'].resetFields()
      }
    }
  }
</script>

<style rel="stylesheet/less" lang="less" scoped>
  @import "../assets/style/base.less";

  .layout-login {
    width: 50%;
    margin: 15% auto;
    font-size: @content-text-font-size;
    .layout-title {
      text-align: center;
      margin-bottom: 6vh;
      .title {
        margin-left: 5vw;
        font-size: @content-text-font-size * 2;
        font-weight: 600;
      }
    }
    .login-buttons {
      text-align: center;
      .text-button {
        min-height: 0.5vh;
        vertical-align: bottom;
        padding: 0 1vw;
        color: green;
        font-size: @content-text-font-size * 0.6;
        &:hover, &:active {
          border: none;
        }
      }
      .reset {
        margin-right: 1vw;
      }
      .login {
        margin-left: 1vw;
      }
    }
  }
</style>
