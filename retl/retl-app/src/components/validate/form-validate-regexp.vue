<style rel="stylesheet/less" lang="less" scoped>
  @import "../../style/base.less";

  .title {
    font-weight: 800;
  }
  .content {
    color: #2c3e50;
  }
</style>

<template>
  <el-form ref="formRegExpValidate" :model="formRegExpValidate" :rules="rulesRegExpValidate" label-width="100px">
    <el-form-item label="规则">
      <span>正则表达式校验</span>
    </el-form-item>
    <el-form-item label="正则表达式" prop="regexp">
      <el-input v-model="formRegExpValidate.regexp" :readonly="mode === 'detail'"></el-input>
    </el-form-item>
    <el-form-item label="常见表达式">
      <el-row v-for="item in regexSamples" :key="item.name">
        <el-col :span="4" class="title content">{{item.name}}：</el-col>
        <el-col :span="20" class="content">{{item.sample}}</el-col>
      </el-row>
    </el-form-item>
  </el-form>
</template>

<script>
  import {formValidateWarn} from '../../assets/notify'
  import {requiredRule} from '../../assets/form-validate-rules'
  import {get} from '../../assets/ajax'
  import {logger} from 'dsutils'

  export default {
    name: 'pane-regexp-validate',
    props: ['rule', 'mode'],
    data() {
      return {
        formRegExpValidate: {
          type: this.rule.type,
          regexp: this.rule.regexp
        },
        rulesRegExpValidate: {
          regexp: [requiredRule({message: '必须输入正则表达式。'})]
        }
      }
    },
    computed: {
      regexSamples() {
        return [{
          name: '电子邮件',
          sample: '(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)'
        }, {
          name: 'IP地址',
          sample: '(\\d)\\.(\\d)\\.(\\d)\\.(\\d)'
        }]
      }
    },
    methods: {
      getRule() {
        let rule = null
        this.$refs['formRegExpValidate'].validate(valid => {
          if (valid) {
            let {type, regexp} = this.formRegExpValidate
            rule = {type, regexp}
          } else {
            formValidateWarn()
          }
        })
        return rule
      },
      reset() {
        this.$refs['formRegExpValidate'].resetFields()
      }
    }
  }
</script>
