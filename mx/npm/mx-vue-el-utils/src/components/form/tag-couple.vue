<style rel="stylesheet/less" lang="less" scoped>
  @import "../../style/base.less";
  @import "../../style/tag.less";

  .tag-row {
    display: inline-block;
  }
</style>

<template>
  <div>
    <el-tag class="tag" v-for="item in tags" :key="item" :closable="!disabled" :close-transition="false" :type="type"
            @close="handleDeleteTag(item)" @dblclick.native="handleEditTag(item)">{{item}}
    </el-tag>
    <div v-if="inputVisible" class="tag-row">
      <el-input class="tag-input" ref="tagInputLeft" v-model="inputLeft" size="mini"
                @keyup.enter.native="handleInputConfirm"></el-input>
      <span>{{separator}}</span>
      <el-input class="tag-input" ref="tagInputRight" v-model="inputRight" size="mini"
                @keyup.enter.native="handleInputConfirm" @blur="handleInputConfirm"></el-input>
    </div>
    <el-button v-else class="tag-row tag-button" size="small" @click="handleShowTagInput" :disabled="disabled">+
    </el-button>
  </div>
</template>

<script>
  import notify from '@/utils/notify'

  export default {
    name: 'tag-couple',
    props: ['value', 'type', 'disabled', 'separator'],
    data () {
      return {
        inputVisible: false,
        inputLeft: '',
        inputRight: '',
        oldValue: ''
      }
    },
    computed: {
      tags () {
        return this.value
      }
    },
    methods: {
      getCouple (tag) {
        if (tag && typeof tag === 'string') {
          let sides = tag.split(this.separator)
          if (sides.length === 2) {
            return sides
          }
        }
        return null
      },
      handleEditTag (tag) {
        this.oldValue = tag
        let couple = this.getCouple(tag)
        if (couple && couple instanceof Array) {
          this.inputLeft = couple[0]
          this.inputRight = couple[1]
        }
        this.handleShowTagInput()
      },
      handleDeleteTag (tag) {
        let tags = this.tags
        tags.splice(tags.indexOf(tag), 1)
        this.$nextTick(() => this.$emit('input', tags))
      },
      handleInputConfirm () {
        let left = this.inputLeft
        let right = this.inputRight
        let tags = this.tags
        if (!tags) {
          tags = []
        }
        if (left && right) {
          let tag = left + this.separator + right
          let tagIndex = this.getTagIndex(tags, left)
          if (tagIndex >= 0) {
            notify.warn('你输入的标签内容[' + tag + ']已经存在，请检查数据。')
            return
          }
          let oldIndex = -1
          let couple = this.getCouple(this.oldValue)
          if (couple && couple instanceof Array) {
            oldIndex = this.getTagIndex(tags, couple[0])
          }
          if (oldIndex >= 0) {
            tags[oldIndex] = tag
          } else {
            tags.push(tag)
          }
        }
        this.$emit('input', tags)
        this.inputLeft = ''
        this.inputRight = ''
        this.inputVisible = false
      },
      handleShowTagInput () {
        this.inputVisible = true
        this.$nextTick(_ => {
          this.$refs['tagInputLeft'].$refs['input'].focus()
        })
      },
      getTagIndex (tags, left) {
        let tagIndex = -1
        if (tags && left) {
          tags.forEach((tag, index) => {
            let couple = this.getCouple(tag)
            if (couple && couple[0] === left) {
              tagIndex = index
            }
          })
        }
        return tagIndex
      }
    }
  }
</script>
