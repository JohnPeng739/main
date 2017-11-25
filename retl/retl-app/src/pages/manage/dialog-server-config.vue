<style rel="stylesheet/less" lang="less" scoped>
  @import "../../style/base.less";
  @import "../../style/dialog.less";

  .title {
    font-weight: 800;
  }
</style>

<template>
  <el-dialog :visible.sync="visible" :title="title" @close="handleClose" :modal-append-to-body="false"
             :close-on-click-modal="false">
    <el-form ref="formServer" :model="formServer" :rules="rulesServer" label-width="100px" class="dialog-form">
      <el-row type="flex">
        <el-col :span="12">
          <el-form-item label="机器名" prop="machineName">
            <el-input v-model="formServer.machineName" :readonly="mode === 'detail'"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="IP地址" prop="machineIp">
            <el-input v-model="formServer.machineIp" :readonly="mode === 'detail'"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row type="flex">
        <el-col :span="16">
          <span class="title">ZOOKEEPER服务器</span>
        </el-col>
        <el-col :span="8">
          <el-form-item label="是否配置">
            <el-switch v-model="isZookeeper" :disabled="mode === 'detail'"></el-switch>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row type="flex" v-if="isZookeeper">
        <el-col :span="12">
          <el-form-item label="集群" prop="zkServerNo">
            <el-switch v-model="formServer.zkCluster" :disabled="mode === 'detail'"></el-switch>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="ZK机器序号" prop="zkServerNo">
            <el-input-number v-model="formServer.zkServerNo" :min="1" :max="5" size="small"
                             :disabled="mode === 'detail'"></el-input-number>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row type="flex" v-if="isZookeeper">
        <el-col :span="24">
          <el-form-item label="数据目录" prop="zkDataDir">
            <el-input v-model="formServer.zkDataDir" :readonly="mode === 'detail'"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row type="flex" v-if="isZookeeper">
        <el-col :span="24">
          <el-form-item label="ZK集群列表" prop="zkServers">
            <ds-tag-normal v-model="formServer.zkServers" type="gray" :disabled="mode === 'detail'"
                           class="tag-row"></ds-tag-normal>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row type="flex">
        <el-col :span="16">
          <span class="title">STORM服务器</span>
        </el-col>
        <el-col :span="8">
          <el-form-item label="是否配置">
            <el-switch v-model="isStorm" :disabled="mode === 'detail'"></el-switch>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row type="flex" v-if="isStorm">
        <el-col :span="24">
          <el-form-item label="运行服务" prop="stormServices">
            <el-select v-model="formServer.stormServices" multiple style="width: 90%" :disabled="mode === 'detail'">
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
          <el-form-item label="ZK服务器" prop="stormZkServers">
            <ds-tag-normal v-model="formServer.stormZkServers" type="gray" :disabled="mode === 'detail'"
                           class="tag-row"></ds-tag-normal>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row type="flex" v-if="isStorm">
        <el-col :span="24">
          <el-form-item label="主控列表" prop="stormNimbuses">
            <ds-tag-normal v-model="formServer.stormNimbuses" type="gray" :disabled="mode === 'detail'"
                           class="tag-row"></ds-tag-normal>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row type="flex" v-if="isStorm">
        <el-col :span="12">
          <el-form-item label="插槽数量" prop="stormSlots">
            <el-input-number v-model="formServer.stormSlots" :min="10" :max="100" :step="5" size="small"
                             :disabled="mode === 'detail'"></el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="起始端口" prop="stormStartPort">
            <el-input-number v-model="formServer.stormStartPort" :controls="false"
                             :disabled="mode === 'detail'"></el-input-number>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row type="flex" v-if="isStorm">
        <el-col :span="24">
          <el-form-item label="数据目录" prop="stormDataDir">
            <el-input v-model="formServer.stormDataDir" :readonly="mode === 'detail'"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button class="button" @click="handleReset" :disabled="mode === 'detail'">重置</el-button>
      <el-button class="button" @click="handleSubmit" :disabled="mode === 'detail'">保存</el-button>
      <el-button class="button" @click="handleClose">关闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import {formValidateWarn} from '../../assets/notify'
  import DsTagNormal from '../../components/ds-tag-normal.vue'

  export default {
    name: 'dialog-server-config',
    components: {DsTagNormal},
    data() {
      return {
        visible: false,
        isZookeeper: false,
        isStorm: false,
        formServer: {
          machineName: '',
          machineIp: '',
          zkCluster: false,
          zkServerNo: 1,
          zkDataDir: '',
          zkServers: [],
          stormServices: [],
          stormZkServers: [],
          stormNimbuses: [],
          stormDataDir: '',
          stormSlots: 10,
          stormStartPort: 6700
        },
        rulesServer: {},
        mode: 'detail'
      }
    },
    computed: {
      title() {
        switch (this.mode) {
          case 'add':
            return '添加服务器'
          case 'edit':
            return '修改服务器'
          default:
            return '服务器详情'
        }
      }
    },
    methods: {
      show(mode, server) {
        this.mode = mode
        let {machineName, machineIp, zookeeper, storm} = server
        let formServer = {machineName, machineIp}
        if (zookeeper) {
          let {cluster, serverNo, dataDir, servers} = zookeeper
          formServer = {zkCluster: cluster, zkServerNo: serverNo, zkDataDir: dataDir, zkServers: servers, ...formServer}
        } else {
          formServer = {zkCluster: false, zkServerNo: 1, zkDataDir: '/opt/zookeeper/data', zkServers: ['storm1:2888:3888'], ...formServer}
        }
        this.isZookeeper = zookeeper && mode !== 'add'
        if (storm) {
          let {services, zookeepers, nimbuses, dataDir, slots, startPort} = storm
          formServer = {
            stormServices: services, stormZkServers: zookeepers, stormNimbuses: nimbuses, stormDataDir: dataDir,
            stormSlots: slots, stormStartPort: startPort, ...formServer
          }
        } else {
          formServer = {
            stormServices: ['nimbus', 'ui', 'supervisor', 'logviewer'], stormZkServers: ['storm1'],
            stormNimbuses: ['storm1'], stormDataDir: '/opt/storm/data', stormSlots: 10, stormStartPort: 6700, ...formServer
          }
        }
        this.isStorm = storm && mode !== 'add'
        this.formServer = formServer
        this.visible = true
      },
      handleSubmit() {
        this.$refs['formServer'].validate(valid => {
          if (valid) {
            let {
              machineName, machineIp, zkCluster, zkServerNo, zkDataDir, zkServers, stormServices, stormZkServers,
              stormNimbuses, stormSlots, stormStartPort, stormDataDir
            } = this.formServer
            let server = {machineName, machineIp}
            if (this.isZookeeper) {
              let zookeeper = {cluster: zkCluster, serverNo: zkServerNo, dataDir: zkDataDir, servers: zkServers}
              server = {zookeeper, ...server}
            }
            if (this.isStorm) {
              let storm = {
                services: stormServices, zookeepers: stormZkServers, nimbuses: stormNimbuses,
                dataDir: stormDataDir, slots: stormSlots, startPort: stormStartPort
              }
              server = {storm, ...server}
            }
            this.$emit('submit', this.mode, server)
            this.handleClose()
          } else {
            formValidateWarn()
          }
        })
      },
      handleReset() {
        this.$refs['formServer'].resetFields()
      },
      handleClose() {
        this.visible = false
        this.$emit('close')
      }
    }
  }
</script>
