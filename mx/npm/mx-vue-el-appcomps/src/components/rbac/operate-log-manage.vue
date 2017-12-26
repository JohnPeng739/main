<template>
  <paginate-pane ref="paginatePane" v-on:buttonHandle="handleButtonClick" :show-add="false" :show-edit="false"
                 :show-delete="false" :show-detail="false">
    <el-table :data="tableData" :max-height="tableMaxHeight" highlight-current-row>
      <el-table-column prop="createdTime" :label="$t('rbac.logs.fields.time')" :width="170">
        <template slot-scope="scope">
          {{parseDatetime(scope.row.createdTime)}}
        </template>
      </el-table-column>
      <el-table-column prop="content" :label="$t('rbac.logs.fields.content')"></el-table-column>
      <el-table-column prop="operator" :label="$t('rbac.common.fields.operator')" :width="150"></el-table-column>
    </el-table>
  </paginate-pane>
</template>

<script>
  import { logger, formatter } from 'mx-app-utils'
  import { ajax, notify, PaginatePane } from 'mx-vue-el-utils'

  export default {
    name: 'page-operate-log-manage',
    components: {PaginatePane},
    props: {
      tableMaxHeight: {
        type: Number,
        default: 540
      }
    },
    data () {
      return {
        tableData: []
      }
    },
    methods: {
      parseDatetime (longDate) {
        if (longDate) {
          return formatter.formatDatetime(longDate)
        } else {
          return this.$t('rbac.common.fields.NA')
        }
      },
      refreshData (pagination) {
        ajax.post('/rest/logs', pagination, (pagination, data) => {
          logger.debug('response, page: %j, data: %j.', pagination, data)
          if (data && data instanceof Array) {
            this.tableData = data
            this.$refs['paginatePane'].setPagination(pagination)
            notify.info(this.$t('rbac.common.message.refreshSuccess', {module: this.$t('rbac.logs.module')}))
          }
        })
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
      this.refreshData(null)
    }
  }
</script>
