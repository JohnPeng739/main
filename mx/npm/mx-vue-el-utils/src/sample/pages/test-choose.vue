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
      <choose-input ref="input1" v-model="chooseInput" v-on:selected="handleSelectedInput" displayFormat="{code} - {name}"
                    placeholder="请选择..."
                    :popover-width="400" :readonly="true">
        <el-table :data="tableData" highlight-current-row @current-change="handleCurrentChange">
          <el-table-column prop="code" label="Code"></el-table-column>
          <el-table-column prop="name" label="Name"></el-table-column>
        </el-table>
      </choose-input>
      <p>{{JSON.stringify(chooseInput)}}</p>
    </div>
    <br/><br/>
    <choose-tag ref="tag1" v-model="chooseTag" displayFormat="{code} - {name}" v-on:selected="handleSelectedTag" :disabled="false"
                type="gray" :popover-width="400">
      <el-table :data="tableData" highlight-current-row @current-change="handleCurrentChange">
        <el-table-column prop="code" label="Code"></el-table-column>
        <el-table-column prop="name" label="Name"></el-table-column>
      </el-table>
    </choose-tag>
    <br/>
    <p>{{JSON.stringify(chooseTag)}}</p>
  </div>
</template>

<script>
  import ChooseInput from '@/components/form/choose-input.vue'
  import ChooseTag from '@/components/form/choose-tag.vue'

  export default {
    name: 'page-test-choose-input',
    components: {ChooseInput, ChooseTag},
    data () {
      return {
        chooseInput: {id: 'id', code: 'code', name: 'name'},
        chooseTag: [{id: 'id', code: 'code', name: 'name'}],
        tableData: [],
        selected: null
      }
    },
    methods: {
      handleSelectedInput (done) {
        done(this.selected)
      },
      handleSelectedTag (done) {
        done(this.selected)
      },
      handleCurrentChange (currentRow, oldCurrentRow) {
        this.selected = currentRow
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
