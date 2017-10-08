<style rel="stylesheet/less" lang="less">
  @import "../style/base.less";

  .layout-user-info {
    margin: 30px auto;
    .buttons {
      text-align: right;
      margin-bottom: 20px;
      button {
        margin: 0 10px;
        color: @button-color;
        &:hover {
          color: @button-hover-color;
        }
        .button-icon {
          display: block;
          padding-top: 20px;
        }
      }
    }
  }
</style>

<template>
  <div class="layout-user-info">
    <el-row type="flex">
      <el-col :offset="4" :span="16" class="buttons">
        <el-button v-if="!passwordFormVisible" type="text" @click="handleChangePassword">
          <ds-icon class="button-icon" name="lock"></ds-icon>
          修改密码</el-button>
        <el-button v-if="passwordFormVisible" type="text" @click="handleOperatePassword('close')">关闭</el-button>
        <el-button v-if="passwordFormVisible" type="text" @click="handleOperatePassword('save')">保存</el-button>
        <el-button v-if="passwordFormVisible" type="text" @click="handleOperatePassword('reset')">重置</el-button>
      </el-col>
    </el-row>
    <el-row v-if="passwordFormVisible" type="flex">
      <el-col :offset="4" :span="16">
        <el-form ref="formPassword" :model="formPassword" :rules="rulesPassword" label-width="100px">
          <el-form-item label="旧密码" prop="old">
            <el-input type="password" v-model="formPassword.old"></el-input>
          </el-form-item>
          <el-form-item label="新密码" prop="new">
            <el-input type="password" v-model="formPassword.new"></el-input>
          </el-form-item>
          <el-form-item label="确认密码" prop="confirm">
            <el-input type="password" v-model="formPassword.confirm"></el-input>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
    <el-row type="flex">
      <el-col :offset="4" :span="16">
        <el-form :model="user" label-width="100px">
          <el-form-item label="代码">
            <el-input v-model="user.code" :readonly="true"></el-input>
          </el-form-item>
          <el-form-item label="姓名">
            <el-input v-model="user.name" :readonly="true"></el-input>
          </el-form-item>
          <el-form-item label="是否有效">
            <el-checkbox v-model="user.valid"></el-checkbox>
          </el-form-item>
          <el-form-item label="是否在线">
            <el-checkbox v-model="user.online"></el-checkbox>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import {logger} from 'dsutils'
  import {get, post} from '../assets/ajax'
  import {info, formValidateWarn} from '../assets/notify'
  import {requiredRule, rangeRule, customRule} from '../assets/form-validate-rules'
  import DsIcon from '../components/icon.vue'

  export default {
    components: {DsIcon},
    name: 'user-info',
    data() {
      let confirmPasswordValidator = (rule, value, callback) => {
        let newPassword = this.formPassword.new
        if (newPassword === value) {
          callback()
        } else {
          callback(new Error('两次输入的新密码不一致。'))
        }
      }
      return {
        passwordFormVisible: false,
        user: {code: '', name: '', valid: false, online: false},
        formPassword: {old: '', new: '', confirm: ''},
        rulesPassword: {
          old: [requiredRule({msg: '必须输入原来的密码。'})],
          new: [requiredRule({msg: '必须输入新的密码。'}), rangeRule({min: 6, msg: '新的密码长度必须大于等于6位。'})],
          confirm: [requiredRule({msg: '必须再次确认新的密码。'}), customRule({validator: confirmPasswordValidator})]
        }
      }
    },
    methods: {
      handleChangePassword() {
        this.passwordFormVisible = true
      },
      handleOperatePassword(operate) {
        if (operate === 'close') {
          this.passwordFormVisible = false
        } else if (operate === 'reset') {
          this.$refs['formPassword'].resetFields()
        } else if (operate === 'save') {
          this.$refs['formPassword'].validate(valid => {
            if (valid) {
              let url = '/rest/password/change'
              logger.debug('send POST "%s"', url)
              let oldPassword = this.formPassword.old
              let newPassword = this.formPassword.new
              post(url, {oldPassword, newPassword}, data => {
                info(this, '修改密码成功。')
                this.passwordFormVisible = false
              })
            } else {
              formValidateWarn(this)
            }
          })
        }
      }
    },
    mounted() {
      let userCode = this.$route.params.userCode
      if (userCode) {
        let url = '/rest/users/' + userCode
        logger.debug('send GET "%s"', url)
        get(url, data => {
          logger.debug('Get user success: %j.', data)
          this.user = data
        })
      }
    }
  }
</script>
