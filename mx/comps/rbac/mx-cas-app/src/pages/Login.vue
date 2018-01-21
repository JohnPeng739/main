<style>
  .pane-buttons {
    margin-top: 20px;
  }
</style>

<template>
  <v-content>
    <v-container fluid>
      <v-layout justify-center align-center>
        <v-flex xs12 sm8 md6 lg4 xl2>
          <v-card>
            <v-toolbar color="green">
              <v-toolbar-title>Login page</v-toolbar-title>
            </v-toolbar>
            <v-card-text>
              <v-form v-model="valid" ref="formLogin" class="form">
                <v-icon x-large>account_box</v-icon>
                <v-text-field label="Account code" v-model="accountCode" :rules="accountCodeRules" :counter="30"
                              required></v-text-field>
                <v-icon x-large>lock</v-icon>
                <v-text-field :type="showPassword ? 'text' : 'password'" label="Password" v-model="password"
                              :rules="passwordRules" :counter="30" required
                              :append-icon="showPassword ? 'visibility' : 'visibility_off'"
                              :append-icon-cb="() => showPassword = !showPassword"></v-text-field>
                <v-checkbox v-model="forcedReplace" label="强行登录"></v-checkbox>
              </v-form>
              <v-layout justify-center align-center class="pane-buttons">
                <v-spacer></v-spacer>
                <v-btn @click.native="handleReset">{{$t('buttons.reset')}}</v-btn>
                <v-spacer></v-spacer>
                <v-btn @click.native="handleLogin" color="primary">{{$t('buttons.login')}}</v-btn>
                <v-spacer></v-spacer>
              </v-layout>
            </v-card-text>
          </v-card>
        </v-flex>
        <message ref="message"></message>
      </v-layout>
    </v-container>
  </v-content>
</template>

<script>
  import {ajax, logger} from 'mx-app-utils'
  import Message from '@/components/Message.vue'

  export default {
    components: {Message},
    data() {
      return {
        valid: false,
        accountCode: '',
        accountCodeRules: [
          v => !!v || 'Account code is required.',
          v => v && v.length <= 30 || 'Accont code must be less than 30 characters.',
          v => v && v.length >= 3 || 'Account code must be more than 3 characters.'
        ],
        password: '',
        passwordRules: [
          v => !!v || 'Password is required.',
          v => v && v.length <= 30 || 'Password must be less than 30 characters.',
          v => v && v.length >= 6 || 'Password must be more than 6 characters.'
        ],
        showPassword: false,
        forcedReplace: false
      }
    },
    methods: {
      handleLogin() {
        if (this.$refs.formLogin.validate()) {
          let {accountCode, password, forcedReplace} = this
          ajax.post('/rest/accounts/login', {accountCode, password, forcedReplace}, data => {
            let {token} = data
            ajax.setToken(token)
            this.$router.push('/')
            this.$refs.message.success('登录成功')
          }, err => {
            this.$refs.message.error(err)
          })
        } else {
          this.$refs.message.warning(this.$t('warning.formValidate', {module: this.$t('buttons.login')}))
        }
      },
      handleReset() {
        this.showPassword = false
        this.$refs.formLogin.reset()
      }
    },
    mounted() {
      console.log(this.$route.query.originTo)
    }
  }
</script>

