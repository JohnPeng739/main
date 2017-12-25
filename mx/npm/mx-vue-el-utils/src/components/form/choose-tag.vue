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
          <div class="pg-layout-buttons">
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
  import notify from '@/utils/notify'

  export default {
    name: 'choose-tag',
    props: ['value', 'displayField', 'disabled', 'type', 'popoverWidth'],
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
        if (item && item[this.displayField]) {
          return item[this.displayField]
        } else {
          return ''
        }
      },
      addTag (tag) {
        if (tag && tag.id && tag[this.displayField]) {
          if (this.tags.indexOf(tag) >= 0) {
            notify.warn(this.$t('message.tag.existed', {tag: tag[this.displayField]}))
          } else {
            this.tags.push(tag)
            this.visible = false
          }
        }
      },
      handleDeleteTag (tag) {
        this.tags.splice(this.tags.indexOf(tag), 1)
        this.$emit('input', this.tags)
      },
      handleOk () {
        this.$emit('selected')
      },
      handleCancel () {
        this.visible = false
      }
    }
  }
</script>
