<style rel="stylesheet/less" lang="less" scoped>
  @import "../../style/base.less";
</style>

<template>
  <el-form ref="formTypeValidate" :model="formTypeValidate" :rules="rulesTypeValidate" label-width="100px">
    <el-form-item label="规则">
      <span>类型校验</span>
    </el-form-item>
    <el-form-item label="值类型" prop="valueType">
      <el-select v-model="formTypeValidate.valueType" :disabled="mode === 'detail'">
        <el-option v-for="item in validateRuleTypes" :key="item.value" :label="item.label" :value="item.value"></el-option>
      </el-select>
    </el-form-item>
  </el-form>
</template>

<script>
  import {mapGetters} from 'vuex'
  import {requiredRule} from '../../assets/form-validate-rules'
  import {formValidateWarn} from '../../assets/notify'
  import {get} from '../../assets/ajax'
  import {logger} from 'mx-app-utils'

  export default {
    name: 'pane-type-validate',
    props: ['rule', 'mode'],
    data() {
      return {
        formTypeValidate: {
          type: this.rule.type,
          valueType: this.rule.valueType
        },
        rulesTypeValidate: {
          valueType: [requiredRule({message: '必须选择一种数据类型。', trigger: 'change'})]
        }
      }
    },
    computed: {
      ...mapGetters(['validateRuleTypes'])
    },
    methods: {
      getRule() {
        let rule = null
        this.$refs['formTypeValidate'].validate(valid => {
          if (valid) {
            let {type, valueType} = this.formTypeValidate
            rule = {type, valueType}
          } else {
            formValidateWarn()
          }
        })
        return rule
      },
      reset() {
        this.$refs['formTypeValidate'].resetFields()
      }
    }
  }
</script>
