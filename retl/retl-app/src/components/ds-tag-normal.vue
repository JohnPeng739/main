<style rel="stylesheet/less" lang="less">
  @import "../style/base.less";
</style>

<template>
  <div>
    <el-tag class="tag" v-for="item in tags" :key="item" :closable="!disabled" :close-transition="false" :type="type"
            @close="handleDeleteTag(item)" @dblclick.native="handelEditTag(item)">{{item}}
    </el-tag>
    <el-input class="tag-input" ref="tagInput" v-if="tagInputVisible" v-model="tagInputValue" size="mini"
              @keyup.enter.native="handleInputConfirm" @blur="handleInputConfirm"></el-input>
    <el-button v-else class="tag-button" size="small" @click="handleShowTagInput" :disabled="disabled">+</el-button>
  </div>
</template>

<script>
  import {warn} from '../assets/notify'

  export default {
    name: 'ds-tag-normal',
    props: ['value', 'type', 'disabled'],
    data() {
      return {
        tagInputVisible: false,
        tagInputValue: '',
        tagEditOld: ''
      }
    },
    computed: {
      tags() {
        return this.value
      }
    },
    methods: {
      handelEditTag(tag) {
        this.tagEditOld = tag
        this.tagInputValue = tag
        this.handleShowTagInput()
      },
      handleDeleteTag(tag) {
        this.tags.splice(this.tags.indexOf(tag), 1)
        this.$emit('input', this.tags)
      },
      handleInputConfirm() {
        let tag = this.tagInputValue
        let tags = this.tags
        if (tags === null || tags === undefined) {
          tags = []
        }
        if (tag) {
          let oldIndex = -1
          if (tags.indexOf(tag) >= 0) {
            warn(this, '你输入的标签内容[' + tag + ']已经存在，请检查数据。')
            return
          } else {
            let old = this.tagEditOld
            if (old && tags.indexOf(old) >= 0) {
              oldIndex = tags.indexOf(old)
            }
          }
          if (oldIndex >= 0) {
            tags[oldIndex] = tag
          } else {
            tags.push(tag)
          }
        }
        this.$emit('input', tags)
        this.tagInputValue = ''
        this.tagEditOld = ''
        this.tagInputVisible = false
      },
      handleShowTagInput() {
        this.tagInputVisible = true
        this.$nextTick(_ => {
          this.$refs['tagInput'].$refs['input'].focus()
        })
      }
    }
  }
</script>
