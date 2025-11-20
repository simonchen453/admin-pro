-- 菜单 SQL
insert into sys_menu_tbl (col_id, col_name, col_parent_id, col_order_num, col_url, col_display, col_type, col_visible, col_permission, col_icon, col_status, col_remark, col_created_by_user_domain, col_created_by_user_id, col_created_date, col_updated_by_user_domain, col_updated_by_user_id, col_updated_date)
values('${id_key_m}', 'M_${CLASSNAME}', '0', '1', '/${moduleName}/${classname}', '${tableComment}管理', 'C', 'show', 'system:${classname}', '', 'active', '', '', '', '${date}', '', '', '${date}');
INSERT INTO `sys_role_menu_assign_tbl` VALUES ('${id_key_r}', 'SYS_ADMIN_ROLE', 'M_${CLASSNAME}', '', '', '${date}', '', '', '${date}');

