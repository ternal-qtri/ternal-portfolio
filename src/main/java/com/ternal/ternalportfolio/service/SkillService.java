package com.ternal.ternalportfolio.service;

import com.ternal.ternalportfolio.entity.Skill;
import com.ternal.ternalportfolio.entity.SkillCategory;
import com.ternal.ternalportfolio.repository.SkillCategoryRepository;
import com.ternal.ternalportfolio.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SkillService {

    private final SkillRepository skillRepo;
    private final SkillCategoryRepository categoryRepo;

    @Transactional(readOnly = true)
    public Page<Skill> getSkills(Long categoryId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), size, Sort.by("category").ascending().and(Sort.by("orderIndex").ascending()));
        String trimmedKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        return skillRepo.findByCategoryAndKeyword(categoryId, trimmedKeyword, pageable);
    }

    @Transactional(readOnly = true)
    public List<SkillCategory> getAllCategories() {
        return categoryRepo.findAll(Sort.by("orderIndex").ascending().and(Sort.by("id").ascending()));
    }

    public void saveSkill(Long id, String name, String iconUrl, String proficiency, Long categoryId, Integer orderIndex) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên kỹ năng không được để trống.");
        }
        if (categoryId == null) {
            throw new IllegalArgumentException("Vui lòng chọn loại kỹ năng.");
        }

        SkillCategory category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Loại kỹ năng không tồn tại trong hệ thống."));

        String cleanName = name.trim();
        String cleanIcon = (iconUrl != null && !iconUrl.trim().isEmpty()) ? iconUrl.trim() : null;
        String cleanProficiency = (proficiency != null && !proficiency.trim().isEmpty()) ? proficiency.trim() : "Cơ bản";
        int cleanOrder = (orderIndex != null) ? orderIndex : 0;

        if (id != null) {
            Skill skill = skillRepo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Kỹ năng không tồn tại."));
            skill.setName(cleanName);
            skill.setIconUrl(cleanIcon);
            skill.setProficiency(cleanProficiency);
            skill.setCategory(category);
            skill.setOrderIndex(cleanOrder);
            skillRepo.save(skill);
        } else {
            Skill skill = Skill.builder()
                    .name(cleanName)
                    .iconUrl(cleanIcon)
                    .proficiency(cleanProficiency)
                    .category(category)
                    .orderIndex(cleanOrder)
                    .build();
            skillRepo.save(skill);
        }
    }

    public void deleteSkill(Long id) {
        if (!skillRepo.existsById(id)) {
            throw new IllegalArgumentException("Kỹ năng không tồn tại hoặc đã bị xóa.");
        }
        skillRepo.deleteById(id);
    }
}
