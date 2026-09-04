package com.ternal.ternalportfolio.controller;

import com.ternal.ternalportfolio.service.AdminProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/projects")
@RequiredArgsConstructor
public class AdminProjectController {

    private final AdminProjectService projectService;

    @GetMapping
    public String list(
            @RequestParam(required = false, defaultValue = "ALL") String type,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        model.addAttribute("projects", projectService.getProjects(type, search, page, 10));
        model.addAttribute("selectedType", (type != null && !type.isBlank()) ? type.toUpperCase() : "ALL");
        model.addAttribute("search", search);
        return "admin/projects";
    }

    @PostMapping("/save")
    public String save(
            @RequestParam(required = false) Long id,
            @RequestParam String title,
            @RequestParam String type,
            @RequestParam String role,
            @RequestParam(required = false) String timeframe,
            @RequestParam(required = false) String githubUrl,
            @RequestParam(required = false) String videoUrl,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String shortDescription,
            @RequestParam(required = false) String features,
            @RequestParam(required = false) String lessonsLearned,
            @RequestParam(required = false) String challenges,
            @RequestParam(required = false, defaultValue = "1") Integer orderIndex,
            @RequestParam(required = false, defaultValue = "false") Boolean isVisible,
            @RequestParam(value = "coverImageFile", required = false) MultipartFile coverImageFile,
            RedirectAttributes ra) {
        try {
            projectService.saveProject(
                    id, title, type, role, timeframe, githubUrl, videoUrl,
                    tags, shortDescription, features, lessonsLearned, challenges,
                    orderIndex, isVisible, coverImageFile
            );
            ra.addFlashAttribute("successMessage", (id != null ? "Cập nhật" : "Thêm mới") + " dự án thành công.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
        }
        return "redirect:/admin/projects";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            projectService.deleteProject(id);
            ra.addFlashAttribute("successMessage", "Đã xóa dự án và ảnh bìa trên Cloudinary thành công.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi xóa dự án: " + e.getMessage());
        }
        return "redirect:/admin/projects";
    }
}
