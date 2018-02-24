<template>
  <div class="dialog-form">
    <el-popover ref="popover" v-model="visible" placement="bottom" :width="popoverWidth" trigger="click">
      <el-row type="flex">
        <el-col :span="24">
          <slot></slot>
        </el-col>
      </el-row>
      <el-row type="flex">
        <el-col :span="24">
          <div class="tag-popover">
            <el-button class="button" @click="handleCancel">{{$t('common.cancel')}}</el-button>
            <el-button class="button" @click="handleOk">{{$t('common.ok')}}</el-button>
          </div>
        </el-col>
      </el-row>
    </el-popover>
    <el-input v-model="dataDisplay" :readonly="readonly" :placeholder="placeholder" :size="size">
      <el-button v-if="showClear" type="text" slot="suffix" size="mini" :disabled="disabled" @click="handleClear">
        <mx-icon name="close"></mx-icon>
      </el-button>
      <el-button slot="append" :disabled="disabled" v-popover:popover>{{$t('common.choose')}}</el-button>
    </el-input>
  </div>
</template>

<script>
  import {formatter} from 'mx-app-utils'
  import MxNotify from '../../utils/mx-notify'

  export default {
    name: 'mx-choose-input',
    props: {
      value: {},
      displayFormat: String,
      popoverWidth: {type: Number, default: 400},
      readonly: {type: Boolean, default: false},
      placeholder: String,
      disabled: {type: Boolean, default: false},
      size: {type: String, default: 'small'}
    },
    data () {
      return {
        visible: false
      }
    },
    computed: {
      showClear () {
        let data = this.value
        return data !== null && data !== undefined
      },
      dataDisplay: {
        get  () {
          let {value, displayFormat} = this
          if (value === null || value === undefined) {
            return ''
          }
          return formatter.formatObj(displayFormat, value)
        },
        set (newValue) {
          // do nothing
        }
      }
    },
    methods: {
      hide () {
        this.visible = false
      },
      handleClear () {
        this.$emit('input', undefined)
      },
      handleCancel () {
        this.hide()
      },
      handleOk () {
        this.$emit('selected', (selected) => {
          if (selected !== null && selected !== undefined) {
            this.dataDisplay = selected[this.displayField]
            this.$emit('input', selected)
            this.hide()
          } else {
            MxNotify.info(this.$t('message.choose'))
          }
        })
      }
    }
  }
</script>
