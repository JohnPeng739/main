<style rel="stylesheet/less" lang="less" scoped>
  @import "../../style/base.less";

  .layout-pane {
    width: 100%;
    padding: 10px 10px 0 10px;
  }
</style>

<template>
  <div>
    <el-form class="layout-form" label-width="120px">
      <div v-for="(item, index) in servers" :key="item.machineIp" class="layout-pane">
        <el-row type="flex">
          <el-col :span="24">
            <h4>服务器 {{index + 1}}</h4>
          </el-col>
        </el-row>
        <el-row type="flex">
          <el-col :span="12">
            <el-form-item label="机器名">
              <el-input v-model="item.machineName" :readonly="true"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="机器IP">
              <el-input v-model="item.machineIp" :readonly="true"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex">
          <el-col :span="24">
            <el-form-item label="运行服务">
              <ds-tag-normal v-model="item.services" type="gray" :disabled="true"></ds-tag-normal>
            </el-form-item>
          </el-col>
        </el-row>
      </div>
    </el-form>
  </div>
</template>

<script>
  import {logger} from 'dsutils'
  import {get} from '../../assets/ajax'
  import {info} from '../../assets/notify'
  import DsTagNormal from '../../components/ds-tag-normal.vue'

  export default {
    name: 'page-servers',
    components: {DsTagNormal},
    data() {
      return {
        servers: []
      }
    },
    methods: {
      setData(data) {
        let servers = []
        data.forEach(server => {
          let {machineName, machineIp, storm} = server
          let services = []
          if (storm && storm.zookeepers && storm.zookeepers.length > 0) {
            storm.zookeepers.forEach(zookeeper => {
              if (zookeeper === machineName) {
                services.push('ZOOKEEPER服务')
                return
              }
            })
          }
          if (storm && storm.services && storm.services.length > 0) {
            storm.services.forEach(service => {
              switch (service) {
                case 'nimbus':
                  services.push('STORM主控服务')
                  break;
                case 'ui':
                  services.push('STORM UI服务')
                  break;
                case 'supervisor':
                  services.push('STORM计算服务')
                  break;
                case 'logviewer':
                  services.push('STORM日志服务')
                  break;
              }
            })
          }
          servers.push({machineName, machineIp, services})
        })
        this.servers = servers
      }
    },
    mounted() {
      let url = '/rest/servers'
      logger.debug('send GET "%s"', url)
      get(url, data => {
        if (data) {
          console.log(data)
          this.setData(data)
        }
      })
    }
  }
</script>
