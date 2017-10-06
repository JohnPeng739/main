<style rel="stylesheet/less" lang="less">
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
        <el-form-item label="连接" prop="server">
          <el-input v-model="formJmsDataSource.server">
            <el-select v-model="formJmsDataSource.protocol" slot="prepend"style="width: 80px;">
              <el-option v-for="item in jmsProtocolTypes" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
          </el-input>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row type="flex" class="form-row">
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
  </el-form>
</template>

<script>
  import {jmsProtocolTypes} from '../topology/types'
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
        jmsProtocolTypes: jmsProtocolTypes,
        formJmsDataSource: {name: '', protocol: 'nio://', server: 'localhost:61616', trace: true, user: '', password: ''},
        rulesJmsDataSource: {
          name: [requiredRule({msg: '必须输入JMS数据源的名称'})],
          server: [
            requiredRule({msg: '必须输入连接的服务器和端口号'}),
            customRule({validator: serverFormalValidator})
          ]
        }
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
            let {name, protocol, server, trace, user, password} = this.formJmsDataSource
            ds = {name, protocol, server, trace, user, password}
          } else {
            formValidateWarn(this)
          }
        })
        return ds
      },
      setDataSource(dataSource) {
        if (dataSource) {
          let {name, protocol, server, trace, user, password} = dataSource
          this.formJmsDataSource = {name, protocol, server, trace, user, password}
        }
      },
      resetFields() {
        this.$refs['formJmsDataSource'].resetFields()
      }
    }
  }
</script>
