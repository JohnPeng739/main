<style rel="stylesheet/less" lang="less" scoped>
  @import "../../style/base.less";
</style>

<template>
  <el-form ref="formSubStringTransform" :model="formSubStringTransform" :rules="rulesSubStringTransform" label-width="100px">
    <el-form-item label="规则">
      <span>字符串子串转换</span>
    </el-form-item>
    <el-form-item label="字段名称" prop="columnName">
      <el-input v-model="formSubStringTransform.columnName" :readonly="mode === 'detail'"></el-input>
    </el-form-item>
    <el-form-item label="被截取字段" prop="field">
      <el-input v-model="formSubStringTransform.field" :readonly="mode === 'detail'"></el-input>
    </el-form-item>
    <el-form-item label="截取起始位置" prop="startPos">
      <el-input-number v-model="formSubStringTransform.startPos" :controls="false" :min="0" :disabled="mode === 'detail'"></el-input-number>
    </el-form-item>
    <el-form-item label="截取结束位置" prop="endPos">
      <el-input-number v-model="formSubStringTransform.endPos" :controls="false" :disabled="mode === 'detail'"></el-input-number>
    </el-form-item>
  </el-form>
</template>

<script>
  import {requiredRule, rangeRule, customRule} from '../../assets/form-validate-rules'
  import {formValidateWarn} from '../../assets/notify'
  import {logger} from 'mx-app-utils'

  export default {
    name: 'pane-sub-string-validate',
    props: ['columns', 'rule', 'mode'],
    data() {
      let endPosValidator = (rule, value, callback) => {
        let {startPos, endPos} = this.formSubStringTransform
        if (endPos > startPos) {
          callback()
        } else {
          callback(new Error('截取结束位置必须大于截取开始位置。'))
        }
      }
      return {
        formSubStringTransform: {
          type: this.rule.type,
          columnName: this.rule.columnName,
          field: this.rule.field,
          startPos: this.rule.startPos,
          endPos: this.rule.endPos
        },
        rulesSubStringTransform: {
          columnName: [requiredRule({msg: '必须输入转换字段名称。'})],
          field: [requiredRule({msg: '必须输入被截取字段名称。'})],
          endPos: [customRule({validator: endPosValidator})]
        }
      }
    },
    methods: {
      getRule() {
        let rule = null
        this.$refs['formSubStringTransform'].validate(valid => {
          if (valid) {
            let {type, columnName, field, startPos, endPos} = this.formSubStringTransform
            rule = {type, columnName, field, startPos, endPos}
          } else {
            formValidateWarn()
          }
        })
        return rule
      },
      reset() {
        this.$refs['formSubStringTransform'].resetFields()
      }
    }
  }
</script>
