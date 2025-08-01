package com.primehub.primecardadmin.controller;

import com.primehub.primecardadmin.dto.ApiResponseDTO;
import com.primehub.primecardadmin.dto.CreditCardDTO;
import com.primehub.primecardadmin.dto.PageResponseDTO;
import com.primehub.primecardadmin.entity.CardLevel;
import com.primehub.primecardadmin.entity.CardStatus;
import com.primehub.primecardadmin.service.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/credit-cards")
public class CreditCardController {

    @Autowired
    private CreditCardService creditCardService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<PageResponseDTO<CreditCardDTO>>> getAllCreditCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String bankName,
            @RequestParam(required = false) String cardType,
            @RequestParam(required = false) CardLevel cardLevel,
            @RequestParam(required = false) CardStatus status) {
        
        PageResponseDTO<CreditCardDTO> creditCards = creditCardService.getAllCreditCards(
                page, size, keyword, bankName, cardType, cardLevel, status);
        
        return ResponseEntity.ok(ApiResponseDTO.success("获取信用卡列表成功", creditCards));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<CreditCardDTO>> getCreditCardById(@PathVariable Long id) {
        CreditCardDTO creditCardDTO = creditCardService.getCreditCardById(id);
        return ResponseEntity.ok(ApiResponseDTO.success("获取信用卡详情成功", creditCardDTO));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<CreditCardDTO>> createCreditCard(@Valid @RequestBody CreditCardDTO creditCardDTO) {
        CreditCardDTO createdCreditCard = creditCardService.createCreditCard(creditCardDTO);
        return ResponseEntity.ok(ApiResponseDTO.success("创建信用卡成功", createdCreditCard));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<CreditCardDTO>> updateCreditCard(
            @PathVariable Long id,
            @Valid @RequestBody CreditCardDTO creditCardDTO) {
        CreditCardDTO updatedCreditCard = creditCardService.updateCreditCard(id, creditCardDTO);
        return ResponseEntity.ok(ApiResponseDTO.success("更新信用卡成功", updatedCreditCard));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Void>> deleteCreditCard(@PathVariable Long id) {
        creditCardService.deleteCreditCard(id);
        return ResponseEntity.ok(ApiResponseDTO.success("删除信用卡成功"));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<CreditCardDTO>> updateCreditCardStatus(
            @PathVariable Long id,
            @RequestParam CardStatus status) {
        CreditCardDTO updatedCreditCard = creditCardService.updateCreditCardStatus(id, status);
        return ResponseEntity.ok(ApiResponseDTO.success("更新信用卡状态成功", updatedCreditCard));
    }

    @GetMapping("/banks")
    public ResponseEntity<ApiResponseDTO<List<String>>> getAllBankNames() {
        List<String> bankNames = creditCardService.getAllBankNames();
        return ResponseEntity.ok(ApiResponseDTO.success("获取银行列表成功", bankNames));
    }

    @GetMapping("/types")
    public ResponseEntity<ApiResponseDTO<List<String>>> getAllCardTypes() {
        List<String> cardTypes = creditCardService.getAllCardTypes();
        return ResponseEntity.ok(ApiResponseDTO.success("获取卡片类型列表成功", cardTypes));
    }

    @GetMapping("/levels")
    public ResponseEntity<ApiResponseDTO<List<CardLevel>>> getAllCardLevels() {
        List<CardLevel> cardLevels = creditCardService.getAllCardLevels();
        return ResponseEntity.ok(ApiResponseDTO.success("获取卡片级别列表成功", cardLevels));
    }
}