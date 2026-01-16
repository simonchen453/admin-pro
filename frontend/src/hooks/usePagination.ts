import { useState, useCallback, useMemo } from 'react';

/**
 * 分页配置接口
 */
export interface PaginationConfig {
    /** 初始页码，默认 1 */
    initialPage?: number;
    /** 初始每页数量，默认 10 */
    initialPageSize?: number;
    /** 可选的每页数量选项 */
    pageSizeOptions?: number[];
}

/**
 * 分页状态接口
 */
export interface PaginationState {
    /** 当前页码 */
    currentPage: number;
    /** 每页数量 */
    pageSize: number;
    /** 总记录数 */
    total: number;
}

/**
 * 分页返回值接口
 */
export interface UsePaginationReturn {
    /** 当前分页状态 */
    pagination: PaginationState;
    /** 设置当前页 */
    setPage: (page: number) => void;
    /** 设置每页数量 */
    setPageSize: (size: number) => void;
    /** 设置总数 */
    setTotal: (total: number) => void;
    /** 重置到第一页 */
    resetPage: () => void;
    /** 分页变化处理函数（用于 Ant Design Table） */
    handleTableChange: (page: number, pageSize: number) => void;
    /** Ant Design Table 分页配置 */
    tableProps: {
        current: number;
        pageSize: number;
        total: number;
        showSizeChanger: boolean;
        showQuickJumper: boolean;
        showTotal: (total: number, range: [number, number]) => string;
        pageSizeOptions: string[];
    };
}

/**
 * 通用分页 Hook
 * 
 * @param config 分页配置
 * @returns 分页状态和操作方法
 * 
 * @example
 * ```tsx
 * const { pagination, handleTableChange, tableProps } = usePagination({
 *   initialPageSize: 20
 * });
 * 
 * // 在 Table 中使用
 * <Table 
 *   dataSource={data}
 *   pagination={tableProps}
 *   onChange={(pagination) => handleTableChange(pagination.current!, pagination.pageSize!)}
 * />
 * ```
 */
export function usePagination(config?: PaginationConfig): UsePaginationReturn {
    const {
        initialPage = 1,
        initialPageSize = 10,
        pageSizeOptions = [10, 20, 50, 100],
    } = config || {};

    const [currentPage, setCurrentPage] = useState(initialPage);
    const [pageSize, setPageSizeState] = useState(initialPageSize);
    const [total, setTotal] = useState(0);

    const setPage = useCallback((page: number) => {
        setCurrentPage(Math.max(1, page));
    }, []);

    const setPageSize = useCallback((size: number) => {
        setPageSizeState(size);
        setCurrentPage(1); // 改变每页数量时重置到第一页
    }, []);

    const resetPage = useCallback(() => {
        setCurrentPage(initialPage);
    }, [initialPage]);

    const handleTableChange = useCallback((page: number, size: number) => {
        if (size !== pageSize) {
            setPageSize(size);
        } else {
            setPage(page);
        }
    }, [pageSize, setPage, setPageSize]);

    const tableProps = useMemo(() => ({
        current: currentPage,
        pageSize,
        total,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total: number, range: [number, number]) =>
            `第 ${range[0]}-${range[1]} 条，共 ${total} 条`,
        pageSizeOptions: pageSizeOptions.map(String),
    }), [currentPage, pageSize, total, pageSizeOptions]);

    const pagination = useMemo(() => ({
        currentPage,
        pageSize,
        total,
    }), [currentPage, pageSize, total]);

    return {
        pagination,
        setPage,
        setPageSize,
        setTotal,
        resetPage,
        handleTableChange,
        tableProps,
    };
}

export default usePagination;
