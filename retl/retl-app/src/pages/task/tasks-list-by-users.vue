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
    <pane-paginate-list ref="panePaginateList" v-on:buttonHandle="handleButtonClick" :showAdd="false" :showEdit="false"
                        :showDelete="false">
      <el-table :max-height="570" :data="tableData" class="layout-table" highlight-current-row
                @current-change="handleCurrentChange">
        <el-table-column prop="name" label="名称"></el-table-column>
        <el-table-column prop="savedTime" label="保存时间" width="180"></el-table-column>
        <el-table-column prop="submitTime" label="提交时间" width="180"></el-table-column>
        <el-table-column prop="submited" label="是否提交" width="200">
          <template slot-scope="scope">
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
  import {logger} from 'mx-app-utils'
  import {info} from '../../assets/notify'
  import {get, post} from '../../assets/ajax'
  import {formatDateTime} from '../../assets/date-utils'
  import PanePaginateList from '../../components/pane-paginate-list.vue'

  export default {
    name: 'page-tasks-list-by-users',
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
        queryByPage: true,
        tableData: [],
        selection: null
      }
    },
    methods: {
      fillTableData(topologies) {
        let tableData = []
        if (topologies && topologies.length > 0) {
          topologies.forEach(topology => {
            let {id, name, updatedTime, submitted, submittedTime, topologyContent, operator} = topology
            let contentJson = JSON.parse(topologyContent)
            tableData.push({id, name, savedTime: this.longDate(updatedTime), submitTime: this.longDate(submittedTime),
              submitted, content: contentJson, operator})
          })
        }
        this.tableData = tableData
      },
      refreshData(pagination) {
        let url = '/rest/topologies'
        if (this.queryByPage) {
          if (!pagination) {
            pagination = {total: 0, size: 20, page: 1}
          }
          logger.debug('send POST "%s"', url)
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
          this.refreshData(this.pagination)
        }
      },
      handleCurrentChange(currentRow, oldCurrentRow) {
        this.selection = currentRow
      }
    },
    mounted() {
      this.handleButtonClick('refresh', null)
    }
  }
</script>
