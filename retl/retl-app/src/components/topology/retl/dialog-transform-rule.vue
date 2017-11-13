<style rel="stylesheet/less" lang="less">
  @import "../../../style/base.less";
  @import "../../../style/dialog.less";
</style>

<template>
  <el-dialog :visible.sync="visible" :title="title" @close="handleClose" :modal-append-to-body="false" @close-on-click-modal="false">
    <pane-formula-transform v-if="type === 'FormulaTransform'" ref="paneFormulaTransform" :columns="columns" :rule="rule" :mode="mode"></pane-formula-transform>
    <pane-merge-transform v-else-if="type === 'MergeTransform'" ref="paneMergeTransform" :columns="columns" :rule="rule" :mode="mode"></pane-merge-transform>
    <pane-sub-string-transform v-else-if="type === 'SubStringTransform'" ref="paneSubStringTransform" :columns="columns" :rule="rule" :mode="mode"></pane-sub-string-transform>
    <div slot="footer" class="dialog-footer">
      <el-button class="button" @click="handleReset" :disabled="mode === 'detail'">重置</el-button>
      <el-button class="button" @click="handleSubmit" :disabled="mode === 'detail'">保存</el-button>
      <el-button class="button" @click="handleClose">关闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import {logger} from 'mx-app-utils'
  import PaneFormulaTransform from '../../transform/form-transform-formula.vue'
  import PaneMergeTransform from '../../transform/form-transform-merge.vue'
  import PaneSubStringTransform from '../../transform/form-transform-substring.vue'

  export default {
    name: 'dialog-transform-rule',
    components: {PaneFormulaTransform, PaneMergeTransform, PaneSubStringTransform},
    data() {
      return {
        visible: false,
        type: 'MergeTransform',
        rule: {},
        columns: [],
        mode: 'detail'
      }
    },
    computed: {
      title() {
        switch (this.mode) {
          case 'add':
            return '新增转换规则'
          case 'edit':
            return '修改转换规则'
          case 'detail':
          default:
            return '转换规则详情'
        }
      }
    },
    methods: {
      show(mode, columns, rule) {
        logger.debug('show validate rule dialog, mode: %s, columns: %j, rule: %j.', mode, columns, rule)
        this.mode = mode
        this.columns = columns
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
        logger.debug('submit %s.', this.type)
        let ref = this.$refs['pane' + this.type]
        if (ref) {
          let rule = ref.getRule()
          if (rule) {
            this.$emit('submit', this.mode, rule)
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
