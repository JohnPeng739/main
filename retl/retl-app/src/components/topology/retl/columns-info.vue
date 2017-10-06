<style rel="stylesheet/less" lang="less">
  @import "../../../style/base.less";
</style>

<template>
  <el-row type="flex">
    <el-col :span="2">列定义</el-col>
    <el-col :span="22">
      <ds-tag-both-sides ref="dsTag" v-model="columns" type="gray" :disable="false" sideSeparator="="></ds-tag-both-sides>
    </el-col>
  </el-row>
</template>

<script>
  import {warn} from '../../../assets/notify'
  import DsTagBothSides from '../../ds-tag-both-sides.vue'

  export default {
    name: 'topology-columns-info',
    props: ['topology'],
    components: {DsTagBothSides},
    data () {
      return {
        columns: []
      }
    },
    methods: {
      getColumnsInfo() {
        let columns = this.columns
        if (columns && columns.length > 0) {
          let list = []
          columns.forEach(column => {
            if (column) {
              let sides = column.split('=')
              if (sides && sides.length === 2) {
                list.push({name: sides[0], desc: sides[1]})
              }
            }
          })
          return list
        } else {
          warn(this, '必须为每个字段列进行定义。')
          return null
        }
      },
      setTopology(topology) {
        if (topology && topology.columns && topology.columns.length > 0) {
          let list = []
          topology.columns.forEach(column => {
            if (column) {
              list.push(column.name + '=' + column.desc)
            }
          })
          this.columns = list
        }
      }
    },
    mounted() {
      if (this.topology) {
        this.setTopology(this.topology)
      }
    }
  }
</script>
