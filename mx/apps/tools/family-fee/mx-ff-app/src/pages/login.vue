<template>
  <div class="layout-login">
    <div class="layout-title">
      <span class="title">
        {{$t('pages.' + (signUp ? 'signUp' : 'signIn') + '.name')}}
      </span>
    </div>
    <el-form ref="formSign" :model="formSign" :rules="rulesSign" label-width="130px">
      <el-form-item prop="code" :label="$t('pages.login.code')">
        <el-input v-model="formSign.code"></el-input>
      </el-form-item>
      <el-form-item v-if="signUp" prop="name" :label="$t('pages.login.name')">
        <el-input v-model="formSign.name"></el-input>
      </el-form-item>
      <el-form-item prop="password" :label="$t('pages.login.password')">
        <el-input type="password" v-model="formSign.password"></el-input>
      </el-form-item>
      <el-form-item v-if="signUp" prop="confirm" :label="$t('pages.login.confirm')">
        <el-input type="password" v-model="formSign.confirm"></el-input>
      </el-form-item>
      <el-form-item v-if="!signUp" prop="forced" :label="$t('pages.login.forcedReplace')">
        <el-switch v-model="formSign.forced"></el-switch>
      </el-form-item>
      <el-form-item>
        <div class="login-buttons">
          <el-button v-if="!signUp" class="text-button" type="text" @click="signUp = true">
            {{$t('pages.login.signIn')}}
          </el-button>
          <el-button class="button reset" @click="handleReset">{{$t('button.reset')}}</el-button>
          <el-button class="button login" @click="handleClickOK">{{$t('button.ok')}}</el-button>
          <el-button v-if="signUp" class="text-button" type="text" @click="signUp = false">
            {{$t('pages.login.signUp')}}
          </el-button>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
  import { mapActions } from 'vuex'
  import {logger} from 'mx-app-utils'
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
      return {
        signUp: false,
        formSign: {code: '', name: '', password: '', confirm: '', forced: false},
        rules: {
          code: [MxFormValidateRules.requiredRule()],
          name: [MxFormValidateRules.requiredRule()],
          password: [MxFormValidateRules.requiredRule()],
          confirm: [MxFormValidateRules.requiredRule(), MxFormValidateRules.customRule({validator: passwordMatchValidator()})]
        }
      }
    },
    computed: {
      rulesSign () {
        let {code, name, password, confirm} = this.rules
        return this.signUp ? {code, name, password, confirm} : {code, password}
      }
    },
    methods: {
      ...mapActions({
        login: 'login'
      }),
      handleClickOK () {
        this.$refs['formSign'].validate(valid => {
          if (valid) {
            let {code, name, password, forced} = this.formSign
            if (this.signUp) {
              let url = '/rest/signUp'
              logger.debug('send POST "%s"', url)
              MxAjax.post(url, {code, name, password}, data => {
                MxNotify.info(this.$t('pages.login.message.signUpSuccess'))
              })
            } else {
              let success = (data) => {
                if (data && data.account) {
                  let {token} = data
                  let {id, code, name, favoriteTools, roles} = data.account
                  let roleCodes = []
                  if (roles && roles instanceof Array && roles.length > 0) {
                    roles.forEach(role => roleCodes.push(role.code))
                  }
                  let authUser = {id, code, name, token, favoriteTools, roles: roleCodes}
                  sessionStorage.setItem('auth.user', JSON.stringify(authUser))
                  sessionStorage.setItem('auth.time', new Date().getTime())
                  MxNotify.info(this.$t('pages.login.message.signInSuccess', {code, name}))
                  this.$router.push('/')
                }
              }
              this.login({code, password, forced, success})
            }
          } else {
            MxNotify.formValidateWarn()
          }
        })
      },
      handleReset () {
        this.$refs['formSign'].resetField()
      }
    }
  }
</script>

<style rel="stylesheet/less" lang="less" scoped>
  .layout-login {
    width: 50%;
    margin: 15% auto;
    .layout-title {
      text-align: center;
      margin-bottom: 6vh;
      .title {
        margin-left: 5vw;
        font-size: 4vw;
        font-weight: 600;
      }
    }
    .login-buttons {
      text-align: center;
      .text-button {
        color: green;
      }
      .reset {
        margin-right: 3vw;
      }
      .login {
        margin-left: 3vw;
      }
    }
  }
</style>
