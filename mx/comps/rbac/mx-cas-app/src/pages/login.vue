<template>
  <div class="layout-login">
    <div class="layout-title">
      <span class="login-title">{{$t('pages.login.name')}}</span>
    </div>
    <el-form ref="formLogin" :model="formLogin" :rules="rulesLogin" label-width="130px">
      <el-form-item prop="code" :label="$t('pages.login.code')">
        <el-input v-model="formLogin.code"></el-input>
      </el-form-item>
      <el-form-item prop="password" :label="$t('pages.login.password')">
        <el-input type="password" v-model="formLogin.password"></el-input>
      </el-form-item>
      <el-form-item prop="forced" :label="$t('pages.login.forcedReplace')">
        <el-switch v-model="formLogin.forced"></el-switch>
      </el-form-item>
      <el-form-item>
        <div class="login-buttons">
          <el-button class="button reset" @click="handleReset">{{$t('button.reset')}}</el-button>
          <el-button class="button login" @click="handleLogin">{{$t('button.ok')}}</el-button>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
  import {mapActions} from 'vuex'
  import {MxFormValidateRules, MxNotify} from 'mx-vue-el-utils'

  export default {
    name: 'page-login',
    data() {
      return {
        formLogin: {code: '', password: '', forced: false},
        rulesLogin: {
          code: [MxFormValidateRules.requiredRule()],
          password: [MxFormValidateRules.requiredRule()]
        }
      }
    },
    methods: {
      ...mapActions({
        login: 'login'
      }),
      handleLogin() {
        this.$refs['formLogin'].validate(valid => {
          if (valid) {
            let {code, password, forced} = this.formLogin
            let success = (data) => {
              if (data && data.account) {
                let {token} = data
                let {id, code, name, favorityTools, roles} = data.account
                let roleCodes = []
                if (roles && roles instanceof Array && roles.length > 0) {
                  roles.forEach(role => roleCodes.push(role.code))
                }
                let authUser = {id, code, name, token, favorityTools, roles: roleCodes}
                sessionStorage.setItem('auth.user', JSON.stringify(authUser))
                sessionStorage.setItem('auth.time', new Date().getTime())
                MxNotify.info(this.$t('rbac.account.message.loginSuccess', {code, name}))
                this.$router.push('/')
              }
            }
            this.login({code, password, forced, success})
          } else {
            MxNotify.formValidateWarn()
          }
        })
      },
      handleReset() {
        this.$refs['formLogin'].resetFields()
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
      .login-title {
        margin-left: 5vw;
        font-size: 4vw;
        font-weight: 600;
      }
    }
    .login-buttons {
      text-align: center;
      .reset {
        margin-right: 3vw;
      }
      .login {
        margin-left: 3vw;
      }
    }
  }
</style>
