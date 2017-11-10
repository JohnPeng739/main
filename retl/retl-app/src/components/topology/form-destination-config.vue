<style rel="stylesheet/less" lang="less" scoped>
  @import "../../style/base.less";

  .form-row {
    padding: 10px 10px 20px 0;
  }
</style>

<template>
  <el-form ref="formDestination" :model="formDestination" :rules="rulesDestination" label-width="100px" class="layout-form">
    <el-row type="flex" class="form-row">
      <el-col :span="16">
        <el-form-item label="名称" prop="destinateName">
          <el-input v-model="formDestination.destinateName"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="是否主题" prop="isTopic">
          <el-switch v-model="formDestination.isTopic" on-text="是" off-text="否"></el-switch>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row type="flex" class="form-row">
      <el-col :span="24">
        <el-form-item label="设置条件">
          <el-switch v-model="formDestination.hasCondition"></el-switch>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row type="flex" v-if="formDestination.hasCondition" class="form-row">
      <el-col :span="8">
        <el-form-item label="条件字段" prop="field">
          <el-input v-model="formDestination.field"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="判断操作" prop="operate">
          <el-select v-model="formDestination.operate">
            <el-option label="等于" value="equals"></el-option>
            <el-option label="前缀" value="startsWith"></el-option>
            <el-option label="后缀" value="endsWith"></el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="值" prop="value">
          <el-input v-model="formDestination.value"></el-input>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row type="flex" class="form-row">
      <el-form-item label="包含内容" prop="includes">
        <ds-tag-normal v-model="formDestination.includes" type="gray"></ds-tag-normal>
      </el-form-item>
    </el-row>
  </el-form>
</template>

<script>
  import {mapGetters} from 'vuex'
  import {logger} from 'dsutils'
  import {get} from '../../assets/ajax'
  import {formValidateWarn} from '../../assets/notify'
  import {requiredRule, customRule} from '../../assets/form-validate-rules'
  import DsTagNormal from '../ds-tag-normal.vue'

  export default {
    name: 'pane-jms-destination-config',
    components: {DsTagNormal},
    data() {
      let serverFormalValidator = (rule, value, callback) => {
        if (value && this.checkServerFormal(value)) {
          callback()
        } else {
          callback(new Error('连接的服务器和端口号格式不正确，请检查数据。'))
        }
      }
      return {
        formDestination: {destinateName: '', isTopic: false, hasCondition: true, field: '', operate: 'equals', value: '', includes: []}
      }
    },
    computed: {
      ...mapGetters(['jmsTypes']),
      rulesDestination() {
        let rules = {
          destinateName: [requiredRule({msg: '必须输入JMS目标名称。'})]
        }
        if (this.formDestination.hasCondition) {
          rules.field = [requiredRule({msg: '必须输入待校验的字段名称。'})]
          rules.value = [requiredRule({msg: '必须输入校验的值。'})]
        }
        return rules
      }
    },
    methods: {
      getDestination() {
        let destination = null
        this.$refs['formDestination'].validate(valid => {
          if (valid) {
            let {destinateName, isTopic, hasCondition, field, operate, value, includes} = this.formDestination
            if (hasCondition) {
              let condition = {field, operate, value}
              destination = {destinateName, isTopic, condition, includes}
            } else {
              destination = {destinateName, isTopic, includes}
            }
          } else {
            formValidateWarn()
          }
        })
        return destination
      },
      setDestination(destination) {
        if (destination) {
          let {destinateName, isTopic, condition, includes} = destination
          let hasCondition = false
          let field = ''
          let operate = 'equals'
          let value = ''
          if (condition) {
            hasCondition = true
            field = condition.field
            operate = condition.operate
            value = condition.value
          }
          this.formDestination = {destinateName, isTopic, hasCondition, field, operate, value, includes}
        }
      },
      resetFields() {
        this.$refs['formDestination'].resetFields()
      }
    }
  }
</script>
