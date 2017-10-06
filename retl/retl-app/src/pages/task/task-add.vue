<style rel="stylesheet/less" lang="less">
  @import "../../style/base.less";

  .layout-steps {
    padding-top: 20px;
    text-align: center;
    .button {
      display: inline;
      .button-icon {
        padding-top: 20px;
      }
    }
    .steps {
      margin-left: 50px;
      display: inline;
    }
  }

  .layout-content {
    margin: 8px;
    padding-top: 10px;
  }

  .layout-buttons {
    padding-top: 20px;
    text-align: center;
  }
</style>
<template>
  <div>
    <el-row type="flex" justify="center">
      <el-col :span="24">
        <div class="layout-steps">
          <el-tooltip effect="dark" content="上一步" :hide-after="2000" placement="bottom">
            <el-button class="button" type="text" @click="handleClickPrev" :disabled="!hasPrev">
              <ds-icon class="button-icon" name="skip_previous"></ds-icon>
            </el-button>
          </el-tooltip>
          <el-steps class="steps" :space="120" :active="steps" finish-status="success">
            <el-step v-for="item in stepList" :key="item.step" :title="item.name"></el-step>
          </el-steps>
          <el-tooltip effect="dark" content="下一步" :hide-after="2000" placement="bottom">
            <el-button class="button" type="text" @click="handleClickNext" :disabled="!hasNext">
              <ds-icon class="button-icon" name="skip_next"></ds-icon>
            </el-button>
          </el-tooltip>
        </div>
      </el-col>
    </el-row>
    <el-row type="flex" justify="center">
      <el-col class="layout-content">
        <topology-basic-info ref="basicInfo" v-if="isStep('basicInfo')" :topology="topology"></topology-basic-info>
        <topology-spouts-info ref="spoutsInfo" v-else-if="isStep('spoutsInfo')" :topology="topology"></topology-spouts-info>
        <topology-columns-info ref="columnsInfo" v-else-if="isStep('columnsInfo')"
                               :topology="topology"></topology-columns-info>
        <topology-validates-info ref="validatesInfo" v-else-if="isStep('validatesInfo')"
                                 :topology="topology"></topology-validates-info>
        <topology-transforms-info ref="transformsInfo" v-else-if="isStep('transformsInfo')"
                                  :topology="topology"></topology-transforms-info>
        <topology-persist-info ref="persistInfo" v-else-if="isStep('persistInfo')"
                               :topology="topology"></topology-persist-info>
        <topology-result-info ref="resultInfo" v-else-if="isStep('resultInfo')" :topology="topology"></topology-result-info>
        <topology-submit ref="submitResult" v-else :topology="topology"></topology-submit>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import {logger} from 'dsutils'
  import {copyData} from '../../assets/utils'
  import {warn} from '../../assets/notify'
  import DsIcon from "../../components/icon.vue"
  import TopologyBasicInfo from '../../components/topology/basic-info.vue'
  import TopologySpoutsInfo from '../../components/topology/spouts-info.vue'
  import TopologyColumnsInfo from '../../components/topology/retl/columns-info.vue'
  import TopologyValidatesInfo from '../../components/topology/retl/validates-info.vue'
  import TopologyTransformsInfo from '../../components/topology/retl/transforms-info.vue'
  import TopologyResultInfo from '../../components/topology/result-info.vue'
  import TopologyPersistInfo from '../../components/topology/persist/persist-info.vue'
  import TopologySubmit from '../../components/topology/submit-result.vue'

  export default {
    name: 'page-add-task',
    components: {
      DsIcon,
      TopologyBasicInfo,
      TopologySpoutsInfo,
      TopologyColumnsInfo,
      TopologyValidatesInfo,
      TopologyTransformsInfo,
      TopologyResultInfo,
      TopologyPersistInfo,
      TopologySubmit
    },
    data() {
      return {
        topology: {name: '', type: '', debug: false, messageTimeoutSecs: 3, maxSpoutPending: 1},
        steps: 0
      }
    },
    computed: {
      stepList() {
        let {type} = this.topology
        if (type === 'RETL') {
          return [{
            name: '基本信息', id: 'basicInfo', step: 0
          }, {
            name: '采集源', id: 'spoutsInfo', step: 1
          }, {
            name: '数据列', id: 'columnsInfo', step: 2
          }, {
            name: '校验规则', id: 'validatesInfo', step: 3
          }, {
            name: '转换规则', id: 'transformsInfo', step: 4
          }, {
            name: '配置结果', id: 'resultInfo', step: 5
          }]
        } else if (type === 'PERSIST') {
          return [{
            name: '基本信息', id: 'basicInfo', step: 0
          }, {
            name: '采集源', id: 'spoutsInfo', step: 1
          }, {
            name: '存储规则', id: 'persistInfo', step: 2
          }, {
            name: '配置结果', id: 'resultInfo', step: 3
          }]
        } else {
          return [{
            name: '基本信息', id: 'basicInfo', step: 0
          }, {
            name: '......', id: 'NA', step: 1
          }]
        }
      },
      hasPrev() {
        return this.steps > 0
      },
      hasNext() {
        return this.steps < this.stepList.length
      }
    },
    methods: {
      isStep(name) {
        let step = this.steps
        let {type} = this.topology
        switch (name) {
          case 'basicInfo':
            return step === 0
          case 'spoutsInfo':
            return step === 1
          case 'columnsInfo':
            return type === 'RETL' && step === 2
          case 'validatesInfo':
            return type === 'RETL' && step === 3
          case 'transformsInfo':
            return type === 'RETL' && step === 4
          case 'resultInfo':
            return (type === 'RETL' && step === 5) || (type === 'PERSIST' && step === 3)
          case 'persistInfo':
            return type === 'PERSIST' && step === 2
        }
        return false
      },
      saveDataBeforeChange() {
        let {type} = this.topology
        switch (this.steps) {
          case 0:
            let basicInfo = this.$refs['basicInfo'].getBasicInfo()
            if (basicInfo !== null && basicInfo !== undefined) {
              copyData(this.topology, basicInfo)
              return true
            }
            break;
          case 1:
            let spoutsInfo = this.$refs['spoutsInfo'].getSpoutsInfo()
            if (spoutsInfo !== null && spoutsInfo !== undefined) {
              this.topology.spouts = spoutsInfo
              return true
            }
            break;
          case 2:
            if (type === 'RETL') {
              let columnsInfo = this.$refs['columnsInfo'].getColumnsInfo()
              if (columnsInfo) {
                this.topology.columns = columnsInfo
                return true
              }
            } else if (type === 'PERSIST') {
              let persistInfo = this.$refs['persistInfo'].getPersistInfo()
              if (persistInfo) {
                this.topology.persist = persistInfo
                return true
              }
            }
            break
          case 3:
            if (type === 'RETL') {
              let validatesInfo = this.$refs['validatesInfo'].getValidatesInfo()
              if (validatesInfo) {
                this.topology.validates = validatesInfo
                return true
              }
            } else if (type === 'PERSIST') {
              // is type == 'PERSIST' result
              return true
            }
            break
          case 4:
            if (type === 'RETL') {
              let transformsInfo = this.$refs['transformsInfo'].getTransformsInfo()
              if (transformsInfo) {
                this.topology.transforms = transformsInfo
                return true
              }
            } else if (type === 'PERSIST') {
              // is type == 'PERSIST' submit
              return true
            }
            break;
          case 5:
            // is type === 'RETL' result
          case 6:
            // is type == 'RETL' submit
            return true
          default:
            break;
        }
        logger.debug('无校验通过的数据，step： %d.', this.steps)
        warn(this, '请在切换页面之前输入必须的数据。')
        return false
      },
      handleClickNext() {
        if (this.saveDataBeforeChange() && this.steps < this.stepList.length) {
          this.steps += 1
        }
        logger.debug(this.topology)
      },
      handleClickPrev() {
        if (this.saveDataBeforeChange() && this.steps > 0) {
          this.steps -= 1
        }
        this.$nextTick(_ => {
          if (this.steps >= 0 && this.steps < this.stepList.length) {
            this.$refs[this.stepList[this.steps].id].setTopology(this.topology)
          }
        })
      }
    }
    // TODO 添加使用store的方式操作topology
  }
</script>
