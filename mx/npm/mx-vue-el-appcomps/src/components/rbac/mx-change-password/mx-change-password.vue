<template>
  <el-form ref="formPassword" slot="form" :model="formPassword" :rules="rulesPassword" label-width="140px"
           class="dialog-form">
    <el-row type="flex">
      <el-col :span="12">
        <el-form-item prop="code" :label="$t('common.code')">
          <el-input :value="accountCode" :readonly="true"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item prop="name" :label="$t('common.name')">
          <el-input :value="accountName" :readonly="true"></el-input>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row type="flex">
      <el-col :span="12">
        <el-form-item prop="oldPassword" :label="$t('pages.account.fields.oldPassword')">
          <el-input type="password" v-model="formPassword.oldPassword"></el-input>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row type="flex">
      <el-col :span="12">
        <el-form-item prop="password" :label="$t('common.password')">
          <el-input type="password" v-model="formPassword.password"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item prop="confirm" :label="$t('pages.account.fields.confirm')">
          <el-input type="password" v-model="formPassword.confirm"></el-input>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row type="flex">
      <el-col :span="24">
        <div class="form-buttons">
          <el-button class="button" @click.native="handleReset">{{$t('common.reset')}}</el-button>
          <el-button class="button" @click.native="handleSubmit">{{$t('common.submit')}}</el-button>
        </div>
      </el-col>
    </el-row>
  </el-form>
</template>

<script>
  import { mapGetters } from 'vuex'
  import { logger } from 'mx-app-utils'
  import { MxFormValidateRules, MxAjax, MxNotify } from 'mx-vue-el-utils'

  export default {
    name: 'mx-change-password',
    data () {
      let passwordMatch = (rule, value, callback) => {
        let {password, confirm} = this.formPassword
        if (password === confirm) {
          callback()
        } else {
          callback(new Error(this.$t('pages.account.message.passwordMatch')))
        }
      }
      return {
        formPassword: {
          oldPassword: '',
          password: '',
          confirm: ''
        },
        rulesPassword: {
          oldPassword: [MxFormValidateRules.requiredRule({msg: this.$t('message.validate.required', [this.$t('pages.account.fields.oldPassword')])})],
          password: [MxFormValidateRules.requiredRule({msg: this.$t('message.validate.required', [this.$t('common.password')])})],
          confirm: [MxFormValidateRules.requiredRule({msg: this.$t('message.validate.required', [this.$t('pages.account.fields.confirm')])}),
            MxFormValidateRules.customRule({validator: passwordMatch})]
        }
      }
    },
    computed: {
      ...mapGetters(['loginUser']),
      accountCode () {
        let user = this.loginUser
        return user && user.code ? user.code : ''
      },
      accountName () {
        let user = this.loginUser
        return user && user.name ? user.name : ''
      }
    },
    methods: {
      handleReset () {
        this.$refs['formPassword'].resetFields()
      },
      handleSubmit () {
        this.$refs['formPassword'].validate(valid => {
          if (valid) {
            let {id, code, name} = this.loginUser
            let {oldPassword, password} = this.formPassword
            let url = '/rest/accounts/' + id + '/password/change'
            logger.debug('send POST "%s".', url)
            let fnSuccess = (data) => {
              if (data) {
                MxNotify.info(this.$t('pages.account.message.changePasswordSuccess', [code, name]))
                this.$router.push('/')
              }
            }
            MxAjax.post({url, data: {newPassword: password, oldPassword}, fnSuccess})
          } else {
            MxNotify.formValidateWarn()
          }
        })
      }
    },
    mounted () {}
  }
</script>
