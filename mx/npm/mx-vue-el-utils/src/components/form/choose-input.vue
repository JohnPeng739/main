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
          <div class="pg-layout-buttons">
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
  import Icon from '@/components/icon.vue'

  export default {
    name: 'choose-input',
    props: ['value', 'displayField', 'popoverWidth', 'readonly', 'placeholder', 'disabled', 'size'],
    components: {Icon},
    data () {
      return {
        dataDisplay: '',
        visible: false
      }
    },
    methods: {
      setSelected (selected) {
        if (selected !== null && selected !== undefined) {
          this.dataDisplay = selected[this.displayField]
          this.$emit('input', selected)
          this.hide()
        }
      },
      hide () {
        this.visible = false
      },
      handleCancel () {
        this.hide()
      },
      handleOk () {
        this.$emit('selected')
      }
    },
    mounted () {
      if (this.value && this.value[this.displayField]) {
        this.dataDisplay = this.value[this.displayField]
      }
    }
  }
</script>
