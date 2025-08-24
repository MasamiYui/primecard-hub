// pages/cards/cards.js
const app = getApp();
const request = require('../../utils/request');

Page({
  data: {
    cardList: [],
    loading: true,
    page: 1,
    pageSize: 10,
    hasMore: true,
    categories: [
      { id: 0, name: '全部' },
      { id: 1, name: '白金卡' },
      { id: 2, name: '金卡' },
      { id: 3, name: '普卡' },
      { id: 4, name: '联名卡' }
    ],
    currentCategory: 0,
    banks: [
      { id: 0, name: '全部银行' },
      { id: 1, name: '招商银行' },
      { id: 2, name: '中信银行' },
      { id: 3, name: '建设银行' },
      { id: 4, name: '工商银行' },
      { id: 5, name: '交通银行' },
      { id: 6, name: '平安银行' },
      { id: 7, name: '浦发银行' },
      { id: 8, name: '广发银行' }
    ],
    currentBank: 0,
    showFilter: false,
    sortOptions: [
      { id: 'default', name: '默认排序' },
      { id: 'hot', name: '热门优先' },
      { id: 'new', name: '最新优先' }
    ],
    currentSort: 'default'
  },

  onLoad() {
    this.loadCards();
  },
  
  onPullDownRefresh() {
    this.setData({
      cardList: [],
      page: 1,
      hasMore: true
    }, () => {
      this.loadCards(() => {
        wx.stopPullDownRefresh();
      });
    });
  },
  
  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadMoreCards();
    }
  },
  
  // 加载信用卡列表
  loadCards(callback) {
    this.setData({ loading: true });
    
    // 模拟数据，实际项目中应该从API获取
    setTimeout(() => {
      const cardList = this.generateMockCardList();
      
      this.setData({
        cardList,
        loading: false,
        hasMore: cardList.length >= this.data.pageSize
      });
      
      if (callback && typeof callback === 'function') {
        callback();
      }
    }, 1000);
    
    // 实际API调用示例
    // const params = {
    //   page: this.data.page,
    //   pageSize: this.data.pageSize,
    //   sort: this.data.currentSort
    // };
    // 
    // if (this.data.currentCategory !== 0) {
    //   params.categoryId = this.data.currentCategory;
    // }
    // 
    // if (this.data.currentBank !== 0) {
    //   params.bankId = this.data.currentBank;
    // }
    // 
    // return request.get('/cards', params)
    //   .then(res => {
    //     this.setData({
    //       cardList: res.data.list,
    //       loading: false,
    //       hasMore: res.data.hasMore
    //     });
    //     
    //     if (callback && typeof callback === 'function') {
    //       callback();
    //     }
    //   })
    //   .catch(err => {
    //     console.error('加载信用卡失败', err);
    //     this.setData({ loading: false });
    //     wx.showToast({
    //       title: '加载数据失败',
    //       icon: 'none'
    //     });
    //     
    //     if (callback && typeof callback === 'function') {
    //       callback();
    //     }
    //   });
  },
  
  // 加载更多信用卡
  loadMoreCards() {
    if (this.data.loading) return;
    
    this.setData({
      page: this.data.page + 1,
      loading: true
    });
    
    // 模拟数据，实际项目中应该从API获取
    setTimeout(() => {
      const moreCards = this.generateMockCardList();
      
      this.setData({
        cardList: [...this.data.cardList, ...moreCards],
        loading: false,
        hasMore: moreCards.length >= this.data.pageSize
      });
    }, 1000);
    
    // 实际API调用示例
    // const params = {
    //   page: this.data.page,
    //   pageSize: this.data.pageSize,
    //   sort: this.data.currentSort
    // };
    // 
    // if (this.data.currentCategory !== 0) {
    //   params.categoryId = this.data.currentCategory;
    // }
    // 
    // if (this.data.currentBank !== 0) {
    //   params.bankId = this.data.currentBank;
    // }
    // 
    // return request.get('/cards', params)
    //   .then(res => {
    //     this.setData({
    //       cardList: [...this.data.cardList, ...res.data.list],
    //       loading: false,
    //       hasMore: res.data.hasMore
    //     });
    //   })
    //   .catch(err => {
    //     console.error('加载更多信用卡失败', err);
    //     this.setData({
    //       page: this.data.page - 1,
    //       loading: false
    //     });
    //     wx.showToast({
    //       title: '加载数据失败',
    //       icon: 'none'
    //     });
    //   });
  },
  
  // 切换分类
  onCategoryTap(e) {
    const categoryId = e.currentTarget.dataset.id;
    if (categoryId === this.data.currentCategory) return;
    
    this.setData({
      currentCategory: categoryId,
      cardList: [],
      page: 1,
      hasMore: true,
      showFilter: false
    }, () => {
      this.loadCards();
    });
  },
  
  // 切换银行
  onBankTap(e) {
    const bankId = e.currentTarget.dataset.id;
    if (bankId === this.data.currentBank) return;
    
    this.setData({
      currentBank: bankId,
      cardList: [],
      page: 1,
      hasMore: true,
      showFilter: false
    }, () => {
      this.loadCards();
    });
  },
  
  // 切换排序
  onSortTap(e) {
    const sortId = e.currentTarget.dataset.id;
    if (sortId === this.data.currentSort) return;
    
    this.setData({
      currentSort: sortId,
      cardList: [],
      page: 1,
      hasMore: true,
      showFilter: false
    }, () => {
      this.loadCards();
    });
  },
  
  // 显示/隐藏筛选面板
  toggleFilter() {
    this.setData({
      showFilter: !this.data.showFilter
    });
  },
  
  // 查看信用卡详情
  onCardItemTap(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/card-detail/card-detail?id=${id}`
    });
  },
  
  // 申请信用卡
  onApplyTap(e) {
    const { id } = e.currentTarget.dataset;
    // 这里可以跳转到申请页面或者直接打开申请链接
    wx.showModal({
      title: '申请信用卡',
      content: `您确定要申请ID为${id}的信用卡吗？`,
      success: (res) => {
        if (res.confirm) {
          wx.showToast({
            title: '申请已提交',
            icon: 'success'
          });
        }
      }
    });
    
    // 阻止事件冒泡
    return false;
  },
  
  // 生成模拟信用卡数据
  generateMockCardList() {
    const list = [];
    const categoryId = this.data.currentCategory;
    const bankId = this.data.currentBank;
    const startId = (this.data.page - 1) * this.data.pageSize + 1;
    
    for (let i = 0; i < this.data.pageSize; i++) {
      const id = startId + i;
      if (id > 30) break; // 最多30条数据
      
      // 根据分类和银行筛选
      const cardCategoryId = id % 4 + 1;
      const cardBankId = id % 8 + 1;
      
      if (categoryId !== 0 && cardCategoryId !== categoryId) continue;
      if (bankId !== 0 && cardBankId !== bankId) continue;
      
      list.push({
        id,
        name: this.getCardName(cardBankId, cardCategoryId),
        bankId: cardBankId,
        bankName: this.data.banks[cardBankId].name,
        categoryId: cardCategoryId,
        categoryName: this.data.categories[cardCategoryId].name,
        image: `/images/placeholders/card.svg`,
        annualFee: this.getAnnualFee(cardCategoryId),
        cashback: (Math.random() * 5).toFixed(1) + '%',
        points: Math.floor(Math.random() * 10) + 1,
        interestRate: (Math.random() * 10 + 10).toFixed(2) + '%',
        applyCount: Math.floor(Math.random() * 10000) + 1000,
        tags: this.getCardTags(cardCategoryId)
      });
    }
    
    // 根据排序选项排序
    if (this.data.currentSort === 'hot') {
      list.sort((a, b) => b.applyCount - a.applyCount);
    } else if (this.data.currentSort === 'new') {
      list.sort((a, b) => b.id - a.id);
    }
    
    return list;
  },
  
  // 获取信用卡名称
  getCardName(bankId, categoryId) {
    const bankNames = [
      '',
      '招商银行',
      '中信银行',
      '建设银行',
      '工商银行',
      '交通银行',
      '平安银行',
      '浦发银行',
      '广发银行'
    ];
    
    const cardTypes = [
      '',
      '白金信用卡',
      '金卡信用卡',
      '标准信用卡',
      '联名信用卡'
    ];
    
    const suffixes = [
      'Pro版',
      '尊享版',
      '钻石版',
      '优享版',
      '畅享版',
      '星座版',
      '旅行版',
      '商务版'
    ];
    
    return `${bankNames[bankId]}${cardTypes[categoryId]}${suffixes[Math.floor(Math.random() * suffixes.length)]}`;
  },
  
  // 获取年费
  getAnnualFee(categoryId) {
    switch(categoryId) {
      case 1: // 白金卡
        return Math.random() > 0.3 ? '¥1,000' : '首年免年费';
      case 2: // 金卡
        return Math.random() > 0.5 ? '¥300' : '首年免年费';
      case 3: // 普卡
        return Math.random() > 0.7 ? '¥100' : '免年费';
      case 4: // 联名卡
        return Math.random() > 0.5 ? '¥500' : '首年免年费';
      default:
        return '¥200';
    }
  },
  
  // 获取信用卡标签
  getCardTags(categoryId) {
    const allTags = [
      '免年费',
      '高额度',
      '低门槛',
      '加油优惠',
      '餐饮优惠',
      '旅行保险',
      '积分优惠',
      '0息分期',
      '航空里程',
      '境外返现',
      '购物折扣',
      '新户礼'
    ];
    
    // 随机选择2-4个标签
    const count = Math.floor(Math.random() * 3) + 2;
    const tags = [];
    const tagIndices = new Set();
    
    while (tagIndices.size < count) {
      tagIndices.add(Math.floor(Math.random() * allTags.length));
    }
    
    tagIndices.forEach(index => {
      tags.push(allTags[index]);
    });
    
    return tags;
  }
});