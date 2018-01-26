<template>
  <div>
    <h1>Tag normal</h1>
    <br/>
    <mx-tag-normal v-model="normal" type="gray" :disabled="false"></mx-tag-normal>
    <br/>
    <mx-tag-normal v-model="normal" type="gray" :disabled="true"></mx-tag-normal>
    <h1>Tag couple</h1>
    <br/>
    <mx-tag-couple v-model="couple" type="gray" :disabled="false" separator="="></mx-tag-couple>
    <br/>
    <mx-tag-couple v-model="couple" type="gray" :disabled="true" separator="="></mx-tag-couple>
    <br/>
    <mx-tag-couple v-model="couple1" type="gray" :disabled="false" separator=":"></mx-tag-couple>
    <br/>
    <mx-tag-couple v-model="couple2" type="gray" :disabled="false" separator=">"></mx-tag-couple>
  </div>
</template>

<script>
  export default {
    name: 'test-tag-page',
    data () {
      return {
        normal: ['a', 'b', 'c'],
        couple: ['a=A', 'b=B', 'c=C'],
        couple1: ['a:A', 'b:B', 'c:C'],
        couple2: ['a>A', 'b>B', 'c>C']
      }
    }
  }
</script>
