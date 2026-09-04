package com.ternal.ternalportfolio.controller;

import com.ternal.ternalportfolio.entity.SkillCategory;
import com.ternal.ternalportfolio.service.SkillCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/skills-categories")
@RequiredArgsConstructor
public class AdminSkillCategoryController {

    private final SkillCategoryService categoryService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("categories", categoryService.getCategories(page, 10));
        return "admin/skills-categories";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute SkillCategory category, RedirectAttributes ra) {
        try {
            categoryService.saveCategory(category);
            ra.addFlashAttribute("successMessage", "Lưu loại kỹ năng thành công.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/skills-categories";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            categoryService.deleteCategory(id);
            ra.addFlashAttribute("successMessage", "Đã xóa loại kỹ năng thành công.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/skills-categories";
    }
}
