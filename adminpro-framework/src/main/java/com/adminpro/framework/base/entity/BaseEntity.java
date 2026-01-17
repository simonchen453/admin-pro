package com.adminpro.framework.base.entity;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 框架基础实体类
 * <p>
 * 该类是整个框架实体层的基础类，所有实体类的根类。
 * 提供了对象序列化、字符串转换和哈希码生成的基础能力。
 * </p>
 * <p>
 * <b>核心功能：</b>
 * <ul>
 *   <li>实现 {@link IEntity} 接口，提供统一的实体标识</li>
 *   <li>提供基于反射的 toString() 方法实现</li>
 *   <li>提供基于反射的 hashCode() 方法实现</li>
 *   <li>支持序列化，通过 serialVersionUID 保证版本兼容性</li>
 * </ul>
 * </p>
 * <p>
 * <b>设计理念：</b>
 * </p>
 * <ul>
 *   <li>最小化基础：只提供最基础、最通用的功能</li>
 *   <li>可扩展性：通过继承链逐步增加功能</li>
 *   <li>一致性：统一所有实体的基础行为</li>
 * </ul>
 * </p>
 * <p>
 * <b>继承层次：</b>
 * </p>
 * <pre>
 * Object
 *   └── BaseEntity (本类)
 *         ├── 直接使用：简单实体，无需审计功能
 *         └── BaseAuditEntity：需要审计功能的实体
 *               └── 业务实体类
 * </pre>
 * <p>
 * <b>使用场景：</b>
 * </p>
 * <pre>
 * // 场景1：简单实体，无需审计功能
 * {@code
 * public class Config extends BaseEntity {
 *     private String configKey;
 *     private String configValue;
 *     // getters and setters...
 * }
 * }
 *
 * // 场景2：需要审计功能，继承 BaseAuditEntity
 * {@code
 * public class User extends BaseAuditEntity {
 *     private String username;
 *     // 自动拥有：createdBy, createdAt, updatedBy, updatedAt
 * }
 * }
 * </pre>
 * <p>
 * <b>注意事项：</b>
 * </p>
 * <ul>
 *   <li>该类使用反射实现 toString() 和 hashCode()，性能略低于手动实现</li>
 *   <li>如需高性能，建议在具体实体类中重写这些方法</li>
 *   <li>equals() 方法未实现，需要根据业务需求在子类中实现</li>
 *   <li>序列化ID固定，修改类结构时需注意兼容性</li>
 * </ul>
 *
 * @see IEntity
 * @see BaseAuditEntity
 * @see org.apache.commons.lang3.builder.ToStringBuilder
 * @see org.apache.commons.lang3.builder.HashCodeBuilder
 * @author AdminPro
 * @version 1.0.0
 */
public class BaseEntity implements IEntity {
    /**
     * 序列化版本唯一标识符
     * <p>
     * 该字段用于在序列化和反序列化过程中标识类的版本，
     * 确保类的结构变化不会导致序列化兼容性问题。
     * </p>
     * <p>
     * <b>作用：</b>
     * <ul>
     *   <li>版本控制：在反序列化时验证类的版本是否兼容</li>
     *   <li>跨JVM传输：确保在不同JVM实例间传输对象时的一致性</li>
     *   <li>持久化存储：在序列化到文件或数据库后能够正确恢复</li>
     * </ul>
     * </p>
     * <p>
     * <b>注意事项：</b>
     * <ul>
     *   <li>修改该类的字段结构时，需要评估是否需要更新此值</li>
     *   <li>如果不确定，建议保持不变，依赖JVM的默认生成机制</li>
     *   <li>显式定义此值可以避免JVM实现差异导致的兼容性问题</li>
     * </ul>
     * </p>
     */
    private static final long serialVersionUID = 5419738720360145404L;

    /**
     * 生成对象的字符串表示
     * <p>
     * 该方法使用 Apache Commons Lang 的 {@link ToStringBuilder}
     * 通过反射机制自动生成对象的字符串表示，包含所有字段的信息。
     * </p>
     * <p>
     * <b>输出格式：</b>
     * </p>
     * <pre>
     * ClassName[field1=value1,field2=value2,...]
     * </pre>
     * <p>
     * <b>使用示例：</b>
     * </p>
     * <pre>
     * User user = new User();
     * user.setId("123");
     * user.setName("张三");
     * System.out.println(user.toString());
     * // 输出：User[id=123,name=张三,...]
     * </pre>
     * <p>
     * <b>性能考虑：</b>
     * <ul>
     *   <li>反射调用有一定性能开销</li>
     *   <li>高频场景建议重写此方法以提高性能</li>
     *   <li>调试和日志场景非常适合使用</li>
     * </ul>
     * </p>
     * <p>
     * <b>重写建议：</b>
     * </p>
     * <pre>
     * {@code
     * @Override
     * public String toString() {
     *     return new ToStringBuilder(this)
     *         .append("id", id)
     *         .append("name", name)
     *         .toString();
     * }
     * }
     * </pre>
     *
     * @return 对象的字符串表示，包含类名和所有字段值
     * @see org.apache.commons.lang3.builder.ToStringBuilder#reflectionToString(Object)
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * 生成对象的哈希码
     * <p>
     * 该方法使用 Apache Commons Lang 的 {@link HashCodeBuilder}
     * 通过反射机制自动生成对象的哈希码。
     * </p>
     * <p>
     * <b>哈希码特性：</b>
     * <ul>
     *   <li>基于对象的所有字段计算</li>
     *   <li>相同的对象（字段值相同）总是产生相同的哈希码</li>
     *   <li>满足 hashCode() 方法的通用约定</li>
     * </ul>
     * </p>
     * <p>
     * <b>使用场景：</b>
     * </p>
     * <pre>
     * // 1. 在集合中使用
     * {@code
     * Set<BaseEntity> entitySet = new HashSet<>();
     * entitySet.add(entity1);
     * entitySet.add(entity2);
     * }
     *
     * // 2. 作为HashMap的键
     * {@code
     * Map<BaseEntity, String> map = new HashMap<>();
     * map.put(entity, "value");
     * }
     *
     * // 3. 对象比较
     * {@code
     * if (entity1.hashCode() == entity2.hashCode()) {
     *     // 可能相等，需要进一步用 equals() 确认
     * }
     * }
     * </pre>
     * <p>
     * <b>注意事项：</b>
     * </p>
     * <ul>
     *   <li>该实现基于反射，性能可能不如手动实现</li>
     *   <li>通常需要同时重写 equals() 方法</li>
     *   <li>作为HashMap键时，确保对象不可变</li>
     *   <li>包含在集合中的对象不应修改影响哈希码的字段</li>
     * </ul>
     * </p>
     * <p>
     * <b>重写建议：</b>
     * </p>
     * <pre>
     * {@code
     * @Override
     * public int hashCode() {
     *     return new HashCodeBuilder(17, 37)
     *         .append(id)
     *         .append(name)
     *         .toHashCode();
     * }
     * }
     * </pre>
     *
     * @return 对象的哈希码值
     * @see org.apache.commons.lang3.builder.HashCodeBuilder#reflectionHashCode(Object)
     * @see Object#hashCode()
     * @see Object#equals(Object)
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
