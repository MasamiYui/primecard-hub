// components/loading/loading.js
Component({
  properties: {
    text: {
      type: String,
      value: '加载中...'
    },
    size: {
      type: String,
      value: 'default' // 可选值：small, default, large
    },
    fullScreen: {
      type: Boolean,
      value: false
    }
  }
});