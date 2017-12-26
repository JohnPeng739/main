<template>
  <choose-input ref="chooseDict" v-model="chooseDict" v-on:selected="handleSelected" displayFormat="{code} - {name}"
                placeholder="请选择..." :popover-width="550" :readonly="true" :disabled="disabled">
    <el-row type="flex">
      <el-col :span="24">
        <el-table :data="tableData" :max-height="400" highlight-current-row @current-change="handleCurrentChange">
          <el-table-column prop="code" label="Code"></el-table-column>
          <el-table-column prop="name" label="Name"></el-table-column>
        </el-table>
      </el-col>
    </el-row>
    <el-row type="flex" justify="end">
      <el-col :span="23">
        <el-pagination @current-change="handlePageChange" :total="pagination.total" :current-page="pagination.page"
                       :page-size="pagination.size" layout="prev, pager, next, jumper"></el-pagination>
      </el-col>
    </el-row>
  </choose-input>
</template>

<script>
  import {logger} from 'mx-app-utils'
  import {ajax, ChooseInput} from 'mx-vue-el-utils'

  export default {
    name: 'choose-dict-input',
    components: {ChooseInput},
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
        ajax.post(url, this.pagination, (pagination, data) => {
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
