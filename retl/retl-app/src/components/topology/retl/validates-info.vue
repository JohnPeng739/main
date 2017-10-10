<style rel="stylesheet/less" lang="less" scoped>
  @import "../../../style/base.less";
</style>

<template>
  <div>
    <el-table :data="tableData" :row-key="getRowKeys" :expand-row-keys="expands" style="width: 100%">
      <el-table-column type="expand">
        <template scope="props">
          <el-row v-for="item in validateDefines[props.row.name]" :key="item.type" type="flex">
            <el-col :span="4">{{getRuleName(item.type)}}</el-col>
            <el-col :span="16">{{JSON.stringify(item)}}</el-col>
            <el-col :span="4">
              <el-button type="text" @click="handleRuleOperate(props.row.name, item.type, 'edit')">修改</el-button>
              <el-button type="text" @click="handleRuleOperate(props.row.name, item.type, 'delete')">删除</el-button>
              <el-button type="text" @click="handleRuleOperate(props.row.name, item.type, 'detail')">详情</el-button>
            </el-col>
          </el-row>
        </template>
      </el-table-column>
      <el-table-column label="字段列" width="200" prop="name"></el-table-column>
      <el-table-column label="字段描述" prop="desc"></el-table-column>
      <el-table-column label="状态" width="200">
        <template scope="props">
          <span>{{getRowState(props.row)}}</span>
          <el-dropdown trigger="click" @command="handleAddRule" style="padding-left: 10px;">
            <el-button type="text">添加校验</el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item v-for="item in supported" :key="item.value"
                                :command="props.row.name + ':' + item.value">{{item.label}}
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>
    <keep-alive v-if="!dialogDestroyed">
      <dialog-validate-rule ref="dialogValidateRule" v-on:submit="handleDialogSubmit"
                            v-on:close="handleDialogClose"></dialog-validate-rule>
    </keep-alive>
  </div>
</template>

<script>
  import {mapGetters, mapActions} from 'vuex'
  import {logger} from 'dsutils'
  import {get} from '../../../assets/ajax'
  import {error, warn} from '../../../assets/notify'
  import DialogValidateRule from './dialog-validate-rule.vue'

  export default {
    name: 'topology-validates-info',
    components: {DialogValidateRule},
    data() {
      return {
        supported: [],
        dialogDestroyed: false,
        columnDefines: [],
        validateDefines: {},
        tableData: null,
        getRowKeys(row) {
          return row.name
        },
        getRowState(row) {
          let state = '没有配置'
          if (row.rules && row.rules.length > 0) {
            state = row.rules.length + '个规则'
          }
          return state
        },
        expands: []
      }
    },
    computed: {
      ...mapGetters(['columns', 'validates'])
    },
    methods: {
      ...mapActions(['setValidates']),
      validated() {
        // 允许不配置校验规则
        return true
      },
      cacheData() {
        let validates = {}
        let tableData = this.tableData
        if (tableData && tableData.length > 0) {
          tableData.forEach(column => {
            let name = column.name
            let rules = null
            if (column.rules) {
              rules = column.rules
            }
            if (rules) {
              validates[name] = rules
            }
          })
        }
        this.setValidates(validates)
      },
      fillTableData() {
        let columns = this.columns
        let validateDefines = this.validates
        if (!validateDefines) {
          validateDefines = {}
        }
        let tableData = []
        columns.forEach(column => {
          if (column) {
            let name = column.name
            let desc = column.desc
            let rules = null
            if (validateDefines) {
              rules = validateDefines[name]
            }
            if (!rules) {
              rules = []
            }
            tableData.push({name, desc, rules})
          }
        })
        this.validateDefines = validateDefines
        this.tableData = tableData
      },
      findColumn(columns, columnName) {
        let columnIndex = -1
        if (columns && columnName) {
          columns.forEach((column, index) => {
            if (column && column.name === columnName) {
              columnIndex = index
              return
            }
          })
        }
        return columnIndex
      },
      findRule(rules, ruleType) {
        let ruleIndex = -1
        if (rules && ruleType) {
          rules.forEach((rule, index) => {
            if (rule.type === ruleType) {
              ruleIndex = index
              return
            }
          })
        }
        return ruleIndex
      },
      handleAddRule(command) {
        logger.debug("add rule, command: %s.", command)
        if (command) {
          let sides = command.split(':')
          if (sides && sides.length === 2) {
            let columnName = sides[0]
            let type = sides[1]
            let rules = this.getRules(columnName)
            let index = this.findRule(rules, type)
            if (index >= 0) {
              warn('字段[' + columnName + ']已经包含了[' + this.getRuleName(type) + ']规则。')
              return
            }
            let rule = this.createDefaultRule(type)
            if (rule) {
              this.$refs['dialogValidateRule'].show('add', columnName, rule)
            } else {
              error('不支持的规则类型： ' + type + '。')
            }
          }
        }
      },
      createDefaultRule(type) {
        switch (type) {
          case 'NullValidate':
            return {type: type, nullable: false}
          case 'LengthValidate':
            return {type: type, min: -1, max: -1}
          case 'TypeValidate':
            return {type: type, valueType: 'STRING'}
          case 'RangeValidate':
            return {type: type, min: -1, max: -1}
          case 'RegExpValidate':
            return {type: type, regexp: ''}
        }
        return null
      },
      handleRuleOperate(name, type, mode) {
        logger.debug("%s rule, name: %s, type: %s.", mode, name, type)
        if (mode === 'delete') {
          this.tableData.forEach((column, indexColumn) => {
            if (column && column.name === name) {
              let rules = column.rules
              if (rules) {
                rules.forEach((rule, indexRule) => {
                  if (rule.type === type) {
                    rules.splice(indexRule, 1)
                    this.tableData[indexColumn].rules = rules
                    return
                  }
                })
              }
            }
          })
        } else {
          let selectedRule = null
          this.tableData.forEach(column => {
            if (column && column.name === name) {
              let rules = column.rules
              if (rules) {
                rules.forEach(rule => {
                  if (rule.type === type) {
                    selectedRule = rule
                    return
                  }
                })
              }
            }
          })
          if (selectedRule) {
            this.$refs['dialogValidateRule'].show(mode, name, selectedRule)
          }
        }
      },
      handleDialogSubmit(mode, columnName, rule) {
        logger.debug('%s the validate rule, column: %s, rule: %j.', mode, columnName, rule)
        if (rule) {
          let rules = this.getRules(columnName)
          if (!rules) {
            rules = []
          }
          let index = this.findColumn(this.tableData, columnName)
          if (index >= 0) {
            if (mode === 'add') {
              rules.push(rule)
              this.tableData[index].rules = rules
            } else {
              let ruleIndex = this.findRule(rules, rule.type)
              if (ruleIndex >= 0) {
                rules[ruleIndex] = rule
              } else {
                warn('字段[' + columnName + ']的规则[' + rule.type + ']不存在。')
                return
              }
            }
            this.validateDefines[columnName] = rules
            this.expands = [columnName]
            this.handleDialogClose()
          } else {
            warn('设置的字段[' + columnName + ']不存在。')
            return
          }
        }
      },
      handleDialogClose() {
        this.dialogDestroyed = true
        setTimeout(_ => {
          this.dialogDestroyed = false
        }, 100)
      },
      getColumn(columnName) {
        let column = null
        this.tableData.forEach(col => {
          if (col && col.name === columnName) {
            column = col
            return
          }
        })
        return column
      },
      getRules(columnName) {
        let rules = []
        let column = this.getColumn(columnName)
        if (column && column.rules) {
          rules = column.rules
        }
        return rules
      },
      getRuleName(type) {
        let name = ''
        this.supported.forEach(support => {
          if (support && support.value === type) {
            name = support.label
            return
          }
        })
        return name
      }
    }
    ,
    mounted() {
      let url = '/rest/topology/validates/supported'
      logger.debug('send GET "%s"', url)
      get(url, data => {
        logger.debug(data)
        this.supported = data
      })
      this.fillTableData()
    }
  }
</script>
