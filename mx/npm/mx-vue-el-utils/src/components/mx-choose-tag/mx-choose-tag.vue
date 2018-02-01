<template>
  <div>
    <el-tag class="tag" v-for="item in tags" :key="item.id" :closable="!disabled" :close-transition="false"
            :type="type" @close="handleDeleteTag(item)">{{getDisplayName(item)}}</el-tag>
    <el-popover ref="popover" v-model="visible" placement="bottom" :width="popoverWidth" trigger="click">
      <el-row type="flex">
        <el-col :span="24">
          <slot></slot>
        </el-col>
      </el-row>
      <el-row type="flex">
        <el-col :span="24">
          <div class="tag-popover">
            <el-button class="button" @click="handleCancel">{{$t('button.cancel')}}</el-button>
            <el-button class="button" @click="handleOk">{{$t('button.ok')}}</el-button>
          </div>
        </el-col>
      </el-row>
    </el-popover>
    <el-button class="tag-button" size="small" v-popover:popover :disabled="disabled">+</el-button>
  </div>
</template>

<script>
  import {formatter} from 'mx-app-utils'
  import MxNotify from '../../utils/mx-notify'

  export default {
    name: 'mx-choose-tag',
    props: {
      value: {},
      displayFormat: String,
      disabled: {type: Boolean, default: false},
      type: String,
      popoverWidth: {type: Number, default: 400}
    },
    data () {
      return {
        visible: false
      }
    },
    computed: {
      tags () {
        return this.value
      }
    },
    methods: {
      getDisplayName (item) {
        if (item !== null && item !== undefined) {
          return formatter.formatObj(this.displayFormat, item)
        } else {
          return ''
        }
      },
      handleDeleteTag (tag) {
        this.tags.splice(this.tags.indexOf(tag), 1)
        this.$emit('input', this.tags)
      },
      handleOk () {
        this.$emit('selected', (selected) => {
          let tag = selected
          if (tag !== null && tag !== undefined) {
            if (this.tags.indexOf(tag) >= 0) {
              MxNotify.warn(this.$t('message.tag.existed', {tag: this.getDisplayName(tag)}))
            } else {
              this.tags.push(tag)
              this.visible = false
            }
          } else {
            MxNotify.info(this.$t('message.choose'))
          }
        })
      },
      handleCancel () {
        this.visible = false
      }
    }
  }
</script>
