<style rel="stylesheet/less" lang="less">
  @import "../../../style/base.less";
</style>

<template>
  <el-row type="flex">
    <el-col :span="2">列定义</el-col>
    <el-col :span="22">
      <ds-tag-both-sides ref="dsTag" v-model="columnDefines" type="gray" :disable="false" sideSeparator=":"></ds-tag-both-sides>
    </el-col>
  </el-row>
</template>

<script>
  import {mapGetters, mapActions} from 'vuex'
  import {logger} from 'mx-app-utils'
  import {warn} from '../../../assets/notify'
  import DsTagBothSides from '../../ds-tag-both-sides.vue'

  export default {
    name: 'topology-columns-info',
    components: {DsTagBothSides},
    data () {
      return {
        columnDefines: []
      }
    },
    computed: {
      ...mapGetters(['columns'])
    },
    methods: {
      ...mapActions(['setColumns']),
      validated() {
        let columnDefines = this.columnDefines
        if (columnDefines && columnDefines.length > 0) {
          return true
        } else {
          warn('没有定义数据列。')
        }
      },
      cacheData() {
        let columns = []
        let columnDefines = this.columnDefines
        if (columnDefines && columnDefines.length > 0) {
          columnDefines.forEach(column => {
            if (column) {
              let sides = column.split(':')
              if (sides && sides.length === 2) {
                columns.push({name: sides[0], desc: sides[1]})
              }
            }
          })
        }
        this.setColumns(columns)
      }
    },
    mounted() {
      let columns = this.columns
      if (columns && columns.length > 0) {
        let columnDefines = []
        columns.forEach(column => {
          if (column) {
            columnDefines.push(column.name + ':' + column.desc)
          }
        })
        this.columnDefines = columnDefines
      }
    }
  }
</script>
