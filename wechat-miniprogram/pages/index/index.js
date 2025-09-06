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
    // 从后端API获取轮播图数据
    return request.get('/client/banners')
      .then(res => {
        console.log('轮播图API响应:', res);
        if (res.success && Array.isArray(res.data)) {
          // 如果数据为空数组，使用默认数据
          if (res.data.length === 0) {
            console.log('轮播图数据为空，使用默认数据');
            this.setData({
              banners: [
                {
                  id: 1,
                  imageUrl: '/images/placeholders/banner.svg',
                  linkUrl: '/pages/news-detail/news-detail?id=1',
                  title: '默认轮播图1'
                },
                {
                  id: 2,
                  imageUrl: '/images/placeholders/banner.svg',
                  linkUrl: '/pages/card-detail/card-detail?id=1',
                  title: '默认轮播图2'
                }
              ]
            });
            return res;
          }
          
          // 处理轮播图数据，转换为小程序需要的格式
          const bannerList = res.data.map(banner => {
            let linkUrl = '';
            
            // 根据不同的链接类型，生成不同的跳转链接
            switch (banner.linkType) {
              case 'CARD':
                linkUrl = `/pages/card-detail/card-detail?id=${banner.linkId}`;
                break;
              case 'NEWS':
                linkUrl = `/pages/news-detail/news-detail?id=${banner.linkId}`;
                break;
              case 'EXTERNAL':
                // 外部链接需要通过web-view组件打开
                linkUrl = `/pages/webview/webview?url=${encodeURIComponent(banner.linkUrl)}`;
                break;
              case 'MINIPROGRAM':
                // 小程序内部不处理跳转到其他小程序的情况
                linkUrl = '';
                break;
              default:
                linkUrl = '';
            }
            
            // 记录轮播图浏览
            this.recordBannerView(banner.id);
            
            return {
              id: banner.id,
              imageUrl: banner.imageUrl,
              linkUrl: linkUrl,
              linkType: banner.linkType,
              title: banner.title
            };
          });
          
          console.log('处理后的轮播图数据:', bannerList);
          this.setData({ banners: bannerList });
        } else {
          // 如果API请求失败或数据格式不正确，使用默认数据
          console.error('获取轮播图失败:', res.message || '未知错误');
          this.setData({
            banners: [
              {
                id: 1,
                imageUrl: '/images/placeholders/banner.svg',
                linkUrl: '/pages/news-detail/news-detail?id=1',
                title: '默认轮播图'
              }
            ]
          });
        }
        return res;
      })
      .catch(err => {
        console.error('获取轮播图异常:', err);
        // 出错时使用默认数据
        this.setData({
          banners: [
            {
              id: 1,
              imageUrl: '/images/placeholders/banner.svg',
              linkUrl: '/pages/news-detail/news-detail?id=1',
              title: '默认轮播图'
            }
          ]
        });
      });
  },
  
  // 加载资讯
  loadNews() {
    // 从API获取推荐新闻
    return request.get('/client/news/recommended')
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
    const { url, index } = e.currentTarget.dataset;
    const banner = this.data.banners[index];
    if (banner) {
      // 记录轮播图点击
      this.recordBannerClick(banner.id);
      
      if (banner.linkType === 'MINIPROGRAM' && banner.linkAppid) {
        // 跳转到其他小程序
        wx.navigateToMiniProgram({
          appId: banner.linkAppid,
          path: banner.linkPage || '',
          success: () => {
            console.log('跳转到其他小程序成功');
          },
          fail: (err) => {
            console.error('跳转到其他小程序失败', err);
          }
        });
      } else if (url) {
        // 普通页面跳转
        wx.navigateTo({
          url
        });
      }
    }
  },
  
  // 记录轮播图点击
  recordBannerClick(bannerId) {
    // 如果没有bannerId或者是默认数据（ID为1、2、3），则不发送请求
    if (!bannerId || [1, 2, 3].includes(Number(bannerId))) {
      console.log('跳过记录轮播图点击，使用的是默认数据或ID无效');
      return;
    }
    
    request.post(`/client/banners/${bannerId}/click`)
      .then(res => {
        console.log('记录轮播图点击成功', res);
      })
      .catch(err => {
        console.error('记录轮播图点击失败', err);
      });
  },
  
  // 记录轮播图浏览
  recordBannerView(bannerId) {
    // 如果没有bannerId或者是默认数据（ID为1、2、3），则不发送请求
    if (!bannerId || [1, 2, 3].includes(Number(bannerId))) {
      console.log('跳过记录轮播图浏览，使用的是默认数据或ID无效');
      return;
    }
    
    request.post(`/client/banners/${bannerId}/view`)
      .then(res => {
        console.log('记录轮播图浏览成功', res);
      })
      .catch(err => {
        console.error('记录轮播图浏览失败', err);
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