<style scoped>

  .about {
    margin: 10px 20px 0 30px;
    color: black;
  }

</style>

<template>

  <div class="about">
    <vue-markdown># 知识产权管理系统</vue-markdown>
    <vue-markdown>----</vue-markdown>
    <vue-markdown>基于Vue2 + Element UI + Java + MongoDB</vue-markdown>
    <vue-markdown>### 如无特殊情况，本项目会持续维护</vue-markdown>
  </div>

</template>

<script>

  import VueMarkdown from 'vue-markdown'
  export default {
    components: {
      VueMarkdown
    }
  }

</script>
