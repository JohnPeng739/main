<style rel="stylesheet/less" lang="less" scoped>
  @import "../../style/base.less";
</style>

<template>
  <el-form ref="formMergeTransform" :model="formMergeTransform" :rules="rulesMergeTransform" label-width="100px">
    <el-form-item label="规则">
      <span>字段合并转换</span>
    </el-form-item>
    <el-form-item label="新字段名称" prop="columnName">
      <el-input v-model="formMergeTransform.columnName" :readonly="mode === 'detail'"></el-input>
    </el-form-item>
    <el-form-item label="被合并字段" prop="fields">
      <el-select v-model="formMergeTransform.fields" multiple :disabled="mode === 'detail'" filterable>
        <el-option v-for="item in columns" :key="item.name" :label="item.name + ': ' + item.desc" :value="item.name"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="分隔符" prop="separator">
      <el-select v-model="formMergeTransform.separator" :disabled="mode === 'detail'" filterable allow-create>
        <el-option label="半角逗号(,)" value=","></el-option>
        <el-option label="半角分号(;)" value=";"></el-option>
        <el-option label="半角空格( )" value=" "></el-option>
      </el-select>
    </el-form-item>
  </el-form>
</template>

<script>
  import {requiredRule, customRule} from '../../assets/form-validate-rules'
  import {formValidateWarn} from '../../assets/notify'
  import {logger} from 'mx-app-utils'

  export default {
    name: 'pane-merge-transform',
    props: ['columns', 'rule', 'mode'],
    data() {
      let fieldsValidator = (rule, value, callback) => {
        if (value && value.length > 0) {
          callback()
        } else {
          callback(new Error('必须至少选择一个被合并的字段。'))
        }
      }
      return {
        formMergeTransform: {
          type: this.rule.type,
          columnName: this.rule.columnName,
          fields: this.rule.fields,
          separator: this.rule.separator
        },
        rulesMergeTransform: {
          columnName: [requiredRule({msg: '必须输入转换字段名称。'})],
          fields: [customRule({validator: fieldsValidator, trigger: 'change'})],
          separator: [requiredRule({msg: '必须至少选择一种分隔符号。', trigger: 'change'})]
        }
      }
    },
    methods: {
      getRule() {
        let rule = null
        this.$refs['formMergeTransform'].validate(valid => {
          if (valid) {
            let {type, columnName, fields, separator} = this.formMergeTransform
            rule = {type, columnName, fields, separator}
          } else {
            formValidateWarn()
          }
        })
        return rule
      },
      reset() {
        this.$refs['formMergeTransform'].resetFields()
      }
    }
  }
</script>
