package com.primehub.primecardadmin.dto;

import com.primehub.primecardadmin.entity.CardLevel;
import com.primehub.primecardadmin.entity.CardStatus;
import com.primehub.primecardadmin.entity.CardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}