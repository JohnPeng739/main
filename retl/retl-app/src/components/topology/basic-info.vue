<style rel="stylesheet/less" lang="less" scoped>
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
        <el-form-item label="名称" prop="name">
          <el-input v-model="formBasicInfo.name"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="类型" prop="type">
          <el-select v-model="formBasicInfo.type" style="width: 100%;">
            <el-option v-for="item in topologyTypes" :key="item.value" :label="item.label"
                       :value="item.value"></el-option>
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row type="flex">
      <el-col :span="24">
        <el-form-item label="描述" prop="description">
          <el-input type="textarea" v-model="formBasicInfo.description" :row="2"></el-input>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row type="flex">
      <el-col :span="24">
        <el-form-item label="消息超时值" prop="messageTimeoutSecs">
          <el-input-number v-model="formBasicInfo.messageTimeoutSecs" :min="1" :max="10"></el-input-number>
          <span>秒</span>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row type="flex">
      <el-col :span=24 v-if="formBasicInfo.type === 'retl'">
        <el-form-item label="存储目标" prop="destinations">
          <pane-listable-config ref="destinations" :list="formBasicInfo.destinations"
                                v-on:delete="handleDeleteDestinations" v-on:edit="handleEditDestinations"
                                v-on:reset="handleResetDestinationForm" v-on:save="handleSaveDestinationForm">
            <form-destination-config ref="formDestination" slot="form-config"></form-destination-config>
          </pane-listable-config>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row type="flex">
      <el-col :span="24">
        <el-form-item label="jdbc源" prop="jdbcDataSources">
          <pane-listable-config ref="jdbcDataSources" :list="formBasicInfo.jdbcDataSources"
                                v-on:delete="handleDeleteJdbcDataSource" v-on:edit="handleEditJdbcDataSource"
                                v-on:reset="handleResetJdbcDataSourceForm" v-on:save="handleSaveJdbcDataSourceForm">
            <form-jdbc-data-source-config ref="formJdbcDataSource" slot="form-config"></form-jdbc-data-source-config>
          </pane-listable-config>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row type="flex">
      <el-col :span="24">
        <el-form-item label="jms源" prop="jmsDataSources">
          <pane-listable-config ref="jmsDataSources" :list="formBasicInfo.jmsDataSources"
                                v-on:delete="handleDeleteJmsDataSource" v-on:edit="handleEditJmsDataSource"
                                v-on:reset="handleResetJmsDataSourceForm" v-on:save="handleSaveJmsDataSourceForm">
            <form-jms-data-source-config ref="formJmsDataSource" slot="form-config"></form-jms-data-source-config>
          </pane-listable-config>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row type="flex" v-if="formBasicInfo.type === 'retl'">
      <el-col :span="24">
        <el-form-item label="缓存数据" prop="caches">
          <pane-listable-config ref="caches" :list="formBasicInfo.caches"
                                v-on:delete="handleDeleteCache" v-on:edit="handleEditCache"
                                v-on:reset="handleResetCacheForm" v-on:save="handleSaveCacheForm">
            <form-cache-config ref="formCache" slot="form-config"></form-cache-config>
          </pane-listable-config>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>

<script>
  import {mapGetters, mapActions} from 'vuex'
  import {logger} from 'dsutils'
  import {error, formValidateWarn} from '../../assets/notify'
  import DsIcon from '../../components/icon.vue'
  import DsTagNormal from '../ds-tag-normal.vue'
  import PaneListableConfig from '../pane-listable-config.vue'
  import FormDestinationConfig from './form-destination-config.vue'
  import FormJdbcDataSourceConfig from '../datasource/form-jdbc-datasource-config.vue'
  import FormJmsDataSourceConfig from '../datasource/form-jms-datasource-config.vue'
  import FormCacheConfig from './retl/form-cache-config.vue'
  import {requiredRule, rangeRule, customRule} from '../../assets/form-validate-rules'

  export default {
    name: 'topology-basic-infor',
    components: {
      DsIcon, DsTagNormal, PaneListableConfig, FormDestinationConfig, FormJdbcDataSourceConfig, FormJmsDataSourceConfig,
      FormCacheConfig
    },
    data() {
      return {
        formBasicInfo: {
          name: '',
          type: '',
          description: '',
          messageTimeoutSecs: 3,
          destinations: [],
          tarIsTopic: false,
          jdbcDataSources: [],
          jmsDataSources: [],
          caches: []
        }
      }
    },
    computed: {
      ...mapGetters(['topology', 'topologyTypes']),
      rulesBasicInfo() {
        let destinationsValidator = (rule, value, callback) => {
          value = this.formBasicInfo.destinations
          if (value && value.length > 0) {
            callback()
          } else {
            callback(new Error('实时抽取类计算拓扑必须配置存储JMS目标'))
          }
        }
        let jdbcDataSourcesValidator = (rule, vallue, callback) => {
          let {type, jdbcDataSources} = this.formBasicInfo
          if (type === 'persist' && (jdbcDataSources && jdbcDataSources.length <= 0)) {
            callback(new Error('数据存储类计算拓扑必须配置jdbc数据源。'))
          } else {
            callback()
          }
        }
        let jmsDataSourcesValidator = (rule, value, callback) => {
          let {jmsDataSources} = this.formBasicInfo
          if (jmsDataSources && jmsDataSources.length > 0) {
            callback()
          } else {
            callback(new Error('必须要配置jms数据源。'))
          }
        }
        let rules = {
          name: [
            requiredRule({msg: '请输入计算拓扑的名称'}),
            rangeRule({min: 6, max: 50})
          ],
          type: [requiredRule({msg: '请选择计算拓扑的类型', trigger: 'change'})],
          jmsDataSources: [customRule({validator: jmsDataSourcesValidator})]
        }
        if (this.formBasicInfo.type === 'retl') {
          rules['destinations'] = [customRule({validator: destinationsValidator})]
        } else {
          rules['jdbcDataSources'] = [customRule({validator: jdbcDataSourcesValidator})]
        }
        return rules
      }
    },
    methods: {
      ...mapActions(['setBaseInfo', 'setCaches', 'setZookeepers', 'setJdbcDataSources', 'setJmsDataSources']),
      validated() {
        let validated = false
        this.$refs['baseInfo'].validate(valid => {
          if (valid) {
            validated = valid
          } else {
            formValidateWarn()
          }
        })
        return validated
      },
      cacheData() {
        let {name, type, description, debug, messageTimeoutSecs, maxSpoutPending, destinations}
          = this.formBasicInfo
        this.setBaseInfo({
          name, type, description, debug, messageTimeoutSecs, maxSpoutPending, destinations
        })
        this.setZookeepers(this.formBasicInfo.zookeepers)
        this.setJdbcDataSources(this.formBasicInfo.jdbcDataSources)
        this.setJmsDataSources(this.formBasicInfo.jmsDataSources)
        this.setCaches(this.formBasicInfo.caches)
      },
      handleDeleteDestinations(indexes) {
        logger.debug('delete jms destination request: %j.', indexes)
        if (indexes && indexes.length > 0) {
          let destinations = this.formBasicInfo.destinations
          if (destinations) {
            // 倒序一下
            indexes = indexes.sort((a, b) => b - a)
            indexes.forEach(index => destinations.splice(index, 1))
          }
          this.fillDestinations(destinations)
        }
      },
      handleEditDestinations(index) {
        logger.debug('edit jms destination request, index: %d', index)
        let destinations = this.formBasicInfo.destinations
        if (destinations && destinations[index]) {
          this.$nextTick(_ => this.$refs['formDestination'].setDestination(destinations[index]))
        } else {
          error('指定的JMS存储目标不存在!')
        }
      },
      handleResetDestinationForm() {
        logger.debug('reset jms destination form.')
        this.$refs['formDestination'].resetFields()
      },
      handleSaveDestinationForm() {
        logger.debug('save jms destination form.')
        let destinations = this.formBasicInfo.destinations
        if (!destinations) {
          destinations = []
        }
        let destination = this.$refs['formDestination'].getDestination()
        let selected = this.$refs['destinations'].getSelected()
        logger.debug('save jms destination data, destination: %j, selected: %j.', destination, selected)
        if (destination) {
          if (selected && selected.length === 1) {
            // edit
            destinations[selected[0]] = destination
          } else {
            // add
            destinations.push(destination)
          }
          this.fillDestinations(destinations)
          this.$refs['destinations'].handleConfigForm('close')
        }
      },
      handleEditJdbcDataSource(index) {
        logger.debug('edit jdbc data source request, index: %d', index)
        let dataSources = this.formBasicInfo.jdbcDataSources
        if (dataSources && dataSources[index]) {
          this.$nextTick(_ => this.$refs['formJdbcDataSource'].setDataSource(dataSources[index]))
        } else {
          error('指定的jdbc数据源不存在!')
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
          this.fillJdbcDataSources(dataSources)
        }
      },
      handleResetJdbcDataSourceForm() {
        logger.debug('reset jdbc data source form.')
        this.$refs['formJdbcDataSource'].resetFields()
      },
      handleSaveJdbcDataSourceForm() {
        logger.debug('save jdbc data source form.')
        let dataSources = this.formBasicInfo.jdbcDataSources
        if (!dataSources) {
          dataSources = []
        }
        let dataSource = this.$refs['formJdbcDataSource'].getDataSource()
        let selected = this.$refs['jdbcDataSources'].getSelected()
        logger.debug('save jdbc data, dataSource: %j, selected: %j.', dataSource, selected)
        if (dataSource) {
          if (selected && selected.length === 1) {
            // edit
            dataSources[selected[0]] = dataSource
          } else {
            // add
            dataSources.push(dataSource)
          }
          this.fillJdbcDataSources(dataSources)
          this.$refs['jdbcDataSources'].handleConfigForm('close')
        }
      },
      handleEditJmsDataSource(index) {
        logger.debug('edit jms data source request, index: %d', index)
        let dataSources = this.formBasicInfo.jmsDataSources
        if (dataSources && dataSources[index]) {
          this.$nextTick(_ => this.$refs['formJmsDataSource'].setDataSource(dataSources[index]))
        } else {
          error('指定的jms数据源不存在!')
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
          this.fillJmsDataSources(dataSources)
        }
      },
      handleResetJmsDataSourceForm() {
        logger.debug('reset jms data source form.')
        this.$refs['formJmsDataSource'].resetFields()
      },
      handleSaveJmsDataSourceForm() {
        logger.debug('save jms data source form.')
        let dataSources = this.formBasicInfo.jmsDataSources
        if (!dataSources) {
          dataSources = []
        }
        let dataSource = this.$refs['formJmsDataSource'].getDataSource()
        let selected = this.$refs['jmsDataSources'].getSelected()
        logger.debug('save jms data, dataSource: %j, selected: %j.', dataSource, selected)
        if (dataSource) {
          if (selected && selected.length > 0) {
            // edit
            dataSources[selected[0]] = dataSource
          } else {
            // add
            dataSources.push(dataSource)
          }
          this.fillJmsDataSources(dataSources)
          this.$refs['jmsDataSources'].handleConfigForm('close')
        }
      },
      handleEditCache(index) {
        logger.debug('edit cache request, index: %d', index)
        let caches = this.formBasicInfo.caches
        if (caches && caches[index]) {
          this.$nextTick(_ => this.$refs['formCache'].setCache(caches[index]))
        } else {
          error('指定的缓存配置不存在!')
        }
      },
      handleDeleteCache(indexes) {
        logger.debug('delete cache request: %j.', indexes)
        if (indexes && indexes.length > 0) {
          let caches = this.formBasicInfo.caches
          if (caches) {
            // 倒序一下
            indexes = indexes.sort((a, b) => b - a)
            indexes.forEach(index => caches.splice(index, 1))
          }
          this.fillCaches(caches)
        }
      },
      handleResetCacheForm() {
        logger.debug('reset cache form.')
        this.$refs['formCache'].resetFields()
      },
      handleSaveCacheForm() {
        logger.debug('save cache form.')
        let caches = this.formBasicInfo.caches
        if (!caches) {
          caches = []
        }
        let cache = this.$refs['formCache'].getCache()
        let selected = this.$refs['caches'].getSelected()
        logger.debug('save cache data, cache: %j, selected: %j.', cache, selected)
        if (cache) {
          if (selected && selected.length > 0) {
            // edit
            caches[selected[0]] = cache
          } else {
            // add
            caches.push(cache)
          }
          this.fillCaches(caches)
          this.$refs['caches'].handleConfigForm('close')
        }
      },
      fillDestinations(destinations) {
        if (!destinations) {
          destinations = []
        }
        this.formBasicInfo.destinations = destinations
        this.$refs['destinations'].setList(this.formBasicInfo.destinations)
        this.cacheData()
      },
      fillJdbcDataSources(dataSources) {
        if (!dataSources) {
          dataSources = []
        }
        this.formBasicInfo.jdbcDataSources = dataSources
        this.$refs['jdbcDataSources'].setList(this.formBasicInfo.jdbcDataSources)
        this.setJdbcDataSources(dataSources)
      },
      fillJmsDataSources(dataSources) {
        if (!dataSources) {
          dataSources = []
        }
        this.formBasicInfo.jmsDataSources = dataSources
        this.$refs['jmsDataSources'].setList(this.formBasicInfo.jmsDataSources)
      },
      fillCaches(caches) {
        if (!caches) {
          caches = []
        }
        this.formBasicInfo.caches = caches
        this.$refs['caches'].setList(this.formBasicInfo.caches)
      }
    },
    mounted() {
      let {
        name, description, type, debug, messageTimeoutSecs, maxSpoutPending, destinations,
        zookeepers, jdbcDataSources, jmsDataSources, caches
      } = this.topology
      this.formBasicInfo = {
        name, description, type, debug, messageTimeoutSecs, maxSpoutPending, destinations,
        zookeepers, jdbcDataSources, jmsDataSources, caches
      }
    }
  }
</script>
