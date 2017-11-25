<style rel="stylesheet/less" lang="less" scoped>
  @import "../../style/base.less";

  .title {
    font-weight: 800;
    padding-left: 10px;
  }
  .row-status {
    margin-top: 15px;
  }
</style>

<template>
  <div>
    <pane-paginate-list ref="panePaginateList" v-on:buttonHandle="handleButtonClick">
      <el-table :max-height="570" :data="tableData" class="layout-table" highlight-current-row
                @current-change="handleCurrentChange">
        <el-table-column type="expand">
          <template slot-scope="scope">
            <el-row type="flex" v-if="scope.row.zookeeper" class="row-status">
              <el-col :span="24">
                <span class="title">服务状态：</span>
                <el-button :disabled="isEnabled(scope.row.status, 'zookeeper')" @click="handleService('zookeeper', 'enable', scope.row.machineIp)" type="primary" size="small">启用</el-button>
                <el-button :disabled="!isEnabled(scope.row.status, 'zookeeper')" @click="handleService('zookeeper', 'disable', scope.row.machineIp)" type="primary" size="small">禁用</el-button>
                <el-button :disabled="isActive(scope.row.status, 'zookeeper')" @click="handleService('zookeeper', 'start', scope.row.machineIp)" type="primary" size="small">启动</el-button>
                <el-button :disabled="!isActive(scope.row.status, 'zookeeper')" @click="handleService('zookeeper', 'stop', scope.row.machineIp)" type="primary" size="small">停止</el-button>
                <el-button :disabled="!isActive(scope.row.status, 'zookeeper')" @click="handleService('zookeeper', 'reload', scope.row.machineIp)" type="primary" size="small">重载</el-button>
                <el-button :disabled="!isActive(scope.row.status, 'zookeeper')" @click="handleService('zookeeper', 'restart', scope.row.machineIp)" type="primary" size="small">重启</el-button>
              </el-col>
            </el-row>
            <el-row type="flex" v-if="scope.row.storm" class="row-status">
              <el-col :span="24">
                <span class="title">服务状态：</span>
                <el-button :disabled="isEnabled(scope.row.status, 'storm')" @click="handleService('storm', 'enable', scope.row.machineIp)" type="primary" size="small">启用</el-button>
                <el-button :disabled="!isEnabled(scope.row.status, 'storm')" @click="handleService('storm', 'disable', scope.row.machineIp)" type="primary" size="small">禁用</el-button>
                <el-button :disabled="isActive(scope.row.status, 'storm')" @click="handleService('storm', 'start', scope.row.machineIp)" type="primary" size="small">启动</el-button>
                <el-button :disabled="!isActive(scope.row.status, 'storm')" @click="handleService('storm', 'stop', scope.row.machineIp)" type="primary" size="small">停止</el-button>
                <el-button :disabled="!isActive(scope.row.status, 'storm')" @click="handleService('storm', 'reload', scope.row.machineIp)" type="primary" size="small">重载</el-button>
                <el-button :disabled="!isActive(scope.row.status, 'storm')" @click="handleService('storm', 'restart', scope.row.machineIp)" type="primary" size="small">重启</el-button>
              </el-col>
            </el-row>
          </template>
        </el-table-column>
        <el-table-column prop="machineName" label="服务器" width="100"></el-table-column>
        <el-table-column prop="machineIp" label="IP地址" width="200"></el-table-column>
        <el-table-column prop="zookeeper" label="ZOOKEEPER服务" width="200">
          <template slot-scope="scope">
            <span v-if="scope.row.zookeeper">
              <span class="title">{{scope.row.zookeeper.cluster ? '加入集群' : '单机运行'}}</span>
              <span class="title">序号：</span>{{scope.row.zookeeper.serverNo}}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="storm" label="STORM服务">
          <template slot-scope="scope">
            <span v-if="scope.row.storm">
              <span class="title">插槽数量：</span>{{scope.row.storm.slots}}个
              <span class="title">起始端口：</span>{{scope.row.storm.startPort}}<br/>
              <span class="title">运行服务：</span>{{getServices(scope.row.storm.services)}}
            </span>
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
  import {Loading} from 'element-ui'
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
        queryByPage: true,
        dialogDestroyed: false
      }
    },
    methods: {
      isActive(status, service) {
        if (status) {
          if (status[service] && status[service].active) {
            return status[service].active
          }
        }
        return false
      },
      isEnabled(status, service) {
        if (status) {
          if (status[service] && status[service].enabled) {
            return status[service].enabled
          }
        }
        return false
      },
      getServiceName(service) {
        switch (service) {
          case 'nimbus':
            return '主控'
          case 'ui':
            return 'UI'
          case 'supervisor':
            return '计算'
          case 'logviewer':
            return '日志'
          default:
            return 'NA'
        }
      },
      getServices(services) {
        if (services && services.length > 0) {
          let names = []
          services.forEach(service => names.push(this.getServiceName(service)))
          return names.join(", ")
        } else {
          return ''
        }
      },
      handleService(service, cmd, machineIp) {
        logger.debug('%s the service: %s.', cmd, service)
        let url = '/rest/server/service?cmd=' + cmd + '&service=' + service + '&machineIp=' + machineIp
        logger.debug('send GET "%s"', url)
        get(url, data => {
          if (data) {
            info(cmd + '服务[' + service + ']成功。')
            setTimeout(_ => this.refreshData(null), 2000)
          }
        })
      },
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
          if (!pagination) {
            pagination = {total: 0, size: 20, page: 1}
          }
          logger.debug('send POST "%s", page: %j.', url, pagination)
          post(url, pagination, ({data, pagination}) => {
            logger.debug('POST response success, data: %j, page: %j.', data, pagination)
            this.$refs['panePaginateList'].setPagination(pagination)
            this.tableData = data
            info('刷新数据成功。')
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
