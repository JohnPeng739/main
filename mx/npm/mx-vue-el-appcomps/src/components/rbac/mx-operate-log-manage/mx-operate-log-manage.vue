<template>
  <mx-paginate-table ref="paginatePane" v-on:buttonHandle="handleButtonClick" :buttons-layout="['refresh']">
    <el-table :data="tableData" :max-height="tableMaxHeight" highlight-current-row>
      <el-table-column prop="createdTime" :label="t('rbac.logs.fields.time')" :width="170">
        <template slot-scope="scope">
          {{parseDatetime(scope.row.createdTime)}}
        </template>
      </el-table-column>
      <el-table-column prop="content" :label="t('rbac.logs.fields.content')"></el-table-column>
      <el-table-column prop="operator" :label="t('rbac.common.fields.operator')" :width="150"></el-table-column>
    </el-table>
  </mx-paginate-table>
</template>

<script>
  import { logger, formatter } from 'mx-app-utils'
  import {t} from '@/locale'

  export default {
    name: 'mx-operate-log-manage',
    props: {
      tableMaxHeight: {
        type: Number,
        default: 540
      }
    },
    data () {
      return {
        t: t,
        tableData: []
      }
    },
    methods: {
      parseDatetime (longDate) {
        if (longDate) {
          return formatter.formatDatetime(longDate)
        } else {
          return t('rbac.common.fields.NA')
        }
      },
      refreshData (pagination) {
        this.$mxPost('/rest/logs', pagination, (pagination, data) => {
          logger.debug('response, page: %j, data: %j.', pagination, data)
          if (data && data instanceof Array) {
            this.tableData = data
            this.$refs['paginatePane'].setPagination(pagination)
            this.$mxInfo(t('rbac.common.message.refreshSuccess', {module: t('rbac.logs.module')}))
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
