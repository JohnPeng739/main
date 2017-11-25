<style rel="stylesheet/less" lang="less" scoped>
  @import "../../style/base.less";
</style>

<template>
  <el-form ref="formInValidate" :model="formInValidate" :rules="rulesInValidate" label-width="100px">
    <el-form-item label="规则">
      <span>被包含(存在性)校验</span>
    </el-form-item>
    <el-form-item label="类型" prop="category">
      <el-select v-model="formInValidate.category">
        <el-option label="已缓存" value="CACHED"></el-option>
        <el-option label="实时" value="REAL"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item v-if="isReal" label="JDBC数据源" prop="dataSource">
      <el-select v-model="formInValidate.dataSource">
        <el-option v-for="item in jdbcDataSources" :key="item.name" :label="item.url" :value="item.name"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item v-if="isReal" label="SQL语句" prop="sql">
      <el-input type="textarea" v-model="formInValidate.sql" :row="3"></el-input>
    </el-form-item>
  </el-form>
</template>

<script>
  import {mapGetters} from 'vuex'
  import {requiredRule} from '../../assets/form-validate-rules'
  import {formValidateWarn} from '../../assets/notify'

  export default {
    name: 'form-in-validate',
    data() {
      return {
        formInValidate: {
          type: 'InValidate',
          category: 'CACHED',
          dataSource: '',
          sql: ''
        },
        ruleCategory: [requiredRule({msg: '请选择一种类型。', trigger: 'change'})],
        ruleDataSource: [requiredRule({msg: '请选择一个数据源。', trigger: 'change'})],
        ruleSql: [requiredRule({msg: '请输入实时检测的SQL语句。'})]
      }
    },
    computed: {
      ...mapGetters(['jdbcDataSources']),
      isReal() {
        return (this.formInValidate.category === 'REAL')
      },
      rulesInValidate() {
        if (this.formInValidate.category === 'CACHED') {
          return {category: this.ruleCategory}
        } else {
          return {category: this.ruleCategory, dataSource: this.ruleDataSource, sql: this.ruleSql}
        }
      }
    },
    methods: {
      getRule() {
        let rule = null
        this.$refs['formInValidate'].validate(valid => {
          if (valid) {
            let {type, category, dataSource, sql} = this.formInValidate
            if (category === 'CACHED') {
              rule = {type, category}
            } else {
              rule = {type, category, dataSource, sql}
            }
          } else {
            formValidateWarn()
          }
        })
        return rule
      },
      reset() {
        this.$refs['formInValidate'].resetFields()
      }
    }
  }
</script>
