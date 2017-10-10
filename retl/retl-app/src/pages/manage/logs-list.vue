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
    }
    .layout-page {
      margin: 10px 20%;
    }
  }
</style>

<template>
  <div class="layout-content">
  <el-table :max-height="570" :data="tableData" class="layout-table" highlight-current-row>
    <el-table-column prop="message" label="操作内容"></el-table-column>
    <el-table-column prop="operator" label="操作人" width="120"></el-table-column>
    <el-table-column prop="createdTime" label="操作时间" width="200">
      <template scope="scope">
        {{longDate(scope.row.createdTime)}}
      </template>
    </el-table-column>
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
  import {post} from '../../assets/ajax'
  import {formatDateTime} from '../../assets/date-utils'

  export default {
    name: 'page-list-logs',
    data() {
      return {
        longDate(time) {
          let datetime = new Date(time)
          return formatDateTime(datetime)
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
      refreshData() {
        let url = '/rest/user/logs'
        logger.debug('send POST "%s"', url)
        post(url, this.pagination, ({data, pagination}) => {
          this.pagination = pagination
          this.tableData = data
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
