<style rel="stylesheet/less" lang="less">
  @import "../../style/base.less";

  .layout-buttons {
    display: inline-block;
    float: right;
    margin: 0;
    padding: 0;
    .button {
      padding: 0 5px;
      min-height: 20px;
      font-size: @content-text-font-size;
      color: @button-color;
      &:hover {
        color: @button-hover-color
      }
    }
    .button-icon {
      font-size: 12px;
    }
  }
</style>

<template>
  <el-form ref="baseInfo" :model="formBasicInfo" :rules="rulesBasicInfo" label-width="100px" class="layout-form">
    <el-row type="flex">
      <el-col :span="16">
        <el-form-item label="拓扑名称" prop="name">
          <el-input v-model="formBasicInfo.name"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="拓扑类型" prop="type">
          <el-select v-model="formBasicInfo.type">
            <el-option v-for="item in topologyTypes" :key="item.value" :label="item.label"
                       :value="item.value"></el-option>
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row type="flex">
      <el-col :span="10">
        <el-form-item label="消息超时值" prop="messageTimeoutSecs">
          <el-input-number v-model="formBasicInfo.messageTimeoutSecs" :min="1" :max="10"></el-input-number>
          <span>秒</span>
        </el-form-item>
      </el-col>
      <el-col :span="8" v-if="formBasicInfo.type === 'RETL'">
        <el-tooltip content="数据存储的队列或订阅主题的名称，默认使用第一个JMS数据源。" placement="bottom" :hide-after="2000">
          <el-form-item label="存储名" prop="tarDestinateName">
            <el-input v-model="formBasicInfo.tarDestinateName"></el-input>
          </el-form-item>
        </el-tooltip>
      </el-col>
      <el-col :span="6" v-if="formBasicInfo.type === 'RETL'">
        <el-form-item label="是否主题" prop="tarIsTopic">
          <el-switch v-model="formBasicInfo.tarIsTopic"></el-switch>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row type="flex">
      <el-col :span="24">
        <el-form-item label="JDBC源" prop="jdbcDataSources">
          <pane-data-source-config ref="jdbcDataSources" :dataSources="formBasicInfo.jdbcDataSources"
                                   v-on:delete="handleDeleteJdbcDataSource" v-on:edit="handleEditJdbcDataSource"
                                   v-on:reset="handleResetJdbcDataSourceForm"
                                   v-on:save="handleSaveJdbcDataSourceForm">
            <form-jdbc-data-source-config ref="formJdbcDataSource" slot="form-config">
            </form-jdbc-data-source-config>
          </pane-data-source-config>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row type="flex">
      <el-col :span="24">
        <el-form-item label="JMS源" prop="jmsDataSources">
          <pane-data-source-config ref="jmsDataSources" :dataSources="formBasicInfo.jmsDataSources"
                                   v-on:delete="handleDeleteJmsDataSource" v-on:edit="handleEditJmsDataSource"
                                   v-on:reset="handleResetJmsDataSourceForm" v-on:save="handleSaveJmsDataSourceForm">
            <form-jms-data-source-config ref="formJmsDataSource" slot="form-config">
            </form-jms-data-source-config>
          </pane-data-source-config>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>

<script>
  import {logger} from 'dsutils'
  import {error, formValidateWarn} from '../../assets/notify'
  import DsIcon from '../../components/icon.vue'
  import DsTagNormal from '../ds-tag-normal.vue'
  import PaneDataSourceConfig from '../pane-listable-config.vue'
  import FormJdbcDataSourceConfig from '../datasource/form-jdbc-datasource-config.vue'
  import FormJmsDataSourceConfig from '../datasource/form-jms-datasource-config.vue'
  import {topologyTypes} from './types'
  import {requiredRule, rangeRule, customRule} from '../../assets/form-validate-rules'

  export default {
    name: 'topology-basic-infor',
    props: ['topology'],
    components: {DsIcon, DsTagNormal, PaneDataSourceConfig, FormJdbcDataSourceConfig, FormJmsDataSourceConfig},
    data() {
      let jdbcDataSourcesValidator = (rule, vallue, callback) => {
        let {type, jdbcDataSources} = this.formBasicInfo
        if (type === 'PERSIST' && (jdbcDataSources && jdbcDataSources.length <= 0)) {
          callback(new Error('数据存储类计算拓扑必须配置JDBC数据源。'))
        } else {
          callback()
        }
      }
      let jmsDataSourcesValidator = (rule, value, callback) => {
        let {jmsDataSources} = this.formBasicInfo
        if (jmsDataSources && jmsDataSources.length > 0) {
          callback()
        } else {
          callback(new Error('必须要配置JMS数据源。'))
        }
      }
      return {
        topologyTypes: topologyTypes,
        formBasicInfo: {
          name: this.topology.name,
          type: this.topology.type,
          messageTimeoutSecs: this.topology.messageTimeoutSecs,
          tarDestinateName: this.topology.tarDestinateName,
          tarIsTopic: this.topology.tarIsTopic,
          jdbcDataSourceEditIndex: null,
          jdbcDataSources: [],
          jmsDataSourceEditIndex: null,
          jmsDataSources: []
        },
        rulesBasicInfo: {
          name: [
            requiredRule({msg: '请输入计算拓扑的名称'}),
            rangeRule({min: 6, max: 20})
          ],
          type: [requiredRule({msg: '请选择计算拓扑的类型', trigger: 'change'})],
          tarDestinateName: [requiredRule({msg: '请输入数据存储的目标（队列或订阅主题）的名称'})],
          jdbcDataSources: [customRule({validator: jdbcDataSourcesValidator})],
          jmsDataSources: [customRule({validator: jmsDataSourcesValidator})]
        }
      }
    },
    methods: {
      getBasicInfo() {
        let bi = null
        this.$refs['baseInfo'].validate(valid => {
          if (valid) {
            let {name, type, messageTimeoutSecs, tarDestinateName, tarIsTopic, zookeepers, jdbcDataSources, jmsDataSources} = this.formBasicInfo
            bi = {
              name,
              type,
              messageTimeoutSecs,
              tarDestinateName,
              tarIsTopic,
              zookeepers,
              jdbcDataSources,
              jmsDataSources
            }
          } else {
            formValidateWarn(this)
          }
        })
        return bi
      },
      setTopology(topology) {
        this.formBasicInfo = topology
        if (topology.jdbcDataSources) {
          this.$refs['jdbcDataSources'].setList(topology.jdbcDataSources)
        }
        if (topology.jmsDataSources) {
          this.$refs['jmsDataSources'].setList(topology.jmsDataSources)
        }
      },
      handleEditJdbcDataSource(index) {
        logger.debug('edit jdbc data source request, index: %d', index)
        this.formBasicInfo.jdbcDataSourceEditIndex = index
        let dataSources = this.formBasicInfo.jdbcDataSources
        if (dataSources && dataSources[index]) {
          console.log(this.$refs['formJdbcDataSource'])
          this.$nextTick(_ => this.$refs['formJdbcDataSource'].setDataSource(dataSources[index]))
        } else {
          error(this, '指定的JDBC数据源不存在!')
        }
      },
      handleDeleteJdbcDataSource(indexes) {
        logger.debug('delete jdbc data sources request: %j.', indexes)
        if (indexes && indexes.length > 0) {
          let dataSources = this.formBasicInfo.jdbcDataSources
          if (dataSources) {
            // 倒序一下
            indexes = indexes.sort((a, b) => b - a)
            indexes.forEach(index => dataSources.splice(index, 1))
          }
          this.setJdbcDataSources(dataSources)
        }
      },
      handleResetJdbcDataSourceForm() {
        logger.debug('reset jdbc data source form.')
        this.$refs['formJdbcDataSource'].resetFields()
      },
      handleSaveJdbcDataSourceForm() {
        logger.debug('save jdbc data source form.')
        let dataSources = this.formBasicInfo.jdbcDataSources
        let editIndex = this.formBasicInfo.jdbcDataSourceEditIndex
        if (!dataSources) {
          dataSources = []
        }
        let dataSource = this.$refs['formJdbcDataSource'].getDataSource()
        if (dataSource) {
          if (editIndex !== null) {
            // edit
            dataSource[editIndex] = dataSource
          } else {
            // add
            dataSources.push(dataSource)
          }
          this.setJdbcDataSources(dataSources)
          this.$refs['jdbcDataSources'].handleConfigForm('close')
        }
      },
      handleEditJmsDataSource(index) {
        logger.debug('edit jms data source request, index: %d', index)
        this.formBasicInfo.jmsDataSourceEditIndex = index
        let dataSources = this.formBasicInfo.jmsDataSources
        if (dataSources && dataSources[index]) {
          this.$nextTick(_ => this.$refs['formJmsDataSource'].setDataSource(dataSources[index]))
        } else {
          error(this, '指定的JMS数据源不存在!')
        }
      },
      handleDeleteJmsDataSource(indexes) {
        logger.debug('delete jms data sources request: %j.', indexes)
        if (indexes && indexes.length > 0) {
          let dataSources = this.formBasicInfo.jmsDataSources
          if (dataSources) {
            // 倒序一下
            indexes = indexes.sort((a, b) => b - a)
            indexes.forEach(index => dataSources.splice(index, 1))
          }
          this.setJmsDataSources(dataSources)
        }
      },
      handleResetJmsDataSourceForm() {
        logger.debug('reset jms data source form.')
        this.$refs['formJmsDataSource'].resetFields()
      },
      handleSaveJmsDataSourceForm() {
        logger.debug('save jms data source form.')
        let dataSources = this.formBasicInfo.jmsDataSources
        let editIndex = this.formBasicInfo.jmsDataSourceEditIndex
        if (!dataSources) {
          dataSources = []
        }
        let dataSource = this.$refs['formJmsDataSource'].getDataSource()
        if (dataSource) {
          if (editIndex !== null) {
            // edit
            dataSources[editIndex] = dataSource
          } else {
            // add
            dataSources.push(dataSource)
          }
          this.setJmsDataSources(dataSources)
          this.$refs['jmsDataSources'].handleConfigForm('close')
        }
      },
      setJdbcDataSources(dataSources) {
        if (!dataSources) {
          dataSources = []
        }
        this.formBasicInfo.jdbcDataSources = dataSources
        this.formBasicInfo.jdbcDataSourceEditIndex = null
        this.$refs['jdbcDataSources'].setList(dataSources)
      },
      setJmsDataSources(dataSources) {
        if (!dataSources) {
          dataSources = []
        }
        this.formBasicInfo.jmsDataSources = dataSources
        this.formBasicInfo.jmsDataSourceEditIndex = null
        this.$refs['jmsDataSources'].setList(dataSources)
      }
    }
  }
</script>
