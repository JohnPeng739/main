<style rel="stylesheet/less" lang="less">
  @import "../style/base.less";

  .layout-login {
    position: relative;
    width: 100%;
    height: 100%;
    padding: 150px 0;
    h1 {
      text-align: center;
      vertical-align: middle;
      margin-bottom: 80px;
      color: @header-bg-color;
    }
    form {
      margin: 0 auto;
      width: 400px;
      height: 100%;
      padding-right: 40px;
      .buttons {
        width: 100%;
        text-align: center;
        padding-top: 40px;
        padding-left: 40px;
        .button-ok {
          width: @button-width;
          margin: 0 30px;
        }
        .button-reset {
          width: @button-width;
          margin: 0 10px 0 30px;
        }
        .button-init {
          width: @button-width - 60px;
          margin: 0;
        }
      }
    }
  }
</style>

<template>
  <div class="layout-login">
    <h1>{{title}}</h1>
    <el-form ref="formLogin" :model="formLogin" :rules="ruleLogin" label-width="100px">
      <el-form-item label="用户名" prop="user">
        <el-input v-model="formLogin.user"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="formLogin.password" type="password"></el-input>
      </el-form-item>
      <div class="buttons">
        <el-button class="button-ok" type="primary" @click="submit">登录</el-button>
        <el-button class="button-reset" @click="reset">重置</el-button>
        <el-button class="button-init" type="text" @click="handleInitUser">初始化</el-button>
      </div>
    </el-form>
  </div>
</template>

<script>
  import {mapActions} from 'vuex'
  import {logger} from 'dsutils'
  import {get} from '../assets/ajax'
  import {warn, info} from '../assets/notify'
  import {requiredRule, rangeRule} from '../assets/form-validate-rules'
  import config from '../modules/manage/config'

  export default {
    data () {
      return {
        title: config.title,
        formLogin: {
          user: '',
          password: ''
        },
        ruleLogin: {
          user: [requiredRule({msg: '请输入用户名'})],
          password: [requiredRule({msg: '请输入密码'}), rangeRule({min: 4, msg: '密码长度不能少于4位'})]
        }
      }
    },
    methods: {
      ...mapActions(['login', 'goto']),
      handleInitUser() {
        let url = '/rest/init'
        logger.debug('send GET "%s"', url)
        get(url, data => {
          logger.debug("Init user successfully, response: %j.", data)
          info(this, '初始化用户[' + data.code + ']成功，请登录系统。')
        })
      },
      submit () {
        logger.debug('submit the login form')
        this.$refs['formLogin'].validate(valid => {
          if (valid) {
            let {user, password} = this.formLogin
            let success = () => {
              info(this, '用户登录成功。')
              this.goto({owner: this, path: '/'})
            }
            this.login({user, password, success})
          } else {
            warn(this, '登录表单数据验证失败， 请核对数据！')
          }
        })
      },
      reset () {
        logger.debug('reset all the fields.')
        this.$refs['formLogin'].resetFields()
      }
    }
  }
</script>
