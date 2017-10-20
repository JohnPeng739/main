<style rel="stylesheet/less" lang="less">
  @import "../../../style/base.less";
</style>

<template>
  <el-form ref="formJdbcPersist" :model="formJdbcPersist" :rules="rulesJdbcPersist" label-width="100px">
    <el-row type="flex">
      <el-col :span="24">
        <el-form-item label="数据源" prop="dataSource">
          <el-select v-model="formJdbcPersist.dataSource">
            <el-option v-for="item in list" :key="item.name" :label="item.url" :value="item.name"></el-option>
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row type="flex">
      <el-col :span="24">
        <el-form-item label="存储表" prop="tables">
          <pane-table-config ref="paneTables" :list="formJdbcPersist.tables"
                                   v-on:delete="handleDeleteTable" v-on:edit="handleEditTable"
                                   v-on:reset="handleResetTableForm" v-on:save="handleSaveTableForm">
            <form-table-config ref="formTable" slot="form-config"></form-table-config>
          </pane-table-config>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>

<script>
  import {mapGetters, mapActions} from 'vuex'
  import {logger} from 'dsutils'
  import {requiredRule} from '../../../assets/form-validate-rules'
  import {error, formValidateWarn} from '../../../assets/notify'
  import PaneTableConfig from '../../pane-listable-config.vue'
  import FormTableConfig from './form-table-config.vue'

  export default {
    name: 'topology-persist-info',
    components: {PaneTableConfig, FormTableConfig},
    data () {
      return {
        list: [],
        formJdbcPersist: {dataSource: '', tables: []},
        rulesJdbcPersist: {
          dataSource: [requiredRule({msg: '必须选择一个数据源', trigger: 'change'})]
        }
      }
    },
    computed: {
      ...mapGetters(['jdbcDataSources', 'persist'])
    },
    methods: {
      ...mapActions(['setPersist']),
      validated() {
        let validated = false
        this.$refs['formJdbcPersist'].validate(valid => {
          if (valid) {
            validated = valid
          } else {
            formValidateWarn()
          }
        })
        return validated
      },
      cacheData() {
        let {dataSource, tables} = this.formJdbcPersist
        this.setPersist({dataSource, tables})
      },
      handleDeleteTable(indexes) {
        logger.debug('delete table config request: %j.', indexes)
        if (indexes && indexes.length > 0) {
          let tables = this.formJdbcPersist.tables
          if (tables) {
            // 倒序一下
            indexes = indexes.sort((a, b) => b - a)
            indexes.forEach(index  => tables.splice(index, 1))
          }
          this.setTables(tables)
        }
      },
      handleEditTable(index) {
        let tables = this.formJdbcPersist.tables
        if (tables && tables[index]) {
          this.$nextTick(_ => {this.$refs['formTable'].setTable(tables[index])})
        } else {
          error( '指定的表不存在。')
        }
      },
      handleResetTableForm() {
        logger.debug('reset table config form.')
        this.$refs['formTable'].resetFields()
      },
      handleSaveTableForm() {
        logger.debug('save table config form.')
        let tables = this.formJdbcPersist.tables
        if (!tables) {
          tables = []
        }
        let table = this.$refs['formTable'].getTable()
        let selected = this.$refs['paneTables'].getSelected()
        if (table) {
          if (selected && selected && selected.length === 1) {
            // edit
            tables[selected[0]] = table
          } else {
            tables.push(table)
          }
          logger.debug('Save tables: %j.', tables)
          this.setTables(tables)
          this.$refs['paneTables'].handleConfigForm('close')
        }
      },
      setTables(tables) {
        if (!tables) {
          tables = []
        }
        this.formJdbcPersist.tables = tables
        this.formJdbcPersist.tableEditIndex = null
        this.$refs['paneTables'].setList(tables)
      }
    },
    mounted() {
      this.$nextTick(_ => {
        let jdbcDataSources = this.jdbcDataSources
        if (jdbcDataSources) {
          this.list = jdbcDataSources
        }
        let persist = this.persist
        if (persist) {
          let {dataSource, tables} = persist
          logger.debug('load init data: %j.', tables)
          this.formJdbcPersist = {dataSource, tables}
          this.$refs['paneTables'].setList(tables)
        }
      })
    }
  }
</script>
