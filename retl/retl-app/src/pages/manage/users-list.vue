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
      <el-table-column prop="code" label="代码" width="100"></el-table-column>
      <el-table-column prop="name" label="姓名" width="200"></el-table-column>
      <el-table-column prop="valid" label="是否有效" width="100">
        <template scope="scope">
          <el-checkbox v-model="scope.row.valid" :disabled="true"></el-checkbox>
        </template>
      </el-table-column>
      <el-table-column prop="online" label="是否在线" width="100">
        <template scope="scope">
          <el-checkbox v-model="scope.row.online" :disabled="true"></el-checkbox>
        </template>
      </el-table-column>
      <el-table-column prop="updatedTime" label="登录时间">
        <template scope="scope">{{longDate(scope.row.updatedTime)}}</template>
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
    name: 'page-user-logs',
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
        let url = '/rest/users'
        logger.debug('send POST "%s"', url)
        post(url, this.pagination, ({data, pagination}) => {
          logger.debug('Response success, data: %j, page: %j.', data, pagination)
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
