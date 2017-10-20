<style rel="stylesheet/less" lang="less" scoped>
  @import "../../style/base.less";

  .config {
    padding: 5px 20px;
    width: 90%;
  }

  .check-result {
    margin: 6px 0 0 18px;
    border: 1px solid @button-color;
    width: 90%;
    height: 490px;
  }

  .check-style {
    display: inline;
    padding-left: 30px;
    font-size: @content-text-font-size + 4px;
    font-weight: 900;
    .checking {
      color: @nav-color;
    }
    .error {
      color: red;
    }
    .pass {
      color: blue;
    }
  }

  .button {
    margin-left: 30px;
    color: @button-color;
    &:hover {
      color: @button-hover-color;
    }
  }
</style>

<template>
  <div>
    <el-row>
      <el-col :span="12">配置详细内容</el-col>
      <el-col :span="12">
        资源校验结果
        <div class="check-style">
          <span v-if="state === 'checking'" class="checking">检验中...</span>
          <span v-else-if="state === 'error'" class="error">检验出错</span>
          <span v-else class="pass">通过检验</span>
          <el-button v-if="state === 'error' || state === 'NA'" type="text" class="button" @click="handleCheckConfig">
            重新检测
          </el-button>
        </div>
      </el-col>
    </el-row>
    <el-row type="flex">
      <el-col :span="12">
        <el-input class="config" type="textarea" v-model="result" :rows="23" :readonly="true"></el-input>
      </el-col>
      <el-col :span="12">
        <div class="check-result" v-html="compileMarkdown"></div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import {mapGetters} from 'vuex'
  import marked from 'marked'
  import {logger} from 'dsutils'
  import {postWithError, defaultError} from '../../assets/ajax'
  import {warn} from '../../assets/notify'
  import config from '../../modules/manage/config'

  export default {
    name: 'topology-result-info',
    data() {
      return {
        checkResult: {}
      }
    },
    computed: {
      ...mapGetters(['topology', 'zookeepers', 'jdbcDataSources', 'jmsDataSources']),
      state() {
        let state = 'NA'
        let checkResult = this.checkResult
        if (checkResult && checkResult.zookeepers && checkResult.zookeepers.need) {
          state = checkResult.zookeepers.state
        }
        if ((state === 'pass' || state === 'NA') && checkResult && checkResult.jdbcDataSources) {
          Object.keys(checkResult.jdbcDataSources).forEach(name => {
            let checker = checkResult.jdbcDataSources[name]
            if (checker && checker.need && checker.state !== 'pass') {
              state = checker.state
              return
            }
          })
        }
        if ((state === 'pass' || state === 'NA') && checkResult && checkResult.jmsDataSources) {
          Object.keys(checkResult.jmsDataSources).forEach(name => {
            let checker = checkResult.jmsDataSources[name]
            if (checker && checker.need && checker.state !== 'pass') {
              state = checker.state
              return
            }
          })
        }
        return state
      },
      result() {
        return JSON.stringify(this.topology, null, '    ')
      },
      compileMarkdown() {
        let checkResult = this.checkResult
        let result = '\n'
        result += '## 总体结果：' + this.getStateName(this.state) + '\n'
        result += '---- \n'
        result += '### Zookeepers: \n'
        result += '- ' + this.getStateName(checkResult.zookeepers.state) + '\n \n'
        result += '### jdbc数据源: \n'
        if (checkResult.jdbcDataSources && Object.keys(checkResult.jdbcDataSources).length > 0) {
          Object.keys(checkResult.jdbcDataSources).forEach((name, index) => {
            result += index + '. ' + name + ': ' + this.getStateName(checkResult.jdbcDataSources[name].state) + '\n'
          })
        } else {
          result += '- 没有配置'
        }
        result += '\n \n'
        result += '### jms数据源: \n'
        if (checkResult.jmsDataSources && Object.keys(checkResult.jmsDataSources).length > 0) {
          Object.keys(checkResult.jmsDataSources).forEach((name, index) => {
            result += index + '. ' + name + ': ' + this.getStateName(checkResult.jmsDataSources[name].state) + '\n'
          })
        } else {
          result += '- 没有配置'
        }
        result += '\n \n'
        result += '----'
        return marked(result, {sanitize: true})
      }
    },
    methods: {
      validated() {
        // 只有校验通过，才能进行下一步操作
        let valid = (this.state === 'pass') || !config.forceValidate
        if (!valid) {
          warn('资源检测没有通过。')
        }
        return valid
      },
      cacheData() {
        // 不需要任何缓存
      },
      getStateName(state) {
        switch (state) {
          case 'checking':
            return '__正在检测中__'
          case 'error':
            return '**检测错误**'
          case 'pass':
            return '检测通过'
          case 'NA':
          default:
            return '未配置'
        }
      },
      createChecker(name, need) {
        return {name, need: need, state: 'NA', startTime: 0, endTime: 0}
      },
      createCheckers() {
        let checkResult = {}
        let topology = this.topology
        let zookeepers = this.zookeepers
        let jdbcDataSources = this.jdbcDataSources
        let jmsDataSources = this.jmsDataSources
        if (zookeepers) {
          checkResult.zookeepers = this.createChecker('zookeepers', true)
        } else {
          checkResult.zookeepers = this.createChecker('zookeepers', false)
        }
        if (jdbcDataSources) {
          let checkers = {}
          jdbcDataSources.forEach(dataSource => {
            if (dataSource) {
              checkers[dataSource.name] = this.createChecker(dataSource.name, true)
            } else {
              checkers[dataSource.name] = this.createChecker(dataSource.name, false)
            }
          })
          checkResult.jdbcDataSources = checkers
        }
        if (jmsDataSources) {
          let checkers = {}
          jmsDataSources.forEach(dataSource => {
            if (dataSource) {
              checkers[dataSource.name] = this.createChecker(dataSource.name, true)
            } else {
              checkers[dataSource.name] = this.createChecker(dataSource.name, false)
            }
          })
          checkResult.jmsDataSources = checkers
        }
        this.checkResult = checkResult
      },
      setCheckedState(checker, passed) {
        if (passed !== undefined) {
          checker.state = (passed ? 'pass' : 'error')
          checker.endTime = new Date().getTime()
        } else {
          checker.state = 'checking'
          checker.startTime = new Date().getTime()
        }
        logger.debug('set check state: %j.', this.checkResult)
      },
      handleCheckConfig() {
        this.createCheckers()
        // 发送后台检查请求
        let checkResult = this.checkResult

        // 检查zookeepers
        let zookeepers = this.zookeepers
        if (checkResult.zookeepers && checkResult.zookeepers.need) {
          this.setCheckedState(checkResult.zookeepers)
          let url = '/rest/topology/validate?type=zookeepers'
          logger.debug('send POST "%s"', url)
          postWithError(url, zookeepers, data => {
            logger.debug('response: %j.', data)
            this.setCheckedState(checkResult.zookeepers, true)
          }, errorMessage => {
            this.setCheckedState(checkResult.zookeepers, false)
            defaultError(errorMessage)
          })
        }

        // 检查jdbc数据源
        let jdbcDataSources = this.jdbcDataSources
        if (jdbcDataSources && jdbcDataSources.length > 0) {
          jdbcDataSources.forEach(dataSource => {
            if (checkResult.jdbcDataSources[dataSource.name].need) {
              this.setCheckedState(checkResult.jdbcDataSources[dataSource.name])
              let url = '/rest/topology/validate?type=jdbcDataSource'
              logger.debug('send POST "%s"', url)
              postWithError(url, dataSource, data => {
                this.setCheckedState(checkResult.jdbcDataSources[dataSource.name], true)
              }, errorMessage => {
                this.setCheckedState(checkResult.jdbcDataSources[dataSource.name], false)
                defaultError(errorMessage)
              })
            }
          })
        }

        // 检查jms数据源
        let jmsDataSources = this.jmsDataSources
        if (jmsDataSources && jmsDataSources.length > 0) {
          jmsDataSources.forEach(dataSource => {
            if (checkResult.jmsDataSources[dataSource.name].need) {
              this.setCheckedState(checkResult.jmsDataSources[dataSource.name])
              let url = '/rest/topology/validate?type=jmsDataSource'
              logger.debug('send POST "%s"', url)
              postWithError(url, dataSource, data => {
                this.setCheckedState(checkResult.jmsDataSources[dataSource.name], true)
              }, errorMessage => {
                this.setCheckedState(checkResult.jmsDataSources[dataSource.name], false)
                defaultError(errorMessage)
              })
            }
          })
        }
      }
    },
    mounted() {
      this.$nextTick(_ => this.handleCheckConfig())
    }
  }
</script>
