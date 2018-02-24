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
  import { MxFormValidateRules, MxNotify } from 'mx-vue-el-utils'

  export default {
    name: 'page-login',
    data () {
      return {
        formLogin: {code: '', password: '', forced: false},
        rulesLogin: {
          code: [MxFormValidateRules.requiredRule({msg: this.$t('message.validate.required', ['Code'])})],
          password: [MxFormValidateRules.requiredRule({msg: this.$t('message.validate.required', ['Password'])})]
        }
      }
    },
    methods: {
      ...mapActions({
        login: 'login'
      }),
      handleLogin () {
        this.$refs['formLogin'].validate(valid => {
          if (valid) {
            let {code, password, forced} = this.formLogin
            let success = (data) => {
              if (data && data.account) {
                let {id, code, name, favoriteTools, roles} = data.account
                let roleCodes = []
                if (roles && roles instanceof Array && roles.length > 0) {
                  roles.forEach(role => {
                    if (role && role.id && role.code) {
                      roleCodes.push(role.code)
                    }
                  })
                }
                roles = roleCodes
                sessionStorage.setItem('auth.user', JSON.stringify({id, code, name, favoriteTools, roles}))
                sessionStorage.setItem('auth.time', new Date().getTime())
                MxNotify.info(this.$t('pages.account.message.loginSuccess', [code, name]))
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
