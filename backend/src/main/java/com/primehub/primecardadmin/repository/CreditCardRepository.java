package com.primehub.primecardadmin.repository;

import com.primehub.primecardadmin.entity.CardLevel;
import com.primehub.primecardadmin.entity.CardStatus;
import com.primehub.primecardadmin.entity.CardType;
import com.primehub.primecardadmin.entity.CreditCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {
    
    Page<CreditCard> findByStatus(CardStatus status, Pageable pageable);
    
    List<CreditCard> findByBankName(String bankName);
    
    Page<CreditCard> findByCardType(CardType cardType, Pageable pageable);
    
    Page<CreditCard> findByCardLevel(CardLevel cardLevel, Pageable pageable);
    
    @Query("SELECT c FROM CreditCard c WHERE c.bankName LIKE %?1% OR c.cardName LIKE %?1%")
    Page<CreditCard> searchByKeyword(String keyword, Pageable pageable);
    
    @Query("SELECT DISTINCT c.bankName FROM CreditCard c ORDER BY c.bankName")
    List<String> findAllBankNames();
}