package com.adminpro.system.rbac.domains.entity.post;

import com.adminpro.framework.base.entity.BaseService;
import com.adminpro.framework.base.util.IdGenerator;
import com.adminpro.framework.base.util.SpringUtil;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.system.rbac.common.RbacCacheConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 职位服务类
 * <p>
 * 提供职位管理的核心业务功能，包括：
 * <ul>
 * <li>职位基本操作：创建、更新、删除、查询</li>
 * <li>职位查询：支持多种条件查询（编号、名称等）</li>
 * <li>批量操作：批量删除职位</li>
 * <li>缓存管理：使用Spring Cache缓存职位数据</li>
 * </ul>
 * </p>
 * <p>
 * 职位用于标识用户的岗位信息，常用于组织架构管理和权限控制
 * </p>
 *
 * @author simon
 * @date 2020-05-21
 * @version 1.0
 * @see PostEntity
 * @see PostDao
 */
@Service
public class PostService extends BaseService<PostEntity, String> {

    private PostDao dao;

    @Autowired
    public PostService(PostDao dao) {
        super(dao);
        this.dao = dao;
    }

    /**
     * 获取PostService实例
     * <p>
     * 通过Spring容器获取Service实例，用于在非Spring管理的类中调用服务
     * </p>
     *
     * @return PostService实例
     */
    public static PostService getInstance() {
        return SpringUtil.getBean(PostService.class);
    }

    /**
     * 搜索职位（分页）
     * <p>
     * 根据搜索参数进行分页查询，支持多种条件过滤
     * </p>
     *
     * @param param 搜索参数对象，包含分页信息和过滤条件
     * @return 分页查询结果集
     */
    public QueryResultSet<PostEntity> search(SearchParam param) {
        return dao.search(param);
    }

    /**
     * 根据参数查询职位列表
     * <p>
     * 根据搜索参数查询符合条件的职位列表，不分页
     * </p>
     *
     * @param param 搜索参数对象，包含过滤条件
     * @return 职位实体列表
     */
    public List<PostEntity> findByParam(SearchParam param) {
        return dao.findByParam(param);
    }

    /**
     * 创建职位
     * <p>
     * 创建新职位，自动生成职位ID
     * </p>
     *
     * @param entity 职位实体对象
     */
    @Override
    @Transactional
    public void create(PostEntity entity) {
        entity.setId(IdGenerator.getInstance().nextStringId());
        super.create(entity);
    }

    /**
     * 批量删除职位
     * <p>
     * 根据职位ID字符串批量删除职位，使用批量SQL提升性能
     * </p>
     *
     * @param ids 职位ID字符串，格式：id1,id2,id3
     */
    @Transactional
    public void deleteByIds(String ids) {
        if (StringUtils.isEmpty(ids)) {
            return;
        }
        String[] idArray = ids.split(",");
        if (idArray.length > 0) {
            dao.deleteByIds(Arrays.asList(idArray));
        }
    }

    /**
     * 根据职位编号查询职位
     * <p>
     * 支持缓存，使用职位编号作为缓存键
     * </p>
     *
     * @param code 职位编号
     * @return 职位实体对象，不存在返回null
     */
    @Cacheable(value = RbacCacheConstants.POST_CACHE, key = "'code_'+#code")
    public PostEntity findByCode(String code) {
        return dao.findByCode(code);
    }

    /**
     * 根据职位名称查询职位
     *
     * @param name 职位名称
     * @return 职位实体对象，不存在返回null
     */
    public PostEntity findByName(String name) {
        return dao.findByName(name);
    }

    /**
     * 根据职位代码列表批量查询
     *
     * @param codes 职位代码列表
     * @return 职位列表
     */
    public List<PostEntity> findByCodes(List<String> codes) {
        return dao.findByCodes(codes);
    }

    /**
     * 根据ID列表批量查询职位
     *
     * @param ids 职位ID列表
     * @return 职位列表
     */
    public List<PostEntity> findByIds(List<String> ids) {
        return dao.findByIds(ids);
    }
}
