<style rel="stylesheet/less" lang="less" scoped>
  @import "../style/base.less";

  .layout-content {
    magin: 0 auto;
    .layout-page {
      margin: 10px 30%;
    }
  }
</style>

<template>
  <div>
    <el-row type="flex">
      <el-col :span="24">
        <span class="layout-buttons">
          <el-button v-if="showAdd" class="button" :plain="true" type="text" @click="handleOperate('add')">
            <ds-icon class="button-icon" name="add"></ds-icon>添加</el-button>
          <el-button v-if="showEdit" class="button" :plain="true" type="text" @click="handleOperate('edit')">
            <ds-icon class="button-icon" name="edit"></ds-icon>修改</el-button>
          <el-button v-if="showDelete" class="button" :plain="true" type="text" @click="handleOperate('delete')">
            <ds-icon class="button-icon" name="delete"></ds-icon>删除</el-button>
          <el-button v-if="showRefresh" class="button" :plain="true" type="text" @click="handleOperate('refresh')">
            <ds-icon class="button-icon" name="refresh"></ds-icon>刷新</el-button>
        </span>
      </el-col>
    </el-row>
    <el-row type="flex">
      <el-col :span="24">
        <div class="layout-content">
          <slot></slot>
          <div class="layout-page">
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
  import DsIcon from './icon.vue'

  export default {
    name: 'pane-paginate-list',
    components: {DsIcon},
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
      showRefresh: {
        default: true
      }
    },
    data() {
      return {
        pagination: {
          total: 0,
          size: 20,
          page: 1
        }
      }
    },
    methods: {
      setPagination(pagination) {
        if (pagination) {
          this.pagination = pagination
        }
      },
      handleOperate(operate) {
        this.$emit('buttonHandle', operate, this.pagination)
      },
      handleSizeChange(size) {
        this.pagination.size = size
      },
      handlePageChange(page) {
        this.pagination.page = page
        this.$emit('buttonHandle', 'refresh', this.pagination)
      }
    }
  }
</script>
