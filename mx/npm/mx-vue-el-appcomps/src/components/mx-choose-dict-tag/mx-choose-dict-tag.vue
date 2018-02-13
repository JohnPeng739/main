<template>
  <mx-choose-tag ref="tag1" v-model="chooseDicts" displayFormat="{code} - {name}" v-on:selected="handleSelected"
                 @change="handleChanged" :disabled="disabled" type="gray" :popover-width="550">
    <el-row type="flex">
      <el-col :span="24">
        <el-table ref="table" :data="tableData" class="inner-table" :max-height="400" :highlight-current-row="!multiple"
                  @current-change="handleCurrentChange" @selection-change="handleSelectChange"
                  header-row-class-name="table-header" style="width: 100%;">
          <el-table-column v-if="multiple" type="selection" width="50" show-overflow-tooltip></el-table-column>
          <el-table-column prop="code" :label="$t('rbac.common.fields.code')" width="150"></el-table-column>
          <el-table-column prop="name" :label="$t('rbac.common.fields.name')"></el-table-column>
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
  </mx-choose-tag>
</template>

<script>
  import { logger } from 'mx-app-utils'
  import { MxAjax } from 'mx-vue-el-utils'

  export default {
    name: 'mx-choose-dict-tag',
    props: ['value', 'restUrl', 'disabled', 'multiple'],
    data () {
      return {
        tableData: [],
        selected: [],
        pagination: {
          total: 0,
          size: 40,
          page: 1
        }
      }
    },
    computed: {
      chooseDicts: {
        get () {
          return this.value
        },
        set (newValue) {
          if (newValue !== null && newValue !== undefined) {
            this.$emit('input', newValue)
          }
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
      handleChanged () {
        this.$refs['table'].clearSelection()
      },
      handleSelected (done) {
        if (this.multiple) {
          done(this.selected)
        } else {
          done(this.selected[0])
        }
      },
      handleCurrentChange (currentRow, oldCurrentRow) {
        this.selected = [currentRow]
      },
      handleSelectChange (val) {
        this.selected = val
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
