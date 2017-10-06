<style rel="stylesheet/less" lang="less">
  @import "../../../style/base.less";
</style>

<template>
  <el-form ref="formJdbcSpout" :model="formJdbcSpout" :rules="rulesJdbcSpout" label-width="100px">
    <el-form-item label="数据源" prop="dataSource">
      <el-select v-model="formJdbcSpout.dataSource" :disabled="mode === 'detail'">
        <el-option v-for="item in list" :key="item.name" :value="item.name"
                   :label="item.url"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="表名" prop="table">
      <el-input v-model="formJdbcSpout.table" :readonly="mode === 'detail'"></el-input>
    </el-form-item>
    <el-form-item label="关键字" prop="key">
      <el-input v-model="formJdbcSpout.key" :readonly="mode === 'detail'"></el-input>
    </el-form-item>
    <el-form-item label="时间戳" prop="timestamp">
      <el-input v-model="formJdbcSpout.timestamp" :readonly="mode === 'detail'"></el-input>
    </el-form-item>
    <el-form-item label="Zookeepers" prop="zookeepers">
      <ds-tag-normal v-model="zookeepers" type="gray"></ds-tag-normal>
    </el-form-item>
    <el-form-item label="ACK路径" prop="ackPath">
      <el-input v-model="formJdbcSpout.ackPath" :readonly="mode === 'detail'"></el-input>
    </el-form-item>
    <el-form-item label="缓冲数">
      <el-input-number v-model="formJdbcSpout.windowSize" :min="1000" :max="10000" :step="500" :disabled="mode === 'detail'"></el-input-number>
    </el-form-item>
    <el-form-item label="间隔时间">
      <el-input-number v-model="formJdbcSpout.intervalSecs" :min="1" :max="30" :disabled="mode === 'detail'"></el-input-number>
      <span>秒</span>
    </el-form-item>
    <el-form-item label="字段列表">
      <ds-tag-normal v-model="formJdbcSpout.fields" type="gray" :disabled="mode === 'detail'"></ds-tag-normal>
    </el-form-item>
    <el-form-item label="字段改名">
      <ds-tag-both-sides v-model="formJdbcSpout.fieldsTransform" type="gray" :disabled="mode === 'detail'" sideSeparator="=>"></ds-tag-both-sides>
    </el-form-item>
  </el-form>
</template>

<script>
  import {requiredRule, customRule} from '../../../assets/form-validate-rules'
  import {formValidateWarn} from '../../../assets/notify'
  import DsTagNormal from '../../ds-tag-normal.vue'
  import DsTagBothSides from '../../ds-tag-both-sides.vue'

  export default {
    name: 'pane-spout-jdbc-config',
    props: ['topology', 'configuration', 'mode'],
    components: {DsTagNormal, DsTagBothSides},
    data() {
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
      let zookeepersValidator = (rule, value, callback) => {
        value = this.zookeepers
        if (value && value.length > 0) {
          callback()
        } else {
          callback(new Error('必须输入Zookeeper服务器名称'))
        }
      }
      return {
        list: this.topology.jdbcDataSources,
        zookeepers: [],
        formJdbcSpout: {
          dataSource: '',
          table: '',
          key: '',
          timestamp: '',
          ackPath: '',
          windowSize: 1000,
          intervalSecs: 6,
          fields: [],
          fieldsTransform: []
        },
        rulesJdbcSpout: {
          dataSource: [requiredRule({msg: '必须选择一个JDBC数据源', trigger: 'change'}), customRule({validator: dataSourceValidator})],
          table: [requiredRule({msg: '必须输入表名'})],
          key: [requiredRule({msg: '必须输入关键字字段名'})],
          timestamp: [requiredRule({msg: '必须输入时间戳字段名'})],
          zookeepers: [customRule({validator: zookeepersValidator})],
          ackPath: [requiredRule({msg: '必须输入ACK缓冲路径'})]
        }
      }
    },
    methods: {
      getConfiguration() {
        let spout = null
        this.$refs['formJdbcSpout'].validate(valid => {
          if (valid) {
            spout = this.formJdbcSpout
            this.topology.zookeepers = this.zookeepers
          } else {
            formValidateWarn(this)
          }
        })
        return spout
      },
    },
    mounted() {
      if (this.configuration !== null && this.configuration !== undefined) {
        let {dataSource, table, key, timestamp, ackPath, windowsSize, intervalSecs, fields, fieldsTransform} = this.configuration
        this.formJdbcSpout = {
          dataSource,
          table,
          key,
          timestamp,
          ackPath,
          windowsSize,
          intervalSecs,
          fields,
          fieldsTransform
        }
      }
    }
  }
</script>
