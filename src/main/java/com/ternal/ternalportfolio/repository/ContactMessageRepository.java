package com.ternal.ternalportfolio.repository;

import com.ternal.ternalportfolio.entity.ContactMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

    @Query(value = "SELECT m FROM ContactMessage m WHERE " +
            "(:status IS NULL OR :status = 'ALL' OR m.status = :status) AND " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            " LOWER(m.senderName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(m.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(m.subject) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(m.message) LIKE LOWER(CONCAT('%', :keyword, '%')))",
            countQuery = "SELECT COUNT(m) FROM ContactMessage m WHERE " +
            "(:status IS NULL OR :status = 'ALL' OR m.status = :status) AND " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            " LOWER(m.senderName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(m.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(m.subject) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(m.message) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<ContactMessage> findByStatusAndKeyword(
            @Param("status") String status,
            @Param("keyword") String keyword,
            Pageable pageable);

    long countByStatus(String status);
}
