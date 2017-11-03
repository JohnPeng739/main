<style rel="stylesheet/less" lang="less" scoped>
  @import "../../style/base.less";

  .button {
    width: 100px;
  }
</style>

<template>
  <div>
    <el-form class="layout-form" label-width="120px">
      <el-row type="flex">
        <el-col :span="20">
          <h4>本机配置</h4>
        </el-col>
        <el-col :span="4" class="layout-buttons">
          <el-button v-if="!canEdit" type="text" @click="handleEditable('local')">配置本机地址</el-button>
          <el-button v-if="localEditable" type="text" @click="handleCancel">取消</el-button>
          <el-button v-if="localEditable" type="text" @click="handleSave('local')">保存本机地址</el-button>
        </el-col>
      </el-row>
      <el-row type="flex">
        <el-col :span="12">
          <el-form-item label="本机名称">
            <el-input v-model="machineName" :readonly="true"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="本机IP">
            <el-input v-model="machineIp" :readonly="!localEditable"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row type="flex" style="padding-top: 50px;">
        <el-col :span="20">
          <h4>Zookeeper配置</h4>
          <el-form-item label="是否配置">
            <el-switch v-model="isZookeeper" :disabled="!zookeeperEditable"></el-switch>
          </el-form-item>
        </el-col>
        <el-col :span="4" class="layout-buttons">
          <el-button v-if="!canEdit" type="text" @click="handleEditable('zookeeper')">调整ZK配置</el-button>
          <el-button v-if="zookeeperEditable" type="text" @click="handleCancel">取消</el-button>
          <el-button v-if="zookeeperEditable" type="text" @click="handleSave('zookeeper')">保存ZK配置</el-button>
        </el-col>
      </el-row>
      <el-row type="flex" v-if="isZookeeper">
        <el-col :span="6">
          <el-form-item label="是否加入集群">
            <el-switch v-model="zookeeper.cluster" :disabled="!zookeeperEditable"></el-switch>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="本机序号">
            <el-input-number v-model="zookeeper.serverNo" :min="1" :max="10" size="small"
                             :disabled="!zookeeper.cluster || !zookeeperEditable"></el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="数据目录">
            <el-input v-model="zookeeper.dataDir" :readonly="!zookeeperEditable"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row type="flex" v-if="isZookeeper && zookeeper.cluster">
        <el-col :span="24">
          <el-form-item label="集群列表">
            <ds-tag-normal v-model="zookeeper.servers" type="gray" :disabled="!zookeeperEditable"
                           class="tag-row"></ds-tag-normal>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row type="flex" v-if="isZookeeper">
        <el-col :span="24" style="text-align: center;">
          <el-button class="button" type="primary" @click="systemctl('enable', 'zookeeper')" :disabled="zookeeperRunning">启用</el-button>
          <el-button class="button" type="primary" @click="systemctl('disable', 'zookeeper')" :disabled="zookeeperRunning">禁用</el-button>
          <el-button class="button" type="primary" @click="systemctl('start', 'zookeeper')" :disabled="zookeeperRunning">启动</el-button>
          <el-button class="button" type="primary" @click="systemctl('stop', 'zookeeper')" :disabled="!zookeeperRunning">停止</el-button>
          <el-button class="button" type="primary" @click="systemctl('restart', 'zookeeper')" :disabled="!zookeeperRunning">重新启动</el-button>
          <el-button class="button" type="primary" @click="systemctl('reload', 'zookeeper')">重新加载</el-button>
        </el-col>
      </el-row>
      <el-row type="flex" style="padding-top: 50px;">
        <el-col :span="20">
          <h4>Storm配置</h4>
          <el-form-item label="是否配置">
            <el-switch v-model="isStorm" :disabled="!stormEditable"></el-switch>
          </el-form-item>
        </el-col>
        <el-col :span="4" class="layout-buttons">
          <el-button v-if="!canEdit" type="text" @click="handleEditable('storm')">调整Storm配置</el-button>
          <el-button v-if="stormEditable" type="text" @click="handleCancel">取消</el-button>
          <el-button v-if="stormEditable" type="text" @click="handleSave('storm')">保存Storm配置</el-button>
        </el-col>
      </el-row>
      <el-row type="flex" v-if="isStorm">
        <el-col :span="24">
          <el-form-item label="运行服务">
            <el-select v-model="storm.services" style="width: 80%" multiple :disabled="!stormEditable">
              <el-option value="nimbus" label="主控服务"></el-option>
              <el-option value="ui" label="UI服务"></el-option>
              <el-option value="supervisor" label="计算服务"></el-option>
              <el-option value="logviewer" label="日志服务"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row type="flex" v-if="isStorm">
        <el-col :span="24">
          <el-form-item label="ZK服务器列表">
            <ds-tag-normal v-model="storm.zookeepers" type="gray" :disabled="!stormEditable"
                           class="tag-row"></ds-tag-normal>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row type="flex" v-if="isStorm">
        <el-col :span="24">
          <el-form-item label="主控节点列表">
            <ds-tag-normal v-model="storm.nimbuses" type="gray" :disabled="!stormEditable"
                           class="tag-row"></ds-tag-normal>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row type="flex" v-if="isStorm">
        <el-col :span="6">
          <el-form-item label="插槽数量">
            <el-input-number v-model="storm.slots" :min="5" :max="50" size="small"
                             :disabled="!stormEditable"></el-input-number>
            个
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="开始端口">
            <el-input-number v-model="storm.startPort" :min="1025" size="small" :controls="false"
                             :disabled="!stormEditable"></el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="数据目录">
            <el-input v-model="storm.dataDir" :readonly="!stormEditable"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row type="flex" v-if="isStorm">
        <el-col :span="24" style="text-align: center;">
          <el-button class="button" type="primary" @click="systemctl('enable', 'storm')" :disabled="stormRunning">启用</el-button>
          <el-button class="button" type="primary" @click="systemctl('disable', 'storm')" :disabled="stormRunning">禁用</el-button>
          <el-button class="button" type="primary" @click="systemctl('start', 'storm')" :disabled="stormRunning">启动</el-button>
          <el-button class="button" type="primary" @click="systemctl('stop', 'storm')" :disabled="!stormRunning">停止</el-button>
          <el-button class="button" type="primary" @click="systemctl('restart', 'storm')" :disabled="!stormRunning">重新启动</el-button>
          <el-button class="button" type="primary" @click="systemctl('reload', 'storm')">重新加载</el-button>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script>
  import {logger} from 'dsutils'
  import {get, post} from '../../assets/ajax'
  import {info} from '../../assets/notify'
  import DsTagNormal from '../../components/ds-tag-normal.vue'
  import ElButton from "../../../node_modules/element-ui/packages/button/src/button.vue";

  export default {
    name: 'page-local-server-config',
    components: {
      ElButton,
      DsTagNormal},
    data() {
      return {
        zookeeperRunning: false,
        stormRunning: false,
        localEditable: false,
        zookeeperEditable: false,
        stormEditable: false,
        machineName: 'NA',
        machineIp: 'NA',
        isZookeeper: false,
        zookeeper: {cluster: false, serverNo: '1', dataDir: '/opt/zookeeper/data', servers: []},
        isStorm: false,
        storm: {services: [], zookeepers: [], nimbuses: [], dataDir: '/opt/storm/data', slots: 10, startPort: 6700}
      }
    },
    computed: {
      canEdit() {
        return this.localEditable || this.zookeeperEditable || this.stormEditable
      }
    },
    methods: {
      handleEditable(type) {
        switch (type) {
          case 'local':
            this.localEditable = true
            break;
          case 'zookeeper':
            this.zookeeperEditable = true
            break;
          case 'storm':
            this.stormEditable = true
            break;
        }
      },
      handleSave(type) {
        let {machineName, machineIp, zookeeper, storm} = this
        let url = '/rest/server'
        logger.debug('send POST "%s"', url)
        post(url, {machineName, machineIp, zookeeper, storm}, data => {
          if (data) {
            info('保存配置信息成功')
            this.setServerInfo(data)
          }
        })
      },
      handleCancel() {
        this.localEditable = false
        this.zookeeperEditable = false
        this.stormEditable = false
      },
      refresh() {
        let url = '/rest/server'
        logger.debug('send GET "%s"', url)
        get(url, data => {
          if (data) {
            this.setServerInfo(data)
          }
        })
      },
      systemctl(operate, serviceName) {
        let url = '/rest/server/systemctl?cmd=' + operate + '&service=' + serviceName
        logger.debug('send GET "%s"', url)
        get(url, data => {
          if (data) {
            this.$nextTick(_ => this.refresh())
          }
        })
      },
      setServerInfo(data) {
        this.machineName = data.machineName
        this.machineIp = data.machineIp
        this.zookeeper = data.zookeeper
        this.isZookeeper = (data.zookeeper !== undefined && data.zookeeper !== null)
        this.storm = data.storm
        this.isStorm = (data.storm !== undefined && data.storm !== null)
        this.localEditable = false
        this.zookeeperEditable = false
        this.stormEditable = false
      }
    },
    mounted() {
      this.refresh()
    }
  }
</script>
