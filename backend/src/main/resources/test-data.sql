-- PrimeCard Hub 测试数据脚本
-- 创建时间: 2023-06-15

-- 清空现有数据（如果需要）
-- 注意：执行此操作将删除所有现有数据
/*
DELETE FROM news_tags;
DELETE FROM news;
DELETE FROM tags;
DELETE FROM categories;
DELETE FROM credit_cards;
DELETE FROM user_session;
DELETE FROM users;
*/

-- 插入用户数据
INSERT INTO users (username, email, password, role, status, created_at, updated_at) VALUES
('admin', 'admin@primehub.com', '$2a$10$eDIJO.xBXAYYi/Qg.1O5A.0BQgTTBw.QiRQY1GkfG/uFj0C.f6QXC', 'ADMIN', 'ACTIVE', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('editor', 'editor@primehub.com', '$2a$10$eDIJO.xBXAYYi/Qg.1O5A.0BQgTTBw.QiRQY1GkfG/uFj0C.f6QXC', 'EDITOR', 'ACTIVE', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('viewer', 'viewer@primehub.com', '$2a$10$eDIJO.xBXAYYi/Qg.1O5A.0BQgTTBw.QiRQY1GkfG/uFj0C.f6QXC', 'VIEWER', 'ACTIVE', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- 插入信用卡数据
INSERT INTO credit_cards (bank_name, card_name, card_type, card_level, annual_fee, apply_url, card_image, status, created_at, updated_at) VALUES
('中国银行', '长城环球通信用卡', 'CREDIT', 'PLATINUM', '¥1800', 'https://www.boc.cn/bcservice/bc1/201806/t20180615_12077336.html', '/uploads/cards/boc_platinum.png', 'ACTIVE', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('招商银行', '招商银行Visa金卡', 'CREDIT', 'GOLD', '¥300', 'https://cc.cmbchina.com/card/applicat.aspx', '/uploads/cards/cmb_gold.png', 'ACTIVE', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('工商银行', '工银Mastercard白金卡', 'CREDIT', 'PLATINUM', '¥1500', 'https://elife.icbc.com.cn/OFSTCARDWEB/dist/index.html', '/uploads/cards/icbc_platinum.png', 'ACTIVE', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('建设银行', '龙卡全球支付信用卡', 'CREDIT', 'STANDARD', '¥0', 'http://credit.ccb.com/cn/creditcard/apply/online.html', '/uploads/cards/ccb_standard.png', 'ACTIVE', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('交通银行', '交通银行白金信用卡', 'CREDIT', 'PLATINUM', '¥2000', 'https://creditcardapp.bankcomm.com/applynew/front/apply/new/identity.html', '/uploads/cards/bocom_platinum.png', 'ACTIVE', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('中信银行', '中信银行钻石信用卡', 'CREDIT', 'DIAMOND', '¥5000', 'https://creditcard.ecitic.com/h5/shenqing/index.html', '/uploads/cards/citic_diamond.png', 'ACTIVE', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('浦发银行', '浦发银行AE白金卡', 'CREDIT', 'PLATINUM', '¥1600', 'https://ecentre.spdbccc.com.cn/creditcard/indexActivity.htm', '/uploads/cards/spdb_platinum.png', 'ACTIVE', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('广发银行', '广发银行臻尚白金卡', 'CREDIT', 'PLATINUM', '¥1800', 'http://www.cgbchina.com.cn/Channel/11854968', '/uploads/cards/cgb_platinum.png', 'ACTIVE', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('兴业银行', '兴业银行VISA标准卡', 'CREDIT', 'STANDARD', '¥100', 'https://www.cib.com.cn/cn/personal/credit-card/apply/index.html', '/uploads/cards/cib_standard.png', 'ACTIVE', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('民生银行', '民生银行金卡信用卡', 'CREDIT', 'GOLD', '¥200', 'http://creditcard.cmbc.com.cn/home/apply/index.shtml', '/uploads/cards/cmbc_gold.png', 'ACTIVE', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- 插入卡片权益数据
INSERT INTO card_benefits (card_id, benefit) VALUES
(1, '全球机场贵宾厅服务'),
(1, '航空意外保险'),
(1, '全球紧急支援服务'),
(1, '高尔夫礼遇'),
(2, '生日双倍积分'),
(2, '网购返现'),
(2, '电影票买一送一'),
(3, '全球机场贵宾厅服务'),
(3, '航空延误险'),
(3, '高额取现额度'),
(3, '酒店优惠'),
(4, '新户礼'),
(4, '积分兑换'),
(4, '账单分期'),
(5, '全球机场贵宾厅服务'),
(5, '高铁贵宾厅'),
(5, '航空延误险'),
(5, '酒店优惠'),
(6, '私人管家服务'),
(6, '全球机场贵宾厅'),
(6, '高尔夫礼遇'),
(6, '豪华酒店礼遇'),
(6, '航空礼遇'),
(7, '全球机场贵宾厅'),
(7, '航空延误险'),
(7, '旅游意外险'),
(7, '酒店优惠'),
(8, '全球机场贵宾厅'),
(8, '高铁贵宾厅'),
(8, '航空延误险'),
(8, '生日礼遇'),
(9, '新户礼'),
(9, '积分兑换'),
(9, '网购优惠'),
(10, '生日双倍积分'),
(10, '电影票优惠'),
(10, '餐饮优惠');

-- 插入分类数据
INSERT INTO categories (name, description, icon, sort_order, status, created_at, updated_at) VALUES
('信用卡资讯', '最新信用卡相关新闻和资讯', 'news-icon', 1, 'ACTIVE', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('用卡技巧', '信用卡使用技巧和攻略', 'tips-icon', 2, 'ACTIVE', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('优惠活动', '信用卡最新优惠活动信息', 'discount-icon', 3, 'ACTIVE', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('积分攻略', '信用卡积分获取和使用攻略', 'points-icon', 4, 'ACTIVE', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('理财知识', '个人理财和信用卡相关知识', 'finance-icon', 5, 'ACTIVE', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- 插入标签数据
INSERT INTO tags (name, color, usage_count, created_at, updated_at) VALUES
('信用卡', '#FF5733', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('优惠', '#33FF57', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('积分', '#3357FF', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('旅行', '#F033FF', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('美食', '#FF3333', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('购物', '#33FFF3', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('理财', '#FFFF33', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('新手', '#8333FF', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('高端卡', '#FF8333', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('活动', '#33FFBE', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- 插入新闻数据
INSERT INTO news (title, content, summary, category_id, status, cover_image, author_id, view_count, publish_time, created_at, updated_at) VALUES
('2023年最值得办理的5张信用卡', '<p>随着消费升级和支付方式的多样化，信用卡已成为现代人生活中不可或缺的金融工具。2023年，各大银行推出了众多具有竞争力的信用卡产品，本文将为您盘点最值得办理的5张信用卡。</p><h2>1. 中国银行长城环球通信用卡白金卡</h2><p>这张卡的最大亮点是其强大的出行权益，包括全球机场贵宾厅服务、航空意外保险以及全球紧急支援服务。年费1800元，刷卡满5万可免年费。</p><h2>2. 招商银行Visa金卡</h2><p>招行金卡的优势在于其丰富的日常优惠，包括生日双倍积分、网购返现以及电影票买一送一等特权。年费较低，仅300元，且首年免年费，次年刷卡满3万可免年费。</p><h2>3. 中信银行钻石信用卡</h2><p>作为高端卡种，中信钻石卡提供私人管家服务、全球机场贵宾厅、高尔夫礼遇等尊贵权益。虽然年费较高，达5000元，但对于高净值人群来说，其带来的增值服务不容忽视。</p><h2>4. 浦发银行AE白金卡</h2><p>浦发AE白金卡在旅行权益方面表现突出，提供全球机场贵宾厅、航空延误险、旅游意外险等保障。年费1600元，刷卡满6万可免年费。</p><h2>5. 广发银行臻尚白金卡</h2><p>广发臻尚白金卡的特色是其多样化的生活权益，包括全球机场贵宾厅、高铁贵宾厅、航空延误险以及生日礼遇等。年费1800元，刷卡满5万可免年费。</p><p>以上就是2023年最值得办理的5张信用卡，消费者可以根据自己的需求和消费习惯选择最适合的一款。</p>', '2023年各大银行推出了众多具有竞争力的信用卡产品，本文为您盘点最值得办理的5张信用卡，包括中行长城环球通、招行Visa金卡、中信钻石卡、浦发AE白金卡和广发臻尚白金卡。', 1, 'PUBLISHED', '/uploads/news/top5_cards_2023.jpg', 1, 1250, '2023-01-15 08:00:00', '2023-01-14 15:30:00', '2023-01-14 15:30:00'),
('信用卡积分兑换攻略：让你的积分价值最大化', '<p>信用卡积分是持卡人在消费过程中获得的回馈，合理利用这些积分可以为我们带来不少实惠。本文将分享一些信用卡积分兑换的技巧，帮助你实现积分价值最大化。</p><h2>了解积分规则</h2><p>不同银行的信用卡积分规则各不相同，包括积分获取方式、有效期、兑换比例等。在开始积累积分前，务必详细了解你所持有信用卡的积分规则。</p><h2>选择高价值兑换项目</h2><p>一般来说，将积分兑换为里程或酒店积分通常能获得较高的价值回报，特别是在有促销活动时。而兑换实物商品或现金抵扣通常价值较低。</p><h2>关注限时优惠活动</h2><p>银行经常会推出积分兑换的限时优惠活动，如兑换里程加成、特定商品折扣等。及时关注这些活动可以大幅提升积分价值。</p><h2>合理规划积分使用时间</h2><p>由于积分通常有有效期限制，建议在积分即将到期前进行兑换。同时，也要避免在没有优惠的情况下仓促兑换。</p><h2>综合利用多张信用卡</h2><p>如果你持有多张不同银行的信用卡，可以根据不同消费场景选择最优的积分回报率，从而最大化积分累积。</p><p>通过以上策略，相信你可以更好地管理和利用信用卡积分，让这些看似微小的回馈为你创造更多价值。</p>', '信用卡积分是持卡人的重要权益，本文分享了积分兑换的技巧，包括了解积分规则、选择高价值兑换项目、关注限时优惠活动、合理规划积分使用时间以及综合利用多张信用卡等方面，帮助读者实现积分价值最大化。', 4, 'PUBLISHED', '/uploads/news/points_strategy.jpg', 2, 980, '2023-02-20 10:30:00', '2023-02-19 16:45:00', '2023-02-19 16:45:00'),
('信用卡安全使用指南：保护你的资金安全', '<p>随着电子支付的普及，信用卡安全问题日益引起关注。本文将为您提供一些实用的信用卡安全使用建议，帮助您有效保护个人财产安全。</p><h2>妥善保管信用卡</h2><p>不要将信用卡与身份证等重要证件放在一起，避免同时丢失。外出时，尽量将信用卡放在贴身的口袋或包包中，减少丢失风险。</p><h2>保护个人信息</h2><p>不要随意向他人透露信用卡号码、有效期、CVV码等敏感信息。在公共场所使用信用卡时，注意遮挡密码输入过程，防止被窥视。</p><h2>警惕钓鱼网站和诈骗电话</h2><p>不要点击来源不明的链接或附件，不要在不安全的网站上输入信用卡信息。接到自称银行工作人员的电话时，不要轻易透露个人和卡片信息，可以通过官方渠道回拨确认。</p><h2>定期检查账单</h2><p>养成定期查看信用卡账单的习惯，及时发现可疑交易。许多银行提供交易提醒服务，建议开通此功能，实时掌握卡片使用情况。</p><h2>及时挂失</h2><p>一旦发现信用卡丢失或被盗，应立即通过银行客服热线进行挂失，并在24小时内到银行柜台办理书面挂失手续。</p><p>通过以上措施，可以有效降低信用卡被盗用的风险，保障个人财产安全。记住，安全意识是保护信用卡安全的第一道防线。</p>', '本文提供了信用卡安全使用的实用建议，包括妥善保管信用卡、保护个人信息、警惕钓鱼网站和诈骗电话、定期检查账单以及及时挂失等方面，帮助读者有效保护个人财产安全，防范信用卡被盗用的风险。', 2, 'PUBLISHED', '/uploads/news/card_security.jpg', 1, 1560, '2023-03-05 09:15:00', '2023-03-04 14:20:00', '2023-03-04 14:20:00'),
('2023年第二季度信用卡优惠活动汇总', '<p>2023年第二季度已经开始，各大银行推出了丰富多样的信用卡优惠活动。本文为您汇总了当前最值得关注的信用卡优惠，帮助您在日常消费中获取更多优惠。</p><h2>餐饮优惠</h2><p>1. 招商银行信用卡：周五餐饮消费满100元立减30元，每户每月限1次。<br>2. 中信银行信用卡：指定餐厅消费满200元享8折优惠，每户每月限2次。<br>3. 广发银行信用卡：周末指定连锁餐厅消费满150元立减50元，每户每周限1次。</p><h2>购物优惠</h2><p>1. 工商银行信用卡：京东商城每周二消费满300元立减60元，每户每周限1次。<br>2. 建设银行信用卡：天猫超市每周四消费满200元立减40元，每户每周限1次。<br>3. 浦发银行信用卡：唯品会每周三消费满400元立减100元，每户每月限2次。</p><h2>娱乐优惠</h2><p>1. 交通银行信用卡：全国指定影院观影买一送一，每户每月限2次。<br>2. 民生银行信用卡：指定KTV消费满300元立减100元，每户每月限1次。<br>3. 兴业银行信用卡：指定景点门票购买满200元立减60元，每户每季度限3次。</p><h2>出行优惠</h2><p>1. 中国银行信用卡：高铁票购买满300元立减50元，每户每月限2次。<br>2. 平安银行信用卡：指定加油站加油满300元立减30元，每户每周限1次。<br>3. 光大银行信用卡：滴滴出行消费满60元立减15元，每户每周限2次。</p><p>以上优惠活动的具体使用条件和有效期可能会有所不同，建议持卡人在使用前详细了解相关规则，并关注银行官方渠道的最新公告。</p>', '2023年第二季度各大银行推出了丰富多样的信用卡优惠活动，本文汇总了当前最值得关注的餐饮、购物、娱乐和出行类优惠，帮助读者在日常消费中获取更多实惠，建议持卡人在使用前详细了解相关规则。', 3, 'PUBLISHED', '/uploads/news/q2_promotions.jpg', 2, 850, '2023-04-10 11:00:00', '2023-04-09 17:30:00', '2023-04-09 17:30:00'),
('如何科学管理多张信用卡', '<p>随着消费场景的多元化，越来越多的人持有多张信用卡。如何科学管理这些信用卡，避免出现逾期、超额等问题，成为许多持卡人面临的挑战。本文将分享一些实用的多卡管理技巧。</p><h2>建立信用卡清单</h2><p>首先，建议创建一个信用卡清单，记录每张卡的基本信息，包括发卡行、卡号后四位、额度、账单日、还款日、年费规则等。这样可以清晰掌握所有卡片情况，避免遗漏。</p><h2>合理规划用卡场景</h2><p>根据不同信用卡的优惠政策，合理规划各卡的使用场景。例如，某张卡在加油站有优惠，则专门用于加油；另一张卡在超市有返现，则用于日常购物等。这样可以最大化每张卡的价值。</p><h2>设置还款提醒</h2><p>为每张信用卡设置还款提醒，可以利用手机日历、银行APP或第三方记账软件。建议在还款日前3-5天设置提醒，留出足够的操作时间，避免因临时原因导致逾期。</p><h2>控制总体负债率</h2><p>虽然持有多张信用卡可以提高总授信额度，但应控制总体负债率，即所有信用卡的已用额度与总额度的比例，建议不超过30%。这不仅有利于个人财务健康，也有助于维护良好的信用记录。</p><h2>定期检视持卡情况</h2><p>每隔3-6个月，检视一次所有信用卡的使用情况，评估每张卡的实际价值。对于长期不用或价值较低的卡片，可以考虑销卡，简化管理。</p><h2>建立应急资金</h2><p>为应对可能的财务紧急情况，建议建立相当于3-6个月生活费的应急资金。这样可以避免在资金紧张时过度依赖信用卡，陷入债务循环。</p><p>通过以上方法，相信您可以更好地管理多张信用卡，既享受到各类优惠权益，又能维护良好的个人信用和财务状况。</p>', '随着消费场景的多元化，越来越多的人持有多张信用卡。本文分享了科学管理多卡的技巧，包括建立信用卡清单、合理规划用卡场景、设置还款提醒、控制总体负债率、定期检视持卡情况以及建立应急资金等方面，帮助读者既能享受各类优惠权益，又能维护良好的个人信用和财务状况。', 2, 'PUBLISHED', '/uploads/news/multi_cards_management.jpg', 1, 1320, '2023-05-18 14:30:00', '2023-05-17 19:45:00', '2023-05-17 19:45:00');

-- 插入新闻标签关联数据
INSERT INTO news_tags (news_id, tag_id) VALUES
(1, 1), (1, 9),
(2, 1), (2, 3), (2, 8),
(3, 1), (3, 8),
(4, 1), (4, 2), (4, 10),
(5, 1), (5, 7), (5, 8);

-- 更新标签使用次数
UPDATE tags SET usage_count = (SELECT COUNT(*) FROM news_tags WHERE tag_id = 1) WHERE id = 1;
UPDATE tags SET usage_count = (SELECT COUNT(*) FROM news_tags WHERE tag_id = 2) WHERE id = 2;
UPDATE tags SET usage_count = (SELECT COUNT(*) FROM news_tags WHERE tag_id = 3) WHERE id = 3;
UPDATE tags SET usage_count = (SELECT COUNT(*) FROM news_tags WHERE tag_id = 4) WHERE id = 4;
UPDATE tags SET usage_count = (SELECT COUNT(*) FROM news_tags WHERE tag_id = 5) WHERE id = 5;
UPDATE tags SET usage_count = (SELECT COUNT(*) FROM news_tags WHERE tag_id = 6) WHERE id = 6;
UPDATE tags SET usage_count = (SELECT COUNT(*) FROM news_tags WHERE tag_id = 7) WHERE id = 7;
UPDATE tags SET usage_count = (SELECT COUNT(*) FROM news_tags WHERE tag_id = 8) WHERE id = 8;
UPDATE tags SET usage_count = (SELECT COUNT(*) FROM news_tags WHERE tag_id = 9) WHERE id = 9;
UPDATE tags SET usage_count = (SELECT COUNT(*) FROM news_tags WHERE tag_id = 10) WHERE id = 10;

-- 插入轮播图测试数据
INSERT INTO banners (
    title, 
    image_url, 
    link_type, 
    link_url, 
    link_id, 
    sort_order, 
    status, 
    start_time, 
    end_time, 
    view_count, 
    click_count, 
    created_by, 
    created_at, 
    updated_at
) VALUES 
(
    '招商银行信用卡优惠活动', 
    'https://via.placeholder.com/750x300/FF6B35/FFFFFF?text=招商银行优惠活动',
    'NEWS', 
    NULL, 
    1, 
    1, 
    'ACTIVE', 
    '2023-01-01 00:00:00', 
    '2024-12-31 23:59:59', 
    128, 
    45, 
    1, 
    CURRENT_TIMESTAMP(), 
    CURRENT_TIMESTAMP()
),
(
    '中信银行钻石卡申请', 
    'https://via.placeholder.com/750x300/4A90E2/FFFFFF?text=中信银行钻石卡',
    'EXTERNAL', 
    'https://creditcard.ecitic.com/h5/shenqing/index.html', 
    NULL, 
    2, 
    'ACTIVE', 
    '2023-01-01 00:00:00', 
    '2024-12-31 23:59:59', 
    89, 
    32, 
    1, 
    CURRENT_TIMESTAMP(), 
    CURRENT_TIMESTAMP()
),
(
    '信用卡积分兑换攻略', 
    'https://via.placeholder.com/750x300/50C878/FFFFFF?text=积分兑换攻略',
    'NEWS', 
    NULL, 
    2, 
    3, 
    'ACTIVE', 
    '2023-01-01 00:00:00', 
    '2024-12-31 23:59:59', 
    156, 
    67, 
    1, 
    CURRENT_TIMESTAMP(), 
    CURRENT_TIMESTAMP()
),
(
    '浦发银行AE白金卡权益', 
    'https://via.placeholder.com/750x300/9B59B6/FFFFFF?text=浦发AE白金卡',
    'EXTERNAL', 
    'https://ecentre.spdbccc.com.cn/creditcard/indexActivity.htm', 
    NULL, 
    4, 
    'ACTIVE', 
    '2023-01-01 00:00:00', 
    '2024-12-31 23:59:59', 
    76, 
    23, 
    1, 
    CURRENT_TIMESTAMP(), 
    CURRENT_TIMESTAMP()
),
(
    '信用卡安全使用指南', 
    'https://via.placeholder.com/750x300/E74C3C/FFFFFF?text=信用卡安全指南',
    'NEWS', 
    NULL, 
    3, 
    5, 
    'ACTIVE', 
    '2023-01-01 00:00:00', 
    '2024-12-31 23:59:59', 
    203, 
    89, 
    1, 
    CURRENT_TIMESTAMP(), 
    CURRENT_TIMESTAMP()
);

-- 注意：以上密码均为加密后的值，原始密码为 'password'
-- 实际使用时，应该使用适当的密码加密方法生成密码哈希