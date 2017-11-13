<style rel="stylesheet/less" lang="less" scoped>
  @import "../../style/base.less";
</style>

<template>
  <div>
    <pane-paginate-list ref="panePaginateList" v-on:buttonHandle="handleButtonClick">
      <el-table :max-height="570" :data="tableData" class="layout-table" highlight-current-row
                @current-change="handleCurrentChange">
        <el-table-column prop="machineName" label="服务器" width="100"></el-table-column>
        <el-table-column prop="machineIp" label="IP地址" width="200"></el-table-column>
        <el-table-column prop="zookeeper" label="ZOOKEEPER服务">
          <template scope="scope">
            <span>{{JSON.stringify(scope.row.zookeeper)}}</span>
          </template>
        </el-table-column>
        <el-table-column prop="storm" label="STORM服务">
          <template scope="scope">
            <span>{{JSON.stringify(scope.row.storm)}}</span>
          </template>
        </el-table-column>
      </el-table>
    </pane-paginate-list>
    <keep-alive v-if="!dialogDestroyed">
      <dialog-server-config ref="dialogServer" v-on:submit="handleDialogSubmit"
                            v-on:close="handleDialogClose"></dialog-server-config>
    </keep-alive>
  </div>
</template>

<script>
  import {logger} from 'mx-app-utils'
  import {get, post, del} from '../../assets/ajax'
  import {info, confirm} from '../../assets/notify'
  import PanePaginateList from '../../components/pane-paginate-list.vue'
  import DialogServerConfig from './dialog-server-config.vue'

  export default {
    name: 'page-servers-list',
    components: {PanePaginateList, DialogServerConfig},
    data() {
      return {
        tableData: [],
        selection: null,
        queryByPage: false,
        dialogDestroyed: false
      }
    },
    methods: {
      handleButtonClick(operate, pagination) {
        if (operate === 'detail' || operate === 'edit' || operate === 'delete') {
          let selection = this.selection
          if (!selection) {
            info('请在操作之前选择要操作的记录。')
          } else {
            if (operate === 'delete') {
              let {machineName} = selection
              confirm('你真的要删除服务器[' + machineName + ']吗？删除后不可回复！', _ => {
                let url = '/rest/server/' + machineName
                logger.debug('send DELETE "%s"', url)
                del(url, data => {
                  if (data) {
                    this.refreshData(null)
                    info('删除服务器[' + machineName + ']成功。')
                  }
                })
              })
            } else {
              let {machineName, machineIp, zookeeper, storm} = selection
              this.$refs['dialogServer'].show(operate, {machineName, machineIp, zookeeper, storm})
            }
          }
        } else if (operate === 'add') {
          this.$refs['dialogServer'].show(operate, {machineName: '', machineIp: ''})
        } else if (operate === 'refresh') {
          this.refreshData(pagination)
        }
      },
      handleDialogSubmit(mode, server) {
        logger.debug('submit, mode: %s, server: %j.', mode, server)
        let url = '/rest/server'
        logger.debug('send POST "%s"', url)
        post(url, server, data => {
          if (data) {
            info('保存配置信息成功')
            this.refreshData(null)
          }
        })
      },
      handleDialogClose() {
        this.dialogDestroyed = true
        setTimeout(_ => this.dialogDestroyed = false, 100)
      },
      handleCurrentChange(currentRow, oldCurrentRow) {
        this.selection = currentRow
      },
      refreshData(pagination) {
        let url = '/rest/servers'
        if (this.queryByPage) {
          logger.debug('send POST "%s"', url)
          post(url, pagination, ({data, pagination}) => {
            logger.debug('POST response success, data: %j, page: %j.', data, pagination)
            this.$refs['panePaginateList'].setPagination(pagination)
            this.tableData = data
          })
        } else {
          logger.debug('send GET "%s"', url)
          get(url, data => {
            logger.debug('GET response success, data: %j.', data)
            this.tableData = data
            info('刷新数据成功。')
          })
        }
      }
    },
    mounted() {
      this.refreshData(null)
    }
  }
</script>
