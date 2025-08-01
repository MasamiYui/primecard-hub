package com.primehub.primecardadmin.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class CategoryOrderDTO {

    @NotNull(message = "分类ID不能为空")
    private Long id;
    
    @NotNull(message = "排序值不能为空")
    @Min(value = 0, message = "排序值必须大于等于0")
    private Integer order;

    // Constructors
    public CategoryOrderDTO() {}

    public CategoryOrderDTO(Long id, Integer order) {
        this.id = id;
        this.order = order;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}