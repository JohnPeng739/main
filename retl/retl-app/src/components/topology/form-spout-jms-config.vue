<style rel="stylesheet/less" lang="less">
  @import "../../style/base.less";
</style>

<template>
  <el-form ref="formJmsSpout" :model="formJmsSpout" :rules="rulesJmsSpout" label-width="100px">
    <el-form-item label="数据源" prop="dataSource">
      <el-select v-model="formJmsSpout.dataSource" :disabled="mode === 'detail'">
        <el-option v-for="item in list" :key="item.name" :value="item.name"
                   :label="item.protocol + item.server"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="目标名" prop="destinateName">
      <el-input v-model="formJmsSpout.destinateName" :readonly="mode === 'detail'"></el-input>
    </el-form-item>
    <el-form-item label="是否主题" prop="isTopic">
      <el-switch v-model="formJmsSpout.isTopic" on-text="是" off-text="否" :disabled="mode === 'detail'"></el-switch>
    </el-form-item>
  </el-form>
</template>

<script>
  import {jmsProtocolTypes} from './types'
  import {requiredRule, customRule} from '../../assets/form-validate-rules'
  import {formValidateWarn} from '../../assets/notify'

  export default {
    name: 'pane-spout-jms-config',
    props: ['topology', 'configuration', 'mode'],
    data () {
      let dataSourceValidator = (rule, value, callback) => {
        let dataSources = this.list
        let found = false
        if (dataSources) {
          dataSources.forEach(ds => {
            if (ds && ds.name === value) {
              found = true
              return
            }
          })
        }
        if(found) {
          callback()
        } else {
          callback(new Error('选择的数据源[' + value + ']尚未配置。'))
        }
      }
      return {
        jmsProtocolTypes: jmsProtocolTypes,
        list: this.topology.jmsDataSources,
        formJmsSpout: {dataSource: '', destinateName: '', isTopic: false, producer: 'JSON'},
        rulesJmsSpout: {
          dataSource: [requiredRule({msg: '必须选择数据源', trigger: 'change'}), customRule({validator: dataSourceValidator})],
          destinateName: [requiredRule({msg: '必须输入连接的目标（队列或主题）名称'})]
        }
      }
    },
    computed: {},
    methods: {
      getConfiguration() {
        let spout = null
        this.$refs['formJmsSpout'].validate(valid => {
          if (valid) {
            spout = this.formJmsSpout
          } else {
            formValidateWarn(this)
          }
        })
        return spout
      }
    },
    mounted () {
      if (this.configuration) {
        let {dataSource, destinateName, isTopic, producer} = this.configuration
        this.formJmsSpout = {dataSource, destinateName, isTopic, producer}
      }
    }
  }
</script>
