package com.ternal.ternalportfolio.repository;

import com.ternal.ternalportfolio.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query(value = "SELECT p FROM Project p WHERE " +
            "(:type IS NULL OR :type = 'ALL' OR p.type = :type) AND " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            " LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(p.role) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(p.tags) LIKE LOWER(CONCAT('%', :keyword, '%')))",
            countQuery = "SELECT COUNT(p) FROM Project p WHERE " +
            "(:type IS NULL OR :type = 'ALL' OR p.type = :type) AND " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            " LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(p.role) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(p.tags) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Project> findByTypeAndKeyword(
            @Param("type") String type,
            @Param("keyword") String keyword,
            Pageable pageable);

    long countByType(String type);

    @Query("SELECT p FROM Project p WHERE (p.status != 'INACTIVE' OR p.status IS NULL) ORDER BY p.orderIndex DESC, p.id DESC")
    java.util.List<Project> findActiveProjectsForClient();
}
