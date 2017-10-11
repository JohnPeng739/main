<style rel="stylesheet/less" lang="less" scoped>
  @import "../../style/base.less";

  .form-row {
    padding: 0 10px 20px 0;
  }
</style>

<template>
  <el-form ref="formJmsDataSource" :model="formJmsDataSource" :rules="rulesJmsDataSource" label-width="60px">
    <el-row type="flex" class="form-row">
      <el-col :span="10">
        <el-form-item label="名称" prop="name">
          <el-input v-model="formJmsDataSource.name"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="14">
        <el-form-item label="类型" prop="method">
          <el-select v-model="formJmsDataSource.method">
            <el-option v-for="item in jmsTypes" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row v-if="isActiveMQ" type="flex" class="form-row">
      <el-col :span="18">
        <el-form-item label="连接" prop="server">
          <el-input v-model="formJmsDataSource.server">
            <el-select v-model="formJmsDataSource.protocol" slot="prepend"style="width: 80px;">
              <el-option label="NIO（推荐使用）" value="nio://"></el-option>
              <el-option label="TCP" value="tcp://"></el-option>
              <el-option label="HTTP" value="http://"></el-option>
            </el-select>
          </el-input>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row v-if="isActiveMQ" type="flex" class="form-row">
      <el-col :span="10">
        <el-form-item label="用户" prop="user">
          <el-input v-model="formJmsDataSource.user"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="口令" prop="password">
          <el-input type="password" v-model="formJmsDataSource.password"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="调试" prop="trace">
          <el-switch v-model="formJmsDataSource.trace"></el-switch>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row v-if="isJNDI" type="flex" class="form-row">
      <el-col :span="18">
        <el-form-item label="JNDI" prop="jndiName">
          <el-input v-model="formJmsDataSource.jndiName"></el-input>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>

<script>
  import {mapGetters} from 'vuex'
  import {logger} from 'dsutils'
  import {get} from '../../assets/ajax'
  import {formValidateWarn} from '../../assets/notify'
  import {requiredRule, customRule} from '../../assets/form-validate-rules'

  export default {
    name: 'pane-jms-dataSource-config',
    data() {
      let serverFormalValidator = (rule, value, callback) => {
        if (value && this.checkServerFormal(value)) {
          callback()
        } else {
          callback(new Error('连接的服务器和端口号格式不正确，请检查数据。'))
        }
      }
      return {
        methodSupported: [],
        formJmsDataSource: {method: 'ACTIVEMQ', name: '', protocol: 'NIO', server: '', trace: true, user: '',
          password: '', jndiName: ''},
        rulesJmsDataSource: {
          name: [requiredRule({msg: '必须输入jms数据源的名称'})],
          server: [
            requiredRule({msg: '必须输入连接的服务器和端口号'}),
            customRule({validator: serverFormalValidator})
          ]
        }
      }
    },
    computed: {
      ...mapGetters(['jmsTypes']),
      isActiveMQ() {
        return this.formJmsDataSource.method === 'ACTIVEMQ'
      },
      isJNDI() {
        return this.formJmsDataSource.method === 'JNDI'
      }
    },
    methods: {
      checkServerFormal(value) {
        let sides = value.split(':')
        if (sides && sides.length === 2) {
          if (isNaN(sides[1])) {
            return false
          }
          if (sides[0].match(/^\w+$/) || sides[0].match(/^(\d+)\.(\d+)\.(\d+)\.(\d+)$/)) {
            return true
          }
        }
        return false
      },
      getDataSource() {
        let ds = null
        this.$refs['formJmsDataSource'].validate(valid => {
          if (valid) {
            let {method, name, protocol, server, trace, user, password, jndiName} = this.formJmsDataSource
            if (method === 'ACTIVEMQ') {
              ds = {method, name, protocol, server, trace, user, password}
            } else if (method === 'JNDI') {
              ds = {method, name, jndiName}
            }
          } else {
            formValidateWarn()
          }
        })
        return ds
      },
      setDataSource(dataSource) {
        if (dataSource) {
          let {method, name, protocol, server, trace, user, password, jndiName} = dataSource
          this.formJmsDataSource = {method, name, protocol, server, trace, user, password, jndiName}
        }
      },
      resetFields() {
        this.$refs['formJmsDataSource'].resetFields()
      }
    }
  }
</script>
