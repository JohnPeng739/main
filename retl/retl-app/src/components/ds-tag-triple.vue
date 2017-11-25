<style rel="stylesheet/less" lang="less" scoped>
  @import "../style/base.less";

  .tag-row {
    display: inline-block;
    .title {
      font-weight: 900;
      .content {
        font-weight: 400;
        padding: 0 3px 0 0;
      }
    }
    .button-icon {
      font-size: 5px;
    }
  }
</style>

<template>
  <div>
    <el-tag class="tag tag-row" v-for="item in tags" :key="item.one" :closable="!disabled" :close-transition="false" :type="type"
            @close="handleDeleteTag(item)" @dblclick.native="handleEditTag(item)">
      <span v-if="showOne" class="title">{{oneTitle}}: <span class="content">{{item.one}}</span></span>
      <span v-if="showTwo" class="title">{{twoTitle}}: <span class="content">{{item.two}}</span></span>
      <span v-if="showThree" class="title">{{threeTitle}}: <span class="content">{{item.three}}</span></span>
    </el-tag>
    <div v-if="tagInputVisible" class="tag-row">
      <span v-if="showOne">{{oneTitle}}<el-input class="tag-input" ref="tagInputOne" v-model="tagInputOne" size="mini"></el-input></span>
      <span v-if="showTwo">{{twoTitle}}<el-input class="tag-input" ref="tagInputTwo" v-model="tagInputTwo" size="mini"></el-input></span>
      <span v-if="showThree">{{threeTitle}}<el-input class="tag-input" ref="tagInputThree" v-model="tagInputThree" size="mini"></el-input></span>
      <el-button class="tag-button tag-row" size="small" @click.native="tagInputVisible = false">
        <ds-icon class="button-icon" name="clear"></ds-icon>
      </el-button>
      <el-button class="tag-button tag-row" size="small" @click.native="handleInputConfirm">
        <ds-icon class="button-icon" name="done"></ds-icon>
      </el-button>
    </div>
    <el-button v-else class="tag-button tag-row" size="small" @click.native="handleShowTagInput" :disabled="disabled">+</el-button>
  </div>
</template>

<script>
  import {warn} from '../assets/notify'
  import DsIcon from './icon.vue'

  export default {
    name: 'ds-tag-normal',
    components: {DsIcon},
    props: {
      value: {
        required: true,
        default: {one: '', two: '', three: ''}
      },
      type: {
        default: 'gray'
      },
      disabled: {
        default: false
      },
      showOne: {
        default: true
      },
      showTwo: {
        default: true
      },
      showThree: {
        default:false
      },
      oneTitle: {
        default: 'one'
      },
      twoTitle: {
        default: 'two'
      },
      threeTitle: {
        default: 'three'
      }
    },
    data() {
      return {
        tagInputVisible: false,
        tagInputOne: '',
        tagInputTwo: '',
        tagInputThree: ''
      }
    },
    computed: {
      tags() {
        return this.value
      }
    },
    methods: {
      handleEditTag(tag) {
        if (tag) {
          this.tagInputOne = tag.one
          this.tagInputTwo = tag.two
          this.tagInputThree = tag.three
        }
        this.handleShowTagInput()
      },
      handleDeleteTag(tag) {
        let tags = this.tags
        let index = this.getTagIndex(tags, tag.one)
        if (index >= 0) {
          tags.splice(index, 1)
          this.$nextTick(_ => {
            this.$emit('input', tags)
          })
        }
      },
      handleInputConfirm() {
        let one = this.tagInputOne
        let two = this.tagInputTwo
        let three = this.tagInputThree
        let tags = this.tags
        if (tags === null || tags === undefined) {
          tags = []
        }
        if (this.showOne && !one) {
          warn('请输入[' + this.oneTitle + ']数据。')
          return
        }
        if (this.showTwo && !two) {
          warn('请输入[' + this.twoTitle + ']数据。')
          return
        }
        if (this.showThree && !three) {
          warn('请输入[' + this.threeTitle + ']数据。')
          return
        }

        let tag = {one, two, three}
        let tagIndex = this.getTagIndex(tags, one)
        if (tagIndex >= 0) {
          // 修改
          tags[tagIndex] = tag
        } else {
          // 新加
          tags.push(tag)
        }
        this.$emit('input', tags)
        this.tagInputOne = ''
        this.tagInputTwo = ''
        this.tagInputThree = ''
        this.tagInputVisible = false
      },
      handleShowTagInput() {
        this.tagInputVisible = true
        this.$nextTick(_ => {
          if (this.showOne) {
            this.$refs['tagInputOne'].$refs['input'].focus()
          } else if (this.showTwo) {
            this.$refs['tagInputTwo'].$refs['input'].focus()
          } else if (this.showThree) {
            this.$refs['tagInputThree'].$refs['input'].focus()
          }
        })
      },
      getTagIndex(tags, one) {
        let tagIndex = -1
        if (tags && one) {
          tags.forEach((tag, index) => {
            if (tag && tag.one === one) {
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
