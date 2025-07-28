package com.primehub.primecardadmin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryOrderDTO {

    @NotNull(message = "分类ID不能为空")
    private Long id;
    
    @NotNull(message = "排序值不能为空")
    @Min(value = 0, message = "排序值必须大于等于0")
    private Integer order;
}