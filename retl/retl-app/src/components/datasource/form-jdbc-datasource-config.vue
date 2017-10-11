<style rel="stylesheet/less" lang="less" scoped>
  @import "../../style/base.less";

  .form-row {
    padding: 0 10px 20px 10px;
  }
</style>

<template>
  <el-form ref="formDataSource" :model="formDataSource" :rules="rulesDataSource" label-width="100px">
    <el-row class="form-row">
      <el-col :span="8">
        <el-form-item label="名称" prop="name">
          <el-input v-model="formDataSource.name"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="16">
        <el-form-item label="驱动" prop="driver">
          <el-select v-model="formDataSource.driver">
            <el-option v-for="item in jdbcDriverTypes" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row class="form-row">
      <el-col :span="24">
        <el-form-item label="URL" prop="url">
          <el-input v-model="formDataSource.url"></el-input>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row class="form-row">
      <el-col :span="8">
        <el-form-item label="用户名" prop="user">
          <el-input v-model="formDataSource.user"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="formDataSource.password"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="初始连接数" prop="initialPoolSize">
          <el-input-number v-model="formDataSource.initialPoolSize" :min="3" :max="10"></el-input-number>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row class="form-row">
      <el-col :span="16">
        <el-form-item label="最大空闲时间" prop="maxIdleTime">
          <el-input-number v-model="formDataSource.maxIdleTime" :min="1000" :max="30000" :step="1000"></el-input-number>
          <span>毫秒</span>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="最大连接数" prop="maxPoolSize">
          <el-input-number v-model="formDataSource.maxPoolSize" :min="10" :max="100" :step="10"></el-input-number>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>

<script>
  import {mapGetters} from 'vuex'
  import {requiredRule} from '../../assets/form-validate-rules'
  import {formValidateWarn} from '../../assets/notify'

  export default {
    name: 'pane-dataSource-config',
    data () {
      return {
        formDataSource: {name: '', driver: '', url: '', user: '', password: '', initialPoolSize: 3, maxIdleTime: 3000, maxPoolSize: 10},
        rulesDataSource: {
          name: [requiredRule({msg: '必须输入数据源名称'})],
          driver: [requiredRule({msg: '必须选择一个数据驱动程序', trigger: 'change'})],
          url: [requiredRule({msg: '必须输入数据源连接字符串'})]
        }
      }
    },
    computed: {
      ...mapGetters(['jdbcDriverTypes'])
    },
    methods: {
      getDataSource() {
        let ds = null
        this.$refs['formDataSource'].validate(valid => {
          if (valid) {
            let {name, driver, url, user, password, initialPoolSize, maxPoolSize, maxIdleTime} = this.formDataSource
            ds = {name, driver, url, user, password, initialPoolSize, maxPoolSize, maxIdleTime}
          } else {
            formValidateWarn()
          }
        })
        return ds
      },
      setDataSource(dataSource) {
        this.formDataSource = dataSource
      },
      resetFields() {
        this.$refs['formDataSource'].resetFields()
      }
    }
  }
</script>
