<style rel="stylesheet/less" lang="less" scoped>
  @import "../style/base.less";

  .layout-content {
    magin: 0 auto;
    .layout-page {
      margin: 10px 30%;
    }
  }

  .layout-paginate {
    margin-left: 20%;
  }

  .layout-buttons {
    display: inline-block;
    float: right;
    margin: 0 10px 0 3px;
    .button {
      padding: 10px;
      font-size: @content-text-font-size;
      color: @button-color;
      &:hover {
        color: @button-hover-color
      }
    }
    .button-icon {
      font-size: 12px;
    }
  }
</style>

<template>
  <div>
    <el-row type="flex">
      <el-col :span="24">
        <span class="layout-buttons">
          <el-button v-if="showAdd" class="button" :plain="true" type="text" @click="handleOperate('add')">
            <icon class="button-icon" name="add"></icon>添加</el-button>
          <el-button v-if="showEdit" class="button" :plain="true" type="text" @click="handleOperate('edit')">
            <icon class="button-icon" name="edit"></icon>修改</el-button>
          <el-button v-if="showDelete" class="button" :plain="true" type="text" @click="handleOperate('delete')">
            <icon class="button-icon" name="delete"></icon>删除</el-button>
          <el-button v-if="showDetail" class="button" :plain="true" type="text" @click="handleOperate('detail')">
            <icon class="button-icon" name="details"></icon>详情</el-button>
          <el-button v-if="showRefresh" class="button" :plain="true" type="text" @click="handleOperate('refresh')">
            <icon class="button-icon" name="refresh"></icon>刷新</el-button>
        </span>
      </el-col>
    </el-row>
    <el-row type="flex">
      <el-col :span="24">
        <div class="layout-content">
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
        if (pagination) {
          this.pagination = pagination
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
