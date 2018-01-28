<template>
  <div>
    <el-row type="flex">
      <el-col :span="24">
        <span class="pg-layout-buttons">
          <el-tooltip v-for="item in buttons" :key="item.code" effect="dark" placement="bottom">
            <div slot="content">
              <span>{{item.name}}</span>
            </div>
            <el-button :plain="true" type="text"
                       @click.native="handdleButtonClick(item.code)" class="button">
              <mx-icon v-if="item.icon" :name="item.icon"></mx-icon>
            </el-button>
          </el-tooltip>
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
  import MxIcon from '@/components/mx-icon'
  import { t } from '@/locale'

  let defaultButtons = ['add', 'edit', 'delete', 'details', 'refresh']

  export default {
    name: 'mx-paginate-table',
    components: {MxIcon},
    props: {
      buttonsLayout: Array
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
    computed: {
      buttons () {
        let layout = this.buttonsLayout
        if (layout === null || layout === undefined) {
          layout = defaultButtons
        }
        let buttons = []
        if (layout && layout instanceof Array) {
          layout.forEach(button => {
            if (button) {
              if (typeof button === 'string' && defaultButtons.indexOf(button) >= 0) {
                buttons.push({code: button, name: t('button.' + button), icon: button})
              } else if (button.code && button.name) {
                buttons.push(button)
              }
            }
          })
        }
        return buttons
      }
    },
    methods: {
      setPagination (pagination) {
        let {total} = this.pagination
        if (pagination && (pagination.total !== total)) {
          this.pagination.total = pagination.total
          logger.debug('Set paginate: %j.', pagination)
        }
      },
      handdleButtonClick (operate) {
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
