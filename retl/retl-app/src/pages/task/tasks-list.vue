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
    .layout-page {
      margin: 10px 20%;
    }
  }
</style>

<template>
  <div class="layout-content">
    <el-table :max-height="570" :data="tableData" class="layout-table" highlight-current-row>
      <el-table-column type="expand">
        <template scope="scope">
          <el-input type="textarea" :value="JSON.stringify(scope.row.content, null, '    ')"
                    :rows="15" :disabled="true"></el-input>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="名称"></el-table-column>
      <el-table-column prop="savedTime" label="保存时间" width="180"></el-table-column>
      <el-table-column prop="submitTime" label="提交时间" width="180"></el-table-column>
      <el-table-column prop="submited" label="是否提交" width="200">
        <template scope="scope">
          <span v-if="scope.row.submitted">已提交集群</span>
          <span v-else style="color: red;">未提交集群</span>
          <el-button class="button" v-if="!scope.row.submitted" size="mini" @click="handleSubmit(scope.row.id)">提交集群</el-button>
        </template>
      </el-table-column>
      <el-table-column prop="operator" label="操作人员" width="120"></el-table-column>
    </el-table>
    <div class="layout-page">
      <el-pagination @size-change="handleSizeChange" @current-change="handlePageChange" :total="pagination.total"
                     :current-page="pagination.page" :page-size="pagination.size" :page-sizes="[10, 20, 50, 100]"
                     layout="total, sizes, prev, pager, next, jumper"></el-pagination>
    </div>
  </div>
</template>

<script>
  import {logger} from 'dsutils'
  import {get, post} from '../../assets/ajax'
  import {formatDateTime} from '../../assets/date-utils'

  export default {
    name: 'page-tasks-list',
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
        pagination: {
          total: 0,
          size: 20,
          page: 1
        }
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
      refreshData() {
        let url = '/rest/topologies'
        logger.debug('send POST "%s"', url)
        post(url, this.pagination, ({data, pagination}) => {
          logger.debug('Response success, data: %j, page: %j.', data, pagination)
          this.pagination = pagination
          this.fillTableData(data)
        })
      },
      handleSubmit(id) {
        let url = '/rest/topology/submit/' + id
        logger.debug('send GET "%s"', url)
        get(url, data => {
          if (data) {
            this.refreshData()
            info( '提交计算拓扑成功。')
          }
        })
      },
      handleSizeChange(size) {
        this.pagination.size = size
      },
      handlePageChange(page) {
        this.pagination.page = page
        this.refreshData()
      }
    },
    mounted() {
      this.refreshData()
    }
  }
</script>
