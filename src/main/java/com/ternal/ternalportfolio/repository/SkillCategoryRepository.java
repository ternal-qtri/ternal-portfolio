package com.ternal.ternalportfolio.repository;

import com.ternal.ternalportfolio.entity.SkillCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillCategoryRepository extends JpaRepository<SkillCategory, Long> {

    @Query("SELECT DISTINCT c FROM SkillCategory c LEFT JOIN FETCH c.skills s ORDER BY c.orderIndex ASC, c.id ASC")
    List<SkillCategory> findAllWithSkills();
}
