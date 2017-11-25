<style rel="stylesheet/less" lang="less" scoped>
  @import "../../style/base.less";
</style>

<template>
  <el-form ref="formContrastTransform" :model="formContrastTransform" :rules="rulesContrastTransform" label-width="100px">
    <el-form-item label="规则">
      <span>对照转换</span>
    </el-form-item>
    <el-row type="flex" class="form-row">
      <el-form-item label="字段名称" prop="columnName">
        <el-input v-model="formContrastTransform.columnName" :readonly="mode === 'detail'"></el-input>
      </el-form-item>
    </el-row>
    <el-row type="flex" class="form-row">
      <el-form-item label="对照源字段" prop="columnName">
        <el-input v-model="formContrastTransform.srcColumn" :readonly="mode === 'detail'"></el-input>
      </el-form-item>
    </el-row>

    <el-row type="flex" class="form-row">
      <el-form-item label="类型" prop="mode">
        <el-select v-model="formContrastTransform.mode">
          <el-option label="静态字典" value="STATIC"></el-option>
          <el-option label="数据库字典" value="JDBC"></el-option>
        </el-select>
      </el-form-item>
    </el-row>

    <el-row v-if="isStatic" type="flex" class="form-row">
      <el-col :span="18">
        <el-form-item label="静态字典" prop="dataEnum">
          <el-input type="textarea" v-model="formContrastTransform.staticDic" :rows="4"></el-input>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row v-if="!isStatic" type="flex" class="form-row">
      <el-col :span="12">
        <el-form-item label="数据源" prop="dataSource">
          <el-select v-model="formContrastTransform.dataSource">
            <el-option v-for="item in jdbcDataSources" :key="item.name" :label="item.url" :value="item.name"></el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="间隔时间" prop="intervalSec">
          <el-input-number v-model="formContrastTransform.intervalSec" :min="5" :step="10" size="small"></el-input-number>
          <span>秒</span>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row v-if="!isStatic" type="flex" class="form-row">
      <el-col :span="24">
        <el-form-item label="SQL语句" prop="sql">
          <el-input type="textarea" v-model="formContrastTransform.sql" :rows="4"></el-input>
        </el-form-item>
      </el-col>
    </el-row>

  </el-form>
</template>

<script>
  import {requiredRule, rangeRule, customRule} from '../../assets/form-validate-rules'
  import {formValidateWarn} from '../../assets/notify'
  import {logger} from 'mx-app-utils'
  import DsTagBothSides from '../ds-tag-both-sides.vue'
  import {mapGetters, mapActions} from 'vuex'

  export default {
    name: 'pane-contrast-validate',
    components: {DsTagBothSides},
    props: ['columns', 'rule', 'mode'],
    data() {
      let mappingValidator = (rule, value, callback) => {
        value = this.formTable.columnDefines
        if (value && value.length > 0) {
          callback()
        } else {
          callback(new Error('必须至少输入一个映射字段'))
        }
      }
      return {
        formContrastTransform: {
          columnName: this.rule.columnName,
          srcColumn:this.rule.srcColumn,
          type:this.rule.type,
          mode:this.rule.mode,
          staticDic: this.rule.staticDic,
          dataSource:this.rule.dataSource,
          intervalSec:this.intervalSec,
          sql:this.rule.sql
        },
        rulesContrastTransform: {
          ruleColumnName: [requiredRule({msg: '必须输入字段名称。'})],
          ruleSrcColumn:[requiredRule({msg: '必须输入字段名称。'})],
          ruleStaticDic: [requiredRule({msg: '必须输入静态数据字典内容。'})],
          ruleDataSource: [requiredRule({msg: '必须选择一个JDBC数据源。', trigger: 'change'})],
          ruleIntervalSec: [requiredRule({msg: '间隔时间不能为空。'})],
          ruleSql: [requiredRule({msg: '必须输入缓存字典的SQL语句。'})]
        }
      }
    },
    computed: {
      ...mapGetters(['jdbcDataSources']),
      rulesCache() {
        if (this.formContrastTransform.mode === 'STATIC') {
          return {
                  columnName: this.rulesContrastTransform.ruleColumnName,
                  ruleSrcColumn:this.rulesContrastTransform.srcColumn,
                  staticDic: this.rulesContrastTransform.staticDic};
        } else {
          return {
                  columnName: this.rulesContrastTransform.ruleColumnName,
                  ruleSrcColumn:this.rulesContrastTransform.srcColumn,
                  intervalSec:this.rulesContrastTransform.rulesInValidate(),
                  dataSource: this.rulesContrastTransform.ruleDataSource,
                  sql: this.rulesContrastTransform.ruleSql
              }
        }
      },
      isStatic() {
        return this.formContrastTransform.mode === 'STATIC'
      }
    },
    methods: {
      getRule() {
        let rule = null
        this.$refs['formContrastTransform'].validate(valid => {
          if (valid) {
            let {columnName,srcColumn,type,mode,staticDic, dataSource, intervalSec, sql} = this.formContrastTransform

            if(this.formContrastTransform.mode === 'STATIC') {
                rule = {columnName,srcColumn,type,mode, staticDic}
            }
            else
            {
                rule = {columnName,srcColumn,type,mode, dataSource, intervalSec,sql}
            }

          } else {
            formValidateWarn()
          }
        })
        return rule
      },
      setRule(r)
      {
          if(r)
          {
            let {columnName,srcColumn,type,mode,staticDic, dataSource, intervalSec, sql} = r

            if(!staticDic)
            {
                staticDic = ''
            }

            if(!dataSource)
            {
              dataSource = ''
            }

            if(!intervalSec)
            {
                intervalSec = '60'
            }

            if(!sql)
            {
                sql = ''
            }

            this.formContrastTransform = {columnName,srcColumn,type,mode,staticDic,dataSource,intervalSec,sql}
          }
      },
      reset() {
        this.$refs['formContrastTransform'].resetFields()
      }
    }
  }
</script>
