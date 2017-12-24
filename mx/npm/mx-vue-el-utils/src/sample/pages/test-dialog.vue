<style rel="stylesheet/less" lang="less" scoped>
  @import "../style/base.less";

  .dialog-form {
    font-size: @content-text-font-size;
    font-weight: @content-text-weight;
  }
</style>

<template>
  <div>
    <el-select v-model="operate">
      <el-option value="add" label="新增"></el-option>
      <el-option value="edit" label="修改"></el-option>
      <el-option value="detail" label="详情"></el-option>
    </el-select>
    <button type="primary" @click="handleShowModalDialog">显示模态对话框</button>
    <dialog-pane ref="dialogPane" :title="title" v-on:reset="handleReset" v-on:submit="handleSubmit">
      <el-form ref="formUser" slot="form" :model="formUser" :rules="rulesUser" label-width="100px" class="dialog-form">
        <el-form-item prop="code" label="代码">
          <el-input v-model="formUser.code" :readonly="readonly"></el-input>
        </el-form-item>
        <el-form-item prop="name" label="名称">
          <el-input v-model="formUser.name" :readonly="readonly"></el-input>
        </el-form-item>
      </el-form>
    </dialog-pane>
  </div>
</template>

<script>
  import { logger } from 'mx-app-utils'
  // import notify from '@/utils/notify'
  // import formValidateRules from '@/utils/form-validate-rules'
  // import DialogPane from '@/components/dialog-pane.vue'
  import {notify, formValidateRules, DialogPane} from '../../../dist/mx-vue-el-utils.min'

  export default {
    name: 'test-dialog-page',
    components: {DialogPane},
    data () {
      return {
        operate: 'detail',
        formUser: {code: 'abcd', name: '123143434545674567'},
        rulesUser: {
          code: [formValidateRules.requiredRule({msg: '必须输入代码'}), formValidateRules.rangeRule({min: 3, max: 10})],
          name: [formValidateRules.requiredRule({msg: '必须输入名称'}), formValidateRules.rangeRule({type: 'string', min: 10})]
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
      handleSubmit () {
        this.$refs['formUser'].validate(valid => {
          if (valid) {
            let {code, name} = this.formUser
            let data = {code, name}
            logger.debug('submit data: %j.', data)
            this.$refs['dialogPane'].hide()
          } else {
            logger.debug('invalide form value.')
            notify.formValidateWarn()
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
