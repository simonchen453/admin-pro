package com.adminpro.system.rbac.domains.entity.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * UserService 单元测试
 * 
 * @author AdminPro
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 单元测试")
public class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    private UserEntity testUser;
    private UserIden testUserIden;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setId("test-user-id-001");
        testUser.setUserDomain("kaizen_default");
        testUser.setLoginName("testuser");
        testUser.setRealName("测试用户");
        testUser.setEmail("test@example.com");
        testUser.setMobileNo("13800138000");
        testUser.setStatus("active");

        testUserIden = new UserIden("kaizen_default", "testuser");
    }

    @Test
    @DisplayName("根据ID查询用户 - 用户存在")
    void findById_WhenUserExists_ReturnsUser() {
        // Arrange
        when(userDao.findById("test-user-id-001")).thenReturn(testUser);

        // Act
        UserEntity result = userService.findById("test-user-id-001");

        // Assert
        assertNotNull(result);
        assertEquals("test-user-id-001", result.getId());
        assertEquals("testuser", result.getLoginName());
        verify(userDao, times(1)).findById("test-user-id-001");
    }

    @Test
    @DisplayName("根据ID查询用户 - 用户不存在")
    void findById_WhenUserNotExists_ReturnsNull() {
        // Arrange
        when(userDao.findById("non-existent-id")).thenReturn(null);

        // Act
        UserEntity result = userService.findById("non-existent-id");

        // Assert
        assertNull(result);
        verify(userDao, times(1)).findById("non-existent-id");
    }

    @Test
    @DisplayName("根据UserIden查询用户")
    void findByIden_ReturnsUser() {
        // Arrange
        when(userDao.findByIden(testUserIden)).thenReturn(testUser);

        // Act
        UserEntity result = userService.findByIden(testUserIden);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getLoginName());
        verify(userDao, times(1)).findByIden(testUserIden);
    }

    @Test
    @DisplayName("根据域和用户ID查询用户 - 用户存在且域匹配")
    void findByUserDomainAndUserId_WhenMatches_ReturnsUser() {
        // Arrange
        when(userDao.findById("test-user-id-001")).thenReturn(testUser);

        // Act
        UserEntity result = userService.findByUserDomainAndUserId("kaizen_default", "test-user-id-001");

        // Assert
        assertNotNull(result);
        assertEquals("kaizen_default", result.getUserDomain());
        verify(userDao, times(1)).findById("test-user-id-001");
    }

    @Test
    @DisplayName("根据域和用户ID查询用户 - 域不匹配")
    void findByUserDomainAndUserId_WhenDomainMismatch_ReturnsNull() {
        // Arrange
        when(userDao.findById("test-user-id-001")).thenReturn(testUser);

        // Act
        UserEntity result = userService.findByUserDomainAndUserId("other_domain", "test-user-id-001");

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("根据域和登录名查询用户")
    void findByUserDomainAndLoginName_ReturnsUser() {
        // Arrange
        when(userDao.findByUserDomainAndLoginName("kaizen_default", "testuser")).thenReturn(testUser);

        // Act
        UserEntity result = userService.findByUserDomainAndLoginName("kaizen_default", "testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getLoginName());
        verify(userDao, times(1)).findByUserDomainAndLoginName("kaizen_default", "testuser");
    }

    @Test
    @DisplayName("验证密码 - 用户不存在返回false")
    void authenticate_WhenUserNotExists_ReturnsFalse() {
        // Arrange
        when(userDao.findByIden(testUserIden)).thenReturn(null);

        // Act
        boolean result = userService.authenticate(testUserIden, "password123");

        // Assert
        assertFalse(result);
    }
}
