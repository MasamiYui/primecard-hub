package com.primehub.primecardadmin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.primehub.primecardadmin.dto.CreditCardDTO;
import com.primehub.primecardadmin.entity.CardLevel;
import com.primehub.primecardadmin.entity.CardStatus;
import com.primehub.primecardadmin.service.CreditCardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CreditCardController.class)
class CreditCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreditCardService creditCardService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllBankNames() throws Exception {
        // 准备测试数据
        List<String> bankNames = Arrays.asList("招商银行", "工商银行", "建设银行");
        when(creditCardService.getAllBankNames()).thenReturn(bankNames);

        // 执行测试
        mockMvc.perform(get("/api/credit-cards/banks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取银行列表成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0]").value("招商银行"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllCardLevels() throws Exception {
        // 准备测试数据
        List<CardLevel> cardLevels = Arrays.asList(CardLevel.STANDARD, CardLevel.GOLD, CardLevel.PLATINUM);
        when(creditCardService.getAllCardLevels()).thenReturn(cardLevels);

        // 执行测试
        mockMvc.perform(get("/api/credit-cards/levels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取卡片级别列表成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateCreditCard() throws Exception {
        // 准备测试数据
        CreditCardDTO creditCardDTO = new CreditCardDTO();
        creditCardDTO.setBankName("测试银行");
        creditCardDTO.setCardName("测试卡片");
        creditCardDTO.setCardLevel(CardLevel.GOLD);
        creditCardDTO.setAnnualFee("200元");
        creditCardDTO.setApplyUrl("https://test.com");
        creditCardDTO.setStatus(CardStatus.ACTIVE);

        CreditCardDTO savedCreditCard = new CreditCardDTO();
        savedCreditCard.setId(1L);
        savedCreditCard.setBankName("测试银行");
        savedCreditCard.setCardName("测试卡片");
        savedCreditCard.setCardLevel(CardLevel.GOLD);
        savedCreditCard.setAnnualFee("200元");
        savedCreditCard.setApplyUrl("https://test.com");
        savedCreditCard.setStatus(CardStatus.ACTIVE);

        when(creditCardService.createCreditCard(any(CreditCardDTO.class))).thenReturn(savedCreditCard);

        // 执行测试
        mockMvc.perform(post("/api/credit-cards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creditCardDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("创建信用卡成功"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.bankName").value("测试银行"))
                .andExpect(jsonPath("$.data.cardName").value("测试卡片"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateCreditCardStatus() throws Exception {
        // 准备测试数据
        Long cardId = 1L;
        CardStatus newStatus = CardStatus.INACTIVE;
        
        CreditCardDTO updatedCard = new CreditCardDTO();
        updatedCard.setId(cardId);
        updatedCard.setBankName("测试银行");
        updatedCard.setCardName("测试卡片");
        updatedCard.setStatus(newStatus);

        when(creditCardService.updateCreditCardStatus(cardId, newStatus)).thenReturn(updatedCard);

        // 执行测试
        mockMvc.perform(patch("/api/credit-cards/{id}/status", cardId)
                        .with(csrf())
                        .param("status", newStatus.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("更新信用卡状态成功"))
                .andExpect(jsonPath("$.data.status").value("INACTIVE"));
    }

    @Test
    @WithMockUser(roles = "VIEWER")
    void testUnauthorizedAccess() throws Exception {
        // 测试权限不足的情况
        mockMvc.perform(post("/api/credit-cards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUnauthenticatedAccess() throws Exception {
        // 测试未认证的情况
        mockMvc.perform(get("/api/credit-cards/banks"))
                .andExpect(status().isUnauthorized());
    }
}