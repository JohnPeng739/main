<template>
  <div>
    <el-form ref="formLogin" :model="formLogin" :rules="rulesLogin" label-width="130px">
      <el-form-item prop="code" label="Code">
        <el-input v-model="formLogin.code"></el-input>
      </el-form-item>
      <el-form-item prop="password" label="Password">
        <el-input type="password" v-model="formLogin.password"></el-input>
      </el-form-item>
      <el-form-item prop="forced" label="Forced replace">
        <el-switch v-model="formLogin.forced"></el-switch>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleReset">重置</el-button>
        <el-button @click="handleLogin">登入</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
  import { mapActions } from 'vuex'
  import { formatter } from 'mx-app-utils'
  import { MxFormValidateRules, MxNotify } from 'mx-vue-el-utils'

  export default {
    name: 'page-login',
    data () {
      return {
        formLogin: {code: '', password: '', forced: false},
        rulesLogin: {
          code: [MxFormValidateRules.requiredRule()],
          password: [MxFormValidateRules.requiredRule()]
        }
      }
    },
    methods: {
      ...mapActions(['login']),
      handleLogin () {
        this.$refs['formLogin'].validate(valid => {
          if (valid) {
            let {code, password, forced} = this.formLogin
            let success = (data) => {
              if (data && data.account) {
                MxNotify.info(formatter.formatArgs('Account[%s] login successfully.', data.account.code))
                this.$router.push('/')
              }
            }
            this.login({code, password, forced, success})
          } else {
            MxNotify.formValidateWarn()
          }
        })
      },
      handleReset () {
        this.$refs['formLogin'].resetFields()
      }
    }
  }
</script>
