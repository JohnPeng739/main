<style rel="stylesheet/less" lang="less" scoped>
  @import "../../style/base.less";
</style>

<template>
  <el-form ref="formRangeValidate" :model="formRangeValidate" :rules="rulesRangeValidate" label-width="100px">
    <el-form-item label="规则">
      <span>值范围校验</span>
    </el-form-item>
    <el-form-item label="最小值" prop="min">
      <el-input-number v-model="formRangeValidate.min" :min="-1" :controls="false" :disabled="mode ==='detail'"></el-input-number>
    </el-form-item>
    <el-form-item label="最大值" prop="max">
      <el-input-number v-model="formRangeValidate.max" :min="-1" :controls="false" :disabled="mode === 'detail'"></el-input-number>
    </el-form-item>
  </el-form>
</template>

<script>
  import {customRule} from '../../assets/form-validate-rules'
  import {formValidateWarn} from '../../assets/notify'

  export default {
    name: 'pane-range-validate',
    props: ['rule', 'mode'],
    data() {
      let rangeValidator = (rule, value, callback) => {
        let form = this.formRangeValidate
        if (form.min === -1 && form.max === -1) {
          callback(new Error('最大值和最小值至少要设置一个。'))
        }
        if (form.min >= 0 && form.max >= 0 && form.max < form.min) {
          callback(new Error('最大值小于最小值，设置错误。'))
        }
        callback()
      }
      return {
        formRangeValidate: {
          type: 'RangeValidate',
          min: this.rule.min,
          max: this.rule.max
        },
        rulesRangeValidate: {
          max: [customRule({validator: rangeValidator, trigger: 'change'})]
        }
      }
    },
    methods: {
      getRule() {
        let rule = null
        this.$refs['formRangeValidate'].validate(valid => {
          if (valid) {
            let {type, min, max} = this.formRangeValidate
            rule = {type, min, max}
          } else {
            formValidateWarn()
          }
        })
        return rule
      },
      reset() {
        this.$refs['formRangeValidate'].resetFields()
      }
    }
  }
</script>
