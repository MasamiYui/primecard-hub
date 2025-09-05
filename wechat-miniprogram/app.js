// app.js
App({
  globalData: {
    userInfo: null,
    isLoggedIn: false,
    baseUrl: 'http://localhost:8080/api', // 开发环境API地址，上线前需修改
    version: '1.0.0'
  },
  
  onLaunch() {
    // 检查登录状态
    this.checkLoginStatus();
    
    // 获取系统信息
    const systemInfo = wx.getSystemInfoSync();
    this.globalData.systemInfo = systemInfo;
    
    // 检查更新
    if (wx.canIUse('getUpdateManager')) {
      const updateManager = wx.getUpdateManager();
      updateManager.onCheckForUpdate(function (res) {
        if (res.hasUpdate) {
          updateManager.onUpdateReady(function () {
            wx.showModal({
              title: '更新提示',
              content: '新版本已经准备好，是否重启应用？',
              success: function (res) {
                if (res.confirm) {
                  updateManager.applyUpdate();
                }
              }
            });
          });
          
          updateManager.onUpdateFailed(function () {
            wx.showModal({
              title: '更新提示',
              content: '新版本下载失败，请检查网络后重试',
              showCancel: false
            });
          });
        }
      });
    }
  },
  
  // 检查登录状态
  checkLoginStatus() {
    const token = wx.getStorageSync('token');
    if (token) {
      // 验证token有效性
      wx.request({
        url: `${this.globalData.baseUrl}/auth/verify`,
        method: 'POST',
        header: {
          'Authorization': `Bearer ${token}`
        },
        success: (res) => {
          if (res.statusCode === 200) {
            this.globalData.isLoggedIn = true;
            this.globalData.userInfo = wx.getStorageSync('userInfo');
          } else {
            // token无效，清除登录信息
            this.clearLoginInfo();
          }
        },
        fail: () => {
          // 请求失败，可能是网络问题，保持登录状态
          this.globalData.isLoggedIn = !!wx.getStorageSync('userInfo');
          this.globalData.userInfo = wx.getStorageSync('userInfo');
        }
      });
    }
  },
  
  // 登录方法
  login(userInfo, callback) {
    // 保存用户信息
    this.globalData.userInfo = userInfo;
    this.globalData.isLoggedIn = true;
    wx.setStorageSync('userInfo', userInfo);
    
    if (callback && typeof callback === 'function') {
      callback();
    }
  },
  
  // 退出登录
  logout(callback) {
    this.clearLoginInfo();
    
    if (callback && typeof callback === 'function') {
      callback();
    }
  },
  
  // 清除登录信息
  clearLoginInfo() {
    this.globalData.userInfo = null;
    this.globalData.isLoggedIn = false;
    wx.removeStorageSync('token');
    wx.removeStorageSync('userInfo');
  }
});