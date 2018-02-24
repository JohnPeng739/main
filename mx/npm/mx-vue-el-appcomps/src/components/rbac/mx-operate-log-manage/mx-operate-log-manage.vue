<template>
  <mx-paginate-table ref="paginatePane" v-on:buttonHandle="handleButtonClick" :buttons-layout="['refresh']">
    <el-table :data="tableData" class="table" :max-height="tableMaxHeight" highlight-current-row
              header-row-class-name="table-header">
      <el-table-column prop="createdTime" :label="$t('common.time')" :width="170">
        <template slot-scope="scope">
          {{parseDatetime(scope.row.createdTime)}}
        </template>
      </el-table-column>
      <el-table-column prop="content" :label="$t('common.content')"></el-table-column>
      <el-table-column prop="operator" :label="$t('common.operator')" :width="150"></el-table-column>
    </el-table>
  </mx-paginate-table>
</template>

<script>
  import { logger, formatter } from 'mx-app-utils'
  import { MxAjax, MxNotify } from 'mx-vue-el-utils'

  export default {
    name: 'mx-operate-log-manage',
    data () {
      return {
        tableMaxHeight: 540,
        tableData: []
      }
    },
    methods: {
      parseDatetime (longDate) {
        if (longDate) {
          return formatter.formatDatetime(longDate)
        } else {
          return this.$t('common.NA')
        }
      },
      refreshData (pagination) {
        let fnSuccess = (pagination, data) => {
          logger.debug('response, page: %j, data: %j.', pagination, data)
          if (data && data instanceof Array) {
            this.tableData = data
            this.$refs['paginatePane'].setPagination(pagination)
            MxNotify.info(this.$t('message.list.refreshSuccess', [this.$t('pages.operateLog.name')]))
          }
        }
        MxAjax.post({url: '/rest/logs', data: pagination, fnSuccess})
      },
      handleButtonClick (operate, pagination) {
        switch (operate) {
          case 'refresh':
            this.refreshData(pagination)
            break
        }
      }
    },
    mounted () {
      if (!this.$isServer) {
        if (this.$el) {
          this.tableMaxHeight = this.$el.clientHeight - 110
        }
      }
      this.refreshData(null)
    }
  }
</script>
