// pages/news/news.js
const app = getApp();
const request = require('../../utils/request');

Page({
  data: {
    newsList: [],
    loading: true,
    page: 1,
    pageSize: 10,
    hasMore: true,
    categories: [
      { id: 0, name: '全部' },
      { id: 1, name: '优惠活动' },
      { id: 2, name: '申卡攻略' },
      { id: 3, name: '用卡技巧' },
      { id: 4, name: '信用知识' }
    ],
    currentCategory: 0
  },

  onLoad() {
    this.loadNews();
  },
  
  onPullDownRefresh() {
    this.setData({
      newsList: [],
      page: 1,
      hasMore: true
    }, () => {
      this.loadNews(() => {
        wx.stopPullDownRefresh();
      });
    });
  },
  
  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadMoreNews();
    }
  },
  
  // 加载资讯
  loadNews(callback) {
    this.setData({ loading: true });
    
    // 从API获取新闻列表
    const params = {
      page: this.data.page - 1, // 后端分页从0开始
      size: this.data.pageSize
    };
    
    if (this.data.currentCategory !== 0) {
      params.categoryId = this.data.currentCategory;
    }
    
    return request.get('/api/client/news', params)
      .then(res => {
        if (res.success && res.data) {
          const newsList = res.data.content || [];
          this.setData({
            newsList,
            loading: false,
            hasMore: !res.data.last // Spring Data JPA的分页信息
          });
        } else {
          this.setData({ loading: false });
          wx.showToast({
            title: '加载数据失败',
            icon: 'none'
          });
        }
        
        if (callback && typeof callback === 'function') {
          callback();
        }
      })
      .catch(err => {
        console.error('加载资讯失败', err);
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
  
  // 加载更多资讯
  loadMoreNews() {
    if (this.data.loading) return;
    
    this.setData({
      page: this.data.page + 1,
      loading: true
    });
    
    // 从API获取更多新闻
    const params = {
      page: this.data.page - 1, // 后端分页从0开始
      size: this.data.pageSize
    };
    
    if (this.data.currentCategory !== 0) {
      params.categoryId = this.data.currentCategory;
    }
    
    return request.get('/client/news', params)
      .then(res => {
        if (res.success && res.data) {
          const moreNews = res.data.content || [];
          this.setData({
            newsList: [...this.data.newsList, ...moreNews],
            loading: false,
            hasMore: !res.data.last
          });
        } else {
          this.setData({
            page: this.data.page - 1,
            loading: false
          });
          wx.showToast({
            title: '加载数据失败',
            icon: 'none'
          });
        }
      })
      .catch(err => {
        console.error('加载更多资讯失败', err);
        this.setData({
          page: this.data.page - 1,
          loading: false
        });
        wx.showToast({
          title: '加载数据失败',
          icon: 'none'
        });
      });
  },
  
  // 切换分类
  onCategoryTap(e) {
    const categoryId = e.currentTarget.dataset.id;
    if (categoryId === this.data.currentCategory) return;
    
    this.setData({
      currentCategory: categoryId,
      newsList: [],
      page: 1,
      hasMore: true
    }, () => {
      this.loadNews();
    });
  },
  
  // 查看资讯详情
  onNewsItemTap(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/news-detail/news-detail?id=${id}`
    });
  },
  
  // 生成模拟资讯数据
  generateMockNewsList() {
    const list = [];
    const categoryId = this.data.currentCategory;
    const startId = (this.data.page - 1) * this.data.pageSize + 1;
    
    for (let i = 0; i < this.data.pageSize; i++) {
      const id = startId + i;
      if (id > 30) break; // 最多30条数据
      
      // 根据分类筛选
      if (categoryId !== 0 && id % 4 !== categoryId % 4) continue;
      
      list.push({
        id,
        title: `资讯标题 ${id}：${this.getRandomTitle(id)}`,
        summary: `这是资讯 ${id} 的摘要内容，描述了相关信用卡的优惠活动或使用技巧。`,
        coverImage: `/images/placeholders/news.svg`,
        publishedAt: this.getRandomDate(),
        categoryId: id % 4 + 1,
        categoryName: this.data.categories[id % 4 + 1].name,
        viewCount: Math.floor(Math.random() * 2000) + 100,
        likeCount: Math.floor(Math.random() * 100) + 10
      });
    }
    
    return list;
  },
  
  // 获取随机标题
  getRandomTitle(id) {
    const titles = [
      '招商银行信用卡新户专享优惠',
      '中信银行信用卡5月优惠活动汇总',
      '平安银行信用卡申请攻略',
      '交通银行信用卡积分兑换指南',
      '建设银行龙卡信用卡权益详解',
      '广发银行信用卡免年费攻略',
      '浦发银行信用卡优惠活动',
      '兴业银行信用卡申请条件',
      '民生银行信用卡用卡技巧',
      '光大银行信用卡积分规则'  
    ];
    
    return titles[id % titles.length];
  },
  
  // 获取随机日期
  getRandomDate() {
    const now = new Date();
    const days = Math.floor(Math.random() * 30);
    const date = new Date(now.getTime() - days * 24 * 60 * 60 * 1000);
    
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    
    return `${year}-${month}-${day} ${hours}:${minutes}`;
  }
});