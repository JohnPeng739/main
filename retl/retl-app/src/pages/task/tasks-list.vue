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
</style>

<template>
  <div>
    <pane-paginate-list ref="panePaginateList" v-on:buttonHandle="handleButtonClick" :showDelete="false">
      <el-table :max-height="570" :data="tableData" class="layout-table" highlight-current-row
                @current-change="handleCurrentChange">
        <el-table-column type="expand">
          <template scope="scope">
            <el-form label-position="left">
              <el-form-item label="拓扑配置">
                <el-input type="textarea" :value="JSON.stringify(scope.row.content, null, '    ')"
                          :rows="15" :disabled="true"></el-input>
              </el-form-item>
              <el-form-item label="提交信息">
                <el-input type="textarea" :value="scope.row.submitInfo"
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
            <el-button class="button" v-if="!scope.row.submitted" size="mini" @click="handleSubmit(scope.row.id, false)">提交集群</el-button>
            <el-button class="button" v-if="!scope.row.submitted && isDebug" size="mini" @click="handleSubmit(scope.row.id, true)">本地仿真</el-button>
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
  import {info} from '../../assets/notify'
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
        if (topologies && topologies.length > 0) {
          topologies.forEach(topology => {
            let {id, name, updatedTime, submitted, submitInfo, submittedTime, topologyContent, operator} = topology
            let contentJson = JSON.parse(topologyContent)
            tableData.push({id, name, savedTime: this.longDate(updatedTime), submitInfo, submitTime: this.longDate(submittedTime),
              submitted, content: contentJson, operator})
          })
        }
        this.tableData = tableData
      },
      handleButtonClick(operate, pagination) {
        if (operate === 'refresh') {
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
            if (operate === 'edit') {
              let topology = selection.content
              topology['id'] = selection.id
              this.setTopology(topology)
              this.goto({owner: this, path: '/tasks/edit', name: '修改拓扑任务'})
            }
          }
        }
      },
      handleRefresh(pagination) {
        if (!pagination) {
          pagination = {total: 0, size: 20, page: 1}
        }
        let url = '/rest/topologies'
        logger.debug('send POST "%s"', url)
        post(url, pagination, ({data, pagination}) => {
          logger.debug('Response success, data: %j, page: %j.', data, pagination)
          this.$refs['panePaginateList'].setPagination(pagination)
          this.fillTableData(data)
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
