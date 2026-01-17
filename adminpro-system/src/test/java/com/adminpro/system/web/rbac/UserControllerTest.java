package com.adminpro.system.web.rbac;

import com.adminpro.system.core.common.helper.FileHelper;
import com.adminpro.system.rbac.domains.entity.dept.DeptService;
import com.adminpro.system.rbac.domains.entity.post.PostService;
import com.adminpro.system.rbac.domains.entity.role.RoleService;
import com.adminpro.system.rbac.domains.entity.user.UserEntity;
import com.adminpro.system.rbac.domains.entity.user.UserService;
import com.adminpro.system.rbac.domains.entity.userpost.UserPostAssignService;
import com.adminpro.system.rbac.domains.entity.userrole.UserRoleAssignService;
import com.adminpro.system.rbac.domains.vo.user.UserCreateValidator;
import com.adminpro.system.rbac.domains.vo.user.UserUpdateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserController 集成测试
 * 
 * @author AdminPro
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserController 集成测试")
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private UserRoleAssignService userRoleAssignService;

    @Mock
    private UserPostAssignService userPostAssignService;

    @Mock
    private DeptService deptService;

    @Mock
    private RoleService roleService;

    @Mock
    private PostService postService;

    @Mock
    private UserCreateValidator userCreateValidator;

    @Mock
    private UserUpdateValidator userUpdateValidator;

    @Mock
    private FileHelper fileHelper;

    @InjectMocks
    private UserController userController;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        testUser = new UserEntity();
        testUser.setId("test-user-id-001");
        testUser.setUserDomain("kaizen_default");
        testUser.setLoginName("testuser");
        testUser.setRealName("测试用户");
        testUser.setEmail("test@example.com");
        testUser.setStatus("active");
    }

    @Test
    @DisplayName("获取用户详情 - 用户存在")
    void getDetail_WhenUserExists_ReturnsUserDetail() throws Exception {
        // Arrange
        when(userService.findById("test-user-id-001")).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/test-user-id-001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).findById("test-user-id-001");
    }

    @Test
    @DisplayName("获取用户详情 - 用户不存在")
    void getDetail_WhenUserNotExists_ReturnsError() throws Exception {
        // Arrange
        when(userService.findById("non-existent-id")).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/non-existent-id")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()); // 返回200但带错误信息

        verify(userService, times(1)).findById("non-existent-id");
    }

    @Test
    @DisplayName("批量删除用户 (RESTful)")
    void batchDelete_WithValidIds_ReturnsSuccess() throws Exception {
        // Arrange
        doNothing().when(userService).deleteMany(anyString());

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/batch-delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[\"domain_id1\", \"domain_id2\"]"))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteMany(anyString());
    }

    @Test
    @DisplayName("批量删除用户 - 空列表")
    void batchDelete_WithEmptyList_ReturnsError() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/users/batch-delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[]"))
                .andExpect(status().isOk()); // 返回200但带错误信息

        verify(userService, never()).deleteMany(anyString());
    }
}
