// pages/index/index.js
const app = getApp();
const request = require('../../utils/request');

Page({
  data: {
    banners: [],
    newsList: [],
    cardList: [],
    loading: true,
    userInfo: null,
    isLoggedIn: false
  },

  onLoad() {
    // 获取用户信息
    this.setData({
      userInfo: app.globalData.userInfo,
      isLoggedIn: app.globalData.isLoggedIn
    });
    
    // 加载首页数据
    this.loadHomeData();
  },
  
  onShow() {
    // 每次显示页面时检查登录状态
    if (this.data.isLoggedIn !== app.globalData.isLoggedIn) {
      this.setData({
        userInfo: app.globalData.userInfo,
        isLoggedIn: app.globalData.isLoggedIn
      });
    }
  },
  
  onPullDownRefresh() {
    // 下拉刷新
    this.loadHomeData(() => {
      wx.stopPullDownRefresh();
    });
  },
  
  // 加载首页数据
  loadHomeData(callback) {
    this.setData({ loading: true });
    
    // 并行请求数据
    Promise.all([
      this.loadBanners(),
      this.loadNews(),
      this.loadCards()
    ])
      .then(() => {
        this.setData({ loading: false });
        if (callback && typeof callback === 'function') {
          callback();
        }
      })
      .catch(err => {
        console.error('加载首页数据失败', err);
        this.setData({ loading: false });
        wx.showToast({
          title: '加载数据失败',
          icon: 'none'
        });
        if (callback && typeof callback === 'function') {
          callback();
        }
      });
  },
  
  // 加载轮播图
  loadBanners() {
    // 模拟数据，实际项目中应该从API获取
    return new Promise((resolve) => {
      setTimeout(() => {
        this.setData({
          banners: [
            {
              id: 1,
              imageUrl: '/images/placeholders/banner.svg',
              linkUrl: '/pages/news-detail/news-detail?id=1'
            },
            {
              id: 2,
              imageUrl: '/images/placeholders/banner.svg',
              linkUrl: '/pages/card-detail/card-detail?id=1'
            },
            {
              id: 3,
              imageUrl: '/images/placeholders/banner.svg',
              linkUrl: '/pages/news/news'
            }
          ]
        });
        resolve();
      }, 500);
    });
    
    // 实际API调用示例
    // return request.get('/banners')
    //   .then(res => {
    //     this.setData({ banners: res.data });
    //   });
  },
  
  // 加载资讯
  loadNews() {
    // 从API获取推荐新闻
    return request.get('/api/client/news/recommended')
      .then(res => {
        if (res.success && res.data) {
          // 只取前3条新闻
          const newsList = res.data.slice(0, 3);
          this.setData({ newsList });
        }
      })
      .catch(err => {
        console.error('加载推荐新闻失败', err);
        // 如果API失败，使用空数组
        this.setData({ newsList: [] });
      });
  },
  
  // 加载信用卡
  loadCards() {
    // 模拟数据，实际项目中应该从API获取
    return new Promise((resolve) => {
      setTimeout(() => {
        this.setData({
          cardList: [
            {
              id: 1,
              bankName: '招商银行',
              cardName: '经典白金卡',
              cardType: '白金卡',
              annualFee: '300元',
              bankLogo: '/images/placeholders/bank-logo.svg',
              cardImage: '/images/placeholders/card.svg',
              features: ['免年费', '高额度', '机场贵宾厅']
            },
            {
              id: 2,
              bankName: '中信银行',
              cardName: '颜卡',
              cardType: '金卡',
              annualFee: '0元',
              bankLogo: '/images/placeholders/bank-logo.svg',
              cardImage: '/images/placeholders/card.svg',
              features: ['免年费', '颜值高', '返现高']
            },
            {
              id: 3,
              bankName: '平安银行',
              cardName: '白金信用卡',
              cardType: '白金卡',
              annualFee: '200元',
              bankLogo: '/images/placeholders/bank-logo.svg',
              cardImage: '/images/placeholders/card.svg',
              features: ['首年免年费', '积分永久有效', '专属礼遇']
            }
          ]
        });
        resolve();
      }, 700);
    });
    
    // 实际API调用示例
    // return request.get('/cards', { limit: 3 })
    //   .then(res => {
    //     this.setData({ cardList: res.data });
    //   });
  },
  
  // 点击轮播图
  onBannerTap(e) {
    const { url } = e.currentTarget.dataset;
    wx.navigateTo({
      url
    });
  },
  
  // 查看更多资讯
  onMoreNewsTap() {
    wx.switchTab({
      url: '/pages/news/news'
    });
  },
  
  // 查看资讯详情
  onNewsItemTap(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/news-detail/news-detail?id=${id}`
    });
  },
  
  // 查看更多信用卡
  onMoreCardsTap() {
    wx.switchTab({
      url: '/pages/cards/cards'
    });
  },
  
  // 查看信用卡详情
  onCardItemTap(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/card-detail/card-detail?id=${id}`
    });
  }
});