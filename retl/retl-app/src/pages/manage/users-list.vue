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
  <div>
    <pane-paginate-list ref="panePaginateList" v-on:buttonHandle="handleButtonClick" :showAdd="false"
                        :showEdit="false" :showDelete="false">
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
    </pane-paginate-list>
  </div>
</template>

<script>
  import {logger} from 'mx-app-utils'
  import {get, post} from '../../assets/ajax'
  import {info} from '../../assets/notify'
  import {formatDateTime} from '../../assets/date-utils'
  import PanePaginateList from '../../components/pane-paginate-list.vue'

  export default {
    name: 'page-user-logs',
    components: {PanePaginateList},
    data() {
      return {
        longDate(time) {
          let datetime = new Date(time)
          return formatDateTime(datetime)
        },
        queryByPage: false,
        tableData: []
      }
    },
    methods: {
      refreshData(pagination) {
        let url = '/rest/users'
        if (this.queryByPage) {
          if (!pagination) {
            pagination = {total: 0, size: 20, page: 1}
          }
          logger.debug('send POST "%s", page: %j.', url, pagination)
          post(url, pagination, ({data, pagination}) => {
            logger.debug('Response success, data: %j, page: %j.', data, pagination)
            this.$refs['panePaginateList'].setPagination(pagination)
            this.tableData = data
            info('刷新数据成功。')
          })
        } else {
          logger.debug('send GET "%s"', url)
          get(url, data => {
            this.tableData = data
            info('刷新数据成功。')
          })
        }
      },
      handleButtonClick(operate, pagination) {
        if (operate === 'refresh') {
          this.refreshData(pagination)
        }
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
      this.refreshData(null)
    }
  }
</script>
