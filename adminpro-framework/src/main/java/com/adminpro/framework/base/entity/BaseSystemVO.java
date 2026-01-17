package com.adminpro.framework.base.entity;

/**
 * 系统级视图对象（VO）基类
 * <p>
 * 该类继承自 {@link BaseAuditVO}，在审计信息的基础上增加了系统级数据标识字段。
 * 用于表示系统级数据（由系统预置、初始化或管理的核心数据）的视图对象。
 * </p>
 * <p>
 * <b>系统级数据说明：</b>
 * 系统级数据是指由系统预置、初始化或管理的核心数据，
 * 与用户自定义数据相对，通常具有特殊的业务含义和操作权限。
 * </p>
 * <p>
 * <b>系统级数据特征：</b>
 * <ul>
 *   <li>系统初始化时创建，不是用户创建</li>
 *   <li>通常不允许普通用户删除</li>
 *   <li>某些关键属性可能不允许修改</li>
 *   <li>在数据导出或备份时需要特殊处理</li>
 *   <li>在系统升级时可能需要保留或更新</li>
 *   <li>在前端显示时通常需要特殊标识</li>
 * </ul>
 * </p>
 * <p>
 * <b>继承层次：</b>
 * </p>
 * <pre>
 * IVO
 *   └── BaseVO
 *         └── BaseAuditVO
 *               └── BaseSystemVO (本类)
 *                     └── 具体业务VO类（如 RoleVO, PermissionVO 等）
 * </pre>
 * <p>
 * <b>使用场景：</b>
 * </p>
 * <pre>
 * // 定义系统角色VO
 * {@code
 * public class RoleVO extends BaseSystemVO {
 *     private String roleName;
 *     private String roleDesc;
 *     private List&lt;String&gt; permissions;
 *     // 自动拥有：系统标识 + 审计信息
 *     // getters and setters...
 * }
 * }
 *
 * // 使用示例
 * {@code
 * // 从Entity转换为VO
 * RoleVO roleVO = new RoleVO();
 * roleVO.setRoleName(role.getName());
 * roleVO.setRoleDesc(role.getDescription());
 * roleVO.setSystem(role.isSystem());  // 设置系统标识
 * roleVO.setCreatedBy(role.getCreatedBy());
 * roleVO.setCreatedAt(formatDate(role.getCreatedAt()));
 * // ... 其他字段
 * }
 * </pre>
 * <p>
 * <b>前端显示示例：</b>
 * </p>
 * <pre>
 * // 在前端根据系统标识进行不同展示
 * {@code
 * // HTML模板示例
 * &lt;div class="role-item" :class="{ 'system-role': role.isSystem }"&gt;
 *   {{ role.roleName }}
 *   &lt;span v-if="role.isSystem"&gt;系统&lt;/span&gt;
 * &lt;/div&gt;
 *
 * // 操作按钮控制
 * &lt;button v-if="!role.isSystem" @click="deleteRole(role)"&gt;
 *   删除
 * &lt;/button&gt;
 * }
 * </pre>
 * <p>
 * <b>与Entity的对应关系：</b>
 * </p>
 * <pre>
 * // Entity层
 * {@code
 * public class Role extends BaseRBACEntity {
 *     private String name;
 *     // ... 其他字段
 * }
 * }
 *
 * // VO层
 * {@code
 * public class RoleVO extends BaseSystemVO {
 *     private String roleName;
 *     // ... 其他字段
 * }
 * }
 *
 * // 转换方法
 * {@code
 * public static RoleVO fromEntity(Role entity) {
 *     RoleVO vo = new RoleVO();
 *     vo.setRoleName(entity.getName());
 *     vo.setSystem(entity.isSystem());
 *     vo.setCreatedBy(entity.getCreatedBy());
 *     // ... 其他字段映射
 *     return vo;
 * }
 * }
 * </pre>
 * <p>
 * <b>注意事项：</b>
 * </p>
 * <ul>
 *   <li>系统标识应该从对应的Entity中复制而来</li>
 *   <li>前端应根据system字段控制UI显示和操作权限</li>
 *   <li>导出数据时，system字段可以用于标识数据来源</li>
 *   <li>序列化到JSON时，system字段会自动包含</li>
 * </ul>
 *
 * @see BaseAuditVO
 * @see BaseVO
 * @see IVO
 * @see com.adminpro.system.rbac.domains.entity.BaseRBACEntity
 * @author AdminPro
 * @version 1.0.0
 */
public abstract class BaseSystemVO extends BaseAuditVO {
    /**
     * 系统级数据标识
     * <p>
     * 该字段用于标识当前VO对象对应的数据是否为系统级数据。
     * </p>
     * <p>
     * <b>标识含义：</b>
     * <ul>
     *   <li>{@code true} - 表示该数据为系统级数据，由系统预置或初始化</li>
     *   <li>{@code false} - 表示该数据为用户自定义数据</li>
     * </ul>
     * </p>
     * <p>
     * <b>前端处理建议：</b>
     * </p>
     * <ul>
     *   <li>显示：系统数据应添加特殊标识（如徽章、图标、颜色等）</li>
     *   <li>编辑：系统数据的关键字段可能设为只读</li>
     *   <li>删除：系统数据通常不显示删除按钮或禁用删除功能</li>
     *   <li>排序：可以将系统数据固定在列表顶部</li>
     * </ul>
     * </p>
     * <p>
     * <b>使用示例：</b>
     * </p>
     * <pre>
     * // 从Entity转换时设置系统标识
     * {@code
     * RoleVO vo = new RoleVO();
     * vo.setRoleName(entity.getName());
     * vo.setSystem(entity.isSystem());  // 复制系统标识
     * }
     *
     * // 前端判断
     * {@code
     * if (vo.isSystem()) {
     *     // 显示系统标识
     *     showSystemBadge();
     *     // 禁用删除按钮
     *     disableDeleteButton();
     * }
     * }
     * </pre>
     */
    private boolean system;

    /**
     * 判断当前VO对象是否为系统级数据
     * <p>
     * 该方法用于快速判断当前VO对象对应的数据是否被标记为系统级数据。
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
     * // 1. 前端渲染时控制显示
     * {@code
     * &lt;div class="data-item" :class="{ 'system': vo.isSystem() }"&gt;
     *   {{ vo.name }}
     *   &lt;span v-if="vo.isSystem()" class="badge"&gt;系统&lt;/span&gt;
     * &lt;/div&gt;
     * }
     *
     * // 2. 控制操作按钮显示
     * {@code
     * &lt;button v-if="!vo.isSystem()" @click="deleteItem(vo)"&gt;
     *   删除
     * &lt;/button&gt;
     * }
     *
     * // 3. 后端权限校验
     * {@code
     * public void deleteData(String id) {
     *     DataVO vo = getDataById(id);
     *     if (vo.isSystem()) {
     *         throw new BusinessException("系统数据不允许删除");
     *     }
     *     // 执行删除
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
     * 该方法用于设置当前VO对象的系统级数据标识。
     * 通常在从Entity转换为VO时使用。
     * </p>
     * <p>
     * <b>使用场景：</b>
     * </p>
     * <ul>
     *   <li>从Entity转换VO时，复制系统标识</li>
     *   <li>在VO构建过程中设置系统标识</li>
     *   <li>在数据导出时标记数据来源</li>
     * </ul>
     * </p>
     * <p>
     * <b>使用示例：</b>
     * </p>
     * <pre>
     * // 场景1：Entity转VO时复制标识
     * {@code
     * public static RoleVO fromEntity(Role entity) {
     *     RoleVO vo = new RoleVO();
     *     vo.setRoleName(entity.getName());
     *     vo.setRoleDesc(entity.getDescription());
     *     vo.setSystem(entity.isSystem());  // 复制系统标识
     *     vo.setCreatedBy(entity.getCreatedBy());
     *     vo.setCreatedAt(formatDate(entity.getCreatedAt()));
     *     vo.setUpdatedBy(entity.getUpdatedBy());
     *     vo.setUpdatedAt(formatDate(entity.getUpdatedAt()));
     *     return vo;
     * }
     * }
     *
     * // 场景2：在Service层构建VO
     * {@code
     * public List&lt;RoleVO&gt; getRoleList() {
     *     List&lt;Role&gt; roles = roleRepository.findAll();
     *     return roles.stream()
     *         .map(role -&gt; {
     *             RoleVO vo = new RoleVO();
     *             vo.setRoleName(role.getName());
     *             vo.setSystem(role.isSystem());  // 设置系统标识
     *             return vo;
     *         })
     *         .collect(Collectors.toList());
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
