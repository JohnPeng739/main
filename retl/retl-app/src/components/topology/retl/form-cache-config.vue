<style rel="stylesheet/less" lang="less" scoped>
  @import "../../../style/base.less";

  .form-row {
    padding: 0 10px 20px 0;
  }
</style>

<template>
  <el-form ref="formCache" :model="formCache" :rules="rulesCache" label-width="100px">
    <el-row type="flex" class="form-row">
      <el-col :span="12">
        <el-form-item label="列名" prop="columnName">
          <el-input v-model="formCache.columnName"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="类型" prop="type">
          <el-select v-model="formCache.type">
            <el-option label="静态字典" value="STATIC"></el-option>
            <el-option label="数据库字典" value="JDBC"></el-option>
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row v-if="isStatic" type="flex" class="form-row">
      <el-col :span="18">
        <el-form-item label="字典" prop="dataEnum">
          <el-input v-model="formCache.dataEnum"></el-input>
          <span>值之间使用半角逗号(,)分隔！</span>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row v-if="!isStatic" type="flex" class="form-row">
      <el-col :span="12">
        <el-form-item label="数据源" prop="dataSource">
          <el-select v-model="formCache.dataSource">
            <el-option v-for="item in jdbcDataSources" :key="item.name" :label="item.url" :value="item.name"></el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="间隔时间" prop="intervalSec">
          <el-input-number v-model="formCache.intervalSec" :min="5" :step="10" size="small"></el-input-number>
          <span>秒</span>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row v-if="!isStatic" type="flex" class="form-row">
      <el-col :span="24">
        <el-form-item label="SQL语句" prop="sql">
          <el-input type="textarea" v-model="formCache.sql" :rows="4"></el-input>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>

<script>
  import {mapGetters} from 'vuex'
  import {logger} from 'mx-app-utils'
  import {get} from '../../../assets/ajax'
  import {formValidateWarn} from '../../../assets/notify'
  import {requiredRule, customRule} from '../../../assets/form-validate-rules'

  export default {
    name: 'pane-cache-config',
    data() {
      return {
        methodSupported: [],
        formCache: {columnName: '', type: 'STATIC', dataEnum: '', dataSource: '', sql: '', intervalSec: 60},
        ruleColumnName: [requiredRule({msg: '必须输入列名。'})],
        ruleType: [requiredRule({msg: '必须选择一个类型。', trigger: 'change'})],
        ruleDataEnum: [requiredRule({msg: '必须输入静态数据字典内容。'})],
        ruleDataSource: [requiredRule({msg: '必须选择一个JDBC数据源。', trigger: 'change'})],
        ruleSql: [requiredRule({msg: '必须输入缓存字典的SQL语句。'})]
      }
    },
    computed: {
      ...mapGetters(['jdbcDataSources']),
      rulesCache() {
        if (this.formCache.type === 'STATIC') {
          return {columnName: this.ruleColumnName, type: this.ruleType, dataEnum: this.ruleDataEnum};
        } else {
          return {columnName: this.ruleColumnName, type: this.ruleType, dataSource: this.ruleDataSource, sql: this.ruleSql}
        }
      },
      isStatic() {
        return this.formCache.type === 'STATIC'
      }
    },
    methods: {
      getCache() {
        let cache = null
        this.$refs['formCache'].validate(valid => {
          if (valid) {
            let {columnName, type, dataEnum, dataSource, sql, intervalSec} = this.formCache
            if (type === 'STATIC') {
              cache = {columnName, type, dataEnum}
            } else {
              cache = {columnName, type, dataSource, sql, intervalSec}
            }
          } else {
            formValidateWarn()
          }
        })
        return cache
      },
      setCache(cache) {
        if (cache) {
          let {columnName, type, dataEnum, dataSource, sql, intervalSec} = cache
          if (!dataEnum) {
            dataEnum = ''
          }
          if (!dataSource) {
            dataSource = ''
          }
          if (!sql) {
            sql = ''
          }
          if (!intervalSec) {
            intervalSec = 5
          }
          this.formCache = {columnName, type, dataEnum, dataSource, sql, intervalSec}
        }
      },
      resetFields() {
        this.$refs['formCache'].resetFields()
      }
    }
  }
</script>
