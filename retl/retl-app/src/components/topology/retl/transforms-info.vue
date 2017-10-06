<style rel="stylesheet/less" lang="less">
  @import "../../../style/base.less";

  .layout-buttons {
    display: inline-block;
    float: right;
    margin: 3px;
    .button {
      padding-left: 10px;
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
              <el-dropdown-item v-for="item in supported" :key="item.value" :command="item.value">{{item.label}}</el-dropdown-item>
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
              {{getTypeName(scope.row.type)}}
            </template>
          </el-table-column>
          <el-table-column prop="conf" label="配置信息"></el-table-column>
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
  import DsIcon from "../../icon.vue"
  import {logger} from 'dsutils'
  import {get} from '../../../assets/ajax'
  import {error, info} from '../../../assets/notify'
  import DialogTransformInfo from './dialog-transform-rule.vue'

  export default {
    name: 'topology-transforms-info',
    props: ['topology'],
    components: {DsIcon, DialogTransformInfo},
    data() {
      return {
        supported: [],
        transforms: this.topology.transforms,
        tableData: [],
        currentRow: null,
        dialogDestroyed: false,
        getTypeName(type) {
          let name = ''
          let supported = this.supported
          if (supported && supported.length > 0) {
            supported.forEach(item => {
              if (item && item.value === type) {
                name = item.label
                return
              }
            })
          }
          return name
        }
      }
    },
    methods: {
      fillTableData() {
        let list = []
        let transforms = this.transforms
        if (transforms) {
          Object.keys(transforms).forEach(name => {
            let rule = transforms[name]
            if (rule) {
              let {type} = rule
              list.push({columnName: name, type, conf: JSON.stringify(rule)})
            }
          })
        }
        this.tableData = list
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
            this.$refs['dialogTransformInfo'].show('add', this.topology.columns, rule)
          } else {
            error(this, '不支持的规则类型： ' + command + '。')
          }
        }
      },
      handleOperate(operate) {
        let currentRow = this.currentRow
        if (currentRow) {
          let {columnName} = currentRow
          if (operate === 'delete') {
            delete this.transforms[columnName]
            this.fillTableData()
          } else {
            let rule = JSON.parse(currentRow.conf)
            rule.columnName = columnName
            rule.oldColumnName = columnName
            this.$refs['dialogTransformInfo'].show(operate, this.topology.columns, rule)
          }
        } else {
          info(this, '在操作之前你需要选中一个现有的规则。')
          return
        }
      },
      handleDialogSubmit(mode, rule) {
        logger.debug('dialog submit, mode: %s, rule: %j.', mode, rule)
        if (mode && rule) {
          if (this.transforms === null || this.transforms === undefined) {
            this.transforms = {}
          }
          let {columnName, oldColumnName} = rule
          delete rule.columnName
          delete rule.oldColumnName
          if (oldColumnName) {
            // edit
            this.transforms[oldColumnName] = rule
          } else {
            // add
            this.transforms[columnName] = rule
          }
          this.fillTableData()
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
      },
      getTransformsInfo() {
        let transforms = this.transforms
        if (transforms && Object.keys(transforms).length > 0) {
          return transforms
        }
        return {}
      },
      setTopology(topology) {
        if (topology) {
          this.transforms = topology.transforms
        }
      }
    },
    mounted() {
      if (this.topology) {
        let {transforms} = this.topology
        if (transforms) {
          this.transforms = transforms
          this.fillTableData()
        }
      }
      let url = '/rest/topology/transforms/supported'
      logger.debug('send GET "%s"', url)
      get(url, data => {
        if (data) {
          this.supported = data
        }
      })
    }
  }
</script>
