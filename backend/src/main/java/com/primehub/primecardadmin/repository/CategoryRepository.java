package com.primehub.primecardadmin.repository;

import com.primehub.primecardadmin.entity.Category;
import com.primehub.primecardadmin.entity.CategoryStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    List<Category> findByStatus(CategoryStatus status, Sort sort);
    
    Optional<Category> findByName(String name);
    
    boolean existsByName(String name);
}