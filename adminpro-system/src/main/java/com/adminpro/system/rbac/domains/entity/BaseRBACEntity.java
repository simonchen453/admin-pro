package com.adminpro.system.rbac.domains.entity;

import com.adminpro.framework.base.entity.BaseAuditEntity;

/**
 * RBAC（基于角色的访问控制）模块的实体基类
 * <p>
 * 该类是RBAC模块所有实体类的基类，继承自框架层的 {@link BaseAuditEntity}，
 * 在审计功能的基础上，增加了系统级数据标识功能。
 * </p>
 * <p>
 * <b>RBAC模块说明：</b>
 * RBAC（Role-Based Access Control）是一种常用的访问控制机制，
 * 通过角色来管理用户权限。该框架的RBAC模块包含用户、角色、权限等核心实体。
 * </p>
 * <p>
 * <b>系统级数据概念：</b>
 * 系统级数据是指由系统预置、初始化或管理的核心数据，
 * 通常不允许普通用户删除或修改。例如：系统预设的角色、默认权限等。
 * </p>
 * <p>
 * <b>主要功能：</b>
 * <ul>
 *   <li>标识数据是否为系统级数据</li>
 *   <li>继承审计字段（创建者、创建时间、更新者、更新时间）</li>
 *   <li>提供系统标识的getter/setter方法</li>
 * </ul>
 * </p>
 * <p>
 * <b>使用场景：</b>
 * </p>
 * <pre>
 * // 1. 定义系统角色
 * {@code
 * public class Role extends BaseRBACEntity {
 *     private String roleName;
 *     // 其他字段...
 * }
 * }
 *
 * // 2. 创建系统角色
 * Role adminRole = new Role();
 * adminRole.setRoleName("超级管理员");
 * adminRole.setSystem(true);  // 标记为系统角色
 *
 * // 3. 创建自定义角色
 * Role customRole = new Role();
 * customRole.setRoleName("部门经理");
 * customRole.setSystem(false);  // 标记为用户自定义角色
 * </pre>
 * <p>
 * <b>继承关系：</b>
 * </p>
 * <pre>
 * Object
 *   └── BaseEntity (框架层)
 *         └── BaseAuditEntity (框架层)
 *               └── BaseRBACEntity (RBAC模块，本类)
 *                     └── User, Role, Permission (具体实体类)
 * </pre>
 * <p>
 * <b>业务逻辑示例：</b>
 * </p>
 * <pre>
 * // 删除角色前的校验
 * {@code
 * public void deleteRole(String roleId) {
 *     Role role = roleService.getById(roleId);
 *     if (role.isSystem()) {
 *         throw new BusinessException("系统角色不允许删除");
 *     }
 *     roleService.delete(roleId);
 * }
 * }
 * </pre>
 *
 * @see com.adminpro.framework.base.entity.BaseAuditEntity
 * @see com.adminpro.framework.base.entity.BaseEntity
 * @author AdminPro
 * @version 1.0.0
 */
public abstract class BaseRBACEntity extends BaseAuditEntity {
    /**
     * 系统级数据标识
     * <p>
     * 该字段用于标识当前实体是否为系统级数据。
     * </p>
     * <p>
     * <b>标识含义：</b>
     * <ul>
     *   <li>{@code true} - 表示该数据为系统级数据，通常由系统预置或初始化</li>
     *   <li>{@code false} - 表示该数据为用户自定义数据</li>
     * </ul>
     * </p>
     * <p>
     * <b>业务规则：</b>
     * 系统级数据通常具有以下特性：
     * <ul>
     *   <li>不允许普通用户删除</li>
     *   <li>某些关键属性不允许修改</li>
     *   <li>在数据导出或备份时需要特殊处理</li>
     *   <li>在系统升级时可能需要保留或更新</li>
     * </ul>
     * </p>
     * <p>
     * <b>使用示例：</b>
     * </p>
     * <pre>
     * // 设置系统权限
     * Permission permission = new Permission();
     * permission.setPermissionName("用户管理");
     * permission.setSystem(true);
     *
     * // 在Service层进行权限校验
     * if (permission.isSystem()) {
     *     throw new BusinessException("系统权限不允许删除");
     * }
     * </pre>
     */
    private boolean system;

    /**
     * 判断当前实体是否为系统级数据
     * <p>
     * 该方法用于快速判断当前实体对象是否被标记为系统级数据。
     * 系统级数据通常有更严格的操作权限控制。
     * </p>
     * <p>
     * <b>返回值说明：</b>
     * <ul>
     *   <li>{@code true} - 该数据为系统级数据</li>
     *   <li>{@code false} - 该数据为用户自定义数据</li>
     * </ul>
     * </p>
     * <p>
     * <b>典型使用场景：</b>
     * </p>
     * <pre>
     * // 1. 在删除操作前校验
     * {@code
     * public void deleteEntity(String id) {
     *     BaseEntity entity = getById(id);
     *     if (entity.isSystem()) {
     *         throw new BusinessException("系统数据不允许删除");
     *     }
     *     repository.delete(id);
     * }
     * }
     *
     * // 2. 在更新操作时进行特殊处理
     * {@code
     * public void updateEntity(BaseEntity entity) {
     *     if (entity.isSystem()) {
     *         // 系统数据只允许更新部分字段
     *         validateSystemDataUpdate(entity);
     *     }
     *     repository.save(entity);
     * }
     * }
     *
     * // 3. 在前端显示时添加标识
     * {@code
     * if (role.isSystem()) {
     *     role.setDisplayName(role.getName() + " (系统)");
     * }
     * }
     * </pre>
     *
     * @return 如果是系统级数据返回 {@code true}，否则返回 {@code false}
     */
    public boolean isSystem() {
        return system;
    }

    /**
     * 设置系统级数据标识
     * <p>
     * 该方法用于设置当前实体的系统级数据标识。
     * 通常在以下场景中使用：
     * <ul>
     *   <li>系统初始化时创建系统数据</li>
     *   <li>数据导入时标识数据来源</li>
     *   <li>数据迁移时保留系统标识</li>
     * </ul>
     * </p>
     * <p>
     * <b>参数说明：</b>
     * </p>
     * <pre>
     * system = true  → 标记为系统数据，应用系统级数据的保护规则
     * system = false → 标记为用户数据，允许用户自由操作
     * </pre>
     * <p>
     * <b>使用示例：</b>
     * </p>
     * <pre>
     * // 系统初始化时创建系统角色
     * {@code
     * @PostConstruct
     * public void initSystemRoles() {
     *     Role adminRole = new Role();
     *     adminRole.setName("ADMIN");
     *     adminRole.setDescription("系统管理员");
     *     adminRole.setSystem(true);  // 标记为系统角色
     *     roleRepository.save(adminRole);
     * }
     * }
     *
     * // 用户创建自定义角色
     * {@code
     * public Role createCustomRole(String roleName) {
     *     Role role = new Role();
     *     role.setName(roleName);
     *     role.setSystem(false);  // 标记为用户角色
     *     return roleRepository.save(role);
     * }
     * }
     * </pre>
     *
     * @param system 系统级数据标识，{@code true}表示系统数据，{@code false}表示用户数据
     */
    public void setSystem(boolean system) {
        this.system = system;
    }
}
