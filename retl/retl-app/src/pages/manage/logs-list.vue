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
    <pane-paginate-list ref="panePaginateList" v-on:buttonHandle="handleButtonClick"
                        :showAdd="false" :showEdit="false" :showDelete="false">
      <el-table :max-height="570" :data="tableData" class="layout-table" highlight-current-row>
        <el-table-column prop="message" label="操作内容"></el-table-column>
        <el-table-column prop="operator" label="操作人" width="120"></el-table-column>
        <el-table-column prop="createdTime" label="操作时间" width="200">
          <template scope="scope">
            {{longDate(scope.row.createdTime)}}
          </template>
        </el-table-column>
      </el-table>
    </pane-paginate-list>
  </div>
</template>

<script>
  import {logger} from 'dsutils'
  import {post} from '../../assets/ajax'
  import {formatDateTime} from '../../assets/date-utils'
  import PanePaginateList from '../../components/pane-paginate-list.vue'

  export default {
    name: 'page-list-logs',
    components: {PanePaginateList},
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
      refreshData(pagination) {
        if (!pagination) {
          pagination = {total: 0, size: 20, page: 1}
        }
        let url = '/rest/user/logs'
        logger.debug('send POST "%s"', url)
        post(url, pagination, ({data, pagination}) => {
          this.$refs['panePaginateList'].setPagination(pagination)
          this.tableData = data
        })
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
