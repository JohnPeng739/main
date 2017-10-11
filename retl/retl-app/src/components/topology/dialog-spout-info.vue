<style rel="stylesheet/less" lang="less" scoped>
  @import "../../style/base.less";
  @import "../../style/dialog.less";

  .spout-configuration {
    font-size: @content-text-font-size;
    font-weight: @content-text-weight;
    .layout-button {
      float: right;
      .button {
        font-size: @content-text-font-size;
        color: @button-color;
        &:hover {
          color: @button-hover-color
        }
        .button-icon {
          font-size: 12px;
        }
      }
    }
  }
</style>

<template>
  <el-dialog :visible.sync="visible" :title="title" @close="handleClose" :modal-append-to-body="false"
             :close-on-click-modal="false">
    <el-form ref="formSpout" :model="formSpout" :rules="rulesSpout" label-width="100px" class="dialog-form">
      <el-form-item label="名称" prop="name">
        <el-input v-model="formSpout.name" :readonly="mode === 'detail'"></el-input>
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="formSpout.type" :disabled="mode === 'detail'">
          <el-option v-for="item in spoutTypes" :key="item.value" :label="item.label" :value="item.value"
                     :disabled="spoutTypeDisabled(item.value)"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="并行度" prop="parallelism">
        <el-input-number v-model="formSpout.parallelism" :min="1" :max="10"
                         :disabled="mode === 'detail' || jdbcConfig"></el-input-number>
      </el-form-item>
      <form-spout-jms-config v-if="jmsConfig" ref="formSpoutJmsConfig" :jmsDataSources="jmsDataSources"
                             :configuration="formSpout.configuration" :mode="mode"></form-spout-jms-config>
      <form-spout-jdbc-config v-if="jdbcConfig" ref="formSpoutJdbcConfig"  :zookeepers="zookeepers" :jdbcDataSources="jdbcDataSources"
                              :configuration="formSpout.configuration" :mode="mode"></form-spout-jdbc-config>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button class="button" @click="handleReset" :disabled="mode === 'detail'">重置</el-button>
      <el-button class="button" @click="handleSubmit" :disabled="mode === 'detail'">保存</el-button>
      <el-button class="button" @click="handleClose">关闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import {mapGetters} from 'vuex'
  import {logger} from 'dsutils'
  import {formValidateWarn} from '../../assets/notify'
  import {requiredRule, rangeRule} from '../../assets/form-validate-rules'
  import DsIcon from '../icon.vue'
  import FormSpoutJmsConfig from './form-spout-jms-config.vue'
  import FormSpoutJdbcConfig from './retl/form-spout-jdbc-config.vue'

  export default {
    name: 'dialog-spout-info',
    components: {
      DsIcon, FormSpoutJmsConfig, FormSpoutJdbcConfig
    },
    data() {
      return {
        visible: false,
        spoutTypeDisabled(value) {
          let topologyType = this.topologyType
          if (topologyType === 'persist' && value === 'jdbc') {
            return true
          } else {
            return false
          }
        },
        topologyType: 'retl',
        zookeepers: [],
        jdbcDataSources: [],
        jmsDataSources: [],
        formSpout: {name: '', type: 'jmsPull', parallelism: 1},
        rulesSpout: {
          name: [
            requiredRule({msg: '请输入采集源的名称'}),
            rangeRule({min: 3})
          ],
          type: [requiredRule({msg: '请选择采集源的类型', trigger: 'change'})]
        },
        mode: 'detail'
      }
    },
    computed: {
      ...mapGetters(['spoutTypes']),
      title() {
        switch (this.mode) {
          case 'add':
            return '添加采集源'
          case 'edit':
            return '修改采集源'
          default:
            return '采集源详情'
        }
      },
      configuration() {
        let conf = this.formSpout.configuration
        if (conf !== null && conf !== undefined) {
          return JSON.stringify(conf)
        } else {
          return ''
        }
      },
      jmsConfig() {
        return (this.formSpout.type === 'jms' || this.formSpout.type === 'jmsPull')
      },
      jdbcConfig() {
        return this.formSpout.type === 'jdbc'
      }
    },
    methods: {
      show(mode, topologyType, zookeepers, jdbcDataSources, jmsDataSources, spout) {
        this.mode = mode
        this.topologyType = topologyType
        this.zookeepers = zookeepers
        this.jdbcDataSources = jdbcDataSources
        this.jmsDataSources = jmsDataSources
        let {name, type, parallelism, configuration} = spout
        this.formSpout = {name, type, parallelism, configuration}
        this.visible = true
      },
      handleSubmit() {
        // 获取pane中的数据
        let conf = null
        if (this.jmsConfig) {
          conf = this.$refs['formSpoutJmsConfig'].getConfiguration()
        } else if (this.jdbcConfig) {
          conf = this.$refs['formSpoutJdbcConfig'].getConfiguration()
          let zookeepers = conf.zookeepers
          this.$emit('saveZookeepers', zookeepers)
          delete conf.zookeepers
        }
        if (conf) {
          this.formSpout.configuration = conf
        } else {
          return
        }
        this.$refs['formSpout'].validate(valid => {
          if (valid) {
            this.$emit('submit', this.mode, this.formSpout)
            this.handleClose()
          } else {
            formValidateWarn()
          }
        })
      },
      handleReset() {
        this.$refs['formSpout'].resetFields()
      },
      handleClose() {
        this.visible = false
        this.$emit('close')
      }
    }
  }
</script>
