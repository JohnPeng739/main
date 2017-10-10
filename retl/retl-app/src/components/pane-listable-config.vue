<style rel="stylesheet/less" lang="less" scoped>
  @import "../style/base.less";

  .layout-list {
    border: 1px solid @button-color;
    .list-row {
      word-wrap: break-word;
    }
  }
</style>

<template>
  <div class="layout-list">
    <el-row>
      <el-col :span="24">
        <span class="layout-buttons">
          <el-button v-if="!formVisible" class="button" :plain="true" type="text"
                     @click="handleListOperate('add')">
            <ds-icon class="button-icon" name="add"></ds-icon>
            添加
          </el-button>
          <el-button v-if="!formVisible" class="button" :plain="true" type="text"
                     @click="handleListOperate('edit')">
            <ds-icon class="button-icon" name="edit"></ds-icon>
            修改
          </el-button>
          <el-button v-if="!formVisible" class="button" :plain="true" type="text"
                     @click="handleListOperate('delete')">
            <ds-icon class="button-icon" name="delete"></ds-icon>
            删除
          </el-button>
            <el-button v-if="formVisible" class="button" :plain="true" type="text"
                       @click="handleConfigForm('reset')">
            <ds-icon class="button-icon" name="autorenew"></ds-icon>
            重置
          </el-button>
          <el-button v-if="formVisible" class="button" :plain="true" type="text"
                     @click="handleConfigForm('save')">
            <ds-icon class="button-icon" name="save"></ds-icon>
            保存
          </el-button>
          <el-button v-if="formVisible" class="button" :plain="true" type="text"
                     @click="handleConfigForm('close')">
            <ds-icon class="button-icon" name="close"></ds-icon>
            关闭
          </el-button>
        </span>
      </el-col>
    </el-row>
    <el-row v-if="formVisible">
      <el-col :span="24">
        <slot name="form-config"></slot>
      </el-col>
    </el-row>
    <el-row v-for="item in list" :key="item.dataSource" type="flex" justify="center"
            align="middle">
      <el-col :span="1">
        <el-checkbox ref="listCheckbox" :key="item.dataSource" class="list-row"></el-checkbox>
      </el-col>
      <el-col :span="22">
        <span class="list-row">{{JSON.stringify(item)}}</span>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import DsIcon from './icon.vue'
  import {info} from '../assets/notify'

  export default {
    name: 'pane-dataSource-config',
    components: {DsIcon},
    props: ['list'],
    data() {
      return {
        listData: this.list,
        formVisible: false
      }
    },
    methods: {
      setList(list) {
        this.listData = list
      },
      getSelected() {
        let checkbox = this.$refs['listCheckbox']
        let checked = []
        if (checkbox) {
          checkbox.forEach((check, index) => {
            if (check.isChecked) {
              checked.push(index)
            }
          })
        }
        return checked
      },
      handleListOperate(operate) {
        let checked = this.getSelected()
        if (operate === 'edit' || operate === 'delete') {
          if (checked.length <= 0) {
            info( '操作之前请先选择数据源。')
            return
          }
          if (operate === 'delete') {
            // 删除选中的数据源
            this.$emit('delete', checked)
            return
          }
          if (checked.length !== 1 && operate === 'edit') {
            info( '修改操作之前只能选择一条数据源。')
            return
          }
        }
        let checkedIndex = -1
        if (checked && checked.length > 0) {
          checkedIndex = checked[0]
        }
        this.formVisible = true
        // 修改，需要发送消息到父组件，由父组件具体调用操作
        // operate为'edit'，checkedIndex为需要操作的数据源序号
        if (operate === 'edit') {
          this.$emit(operate, checkedIndex)
        }
      },
      handleConfigForm(operate) {
        switch (operate) {
          case 'reset':
          case 'save':
            // 对form的具体操作，需要发送消息到父组件，由父组件具体调用操作
            this.$emit(operate)
            break;
          case 'close':
            this.formVisible = false
            break;
        }
      }
    }
  }
</script>
