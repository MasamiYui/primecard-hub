// pages/webview/webview.js
Page({
  data: {
    url: '',
    loading: true,
    error: false
  },
  
  onLoad(options) {
    if (options.url) {
      try {
        // 解码URL
        const decodedUrl = decodeURIComponent(options.url);
        this.setData({
          url: decodedUrl,
          loading: false
        });
      } catch (error) {
        console.error('URL解码失败', error);
        this.setData({
          error: true,
          loading: false
        });
      }
    } else {
      this.setData({
        error: true,
        loading: false
      });
    }
  },
  
  // 网页加载成功
  onWebviewLoad() {
    this.setData({
      loading: false
    });
  },
  
  // 网页加载失败
  onWebviewError(e) {
    console.error('Webview加载失败', e.detail);
    this.setData({
      error: true,
      loading: false
    });
  },
  
  // 返回上一页
  goBack() {
    wx.navigateBack();
  }
});