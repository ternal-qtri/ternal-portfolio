package com.ternal.ternalportfolio.service;

import com.ternal.ternalportfolio.entity.Project;
import com.ternal.ternalportfolio.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdminProjectService {

    private final ProjectRepository projectRepo;
    private final CloudinaryService cloudinaryService;

    private static final Pattern YOUTUBE_PATTERN = Pattern.compile(
            "(?:youtu\\.be\\/|youtube\\.com\\/(?:embed\\/|v\\/|watch\\?v=|watch\\?.+&v=|shorts\\/))([\\w-]{11})"
    );

    public Page<Project> getProjects(String type, String keyword, int page, int size) {
        int validPage = Math.max(0, page);
        int validSize = size > 0 ? size : 10;
        Pageable pageable = PageRequest.of(validPage, validSize, Sort.by(Sort.Direction.DESC, "orderIndex").and(Sort.by(Sort.Direction.DESC, "id")));

        String cleanType = (type != null && !type.isBlank() && !type.equalsIgnoreCase("ALL"))
                ? type.trim().toUpperCase() : null;
        String cleanKeyword = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;

        return projectRepo.findByTypeAndKeyword(cleanType, cleanKeyword, pageable);
    }

    public Project getProject(Long id) {
        return projectRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy dự án với ID: " + id));
    }

    @Transactional
    public void saveProject(
            Long id,
            String title,
            String type,
            String role,
            String timeframe,
            String githubUrl,
            String videoUrl,
            String tags,
            String shortDescription,
            String features,
            String lessonsLearned,
            String challenges,
            Integer orderIndex,
            Boolean isVisible,
            MultipartFile coverFile) {

        // 1. Validation cơ bản
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Tiêu đề dự án không được để trống.");
        }
        if (type == null || (!type.equalsIgnoreCase("PERSONAL") && !type.equalsIgnoreCase("TEAM"))) {
            throw new IllegalArgumentException("Kiểu dự án không hợp lệ (phải là Cá nhân hoặc Nhóm).");
        }
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Vai trò trong dự án không được để trống.");
        }

        // 2. Chuẩn hóa YouTube Embed URL
        String normalizedVideoUrl = normalizeYouTubeUrl(videoUrl);

        // 3. Chuẩn hóa tags công nghệ ("Spring Boot, MySQL, React")
        String normalizedTags = normalizeTags(tags);

        Project project;
        String oldPublicId = null;

        if (id != null) {
            project = getProject(id);
            oldPublicId = project.getCoverImagePublicId();
        } else {
            project = new Project();
            if (coverFile == null || coverFile.isEmpty()) {
                throw new IllegalArgumentException("Vui lòng tải lên ảnh bìa cho dự án mới.");
            }
        }

        // 4. Xử lý tải ảnh lên Cloudinary
        if (coverFile != null && !coverFile.isEmpty()) {
            CloudinaryService.UploadResult uploadResult = cloudinaryService.uploadImage(coverFile);
            project.setCoverImage(uploadResult.url());
            project.setCoverImagePublicId(uploadResult.publicId());

            // Nếu cập nhật và đã có ảnh cũ trên Cloudinary, xóa ảnh cũ
            if (oldPublicId != null && !oldPublicId.isBlank()) {
                cloudinaryService.deleteImage(oldPublicId);
            }
        }

        // 5. Cập nhật các trường thông tin
        project.setTitle(title.trim());
        project.setType(type.trim().toUpperCase());
        project.setRole(role.trim());
        project.setTimeframe(timeframe != null ? timeframe.trim() : null);
        project.setGithubUrl(githubUrl != null ? githubUrl.trim() : null);
        project.setVideoUrl(normalizedVideoUrl);
        project.setTags(normalizedTags);
        project.setShortDescription(shortDescription != null ? shortDescription.trim() : null);
        project.setFeatures(features != null ? features.trim() : null);
        project.setLessonsLearned(lessonsLearned != null ? lessonsLearned.trim() : null);
        project.setChallenges(challenges != null ? challenges.trim() : null);
        project.setOrderIndex(orderIndex != null ? orderIndex : 1);
        project.setStatus(Boolean.TRUE.equals(isVisible) ? "ACTIVE" : "INACTIVE");

        projectRepo.save(project);
        log.info("Saved project: id={}, title={}, coverUrl={}", project.getId(), project.getTitle(), project.getCoverImage());
    }

    @Transactional
    public void deleteProject(Long id) {
        Project project = getProject(id);

        // Xóa ảnh trên Cloudinary nếu có public_id
        if (project.getCoverImagePublicId() != null && !project.getCoverImagePublicId().isBlank()) {
            cloudinaryService.deleteImage(project.getCoverImagePublicId());
        }

        projectRepo.delete(project);
        log.info("Deleted project id={} and removed its Cloudinary image", id);
    }

    public String normalizeYouTubeUrl(String rawUrl) {
        if (rawUrl == null || rawUrl.trim().isEmpty()) {
            return null;
        }
        String clean = rawUrl.trim();
        Matcher matcher = YOUTUBE_PATTERN.matcher(clean);
        if (matcher.find()) {
            String videoId = matcher.group(1);
            return "https://www.youtube.com/embed/" + videoId;
        }

        if (clean.matches("^[\\w-]{11}$")) {
            return "https://www.youtube.com/embed/" + clean;
        }

        throw new IllegalArgumentException("Liên kết YouTube không hợp lệ. Vui lòng nhập đúng URL video YouTube.");
    }

    public String normalizeTags(String rawTags) {
        if (rawTags == null || rawTags.trim().isEmpty()) {
            return null;
        }
        return Arrays.stream(rawTags.split(","))
                .map(String::trim)
                .filter(t -> !t.isEmpty())
                .collect(Collectors.joining(", "));
    }
}
