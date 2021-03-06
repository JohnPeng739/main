<style rel="stylesheet/less" lang="less" scoped>
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
    <slot name="fields"></slot>
    <slot name="fieldsTransform"></slot>
    <el-form-item label="关键字" prop="key">
      <el-select v-model="formJdbcSpout.key" :disabled="mode === 'detail'">
        <el-option v-for="item in fields" :key="item" :label="item" :value="item"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="时间戳" prop="timestamp">
      <el-select v-model="formJdbcSpout.timestamp" :disabled="mode === 'detail'">
        <el-option v-for="item in fields" :key="item" :label="item" :value="item"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="Zookeepers" prop="zookeepers">
      <ds-tag-normal v-model="formJdbcSpout.zookeepers" type="gray" :disabled="mode === 'detail'"></ds-tag-normal>
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
  </el-form>
</template>

<script>
  import {requiredRule, customRule} from '../../../assets/form-validate-rules'
  import {formValidateWarn} from '../../../assets/notify'
  import DsTagNormal from '../../ds-tag-normal.vue'
  import DsTagBothSides from '../../ds-tag-both-sides.vue'

  export default {
    name: 'pane-spout-jdbc-config',
    props: ['jdbcDataSources', 'zookeepers', 'configuration', 'mode', 'fields'],
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
        value = this.formJdbcSpout.zookeepers
        if (value && value.length > 0) {
          callback()
        } else {
          callback(new Error('必须输入Zookeeper服务器名称'))
        }
      }
      let fieldsValidator = (rule, value, callback) => {
        value = this.formJdbcSpout.fields
        if (value && value.length > 0) {
          callback()
        } else {
          callback(new Error('必须输入需要获取的表中的字段'))
        }
      }
      return {
        list: this.jdbcDataSources,
        formJdbcSpout: {
          dataSource: '',
          table: '',
          key: '',
          timestamp: '',
          zookeepers: [],
          ackPath: '',
          windowSize: 1000,
          intervalSecs: 6
        },
        rulesJdbcSpout: {
          dataSource: [requiredRule({msg: '必须选择一个jdbc数据源', trigger: 'change'}), customRule({validator: dataSourceValidator})],
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
          } else {
            formValidateWarn()
          }
        })
        return spout
      },
    },
    mounted() {
      if (this.configuration !== null && this.configuration !== undefined) {
        let {dataSource, table, key, timestamp, ackPath, windowsSize, intervalSecs} = this.configuration
        this.formJdbcSpout = {
          dataSource,
          table,
          key,
          timestamp,
          zookeepers: this.zookeepers,
          ackPath,
          windowsSize,
          intervalSecs
        }
      }
    }
  }
</script>
