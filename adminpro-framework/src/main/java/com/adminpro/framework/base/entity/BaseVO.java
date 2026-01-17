package com.adminpro.framework.base.entity;

/**
 * 框架视图对象（VO）基类
 * <p>
 * VO（Value Object，值对象）模式用于封装业务层返回给展示层的数据。
 * 该类是所有VO类的基类，实现了 {@link IVO} 接口，提供统一的VO基础能力。
 * </p>
 * <p>
 * <b>VO的作用：</b>
 * <ul>
 *   <li>数据传输：在不同层之间传输数据</li>
 *   <li>视图展示：为前端展示提供结构化数据</li>
 *   <li>数据隔离：避免直接暴露实体对象</li>
 *   <li>性能优化：只传输必要的数据，减少冗余</li>
 * </ul>
 * </p>
 * <p>
 * <b>VO与Entity的区别：</b>
 * <table border="1" cellpadding="3" cellspacing="0">
 *   <tr><th>特性</th><th>Entity（实体）</th><th>VO（视图对象）</th></tr>
 *   <tr><td>用途</td><td>数据库映射，业务逻辑</td><td>数据展示，接口返回</td></tr>
 *   <tr><td>字段</td><td>包含所有数据库字段</td><td>只包含展示需要的字段</td></tr>
 *   <tr><td>关系</td><td>包含关联关系（@OneToMany等）</td><td>扁平化结构，无复杂关系</td></tr>
 *   <tr><td>注解</td><td>JPA注解（@Entity, @Table等）</td><td>校验注解（@NotNull, @Size等）</td></tr>
 *   <tr><td>生命周期</td><td>由JPA管理</td><td>临时对象，无状态</td></tr>
 * </table>
 * </p>
 * <p>
 * <b>继承层次：</b>
 * </p>
 * <pre>
 * IVO (接口)
 *   └── BaseVO (本类)
 *         ├── 直接使用：简单VO
 *         └── BaseAuditVO：需要审计信息的VO
 *               └── BaseSystemVO：系统级数据的VO
 *                     └── 具体业务VO类
 * </pre>
 * <p>
 * <b>使用场景：</b>
 * </p>
 * <pre>
 * // 场景1：简单的VO，无需审计信息
 * {@code
 * public class ConfigVO extends BaseVO {
 *     private String configKey;
 *     private String configValue;
 *     // getters and setters...
 * }
 * }
 *
 * // 场景2：需要显示审计信息，继承 BaseAuditVO
 * {@code
 * public class UserVO extends BaseAuditVO {
 *     private String username;
 *     private String email;
 *     // 自动拥有：createdBy, createdAt, updatedBy, updatedAt
 * }
 * }
 *
 * // 场景3：系统级数据，继承 BaseSystemVO
 * {@code
 * public class RoleVO extends BaseSystemVO {
 *     private String roleName;
 *     // 自动拥有：系统标识 + 审计信息
 * }
 * }
 * </pre>
 * <p>
 * <b>命名规范：</b>
 * <ul>
 *   <li>VO类以 "VO" 结尾，如 UserVO、OrderVO</li>
 *   <li>列表VO通常以 "ListVO" 或 "QueryVO" 结尾</li>
 *   <li>详情VO通常以 "DetailVO" 或 "InfoVO" 结尾</li>
 *   <li>表单提交VO通常以 "FormVO" 或 "SubmitVO" 结尾</li>
 * </ul>
 * </p>
 * <p>
 * <b>最佳实践：</b>
 * </p>
 * <ul>
 *   <li>VO应该是不可变的（final字段），创建后不修改</li>
 *   <li>使用构造函数或Builder模式创建VO</li>
 *   <li>添加JSR-303校验注解（@NotNull, @Size等）</li>
 *   <li>提供fromEntity()静态方法转换Entity到VO</li>
 *   <li>避免在VO中包含业务逻辑</li>
 * </ul>
 * </p>
 *
 * @see IVO
 * @see BaseAuditVO
 * @see BaseSystemVO
 * @author AdminPro
 * @version 1.0.0
 */
public abstract class BaseVO implements IVO {
}
