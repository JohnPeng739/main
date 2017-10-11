<style rel="stylesheet/less" lang="less" scoped>
  @import "../../../style/base.less";
</style>

<template>
  <div>
    <el-row type="flex">
      <el-col :span="24">
        <span class="layout-buttons">
          <el-dropdown trigger="click" @command="handleAddRule">
            <el-button class="button" :plain="true" type="text">
              <ds-icon class="button-icon" name="add"></ds-icon>
              添加
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item v-for="item in transformTypes" :key="item.value"
                                :command="item.value">{{item.label}}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <el-button class="button" :plain="true" type="text" @click="handleOperate('edit')">
            <ds-icon class="button-icon" name="edit"></ds-icon>
            修改
          </el-button>
          <el-button class="button" :plain="true" type="text" @click="handleOperate('delete')">
            <ds-icon class="button-icon" name="delete"></ds-icon>
            删除
          </el-button>
          <el-button class="button" :plain="true" type="text" @click="handleOperate('detail')">
            <ds-icon class="button-icon" name="subject"></ds-icon>
            详情
          </el-button>
        </span>
      </el-col>
    </el-row>
    <el-row type="flex">
      <el-col :span="24">
        <el-table ref="tableTransformRule" :data="tableData" highlightCurrentRow @current-change="handleCurrentChange"
                  stripe style="width: 100%;">
          <el-table-column prop="columnName" label="字段名称" width="150"></el-table-column>
          <el-table-column prop="type" label="转换类型" width="200">
            <template scope="scope">
              {{typeLabel('transformTypes', scope.row.type)}}
            </template>
          </el-table-column>
          <el-table-column prop="conf" label="配置信息">
            <template scope="scope">{{scope.row.conf}}</template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
    <keep-alive v-if="!dialogDestroyed">
      <dialog-transform-info ref="dialogTransformInfo" v-on:submit="handleDialogSubmit"
                             v-on:close="handleDialogClose"></dialog-transform-info>
    </keep-alive>
  </div>
</template>

<script>
  import {mapGetters, mapActions} from 'vuex'
  import {getTypeLabel} from '../../../modules/manage/store/modules/types'
  import DsIcon from "../../icon.vue"
  import {logger} from 'dsutils'
  import {get} from '../../../assets/ajax'
  import {error, info} from '../../../assets/notify'
  import DialogTransformInfo from './dialog-transform-rule.vue'

  export default {
    name: 'topology-transforms-info',
    components: {DsIcon, DialogTransformInfo},
    data() {
      return {
        tableData: [],
        currentRow: null,
        dialogDestroyed: false,
      }
    },
    computed: {
      ...mapGetters(['transformTypes', 'columns', 'transforms'])
    },
    methods: {
      ...mapActions(['setTransforms']),
      typeLabel(type, value) {
        return getTypeLabel(type, value)
      },
      validated() {
        // 允许不配置转换规则
        return true
      },
      cacheData() {
        let transforms = {}
        let tableData = this.tableData
        if (tableData && tableData.length > 0) {
          tableData.forEach(row => {
            let {columnName, conf} = row
            transforms[columnName] = conf
          })
        }
        this.setTransforms(transforms)
      },
      fillTableData() {
        let tableData = []
        let transforms = this.transforms
        if (transforms) {
          Object.keys(transforms).forEach(name => {
            let rule = transforms[name]
            if (rule) {
              let {type} = rule
              tableData.push({columnName: name, type, conf: rule})
            }
          })
        }
        this.tableData = tableData
      },
      createDefaultRule(type) {
        switch (type) {
          case 'FormulaTransform':
            return {columnName: '', type, calculate: ''}
          case 'MergeTransform':
            return {columnName: '', type, fields: [], separator: ','}
        }
        return null
      },
      handleAddRule(command) {
        logger.debug(' add rule, command: %s.', command)
        if (command) {
          let rule = this.createDefaultRule(command)
          if (rule) {
            this.$refs['dialogTransformInfo'].show('add', this.columns, rule)
          } else {
            error('不支持的规则类型： ' + command + '。')
          }
        }
      },
      removeData(columnName) {
        let tableData = this.tableData
        let indexes = []
        tableData.forEach((row, index) => {
          if (row.columnName === columnName) {
            indexes.push(index)
          }
        })
        if (indexes.length > 0) {
          // 删除前倒序一下
          indexes = indexes.sort((a, b) => b - a)
          indexes.forEach(index => tableData.splice(index, 1))
          this.tableData = tableData
        }
      },
      handleOperate(operate) {
        let currentRow = this.currentRow
        if (currentRow) {
          logger.debug('current row: %j.', currentRow)
          let {columnName} = currentRow
          if (operate === 'delete') {
            this.removeData(columnName)
          } else {
            let rule = currentRow.conf
            rule.columnName = columnName
            this.$refs['dialogTransformInfo'].show(operate, this.columns, rule)
          }
        } else {
          info('在操作之前你需要选中一个现有的规则。')
          return
        }
      },
      handleDialogSubmit(mode, rule) {
        logger.debug('dialog submit, mode: %s, rule: %j.', mode, rule)
        let tableData = this.tableData
        if (mode && rule) {
          if (mode === 'add') {
            let {columnName, type} = rule
            let conf = rule
            delete conf.columnName
            tableData.push({columnName, type, conf})
          } else if (mode === 'edit') {
            let oldColumnName = this.currentRow.columnName
            let {columnName, type} = rule
            let conf = rule
            delete conf.columnName
            let tableData = this.tableData
            tableData.forEach((row, index) => {
              if (row.columnName === oldColumnName) {
                tableData[index] = {columnName, type, conf}
                return
              }
            })
          }
          logger.debug('submit data, %j.', tableData)
          this.tableData = []
          this.$nextTick(_ => this.tableData = tableData)
        }
      },
      handleDialogClose() {
        this.dialogDestroyed = true
        setTimeout(_ => {
          this.dialogDestroyed = false
        }, 100)
      },
      handleCurrentChange(val) {
        this.currentRow = val
      }
    },
    mounted() {
      this.fillTableData()
    }
  }
</script>
