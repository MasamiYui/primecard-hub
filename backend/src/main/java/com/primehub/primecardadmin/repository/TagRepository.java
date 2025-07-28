package com.primehub.primecardadmin.repository;

import com.primehub.primecardadmin.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    
    Optional<Tag> findByName(String name);
    
    boolean existsByName(String name);
    
    List<Tag> findByNameContaining(String keyword);
    
    Page<Tag> findAllByOrderByUsageCountDesc(Pageable pageable);
}