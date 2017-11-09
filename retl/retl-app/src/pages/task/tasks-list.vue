<style rel="stylesheet/less" lang="less" scoped>
  @import "../../style/base.less";

  .layout-content {
    magin: 0 auto;
    .layout-table {
      width: 100%;
      height: 600px;
      min-height: 570px;
      .span-role {
        padding: 0 2px 0 2px;
      }
      .button {
        margin-left: 10px;
      }
    }
  }
  .inline-span {
    padding: 0 20px 0 20px;
  }
  .info {
    color: green;
  }
  .warn {
    color: red;
  }
  .inline-button {
    width: 100px;
    padding: 5px;
    border-color: red;
  }
</style>

<template>
  <div>
    <pane-paginate-list ref="panePaginateList" v-on:buttonHandle="handleButtonClick">
      <el-table :max-height="570" :data="tableData" class="layout-table" highlight-current-row
                @current-change="handleCurrentChange">
        <el-table-column type="expand">
          <template scope="scope">
            <el-form label-position="left">
              <el-form-item label="集群状态">
                <span v-if="scope.row.status" class="inline-span info">{{scope.row.status}}</span>
                <span v-else class="inline-span warn">未提交到集群</span>
                <el-button v-if="!scope.row.submitted" @click="handleSubmit(scope.row.id, false)" size="mini" class="inline-button">提交集群</el-button>
                <el-button v-if="!scope.row.submitted && isDebug" @click="handleSubmit(scope.row.id, true)" size="mini" class="inline-button">本地仿真</el-button>
                <el-button v-if="scope.row.submitted" @click="handleRebalance(scope.row.topologyId)" size="mini" class="inline-button">重新负载均衡</el-button>
                <el-button v-if="scope.row.submitted" @click="handleKill(scope.row.id)" size="mini" class="inline-button">杀死拓扑</el-button>
              </el-form-item>
              <el-form-item label="拓扑配置">
                <el-input type="textarea" :value="JSON.stringify(scope.row.content, null, '    ')"
                          :rows="15" :disabled="true"></el-input>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="名称"></el-table-column>
        <el-table-column prop="savedTime" label="保存时间" width="180"></el-table-column>
        <el-table-column prop="submitTime" label="提交时间" width="180"></el-table-column>
        <el-table-column prop="submited" label="是否提交" width="200">
          <template scope="scope">
            <span v-if="scope.row.submitted">已提交集群</span>
            <span v-else style="color: red;">未提交集群</span>
          </template>
        </el-table-column>
        <el-table-column prop="operator" label="操作人员" width="120"></el-table-column>
      </el-table>
    </pane-paginate-list>
  </div>
</template>

<script>
  import {mapActions} from 'vuex'
  import {logger} from 'dsutils'
  import {info, warn, confirm} from '../../assets/notify'
  import {get, post} from '../../assets/ajax'
  import {formatDateTime} from '../../assets/date-utils'
  import config from '../../modules/manage/config'
  import PanePaginateList from '../../components/pane-paginate-list.vue'

  export default {
    name: 'page-tasks-list',
    components: {PanePaginateList},
    data() {
      return {
        longDate(time) {
          if (time) {
            let datetime = new Date(time)
            return formatDateTime(datetime)
          } else {
            return ''
          }
        },
        queryByPage: false,
        tableData: [],
        selection: null
      }
    },
    computed: {
      isDebug() {
        return config.debug
      }
    },
    methods: {
      ...mapActions(['goto', 'cacheClean', 'setTopology']),
      fillTableData(topologies) {
        let tableData = []
        let topologyIds = []
        if (topologies && topologies.length > 0) {
          topologies.forEach(topology => {
            console.log(topology)
            let {id, name, updatedTime, submitted, submitInfo, submittedTime, topologyContent, operator, topologyId} = topology
            console.log(topologyContent)
            let contentJson = JSON.parse(topologyContent)
            tableData.push({id, name, savedTime: this.longDate(updatedTime), submitInfo, submitTime: this.longDate(submittedTime),
              submitted, content: contentJson, operator, topologyId})
            if (topologyId) {
              topologyIds.push(topologyId)
            }
          })
        }
        this.tableData = tableData
        /*
        if (topologyIds.length > 0) {
          let url = '/rest/topologies/realStatus?topologyIds=' + topologyIds.join(',')
          logger.debug('send GET "%s"', url)
          get(url, data => {
            if (data && data.length > 0) {
              data.forEach(realStatus => {
                let {id, status} = realStatus
                this.tableData.forEach(row => {
                  if (id === row.topologyId) {
                    row.status = status
                    return
                  }
                })
              })
            }
          })
        }
        */
      },
      handleButtonClick(operate, pagination) {
        if (operate === 'detail') {
          let selection = this.selection
          if (!selection) {
            info('请在操作之前选择要操作的记录。')
          } else {
            let {id} = selection
            this.$router.push('/tasks/task/' + id)
          }
        } else if (operate === 'refresh') {
          this.handleRefresh(pagination)
          return
        } else if (operate === 'add') {
          this.cacheClean()
          this.goto({owner: this, path: '/tasks/add', name: '新增拓扑任务'})
        } else if (operate === 'edit' || operate === 'delete') {
          // 检查是否已经选择操作项
          let selection = this.selection
          if (!selection) {
            info('请在操作之前选择要操作的记录。')
          } else {
            if (selection.submitted) {
              warn('不能修改或删除已经提交到STORM集群中的计算拓扑。')
              return
            }
            if (operate === 'edit') {
              let topology = selection.content
              topology['id'] = selection.id
              this.setTopology(topology)
              this.goto({owner: this, path: '/tasks/edit', name: '修改拓扑任务'})
            } else {
              confirm('你真的要删除计算拓扑[' + selection.name + ']吗？删除后不可恢复！', _ => {
                let url = '/rest/topology/delete?topologyId=' + selection.id
                logger.debug('send GET "%s"', url)
                get(url, data => {
                  if (data) {
                    this.handleRefresh(null)
                    info('删除计算拓扑成功。')
                  }
                })
              })
            }
          }
        }
      },
      handleRefresh(pagination) {
        let url = '/rest/topologies'
        if (this.queryByPage) {
          if (!pagination) {
            pagination = {total: 0, size: 20, page: 1}
          }
          logger.debug('send POST "%s", page: %j.', url, pagination)
          post(url, pagination, ({data, pagination}) => {
            logger.debug('Response success, data: %j, page: %j.', data, pagination)
            this.$refs['panePaginateList'].setPagination(pagination)
            this.fillTableData(data)
            info('刷新数据成功。')
          })
        } else {
          logger.debug('send GET "%s"', url)
          get(url, data => {
            this.fillTableData(data)
            info('刷新数据成功。')
          })
        }
      },
      handleRebalance(id) {
        let url = '/api/v1/topology/' + id + '/rebalance/3'
        logger.debug('send POST "%s"', url)
        post(url, {}, data => {
          if(data && data.status === 'success') {
            info('对拓扑[' + data.name + ']重新负载均衡操作成功！')
          } else {
            warn('对拓扑[\' + data.name + \']重新负载均衡操作失败！')
          }
        })
      },
      handleKill(id) {
        let url = '/rest/topology/kill?topologyId=' + id
        logger.debug('send POST "%s"', url)
        get(url, data => {
          if (data) {
            this.handleRefresh(null)
            info('杀死拓扑[' + data.name + ']操作成功！')
          } else {
            warn('杀死拓扑[' + data.name + ']操作失败！')
          }
        })
      },
      handleSubmit(id, simulation) {
        let url = '/rest/topology/submit/' + id + '?simulation=' + (simulation ? 'true' : 'false')
        logger.debug('send GET "%s"', url)
        get(url, data => {
          if (data) {
            this.handleRefresh(null)
            info('提交计算拓扑成功。')
          }
        }, errorMessage => {
          // 如果发生错误，多数是超时，后台还在处理，延迟5秒刷新
          setTimeout(_ => {
            this.handleRefresh(null)
          }, 5000)
        })
      },
      handleCurrentChange(currentRow, oldCurrentRow) {
        this.selection = currentRow
      }
    },
    mounted() {
      this.handleRefresh(null)
    }
  }
</script>
