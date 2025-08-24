// pages/card-detail/card-detail.js
const app = getApp();
const request = require('../../utils/request');

Page({
  data: {
    cardId: null,
    cardDetail: null,
    loading: true,
    error: false,
    activeTab: 'intro',
    tabs: [
      { id: 'intro', name: '卡片介绍' },
      { id: 'benefits', name: '权益详情' },
      { id: 'fees', name: '费用说明' },
      { id: 'apply', name: '申请条件' }
    ],
    showCompare: false,
    compareCards: []
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ cardId: options.id });
      this.loadCardDetail();
    } else {
      this.setData({ 
        loading: false, 
        error: true 
      });
      wx.showToast({
        title: '信用卡ID不存在',
        icon: 'none'
      });
    }
  },
  
  onShareAppMessage() {
    const cardDetail = this.data.cardDetail;
    if (!cardDetail) return {};
    
    return {
      title: cardDetail.name,
      path: `/pages/card-detail/card-detail?id=${this.data.cardId}`,
      imageUrl: cardDetail.image
    };
  },
  
  // 加载信用卡详情
  loadCardDetail() {
    this.setData({ loading: true, error: false });
    
    // 模拟数据，实际项目中应该从API获取
    setTimeout(() => {
      const cardDetail = this.generateMockCardDetail();
      
      this.setData({
        cardDetail,
        loading: false
      });
      
      // 设置页面标题
      wx.setNavigationBarTitle({
        title: cardDetail.name.substring(0, 12) + (cardDetail.name.length > 12 ? '...' : '')
      });
    }, 1000);
    
    // 实际API调用示例
    // return request.get(`/cards/${this.data.cardId}`)
    //   .then(res => {
    //     this.setData({
    //       cardDetail: res.data,
    //       loading: false
    //     });
    //     
    //     // 设置页面标题
    //     wx.setNavigationBarTitle({
    //       title: res.data.name.substring(0, 12) + (res.data.name.length > 12 ? '...' : '')
    //     });
    //   })
    //   .catch(err => {
    //     console.error('加载信用卡详情失败', err);
    //     this.setData({ 
    //       loading: false, 
    //       error: true 
    //     });
    //     wx.showToast({
    //       title: '加载数据失败',
    //       icon: 'none'
    //     });
    //   });
  },
  
  // 切换标签页
  onTabTap(e) {
    const tabId = e.currentTarget.dataset.id;
    this.setData({ activeTab: tabId });
  },
  
  // 点击重试
  onRetryTap() {
    this.loadCardDetail();
  },
  
  // 申请信用卡
  onApplyTap() {
    if (!this.data.cardDetail) return;
    
    wx.showModal({
      title: '申请信用卡',
      content: `您确定要申请${this.data.cardDetail.name}吗？`,
      success: (res) => {
        if (res.confirm) {
          wx.showToast({
            title: '申请已提交',
            icon: 'success'
          });
        }
      }
    });
  },
  
  // 收藏信用卡
  onCollectTap() {
    if (!this.data.cardDetail) return;
    
    const cardDetail = { ...this.data.cardDetail };
    cardDetail.isCollected = !cardDetail.isCollected;
    
    this.setData({ cardDetail });
    
    wx.showToast({
      title: cardDetail.isCollected ? '收藏成功' : '已取消收藏',
      icon: 'success'
    });
    
    // 实际API调用示例
    // request.post(`/cards/${this.data.cardId}/collect`, { collected: cardDetail.isCollected })
    //   .then(res => {
    //     console.log('收藏成功', res);
    //   })
    //   .catch(err => {
    //     console.error('收藏失败', err);
    //     // 恢复原状态
    //     cardDetail.isCollected = !cardDetail.isCollected;
    //     this.setData({ cardDetail });
    //   });
  },
  
  // 添加/移除对比
  onCompareTap() {
    if (!this.data.cardDetail) return;
    
    const cardDetail = { ...this.data.cardDetail };
    cardDetail.isComparing = !cardDetail.isComparing;
    
    this.setData({ cardDetail });
    
    // 更新对比列表
    if (cardDetail.isComparing) {
      // 添加到对比列表
      wx.showToast({
        title: '已添加到对比',
        icon: 'success'
      });
    } else {
      // 从对比列表移除
      wx.showToast({
        title: '已从对比中移除',
        icon: 'success'
      });
    }
  },
  
  // 显示/隐藏对比面板
  toggleCompare() {
    this.setData({
      showCompare: !this.data.showCompare
    });
    
    if (this.data.showCompare && this.data.compareCards.length === 0) {
      // 加载对比卡片
      this.loadCompareCards();
    }
  },
  
  // 加载对比卡片
  loadCompareCards() {
    // 模拟数据，实际项目中应该从API获取
    setTimeout(() => {
      const compareCards = [
        {
          id: 101,
          name: '招商银行白金信用卡',
          image: '/images/placeholders/card.svg',
          annualFee: '¥1,000',
          cashback: '1.5%',
          points: '5倍'
        },
        {
          id: 102,
          name: '中信银行白金信用卡',
          image: '/images/placeholders/card.svg',
          annualFee: '¥800',
          cashback: '2.0%',
          points: '3倍'
        }
      ];
      
      this.setData({ compareCards });
    }, 500);
  },
  
  // 查看对比详情
  onViewCompareTap() {
    wx.navigateTo({
      url: '/pages/card-compare/card-compare'
    });
  },
  
  // 生成模拟信用卡详情数据
  generateMockCardDetail() {
    const id = parseInt(this.data.cardId);
    const categoryId = id % 4 + 1;
    const bankId = id % 8 + 1;
    
    const categories = [
      { id: 1, name: '白金卡' },
      { id: 2, name: '金卡' },
      { id: 3, name: '普卡' },
      { id: 4, name: '联名卡' }
    ];
    
    const banks = [
      { id: 1, name: '招商银行' },
      { id: 2, name: '中信银行' },
      { id: 3, name: '建设银行' },
      { id: 4, name: '工商银行' },
      { id: 5, name: '交通银行' },
      { id: 6, name: '平安银行' },
      { id: 7, name: '浦发银行' },
      { id: 8, name: '广发银行' }
    ];
    
    return {
      id,
      name: this.getCardName(bankId, categoryId),
      bankId,
      bankName: banks[bankId - 1].name,
      categoryId,
      categoryName: categories[categoryId - 1].name,
      image: `/images/placeholders/card.svg`,
      annualFee: this.getAnnualFee(categoryId),
      cashback: (Math.random() * 5).toFixed(1) + '%',
      points: Math.floor(Math.random() * 10) + 1 + '倍',
      interestRate: (Math.random() * 10 + 10).toFixed(2) + '%',
      creditLimit: this.getCreditLimit(categoryId),
      applyCount: Math.floor(Math.random() * 10000) + 1000,
      tags: this.getCardTags(categoryId),
      isCollected: false,
      isComparing: false,
      intro: this.getCardIntro(bankId, categoryId),
      benefits: this.getCardBenefits(categoryId),
      fees: this.getCardFees(categoryId),
      applyConditions: this.getApplyConditions(categoryId),
      applyMaterials: this.getApplyMaterials()
    };
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
  
  // 获取额度
  getCreditLimit(categoryId) {
    switch(categoryId) {
      case 1: // 白金卡
        return '¥50,000-¥300,000';
      case 2: // 金卡
        return '¥20,000-¥100,000';
      case 3: // 普卡
        return '¥10,000-¥50,000';
      case 4: // 联名卡
        return '¥30,000-¥200,000';
      default:
        return '¥20,000-¥100,000';
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
  },
  
  // 获取卡片介绍
  getCardIntro(bankId, categoryId) {
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
    
    return `${bankNames[bankId]}${cardTypes[categoryId]}是${bankNames[bankId]}推出的一款高端信用卡产品，为持卡人提供全方位的金融服务和生活特权。

卡片采用精致的设计，彰显持卡人的尊贵身份。持卡人可享受专属客户经理服务、机场贵宾厅、高尔夫礼遇、酒店优惠等多项权益。

同时，卡片还提供灵活的分期付款选项、丰富的积分兑换计划和全球紧急支援服务，满足持卡人在购物、旅行和日常生活中的各种需求。`;
  },
  
  // 获取权益详情
  getCardBenefits(categoryId) {
    const benefits = [];
    
    // 基础权益
    benefits.push({
      title: '基础权益',
      items: [
        { name: '免年费特权', desc: '刷卡满6次免次年年费' },
        { name: '积分奖励', desc: '消费1元累积1积分，可兑换礼品或里程' },
        { name: '网上支付', desc: '支持各大电商平台和移动支付' },
        { name: '电子账单', desc: '随时查询消费明细和账单' }
      ]
    });
    
    // 根据卡片类型添加不同权益
    switch(categoryId) {
      case 1: // 白金卡
        benefits.push({
          title: '尊享权益',
          items: [
            { name: '机场贵宾厅', desc: '全球600+机场贵宾厅免费使用' },
            { name: '高尔夫礼遇', desc: '国内多家高尔夫球场优惠' },
            { name: '酒店优惠', desc: '国际知名酒店集团优惠及升级' },
            { name: '专属客户经理', desc: '一对一专属服务' },
            { name: '全球紧急支援', desc: '境外紧急现金支援和医疗援助' }
          ]
        });
        break;
      
      case 2: // 金卡
        benefits.push({
          title: '优享权益',
          items: [
            { name: '机场贵宾厅', desc: '国内20+机场贵宾厅优惠' },
            { name: '餐饮优惠', desc: '精选餐厅8折优惠' },
            { name: '电影优惠', desc: '每月2张电影票买一赠一' },
            { name: '商旅保险', desc: '免费旅行意外保险' }
          ]
        });
        break;
      
      case 3: // 普卡
        benefits.push({
          title: '实用权益',
          items: [
            { name: '生日礼遇', desc: '生日当月获赠积分或礼品' },
            { name: '加油优惠', desc: '指定加油站95折' },
            { name: '网购返现', desc: '指定电商平台消费返现' }
          ]
        });
        break;
      
      case 4: // 联名卡
        benefits.push({
          title: '联名权益',
          items: [
            { name: '品牌专属优惠', desc: '联名品牌专属折扣和活动' },
            { name: '会员积分双享', desc: '同时累积银行积分和品牌积分' },
            { name: '限量礼品', desc: '专属定制限量版礼品' },
            { name: '品牌活动优先参与', desc: '优先参与品牌发布会和活动' }
          ]
        });
        break;
    }
    
    // 消费优惠
    benefits.push({
      title: '消费优惠',
      items: [
        { name: '0息分期', desc: '大额消费3-24期分期付款，最低0手续费' },
        { name: '优惠商户', desc: '全国数千家商户刷卡优惠' },
        { name: '新户礼', desc: '新卡激活后首刷有礼' }
      ]
    });
    
    return benefits;
  },
  
  // 获取费用说明
  getCardFees(categoryId) {
    const fees = [];
    
    // 年费
    let annualFee = '';
    switch(categoryId) {
      case 1: // 白金卡
        annualFee = '主卡年费1000元，附属卡500元。刷卡满6次或消费满5万元免次年年费。';
        break;
      case 2: // 金卡
        annualFee = '主卡年费300元，附属卡150元。刷卡满6次或消费满3万元免次年年费。';
        break;
      case 3: // 普卡
        annualFee = '主卡年费100元，附属卡50元。刷卡满6次或消费满1万元免次年年费。';
        break;
      case 4: // 联名卡
        annualFee = '主卡年费500元，附属卡250元。刷卡满6次或消费满3万元免次年年费。';
        break;
      default:
        annualFee = '主卡年费200元，附属卡100元。刷卡满6次或消费满2万元免次年年费。';
    }
    
    fees.push({ title: '年费', content: annualFee });
    
    // 其他费用
    fees.push({ 
      title: '利息', 
      content: '日利率万分之五，年化利率18.25%。' 
    });
    
    fees.push({ 
      title: '取现费用', 
      content: '预借现金交易金额的3%，最低人民币10元/笔。' 
    });
    
    fees.push({ 
      title: '违约金', 
      content: '最低还款额未还部分的5%，最低人民币10元。' 
    });
    
    fees.push({ 
      title: '境外交易手续费', 
      content: '交易金额的1.5%。' 
    });
    
    fees.push({ 
      title: '分期手续费', 
      content: '根据分期期数不同，费率为0%-0.9%/月。' 
    });
    
    fees.push({ 
      title: '补卡费', 
      content: '人民币40元/卡。' 
    });
    
    return fees;
  },
  
  // 获取申请条件
  getApplyConditions(categoryId) {
    const conditions = [];
    
    // 基本条件
    conditions.push({
      title: '基本条件',
      items: [
        '年龄18-65周岁',
        '具有完全民事行为能力',
        '信用记录良好',
        '有稳定的职业和收入来源'
      ]
    });
    
    // 收入要求
    let incomeRequirement = '';
    switch(categoryId) {
      case 1: // 白金卡
        incomeRequirement = '年收入20万元以上';
        break;
      case 2: // 金卡
        incomeRequirement = '年收入10万元以上';
        break;
      case 3: // 普卡
        incomeRequirement = '年收入3万元以上';
        break;
      case 4: // 联名卡
        incomeRequirement = '年收入8万元以上';
        break;
      default:
        incomeRequirement = '年收入5万元以上';
    }
    
    conditions.push({
      title: '收入要求',
      items: [incomeRequirement]
    });
    
    // 其他要求
    conditions.push({
      title: '其他要求',
      items: [
        '无不良信用记录',
        '近6个月内无逾期还款记录',
        '非本行黑名单客户'
      ]
    });
    
    return conditions;
  },
  
  // 获取申请材料
  getApplyMaterials() {
    return [
      '身份证原件',
      '收入证明（银行流水、工资单等）',
      '居住证明（水电费账单、房产证等）',
      '工作证明（工作证、劳动合同等）'
    ];
  }
});