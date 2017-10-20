<style rel="stylesheet/less" lang="less" scoped>
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
          <el-steps class="steps" :space="100" :active="steps" finish-status="success">
            <el-step v-for="item in stepList" :key="item.step" :title="item.name"></el-step>
          </el-steps>
          <el-tooltip effect="dark" content="暂存" :hide-after="2000" placement="bottom">
            <el-button class="button" type="text" @click="handleCacheSave" :disabled="isStep('resultInfo') || isStep('submitInfo')">
              <ds-icon class="button-icon" name="save"></ds-icon>
            </el-button>
          </el-tooltip>
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
        <topology-basic-info ref="basicInfo" v-if="isStep('basicInfo')"></topology-basic-info>
        <topology-spouts-info ref="spoutsInfo" v-else-if="isStep('spoutsInfo')"></topology-spouts-info>
        <topology-columns-info ref="columnsInfo" v-else-if="isStep('columnsInfo')"></topology-columns-info>
        <topology-validates-info ref="validatesInfo" v-else-if="isStep('validatesInfo')"></topology-validates-info>
        <topology-transforms-info ref="transformsInfo" v-else-if="isStep('transformsInfo')"></topology-transforms-info>
        <topology-persist-info ref="persistInfo" v-else-if="isStep('persistInfo')"></topology-persist-info>
        <topology-result-info ref="resultInfo" v-else-if="isStep('resultInfo')"></topology-result-info>
        <topology-submit ref="submitResult" v-else></topology-submit>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import {mapGetters, mapActions} from 'vuex'
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
        saveInterval: null,
        steps: 0
      }
    },
    computed: {
      ...mapGetters(['topology']),
      stepList() {
        logger.debug(this.topology)
        let {type} = this.topology
        if (type === 'retl') {
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
        } else if (type === 'persist') {
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
      ...mapActions(['cacheLoad', 'cacheSave', 'cacheClean', 'loadTypes']),
      isStep(name) {
        let step = this.steps
        let {type} = this.topology
        switch (name) {
          case 'basicInfo':
            return step === 0
          case 'spoutsInfo':
            return step === 1
          case 'columnsInfo':
            return type === 'retl' && step === 2
          case 'validatesInfo':
            return type === 'retl' && step === 3
          case 'transformsInfo':
            return type === 'retl' && step === 4
          case 'resultInfo':
            return (type === 'retl' && step === 5) || (type === 'persist' && step === 3)
          case 'persistInfo':
            return type === 'persist' && step === 2
          case 'submitInfo':
            return (type === 'retl' && step === 6) || (type === 'persist' && step === 4)
        }
        return false
      },
      getCurrentPane() {
        let {type} = this.topology
        let paneStep = null
        switch (this.steps) {
          case 0:
            paneStep = this.$refs['basicInfo']
            break;
          case 1:
            paneStep = this.$refs['spoutsInfo']
            break;
          case 2:
            if (type === 'retl') {
              paneStep = this.$refs['columnsInfo']
            } else if (type === 'persist') {
              paneStep = this.$refs['persistInfo']
            }
            break
          case 3:
            if (type === 'retl') {
              paneStep = this.$refs['validatesInfo']
            } else if (type === 'persist') {
              paneStep = this.$refs['resultInfo']
            }
            break
          case 4:
            if (type === 'retl') {
              paneStep = this.$refs['transformsInfo']
            }
            break;
          case 5:
            if (type === 'retl') {
              paneStep = this.$refs['resultInfo']
            }
            break
          default:
            break;
        }
        return paneStep
      },
      validateDataBeforeSwitch() {
        let {type} = this.topology
        let paneStep = this.getCurrentPane()
        if (this.isStep('submitInfo')) {
          // 提交页面，不需要校验
          return true
        }
        if (paneStep) {
          let valid = paneStep.validated()
          paneStep.cacheData()
          return valid
        } else {
          warn('获取步骤子页面错误， step: ' + this.steps + '。')
          return false
        }
      },
      handleClickNext() {
        if (this.validateDataBeforeSwitch() && this.steps < this.stepList.length) {
          this.steps += 1
        }
        logger.debug(this.topology)
      },
      handleCacheSave() {
        let paneStep = this.getCurrentPane()
        if (paneStep) {
          paneStep.cacheData()
        }
        this.cacheSave()
      },
      handleClickPrev() {
        this.handleCacheSave()
        if (this.steps > 0) {
          this.steps -= 1
        }
      }
    },
    mounted() {
      this.$nextTick(_ => {
        this.loadTypes()
        this.cacheLoad()
      })
      // 每过30秒中自动缓存一下
      this.saveInterval = setInterval(_ => this.handleCacheSave(), 300000)
    },
    destroyed() {
      clearInterval(this.saveInterval)
    }
  }
</script>
