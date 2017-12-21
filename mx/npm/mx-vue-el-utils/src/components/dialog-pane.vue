<style rel="stylesheet/less" lang="less" scoped>
  @import "../style/dialog.less";
</style>

<template>
  <keep-alive>
    <el-dialog :visible.sync="visible" :title="title" :modal-append-to-body="false" :close-on-click-modal="false"
               :width="width">
      <slot name="form"></slot>
      <div slot="footer" class="dialog-footer">
        <el-button class="button" @click.native="handleReset" :disabled="readonly">重置</el-button>
        <el-button class="button" @click.native="handleSubmit" :disabled="readonly">保存</el-button>
        <el-button class="button" @click.native="handleClose">关闭</el-button>
      </div>
    </el-dialog>
  </keep-alive>
</template>

<script>
  import { logger } from 'mx-app-utils'

  export default {
    name: 'dialog-pane',
    props: {
      title: ''
    },
    data () {
      return {
        visible: false,
        operate: 'detail',
        width: '50%'
      }
    },
    computed: {
      readonly () {
        return this.operate === 'detail'
      }
    },
    methods: {
      show (operate, width) {
        this.operate = operate
        if (width && typeof width === 'string') {
          this.width = width
        } else {
          this.width = '50%'
        }
        this.visible = true
        logger.debug('operate: %s.', operate)
      },
      hide () {
        this.handleClose()
      },
      handleClose () {
        logger.debug('Close the dialog.')
        this.visible = false
      },
      handleReset () {
        this.$emit('reset')
      },
      handleSubmit () {
        this.$emit('submit')
      }
    }
  }
</script>
