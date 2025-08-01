package com.primehub.primecardadmin.service;

import com.primehub.primecardadmin.dto.CreditCardDTO;
import com.primehub.primecardadmin.dto.PageResponseDTO;
import com.primehub.primecardadmin.entity.CreditCard;
import com.primehub.primecardadmin.entity.CardLevel;
import com.primehub.primecardadmin.entity.CardStatus;
import com.primehub.primecardadmin.entity.CardType;
import com.primehub.primecardadmin.exception.ResourceNotFoundException;
import com.primehub.primecardadmin.repository.CreditCardRepository;
import com.primehub.primecardadmin.service.impl.CreditCardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditCardServiceTest {

    @Mock
    private CreditCardRepository creditCardRepository;

    @InjectMocks
    private CreditCardServiceImpl creditCardService;

    private CreditCard testCreditCard;
    private CreditCardDTO creditCardDTO;

    @BeforeEach
    void setUp() {
        testCreditCard = new CreditCard();
        testCreditCard.setId(1L);
        testCreditCard.setBankName("Test Bank");
        testCreditCard.setCreatedAt(LocalDateTime.now());
        testCreditCard.setUpdatedAt(LocalDateTime.now());

        creditCardDTO = new CreditCardDTO();
        creditCardDTO.setId(1L);
        creditCardDTO.setBankName("Test Bank");
    }

    @Test
    void getAllCreditCards_Success() {
        // Given
        List<CreditCard> creditCards = Arrays.asList(testCreditCard);
        Page<CreditCard> creditCardPage = new PageImpl<>(creditCards);
        when(creditCardRepository.findAll(any(Specification.class), any(org.springframework.data.domain.Pageable.class))).thenReturn(creditCardPage);

        // When
        PageResponseDTO<CreditCardDTO> result = creditCardService.getAllCreditCards(0, 10, null, null, null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Test Bank", result.getContent().get(0).getBankName());
    }

    @Test
    void getCreditCardById_Success() {
        // Given
        when(creditCardRepository.findById(1L)).thenReturn(Optional.of(testCreditCard));

        // When
        CreditCardDTO result = creditCardService.getCreditCardById(1L);

        // Then
        assertNotNull(result);
        assertEquals("Test Bank", result.getBankName());
    }

    @Test
    void getCreditCardById_NotFound_ThrowsException() {
        // Given
        when(creditCardRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            creditCardService.getCreditCardById(1L);
        });
    }

    @Test
    void createCreditCard_Success() {
        // Given
        when(creditCardRepository.save(any(CreditCard.class))).thenReturn(testCreditCard);

        // When
        CreditCardDTO result = creditCardService.createCreditCard(creditCardDTO);

        // Then
        assertNotNull(result);
        assertEquals("Test Bank", result.getBankName());
        verify(creditCardRepository).save(any(CreditCard.class));
    }

    @Test
    void updateCreditCard_Success() {
        // Given
        CreditCardDTO updateDTO = new CreditCardDTO();
        updateDTO.setBankName("Updated Bank");

        CreditCard updatedCard = new CreditCard();
        updatedCard.setId(1L);
        updatedCard.setBankName("Updated Bank");

        when(creditCardRepository.findById(1L)).thenReturn(Optional.of(testCreditCard));
        when(creditCardRepository.save(any(CreditCard.class))).thenReturn(updatedCard);

        // When
        CreditCardDTO result = creditCardService.updateCreditCard(1L, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals("Updated Bank", result.getBankName());
    }

    @Test
    void updateCreditCard_NotFound_ThrowsException() {
        // Given
        when(creditCardRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            creditCardService.updateCreditCard(1L, creditCardDTO);
        });
    }

    @Test
    void deleteCreditCard_Success() {
        // Given
        when(creditCardRepository.findById(1L)).thenReturn(Optional.of(testCreditCard));

        // When
        creditCardService.deleteCreditCard(1L);

        // Then
        verify(creditCardRepository).delete(testCreditCard);
    }

    @Test
    void deleteCreditCard_NotFound_ThrowsException() {
        // Given
        when(creditCardRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            creditCardService.deleteCreditCard(1L);
        });
    }

    @Test
    void updateCreditCardStatus_Success() {
        // Given
        CreditCard updatedCard = new CreditCard();
        updatedCard.setId(1L);
        updatedCard.setBankName("Test Bank");

        when(creditCardRepository.findById(1L)).thenReturn(Optional.of(testCreditCard));
        when(creditCardRepository.save(any(CreditCard.class))).thenReturn(updatedCard);

        // When
        CreditCardDTO result = creditCardService.updateCreditCardStatus(1L, CardStatus.INACTIVE);

        // Then
        assertNotNull(result);
        verify(creditCardRepository).save(any(CreditCard.class));
    }

    @Test
    void updateCreditCardStatus_NotFound_ThrowsException() {
        // Given
        when(creditCardRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            creditCardService.updateCreditCardStatus(1L, CardStatus.INACTIVE);
        });
    }
}