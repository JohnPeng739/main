<template>
  <div>
    <el-select v-model="operate">
      <el-option value="add" label="新增"></el-option>
      <el-option value="edit" label="修改"></el-option>
      <el-option value="details" label="详情"></el-option>
    </el-select>
    <button type="primary" @click="handleShowModalDialog">显示模态对话框</button>
    <mx-dialog ref="dialogPane" :title="title" v-on:reset="handleReset" v-on:submit="handleSubmit"
               class="layout-dialog">
      <el-form ref="formUser" slot="form" :model="formUser" :rules="rulesUser" label-width="100px" class="dialog-form">
        <el-form-item prop="code" label="代码">
          <el-input v-model="formUser.code" :readonly="readonly"></el-input>
        </el-form-item>
        <el-form-item prop="password" label="密码">
          <mx-password v-model="formUser.password" :readonly="readonly"></mx-password>
        </el-form-item>
        <el-form-item prop="name" label="名称">
          <el-input v-model="formUser.name" :readonly="readonly"></el-input>
        </el-form-item>
        <el-form-item prop="name" label="名称">
          <el-input v-model="formUser.name" :readonly="readonly"></el-input>
        </el-form-item>
        <el-form-item prop="department" label="部门">
          <mx-dict-select :dict-data="departments" v-model="formUser.department" :disabled="readonly"
                          display-format="{code}-{name}"></mx-dict-select>
        </el-form-item>
      </el-form>
    </mx-dialog>
  </div>
</template>

<script>
  import { logger } from 'mx-app-utils'
  import { MxFormValidateRules, MxNotify, MxPassword, MxDictSelect } from '@/index'
  // import { MxFormValidateRules, MxNotify } from '../../../dist/mx-vue-el-utils.min'

  export default {
    name: 'test-dialog-page',
    components: {MxPassword, MxDictSelect},
    data () {
      return {
        operate: 'details',
        departments: [{
          id: 'ggs',
          code: 'ggs',
          name: '高管室'
        }, {
          id: 'zzs',
          code: 'zzs',
          name: '总师室'
        }, {
          id: 'jccpc',
          code: 'jccpc',
          name: '基础产品中心',
          children: [{
            id: 'k1',
            code: 'k1',
            name: '开发一部'
          }, {
            id: 'xt',
            code: 'xt',
            name: '协同通信部'
          }]
        }, {
          id: 'yycpc',
          code: 'yycpc',
          name: '应用软件中心',
          children: [{
            id: 'k2',
            code: 'k2',
            name: '开发二部'
          }, {
            id: 'k3',
            code: 'k3',
            name: '开发三部'
          }, {
            id: 'gis',
            code: 'gis',
            name: 'GIS部'
          }]
        }],
        formUser: {
          code: 'abcd',
          password: '12345678',
          name: '123143434545674567',
          department: {id: 'zzs'}
        },
        rulesUser: {
          code: [MxFormValidateRules.requiredRule(), MxFormValidateRules.rangeRule({min: 3, max: 10})],
          password: [MxFormValidateRules.requiredRule(), MxFormValidateRules.rangeRule({min: 3, max: 10})],
          name: [MxFormValidateRules.requiredRule({msg: '必须输入名称'}), MxFormValidateRules.rangeRule({
            type: 'string',
            min: 10
          })]
        }
      }
    },
    computed: {
      title () {
        switch (this.operate) {
          case 'add':
            return '新增用户'
          case 'edit':
            return '修改用户信息'
          case 'details':
          default:
            return '显示用户详情'
        }
      },
      readonly () {
        return this.operate === 'details'
      }
    },
    methods: {
      handleShowModalDialog () {
        let {operate} = this
        this.$refs['dialogPane'].show(operate, '60%')
      },
      handleSubmit (done) {
        this.$refs['formUser'].validate(valid => {
          if (valid) {
            let {code, password, name, department} = this.formUser
            let data = {code, password, name, department}
            logger.debug('submit data: %j.', data)
            done()
          } else {
            logger.debug('invalide form value.')
            MxNotify.formValidateWarn()
          }
        })
      },
      handleReset () {
        this.$refs['formUser'].resetFields()
      }
    },
    mounted () {
      logger.debug('rules: %j.', this.rulesUser)
    }
  }
</script>
