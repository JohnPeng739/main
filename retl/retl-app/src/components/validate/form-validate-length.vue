<style rel="stylesheet/less" lang="less" scoped>
  @import "../../style/base.less";
</style>

<template>
  <el-form ref="formLengthValidate" :model="formLengthValidate" :rules="rulesLengthValidate" label-width="100px">
    <el-form-item label="规则">
      <span>长度校验</span>
    </el-form-item>
    <el-form-item label="最小长度" prop="min">
      <el-input-number v-model="formLengthValidate.min" :min="-1" :controls="false" :disabled="mode === 'detail'"></el-input-number>
    </el-form-item>
    <el-form-item label="最大长度" prop="max">
      <el-input-number v-model="formLengthValidate.max" :min="-1" :controls="false" :disabled="mode === 'detail'"></el-input-number>
    </el-form-item>
  </el-form>
</template>

<script>
  import {formValidateWarn} from '../../assets/notify'
  import {customRule} from '../../assets/form-validate-rules'

  export default {
    name: 'pane-length-validate',
    props: ['rule', 'mode'],
    data() {
      let lengthValidator = (rule, value, callback) => {
        let form = this.formLengthValidate
        if (form.min === -1 && form.max === -1) {
          callback(new Error('最大长度和最小长度至少要设置一个。'))
        }
        if (form.min >= 0 && form.max >= 0 && form.max < form.min) {
          callback(new Error('最大长度小于最小长度，设置错误。'))
        }
        callback()
      }
      return {
        formLengthValidate: {
          type: 'LengthValidate',
          min: this.rule.min,
          max: this.rule.max
        },
        rulesLengthValidate: {
          max: [customRule({validator: lengthValidator, trigger: 'change'})]
        }
      }
    },
    methods: {
      getRule() {
        let rule = null
        this.$refs['formLengthValidate'].validate(valid => {
          if (valid) {
            let {type, min, max} = this.formLengthValidate
            rule = {type, min, max}
          } else {
            formValidateWarn()
          }
        })
        return rule
      },
      reset() {
        this.$refs['formLengthValidate'].resetFields()
      }
    }
  }
</script>
