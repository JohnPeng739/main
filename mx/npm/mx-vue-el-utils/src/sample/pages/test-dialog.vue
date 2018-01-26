<template>
  <div>
    <el-select v-model="operate">
      <el-option value="add" label="新增"></el-option>
      <el-option value="edit" label="修改"></el-option>
      <el-option value="detail" label="详情"></el-option>
    </el-select>
    <button type="primary" @click="handleShowModalDialog">显示模态对话框</button>
    <mx-dialog ref="dialogPane" :title="title" v-on:reset="handleReset" v-on:submit="handleSubmit"
               class="layout-dialog">
      <el-form ref="formUser" slot="form" :model="formUser" :rules="rulesUser" label-width="100px" class="dialog-form">
        <el-form-item prop="code" label="代码">
          <el-input v-model="formUser.code" :readonly="readonly"></el-input>
        </el-form-item>
        <el-form-item prop="name" label="名称">
          <el-input v-model="formUser.name" :readonly="readonly"></el-input>
        </el-form-item>
      </el-form>
    </mx-dialog>
  </div>
</template>

<script>
  import { logger } from 'mx-app-utils'
  // import MxFormValidateRules from '@/utils/mx-form-validate-rules'
  import { MxFormValidateRules } from '../../../dist/mx-vue-el-utils.min'

  export default {
    name: 'test-dialog-page',
    data () {
      return {
        operate: 'detail',
        formUser: {code: 'abcd', name: '123143434545674567'},
        rulesUser: {
          code: [MxFormValidateRules.requiredRule({msg: '必须输入代码'}), MxFormValidateRules.rangeRule({min: 3, max: 10})],
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
          case 'detail':
          default:
            return '显示用户详情'
        }
      },
      readonly () {
        return this.operate === 'detail'
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
            let {code, name} = this.formUser
            let data = {code, name}
            logger.debug('submit data: %j.', data)
            done()
          } else {
            logger.debug('invalide form value.')
            this.$mxFormValidateWarn()
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
