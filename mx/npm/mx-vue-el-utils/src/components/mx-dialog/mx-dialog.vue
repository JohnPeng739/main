<template>
  <keep-alive>
    <el-dialog :visible.sync="visible" :title="title" :modal-append-to-body="false" :close-on-click-modal="false"
               :width="width">
      <slot name="form"></slot>
      <div slot="footer">
        <el-button class="button" @click.native="handleReset" :disabled="readonly">{{t('button.reset')}}</el-button>
        <el-button class="button" @click.native="handleSubmit" :disabled="readonly">{{t('button.submit')}}</el-button>
        <el-button class="button" @click.native="handleClose">{{t('button.close')}}</el-button>
      </div>
    </el-dialog>
  </keep-alive>
</template>

<script>
  import { logger } from 'mx-app-utils'
  import {t} from '@/locale'

  export default {
    name: 'mx-dialog',
    props: {
      title: {type: String, default: ''}
    },
    data () {
      return {
        visible: false,
        operate: 'detail',
        width: '60%',
        t: t
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
