<template>
  <mx-choose-input ref="chooseDict" v-model="chooseDict" v-on:selected="handleSelected" displayFormat="{fullName}"
                   placeholder="t('button.choose')" :popover-width="550" :readonly="true" :disabled="disabled">
    <el-row type="flex">
      <el-col :span="24">
        <el-table :data="tableData" :max-height="400" highlight-current-row @current-change="handleCurrentChange">
          <el-table-column prop="fullName" :label="t('rbac.user.fields.name')"></el-table-column>
          <el-table-column prop="station" :label="t('rbac.user.fields.station')"></el-table-column>
        </el-table>
      </el-col>
    </el-row>
    <el-row type="flex" justify="end">
      <el-col :span="23">
        <el-pagination @current-change="handlePageChange" :total="pagination.total" :current-page="pagination.page"
                       :page-size="pagination.size" layout="prev, pager, next, jumper"></el-pagination>
      </el-col>
    </el-row>
  </mx-choose-input>
</template>

<script>
  import { logger } from 'mx-app-utils'
  import { t } from '@/locale'

  export default {
    name: 'mx-choose-user-input',
    props: ['value', 'disabled'],
    data () {
      return {
        t: t,
        tableData: [],
        selected: null,
        pagination: {
          total: 0,
          size: 40,
          page: 1
        }
      }
    },
    computed: {
      chooseDict: {
        get () {
          return this.value
        },
        set (newValue) {
          this.$emit('input', newValue)
        }
      }
    },
    methods: {
      refresh () {
        let url = '/rest/users'
        logger.debug('send POST "%s".', url)
        this.$mxPost(url, this.pagination, (pagination, data) => {
          this.pagination.total = pagination.total
          this.tableData = data
        })
      },
      handleSelected (done) {
        done(this.selected)
      },
      handleCurrentChange (currentRow, oldCurrentRow) {
        this.selected = currentRow
      },
      handlePageChange (page) {
        this.pagination.page = page
        this.refresh()
      }
    },
    mounted () {
      this.refresh()
    }
  }
</script>
