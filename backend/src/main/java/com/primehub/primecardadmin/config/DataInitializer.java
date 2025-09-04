package com.primehub.primecardadmin.config;

import com.primehub.primecardadmin.entity.*;
import com.primehub.primecardadmin.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

/**
 * 数据初始化器
 * 在测试数据环境下自动插入测试数据
 */
@Component
@Profile("test-data")
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CreditCardRepository creditCardRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeData();
    }

    private void initializeData() {
        System.out.println("开始初始化测试数据...");
        
        // 创建测试用户
        createUsers();
        
        // 创建测试分类
        createCategories();
        
        // 创建测试标签
        createTags();
        
        // 创建测试信用卡
        createCreditCards();
        
        // 创建测试资讯
        createNews();
        
        System.out.println("测试数据初始化完成！");
    }

    private void createUsers() {
        if (userRepository.count() == 0) {
            System.out.println("创建测试用户...");
            
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@primecard.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(UserRole.ADMIN);
            admin.setStatus(UserStatus.ACTIVE);
            userRepository.save(admin);

            User editor = new User();
            editor.setUsername("editor");
            editor.setEmail("editor@primecard.com");
            editor.setPassword(passwordEncoder.encode("editor123"));
            editor.setRole(UserRole.EDITOR);
            editor.setStatus(UserStatus.ACTIVE);
            userRepository.save(editor);

            User viewer = new User();
            viewer.setUsername("viewer");
            viewer.setEmail("viewer@primecard.com");
            viewer.setPassword(passwordEncoder.encode("viewer123"));
            viewer.setRole(UserRole.VIEWER);
            viewer.setStatus(UserStatus.ACTIVE);
            userRepository.save(viewer);
            
            System.out.println("用户创建完成：admin, editor, viewer");
        }
    }

    private void createCategories() {
        if (categoryRepository.count() == 0) {
            System.out.println("创建测试分类...");
            
            Category category1 = new Category();
            category1.setName("信用卡资讯");
            category1.setDescription("关于信用卡的最新资讯和政策");
            category1.setStatus(CategoryStatus.ACTIVE);
            category1.setSortOrder(1);
            categoryRepository.save(category1);

            Category category2 = new Category();
            category2.setName("优惠活动");
            category2.setDescription("各银行信用卡优惠活动信息");
            category2.setStatus(CategoryStatus.ACTIVE);
            category2.setSortOrder(2);
            categoryRepository.save(category2);

            Category category3 = new Category();
            category3.setName("理财知识");
            category3.setDescription("个人理财和投资知识分享");
            category3.setStatus(CategoryStatus.ACTIVE);
            category3.setSortOrder(3);
            categoryRepository.save(category3);
            
            System.out.println("分类创建完成：信用卡资讯, 优惠活动, 理财知识");
        }
    }

    private void createTags() {
        if (tagRepository.count() == 0) {
            System.out.println("创建测试标签...");
            
            Tag tag1 = new Tag();
            tag1.setName("热门");
            tag1.setColor("#FF5722");
            tag1.setUsageCount(100);
            tagRepository.save(tag1);

            Tag tag2 = new Tag();
            tag2.setName("推荐");
            tag2.setColor("#2196F3");
            tag2.setUsageCount(80);
            tagRepository.save(tag2);

            Tag tag3 = new Tag();
            tag3.setName("新手");
            tag3.setColor("#4CAF50");
            tag3.setUsageCount(50);
            tagRepository.save(tag3);

            Tag tag4 = new Tag();
            tag4.setName("高端");
            tag4.setColor("#9C27B0");
            tag4.setUsageCount(30);
            tagRepository.save(tag4);
            
            System.out.println("标签创建完成：热门, 推荐, 新手, 高端");
        }
    }

    private void createCreditCards() {
        if (creditCardRepository.count() == 0) {
            System.out.println("创建测试信用卡...");
            
            CreditCard card1 = new CreditCard();
            card1.setBankName("招商银行");
            card1.setCardName("经典白金卡");
            card1.setCardType(CardType.CREDIT);
            card1.setCardLevel(CardLevel.PLATINUM);
            card1.setAnnualFee("3600元");
            card1.setBenefits(Arrays.asList("机场贵宾厅", "酒店优惠", "积分兑换"));
            card1.setApplyUrl("https://ccclub.cmbchina.com/");
            card1.setCardImage("/images/cmb-platinum.jpg");
            card1.setStatus(CardStatus.ACTIVE);
            creditCardRepository.save(card1);

            CreditCard card2 = new CreditCard();
            card2.setBankName("工商银行");
            card2.setCardName("宇宙星座卡");
            card2.setCardType(CardType.CREDIT);
            card2.setCardLevel(CardLevel.GOLD);
            card2.setAnnualFee("200元");
            card2.setBenefits(Arrays.asList("网购返现", "餐饮优惠", "免费洗车"));
            card2.setApplyUrl("https://mybank.icbc.com.cn/");
            card2.setCardImage("/images/icbc-cosmos.jpg");
            card2.setStatus(CardStatus.ACTIVE);
            creditCardRepository.save(card2);

            CreditCard card3 = new CreditCard();
            card3.setBankName("建设银行");
            card3.setCardName("龙卡信用卡");
            card3.setCardType(CardType.CREDIT);
            card3.setCardLevel(CardLevel.STANDARD);
            card3.setAnnualFee("免年费");
            card3.setBenefits(Arrays.asList("免年费", "积分兑换", "网点多"));
            card3.setApplyUrl("https://creditcard.ccb.com/");
            card3.setCardImage("/images/ccb-dragon.jpg");
            card3.setStatus(CardStatus.ACTIVE);
            creditCardRepository.save(card3);

            CreditCard card4 = new CreditCard();
            card4.setBankName("中国银行");
            card4.setCardName("长城环球通卡");
            card4.setCardType(CardType.CREDIT);
            card4.setCardLevel(CardLevel.GOLD);
            card4.setAnnualFee("300元");
            card4.setBenefits(Arrays.asList("境外消费返现", "免货币转换费", "全球紧急救援"));
            card4.setApplyUrl("https://www.boc.cn/");
            card4.setCardImage("/images/boc-global.jpg");
            card4.setStatus(CardStatus.ACTIVE);
            creditCardRepository.save(card4);
            
            System.out.println("信用卡创建完成：4张测试信用卡");
        }
    }

    private void createNews() {
        if (newsRepository.count() == 0) {
            System.out.println("创建测试资讯...");
            
            Category category1 = categoryRepository.findByName("信用卡资讯").orElse(null);
            Category category2 = categoryRepository.findByName("优惠活动").orElse(null);
            Category category3 = categoryRepository.findByName("理财知识").orElse(null);
            
            Tag tag1 = tagRepository.findByName("热门").orElse(null);
            Tag tag2 = tagRepository.findByName("推荐").orElse(null);
            Tag tag3 = tagRepository.findByName("新手").orElse(null);
            
            User admin = userRepository.findByUsername("admin").orElse(null);

            if (category1 != null && tag1 != null && admin != null) {
                News news1 = new News();
                news1.setTitle("2024年信用卡市场趋势分析");
                news1.setSummary("深度分析2024年信用卡市场的发展趋势和机遇，为持卡人提供专业建议");
                news1.setContent("随着数字化支付的普及，信用卡市场正在经历深刻变革。本文将从多个维度分析2024年信用卡市场的发展趋势...");
                news1.setCategory(category1);
                news1.setAuthor(admin);
                news1.setStatus(NewsStatus.PUBLISHED);
                news1.setViewCount(1500);
                news1.setPublishTime(LocalDateTime.now().minusDays(5));
                news1.setCoverImage("/images/news/market-trend-2024.jpg");
                news1.setTags(new HashSet<>(Arrays.asList(tag1, tag2)));
                newsRepository.save(news1);
            }

            if (category2 != null && tag2 != null && admin != null) {
                News news2 = new News();
                news2.setTitle("招商银行信用卡双十一优惠活动");
                news2.setSummary("招商银行推出双十一专属优惠，最高返现20%，多重福利等你来享");
                news2.setContent("招商银行为庆祝双十一购物节，特推出信用卡专属优惠活动。活动期间，使用招商银行信用卡消费可享受多重优惠...");
                news2.setCategory(category2);
                news2.setAuthor(admin);
                news2.setStatus(NewsStatus.PUBLISHED);
                news2.setViewCount(2300);
                news2.setPublishTime(LocalDateTime.now().minusDays(3));
                news2.setCoverImage("/images/news/cmb-double11.jpg");
                news2.setTags(new HashSet<>(Arrays.asList(tag1, tag2)));
                newsRepository.save(news2);
            }

            if (category3 != null && tag3 != null && admin != null) {
                News news3 = new News();
                news3.setTitle("如何选择适合自己的信用卡");
                news3.setSummary("详细指南教你如何根据个人需求选择最适合的信用卡，避免踩坑");
                news3.setContent("选择信用卡是一个重要决定，需要考虑多个因素。本文将为新手用户提供全面的选卡指南...");
                news3.setCategory(category3);
                news3.setAuthor(admin);
                news3.setStatus(NewsStatus.PUBLISHED);
                news3.setViewCount(890);
                news3.setPublishTime(LocalDateTime.now().minusDays(7));
                news3.setCoverImage("/images/news/choose-card-guide.jpg");
                news3.setTags(new HashSet<>(Arrays.asList(tag3)));
                newsRepository.save(news3);
            }

            if (category1 != null && admin != null) {
                News news4 = new News();
                news4.setTitle("信用卡安全使用指南");
                news4.setSummary("保护您的信用卡安全，防范各种诈骗和盗刷风险");
                news4.setContent("随着信用卡使用的普及，相关的安全风险也在增加。本文将介绍如何安全使用信用卡...");
                news4.setCategory(category1);
                news4.setAuthor(admin);
                news4.setStatus(NewsStatus.DRAFT);
                news4.setViewCount(0);
                news4.setCoverImage("/images/news/card-security.jpg");
                newsRepository.save(news4);
            }
            
            System.out.println("资讯创建完成：4篇测试资讯");
        }
    }
}