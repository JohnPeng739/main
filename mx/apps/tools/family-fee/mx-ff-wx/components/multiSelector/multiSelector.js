// components/multiSelector/multiSelector.js
Component({
  /**
   * 组件的属性列表
   */
  properties: {
    selectorIndex: {
      type: Array,
      value: []
    },
    selectorData: {
      type: Array,
      value: []
    },
    columns: {
      type: Number,
      value: 3
    },
    disabled: {
      type: Boolean,
      value: false
    }
  },

  /**
   * 组件的初始数据
   */
  data: {
    dataArray: [],
    dataIndex: []
  },
  ready: function() {
    this.setData({
      dataIndex: this.properties.selectorIndex
    })
    this.initData(0, 0)
  },
  /**
   * 组件的方法列表
   */
  methods: {
    bindSelectorChange: function(e) {
      let selectorIndex = e.detail.value
      this.setData({
        selectorIndex: selectorIndex
      })
      let list = this.data.selectorData
      let item = null
      for (let index in selectorIndex) {
        if (list && list.length > selectorIndex[index]) {
          item = list[selectorIndex[index]]
          if (item) {
            list = item.children
          }
        }
      }
      this.triggerEvent('change', {value: item})
    },
    bindColumnChange: function(e) {
      let {
        column,
        value
      } = e.detail
      let selectorIndex = this.data.selectorIndex
      selectorIndex[column] = value
      this.initData(column, value)
    },
    initData: function(column, value) {
      let { selectorIndex, selectorData, columns} = this.data
      let list = selectorData
      let item = null
      let result = []
      for (let index = 0; index < columns; index++) {
        let col = []
        if (list && list.length > 0) {
          list.forEach(element => col.push(element.name))
          item = list[selectorIndex[index]]
          if (item) {
            list = item.children
          } else {
            list = null
          }
        }
        result.push(col)
      }
      this.setData({
        dataArray: result
      })
    }
  }
})