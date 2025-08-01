package com.primehub.primecardadmin.dto;

import com.primehub.primecardadmin.entity.CardLevel;
import com.primehub.primecardadmin.entity.CardStatus;
import com.primehub.primecardadmin.entity.CardType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CreditCardDTO {
    private Long id;
    
    @NotBlank(message = "银行名称不能为空")
    private String bankName;
    
    @NotBlank(message = "卡片名称不能为空")
    private String cardName;
    
    @NotNull(message = "卡片类型不能为空")
    private CardType cardType;
    
    @NotNull(message = "卡片级别不能为空")
    private CardLevel cardLevel;
    
    private String annualFee;
    
    private List<String> benefits = new ArrayList<>();
    
    private String applyUrl;
    
    private String cardImage;
    
    private CardStatus status;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;

    // Constructors
    public CreditCardDTO() {}

    public CreditCardDTO(Long id, String bankName, String cardName, CardType cardType, 
                        CardLevel cardLevel, String annualFee, List<String> benefits, 
                        String applyUrl, String cardImage, CardStatus status, 
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.bankName = bankName;
        this.cardName = cardName;
        this.cardType = cardType;
        this.cardLevel = cardLevel;
        this.annualFee = annualFee;
        this.benefits = benefits;
        this.applyUrl = applyUrl;
        this.cardImage = cardImage;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public CardLevel getCardLevel() {
        return cardLevel;
    }

    public void setCardLevel(CardLevel cardLevel) {
        this.cardLevel = cardLevel;
    }

    public String getAnnualFee() {
        return annualFee;
    }

    public void setAnnualFee(String annualFee) {
        this.annualFee = annualFee;
    }

    public List<String> getBenefits() {
        return benefits;
    }

    public void setBenefits(List<String> benefits) {
        this.benefits = benefits;
    }

    public String getApplyUrl() {
        return applyUrl;
    }

    public void setApplyUrl(String applyUrl) {
        this.applyUrl = applyUrl;
    }

    public String getCardImage() {
        return cardImage;
    }

    public void setCardImage(String cardImage) {
        this.cardImage = cardImage;
    }

    public CardStatus getStatus() {
        return status;
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}