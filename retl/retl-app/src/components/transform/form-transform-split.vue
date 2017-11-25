<style rel="stylesheet/less" lang="less" scoped>
  @import "../../style/base.less";
</style>

<template>
  <el-form ref="formSplitTransform" :model="formSplitTransform" :rules="rulesSplitTransform" label-width="100px">
    <el-form-item label="规则">
      <span>字段分解转换</span>
    </el-form-item>
    <el-form-item label="列名" prop="columnName">
      <el-input v-model="formSplitTransform.columnName" :readonly="mode === 'detail'"></el-input>
    </el-form-item>
    <el-form-item label="待分解字段" prop="field">
      <el-input v-model="formSplitTransform.field" :readonly="mode === 'detail'"></el-input>
    </el-form-item>
    <el-form-item label="目标字段" prop="fields">
      <ds-tag-normal v-model="formSplitTransform.fields" type="gray" :disabled="mode === 'detail'"></ds-tag-normal>
    </el-form-item>
    <el-form-item label="分隔符" prop="separator">
      <el-select v-model="formSplitTransform.separator" :disabled="mode === 'detail'" filterable allow-create>
        <el-option label="半角逗号(,)" value=","></el-option>
        <el-option label="半角分号(;)" value=";"></el-option>
        <el-option label="半角空格( )" value=" "></el-option>
      </el-select>
    </el-form-item>
  </el-form>
</template>

<script>
  import {formValidateWarn} from '../../assets/notify'
  import {requiredRule, customRule} from '../../assets/form-validate-rules'
  import DsTagNormal from '../ds-tag-normal.vue'

  export default {
    name: 'pane-split-transform',
    props: ['columns', 'rule', 'mode'],
    components: {DsTagNormal},
    data() {
      let fieldsValidator = (rule, value, callback) => {
        if (value && value.length > 0) {
          callback()
        } else {
          callback(new Error('必须至少选择一个存放目标数据的字段。'))
        }
      }
      return {
        formSplitTransform: {
          type: this.rule.type,
          field:this.rule.field,
          columnName: this.rule.columnName,
          fields: this.rule.fields,
          separator: this.rule.separator
        },
        rulesSplitTransform: {
          columnName: [requiredRule({msg: '必须输入列名。'})],
          field: [requiredRule({msg: '必须输入待转换字段名称。'})],
          fields: [customRule({validator: fieldsValidator, trigger: 'change'})],
          separator: [requiredRule({msg: '必须至少选择一种数据分隔符号。', trigger: 'change'})]
        }
      }
    },
    methods: {
      getRule() {
        let rule = null
        this.$refs['formSplitTransform'].validate(valid => {
          if (valid) {
            let {type, columnName, fields, separator,field} = this.formSplitTransform
            rule = {type, columnName, fields, separator,field}
          } else {
            formValidateWarn()
          }
        })
        return rule
      },
      reset() {
        this.$refs['formSplitTransform'].resetField()
      }
    }
  }
</script>
