<style rel="stylesheet/less" lang="less">
  @import "../../../style/base.less";

  .form-row {
    height: 50px;
    padding: 0 20px 20px 0;
  }
</style>

<template>
  <el-form ref="formTable" :model="formTable" :rules="rulesTable" label-width="80px">
    <el-row type="flex">
      <el-col :span="12" class="form-row">
        <el-form-item label="表名" prop="table">
          <el-input v-model="formTable.table"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12" class="form-row">
        <el-form-item label="关键字" prop="key">
          <el-input v-model="formTable.key"></el-input>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row type="flex" class="form-row">
      <el-col :span="24">
        <el-form-item label="字段映射" prop="mapping">
          <ds-tag-both-sides v-model="formTable.listMapping" type="gray" sideSeparator="=>"></ds-tag-both-sides>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row type="flex" class="form-row">
      <el-col :span="24">
        <el-form-item label="特殊字段" prop="specialTypes">
          <ds-tag-both-sides v-model="formTable.listSpecialTypes" type="gray" sideSeparator=":"></ds-tag-both-sides>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>

<script>
  import {requiredRule, customRule} from '../../../assets/form-validate-rules'
  import {formValidateWarn} from '../../../assets/notify'
  import DsTagBothSides from '../../ds-tag-both-sides.vue'

  export default {
    name: 'form-table-config',
    components: {DsTagBothSides},
    data() {
      let mappingValidator = (rule, value, callback) => {
        value = this.formTable.listMapping
        if (value && value.length > 0) {
          callback()
        } else {
          callback(new Error('必须至少输入一个映射字段'))
        }
      }
      return {
        formTable: {table: '', key: '', listMapping: [], listSpecialTypes: []},
        rulesTable: {
          table: [requiredRule({msg: '必须输入表名'})],
          key: [requiredRule({msg: '必须输入关键字'})],
          mapping: [customRule({validator: mappingValidator})]
        }
      }
    },
    methods: {
      getTable() {
        let tb = null
        this.$refs['formTable'].validate(valid => {
          if (valid) {
            let {table, key, listMapping, listSpecialTypes} = this.formTable
            let mapping = {}
            let specialTypes = {}
            if (listMapping && listMapping.length > 0) {
              listMapping.forEach(row => {
                if (row) {
                  let sides = row.split('=>')
                  if (sides && sides.length === 2) {
                    mapping[sides[0]] = sides[1]
                  }
                }
              })
            }
            if (listSpecialTypes && listSpecialTypes.length > 0) {
              listSpecialTypes.forEach(row => {
                if (row) {
                  let sides = row.split(':')
                  if (sides && sides.length === 2) {
                    specialTypes[sides[0]] = sides[1]
                  }
                }
              })
            }
            tb = {table, key, mapping, specialTypes}
          } else {
            formValidateWarn(this)
          }
        })
        return tb
      },
      setTable(tb) {
        if (tb) {
          let {table, key, mapping, specialTypes} = tb
          let listMapping = []
          let listSpecialTypes = []
          if(mapping && Object.keys(mapping).length > 0) {
            Object.keys(mapping).forEach(field => {
              if (field) {
                listMapping.push(field + '=>' + mapping[field])
              }
            })
          }
          if (specialTypes && Object.keys(specialTypes).length > 0) {
            Object.keys(specialTypes).forEach(field => {
              if (field) {
                listSpecialTypes.push(field + ':' + specialTypes[field])
              }
            })
          }
          this.formTable = {table, key, listMapping, listSpecialTypes}
        }
      },
      resetFields() {
        this.$refs['formTable'].resetFields()
      }
    }
  }
</script>
