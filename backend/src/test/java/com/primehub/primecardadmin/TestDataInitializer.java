package com.primehub.primecardadmin;

import com.primehub.primecardadmin.entity.*;
import com.primehub.primecardadmin.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 测试数据初始化器
 * 仅在test profile下运行
 */
@Component
@Profile("test")
public class TestDataInitializer implements CommandLineRunner {

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
        initializeTestData();
    }

    private void initializeTestData() {
        // 创建测试用户
        createTestUsers();
        
        // 创建测试分类
        createTestCategories();
        
        // 创建测试标签
        createTestTags();
        
        // 创建测试信用卡
        createTestCreditCards();
        
        // 创建测试资讯
        createTestNews();
    }

    private void createTestUsers() {
        if (userRepository.count() == 0) {
            User admin = new User("admin", "admin@primecard.com", passwordEncoder.encode("admin123"), UserRole.ADMIN);
            admin.setStatus(UserStatus.ACTIVE);
            userRepository.save(admin);

            User user1 = new User("user1", "user1@example.com", passwordEncoder.encode("user123"), UserRole.VIEWER);
            user1.setStatus(UserStatus.ACTIVE);
            userRepository.save(user1);

            User user2 = new User("user2", "user2@example.com", passwordEncoder.encode("user123"), UserRole.VIEWER);
            user2.setStatus(UserStatus.INACTIVE);
            userRepository.save(user2);
        }
    }

    private void createTestCategories() {
        if (categoryRepository.count() == 0) {
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
            category3.setStatus(CategoryStatus.INACTIVE);
            category3.setSortOrder(3);
            categoryRepository.save(category3);
        }
    }

    private void createTestTags() {
        if (tagRepository.count() == 0) {
            Tag tag1 = new Tag("热门", "#FF5722");
            tag1.setUsageCount(100);
            tagRepository.save(tag1);

            Tag tag2 = new Tag("推荐", "#2196F3");
            tag2.setUsageCount(80);
            tagRepository.save(tag2);

            Tag tag3 = new Tag("新手", "#4CAF50");
            tag3.setUsageCount(50);
            tagRepository.save(tag3);

            Tag tag4 = new Tag("高端", "#9C27B0");
            tag4.setUsageCount(30);
            tagRepository.save(tag4);
        }
    }

    private void createTestCreditCards() {
        if (creditCardRepository.count() == 0) {
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
            card3.setCardName("龙卡");
            card3.setCardType(CardType.CREDIT);
            card3.setCardLevel(CardLevel.STANDARD);
            card3.setAnnualFee("免年费");
            card3.setBenefits(Arrays.asList("免年费", "积分兑换", "网点多"));
            card3.setApplyUrl("https://creditcard.ccb.com/");
            card3.setCardImage("/images/ccb-dragon.jpg");
            card3.setStatus(CardStatus.INACTIVE);
            creditCardRepository.save(card3);
        }
    }

    private void createTestNews() {
        if (newsRepository.count() == 0) {
            Category category1 = categoryRepository.findByName("信用卡资讯").orElse(null);
            Category category2 = categoryRepository.findByName("优惠活动").orElse(null);
            Tag tag1 = tagRepository.findByName("热门").orElse(null);
            Tag tag2 = tagRepository.findByName("推荐").orElse(null);
            User admin = userRepository.findByUsername("admin").orElse(null);

            if (category1 != null && tag1 != null && admin != null) {
                News news1 = new News();
                news1.setTitle("2024年信用卡市场趋势分析");
                news1.setSummary("深度分析2024年信用卡市场的发展趋势和机遇");
                news1.setContent("随着数字化支付的普及，信用卡市场正在经历深刻变革...");
                news1.setCategory(category1);
                news1.setAuthor(admin);
                news1.setStatus(NewsStatus.PUBLISHED);
                news1.setViewCount(1500);
                news1.setPublishTime(LocalDateTime.now().minusDays(5));
                newsRepository.save(news1);
            }

            if (category2 != null && tag2 != null && admin != null) {
                News news2 = new News();
                news2.setTitle("招商银行信用卡双十一优惠活动");
                news2.setSummary("招商银行推出双十一专属优惠，最高返现20%");
                news2.setContent("招商银行为庆祝双十一购物节，特推出信用卡专属优惠活动...");
                news2.setCategory(category2);
                news2.setAuthor(admin);
                news2.setStatus(NewsStatus.PUBLISHED);
                news2.setViewCount(2300);
                news2.setPublishTime(LocalDateTime.now().minusDays(3));
                newsRepository.save(news2);
            }

            if (category1 != null && admin != null) {
                News news3 = new News();
                news3.setTitle("如何选择适合自己的信用卡");
                news3.setSummary("详细指南教你如何根据个人需求选择最适合的信用卡");
                news3.setContent("选择信用卡是一个重要决定，需要考虑多个因素...");
                news3.setCategory(category1);
                news3.setAuthor(admin);
                news3.setStatus(NewsStatus.DRAFT);
                news3.setViewCount(0);
                newsRepository.save(news3);
            }
        }
    }
}