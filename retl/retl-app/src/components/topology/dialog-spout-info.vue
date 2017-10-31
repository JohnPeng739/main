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
  .tag-row {
    display: inline-block;
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
                             :configuration="formSpout.configuration" :mode="mode">
        <el-form-item v-if="canFieldTransform" slot="fields" label="字段列表" prop="fields">
          <ds-tag-normal v-model="formSpout.fields" type="gray" :disabled="mode === 'detail'" class="tag-row"></ds-tag-normal>
          <el-button v-if="canSampleData" @click="handleOperateSampleJmsData" size="mini" class="tag-row">样本数据</el-button>
          <el-input v-if="showSampleJmsData" type="textarea" :rows="6" v-model="sampleJmsData"></el-input>
        </el-form-item>
        <el-form-item v-if="canFieldTransform" slot="fieldsTransform" label="字段改名">
          <ds-tag-both-sides v-model="formSpout.fieldsTransform" type="gray" :disabled="mode === 'detail'" sideSeparator="=>"></ds-tag-both-sides>
        </el-form-item>
      </form-spout-jms-config>
      <form-spout-jdbc-config v-if="jdbcConfig" ref="formSpoutJdbcConfig"  :zookeepers="zookeepers" :jdbcDataSources="jdbcDataSources"
                              :fields="formSpout.fields" :configuration="formSpout.configuration" :mode="mode">
        <el-form-item v-if="canFieldTransform" slot="fields" label="字段列表" prop="fields">
          <ds-tag-normal v-model="formSpout.fields" type="gray" :disabled="mode === 'detail'"></ds-tag-normal>
        </el-form-item>
        <el-form-item v-if="canFieldTransform" slot="fieldsTransform" label="字段改名">
          <ds-tag-both-sides v-model="formSpout.fieldsTransform" type="gray" :disabled="mode === 'detail'" sideSeparator="=>"></ds-tag-both-sides>
        </el-form-item>
      </form-spout-jdbc-config>
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
  import {formValidateWarn, warn} from '../../assets/notify'
  import {requiredRule, rangeRule} from '../../assets/form-validate-rules'
  import DsIcon from '../icon.vue'
  import FormSpoutJmsConfig from './form-spout-jms-config.vue'
  import FormSpoutJdbcConfig from './retl/form-spout-jdbc-config.vue'
  import DsTagNormal from '../ds-tag-normal.vue'
  import DsTagBothSides from '../ds-tag-both-sides.vue'

  export default {
    name: 'dialog-spout-info',
    components: {
      DsIcon, FormSpoutJmsConfig, FormSpoutJdbcConfig, DsTagNormal, DsTagBothSides
    },
    data() {
      return {
        visible: false,
        showSampleJmsData: false,
        sampleJmsData: '',
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
        formSpout: {name: '', type: 'jmsPull', parallelism: 1, fields: [], fieldsTransform: []},
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
      canSampleData() {
        return !(this.formSpout.fields && this.formSpout.fields.length > 0)
      },
      canFieldTransform() {
        let {type} = this.formSpout
        return (type === 'jmsPull' || type === 'jdbc') && this.topologyType === 'retl'
      },
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
        if (configuration) {
          let {fields, fieldsTransform} = configuration
          this.formSpout = {name, type, parallelism, fields, fieldsTransform, configuration}
        } else {
          this.formSpout = {name, type, parallelism, fields: [], fieldsTransform: [], configuration}
        }
        this.visible = true
      },
      handleOperateSampleJmsData() {
        if (this.showSampleJmsData) {
          if (this.sampleJmsData && this.sampleJmsData.length > 0) {
            try {
              let json = JSON.parse(this.sampleJmsData)
              let fields = []
              Object.keys(json).forEach(key => fields.push(key))
              this.formSpout.fields = fields
            } catch (error) {
              console.log(error)
              warn('输入的数据不符合JSON格式要求。')
              return
            }
          }
        }
        this.showSampleJmsData = !this.showSampleJmsData
      },
      handleSubmit() {
        // 获取pane中的数据
        let conf = null
        if (this.jmsConfig) {
          conf = this.$refs['formSpoutJmsConfig'].getConfiguration()
        } else if (this.jdbcConfig) {
          conf = this.$refs['formSpoutJdbcConfig'].getConfiguration()
          if (conf && conf.zookeepers) {
            let zookeepers = conf.zookeepers
            this.$emit('saveZookeepers', zookeepers)
            delete conf.zookeepers
          }
        }
        if (conf) {
          this.formSpout.configuration = conf
        } else {
          return
        }
        this.$refs['formSpout'].validate(valid => {
          if (valid) {
            let {name, type, parallelism, fields, fieldsTransform, configuration} = this.formSpout
            configuration.fields = fields
            configuration.fieldsTransform = fieldsTransform
            this.$emit('submit', this.mode, {name, type, parallelism, configuration})
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
