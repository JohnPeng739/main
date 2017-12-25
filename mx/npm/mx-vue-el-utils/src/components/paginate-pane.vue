<template>
  <div>
    <el-row type="flex">
      <el-col :span="24">
        <span class="pg-layout-buttons">
          <el-button v-if="showAdd" class="button" :plain="true" type="text" @click.native="handleOperate('add')">
            <icon name="add"></icon>{{$t('button.add')}}</el-button>
          <el-button v-if="showEdit" class="button" :plain="true" type="text" @click.native="handleOperate('edit')">
            <icon name="edit"></icon>{{$t('button.edit')}}</el-button>
          <el-button v-if="showDelete" class="button" :plain="true" type="text" @click.native="handleOperate('delete')">
            <icon name="delete"></icon>{{$t('button.delete')}}</el-button>
          <el-button v-if="showDetail" class="button" :plain="true" type="text" @click.native="handleOperate('detail')">
            <icon name="details"></icon>{{$t('button.detail')}}</el-button>
          <el-button v-if="showRefresh" class="button" :plain="true" type="text"
                     @click.native="handleOperate('refresh')">
            <icon name="refresh"></icon>{{$t('button.refresh')}}</el-button>
        </span>
      </el-col>
    </el-row>
    <el-row type="flex">
      <el-col :span="24">
        <div class="pg-layout-content">
          <slot></slot>
          <div class="layout-paginate">
            <el-pagination @size-change="handleSizeChange" @current-change="handlePageChange" :total="pagination.total"
                           :current-page="pagination.page" :page-size="pagination.size" :page-sizes="[10, 20, 50, 100]"
                           layout="total, sizes, prev, pager, next, jumper"></el-pagination>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import { logger } from 'mx-app-utils'
  import Icon from '@/components/icon'

  export default {
    name: 'paginate-content-pane',
    components: {Icon},
    props: {
      showAdd: {
        default: true
      },
      showEdit: {
        default: true
      },
      showDelete: {
        default: true
      },
      showDetail: {
        default: true
      },
      showRefresh: {
        default: true
      }
    },
    data () {
      return {
        pagination: {
          total: 0,
          size: 20,
          page: 1
        }
      }
    },
    methods: {
      setPagination (pagination) {
        let {total, size, page} = this.pagination
        if (pagination && (pagination.total !== total || pagination.size !== size || pagination.page !== page)) {
          this.pagination = pagination
          logger.debug('Set paginate: %j.', pagination)
        }
      },
      handleOperate (operate) {
        this.$emit('buttonHandle', operate, this.pagination)
      },
      handleSizeChange (size) {
        this.pagination.size = size
        this.$emit('buttonHandle', 'refresh', this.pagination)
      },
      handlePageChange (page) {
        this.pagination.page = page
        this.$emit('buttonHandle', 'refresh', this.pagination)
      }
    }
  }
</script>
