<style rel="stylesheet/less" lang="less" scoped>
  @import "../../style/base.less";

  .layout-buttons {
    display: inline-block;
    float: right;
    margin: 3px;
    .button {
      padding-left: 10px;
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

  .layout-spout-info {
    min-width: 400px;
    max-width: 1000px;
    margin: auto;
  }
</style>

<template>
  <div class="layout-spout-info">
    <el-row type="flex">
      <el-col :offset="12" :span="12">
        <span class="layout-buttons">
          <el-button class="button" :plain="true" type="text" @click="handleOperate('add')">
            <ds-icon class="button-icon" name="add"></ds-icon>
            添加
          </el-button>
          <el-button class="button" :plain="true" type="text" @click="handleOperate('edit')">
            <ds-icon class="button-icon" name="edit"></ds-icon>
            修改
          </el-button>
          <el-button class="button" :plain="true" type="text" @click="handleOperate('delete')">
            <ds-icon class="button-icon" name="delete"></ds-icon>
            删除
          </el-button>
          <el-button class="button" :plain="true" type="text" @click="handleOperate('detail')">
            <ds-icon class="button-icon" name="subject"></ds-icon>
            详情
          </el-button>
        </span>
      </el-col>
    </el-row>
    <el-row type="flex">
      <el-col :span="24">
        <el-table ref="tableSpouts" :data="tableData" highlight-current-row @current-change="handleCurrentChange" stripe
                  style="width: 100%">
          <el-table-column prop="name" label="名称" width="200"></el-table-column>
          <el-table-column prop="type" label="类型" width="150">
            <template slot-scope="scope">
              {{spoutTypes[scope.row.type]}}
            </template>
          </el-table-column>
          <el-table-column prop="conf" label="配置信息"></el-table-column>
        </el-table>
      </el-col>
    </el-row>
    <keep-alive v-if="!dialogDestroyed">
      <dialog-spout-info ref="dialogSpoutInfo" v-on:submit="handleDialogSubmit" v-on:close="handleDialogClose"
                         v-on:saveZookeepers="handleSaveZookeepers"></dialog-spout-info>
    </keep-alive>
  </div>
</template>

<script>
  import {mapGetters, mapActions} from 'vuex'
  import {logger} from 'mx-app-utils'
  import {warn, info} from '../../assets/notify'
  import DsIcon from '../icon.vue'
  import DialogSpoutInfo from './dialog-spout-info.vue'

  export default {
    name: 'topology-spout-info',
    components: {DsIcon, DialogSpoutInfo},
    data() {
      return {
        tableData: [],
        list: this.spouts,
        currentRow: null,
        dialogDestroyed: false
      }
    },
    computed: {
      ...mapGetters(['spoutTypes', 'topology', 'zookeepers', 'jdbcDataSources', 'jmsDataSources', 'spouts'])
    },
    methods: {
      ...mapActions(['setSpouts', 'setZookeepers']),
      fillTableData() {
        let tableData = []
        let spouts = this.spouts
        this.list = spouts
        if (spouts) {
          spouts.forEach(spout => {
            let {name, type, parallelism, configuration} = spout
            let conf = JSON.stringify(spout)
            tableData.push({name, type, parallelism, configuration, conf})
          })
        }
        this.tableData = tableData
      },
      getSpoutTypeLabel(value) {
        return spoutTypeLabel(value)
      },
      validated() {
        let spouts = this.list
        if (spouts && spouts.length <= 0) {
          warn('切换之前必须至少输入一个有效的采集源。')
          return false
        } else if (spouts && spouts.length > 0) {
          let foundJdbc = false
          spouts.forEach(spout => {
            if (spout && spout.type === 'jdbc') {
              foundJdbc = true
              return
            }
          })
          if (foundJdbc && spouts.length !== 1) {
            // jdbc类型的Spout必须单独配置在一个独立的拓扑中，不能和其他Spout同时配置
            warn('你在配置一个jdbc类型采集源的同时，配置了其他的采集源，这是不被许可的。')
            return false
          }
        }
        return true
      },
      cacheData() {
        this.setSpouts(this.list)
      },
      handleOperate(operate) {
        let spoutInfo = {}
        let spout = this.currentRow
        if (operate === 'edit' || operate === 'delete' || operate === 'detail') {
          // 检查是否选择了spout，否则跳出
          if (spout === null || spout === undefined) {
            info('在操作之前你需要选中一个现有的数据源。')
            return
          } else {
            let {name, type, parallelism, configuration} = this.currentRow
            spoutInfo = {name, type, parallelism, configuration}
          }
        } else if (operate === 'add') {
          spoutInfo = {name: '', type: 'jmsPull', parallelism: 1}
        }
        if (operate === 'delete') {
          let list = this.spouts
          if (list) {
            for (var index = list.length - 1; index >= 0; index--) {
              if (spout.name === list[index].name) {
                list.splice(index, 1)
              }
            }
          }
          this.list = list
          this.fillTableData()
        } else {
          this.$refs['dialogSpoutInfo'].show(operate, this.topology.type, this.zookeepers, this.jdbcDataSources,
            this.jmsDataSources, spoutInfo)
        }
      },
      handleSaveZookeepers(zookeepers) {
        this.setZookeepers(zookeepers)
      },
      handleDialogSubmit(mode, spout) {
        if (spout === null || spout === undefined) {
          warn('对话框提交的数据为空，请联系开发人员。')
          return
        }
        if (spout && spout.zookeepers) {
          // TODO
          logger.debug('commit zookeepers: %j.', spout.zookeepers)
        }
        if (mode === 'add') {
          this.list.unshift(spout)
          this.fillTableData()
          return
        } else if (mode === 'edit') {
          if (this.currentRow === null || this.currentRow === undefined) {
            warn('你没有选择数据')
          }
          if (this.spouts) {
            let spouts = this.spouts
            this.list = []
            for (var index = 0; index < spouts.length; index++) {
              if (this.currentRow.name === spouts[index].name) {
                let {name, type, parallelism, configuration} = spout
                spouts[index] = {name, type, parallelism, configuration}
                this.list = spouts
                this.fillTableData()
                return
              }
            }
          }
        } else {
          warn('提交数据仅支持添加和修改数据源的操作。')
        }
      },
      handleDialogClose() {
        this.dialogDestroyed = true
        setTimeout(_ => {
          this.dialogDestroyed = false
        }, 100)
      },
      handleCurrentChange(val) {
        this.currentRow = val
      }
    },
    mounted() {
      this.fillTableData()
    }
  }
</script>
