// components/empty-state/empty-state.js
Component({
  properties: {
    text: {
      type: String,
      value: '暂无数据'
    },
    icon: {
      type: String,
      value: 'icon-empty'
    },
    showButton: {
      type: Boolean,
      value: false
    },
    buttonText: {
      type: String,
      value: '刷新'
    }
  },

  methods: {
    onButtonTap() {
      this.triggerEvent('buttonTap');
    }
  }
});