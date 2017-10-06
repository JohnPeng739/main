<style rel="stylesheet/less" lang="less">
  @import "../../style/base.less";

  .config {
    padding: 5px 20px;
    width: 90%;
  }

  .check-result {
    margin: 6px 0 0 18px;
    border: 1px solid @button-color;
    width: 90%;
    height: 490px;
  }

  .check-style {
    display: inline;
    padding-left: 30px;
    font-size: @content-text-font-size + 4px;
    font-weight: 900;
    .checking {
      color: @nav-color;
    }
    .error {
      color: red;
    }
    .pass {
      color: blue;
    }
  }
</style>

<template>
  <div>
    <el-row>
      <el-col :span="12">配置详细内容</el-col>
      <el-col :span="12">
        资源校验结果
        <div class="check-style">
          <span v-if="state === 'checking'" class="checking">检验中...</span>
          <span v-else-if="state === 'error'" class="error">检验出错</span>
          <span v-else class="pass">通过检验</span>
        </div>
      </el-col>
    </el-row>
    <el-row type="flex">
      <el-col :span="12">
        <el-input class="config" type="textarea" v-model="result" :rows="23" :readonly="true"></el-input>
      </el-col>
      <el-col :span="12">
        <div class="check-result" v-html="compileMarkdown"></div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import marked from 'marked'

  export default {
    name: 'topology-result-info',
    props: ['topology'],
    data() {
      return {
        result: '',
        checkResult: '## 尚未检验',
        state: 'checking'
      }
    },
    computed: {
      compileMarkdown() {
        return marked(this.checkResult, {sanitize: true})
      }
    },
    methods: {
      checkConfig(topology) {
        // TODO 通过后台校验相关资源是否有效
        // 校验数据库连接、消息中间件连接、zookeeper连接等
      },
      setTopology(topology) {
        if (topology) {
          this.result = JSON.stringify(topology, null, '  ')
          setTimeout(_ => {
            this.checkConfig(topology)
          }, 100)
        }
      }
    },
    mounted() {
      this.setTopology(this.topology)
    }
  }
</script>
