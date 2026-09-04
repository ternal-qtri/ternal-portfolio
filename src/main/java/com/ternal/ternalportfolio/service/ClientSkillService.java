package com.ternal.ternalportfolio.service;

import com.ternal.ternalportfolio.entity.SkillCategory;
import com.ternal.ternalportfolio.repository.SkillCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientSkillService {

    private final SkillCategoryRepository categoryRepo;

    public List<SkillCategory> getSkillCategoriesForClient() {
        return categoryRepo.findAllWithSkills();
    }
}
