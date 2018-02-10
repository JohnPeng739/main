<style rel="stylesheet/less" lang="less" scoped>
  @import "../style/base.less";

  .content {
    width: 60%;
    margin: 20px auto;
  }
</style>

<template>
  <div>
    <h1>测试 可选择输入框</h1>
    <br/>
    <div class="content">
      <mx-choose-input ref="input1" v-model="chooseInput" v-on:selected="handleSelectedInput"
                       displayFormat="{code} - {name}"
                       placeholder="请选择..."
                       :popover-width="400" :readonly="true">
        <el-table :data="tableData" highlight-current-row @current-change="handleCurrentChange1">
          <el-table-column prop="code" label="Code"></el-table-column>
          <el-table-column prop="name" label="Name"></el-table-column>
        </el-table>
      </mx-choose-input>
      <p>{{JSON.stringify(chooseInput)}}</p>
    </div>
    <br/><br/>
    <div class="content">
      <mx-choose-tag ref="tag1" v-model="chooseTag" displayFormat="{code} - {name}" @selected="handleSelectedTag1"
                     @change="handleTagChange" :disabled="false"
                     type="gray" :popover-width="400">
        <el-table :data="tableData" highlight-current-row @current-change="handleCurrentChange2">
          <el-table-column prop="code" label="Code"></el-table-column>
          <el-table-column prop="name" label="Name"></el-table-column>
        </el-table>
      </mx-choose-tag>
      <p>{{JSON.stringify(chooseTag)}}</p>
    </div>
    <br/><br/>
    <div class="content">
      <mx-choose-tag ref="tag1" v-model="chooseTag" displayFormat="{code} - {name}" @selected="handleSelectedTag2"
                     @change="handleTagChange" :disabled="false"
                     type="gray" :popover-width="400">
        <el-table :data="tableData" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" show-overflow-tooltip></el-table-column>
          <el-table-column prop="code" label="Code"></el-table-column>
          <el-table-column prop="name" label="Name"></el-table-column>
        </el-table>
      </mx-choose-tag>
      <br/>
      <p>{{JSON.stringify(chooseTag)}}</p>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'page-test-choose-input',
    data () {
      return {
        chooseInput: {id: 'id', code: 'code', name: 'name'},
        chooseTag: [{id: 'id', code: 'code', name: 'name'}],
        tableData: [],
        selected1: null,
        selected2: null,
        selected3: []
      }
    },
    methods: {
      handleSelectedInput (done) {
        done(this.selected1)
      },
      handleSelectedTag1 (done) {
        done(this.selected2)
      },
      handleSelectedTag2 (done) {
        done(this.selected3)
      },
      handleCurrentChange1 (current) {
        this.selected1 = current
      },
      handleCurrentChange2 (current) {
        this.selected2 = current
      },
      handleSelectionChange (val) {
        this.selected3 = val
      },
      handleTagChange (tags) {
        console.log(tags)
      }
    },
    mounted () {
      let data = []
      for (let i = 1; i < 10; i++) {
        data.push({id: i, code: 'code ' + i, name: 'name ' + i})
      }
      this.tableData = data
    }
  }
</script>
