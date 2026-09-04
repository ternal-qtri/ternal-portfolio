package com.ternal.ternalportfolio.controller;

import com.ternal.ternalportfolio.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/skills")
@RequiredArgsConstructor
public class AdminSkillController {

    private final SkillService skillService;

    @GetMapping
    public String list(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        model.addAttribute("skills", skillService.getSkills(categoryId, search, page, 10));
        model.addAttribute("categories", skillService.getAllCategories());
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("search", search);
        return "admin/skills";
    }

    @PostMapping("/save")
    public String save(
            @RequestParam(required = false) Long id,
            @RequestParam String name,
            @RequestParam(required = false) String iconUrl,
            @RequestParam(required = false) String proficiency,
            @RequestParam Long categoryId,
            @RequestParam(defaultValue = "0") Integer orderIndex,
            RedirectAttributes ra) {
        try {
            skillService.saveSkill(id, name, iconUrl, proficiency, categoryId, orderIndex);
            ra.addFlashAttribute("successMessage", "Lưu thông tin kỹ năng thành công.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/skills";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            skillService.deleteSkill(id);
            ra.addFlashAttribute("successMessage", "Đã xóa kỹ năng thành công.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/skills";
    }
}
