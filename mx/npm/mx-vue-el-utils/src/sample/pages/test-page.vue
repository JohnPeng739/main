<template>
  <div>
    <mx-paginate-table ref="paginatePane" :buttons-layout="buttonsLayout" v-on:buttonHandle="handleButtonClick">
      <el-table id="__table" :data="tableData" class="table" :maxHeight="maxHeight" header-row-class-name="table-header"
                highlight-current-row @current-change="handleCurrentChange">
        <el-table-column prop="code" label="代码"></el-table-column>
        <el-table-column prop="name" label="名称"></el-table-column>
        <el-table-column prop="desc" label="描述"></el-table-column>
      </el-table>
    </mx-paginate-table>
  </div>
</template>

<script>
  import { logger } from 'mx-app-utils'
  import {MxNotify} from '../../../dist/mx-vue-el-utils.min'

  export default {
    name: 'test-paginate-content',
    data () {
      return {
        maxHeight: 500,
        tableData: [],
        buttonsLayout: ['delete', 'details', 'edit', 'refresh', 'add', {code: 'custom', name: '自定义', icon: 'apps'}]
      }
    },
    methods: {
      handleRefresh () {
        logger.debug('refresh data.')
        this.$refs['paginatePane'].setPagination({total: 120, size: 20, page: 3})
        let data = []
        let index = 1
        for (; index <= 15; index++) {
          data.push({code: 'code ' + index, name: 'name ' + index, desc: 'description ' + index})
        }
        this.tableData = data
        MxNotify.info('重新获取并加载数据成功。')
      },
      handleButtonClick (operate, pagination) {
        logger.debug('Click a button, operate: %s, page: %j.', operate, pagination)
      },
      handleCurrentChange (currentRow, oldCurrentRow) {
        logger.debug('Current row changed: new: %j, old: %j.', currentRow, oldCurrentRow)
      }
    },
    mounted () {
      this.maxHeight = document.getElementById('__table').clientHeight
      this.handleRefresh()
    }
  }
</script>
