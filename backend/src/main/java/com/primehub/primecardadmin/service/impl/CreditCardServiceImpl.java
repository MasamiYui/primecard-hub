package com.primehub.primecardadmin.service.impl;

import com.primehub.primecardadmin.dto.CreditCardDTO;
import com.primehub.primecardadmin.dto.PageResponseDTO;
import com.primehub.primecardadmin.entity.CardLevel;
import com.primehub.primecardadmin.entity.CardStatus;
import com.primehub.primecardadmin.entity.CardType;
import com.primehub.primecardadmin.entity.CreditCard;
import com.primehub.primecardadmin.exception.ResourceNotFoundException;
import com.primehub.primecardadmin.repository.CreditCardRepository;
import com.primehub.primecardadmin.service.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CreditCardServiceImpl implements CreditCardService {

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Override
    public PageResponseDTO<CreditCardDTO> getAllCreditCards(int page, int size, String keyword, 
                                                           String bankName, String cardType, 
                                                           CardLevel cardLevel, CardStatus status) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Specification<CreditCard> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 关键词搜索
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likeKeyword = "%" + keyword.trim() + "%";
                Predicate cardNamePredicate = criteriaBuilder.like(root.get("cardName"), likeKeyword);
                Predicate bankNamePredicate = criteriaBuilder.like(root.get("bankName"), likeKeyword);
                predicates.add(criteriaBuilder.or(cardNamePredicate, bankNamePredicate));
            }
            
            // 银行名称
            if (bankName != null && !bankName.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("bankName"), bankName));
            }
            
            // 卡片类型
            if (cardType != null && !cardType.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("cardType"), CardType.valueOf(cardType)));
            }
            
            // 卡片级别
            if (cardLevel != null) {
                predicates.add(criteriaBuilder.equal(root.get("cardLevel"), cardLevel));
            }
            
            // 卡片状态
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<CreditCard> creditCardPage = creditCardRepository.findAll(spec, pageable);
        List<CreditCardDTO> creditCardDTOs = creditCardPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageResponseDTO<>(
                creditCardDTOs,
                creditCardPage.getNumber(),
                creditCardPage.getSize(),
                creditCardPage.getTotalElements(),
                creditCardPage.getTotalPages()
        );
    }

    @Override
    public CreditCardDTO getCreditCardById(Long id) {
        CreditCard creditCard = creditCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("信用卡不存在"));
        return convertToDTO(creditCard);
    }

    @Override
    @Transactional
    public CreditCardDTO createCreditCard(CreditCardDTO creditCardDTO) {
        CreditCard creditCard = convertToEntity(creditCardDTO);
        CreditCard savedCreditCard = creditCardRepository.save(creditCard);
        return convertToDTO(savedCreditCard);
    }

    @Override
    @Transactional
    public CreditCardDTO updateCreditCard(Long id, CreditCardDTO creditCardDTO) {
        CreditCard existingCreditCard = creditCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("信用卡不存在"));
        
        // 更新信用卡信息
        existingCreditCard.setCardName(creditCardDTO.getCardName());
        existingCreditCard.setBankName(creditCardDTO.getBankName());
        existingCreditCard.setCardType(creditCardDTO.getCardType());
        existingCreditCard.setCardLevel(creditCardDTO.getCardLevel());
        existingCreditCard.setAnnualFee(creditCardDTO.getAnnualFee());
        existingCreditCard.setBenefits(creditCardDTO.getBenefits());
        existingCreditCard.setApplyUrl(creditCardDTO.getApplyUrl());
        existingCreditCard.setCardImage(creditCardDTO.getCardImage());
        existingCreditCard.setStatus(creditCardDTO.getStatus());
        
        CreditCard updatedCreditCard = creditCardRepository.save(existingCreditCard);
        return convertToDTO(updatedCreditCard);
    }

    @Override
    @Transactional
    public void deleteCreditCard(Long id) {
        CreditCard creditCard = creditCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("信用卡不存在"));
        creditCardRepository.delete(creditCard);
    }

    @Override
    @Transactional
    public CreditCardDTO updateCreditCardStatus(Long id, CardStatus status) {
        CreditCard creditCard = creditCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("信用卡不存在"));
        
        creditCard.setStatus(status);
        
        CreditCard updatedCreditCard = creditCardRepository.save(creditCard);
        return convertToDTO(updatedCreditCard);
    }

    @Override
    public List<String> getAllBankNames() {
        return creditCardRepository.findAllBankNames();
    }

    @Override
    public List<String> getAllCardTypes() {
        return Arrays.stream(CardType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public List<CardLevel> getAllCardLevels() {
        return Arrays.asList(CardLevel.values());
    }

    @Override
    public PageResponseDTO<CreditCardDTO> getCreditCardsByBank(String bankName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<CreditCard> creditCardPage = creditCardRepository.findByStatus(CardStatus.ACTIVE, pageable);
        
        // 过滤银行名称
        List<CreditCard> filteredCards = creditCardPage.getContent().stream()
                .filter(card -> card.getBankName().equals(bankName))
                .collect(Collectors.toList());
        
        List<CreditCardDTO> creditCardDTOs = filteredCards.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageResponseDTO<>(
                creditCardDTOs,
                creditCardPage.getNumber(),
                creditCardPage.getSize(),
                creditCardPage.getTotalElements(),
                creditCardPage.getTotalPages()
        );
    }

    @Override
    public PageResponseDTO<CreditCardDTO> getCreditCardsByType(String cardType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        CardType type = CardType.valueOf(cardType);
        Page<CreditCard> creditCardPage = creditCardRepository.findByCardType(type, pageable);
        
        List<CreditCardDTO> creditCardDTOs = creditCardPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageResponseDTO<>(
                creditCardDTOs,
                creditCardPage.getNumber(),
                creditCardPage.getSize(),
                creditCardPage.getTotalElements(),
                creditCardPage.getTotalPages()
        );
    }

    @Override
    public PageResponseDTO<CreditCardDTO> getCreditCardsByLevel(CardLevel cardLevel, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<CreditCard> creditCardPage = creditCardRepository.findByCardLevel(cardLevel, pageable);
        
        List<CreditCardDTO> creditCardDTOs = creditCardPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageResponseDTO<>(
                creditCardDTOs,
                creditCardPage.getNumber(),
                creditCardPage.getSize(),
                creditCardPage.getTotalElements(),
                creditCardPage.getTotalPages()
        );
    }
    
    // 辅助方法：将实体转换为DTO
    private CreditCardDTO convertToDTO(CreditCard creditCard) {
        CreditCardDTO dto = new CreditCardDTO();
        dto.setId(creditCard.getId());
        dto.setCardName(creditCard.getCardName());
        dto.setBankName(creditCard.getBankName());
        dto.setCardType(creditCard.getCardType());
        dto.setCardLevel(creditCard.getCardLevel());
        dto.setAnnualFee(creditCard.getAnnualFee());
        dto.setBenefits(creditCard.getBenefits());
        dto.setApplyUrl(creditCard.getApplyUrl());
        dto.setCardImage(creditCard.getCardImage());
        dto.setStatus(creditCard.getStatus());
        dto.setCreatedAt(creditCard.getCreatedAt());
        dto.setUpdatedAt(creditCard.getUpdatedAt());
        return dto;
    }
    
    // 辅助方法：将DTO转换为实体
    private CreditCard convertToEntity(CreditCardDTO dto) {
        CreditCard creditCard = new CreditCard();
        creditCard.setId(dto.getId());
        creditCard.setCardName(dto.getCardName());
        creditCard.setBankName(dto.getBankName());
        creditCard.setCardType(dto.getCardType());
        creditCard.setCardLevel(dto.getCardLevel());
        creditCard.setAnnualFee(dto.getAnnualFee());
        creditCard.setBenefits(dto.getBenefits());
        creditCard.setApplyUrl(dto.getApplyUrl());
        creditCard.setCardImage(dto.getCardImage());
        creditCard.setStatus(dto.getStatus() != null ? dto.getStatus() : CardStatus.ACTIVE);
        return creditCard;
    }
}