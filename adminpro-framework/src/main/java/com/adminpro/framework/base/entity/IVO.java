package com.adminpro.framework.base.entity;

import java.io.Serializable;

/**
 * 视图对象（VO）接口
 * <p>
 * VO（Value Object，值对象）接口，定义了所有视图对象必须实现的基础契约。
 * 该接口继承自 {@link Serializable}，确保所有VO对象都可以被序列化。
 * </p>
 * <p>
 * <b>设计目的：</b>
 * <ul>
 *   <li>统一标识：标记所有VO类，便于类型识别和处理</li>
 *   <li>序列化支持：确保VO可以在网络传输和持久化中使用</li>
 *   <li>类型安全：在方法参数和返回值中使用IVO接口，提高代码可读性</li>
 *   <li>扩展性：未来可以在接口中添加通用方法</li>
 * </ul>
 * </p>
 * <p>
 * <b>序列化说明：</b>
 * </p>
 * <ul>
 *   <li>支持Java原生序列化机制</li>
 *   <li>支持JSON序列化（通过Jackson等框架）</li>
 *   <li>支持跨JVM传输（RMI、RPC调用）</li>
 *   <li>支持会话存储和缓存</li>
 * </ul>
 * </p>
 * <p>
 * <b>使用场景：</b>
 * </p>
 * <pre>
 * // 场景1：作为方法返回值类型
 * {@code
 * public List&lt;? extends IVO&gt; getUserList() {
 *     List&lt;User&gt; users = userRepository.findAll();
 *     return users.stream()
 *         .map(this::toVO)
 *         .collect(Collectors.toList());
 * }
 * }
 *
 * // 场景2：作为方法参数类型
 * {@code
 * public void saveVO(IVO vo) {
 *     if (vo instanceof UserVO) {
 *         UserVO userVO = (UserVO) vo;
 *         // 保存逻辑
 *     }
 * }
 * }
 *
 * // 场景3：类型检查
 * {@code
 * public boolean isVO(Object obj) {
 *     return obj instanceof IVO;
 * }
 * }
 *
 * // 场景4：序列化到缓存
 * {@code
 * public void cacheVO(String key, IVO vo) {
 *     // 由于IVO继承Serializable，可以直接序列化
 *     cacheManager.put(key, vo);
 * }
 * }
 * </pre>
 * <p>
 * <b>实现示例：</b>
 * </p>
 * <pre>
 * // 方式1：直接实现IVO接口
 * {@code
 * public class ConfigVO implements IVO {
 *     private String configKey;
 *     private String configValue;
 *     // getters and setters...
 * }
 * }
 *
 * // 方式2：继承BaseVO（推荐）
 * {@code
 * public class UserVO extends BaseVO {
 *     private String username;
 *     // BaseVO已经实现了IVO接口
 * }
 * }
 * </pre>
 * <p>
 * <b>注意事项：</b>
 * </p>
 * <ul>
 *   <li>建议通过继承 {@link BaseVO} 来实现此接口，而不是直接实现</li>
 *   <li>如果直接实现，需要手动添加serialVersionUID字段</li>
 *   <li>VO中的字段应该是可序列化的（String、Number、Date等）</li>
 *   <li>避免在VO中包含不可序列化的对象（如Connection、Stream等）</li>
 * </ul>
 * </p>
 * <p>
 * <b>与其他接口的关系：</b>
 * </p>
 * <pre>
 * Serializable (Java标准接口)
 *   └── IVO (本接口)
 *         └── BaseVO (抽象实现类)
 *               ├── BaseAuditVO
 *               └── BaseSystemVO
 * </pre>
 *
 * @see Serializable
 * @see BaseVO
 * @see BaseAuditVO
 * @see BaseSystemVO
 * @author AdminPro
 * @version 1.0.0
 */
public interface IVO extends Serializable {
}
