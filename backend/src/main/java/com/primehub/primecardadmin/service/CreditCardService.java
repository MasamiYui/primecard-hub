package com.primehub.primecardadmin.service;

import com.primehub.primecardadmin.dto.CreditCardDTO;
import com.primehub.primecardadmin.dto.PageResponseDTO;
import com.primehub.primecardadmin.entity.CardLevel;
import com.primehub.primecardadmin.entity.CardStatus;

import java.util.List;

public interface CreditCardService {

    /**
     * 获取所有信用卡（分页）
     *
     * @param page      页码
     * @param size      每页大小
     * @param keyword   关键词搜索
     * @param bankName  银行名称
     * @param cardType  卡片类型
     * @param cardLevel 卡片级别
     * @param status    卡片状态
     * @return 信用卡分页结果
     */
    PageResponseDTO<CreditCardDTO> getAllCreditCards(int page, int size, String keyword, 
                                                   String bankName, String cardType, 
                                                   CardLevel cardLevel, CardStatus status);

    /**
     * 根据ID获取信用卡
     *
     * @param id 信用卡ID
     * @return 信用卡DTO
     */
    CreditCardDTO getCreditCardById(Long id);

    /**
     * 创建信用卡
     *
     * @param creditCardDTO 信用卡DTO
     * @return 创建的信用卡DTO
     */
    CreditCardDTO createCreditCard(CreditCardDTO creditCardDTO);

    /**
     * 更新信用卡
     *
     * @param id            信用卡ID
     * @param creditCardDTO 信用卡DTO
     * @return 更新后的信用卡DTO
     */
    CreditCardDTO updateCreditCard(Long id, CreditCardDTO creditCardDTO);

    /**
     * 删除信用卡
     *
     * @param id 信用卡ID
     */
    void deleteCreditCard(Long id);

    /**
     * 更新信用卡状态
     *
     * @param id     信用卡ID
     * @param status 新状态
     * @return 更新后的信用卡DTO
     */
    CreditCardDTO updateCreditCardStatus(Long id, CardStatus status);

    /**
     * 获取所有银行名称
     *
     * @return 银行名称列表
     */
    List<String> getAllBankNames();

    /**
     * 获取所有卡片类型
     *
     * @return 卡片类型列表
     */
    List<String> getAllCardTypes();

    /**
     * 获取所有卡片级别
     *
     * @return 卡片级别列表
     */
    List<CardLevel> getAllCardLevels();

    /**
     * 根据银行名称获取信用卡
     *
     * @param bankName 银行名称
     * @param page     页码
     * @param size     每页大小
     * @return 信用卡分页结果
     */
    PageResponseDTO<CreditCardDTO> getCreditCardsByBank(String bankName, int page, int size);

    /**
     * 根据卡片类型获取信用卡
     *
     * @param cardType 卡片类型
     * @param page     页码
     * @param size     每页大小
     * @return 信用卡分页结果
     */
    PageResponseDTO<CreditCardDTO> getCreditCardsByType(String cardType, int page, int size);

    /**
     * 根据卡片级别获取信用卡
     *
     * @param cardLevel 卡片级别
     * @param page      页码
     * @param size      每页大小
     * @return 信用卡分页结果
     */
    PageResponseDTO<CreditCardDTO> getCreditCardsByLevel(CardLevel cardLevel, int page, int size);
}