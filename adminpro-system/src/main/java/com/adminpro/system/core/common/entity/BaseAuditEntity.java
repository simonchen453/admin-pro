package com.adminpro.system.core.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

/**
 * 系统核心模块的审计实体基类
 * <p>
 * 该类继承自框架层的 {@link com.adminpro.framework.base.entity.BaseAuditEntity}，
 * 为系统核心模块提供审计功能扩展。审计字段包括创建者、创建时间、更新者、更新时间，
 * 用于追踪数据的创建和修改历史。
 * </p>
 * <p>
 * 该类在框架层审计功能的基础上，提供了额外的业务方法：
 * <ul>
 *   <li>清空审计时间字段的方法</li>
 *   <li>判断数据所有者的方法，用于权限控制</li>
 * </ul>
 * </p>
 * <p>
 * <b>使用场景：</b>
 * </p>
 * <pre>
 * public class User extends BaseAuditEntity {
 *     private String username;
 *     // 其他字段...
 * }
 * </pre>
 * <p>
 * <b>继承关系：</b>
 * </p>
 * <pre>
 * Object
 *   └── BaseEntity (框架层)
 *         └── BaseAuditEntity (框架层)
 *               └── BaseAuditEntity (系统核心层，本类)
 *                     └── 具体业务实体类
 * </pre>
 *
 * @see com.adminpro.framework.base.entity.BaseAuditEntity
 * @see com.adminpro.framework.base.entity.BaseEntity
 * @author AdminPro
 * @version 1.0.0
 */
public abstract class BaseAuditEntity extends com.adminpro.framework.base.entity.BaseAuditEntity {

    /**
     * 清空审计时间字段
     * <p>
     * 该方法用于清空实体的创建时间和更新时间字段。
     * 通常在数据复制、数据导入或特殊业务场景下使用，
     * 避免审计时间字段被错误地保留或复制。
     * </p>
     * <p>
     * 该方法使用了 {@code @JsonIgnore} 注解，表示在JSON序列化时忽略此方法。
     * </p>
     * <p>
     * <b>使用示例：</b>
     * </p>
     * <pre>
     * User newUser = new User();
     * newUser.copyFrom(oldUser);
     * newUser.emptyAuditTime();  // 清空时间字段，重新生成
     * </pre>
     *
     * @see com.fasterxml.jackson.annotation.JsonIgnore
     */
    @Override
    @JsonIgnore
    public void emptyAuditTime() {
        this.setCreatedAt(null);
        this.setUpdatedAt(null);
    }

    /**
     * 判断指定用户是否为数据的所有者
     * <p>
     * 该方法用于权限控制，判断当前实体是否由指定用户ID的用户创建。
     * 通过比较创建者ID与传入的用户ID，确定数据所有权。
     * </p>
     * <p>
     * <b>判断逻辑：</b>
     * <ul>
     *   <li>如果传入的用户ID为空或null，返回 {@code false}</li>
     *   <li>如果当前实体的创建者ID与传入的用户ID相同，返回 {@code true}</li>
     *   <li>否则返回 {@code false}</li>
     * </ul>
     * </p>
     * <p>
     * 该方法使用了 {@code @JsonIgnore} 注解，表示在JSON序列化时忽略此方法。
     * </p>
     * <p>
     * <b>使用场景：</b>
     * </p>
     * <pre>
     * // 在Controller中检查操作权限
     * if (entity.isOwner(currentUser.getId())) {
     *     // 允许修改
     *     entity.setData(newData);
     * } else {
     *     throw new AccessDeniedException("无权修改此数据");
     * }
     * </pre>
     *
     * @param userId 要判断的用户ID，不能为null
     * @return 如果该用户是数据的创建者（所有者），返回 {@code true}；否则返回 {@code false}
     * @see org.apache.commons.lang3.StringUtils#isEmpty(String)
     * @see org.apache.commons.lang3.StringUtils#equals(String, String)
     */
    @JsonIgnore
    public boolean isOwner(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return false;
        }

        if (StringUtils.equals(userId, getCreatedBy())) {
            return true;
        } else {
            return false;
        }
    }
}
