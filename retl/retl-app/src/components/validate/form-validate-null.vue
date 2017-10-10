<style rel="stylesheet/less" lang="less" scoped>
  @import "../../style/base.less";
</style>

<template>
  <el-form ref="formNullValidate" :model="formNullValidate" :rules="rulesNullValidate" label-width="100px">
    <el-form-item label="规则">
      <span>空值校验</span>
    </el-form-item>
    <el-form-item label="允许为空" prop="nullable">
      <el-switch v-model="formNullValidate.nullable" :width="80" on-text="允许" off-text="不允许" :disabled="mode === 'detail'"></el-switch>
    </el-form-item>
  </el-form>
</template>

<script>
  export default {
    name: 'pane-null-validate',
    props: ['rule', 'mode'],
    data() {
      return {
        formNullValidate: {
          type: this.rule.type,
          nullable: this.rule.nullable
        },
        rulesNullValidate: {
          nullable: []
        }
      }
    },
    methods: {
      getRule() {
        let {type, nullable} = this.formNullValidate
        return {type, nullable}
      },
      reset() {
        this.$refs['formNullValidate'].resetFields()
      }
    },
    mounted() {
      if (this.nullable === undefined) {
        this.formNullValidate.nullable = false
      }
    }
  }
</script>
