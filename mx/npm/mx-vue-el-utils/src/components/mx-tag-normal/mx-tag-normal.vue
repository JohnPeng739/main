<template>
  <div class="tag">
    <el-tag class="tag" v-for="item in tags" :key="item" :closable="!disabled" :close-transition="false" :type="type"
            @close="handleDeleteTag(item)" @dblclick.native="handelEditTag(item)">{{item}}
    </el-tag>
    <el-input class="tag-input" ref="tagInput" v-if="inputVisible" v-model="inputValue" size="mini"
              @keyup.enter.native="handleInputConfirm" @blur="handleInputConfirm"></el-input>
    <el-button v-else class="tag-button" size="small" @click="handleShowTagInput" :disabled="disabled">+</el-button>
  </div>
</template>

<script>
  import MxNotify from '../../utils/mx-notify'

  export default {
    name: 'mx-tag-normal',
    props: {
      value: {},
      type: String,
      disabled: {type: Boolean, default: false}
    },
    data () {
      return {
        inputVisible: false,
        inputValue: '',
        oldValue: ''
      }
    },
    computed: {
      tags () {
        return this.value
      }
    },
    methods: {
      handelEditTag (tag) {
        this.oldValue = tag
        this.inputValue = tag
        this.handleShowTagInput()
      },
      handleDeleteTag (tag) {
        this.tags.splice(this.tags.indexOf(tag), 1)
        this.$emit('input', this.tags)
      },
      handleInputConfirm () {
        let tag = this.inputValue
        let tags = this.tags
        if (!tags) {
          tags = []
        }
        if (tag) {
          let oldIndex = -1
          if (tags.indexOf(tag) >= 0) {
            MxNotify.warn(this.$t('message.tag.existed', {tag}))
            return
          } else {
            let old = this.oldValue
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
        this.inputValue = ''
        this.oldValue = ''
        this.inputVisible = false
      },
      handleShowTagInput () {
        this.inputVisible = true
        this.$nextTick(_ => {
          this.$refs['tagInput'].$refs['input'].focus()
        })
      }
    }
  }
</script>
