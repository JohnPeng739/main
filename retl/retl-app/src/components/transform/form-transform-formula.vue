<style rel="stylesheet/less" lang="less">
  @import "../../style/base.less";
</style>

<template>
  <el-form ref="formFormulaTransform" :model="formFormulaTransform" :rules="rulesFormulaTransform" label-width="100px">
    <el-form-item label="规则">
      <span>四则运算转换</span>
    </el-form-item>
    <el-form-item label="字段名称" prop="columnName">
      <el-input v-model="formFormulaTransform.columnName" :readonly="mode === 'detail'"></el-input>
    </el-form-item>
    <el-form-item label="运算公式" prop="calculate">
      <el-input v-model="formFormulaTransform.calculate" :readonly="mode === 'detail'"></el-input>
    </el-form-item>
  </el-form>
</template>

<script>
  import {requiredRule, customRule} from '../../assets/form-validate-rules'
  import {formValidateWarn} from '../../assets/notify'
  import {logger} from 'dsutils'

  export default {
    name: 'pane-type-validate',
    props: ['columns', 'rule', 'mode'],
    data() {
      let fieldExistValidator = (rule, value, callback) => {
        let {found, fields} = this.fieldExist(value)
        if (found) {
          callback()
        } else {
          callback(new Error('公式中的部分字段['+ fields.join(',') + ']在已经定义的数据列中不存在。'))
        }
      }
      return {
        definedFields: [],
        formFormulaTransform: {
          type: this.rule.type,
          columnName: this.rule.columnName,
          calculate: this.rule.calculate
        },
        rulesFormulaTransform: {
          columnName: [requiredRule({msg: '必须输入计算字段名称。'})],
          calculate: [requiredRule({msg: '必须输入四则运算公式。'}), customRule({validator: fieldExistValidator})]
        }
      }
    },
    methods: {
      fieldExist(value) {
        // 判断公式中的相关字段是否已经提前被定义
        let formula = value
        let columns = this.definedFields
        if (columns) {
          if (formula) {
            formula = formula.replace(/\+/g, ' ').replace(/-/g, ' ').replace(/\*/g, ' ').replace(/\//g, ' ')
              .replace(/\(/g, ' ').replace(/\)/g, ' ')
            let fields = formula.split(' ')
            let found = true
            let list = []
            if (fields && fields.length > 0) {
              fields.forEach(field => {
                if (columns.indexOf(field) === -1 && isNaN(field)) {
                  found = false
                  list.push(field)
                }
              })
            }
            return {found, fields: list}
          }
        }
        return {found: false, fields: ['*']}
      },
      getRule() {
        let rule = null
        this.$refs['formFormulaTransform'].validate(valid => {
          if (valid) {
            let {type, columnName, calculate} = this.formFormulaTransform
            rule = {type, columnName, calculate}
          } else {
            formValidateWarn(this)
          }
        })
        return rule
      },
      reset() {
        this.$refs['formFormulaTransform'].resetFields()
      }
    },
    mounted() {
      // 转换column
      let columns = this.columns
      if (columns && columns.length > 0) {
        columns.forEach(column => {
          if (column) {
            this.definedFields.push(column.name)
          }
        })
      }
    }
  }
</script>
