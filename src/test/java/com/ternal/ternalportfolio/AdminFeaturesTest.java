package com.ternal.ternalportfolio;

import com.ternal.ternalportfolio.entity.ContactMessage;
import com.ternal.ternalportfolio.entity.Project;
import com.ternal.ternalportfolio.entity.Skill;
import com.ternal.ternalportfolio.entity.SkillCategory;
import com.ternal.ternalportfolio.repository.ContactMessageRepository;
import com.ternal.ternalportfolio.repository.ProjectRepository;
import com.ternal.ternalportfolio.repository.SkillCategoryRepository;
import com.ternal.ternalportfolio.repository.SkillRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminFeaturesTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepo;

    @Autowired
    private SkillCategoryRepository skillCategoryRepo;

    @Autowired
    private SkillRepository skillRepo;

    @Autowired
    private ContactMessageRepository contactRepo;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    // =========================================================================
    // 1. KIỂM THỬ BẢO MẬT & PHÂN QUYỀN (SECURITY & AUTHORIZATION)
    // =========================================================================

    @Test
    @Order(1)
    @DisplayName("Bảo mật: Khách chưa đăng nhập truy cập /admin/projects phải bị chuyển hướng đến /admin/login")
    void testAnonymousAccessToAdminShouldRedirect() throws Exception {
        mockMvc.perform(get("/admin/projects"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/login"));
    }

    @Test
    @Order(2)
    @DisplayName("Bảo mật: Người dùng có role USER (không phải ADMIN) truy cập /admin/projects phải bị từ chối 403 Forbidden")
    @WithMockUser(username = "user@gmail.com", roles = {"USER"})
    void testUserRoleAccessToAdminShouldBeForbidden() throws Exception {
        mockMvc.perform(get("/admin/projects"))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/error/403"));
    }

    @Test
    @Order(3)
    @DisplayName("Bảo mật: Trang đăng nhập /admin/login phải công khai (200 OK)")
    void testAdminLoginPageIsPublic() throws Exception {
        mockMvc.perform(get("/admin/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"));
    }

    // =========================================================================
    // 2. KIỂM THỬ QUẢN LÝ DỰ ÁN (ADMIN PROJECT MANAGEMENT - /admin/projects)
    // =========================================================================

    @Test
    @Order(4)
    @DisplayName("Dự án: Admin truy cập danh sách dự án thành công và nạp đúng Model")
    @WithMockUser(username = "admin@ternal.vn", roles = {"ADMIN"})
    void testAdminGetProjectsList() throws Exception {
        mockMvc.perform(get("/admin/projects"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/projects"))
                .andExpect(model().attributeExists("projects"))
                .andExpect(model().attributeExists("selectedType"));
    }

    @Test
    @Order(5)
    @DisplayName("Dự án: Thêm mới dự án khi thiếu thông tin bắt buộc hoặc thiếu ảnh bìa phải báo lỗi qua FlashAttribute")
    @WithMockUser(username = "admin@ternal.vn", roles = {"ADMIN"})
    void testAdminSaveProjectValidationFailure() throws Exception {
        // Thiếu tiêu đề
        mockMvc.perform(post("/admin/projects/save")
                        .param("title", "")
                        .param("type", "PERSONAL")
                        .param("role", "Backend"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/projects"))
                .andExpect(flash().attributeExists("errorMessage"));

        // Thêm mới nhưng không có ảnh bìa bắt buộc
        mockMvc.perform(post("/admin/projects/save")
                        .param("title", "Dự án thiếu ảnh bìa")
                        .param("type", "PERSONAL")
                        .param("role", "Backend"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/projects"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @Order(6)
    @DisplayName("Dự án: Toàn bộ vòng đời Cập nhật -> Xóa dự án (CRUD lifecycle)")
    @WithMockUser(username = "admin@ternal.vn", roles = {"ADMIN"})
    void testAdminProjectCrudLifecycle() throws Exception {
        String projectTitle = "Dự án Test JUnit E2E " + System.currentTimeMillis();

        // 1. Tạo dự án mẫu trong CSDL
        Project project = new Project();
        project.setTitle(projectTitle);
        project.setType("PERSONAL");
        project.setRole("Lead Backend Developer");
        project.setShortDescription("Mô tả ngắn dự án test ban đầu");
        project.setCoverImage("https://example.com/test-cover.png");
        project.setCoverImagePublicId(null);
        project.setOrderIndex(99);
        project.setStatus("ACTIVE");
        Project savedProject = projectRepo.save(project);
        assertNotNull(savedProject.getId(), "Dự án phải được lưu trong CSDL");

        // 2. Cập nhật dự án qua POST /admin/projects/save
        String updatedTitle = projectTitle + " - Đã cập nhật";
        mockMvc.perform(post("/admin/projects/save")
                        .param("id", savedProject.getId().toString())
                        .param("title", updatedTitle)
                        .param("type", "TEAM")
                        .param("role", "Senior Software Engineer")
                        .param("shortDescription", "Mô tả sau khi cập nhật")
                        .param("tags", "Java, Spring Boot, JUnit")
                        .param("orderIndex", "100")
                        .param("isVisible", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/projects"))
                .andExpect(flash().attributeExists("successMessage"));

        Project updatedProject = projectRepo.findById(savedProject.getId()).orElse(null);
        assertNotNull(updatedProject, "Dự án đã cập nhật phải tồn tại trong CSDL");
        assertEquals(updatedTitle, updatedProject.getTitle());
        assertEquals("TEAM", updatedProject.getType());
        assertEquals("Senior Software Engineer", updatedProject.getRole());

        // 3. Xóa dự án qua POST /admin/projects/delete/{id}
        mockMvc.perform(post("/admin/projects/delete/" + savedProject.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/projects"))
                .andExpect(flash().attributeExists("successMessage"));

        // Xác nhận đã xóa khỏi CSDL
        assertTrue(projectRepo.findById(savedProject.getId()).isEmpty(), "Dự án phải được xóa khỏi CSDL");
    }

    // =========================================================================
    // 3. KIỂM THỬ QUẢN LÝ LOẠI KỸ NĂNG (ADMIN SKILL CATEGORY - /admin/skills-categories)
    // =========================================================================

    @Test
    @Order(7)
    @DisplayName("Loại kỹ năng: Admin truy cập danh sách loại kỹ năng thành công")
    @WithMockUser(username = "admin@ternal.vn", roles = {"ADMIN"})
    void testAdminGetSkillCategoriesList() throws Exception {
        mockMvc.perform(get("/admin/skills-categories"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/skills-categories"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    @Order(8)
    @DisplayName("Loại kỹ năng: Toàn bộ vòng đời Thêm mới -> Cập nhật -> Xóa loại kỹ năng")
    @WithMockUser(username = "admin@ternal.vn", roles = {"ADMIN"})
    void testAdminSkillCategoryCrudLifecycle() throws Exception {
        String categoryName = "Test Category " + System.currentTimeMillis();

        // 1. Thêm mới loại kỹ năng
        mockMvc.perform(post("/admin/skills-categories/save")
                        .param("name", categoryName)
                        .param("description", "Mô tả phân loại kiểm thử")
                        .param("orderIndex", "88"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/skills-categories"))
                .andExpect(flash().attributeExists("successMessage"));

        SkillCategory savedCategory = skillCategoryRepo.findAll().stream()
                .filter(c -> categoryName.equals(c.getName()))
                .findFirst()
                .orElse(null);
        assertNotNull(savedCategory, "Loại kỹ năng phải tồn tại trong CSDL");

        // 2. Cập nhật loại kỹ năng
        String updatedCategoryName = categoryName + " (Đã sửa)";
        mockMvc.perform(post("/admin/skills-categories/save")
                        .param("id", savedCategory.getId().toString())
                        .param("name", updatedCategoryName)
                        .param("description", "Mô tả mới")
                        .param("orderIndex", "90"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/skills-categories"));

        SkillCategory updated = skillCategoryRepo.findById(savedCategory.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals(updatedCategoryName, updated.getName());

        // 3. Xóa loại kỹ năng
        mockMvc.perform(post("/admin/skills-categories/delete/" + savedCategory.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/skills-categories"))
                .andExpect(flash().attributeExists("successMessage"));

        assertTrue(skillCategoryRepo.findById(savedCategory.getId()).isEmpty(), "Loại kỹ năng phải được xóa khỏi CSDL");
    }

    // =========================================================================
    // 4. KIỂM THỬ QUẢN LÝ KỸ NĂNG (ADMIN SKILL - /admin/skills)
    // =========================================================================

    @Test
    @Order(9)
    @DisplayName("Kỹ năng: Admin truy cập danh sách kỹ năng thành công")
    @WithMockUser(username = "admin@ternal.vn", roles = {"ADMIN"})
    void testAdminGetSkillsList() throws Exception {
        mockMvc.perform(get("/admin/skills"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/skills"))
                .andExpect(model().attributeExists("skills"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    @Order(10)
    @DisplayName("Kỹ năng: Toàn bộ vòng đời Thêm mới -> Cập nhật -> Xóa kỹ năng")
    @WithMockUser(username = "admin@ternal.vn", roles = {"ADMIN"})
    void testAdminSkillCrudLifecycle() throws Exception {
        // Chuẩn bị 1 category trước để gắn skill vào
        SkillCategory parentCategory = skillCategoryRepo.save(SkillCategory.builder()
                .name("Temp Cat for Skill Test " + System.currentTimeMillis())
                .orderIndex(1)
                .build());

        String skillName = "Skill Test " + System.currentTimeMillis();

        // 1. Thêm mới Skill
        mockMvc.perform(post("/admin/skills/save")
                        .param("name", skillName)
                        .param("proficiency", "Thành thạo")
                        .param("categoryId", parentCategory.getId().toString())
                        .param("orderIndex", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/skills"))
                .andExpect(flash().attributeExists("successMessage"));

        Skill savedSkill = skillRepo.findAll().stream()
                .filter(s -> skillName.equals(s.getName()))
                .findFirst()
                .orElse(null);
        assertNotNull(savedSkill, "Kỹ năng phải tồn tại trong CSDL");
        assertEquals("Thành thạo", savedSkill.getProficiency());

        // 2. Cập nhật Skill
        String updatedSkillName = skillName + " (Updated)";
        mockMvc.perform(post("/admin/skills/save")
                        .param("id", savedSkill.getId().toString())
                        .param("name", updatedSkillName)
                        .param("proficiency", "Chuyên sâu")
                        .param("categoryId", parentCategory.getId().toString())
                        .param("orderIndex", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/skills"))
                .andExpect(flash().attributeExists("successMessage"));

        Skill updated = skillRepo.findById(savedSkill.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals(updatedSkillName, updated.getName());
        assertEquals("Chuyên sâu", updated.getProficiency());

        // 3. Xóa Skill
        mockMvc.perform(post("/admin/skills/delete/" + savedSkill.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/skills"))
                .andExpect(flash().attributeExists("successMessage"));

        assertTrue(skillRepo.findById(savedSkill.getId()).isEmpty(), "Kỹ năng phải được xóa khỏi CSDL");

        // Dọn dẹp Category tạm
        skillCategoryRepo.deleteById(parentCategory.getId());
    }

    // =========================================================================
    // 5. KIỂM THỬ QUẢN LÝ TIN NHẮN LIÊN HỆ (ADMIN CONTACT - /admin/contacts)
    // =========================================================================

    @Test
    @Order(11)
    @DisplayName("Liên hệ: Admin truy cập danh sách liên hệ thành công và có số liệu thống kê")
    @WithMockUser(username = "admin@ternal.vn", roles = {"ADMIN"})
    void testAdminGetContactsList() throws Exception {
        mockMvc.perform(get("/admin/contacts"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/contacts"))
                .andExpect(model().attributeExists("contacts"))
                .andExpect(model().attributeExists("totalCount"))
                .andExpect(model().attributeExists("unreadCount"))
                .andExpect(model().attributeExists("repliedCount"));
    }

    @Test
    @Order(12)
    @DisplayName("Liên hệ: Đánh dấu đã đọc (mark-read) qua AJAX trả về JSON thành công")
    @WithMockUser(username = "admin@ternal.vn", roles = {"ADMIN"})
    void testAdminMarkContactAsRead() throws Exception {
        // Tạo tin nhắn UNREAD
        ContactMessage message = contactRepo.save(ContactMessage.builder()
                .senderName("Người gửi kiểm thử")
                .email("test.contact@gmail.com")
                .subject("Tiêu đề chưa đọc")
                .message("Nội dung tin nhắn kiểm thử")
                .status("UNREAD")
                .build());

        // Gọi API mark-read
        mockMvc.perform(post("/admin/contacts/mark-read/" + message.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"success\":true")));

        // Xác nhận trạng thái trong CSDL đã chuyển thành READ
        ContactMessage updated = contactRepo.findById(message.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals("READ", updated.getStatus());

        // Dọn dẹp
        contactRepo.deleteById(message.getId());
    }

    @Test
    @Order(13)
    @DisplayName("Liên hệ: Đánh dấu đã phản hồi (mark-replied) chuyển hướng và cập nhật CSDL")
    @WithMockUser(username = "admin@ternal.vn", roles = {"ADMIN"})
    void testAdminMarkContactAsReplied() throws Exception {
        ContactMessage message = contactRepo.save(ContactMessage.builder()
                .senderName("Người gửi kiểm thử 2")
                .email("test.contact2@gmail.com")
                .subject("Tiêu đề kiểm thử 2")
                .message("Nội dung tin nhắn 2")
                .status("READ")
                .build());

        mockMvc.perform(post("/admin/contacts/mark-replied/" + message.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/contacts"))
                .andExpect(flash().attributeExists("successMessage"));

        ContactMessage updated = contactRepo.findById(message.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals("REPLIED", updated.getStatus());

        // Dọn dẹp
        contactRepo.deleteById(message.getId());
    }

    @Test
    @Order(14)
    @DisplayName("Liên hệ: Xóa tin nhắn liên hệ (delete) thành công khỏi CSDL")
    @WithMockUser(username = "admin@ternal.vn", roles = {"ADMIN"})
    void testAdminDeleteContact() throws Exception {
        ContactMessage message = contactRepo.save(ContactMessage.builder()
                .senderName("Người gửi sắp bị xóa")
                .email("delete.me@gmail.com")
                .subject("Sắp bị xóa")
                .message("Nội dung sắp bị xóa")
                .status("UNREAD")
                .build());

        mockMvc.perform(post("/admin/contacts/delete/" + message.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/contacts"))
                .andExpect(flash().attributeExists("successMessage"));

        assertTrue(contactRepo.findById(message.getId()).isEmpty(), "Tin nhắn liên hệ phải được xóa khỏi CSDL");
    }
}
