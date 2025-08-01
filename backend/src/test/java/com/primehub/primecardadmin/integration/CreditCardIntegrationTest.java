package com.primehub.primecardadmin.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.primehub.primecardadmin.dto.CreditCardDTO;
import com.primehub.primecardadmin.entity.CardLevel;
import com.primehub.primecardadmin.entity.CardStatus;
import com.primehub.primecardadmin.entity.CardType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class CreditCardIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testCreditCardCRUDOperations() throws Exception {
        // 1. 创建信用卡
        CreditCardDTO createRequest = new CreditCardDTO();
        createRequest.setBankName("测试银行");
        createRequest.setCardName("测试信用卡");
        createRequest.setCardType(CardType.CREDIT);
        createRequest.setCardLevel(CardLevel.GOLD);
        createRequest.setAnnualFee("300元");
        createRequest.setBenefits(Arrays.asList("积分返现", "免费洗车"));
        createRequest.setApplyUrl("https://test-bank.com/apply");
        createRequest.setStatus(CardStatus.ACTIVE);

        String createResponse = mockMvc.perform(post("/api/credit-cards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.bankName").value("测试银行"))
                .andExpect(jsonPath("$.data.cardName").value("测试信用卡"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 从响应中提取ID
        Long cardId = objectMapper.readTree(createResponse).get("data").get("id").asLong();

        // 2. 查询信用卡详情
        mockMvc.perform(get("/api/credit-cards/{id}", cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(cardId))
                .andExpect(jsonPath("$.data.bankName").value("测试银行"))
                .andExpect(jsonPath("$.data.cardName").value("测试信用卡"));

        // 3. 更新信用卡
        CreditCardDTO updateRequest = new CreditCardDTO();
        updateRequest.setId(cardId);
        updateRequest.setBankName("测试银行");
        updateRequest.setCardName("更新后的信用卡");
        updateRequest.setCardType(CardType.CREDIT);
        updateRequest.setCardLevel(CardLevel.PLATINUM);
        updateRequest.setAnnualFee("500元");
        updateRequest.setBenefits(Arrays.asList("积分返现", "免费洗车", "机场贵宾厅"));
        updateRequest.setApplyUrl("https://test-bank.com/apply");
        updateRequest.setStatus(CardStatus.ACTIVE);

        mockMvc.perform(put("/api/credit-cards/{id}", cardId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.cardName").value("更新后的信用卡"))
                .andExpect(jsonPath("$.data.cardLevel").value("PLATINUM"));

        // 4. 更新状态
        mockMvc.perform(patch("/api/credit-cards/{id}/status", cardId)
                        .with(csrf())
                        .param("status", "INACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("INACTIVE"));

        // 5. 删除信用卡
        mockMvc.perform(delete("/api/credit-cards/{id}", cardId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 6. 验证删除后无法查询
        mockMvc.perform(get("/api/credit-cards/{id}", cardId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testGetCreditCardsList() throws Exception {
        // 测试获取信用卡列表
        mockMvc.perform(get("/api/credit-cards")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").isNumber())
                .andExpect(jsonPath("$.data.totalPages").isNumber());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testGetBankNames() throws Exception {
        // 测试获取银行名称列表
        mockMvc.perform(get("/api/credit-cards/banks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testGetCardLevels() throws Exception {
        // 测试获取卡片级别列表
        mockMvc.perform(get("/api/credit-cards/levels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(username = "viewer", roles = "VIEWER")
    void testViewerPermissions() throws Exception {
        // 测试VIEWER角色只能查看，不能修改
        mockMvc.perform(get("/api/credit-cards"))
                .andExpect(status().isOk());

        // 尝试创建应该被拒绝
        CreditCardDTO createRequest = new CreditCardDTO();
        createRequest.setBankName("测试银行");
        createRequest.setCardName("测试信用卡");

        mockMvc.perform(post("/api/credit-cards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
    }
}