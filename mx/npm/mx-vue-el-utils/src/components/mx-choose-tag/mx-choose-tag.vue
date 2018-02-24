<template>
  <div>
    <el-tag class="tag" v-for="item in tags" :key="item.id" :closable="!disabled" :close-transition="false"
            :type="type" @close="handleDeleteTag(item)">{{getDisplayName(item)}}
    </el-tag>
    <el-popover ref="popover" v-model="visible" placement="bottom" :width="popoverWidth" trigger="click">
      <el-row type="flex">
        <el-col :span="24">
          <slot></slot>
        </el-col>
      </el-row>
      <el-row type="flex">
        <el-col :span="24">
          <div class="tag-popover">
            <el-button class="button" @click="handleCancel">{{$t('common.cancel')}}</el-button>
            <el-button class="button" @click="handleOk">{{$t('common.ok')}}</el-button>
          </div>
        </el-col>
      </el-row>
    </el-popover>
    <el-button class="tag-button" size="small" v-popover:popover :disabled="disabled">+</el-button>
  </div>
</template>

<script>
  import { formatter } from 'mx-app-utils'
  import MxNotify from '../../utils/mx-notify'

  export default {
    name: 'mx-choose-tag',
    props: {
      value: {},
      displayFormat: String,
      disabled: {type: Boolean, default: false},
      type: String,
      popoverWidth: {type: Number, default: 400},
      keyField: {
        type: String,
        default: 'code'
      }
    },
    data () {
      return {
        visible: false
      }
    },
    computed: {
      tags: {
        get () {
          return this.value
        },
        set (val) {
          this.$emit('input', val)
        }
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
        this.$emit('change', this.tags)
      },
      handleOk () {
        this.$emit('selected', (selected) => {
          if (selected !== undefined && selected !== null) {
            let tags = selected && selected instanceof Array ? selected : [selected]
            if (tags.length > 0) {
              for (let index in tags) {
                let tag = tags[index]
                if (!tag[this.keyField]) {
                  MxNotify.error(this.$t('message.tag.fieldNotExist', [this.keyField]))
                  return
                }
                for (let innerIndex in this.tags) {
                  if (this.tags[innerIndex][this.keyField] === tag[this.keyField]) {
                    MxNotify.warn(this.$t('message.tag.existed', [this.getDisplayName(tag)]))
                    return
                  }
                }
              }
              this.tags = this.tags.concat(tags)
              this.visible = false
              this.$emit('change', this.tags)
              return
            }
          }
          MxNotify.info(this.$t('message.choose'))
        })
      },
      handleCancel () {
        this.visible = false
      }
    }
  }
</script>
