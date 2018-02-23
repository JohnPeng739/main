<template>
  <el-cascader :options="options" :separator="separator" :placeholder="placeholder" :disabled="disabled"
               :clearable="clearable" :expand-trigger="expandTrigger" :show-all-levels="showAllLevels"
               :filterable="filterable" :change-on-select="!leafOnly" :size="size" style="width: 100%;"
               popper-class="layout-dict-select" v-model="selection" @change="handleChange"></el-cascader>
</template>

<script>
  import {clone, formatter} from 'mx-app-utils'

  export default {
    name: 'mx-dict-select',
    props: {
      value: '',
      dictData: {
        type: Array,
        default: function () {
          return []
        }
      },
      separator: {
        type: String,
        default: ' > '
      },
      placeholder: {
        type: String
      },
      disabled: {
        type: Boolean,
        default: false
      },
      clearable: {
        type: Boolean,
        default: true
      },
      expandTrigger: {
        type: String,
        default: 'hover'
      },
      showAllLevels: {
        type: Boolean,
        default: true
      },
      filterable: {
        type: Boolean,
        default: true
      },
      leafOnly: {
        type: Boolean,
        default: true
      },
      size: {
        type: String
      },
      displayFormat: {
        type: String,
        default: '{code}-{name}'
      }
    },
    data () {
      return {
        options: [],
        dictList: []
      }
    },
    computed: {
      selection: {
        get () {
          return this.getSelectList()
        },
        set (val) {}
      }
    },
    methods: {
      transformData (parent, src, tar) {
        if (src && src instanceof Array && src.length > 0) {
          for (let index = 0; index < src.length; index++) {
            let item = src[index]
            let selection = []
            if (parent && parent.selection && parent.selection.length > 0) {
              selection = selection.concat(parent.selection)
            }
            selection.push(item.id)
            item.selection = selection
            this.dictList.push(item)
            let children = []
            this.transformData(item, item.children, children)
            if (children.length <= 0) {
              children = undefined
            }
            let label = formatter.formatObj(this.displayFormat, item)
            tar.push({value: item.id, label, children})
          }
        }
      },
      getSelectList () {
        let dict = this.value
        if (dict && dict.id) {
          let dictList = this.dictList
          for (let index = 0; index < dictList.length; index++) {
            if (dict.id === dictList[index].id) {
              return dictList[index].selection
            }
          }
        } else {
          return []
        }
      },
      handleChange (val) {
        if (val) {
          let id = val.pop()
          let dictList = this.dictList
          for (let index = 0; index < dictList.length; index++) {
            if (id === dictList[index].id) {
              let item = clone(dictList[index])
              delete item.selection
              this.$emit('input', item)
            }
          }
        }
      }
    },
    mounted () {
      let options = []
      let dictData = this.dictData
      this.transformData(undefined, dictData, options)
      this.options = options
    }
  }
</script>
