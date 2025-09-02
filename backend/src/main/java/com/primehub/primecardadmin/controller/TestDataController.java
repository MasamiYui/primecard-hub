package com.primehub.primecardadmin.controller;

import com.primehub.primecardadmin.util.TestDataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试数据控制器
 * 仅在开发环境中使用，用于通过API加载测试数据
 */
@RestController
@RequestMapping("/api/test-data")
@Profile({"test-data", "test"})
public class TestDataController {

    private final TestDataLoader testDataLoader;

    @Autowired
    public TestDataController(TestDataLoader testDataLoader) {
        this.testDataLoader = testDataLoader;
    }

    /**
     * 加载测试数据的API端点
     * 注意：此端点仅在开发和测试环境中可用
     * 
     * @return 加载结果消息
     */
    @PostMapping("/load")
    public ResponseEntity<String> loadTestData() {
        String result = testDataLoader.loadTestData();
        return ResponseEntity.ok(result);
    }
}