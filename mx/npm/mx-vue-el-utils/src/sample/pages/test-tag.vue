<template>
  <div>
    <h1>Tag normal</h1>
    <br/>
    <tag-normal v-model="normal" type="gray" :disabled="false"></tag-normal>
    <br/>
    <tag-normal v-model="normal" type="gray" :disabled="true"></tag-normal>
    <h1>Tag couple</h1>
    <br/>
    <tag-couple v-model="couple" type="gray" :disabled="false" separator="="></tag-couple>
    <br/>
    <tag-couple v-model="couple" type="gray" :disabled="true" separator="="></tag-couple>
    <br/>
    <tag-couple v-model="couple1" type="gray" :disabled="false" separator=":"></tag-couple>
    <br/>
    <tag-couple v-model="couple2" type="gray" :disabled="false" separator=">"></tag-couple>
  </div>
</template>

<script>
  import {TagNormal, TagCouple} from '../../../dist/mx-vue-el-utils.min'
  // import TagNormal from '@/components/form/tag-normal'
  // import TagCouple from '@/components/form/tag-couple'

  export default {
    name: 'test-tag-page',
    components: {TagNormal, TagCouple},
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
