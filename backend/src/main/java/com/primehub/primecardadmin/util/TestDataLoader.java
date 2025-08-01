package com.primehub.primecardadmin.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 测试数据加载工具类
 * 仅在开发环境中使用，用于自动加载测试数据
 * 使用方法：启动应用时添加命令行参数 --spring.profiles.active=dev,test-data
 */
@Component
@Profile("test-data")
public class TestDataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(TestDataLoader.class);

    private final DataSource dataSource;

    @Autowired
    public TestDataLoader(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) {
        logger.info("开始加载测试数据...");
        try {
            // 检查是否需要确认
            boolean shouldProceed = true;
            if (System.console() != null) {
                System.out.print("警告：即将加载测试数据，这将可能覆盖现有数据。是否继续？(y/n): ");
                String input = System.console().readLine();
                shouldProceed = "y".equalsIgnoreCase(input) || "yes".equalsIgnoreCase(input);
            }

            if (shouldProceed) {
                // 执行SQL脚本
                ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
                populator.addScript(new ClassPathResource("test-data.sql"));
                populator.execute(dataSource);

                logger.info("测试数据加载成功！");
                logger.info("可用测试账号：");
                logger.info("管理员账号: admin@primehub.com 密码: password");
                logger.info("编辑账号: editor@primehub.com 密码: password");
                logger.info("查看账号: viewer@primehub.com 密码: password");
            } else {
                logger.info("用户取消了测试数据加载操作。");
            }
        } catch (Exception e) {
            logger.error("加载测试数据时发生错误", e);
        }
    }

    /**
     * 手动触发测试数据加载的方法
     * 可以在需要时通过API或其他方式调用此方法加载测试数据
     * 
     * @return 加载结果消息
     */
    public String loadTestData() {
        try {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("test-data.sql"));
            populator.execute(dataSource);
            return "测试数据加载成功！";
        } catch (Exception e) {
            logger.error("手动加载测试数据时发生错误", e);
            return "测试数据加载失败：" + e.getMessage();
        }
    }
}