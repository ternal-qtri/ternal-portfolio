package com.ternal.ternalportfolio.repository;

import com.ternal.ternalportfolio.entity.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    boolean existsByCategoryId(Long categoryId);

    @Query(value = "SELECT s FROM Skill s LEFT JOIN FETCH s.category WHERE " +
           "(:categoryId IS NULL OR s.category.id = :categoryId) AND " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')))",
           countQuery = "SELECT count(s) FROM Skill s WHERE " +
           "(:categoryId IS NULL OR s.category.id = :categoryId) AND " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Skill> findByCategoryAndKeyword(
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,
            Pageable pageable);
}
