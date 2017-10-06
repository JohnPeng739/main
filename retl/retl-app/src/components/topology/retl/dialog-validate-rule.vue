<style rel="stylesheet/less" lang="less">
  @import "../../../style/base.less";
  @import "../../../style/dialog.less";
</style>

<template>
  <el-dialog :visible.sync="visible" :title="title" @close="handleClose" :modal-append-to-body="false" @close-on-click-modal="false">
    <pane-null-validate v-if="type === 'NullValidate'" ref="paneNullValidate" :rule="rule" :mode="mode"></pane-null-validate>
    <pane-type-validate v-else-if="type === 'TypeValidate'" ref="paneTypeValidate" :rule="rule" :mode="mode"></pane-type-validate>
    <pane-length-validate v-else-if="type === 'LengthValidate'" ref="paneLengthValidate" :rule="rule" :mode="mode"></pane-length-validate>
    <pane-range-validate v-else-if="type === 'RangeValidate'" ref="paneRangeValidate" :rule="rule" :mode="mode"></pane-range-validate>
    <pane-reg-exp-validate v-else-if="type === 'RegExpValidate'" ref="paneRegExpValidate" :rule="rule" :mode="mode"></pane-reg-exp-validate>
    <div slot="footer" class="dialog-footer">
      <el-button class="button" @click="handleReset" :disabled="mode === 'detail'">重置</el-button>
      <el-button class="button" @click="handleSubmit" :disabled="mode === 'detail'">保存</el-button>
      <el-button class="button" @click="handleClose">关闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import {logger} from 'dsutils'
  import PaneNullValidate from '../../validate/form-validate-null.vue'
  import PaneTypeValidate from '../../validate/form-validate-type.vue'
  import PaneLengthValidate from '../../validate/form-validate-length.vue'
  import PaneRangeValidate from '../../validate/form-validate-range.vue'
  import PaneRegExpValidate from '../../validate/form-validate-regexp.vue'

  export default {
    name: 'dialog-validate-rule',
    components: {PaneNullValidate, PaneTypeValidate, PaneLengthValidate, PaneRangeValidate, PaneRegExpValidate},
    data() {
      return {
        visible: false,
        type: 'NullValidate',
        rule: {},
        columnName: '',
        mode: 'detail'
      }
    },
    computed: {
      title() {
        switch (this.mode) {
          case 'add':
            return '新增校验规则'
          case 'edit':
            return '修改校验规则'
          case 'detail':
          default:
            return '校验规则详情'
        }
      }
    },
    methods: {
      show(mode, columnName, rule) {
        logger.debug('show validate rule dialog, mode: %s, name: %s, rule: %j.', mode, columnName, rule)
        this.mode = mode
        this.columnName = columnName
        this.rule = rule
        if (rule && rule.type) {
          this.type = rule.type
        }
        this.visible = true
      },
      handleReset() {
        let ref = this.$refs['pane' + this.type]
        if (ref) {
          ref.reset()
        }
      },
      handleSubmit() {
        let ref = this.$refs['pane' + this.type]
        if (ref) {
          let rule = ref.getRule()
          if (rule) {
            this.$emit('submit', this.mode, this.columnName, rule)
            this.handleClose()
          }
        }
      },
      handleClose() {
        this.visible = false
        this.$emit('close')
      }
    }
  }
</script>
