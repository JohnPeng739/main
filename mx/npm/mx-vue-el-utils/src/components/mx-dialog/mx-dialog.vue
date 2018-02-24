<template>
  <keep-alive>
    <el-dialog :visible.sync="visible" :title="title" :modal-append-to-body="false" :close-on-click-modal="false"
               :width="width">
      <slot name="form"></slot>
      <div slot="footer">
        <el-button class="button" @click.native="handleReset" :disabled="readonly">{{$t('common.reset')}}</el-button>
        <el-button class="button" @click.native="handleSubmit" :disabled="readonly">{{$t('common.submit')}}</el-button>
        <el-button class="button" @click.native="handleClose">{{$t('common.close')}}</el-button>
      </div>
    </el-dialog>
  </keep-alive>
</template>

<script>
  import { logger } from 'mx-app-utils'

  export default {
    name: 'mx-dialog',
    props: {
      title: {type: String, default: ''}
    },
    data () {
      return {
        visible: false,
        operate: 'details',
        width: '60%'
      }
    },
    computed: {
      readonly () {
        return this.operate === 'details'
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
        this.visible = false
      },
      handleClose () {
        logger.debug('Close the dialog.')
        this.hide()
      },
      handleReset () {
        this.$emit('reset')
      },
      handleSubmit () {
        this.$emit('submit', () => this.hide())
      }
    }
  }
</script>
