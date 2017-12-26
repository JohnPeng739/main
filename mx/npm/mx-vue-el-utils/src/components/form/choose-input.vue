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
            <el-button class="button" @click="handleCancel">{{$t('button.cancel')}}</el-button>
            <el-button class="button" @click="handleOk">{{$t('button.ok')}}</el-button>
          </div>
        </el-col>
      </el-row>
    </el-popover>
    <el-input v-model="dataDisplay" :readonly="readonly" :placeholder="placeholder" :size="size">
      <el-button slot="append" :disabled="disabled" v-popover:popover>
        <icon name="search"></icon>
      </el-button>
    </el-input>
  </div>
</template>

<script>
  import {formatter} from 'mx-app-utils'
  import notify from '@/utils/notify'
  import Icon from '@/components/icon.vue'

  export default {
    name: 'choose-input',
    props: ['value', 'displayFormat', 'popoverWidth', 'readonly', 'placeholder', 'disabled', 'size'],
    components: {Icon},
    data () {
      return {
        visible: false
      }
    },
    computed: {
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
            notify.info(this.$t('message.choose'))
          }
        })
      }
    }
  }
</script>
