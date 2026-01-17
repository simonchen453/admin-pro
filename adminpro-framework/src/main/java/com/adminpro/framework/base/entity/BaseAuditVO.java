package com.adminpro.framework.base.entity;

/**
 * 带审计信息的视图对象（VO）基类
 * <p>
 * 该类继承自 {@link BaseVO}，在基础VO功能之上增加了审计字段。
 * 用于需要在视图中显示数据创建和修改信息的场景。
 * </p>
 * <p>
 * <b>审计字段说明：</b>
 * <table border="1" cellpadding="3" cellspacing="0">
 *   <tr><th>字段名</th><th>类型</th><th>说明</th><th>用途</th></tr>
 *   <tr><td>createdBy</td><td>String</td><td>创建者ID</td><td>显示创建人</td></tr>
 *   <tr><td>createdAt</td><td>String</td><td>创建时间</td><td>显示创建时间（格式化后）</td></tr>
 *   <tr><td>updatedBy</td><td>String</td><td>更新者ID</td><td>显示最后修改人</td></tr>
 *   <tr><td>updatedAt</td><td>String</td><td>更新时间</td><td>显示最后修改时间（格式化后）</td></tr>
 * </table>
 * </p>
 * <p>
 * <b>与Entity审计字段的区别：</b>
 * <ul>
 *   <li>Entity中时间字段类型为 {@link java.util.Date}</li>
 *   <li>VO中时间字段类型为 {@link String}（已格式化）</li>
 *   <li>VO更适合直接在前端展示，无需二次处理</li>
 * </ul>
 * </p>
 * <p>
 * <b>继承层次：</b>
 * </p>
 * <pre>
 * IVO
 *   └── BaseVO
 *         └── BaseAuditVO (本类)
 *               ├── 直接使用：需要审计信息的VO
 *               └── BaseSystemVO：系统级数据的VO
 *                     └── 具体业务VO类
 * </pre>
 * <p>
 * <b>使用场景：</b>
 * </p>
 * <pre>
 * // 定义用户VO（需要显示审计信息）
 * {@code
 * public class UserVO extends BaseAuditVO {
 *     private String username;
 *     private String email;
 *     private String phone;
 *     // 自动拥有审计字段：createdBy, createdAt, updatedBy, updatedAt
 *     // getters and setters...
 * }
 * }
 * </pre>
 * <p>
 * <b>Entity到VO的转换示例：</b>
 * </p>
 * <pre>
 * {@code
 * // 转换工具方法
 * public static UserVO fromEntity(User entity) {
 *     UserVO vo = new UserVO();
 *     vo.setUsername(entity.getUsername());
 *     vo.setEmail(entity.getEmail());
 *     vo.setPhone(entity.getPhone());
 *
 *     // 转换审计字段
 *     vo.setCreatedBy(entity.getCreatedBy());
 *     vo.setCreatedAt(formatDate(entity.getCreatedAt()));  // Date转String
 *     vo.setUpdatedBy(entity.getUpdatedBy());
 *     vo.setUpdatedAt(formatDate(entity.getUpdatedAt()));  // Date转String
 *
 *     return vo;
 * }
 *
 * // 日期格式化方法
 * private static String formatDate(Date date) {
 *     if (date == null) {
 *         return null;
 *     }
 *     return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
 * }
 * }
 * </pre>
 * <p>
 * <b>前端显示示例：</b>
 * </p>
 * <pre>
 * {@code
 * &lt;!-- 在列表中显示审计信息 --&gt;
 * &lt;table&gt;
 *   &lt;tr&gt;
 *     &lt;th&gt;用户名&lt;/th&gt;
 *     &lt;th&gt;邮箱&lt;/th&gt;
 *     &lt;th&gt;创建时间&lt;/th&gt;
 *     &lt;th&gt;更新时间&lt;/th&gt;
 *   &lt;/tr&gt;
 *   &lt;tr v-for="user in userList" :key="user.id"&gt;
 *     &lt;td&gt;{{ user.username }}&lt;/td&gt;
 *     &lt;td&gt;{{ user.email }}&lt;/td&gt;
 *     &lt;td&gt;{{ user.createdAt }}&lt;/td&gt;
 *     &lt;td&gt;{{ user.updatedAt }}&lt;/td&gt;
 *   &lt;/tr&gt;
 * &lt;/table&gt;
 *
 * &lt;!-- 在详情页显示审计信息 --&gt;
 * &lt;div class="audit-info"&gt;
 *   &lt;p&gt;创建人：{{ user.createdBy }}&lt;/p&gt;
 *   &lt;p&gt;创建时间：{{ user.createdAt }}&lt;/p&gt;
 *   &lt;p&gt;最后修改人：{{ user.updatedBy }}&lt;/p&gt;
 *   &lt;p&gt;最后修改时间：{{ user.updatedAt }}&lt;/p&gt;
 * &lt;/div&gt;
 * }
 * </pre>
 * <p>
 * <b>命名建议：</b>
 * <ul>
 *   <li>审计字段通常显示在列表/详情页的底部或侧边栏</li>
 *   <li>创建时间和更新时间可以格式化为更友好的格式（如"3分钟前"）</li>
 *   <li>创建者和更新者可以通过ID查询用户名后显示</li>
 *   <li>某些场景下可以隐藏审计字段（如移动端）</li>
 * </ul>
 * </p>
 * <p>
 * <b>性能优化建议：</b>
 * </p>
 * <ul>
 *   <li>在列表查询时，可以不返回审计字段（减少数据传输）</li>
 *   <li>在详情查询时，再返回完整的审计信息</li>
 *   <li>对于敏感数据，可以选择性隐藏审计字段</li>
 * </ul>
 * </p>
 *
 * @see BaseVO
 * @see BaseSystemVO
 * @see IVO
 * @see com.adminpro.framework.base.entity.BaseAuditEntity
 * @author AdminPro
 * @version 1.0.0
 */
public abstract class BaseAuditVO extends BaseVO {
    /**
     * 创建者标识
     * <p>
     * 记录创建该数据的用户ID或系统标识。
     * 与Entity中的createdBy字段对应，但类型为String。
     * </p>
     * <p>
     * <b>数据来源：</b>
     * <ul>
     *   <li>通常从Entity的createdBy字段复制而来</li>
     *   <li>可以存储用户ID或系统标识</li>
     *   <li>可以为null（如系统初始化数据）</li>
     * </ul>
     * </p>
     * <p>
     * <b>前端处理：</b>
     * </p>
     * <pre>
     * // 1. 直接显示用户ID
     * {{ vo.createdBy }}
     *
     * // 2. 通过ID查询用户名
     * &lt;span&gt;创建人：{{ getUserName(vo.createdBy) }}&lt;/span&gt;
     *
     * // 3. 系统数据显示特殊标识
     * &lt;span v-if="vo.createdBy === 'SYSTEM'"&gt;系统创建&lt;/span&gt;
     * &lt;span v-else&gt;创建人：{{ getUserName(vo.createdBy) }}&lt;/span&gt;
     * </pre>
     */
    private String createdBy;

    /**
     * 创建时间
     * <p>
     * 记录数据创建的时间戳，已格式化为字符串。
     * 与Entity中的createdAt字段对应，但类型为String。
     * </p>
     * <p>
     * <b>格式化建议：</b>
     * <ul>
     *   <li>标准格式：yyyy-MM-dd HH:mm:ss（如：2024-01-15 14:30:25）</li>
     *   <li>友好格式：可以根据用户语言环境显示（如：Jan 15, 2024）</li>
     *   <li>相对时间：如"3分钟前"、"2小时前"等</li>
     * </ul>
     * </p>
     * <p>
     * <b>转换示例：</b>
     * </p>
     * <pre>
     * {@code
     * // 从Entity的Date转换为String
     * public String formatCreatedAt(Date createdAt) {
     *     if (createdAt == null) {
     *         return null;
     *     }
     *     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     *     return sdf.format(createdAt);
     * }
     *
     * // 在转换VO时使用
     * vo.setCreatedAt(formatCreatedAt(entity.getCreatedAt()));
     * }
     * </pre>
     * <p>
     * <b>前端显示：</b>
     * </p>
     * <pre>
     * &lt;!-- 标准格式显示 --&gt;
     * &lt;span&gt;创建时间：{{ vo.createdAt }}&lt;/span&gt;
     *
     * &lt;!-- 使用时间库显示相对时间 --&gt;
     * &lt;span&gt;{{ moment(vo.createdAt).fromNow() }}&lt;/span&gt;
     * </pre>
     */
    private String createdAt;

    /**
     * 更新者标识
     * <p>
     * 记录最后一次修改数据的用户ID或系统标识。
     * 与Entity中的updatedBy字段对应，但类型为String。
     * </p>
     * <p>
     * <b>数据来源：</b>
     * <ul>
     *   <li>通常从Entity的updatedBy字段复制而来</li>
     *   <li>记录最后一次修改操作的用户</li>
     *   <li>可以为null（如从未更新过）</li>
     * </ul>
     * </p>
     * <p>
     * <b>前端处理：</b>
     * </p>
     * <pre>
     * // 显示最后修改人
     * &lt;span&gt;最后修改人：{{ getUserName(vo.updatedBy) }}&lt;/span&gt;
     *
     * // 与创建人对比显示
     * &lt;div v-if="vo.createdBy !== vo.updatedBy"&gt;
     *   &lt;p&gt;创建人：{{ getUserName(vo.createdBy) }}&lt;/p&gt;
     *   &lt;p&gt;修改人：{{ getUserName(vo.updatedBy) }}&lt;/p&gt;
     * &lt;/div&gt;
     * </pre>
     */
    private String updatedBy;

    /**
     * 更新时间
     * <p>
     * 记录数据最后一次修改的时间戳，已格式化为字符串。
     * 与Entity中的updatedAt字段对应，但类型为String。
     * </p>
     * <p>
     * <b>格式化建议：</b>
     * <ul>
     *   <li>标准格式：yyyy-MM-dd HH:mm:ss</li>
     *   <li>友好格式：根据用户语言环境显示</li>
     *   <li>相对时间：如"刚刚"、"5分钟前"等</li>
     * </ul>
     * </p>
     * <p>
     * <b>转换示例：</b>
     * </p>
     * <pre>
     * {@code
     * // 从Entity的Date转换为String
     * public String formatUpdatedAt(Date updatedAt) {
     *     if (updatedAt == null) {
     *         return null;
     *     }
     *     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     *     return sdf.format(updatedAt);
     * }
     *
     * // 在转换VO时使用
     * vo.setUpdatedAt(formatUpdatedAt(entity.getUpdatedAt()));
     * }
     * </pre>
     * <p>
     * <b>前端显示：</b>
     * </p>
     * <pre>
     * &lt;!-- 显示最后修改时间 --&gt;
     * &lt;span&gt;最后修改时间：{{ vo.updatedAt }}&lt;/span&gt;
     *
     * &lt;!-- 使用相对时间 --&gt;
     * &lt;span&gt;{{ moment(vo.updatedAt).fromNow() }} 更新&lt;/span&gt;
     *
     * &lt;!-- 与创建时间对比 --&gt;
     * &lt;div&gt;
     *   &lt;p&gt;创建于：{{ vo.createdAt }}&lt;/p&gt;
     *   &lt;p&gt;更新于：{{ vo.updatedAt }}&lt;/p&gt;
     * &lt;/div&gt;
     * </pre>
     */
    private String updatedAt;

    /**
     * 获取创建者标识
     * <p>
     * 返回创建该数据的用户ID或系统标识。
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
     * 设置创建该数据的用户ID或系统标识。
     * 通常在从Entity转换为VO时调用。
     * </p>
     *
     * @param createdBy 创建者ID，可以为null
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * 获取创建时间
     * <p>
     * 返回数据创建的时间戳（已格式化为字符串）。
     * </p>
     *
     * @return 格式化后的创建时间字符串，如果未设置则返回null
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置创建时间
     * <p>
     * 设置数据创建的时间戳。
     * 通常在从Entity转换VO时，将Date类型的createdAt转换为String。
     * </p>
     * <p>
     * <b>使用示例：</b>
     * </p>
     * <pre>
     * {@code
     * // 从Entity转换时设置
     * Date entityCreatedAt = entity.getCreatedAt();
     * String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
     *     .format(entityCreatedAt);
     * vo.setCreatedAt(formattedDate);
     * }
     * </pre>
     *
     * @param createdAt 格式化后的创建时间字符串，可以为null
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 获取更新者标识
     * <p>
     * 返回最后一次修改数据的用户ID或系统标识。
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
     * 设置最后一次修改数据的用户ID或系统标识。
     * 通常在从Entity转换为VO时调用。
     * </p>
     *
     * @param updatedBy 更新者ID，可以为null
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * 获取更新时间
     * <p>
     * 返回数据最后一次修改的时间戳（已格式化为字符串）。
     * </p>
     *
     * @return 格式化后的更新时间字符串，如果从未更新则可能返回null
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 设置更新时间
     * <p>
     * 设置数据最后一次修改的时间戳。
     * 通常在从Entity转换VO时，将Date类型的updatedAt转换为String。
     * </p>
     * <p>
     * <b>使用示例：</b>
     * </p>
     * <pre>
     * {@code
     * // 从Entity转换时设置
     * Date entityUpdatedAt = entity.getUpdatedAt();
     * String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
     *     .format(entityUpdatedAt);
     * vo.setUpdatedAt(formattedDate);
     * }
     * </pre>
     *
     * @param updatedAt 格式化后的更新时间字符串，可以为null
     */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
