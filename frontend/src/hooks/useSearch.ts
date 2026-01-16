import { useState, useCallback, useMemo } from 'react';
import { Form } from 'antd';
import type { FormInstance } from 'antd';

/**
 * 搜索配置接口
 */
export interface SearchConfig<T> {
    /** 初始搜索值 */
    initialValues?: Partial<T>;
    /** 搜索时的回调 */
    onSearch?: (values: T) => void;
    /** 重置时的回调 */
    onReset?: () => void;
}

/**
 * 搜索 Hook 返回值接口
 */
export interface UseSearchReturn<T> {
    /** Form 实例 */
    form: FormInstance<T>;
    /** 当前搜索值 */
    searchValues: Partial<T>;
    /** 是否正在搜索 */
    searching: boolean;
    /** 设置搜索状态 */
    setSearching: (searching: boolean) => void;
    /** 执行搜索 */
    handleSearch: () => void;
    /** 重置搜索 */
    handleReset: () => void;
    /** 获取当前表单值 */
    getSearchValues: () => T;
}

/**
 * 通用搜索表单 Hook
 * 
 * @param config 搜索配置
 * @returns 搜索状态和操作方法
 * 
 * @example
 * ```tsx
 * interface UserSearchForm {
 *   loginName?: string;
 *   status?: string;
 * }
 * 
 * const { form, handleSearch, handleReset, searching } = useSearch<UserSearchForm>({
 *   onSearch: (values) => fetchUsers(values),
 *   onReset: () => fetchUsers({})
 * });
 * 
 * <Form form={form} onFinish={handleSearch}>
 *   <Form.Item name="loginName">
 *     <Input placeholder="登录名" />
 *   </Form.Item>
 *   <Button onClick={handleSearch} loading={searching}>搜索</Button>
 *   <Button onClick={handleReset}>重置</Button>
 * </Form>
 * ```
 */
export function useSearch<T extends object>(config?: SearchConfig<T>): UseSearchReturn<T> {
    const { initialValues, onSearch, onReset } = config || {};

    const [form] = Form.useForm<T>();
    const [searchValues, setSearchValues] = useState<Partial<T>>(initialValues || {});
    const [searching, setSearching] = useState(false);

    const handleSearch = useCallback(() => {
        const values = form.getFieldsValue();
        setSearchValues(values);
        onSearch?.(values);
    }, [form, onSearch]);

    const handleReset = useCallback(() => {
        form.resetFields();
        setSearchValues(initialValues || {});
        onReset?.();
    }, [form, initialValues, onReset]);

    const getSearchValues = useCallback(() => {
        return form.getFieldsValue();
    }, [form]);

    return {
        form,
        searchValues,
        searching,
        setSearching,
        handleSearch,
        handleReset,
        getSearchValues,
    };
}

export default useSearch;
