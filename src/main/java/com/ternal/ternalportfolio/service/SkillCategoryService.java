package com.ternal.ternalportfolio.service;

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

@Service
@RequiredArgsConstructor
@Transactional
public class SkillCategoryService {

    private final SkillCategoryRepository categoryRepo;
    private final SkillRepository skillRepo;

    @Transactional(readOnly = true)
    public Page<SkillCategory> getCategories(int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), size, Sort.by("orderIndex").ascending().and(Sort.by("id").ascending()));
        return categoryRepo.findAll(pageable);
    }

    public void saveCategory(SkillCategory category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên loại kỹ năng không được để trống.");
        }
        category.setName(category.getName().trim());
        if (category.getOrderIndex() == null) {
            category.setOrderIndex(0);
        }

        if (category.getId() != null) {
            categoryRepo.findById(category.getId()).ifPresentOrElse(existing -> {
                existing.setName(category.getName());
                existing.setDescription(category.getDescription());
                existing.setOrderIndex(category.getOrderIndex());
                categoryRepo.save(existing);
            }, () -> categoryRepo.save(category));
        } else {
            categoryRepo.save(category);
        }
    }

    public void deleteCategory(Long id) {
        if (skillRepo.existsByCategoryId(id)) {
            throw new IllegalStateException("Không thể xóa: Đang có kỹ năng thuộc loại này!");
        }
        categoryRepo.deleteById(id);
    }
}
