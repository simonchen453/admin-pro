package com.adminpro.system.core.cache;

import com.adminpro.framework.base.web.BaseSearchForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SearchForm 缓存工具类
 * <p>
 * 在 JWT 无状态认证下，替代 Session 存储用户的搜索表单数据。
 * 使用会话级缓存，每个登录会话独立维护搜索条件。
 * </p>
 * <p>
 * 特性：
 * <ul>
 * <li>模块隔离：不同模块的搜索条件互不影响</li>
 * <li>会话独立：每个登录会话独立维护搜索条件</li>
 * <li>自动迁移：Token 刷新时自动迁移数据</li>
 * <li>自动清理：用户登出时自动清理</li>
 * </ul>
 * </p>
 *
 * <h3>使用示例</h3>
 * 
 * <pre>{@code
 * // 存储搜索条件（按模块隔离）
 * SearchFormCache.set("user", userSearchForm);
 * SearchFormCache.set("order", orderSearchForm);
 *
 * // 获取搜索条件
 * UserSearchForm form = SearchFormCache.get("user", UserSearchForm.class);
 *
 * // 清除指定模块的搜索条件
 * SearchFormCache.clear("user");
 * }</pre>
 *
 * @author simon
 * @since 1.0.0
 * @see CurrentUserCache
 */
public class SearchFormCache {

    private static final Logger log = LoggerFactory.getLogger(SearchFormCache.class);

    /**
     * 搜索表单缓存 key 前缀
     */
    private static final String SEARCH_FORM_PREFIX = "search_form:";

    /**
     * 默认缓存过期时间（30分钟）
     */
    private static final int DEFAULT_EXPIRE_SECONDS = 1800;

    /**
     * 设置搜索表单（按模块隔离）
     * <p>
     * 不同模块使用不同的 moduleKey，互不影响。
     * 缓存 30 分钟后自动过期。
     * </p>
     *
     * @param moduleKey  模块标识（如 "user"、"order"、"product"）
     * @param searchForm 搜索表单对象
     */
    public static void set(String moduleKey, BaseSearchForm searchForm) {
        if (searchForm == null) {
            log.warn("尝试存储空的 SearchForm, module={}", moduleKey);
            return;
        }
        String suffix = SEARCH_FORM_PREFIX + moduleKey;
        CurrentUserCache.set(suffix, searchForm, DEFAULT_EXPIRE_SECONDS);
        log.debug("存储 SearchForm: module={}", moduleKey);
    }

    /**
     * 获取搜索表单
     *
     * @param moduleKey 模块标识
     * @param clazz     SearchForm 的具体类型
     * @param <T>       返回类型
     * @return 搜索表单对象，不存在则返回 null
     */
    public static <T extends BaseSearchForm> T get(String moduleKey, Class<T> clazz) {
        String suffix = SEARCH_FORM_PREFIX + moduleKey;
        T searchForm = CurrentUserCache.get(suffix, clazz);
        log.debug("获取 SearchForm: module={}, found={}", moduleKey, searchForm != null);
        return searchForm;
    }

    /**
     * 清除指定模块的搜索表单
     *
     * @param moduleKey 模块标识
     */
    public static void clear(String moduleKey) {
        String suffix = SEARCH_FORM_PREFIX + moduleKey;
        CurrentUserCache.delete(suffix);
        log.debug("清除 SearchForm: module={}", moduleKey);
    }

    /**
     * 检查指定模块的搜索表单是否存在
     *
     * @param moduleKey 模块标识
     * @return true 表示存在
     */
    public static boolean exists(String moduleKey) {
        return get(moduleKey, BaseSearchForm.class) != null;
    }
}
