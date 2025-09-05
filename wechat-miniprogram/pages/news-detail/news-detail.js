// pages/news-detail/news-detail.js
const app = getApp();
const request = require('../../utils/request');

Page({
  data: {
    newsId: null,
    newsDetail: null,
    loading: true,
    error: false
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ newsId: options.id });
      this.loadNewsDetail();
    } else {
      this.setData({ 
        loading: false, 
        error: true 
      });
      wx.showToast({
        title: '资讯ID不存在',
        icon: 'none'
      });
    }
  },
  
  onShareAppMessage() {
    const newsDetail = this.data.newsDetail;
    if (!newsDetail) return {};
    
    return {
      title: newsDetail.title,
      path: `/pages/news-detail/news-detail?id=${this.data.newsId}`,
      imageUrl: newsDetail.coverImage
    };
  },
  
  // 加载资讯详情
  loadNewsDetail() {
    this.setData({ loading: true, error: false });
    
    // 从API获取新闻详情
    const apiUrl = `/api/client/news/${this.data.newsId}`;
    
    request.get(apiUrl)
      .then(res => {
        if (res.success && res.data) {
          const newsDetail = {
            id: res.data.id,
            title: res.data.title,
            author: res.data.authorName,
            publishedAt: this.formatDate(res.data.publishTime || res.data.createdAt),
            categoryName: res.data.categoryName,
            coverImage: res.data.coverImage,
            content: res.data.content || '', // 直接使用Markdown内容字符串
            viewCount: res.data.viewCount || 0,
            likeCount: 0,
            isLiked: false,
            isCollected: false,
            tags: res.data.tags || []
          };
          
          this.setData({
            newsDetail,
            loading: false
          });
          
          // 设置页面标题
          wx.setNavigationBarTitle({
            title: newsDetail.title.substring(0, 12) + (newsDetail.title.length > 12 ? '...' : '')
          });
          
          // 增加浏览量
          this.incrementViewCount();
        } else {
          this.setData({ loading: false, error: true });
        }
      })
      .catch(err => {
        console.error('加载资讯详情失败', err);
        this.setData({ loading: false, error: true });
      });
      
    // 模拟数据用于开发测试
    // setTimeout(() => {
    //   const newsDetail = this.generateMockNewsDetail();
    //   
    //   this.setData({
    //     newsDetail,
    //     loading: false
    //   });
    //   
    //   // 设置页面标题
    //   wx.setNavigationBarTitle({
    //     title: newsDetail.title.substring(0, 12) + (newsDetail.title.length > 12 ? '...' : '')
    //   });
    // }, 1000);
  },
  
  // 实际API调用示例
  // incrementViewCount() {
  //   return request.post(`/news/${this.data.newsId}/view`)
  //     .then(res => {
  //       console.log('增加浏览量成功', res);
  //     })
  //     .catch(err => {
  //       console.error('增加浏览量失败', err);
  //     });
  // },
  
  // 格式化日期
  formatDate(dateStr) {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    
    return `${year}-${month}-${day} ${hours}:${minutes}`;
  },
  
  // 点击重试
  onRetryTap() {
    this.loadNewsDetail();
  },
  
  // 点赞
  onLikeTap() {
    if (!this.data.newsDetail) return;
    
    const newsDetail = { ...this.data.newsDetail };
    newsDetail.isLiked = !newsDetail.isLiked;
    newsDetail.likeCount = newsDetail.isLiked ? newsDetail.likeCount + 1 : newsDetail.likeCount - 1;
    
    this.setData({ newsDetail });
    
    // 实际API调用示例
    // request.post(`/news/${this.data.newsId}/like`, { liked: newsDetail.isLiked })
    //   .then(res => {
    //     console.log('点赞成功', res);
    //   })
    //   .catch(err => {
    //     console.error('点赞失败', err);
    //     // 恢复原状态
    //     newsDetail.isLiked = !newsDetail.isLiked;
    //     newsDetail.likeCount = newsDetail.isLiked ? newsDetail.likeCount + 1 : newsDetail.likeCount - 1;
    //     this.setData({ newsDetail });
    //   });
  },
  
  // 收藏
  onCollectTap() {
    if (!this.data.newsDetail) return;
    
    const newsDetail = { ...this.data.newsDetail };
    newsDetail.isCollected = !newsDetail.isCollected;
    
    this.setData({ newsDetail });
    
    // 实际API调用示例
    // request.post(`/news/${this.data.newsId}/collect`, { collected: newsDetail.isCollected })
    //   .then(res => {
    //     console.log('收藏成功', res);
    //     wx.showToast({
    //       title: newsDetail.isCollected ? '收藏成功' : '已取消收藏',
    //       icon: 'success'
    //     });
    //   })
    //   .catch(err => {
    //     console.error('收藏失败', err);
    //     // 恢复原状态
    //     newsDetail.isCollected = !newsDetail.isCollected;
    //     this.setData({ newsDetail });
    //   });
  },
  
  // 分享
  onShareTap() {
    if (!this.data.newsDetail) return;
    
    wx.showShareMenu({
      withShareTicket: true,
      menus: ['shareAppMessage', 'shareTimeline']
    });
  },
  
  // 增加浏览量
  incrementViewCount() {
    // 模拟增加浏览量
    console.log('增加浏览量');
    // 实际项目中应调用API
  },
  
  // 生成模拟资讯详情数据
  generateMockNewsDetail() {
    const id = parseInt(this.data.newsId);
    const categoryId = id % 4 + 1;
    const categories = [
      { id: 1, name: '优惠活动' },
      { id: 2, name: '申卡攻略' },
      { id: 3, name: '用卡技巧' },
      { id: 4, name: '信用知识' }
    ];
    
    return {
      id,
      title: this.getRandomTitle(id),
      coverImage: `/images/placeholders/news.svg`,
      publishedAt: this.getRandomDate(),
      categoryId,
      categoryName: categories[categoryId - 1].name,
      author: '信用卡专家',
      viewCount: Math.floor(Math.random() * 2000) + 100,
      likeCount: Math.floor(Math.random() * 100) + 10,
      isLiked: false,
      isCollected: false,
      content: this.getNewsContent(id)
    };
  },
  
  // 获取随机标题
  getRandomTitle(id) {
    const titles = [
      '招商银行信用卡新户专享优惠活动详解',
      '中信银行信用卡5月优惠活动汇总与分析',
      '平安银行信用卡申请全攻略：提高通过率的秘诀',
      '交通银行信用卡积分兑换指南：如何最大化积分价值',
      '建设银行龙卡信用卡权益详解与使用技巧',
      '广发银行信用卡免年费攻略：三大方法轻松免年费',
      '浦发银行信用卡优惠活动解析与参与方式',
      '兴业银行信用卡申请条件与材料准备详解',
      '民生银行信用卡用卡技巧：账单日与还款日的选择',
      '光大银行信用卡积分规则详解与高效累积方法'  
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
  },
  
  // 获取资讯内容
  getNewsContent(id) {
    // 根据不同的资讯类型返回不同的内容
    const categoryId = id % 4 + 1;
    
    let content = '';
    
    switch(categoryId) {
      case 1: // 优惠活动
        content = `
# 招商银行信用卡新户专享优惠活动详解

![活动海报](/images/placeholders/news.svg)

## 活动时间

2023年5月1日至2023年6月30日

## 活动内容

招商银行信用卡新户可享受以下专属优惠：

1. **首刷礼**：新卡激活后首刷任意金额，即可获得价值100元电影票券
2. **消费返现**：首月累计消费满3000元，返现100元
3. **分期优惠**：首次办理分期享受手续费5折优惠

## 参与方式

1. 登录招商银行APP或信用卡APP
2. 点击"优惠活动"版块
3. 找到"新户专享"活动并报名参与

## 活动规则

- 本活动仅限招商银行信用卡新户参与
- 新户定义：过去12个月内未持有招商银行信用卡的客户
- 活动奖励将在满足条件后的10个工作日内发放
- 每位客户仅可参与一次

## 注意事项

- 活动期间，如发现客户存在刷卡套现、虚假交易等违规行为，银行有权取消其活动资格
- 本活动最终解释权归招商银行所有
        `;
        break;
      
      case 2: // 申卡攻略
        content = `
# 平安银行信用卡申请全攻略：提高通过率的秘诀

![信用卡样式](/images/placeholders/card.svg)

## 平安银行热门信用卡推荐

### 1. 平安银行标准白金卡

- **年费**：主卡300元/年，附属卡150元/年（首年免年费，次年刷卡6次免年费）
- **额度**：最高20万
- **权益**：机场贵宾厅、高尔夫礼遇、酒店优惠等

### 2. 平安银行中国红卡

- **年费**：主卡100元/年（首年免年费，次年刷卡3次免年费）
- **额度**：最高10万
- **权益**：网购返现、加油优惠、餐饮折扣等

## 申请条件

1. 年龄要求：18-65周岁
2. 收入要求：月收入在2000元以上
3. 信用记录：良好的个人信用记录
4. 工作稳定：连续工作6个月以上

## 申请材料

1. 身份证原件
2. 收入证明（银行流水、工资单等）
3. 居住证明（水电费账单、房产证等）
4. 工作证明（工作证、劳动合同等）

## 提高通过率的技巧

1. **提前了解自己的征信情况**
   申请前查询个人征信报告，确保没有不良记录

2. **选择合适的信用卡产品**
   根据自己的收入和消费习惯选择适合的卡种

3. **准备充分的申请材料**
   提供真实、完整的个人信息和证明材料

4. **合理填写申请表**
   收入信息要真实且与提供的证明材料一致

5. **避免短期内多次申请**
   短期内多次申请信用卡会降低通过率

## 申请渠道

1. 平安银行官网
2. 平安银行APP
3. 平安银行网点
4. 平安银行客服电话

## 审批流程及时间

1. 提交申请
2. 资料审核（1-3个工作日）
3. 电话核实（1个工作日）
4. 审批结果（1-2个工作日）
5. 制卡寄送（3-5个工作日）

总体审批时间约7-10个工作日
        `;
        break;
      
      case 3: // 用卡技巧
        content = `
# 信用卡账单日与还款日的聪明选择

![账单示意图](/images/placeholders/news.svg)

## 什么是账单日和还款日？

**账单日**是银行对您当期消费进行汇总结算的日期，也是一个账单周期的结束和下一个账单周期的开始。

**还款日**是您必须还清当期账单最低还款额的最后期限，通常在账单日后的18-25天。

## 账单日与还款日的关系

假设您的账单日是每月5日，还款日是每月25日：
- 4月6日至5月5日的消费将在5月5日生成账单
- 您需要在5月25日前还款
- 如果全额还款，则不产生利息

## 如何选择最佳账单日？

### 1. 与工资发放日错开

建议选择工资发放后的3-5天作为账单日，这样：
- 工资到账后可以立即还清上期账单
- 新的消费进入下一期账单，可以获得长达50天左右的免息期

### 2. 根据大额消费时间选择

如果您有固定的大额消费，如房租：
- 将账单日设在大额消费后1-2天
- 这样大额消费可以享受最长的免息期

### 3. 多卡账单日错开

如果持有多张信用卡：
- 将不同卡的账单日和还款日错开安排
- 避免还款压力集中在某一时段

## 最大化免息期的策略

### 1. 了解免息期计算方式

免息期 = 账单日 + 还款宽限期(通常20-25天) - 消费日

### 2. 大额消费安排在账单日后

- 账单日刚过进行大额消费
- 这笔消费计入下期账单
- 可享受最长约50天免息期

### 3. 灵活使用分期付款

- 当无法享受足够免息期时
- 考虑使用分期付款功能
- 部分银行有免息分期活动

## 注意事项

1. **避免取现**
   信用卡取现没有免息期，从取现当天开始计息

2. **设置还款提醒**
   在还款日前3-5天设置提醒，避免逾期

3. **保持充足还款资金**
   还款日前确保还款账户有足够资金

4. **了解最低还款额陷阱**
   只还最低还款额会产生高额利息，应尽量全额还款
        `;
        break;
      
      case 4: // 信用知识
        content = `
# 个人征信报告解读：了解影响信用的关键因素

![征信报告](/images/placeholders/news.svg)

## 什么是个人征信报告？

个人征信报告是记录个人信用活动和信用评价的文件，由中国人民银行征信中心统一管理。它记录了个人的信贷交易和履约情况，是金融机构评估个人信用状况的重要依据。

## 征信报告包含哪些内容？

### 1. 个人基本信息

- 姓名、证件号码、联系方式等
- 工作单位、居住地址等

### 2. 信贷交易信息

- **贷款信息**：包括房贷、车贷、消费贷等
- **信用卡信息**：包括发卡行、额度、使用情况等
- **担保信息**：为他人提供的担保记录

### 3. 公共记录信息

- 欠税记录
- 民事判决记录
- 行政处罚记录

### 4. 查询记录

- 什么机构在什么时间查询过您的征信
- 查询原因（贷款审批、信用卡审批等）

## 影响个人征信的关键因素

### 1. 还款历史（占比35%）

- **按时还款**：最重要的正面因素
- **逾期还款**：严重负面影响，尤其是连续逾期
- **逾期时间**：逾期天数越长，影响越严重

### 2. 信贷负债水平（占比25%）

- **负债率**：总债务占总额度的比例
- **信用卡使用率**：已用额度占总额度的比例（建议控制在30%以下）

### 3. 信用历史长度（占比15%）

- **首笔贷款/信用卡时间**：使用信贷产品的时间越长越好
- **账户活跃度**：长期不使用的账户对提升信用帮助有限

### 4. 信贷类型多样性（占比10%）

- **多种类型的信贷产品**：如信用卡、房贷、车贷等
- **过度依赖单一类型**：如只有多张信用卡，评分较低

### 5. 新增信贷申请（占比15%）

- **短期内多次申请**：显示财务压力，降低信用评分
- **新开户数量**：短期内开立多个新账户会降低评分

## 如何查询个人征信报告？

### 1. 线下查询

- 携带身份证前往中国人民银行分支机构
- 填写《个人信用报告本人查询申请表》
- 现场打印查询结果

### 2. 线上查询

- 登录中国人民银行征信中心官网
- 注册个人账户并进行身份认证
- 在线查询并下载报告

### 3. 通过商业银行APP查询

- 部分银行APP提供征信查询服务
- 需要进行身份验证和授权

## 如何修复不良征信记录？

### 1. 了解不良记录原因

详细查看征信报告，确认不良记录的具体原因

### 2. 及时还清欠款

对于逾期账单，应立即还清并保留还款凭证

### 3. 与银行协商

如有特殊情况导致的逾期，可与银行协商消除不良记录

### 4. 保持良好的还款习惯

坚持按时还款至少两年，逐步改善信用记录

### 5. 合理控制信贷申请

避免短期内多次申请信贷产品

### 6. 定期检查征信报告

每年查询1-2次征信报告，及时发现并纠正错误信息

## 征信报告常见问题

### Q1: 不良征信记录会保存多久？

A: 一般情况下，不良记录在还清欠款后会保存5年，5年后自动消除。

### Q2: 查询自己的征信会影响信用评分吗？

A: 个人查询属于"自查"，不会影响信用评分。

### Q3: 征信报告中有错误信息怎么办？

A: 可以向征信中心提出异议申请，征信中心会在20个工作日内给予答复。
        `;
        break;
      
      default:
        content = `
# 默认资讯内容

这是一篇关于信用卡的资讯文章，内容丰富多彩，包含了许多有用的信息和建议。

## 第一部分

这里是第一部分的内容，介绍了信用卡的基本知识和使用技巧。

## 第二部分

这里是第二部分的内容，详细讲解了如何合理使用信用卡，避免陷入债务陷阱。

## 第三部分

这里是第三部分的内容，分享了一些信用卡优惠活动和积分兑换的小技巧。
        `;
    }
    
    return content;
  }
});