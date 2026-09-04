package com.ternal.ternalportfolio.controller;

import com.ternal.ternalportfolio.service.ClientProjectService;
import com.ternal.ternalportfolio.service.ClientSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ClientController {

    private final ClientSkillService clientSkillService;
    private final ClientProjectService clientProjectService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("featuredProjects", clientProjectService.getFeaturedProjectsForHome());
        return "client/index";
    }

    @GetMapping("/skills")
    public String skills(Model model) {
        model.addAttribute("categories", clientSkillService.getSkillCategoriesForClient());
        return "client/skills";
    }

    @GetMapping("/projects")
    public String projects(Model model) {
        model.addAttribute("projects", clientProjectService.getActiveProjectsForClient());
        return "client/projects";
    }

    @GetMapping("/projects/detail")
    public String projectDetail(@RequestParam(required = false) Long id, Model model) {
        if (id == null) {
            return "redirect:/projects";
        }
        return clientProjectService.getProjectDetail(id)
                .map(project -> {
                    model.addAttribute("project", project);
                    ClientProjectService.AdjacentProjects adjacent = clientProjectService.getAdjacentProjects(id);
                    model.addAttribute("prevProject", adjacent.prev());
                    model.addAttribute("nextProject", adjacent.next());
                    return "client/project-detail";
                })
                .orElse("redirect:/projects");
    }
}
