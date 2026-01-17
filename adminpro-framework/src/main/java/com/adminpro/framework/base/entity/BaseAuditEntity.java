package com.adminpro.framework.base.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

/**
 * 框架审计实体基类
 * <p>
 * 该类继承自 {@link BaseEntity}，在基础实体功能之上增加了审计字段，
 * 用于记录数据的创建和修改信息。审计字段包括创建者、创建时间、更新者、更新时间。
 * </p>
 * <p>
 * <b>审计字段说明：</b>
 * <table border="1" cellpadding="3" cellspacing="0">
 *   <tr><th>字段名</th><th>类型</th><th>说明</th><th>用途</th></tr>
 *   <tr><td>createdBy</td><td>String</td><td>创建者ID</td><td>记录数据创建人</td></tr>
 *   <tr><td>createdAt</td><td>Date</td><td>创建时间</td><td>记录数据创建时间</td></tr>
 *   <tr><td>updatedBy</td><td>String</td><td>更新者ID</td><td>记录最后修改人</td></tr>
 *   <tr><td>updatedAt</td><td>Date</td><td>更新时间</td><td>记录最后修改时间</td></tr>
 * </table>
 * </p>
 * <p>
 * <b>主要功能：</b>
 * <ul>
 *   <li>提供审计字段的定义和getter/setter方法</li>
 *   <li>提供清空审计字段的方法（数据复制时使用）</li>
 *   <li>定义审计字段的列名常量（用于查询构建）</li>
 * </ul>
 * </p>
 * <p>
 * <b>继承层次：</b>
 * </p>
 * <pre>
 * Object
 *   └── BaseEntity
 *         └── BaseAuditEntity (本类)
 *               └── 具体业务实体类
 * </pre>
 * <p>
 * <b>使用场景：</b>
 * </p>
 * <pre>
 * // 定义实体类
 * {@code
 * public class User extends BaseAuditEntity {
 *     private String username;
 *     private String email;
 *     // 自动拥有审计字段
 * }
 * }
 *
 * // 创建数据时设置审计信息
 * {@code
 * User user = new User();
 * user.setUsername("zhangsan");
 * user.setCreatedBy(currentUser.getId());
 * user.setCreatedAt(new Date());
 * userService.save(user);
 * }
 *
 * // 更新数据时更新审计信息
 * {@code
 * User user = userService.getById(userId);
 * user.setEmail("new@example.com");
 * user.setUpdatedBy(currentUser.getId());
 * user.setUpdatedAt(new Date());
 * userService.update(user);
 * }
 * </pre>
 * <p>
 * <b>自动填充：</b>
 * </p>
 * 在实际应用中，审计字段通常通过框架自动填充，例如：
 * <ul>
 *   <li>使用 JPA 的 @PrePersist 和 @PreUpdate 生命周期回调</li>
 *   <li>使用 MyBatis-Plus 的自动填充功能</li>
 *   <li>使用 Spring Data 的 Auditing 功能</li>
 * </ul>
 *
 * @see BaseEntity
 * @see com.fasterxml.jackson.annotation.JsonIgnore
 * @author AdminPro
 * @version 1.0.0
 */
public abstract class BaseAuditEntity extends BaseEntity {
    /**
     * 创建者标识
     * <p>
     * 记录创建该数据实体的用户ID或其他标识符。
     * </p>
     * <p>
     * <b>存储格式：</b>
     * <ul>
     *   <li>通常存储用户ID（String类型）</li>
     *   <li>可以存储系统标识（如"SYSTEM"）</li>
     *   <li>可以为null（如系统初始化数据）</li>
     * </ul>
     * </p>
     * <p>
     * <b>使用示例：</b>
     * </p>
     * <pre>
     * // 创建数据时设置创建者
     * entity.setCreatedBy(currentUser.getId());
     * repository.save(entity);
     *
     * // 查询数据时显示创建者
     * User creator = userService.getById(entity.getCreatedBy());
     * </pre>
     */
    private String createdBy;

    /**
     * 创建时间
     * <p>
     * 记录数据实体创建的时间戳。
     * </p>
     * <p>
     * <b>时间精度：</b>
     * <ul>
     *   <li>精确到毫秒级</li>
     *   <li>使用系统默认时区</li>
     *   <li>数据库字段类型通常为 DATETIME 或 TIMESTAMP</li>
     * </ul>
     * </p>
     * <p>
     * <b>使用示例：</b>
     * </p>
     * <pre>
     * // 创建数据时设置创建时间
     * entity.setCreatedAt(new Date());
     * repository.save(entity);
     *
     * // 按创建时间查询
     * {@code
     * List<Entity> list = repository.findByCreatedAtAfter(startDate);
     * }
     * </pre>
     */
    private Date createdAt;

    /**
     * 更新者标识
     * <p>
     * 记录最后一次修改该数据实体的用户ID或其他标识符。
     * </p>
     * <p>
     * <b>更新规则：</b>
     * <ul>
     *   <li>每次更新数据时都应该更新此字段</li>
     *   <li>通常与 updatedAt 字段同时更新</li>
     *   <li>首次创建时可以与 createdBy 相同</li>
     * </ul>
     * </p>
     * <p>
     * <b>使用示例：</b>
     * </p>
     * <pre>
     * // 更新数据时设置更新者
     * entity.setUpdatedBy(currentUser.getId());
     * repository.save(entity);
     * </pre>
     */
    private String updatedBy;

    /**
     * 更新时间
     * <p>
     * 记录数据实体最后一次修改的时间戳。
     * </p>
     * <p>
     * <b>更新规则：</b>
     * <ul>
     *   <li>每次更新数据时都应该更新此字段</li>
     *   <li>通常与 updatedBy 字段同时更新</li>
     *   <li>用于实现乐观锁版本控制</li>
     * </ul>
     * </p>
     * <p>
     * <b>使用示例：</b>
     * </p>
     * <pre>
     * // 更新数据时设置更新时间
     * entity.setUpdatedAt(new Date());
     * repository.save(entity);
     *
     * // 检查数据是否被修改
     * {@code
     * if (entity.getUpdatedAt().after(lastCheckTime)) {
     *     // 数据已被修改
     * }
     * }
     * </pre>
     */
    private Date updatedAt;

    /**
     * 创建时间字段列名常量
     * <p>
     * 用于动态查询构建、SQL拼接等场景，避免硬编码字段名。
     * </p>
     * <p>
     * <b>使用示例：</b>
     * </p>
     * <pre>
     * // 在查询构建器中使用
     * {@code
     * query.addSort(BaseAuditEntity.COL_CREATED_AT, SortOrder.DESC);
     * }
     * </pre>
     */
    public static final String COL_CREATED_AT = "COL_CREATED_AT";

    /**
     * 创建者字段列名常量
     * <p>
     * 用于动态查询构建、SQL拼接等场景，避免硬编码字段名。
     * </p>
     * <p>
     * <b>使用示例：</b>
     * </p>
     * <pre>
     * // 在查询构建器中使用
     * {@code
     * query.addFilter(BaseAuditEntity.COL_CREATED_BY, FilterOperator.EQ, userId);
     * }
     * </pre>
     */
    public static final String COL_CREATED_BY = "COL_CREATED_BY";

    /**
     * 更新时间字段列名常量
     * <p>
     * 用于动态查询构建、SQL拼接等场景，避免硬编码字段名。
     * </p>
     * <p>
     * <b>使用示例：</b>
     * </p>
     * <pre>
     * // 在查询构建器中使用
     * {@code
     * query.addSort(BaseAuditEntity.COL_UPDATED_AT, SortOrder.DESC);
     * }
     * </pre>
     */
    public static final String COL_UPDATED_AT = "COL_UPDATED_AT";

    /**
     * 更新者字段列名常量
     * <p>
     * 用于动态查询构建、SQL拼接等场景，避免硬编码字段名。
     * </p>
     * <p>
     * <b>使用示例：</b>
     * </p>
     * <pre>
     * // 在查询构建器中使用
     * {@code
     * query.addFilter(BaseAuditEntity.COL_UPDATED_BY, FilterOperator.EQ, userId);
     * }
     * </pre>
     */
    public static final String COL_UPDATED_BY = "COL_UPDATED_BY";

    /**
     * 获取创建者标识
     * <p>
     * 返回创建该数据实体的用户ID或系统标识。
     * </p>
     *
     * @return 创建者ID，如果未设置则返回null
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * 设置创建者标识
     * <p>
     * 设置创建该数据实体的用户ID或系统标识。
     * 通常在数据首次保存时设置，后续不应修改。
     * </p>
     *
     * @param createdBy 创建者ID，可以为null
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * 获取更新者标识
     * <p>
     * 返回最后一次修改该数据实体的用户ID或系统标识。
     * </p>
     *
     * @return 更新者ID，如果从未更新则可能返回null
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * 设置更新者标识
     * <p>
     * 设置最后一次修改该数据实体的用户ID或系统标识。
     * 每次更新数据时都应该更新此字段。
     * </p>
     *
     * @param updatedBy 更新者ID，可以为null
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * 获取创建时间
     * <p>
     * 返回数据实体的创建时间戳。
     * </p>
     *
     * @return 创建时间，如果未设置则返回null
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置创建时间
     * <p>
     * 设置数据实体的创建时间戳。
     * 通常在数据首次保存时设置，后续不应修改。
     * </p>
     *
     * @param createdAt 创建时间，可以为null
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 获取更新时间
     * <p>
     * 返回数据实体最后一次修改的时间戳。
     * </p>
     *
     * @return 更新时间，如果从未更新则可能返回null
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 设置更新时间
     * <p>
     * 设置数据实体最后一次修改的时间戳。
     * 每次更新数据时都应该更新此字段。
     * </p>
     *
     * @param updatedAt 更新时间，可以为null
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 清空审计时间字段
     * <p>
     * 该方法用于清空实体的创建时间和更新时间字段。
     * </p>
     * <p>
     * <b>使用场景：</b>
     * </p>
     * <ul>
     *   <li>数据复制：复制实体时，避免复制时间字段</li>
     *   <li>数据导入：导入数据时，让框架自动生成新的时间戳</li>
     *   <li>数据迁移：迁移数据时，重新设置审计时间</li>
     *   <li>模板处理：基于模板创建新实体时</li>
     * </ul>
     * </p>
     * <p>
     * <b>使用示例：</b>
     * </p>
     * <pre>
     * // 复制实体时清空时间字段
     * {@code
     * Entity newEntity = new Entity();
     * BeanUtils.copyProperties(oldEntity, newEntity);
     * newEntity.emptyAuditTime();  // 清空时间
     * newEntity.setCreatedAt(new Date());  // 设置新的创建时间
     * repository.save(newEntity);
     * }
     * </pre>
     * <p>
     * <b>注意事项：</b>
     * 该方法使用了 {@code @JsonIgnore} 注解，
     * 表示在JSON序列化时忽略此方法。
     * </p>
     *
     * @see com.fasterxml.jackson.annotation.JsonIgnore
     * @see #emptyAudit()
     */
    @JsonIgnore
    public void emptyAuditTime() {
        this.createdAt = null;
        this.updatedAt = null;
    }

    /**
     * 清空所有审计字段
     * <p>
     * 该方法用于清空实体的所有审计字段，包括创建者、创建时间、更新者、更新时间。
     * </p>
     * <p>
     * <b>使用场景：</b>
     * </p>
     * <ul>
     *   <li>数据复制：完全复制实体时，重新生成所有审计信息</li>
     *   <li>数据导入：导入数据时，使用当前用户作为创建者和更新者</li>
     *   <li>数据脱敏：导出或分享数据时，移除审计信息</li>
     *   <li>数据重置：重置实体状态时</li>
     * </ul>
     * </p>
     * <p>
     * <b>使用示例：</b>
     * </p>
     * <pre>
     * // 复制实体时清空所有审计字段
     * {@code
     * Entity newEntity = new Entity();
     * BeanUtils.copyProperties(oldEntity, newEntity);
     * newEntity.emptyAudit();  // 清空所有审计字段
     *
     * // 设置新的审计信息
     * newEntity.setCreatedBy(currentUser.getId());
     * newEntity.setCreatedAt(new Date());
     * newEntity.setUpdatedBy(currentUser.getId());
     * newEntity.setUpdatedAt(new Date());
     * repository.save(newEntity);
     * }
     * </pre>
     * <p>
     * <b>注意事项：</b>
     * <ul>
     *   <li>清空后应立即设置新的审计信息</li>
     *   <li>保存到数据库前必须确保审计字段已正确设置</li>
     *   <li>该方法使用了 {@code @JsonIgnore} 注解，在JSON序列化时忽略</li>
     * </ul>
     * </p>
     *
     * @see com.fasterxml.jackson.annotation.JsonIgnore
     * @see #emptyAuditTime()
     */
    @JsonIgnore
    public void emptyAudit() {
        this.createdAt = null;
        this.updatedAt = null;
        this.createdBy = null;
        this.updatedBy = null;
    }
}
