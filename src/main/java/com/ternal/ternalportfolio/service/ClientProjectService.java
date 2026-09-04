package com.ternal.ternalportfolio.service;

import com.ternal.ternalportfolio.entity.Project;
import com.ternal.ternalportfolio.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientProjectService {

    private final ProjectRepository projectRepo;

    public List<Project> getActiveProjectsForClient() {
        return projectRepo.findActiveProjectsForClient();
    }

    public List<Project> getFeaturedProjectsForHome() {
        return projectRepo.findActiveProjectsForClient().stream()
                .limit(3)
                .toList();
    }

    public Optional<Project> getProjectDetail(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return projectRepo.findById(id)
                .filter(p -> p.getStatus() == null || !"INACTIVE".equalsIgnoreCase(p.getStatus()));
    }

    public record AdjacentProjects(Project prev, Project next) {}

    public AdjacentProjects getAdjacentProjects(Long currentId) {
        if (currentId == null) {
            return new AdjacentProjects(null, null);
        }
        List<Project> allActive = getActiveProjectsForClient();
        Project prev = null;
        Project next = null;

        for (int i = 0; i < allActive.size(); i++) {
            if (currentId.equals(allActive.get(i).getId())) {
                if (i > 0) {
                    prev = allActive.get(i - 1);
                }
                if (i < allActive.size() - 1) {
                    next = allActive.get(i + 1);
                }
                break;
            }
        }
        return new AdjacentProjects(prev, next);
    }
}
