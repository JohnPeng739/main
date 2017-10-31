<style rel="stylesheet/less" lang="less" scoped>
  @import "../style/base.less";

  .tag-row {
    display: inline-block;
  }
</style>

<template>
  <div>
    <el-tag class="tag" v-for="item in tags" :key="item" :closable="!disabled" :close-transition="false" :type="type"
            @close="handleDeleteTag(item)" @dblclick.native="handleEditTag(item)">{{item}}
    </el-tag>
    <div v-if="tagInputVisible" class="tag-row">
      <el-input class="tag-input" ref="tagInputLeft" v-model="tagInputLeft" size="mini"></el-input>
      <span>{{sideSeparator}}</span>
      <el-input class="tag-input" ref="tagInputRight" v-model="tagInputRight" size="mini"
                @keyup.enter.native="handleInputConfirm" @blur="handleInputConfirm"></el-input>
    </div>
    <el-button v-else class="tag-button tag-row" size="small" @click="handleShowTagInput" :disabled="disabled">+</el-button>
  </div>
</template>

<script>
  import {warn} from '../assets/notify'

  export default {
    name: 'ds-tag-normal',
    props: ['value', 'type', 'disabled', 'sideSeparator'],
    data() {
      return {
        tagInputVisible: false,
        tagInputLeft: '',
        tagInputRight: '',
        tagEditOld: ''
      }
    },
    computed: {
      tags() {
        return this.value
      }
    },
    methods: {
      getSides(tag) {
        if (tag) {
          let sides = tag.split(this.sideSeparator)
          if (sides.length === 2) {
            return sides
          }
        }
        return null
      },
      handleEditTag(tag) {
        this.tagEditOld = tag
        let sides = this.getSides(tag)
        if (sides) {
          this.tagInputLeft = sides[0]
          this.tagInputRight = sides[1]
        }
        this.handleShowTagInput()
      },
      handleDeleteTag(tag) {
        let tags = this.tags
        tags.splice(tags.indexOf(tag), 1)
        this.$nextTick(_ => {this.$emit('input', tags)})
      },
      handleInputConfirm() {
        let left = this.tagInputLeft
        let right = this.tagInputRight
        let tags = this.tags
        if (tags === null || tags === undefined) {
          tags = []
        }
        if (left && right) {
          let tag = left + this.sideSeparator + right
          let tagIndex = this.getTagIndex(tags, left)
          if (tagIndex >= 0) {
            warn( '你输入的标签内容[' + tag + ']已经存在，请检查数据。')
            return
          }
          let oldIndex = -1
          let sides = this.getSides(this.tagEditOld)
          if (sides) {
            oldIndex = this.getTagIndex(tags, sides[0])
          }
          if (oldIndex >= 0) {
            tags[oldIndex] = tag
          } else {
            tags.push(tag)
          }
        }
        this.$emit('input', tags)
        this.tagInputLeft = ''
        this.tagInputRight = ''
        this.tagInputVisible = false
      },
      handleShowTagInput() {
        this.tagInputVisible = true
        this.$nextTick(_ => {
          this.$refs['tagInputLeft'].$refs['input'].focus()
        })
      },
      getTagIndex(tags, left) {
        let tagIndex = -1
        if (tags && left) {
          tags.forEach((tag, index) => {
            let sides = this.getSides(tag)
            if (sides && sides[0] === left) {
              tagIndex = index
              return
            }
          })
        }
        return tagIndex
      }
    }
  }
</script>
