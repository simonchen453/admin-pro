package com.adminpro.core.jdbc.query;

/**
 * 用来转换ResultSet查询到的对象的转换类
 */
public interface IModelConverter<S, T> {
    T convert(S s);

    S inverse(T s);
}
