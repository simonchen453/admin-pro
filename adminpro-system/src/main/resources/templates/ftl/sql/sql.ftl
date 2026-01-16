-- 菜单 SQL
insert into sys_menu_tbl (col_id, col_name, col_display, col_parent_id, col_order_num, col_url, col_is_frame, col_type, col_visible, col_status, col_permission, col_icon, col_remark, col_created_by, col_created_at, col_updated_by, col_updated_at)
values('${id_key_m}', 'M_${CLASSNAME}', '${tableComment}管理', '0', '1', '/${moduleName}/${classname}', 0, 'C', 'show', 'active', 'system:${classname}', '', '', '', '${date}', '', '${date}');
INSERT INTO `sys_role_menu_assign_tbl` (col_id, col_role_id, col_menu_id, col_created_by, col_created_at, col_updated_by, col_updated_at)
VALUES ('${id_key_r}', (select col_id from sys_role_tbl where col_name = 'SYS_ADMIN_ROLE'), '${id_key_m}', '', '${date}', '', '${date}');
