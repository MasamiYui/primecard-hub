// pages/profile/profile.js
const app = getApp();
const request = require('../../utils/request');

Page({
  data: {
    userInfo: null,
    isLogin: false,
    loading: true,
    functionGroups: [
      {
        title: '我的信用卡',
        functions: [
          { id: 'myCards', name: '我的卡片', icon: 'icon-card', url: '/pages/my-cards/my-cards' },
          { id: 'collection', name: '我的收藏', icon: 'icon-star', url: '/pages/collection/collection' },
          { id: 'compare', name: '卡片对比', icon: 'icon-compare', url: '/pages/card-compare/card-compare' }
        ]
      },
      {
        title: '我的资讯',
        functions: [
          { id: 'favorite', name: '我的收藏', icon: 'icon-favorite', url: '/pages/favorite/favorite' },
          { id: 'history', name: '浏览历史', icon: 'icon-history', url: '/pages/history/history' }
        ]
      },
      {
        title: '设置',
        functions: [
          { id: 'settings', name: '通用设置', icon: 'icon-settings', url: '/pages/settings/settings' },
          { id: 'feedback', name: '意见反馈', icon: 'icon-feedback', url: '/pages/feedback/feedback' },
          { id: 'about', name: '关于我们', icon: 'icon-about', url: '/pages/about/about' }
        ]
      }
    ],
    statistics: {
      cardCount: 0,
      collectionCount: 0,
      favoriteCount: 0,
      historyCount: 0
    }
  },

  onLoad() {
    this.checkLoginStatus();
  },
  
  onShow() {
    // 每次页面显示时检查登录状态
    this.checkLoginStatus();
    // 如果已登录，刷新统计数据
    if (this.data.isLogin) {
      this.loadStatistics();
    }
  },
  
  onPullDownRefresh() {
    this.checkLoginStatus();
    if (this.data.isLogin) {
      this.loadStatistics();
    }
    setTimeout(() => {
      wx.stopPullDownRefresh();
    }, 1000);
  },
  
  // 检查登录状态
  checkLoginStatus() {
    const userInfo = app.globalData.userInfo;
    const isLogin = app.globalData.isLogin;
    
    this.setData({
      userInfo,
      isLogin,
      loading: false
    });
    
    if (isLogin) {
      this.loadStatistics();
    }
  },
  
  // 加载统计数据
  loadStatistics() {
    // 模拟数据，实际项目中应该从API获取
    setTimeout(() => {
      this.setData({
        statistics: {
          cardCount: Math.floor(Math.random() * 5),
          collectionCount: Math.floor(Math.random() * 10) + 5,
          favoriteCount: Math.floor(Math.random() * 15) + 3,
          historyCount: Math.floor(Math.random() * 30) + 10
        }
      });
    }, 500);
    
    // 实际API调用示例
    // return request.get('/user/statistics')
    //   .then(res => {
    //     this.setData({
    //       statistics: res.data
    //     });
    //   })
    //   .catch(err => {
    //     console.error('加载统计数据失败', err);
    //   });
  },
  
  // 登录
  onLoginTap() {
    wx.getUserProfile({
      desc: '用于完善会员资料',
      success: (res) => {
        const userInfo = res.userInfo;
        
        // 模拟登录成功
        app.globalData.userInfo = userInfo;
        app.globalData.isLogin = true;
        
        this.setData({
          userInfo,
          isLogin: true
        });
        
        // 加载统计数据
        this.loadStatistics();
        
        // 实际登录流程示例
        // wx.login({
        //   success: (loginRes) => {
        //     if (loginRes.code) {
        //       // 发送 code 到后台换取 openId, sessionKey, unionId
        //       request.post('/auth/login', {
        //         code: loginRes.code,
        //         userInfo: userInfo
        //       })
        //       .then(res => {
        //         // 保存登录信息
        //         app.globalData.token = res.data.token;
        //         app.globalData.userInfo = res.data.userInfo;
        //         app.globalData.isLogin = true;
        //         
        //         this.setData({
        //           userInfo: res.data.userInfo,
        //           isLogin: true
        //         });
        //         
        //         // 加载统计数据
        //         this.loadStatistics();
        //       })
        //       .catch(err => {
        //         console.error('登录失败', err);
        //         wx.showToast({
        //           title: '登录失败',
        //           icon: 'none'
        //         });
        //       });
        //     }
        //   }
        // });
      },
      fail: (err) => {
        console.error('获取用户信息失败', err);
        wx.showToast({
          title: '获取用户信息失败',
          icon: 'none'
        });
      }
    });
  },
  
  // 退出登录
  onLogoutTap() {
    wx.showModal({
      title: '退出登录',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          // 清除登录信息
          app.globalData.userInfo = null;
          app.globalData.isLogin = false;
          app.globalData.token = '';
          
          this.setData({
            userInfo: null,
            isLogin: false,
            statistics: {
              cardCount: 0,
              collectionCount: 0,
              favoriteCount: 0,
              historyCount: 0
            }
          });
          
          // 实际退出登录流程示例
          // request.post('/auth/logout')
          //   .then(res => {
          //     console.log('退出登录成功', res);
          //   })
          //   .catch(err => {
          //     console.error('退出登录失败', err);
          //   });
        }
      }
    });
  },
  
  // 跳转到功能页面
  onFunctionTap(e) {
    const url = e.currentTarget.dataset.url;
    
    // 检查是否需要登录
    if (!this.data.isLogin) {
      wx.showModal({
        title: '提示',
        content: '该功能需要登录后使用，是否立即登录？',
        success: (res) => {
          if (res.confirm) {
            this.onLoginTap();
          }
        }
      });
      return;
    }
    
    wx.navigateTo({ url });
  },
  
  // 编辑个人资料
  onEditProfileTap() {
    if (!this.data.isLogin) {
      this.onLoginTap();
      return;
    }
    
    wx.navigateTo({
      url: '/pages/edit-profile/edit-profile'
    });
  }
});