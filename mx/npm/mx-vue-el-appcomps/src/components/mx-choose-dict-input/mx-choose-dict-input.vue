<template>
  <mx-choose-input ref="chooseDict" v-model="chooseDict" v-on:selected="handleSelected" displayFormat="{code} - {name}"
                   :popover-width="550" :readonly="true" :disabled="disabled">
    <el-row type="flex">
      <el-col :span="24">
        <el-table :data="tableData" class="inner-table" :max-height="400" highlight-current-row
                  @current-change="handleCurrentChange" header-row-class-name="table-header">
          <el-table-column prop="code" :label="$t('common.code')"></el-table-column>
          <el-table-column prop="name" :label="$t('common.name')"></el-table-column>
        </el-table>
      </el-col>
    </el-row>
    <el-row type="flex" justify="end">
      <el-col :span="24" class="pg-layout-content">
        <div class="layout-paginate">
          <el-pagination @current-change="handlePageChange" :total="pagination.total" :current-page="pagination.page"
                         :page-size="pagination.size" layout="prev, pager, next, jumper"></el-pagination>
        </div>
      </el-col>
    </el-row>
  </mx-choose-input>
</template>

<script>
  import { logger } from 'mx-app-utils'
  import { MxAjax } from 'mx-vue-el-utils'

  export default {
    name: 'mx-choose-dict-input',
    props: ['value', 'restUrl', 'disabled'],
    data () {
      return {
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
        let url = this.restUrl
        logger.debug('send POST "%s".', url)
        let fnSuccess = (pagination, data) => {
          this.pagination.total = pagination.total
          this.tableData = data
        }
        MxAjax.post({url, data: this.pagination, fnSuccess})
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
