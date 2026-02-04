-- liquibase formatted sql
-- changeset simon:1000001
CREATE TABLE `sys_user_domain_env_tbl`  (
  `col_id` varchar(64) NOT NULL COMMENT 'ID',
  `col_common_role` varchar(255) DEFAULT NULL COMMENT '用户域基础角色',
  `col_description` varchar(255) DEFAULT NULL COMMENT '用户域环境配置描述',
  `col_error_page_url` varchar(255) DEFAULT NULL COMMENT '用户域公共错误页面url',
  `col_fatal_error_page_url` varchar(255) DEFAULT NULL COMMENT '用户域严重错误页面url',
  `col_home_page_url` varchar(255) DEFAULT NULL COMMENT '用户域首页页面url',
  `col_layout` varchar(255) DEFAULT NULL COMMENT '用户域布局名称',
  `col_session_expired_url` varchar(255) DEFAULT NULL COMMENT '用户域session过期页面url',
  `col_user_domain` varchar(64) DEFAULT NULL COMMENT '用户域',
  PRIMARY KEY (`col_id`),
  UNIQUE INDEX `unq_user_domain`(`col_user_domain`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '用户域环境配置';

CREATE TABLE `sys_user_domain_tbl`  (
  `col_id` varchar(64) NOT NULL COMMENT 'ID',
  `col_name` varchar(128) DEFAULT NULL COMMENT '用户域名称',
  `col_display` varchar(255) DEFAULT NULL COMMENT '用户域显示名称',
  `col_is_system` bit(1) NULL DEFAULT NULL COMMENT '是否系统配置',

  `col_created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `col_created_at` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `col_updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `col_updated_at` datetime(0) NULL DEFAULT NULL COMMENT '更新日期',
  PRIMARY KEY (`col_id`),
  UNIQUE INDEX `unq_name`(`col_name`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '用户域';

CREATE TABLE `sys_user_tbl`  (
    `col_id` varchar(64) NOT NULL COMMENT 'ID',
    `col_user_domain` varchar(64) NOT NULL COMMENT '用户域',
    `col_login_name` varchar(255) DEFAULT NULL COMMENT '用户登录名称',
    `col_display` varchar(255) DEFAULT NULL COMMENT '用户显示昵称',
    `col_real_name` varchar(255) DEFAULT NULL COMMENT '用户真实名称',
    `col_id_no` varchar(255) DEFAULT NULL COMMENT '用户身份证号',
    `col_email` varchar(255) DEFAULT NULL COMMENT '用户邮箱',
    `col_status` varchar(64) DEFAULT NULL COMMENT '用户状态',
    `col_authenticated` bit(1) NULL DEFAULT NULL COMMENT '是否实名认证',
    `col_mobile_no` varchar(255) DEFAULT NULL COMMENT '手机号码',
    `col_is_system` bit(1) DEFAULT NULL COMMENT '是否系统配置',
    `col_address` varchar(255) DEFAULT NULL COMMENT '地址',
    `col_pwd` varchar(255) DEFAULT NULL COMMENT '密码',
    `col_birthday` datetime(0) DEFAULT NULL COMMENT '生日',
    `col_sex` varchar(12) DEFAULT NULL COMMENT '性别',
    `col_marital` varchar(12) DEFAULT NULL COMMENT '婚姻状态',
    `col_nation` varchar(255) DEFAULT NULL COMMENT '民族',
    `col_pay_no_pwd` int(11) DEFAULT NULL COMMENT '是否免密支付',
    `col_pay_pwd` varchar(255) DEFAULT NULL COMMENT '支付密码',
    `col_pay_no_pwd_point` int(11) NULL DEFAULT NULL COMMENT '免密支付阀值',
    `col_avatar_url` varchar(255) DEFAULT NULL COMMENT '头像地址',
    `col_post` varchar(255) DEFAULT NULL COMMENT '职位',
    `col_profession` varchar(255) DEFAULT NULL COMMENT '职业',
    `col_constellation` varchar(255) DEFAULT NULL COMMENT '星座',
    `col_third_party_user_name` varchar(255) DEFAULT NULL COMMENT '第三方账号',
    `col_third_party_pwd` varchar(255) DEFAULT NULL COMMENT '第三方密码',
    `col_job_no` varchar(255) DEFAULT NULL COMMENT '工号',
    `col_ext_user_id` varchar(255) DEFAULT NULL COMMENT '外部用户ID',
    `col_authenticate_date` datetime(0) DEFAULT NULL COMMENT '实名认证时间',
    `col_latest_login_time` datetime(0) DEFAULT NULL COMMENT '最后登录时间',
    `col_latest_change_pwd_time` datetime(0) NULL DEFAULT NULL COMMENT '最后修改支付密码时间',
    `col_description` varchar(255) DEFAULT NULL COMMENT '用户描述',
    `col_dept_no` varchar(255) DEFAULT NULL COMMENT '部门号',
    `col_share_code` varchar(255) DEFAULT NULL COMMENT '分享码',
    `col_parent_share_code` varchar(255) DEFAULT NULL COMMENT '上级分享码',
    `col_integral` bigint(20) DEFAULT 0 COMMENT '用户积分',
    `col_province` varchar(255) DEFAULT NULL COMMENT '省',
    `col_city` varchar(255) DEFAULT NULL COMMENT '市',
    `col_district` varchar(255) DEFAULT NULL COMMENT '区',

    `col_created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
    `col_created_at` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
    `col_updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
    `col_updated_at` datetime(0) NULL DEFAULT NULL COMMENT '更新日期',
    PRIMARY KEY (`col_id`),
    UNIQUE INDEX `unq_user_domain_login_name`(`col_user_domain`, `col_login_name`),
    UNIQUE INDEX `unq_ext_userid`(`col_ext_user_id`),
    UNIQUE INDEX `uk_share_code`(`col_share_code`),
    UNIQUE INDEX `unq_mobileno`(`col_user_domain`, `col_mobile_no`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '用户';

CREATE TABLE `sys_user_profile_tbl`  (
  `col_id` varchar(64) NOT NULL COMMENT 'ID',
  `col_user_id` varchar(64) NOT NULL COMMENT '用户ID',
  `col_show_age` int(1) NULL DEFAULT 0 COMMENT '是否显示年龄',
  `col_show_profession` int(1) NULL DEFAULT 0 COMMENT '是否显示职业',
  `col_show_footprint` int(1) NULL DEFAULT 0 COMMENT '是否显示足记',
  PRIMARY KEY (`col_id`),
  CONSTRAINT `fk_user_profile_user` FOREIGN KEY (`col_user_id`) REFERENCES `sys_user_tbl`(`col_id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '用户简况';

CREATE TABLE `sys_user_tag_tbl`  (
  `col_id` varchar(64) NOT NULL COMMENT 'ID',
  `col_user_id` varchar(64) NOT NULL COMMENT '用户ID',
  `col_tag` varchar(255) DEFAULT NULL COMMENT '标签内容',
  PRIMARY KEY (`col_id`),
  UNIQUE INDEX `user_tag_unique`(`col_user_id`, `col_tag`),
  CONSTRAINT `fk_user_tag_user` FOREIGN KEY (`col_user_id`) REFERENCES `sys_user_tbl`(`col_id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '用户标签';

CREATE TABLE `sys_user_token_tbl`  (
  `col_id` varchar(64) NOT NULL COMMENT 'ID',
  `col_token` varchar(255) DEFAULT NULL COMMENT 'Token值',
  `col_user_id` varchar(64) DEFAULT NULL COMMENT '用户ID',
  `col_device` varchar(255) DEFAULT NULL COMMENT '登录设备',
  `col_expire_time` datetime(0) NULL DEFAULT NULL COMMENT '过期时间',
  `col_status` varchar(64) DEFAULT NULL COMMENT '状态',
  `col_update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`col_id`),
  CONSTRAINT `fk_user_token_user` FOREIGN KEY (`col_user_id`) REFERENCES `sys_user_tbl`(`col_id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '用户Token';

CREATE TABLE `sys_role_menu_assign_tbl`  (
  `col_id` varchar(64) NOT NULL COMMENT 'ID',
  `col_role_id` varchar(64) NOT NULL COMMENT '角色ID',
  `col_menu_id` varchar(64) NOT NULL COMMENT '菜单ID',

  `col_created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `col_created_at` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `col_updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `col_updated_at` datetime(0) NULL DEFAULT NULL COMMENT '更新日期',
  PRIMARY KEY (`col_id`),
  UNIQUE INDEX `unique_idx`(`col_role_id`, `col_menu_id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '角色和菜单关联表';

CREATE TABLE `sys_role_tbl`  (
  `col_id` varchar(64) NOT NULL COMMENT '角色ID',
  `col_name` varchar(128) DEFAULT NULL COMMENT '角色名称',
  `col_display` varchar(255) DEFAULT NULL COMMENT '角色显示名称',
  `col_status` varchar(64) DEFAULT NULL COMMENT '状态',
  `col_is_system` bit(1) NULL DEFAULT NULL COMMENT '是否系统配置',

  `col_created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `col_created_at` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `col_updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `col_updated_at` datetime(0) NULL DEFAULT NULL COMMENT '更新日期',
  PRIMARY KEY (`col_id`),
  UNIQUE INDEX `unq_name`(`col_name`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '角色';

CREATE TABLE `sys_menu_tbl`  (
  `col_id` varchar(64) NOT NULL COMMENT 'ID',
  `col_name` varchar(128) DEFAULT NULL COMMENT '菜单名称',
  `col_display` varchar(255) DEFAULT NULL COMMENT '显示名称',
  `col_parent_id` varchar(64) DEFAULT NULL COMMENT '显示名称',
  `col_order_num` int(4) DEFAULT 0 COMMENT '显示顺序',
  `col_url` varchar(255) DEFAULT NULL COMMENT '链接url',
  `col_is_frame` int(1) NULL DEFAULT NULL COMMENT '是否为外链（0是 1否）',
  `col_type` varchar(64) NULL DEFAULT NULL COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `col_visible` varchar(64) NULL DEFAULT 0 COMMENT '菜单状态（show显示 hidden隐藏）',
  `col_status` varchar(64) NULL DEFAULT NULL COMMENT '菜单状态',
  `col_permission` varchar(100) NULL DEFAULT NULL COMMENT '权限标识',
  `col_icon` varchar(100) DEFAULT NULL COMMENT '图标',
  `col_remark` varchar(500) DEFAULT NULL COMMENT '备注',

  `col_created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `col_created_at` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `col_updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `col_updated_at` datetime(0) NULL DEFAULT NULL COMMENT '更新日期',
  PRIMARY KEY (`col_id`),
  UNIQUE INDEX `name_unique`(`col_name`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '菜单权限表';

CREATE TABLE `sys_user_menu_assign_tbl`  (
  `col_id` varchar(64) NOT NULL COMMENT 'ID',
  `col_user_id` varchar(64) DEFAULT NULL COMMENT '用户ID',
  `col_menu_id` varchar(64) DEFAULT NULL COMMENT '菜单ID',
  `col_remarks` varchar(255) DEFAULT NULL COMMENT '备注',

  `col_created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `col_created_at` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `col_updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `col_updated_at` datetime(0) NULL DEFAULT NULL COMMENT '更新日期',
  PRIMARY KEY (`col_id`),
  UNIQUE INDEX `unq_user_menu`(`col_user_id`, `col_menu_id`),
  CONSTRAINT `fk_user_menu_user` FOREIGN KEY (`col_user_id`) REFERENCES `sys_user_tbl`(`col_id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '用户菜单分配表';

CREATE TABLE `sys_user_role_assign_tbl`  (
  `col_id` varchar(64) NOT NULL COMMENT 'ID',
  `col_user_id` varchar(64) DEFAULT NULL COMMENT '用户ID',
  `col_role_id` varchar(64) DEFAULT NULL COMMENT '角色ID',
  `col_remarks` varchar(255) DEFAULT NULL COMMENT '备注',

  `col_created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `col_created_at` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `col_updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `col_updated_at` datetime(0) NULL DEFAULT NULL COMMENT '更新日期',
  PRIMARY KEY (`col_id`),
  UNIQUE INDEX `unq_user_role`(`col_user_id`, `col_role_id`),
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`col_user_id`) REFERENCES `sys_user_tbl`(`col_id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '用户角色分配';

CREATE TABLE `sys_user_post_assign_tbl`  (
  `col_id` varchar(64) NOT NULL COMMENT 'ID',
  `col_user_id` varchar(64) DEFAULT NULL COMMENT '用户ID',
  `col_post_id` varchar(64) DEFAULT NULL COMMENT '职位ID',
  `col_remarks` varchar(255) DEFAULT NULL COMMENT '备注',

  `col_created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `col_created_at` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `col_updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `col_updated_at` datetime(0) NULL DEFAULT NULL COMMENT '更新日期',
  PRIMARY KEY (`col_id`),
  UNIQUE INDEX `unq_user_post`(`col_user_id`, `col_post_id`),
  CONSTRAINT `fk_user_post_user` FOREIGN KEY (`col_user_id`) REFERENCES `sys_user_tbl`(`col_id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '用户角色分配';

CREATE TABLE `sys_dept_tbl`  (
  `col_id` varchar(64) NOT NULL COMMENT 'ID',
  `col_no` varchar(128) DEFAULT NULL COMMENT '部门编号',
  `col_parent_id` varchar(64) NOT NULL COMMENT 'ID',
  `col_ancestors` varchar(64) NOT NULL COMMENT '祖级列表',
  `col_name` varchar(128) DEFAULT NULL COMMENT '部门名称',
  `col_order_num` int(4) DEFAULT NULL COMMENT '显示顺序',
  `col_del_flag` int(1) DEFAULT NULL COMMENT '删除标志（0代表存在 2代表删除）',
  `col_description` varchar(255) DEFAULT NULL COMMENT '部门描述',
  `col_linkman` varchar(128) DEFAULT NULL COMMENT '联系人',
  `col_contact` varchar(128) DEFAULT NULL COMMENT '联系方式',
  `col_phone` varchar(128) DEFAULT NULL COMMENT '联系电话',
  `col_email` varchar(128) DEFAULT NULL COMMENT '邮箱',
  `col_status` varchar(64) DEFAULT NULL COMMENT '状态',

  `col_created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `col_created_at` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `col_updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `col_updated_at` datetime(0) NULL DEFAULT NULL COMMENT '更新日期',
  PRIMARY KEY (`col_id`),
  UNIQUE INDEX `unq_no`(`col_no`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '部门表';

create table sys_post_tbl (
  `col_id` varchar(64) NOT NULL COMMENT 'ID',
  `col_code` varchar(64) not null COMMENT '职位编码',
  `col_name` varchar(50) not null COMMENT '职位名称',
  `col_sort` int(4) not null COMMENT '显示顺序',
  `col_status` varchar(20) not null COMMENT '状态',
  `col_remark` varchar(500) DEFAULT NULL COMMENT '备注',

  `col_created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `col_created_at` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `col_updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `col_updated_at` datetime(0) NULL DEFAULT NULL COMMENT '更新日期',
  primary key (col_id),
  UNIQUE INDEX `unq_code`(`col_code`),
  UNIQUE INDEX `unq_name`(`col_name`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '职位信息表';

CREATE TABLE `sys_city_tbl`  (
  `col_id` varchar(20) NOT NULL COMMENT '区域唯一标识',
  `col_title` varchar(255) DEFAULT NULL COMMENT '区域名称',
  `col_parent` varchar(38) DEFAULT NULL COMMENT '上级区域ID',
  `col_level` int(11) NULL DEFAULT NULL COMMENT '类型 1:省级、2:市级、3:区县级、4:街道',
  `col_keyword` varchar(38) DEFAULT NULL COMMENT '查询关键字',
  PRIMARY KEY (`col_id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '城市区划';

CREATE TABLE `sys_code_message_tbl`  (
  `col_code` varchar(255) NOT NULL COMMENT '消息code',
  `col_dev_msg` varchar(255) DEFAULT NULL COMMENT '开发消息',
  `col_user_msg` varchar(255) DEFAULT NULL COMMENT '用户消息',
  PRIMARY KEY (`col_code`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '错误消息';

CREATE TABLE `sys_commons_sequence`  (
  `col_seq_name` varchar(128) NOT NULL COMMENT '序列名称',
  `col_seq_next_value` bigint(20) NOT NULL COMMENT '序列下一个值',
  PRIMARY KEY (`col_seq_name`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '公共序列';

CREATE TABLE `sys_exception_log`  (
  `col_id` varchar(64) NOT NULL COMMENT 'ID',
  `col_type` text COMMENT '异常类型',
  `col_path` varchar(255) DEFAULT NULL COMMENT '访问路径',
  `col_details` mediumtext COMMENT '异常详情',

  `col_created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `col_created_at` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `col_updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `col_updated_at` datetime(0) NULL DEFAULT NULL COMMENT '更新日期',
  PRIMARY KEY (`col_id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '异常信息';

CREATE TABLE `sys_notification_tbl`  (
  `col_id` varchar(64) NOT NULL COMMENT 'ID',
  `col_title` varchar(255) DEFAULT NULL COMMENT '标题',
  `col_content` mediumtext COMMENT '内容',
  `col_user_domain` varchar(64) DEFAULT NULL COMMENT '用户域',
  `col_start_time` datetime(0) NULL DEFAULT NULL COMMENT '开始时间',
  `col_end_time` datetime(0) NULL DEFAULT NULL COMMENT '结束时间',

  `col_created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `col_created_at` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `col_updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `col_updated_at` datetime(0) NULL DEFAULT NULL COMMENT '更新日期',
  PRIMARY KEY (`col_id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '通知';

CREATE TABLE `sys_oss_tbl`  (
  `col_id` varchar(64) NOT NULL COMMENT 'ID',
  `col_url` varchar(200) DEFAULT NULL COMMENT 'URL地址',
  `col_key` varchar(200) DEFAULT NULL COMMENT 'KEY',
  `col_hash` varchar(200) DEFAULT NULL COMMENT 'HASH',
  `col_suffix` varchar(200) DEFAULT NULL COMMENT '后缀',
  `col_original` varchar(200) DEFAULT NULL COMMENT '文件名字',
  `col_size` bigint(20) NULL DEFAULT NULL COMMENT '文件大小',
  `col_batch_id` varchar(255) DEFAULT NULL,
  `col_type` varchar(255) DEFAULT NULL,
  `col_cover` varchar(255) DEFAULT NULL COMMENT '封面地址',
  `col_cover_key` varchar(255) DEFAULT NULL COMMENT '封面KEY',
  `col_status` varchar(64) DEFAULT NULL COMMENT '状态',

  `col_created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `col_created_at` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `col_updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `col_updated_at` datetime(0) NULL DEFAULT NULL COMMENT '更新日期',
  PRIMARY KEY (`col_id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '文件上传';

CREATE TABLE `sys_schedule_job_tbl`  (
  `col_id` varchar(64) NOT NULL COMMENT '任务id',
  `col_bean_name` varchar(200) DEFAULT NULL COMMENT 'spring bean名称',
  `col_method_name` varchar(100) DEFAULT NULL COMMENT '方法名',
  `col_params` varchar(2000) DEFAULT NULL COMMENT '参数',
  `col_cron_expression` varchar(100) DEFAULT NULL COMMENT 'cron表达式',
  `col_status` tinyint(4) NULL DEFAULT NULL COMMENT '任务状态  0：正常  1：暂停',
  `col_remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `col_created_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`col_id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '定时任务';

CREATE TABLE `sys_schedule_job_log_tbl`  (
  `col_id` varchar(64) NOT NULL COMMENT '任务日志id',
  `col_job_id` varchar(255) NOT NULL COMMENT '任务id',
  `col_bean_name` varchar(200) DEFAULT NULL COMMENT 'spring bean名称',
  `col_method_name` varchar(100) DEFAULT NULL COMMENT '方法名',
  `col_params` varchar(2000) DEFAULT NULL COMMENT '参数',
  `col_status` tinyint(4) NOT NULL COMMENT '任务状态    0：成功    1：失败',
  `col_error` varchar(2000) DEFAULT NULL COMMENT '失败信息',
  `col_times` int(11) NOT NULL COMMENT '耗时(单位：毫秒)',
  `col_created_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`col_id`),
  INDEX `job_id`(`col_job_id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '定时任务日志';

CREATE TABLE `sys_session_tbl`  (
  `col_id` varchar(64) NOT NULL COMMENT 'ID',
  `col_session_id` varchar(128) DEFAULT NULL COMMENT 'Session ID',
  `col_third_party_session_id` varchar(128) DEFAULT NULL COMMENT '第三方Session ID',
  `col_status` varchar(64) DEFAULT NULL COMMENT '状态',
  `col_user_id` varchar(64) DEFAULT NULL COMMENT '用户ID',
  `col_ip_addr` varchar(128) DEFAULT NULL COMMENT '登陆IP',
  `col_login_location` varchar(128) DEFAULT NULL COMMENT '登陆地址',
  `col_browser` varchar(128) DEFAULT NULL COMMENT '浏览器',
  `col_os` varchar(128) DEFAULT NULL COMMENT '操作系统',
  `col_dept_no` varchar(128) DEFAULT NULL COMMENT '部门编号',

  `col_created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `col_created_at` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `col_updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `col_updated_at` datetime(0) NULL DEFAULT NULL COMMENT '更新日期',
  PRIMARY KEY (`col_id`),
  UNIQUE INDEX `unq_session_id`(`col_session_id`),
  CONSTRAINT `fk_session_user` FOREIGN KEY (`col_user_id`) REFERENCES `sys_user_tbl`(`col_id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '用户Session';

CREATE TABLE `sys_sys_log_tbl`  (
  `col_id` varchar(64) NOT NULL COMMENT 'ID',
  `col_user_id` varchar(64) DEFAULT NULL COMMENT '用户ID',
  `col_ip` varchar(255) DEFAULT NULL COMMENT '用户IP',
  `col_browser` varchar(255) DEFAULT NULL COMMENT '访问浏览器',
  `col_method` varchar(255) DEFAULT NULL COMMENT '访问Request Method',
  `col_os` varchar(255) DEFAULT NULL COMMENT '操作系统',
  `col_params` mediumtext COMMENT '访问参数',
  `col_response` longtext COMMENT 'Response',
  `col_time` bigint(20) NULL DEFAULT NULL COMMENT '消耗时间',

  `col_created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `col_created_at` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `col_updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `col_updated_at` datetime(0) NULL DEFAULT NULL COMMENT '更新日期',
  PRIMARY KEY (`col_id`),
  CONSTRAINT `fk_sys_log_user` FOREIGN KEY (`col_user_id`) REFERENCES `sys_user_tbl`(`col_id`) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '系统日志';

CREATE TABLE `sys_task_tbl`  (
  `col_id` varchar(64) NOT NULL COMMENT '任务ID',
  `col_intray_user_domain` varchar(64) DEFAULT NULL COMMENT '接受人用户域',
  `col_intray_user_id` varchar(64) DEFAULT NULL COMMENT '接受人用户ID',
  `col_ref_no` varchar(64) DEFAULT NULL COMMENT '业务关联号',
  `col_task_no` varchar(255) DEFAULT NULL COMMENT '任务号',
  `col_title` varchar(255) DEFAULT NULL COMMENT '标题',
  `col_type` varchar(255) DEFAULT NULL COMMENT '类型',
  `col_status` varchar(64) DEFAULT NULL COMMENT '状态',
  `col_close_comments` varchar(255) DEFAULT NULL COMMENT '任务关闭备注',
  `col_close_type` varchar(255) DEFAULT NULL COMMENT '任务关闭类型',
  `col_start_date` datetime(0) NULL DEFAULT NULL COMMENT '任务开始时间',
  `col_end_date` datetime(0) NULL DEFAULT NULL COMMENT '任务结束时间',

  `col_created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `col_created_at` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `col_updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `col_updated_at` datetime(0) NULL DEFAULT NULL COMMENT '更新日期',
  PRIMARY KEY (`col_id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '任务';

CREATE TABLE `sys_task_log_tbl`  (
  `col_id` varchar(64) NOT NULL COMMENT 'ID',
  `col_task_id` varchar(64) DEFAULT NULL COMMENT '任务ID',
  `col_title` varchar(255) DEFAULT NULL COMMENT '任务标题',

  `col_created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `col_created_at` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `col_updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `col_updated_at` datetime(0) NULL DEFAULT NULL COMMENT '更新日期',
  PRIMARY KEY (`col_id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '任务日志';

create table sys_config_tbl (
  `col_id` varchar(64) NOT NULL COMMENT '参数主键',
  `col_key` varchar(128) NOT NULL COMMENT '参数键名',
  `col_name` varchar(128) NOT NULL COMMENT '参数名称',
  `col_value` varchar(500) NOT NULL COMMENT '参数键值',
  `col_system` int(1) DEFAULT 0 COMMENT '是否系统内置',
  `col_remark` varchar(500) DEFAULT NULL COMMENT '备注',

  `col_created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `col_created_at` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `col_updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `col_updated_at` datetime(0) NULL DEFAULT NULL COMMENT '更新日期',
  primary key (col_id),
  unique (col_key)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '参数配置表';

create table sys_dict_tbl(
  `col_id` varchar(64) NOT NULL COMMENT '字典主键',
  `col_name` varchar(128) NOT NULL COMMENT '字典名称',
  `col_key` varchar(128) NOT NULL COMMENT '字典类型',
  `col_status` varchar(64) NOT NULL COMMENT '状态',
  `col_remark` varchar(64) NOT NULL COMMENT '备注',

  `col_created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `col_created_at` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `col_updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `col_updated_at` datetime(0) NULL DEFAULT NULL COMMENT '更新日期',
  primary key (col_id),
  unique (col_key)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '字典类型表';

create table sys_dict_data_tbl(
  `col_id` varchar(64) NOT NULL COMMENT 'ID',
  `col_key` varchar(64) NOT NULL COMMENT '字典类型',
  `col_sort` int(4) DEFAULT 0 COMMENT '排序',
  `col_label` varchar(128) DEFAULT NULL COMMENT '标签',
  `col_value` varchar(128) DEFAULT NULL COMMENT '键值',
  `col_css_class` varchar(128) DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `col_list_class` varchar(128) DEFAULT NULL COMMENT '表格回显样式',
  `col_default` int(1) DEFAULT NULL COMMENT '是否默认',
  `col_status` varchar(64) DEFAULT NULL COMMENT '状态',
  `col_remark` varchar(500) DEFAULT NULL COMMENT '备注',

  `col_created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `col_created_at` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `col_updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `col_updated_at` datetime(0) NULL DEFAULT NULL COMMENT '更新日期',
  primary key (col_id)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '字典数据表';

create table sys_login_info_tbl (
  `col_id` varchar(64) NOT NULL COMMENT 'ID',
  `col_key` varchar(64) NOT NULL COMMENT '字典类型',
  `col_user_id` varchar(64) NOT NULL COMMENT '用户ID',
  `col_ip_address` varchar(64) DEFAULT NULL COMMENT '登录IP地址',
  `col_login_location` varchar(255) DEFAULT NULL COMMENT '登录地点',
  `col_browser` varchar(64) DEFAULT NULL COMMENT '浏览器类型',
  `col_os` varchar(64) DEFAULT NULL COMMENT '浏览器类型',
  `col_status` varchar(64) DEFAULT NULL COMMENT '登录状态',
  `col_login_time` datetime DEFAULT NULL COMMENT '登录状态',
  `col_message` varchar(512) DEFAULT NULL COMMENT '提示消息',

  `col_created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `col_created_at` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `col_updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `col_updated_at` datetime(0) NULL DEFAULT NULL COMMENT '更新日期',
  primary key (col_id),
  CONSTRAINT `fk_login_info_user` FOREIGN KEY (`col_user_id`) REFERENCES `sys_user_tbl`(`col_id`) ON DELETE CASCADE
) engine=innodb auto_increment=100 comment = '系统访问记录';

-- changeset simon:1000002
INSERT INTO `sys_menu_tbl` VALUES ('1998425430192951296', 'M_HOME', '首页', '0', 0, '/home', 0, 'C', 'show', 'active', 'system:home', 'HomeOutlined', NULL, 'system', '2025-12-10 00:12:06', 'system', '2025-12-10 00:13:16');
INSERT INTO `sys_menu_tbl` VALUES ('1268893422756958208', 'C_SYS_MGR', '系统管理', '0', 2, NULL, 0, 'M', 'show', 'active', NULL, 'SettingOutlined', NULL, '', '2020-06-05 21:12:22', 'system', '2020-06-20 13:00:32');
INSERT INTO `sys_menu_tbl` VALUES ('1268894158748258304', 'M_USER', '用户管理', '1268893422756958208', 1, '/admin/user', 0, 'C', 'show', 'active', 'system:user', 'UserOutlined', NULL, '', '2020-06-05 21:15:17', 'system', '2020-06-19 23:32:11');
INSERT INTO `sys_menu_tbl` VALUES ('1268894323924144128', 'M_ROLE', '角色管理', '1268893422756958208', 2, '/admin/role', 0, 'C', 'show', 'active', 'system:role', 'TeamOutlined', NULL, '', '2020-06-05 21:15:56', 'system', '2020-06-19 23:46:49');
INSERT INTO `sys_menu_tbl` VALUES ('1268894555311312896', 'M_MENU', '菜单管理', '1268893422756958208', 3, '/admin/menu', 0, 'C', 'show', 'active', 'system:menu', 'BarsOutlined', NULL, '', '2020-06-05 21:16:52', 'system', '2020-06-19 23:35:17');
INSERT INTO `sys_menu_tbl` VALUES ('1268895433615347711', 'M_DOMAIN', '用户域', '1268893422756958208', 4, '/admin/domain', 0, 'C', 'show', 'active', 'system:domain', 'TagOutlined', NULL, '', '2020-06-05 21:20:21', 'system', '2020-06-19 23:53:35');
INSERT INTO `sys_menu_tbl` VALUES ('1268895433615347712', 'M_USER_DOMAIN_ENV', '用户域配置', '1268893422756958208', 5, '/admin/userDomainEnv', 0, 'C', 'show', 'active', 'system:user_domain_env', 'ToolOutlined', NULL, '', '2020-06-05 21:20:21', 'system', '2020-06-19 23:36:20');
INSERT INTO `sys_menu_tbl` VALUES ('1268895694639468544', 'M_DEPT', '部门管理', '1268893422756958208', 6, '/admin/dept', 0, 'C', 'show', 'active', 'system:dept', 'ApartmentOutlined', NULL, '', '2020-06-05 21:21:23', 'system', '2020-06-19 23:53:06');
INSERT INTO `sys_menu_tbl` VALUES ('1268895982188367872', 'M_POST', '岗位管理', '1268893422756958208', 7, '/admin/post', 0, 'C', 'show', 'active', 'system:post', 'IdcardOutlined', NULL, '', '2020-06-05 21:22:32', 'system', '2020-06-19 23:37:01');
INSERT INTO `sys_menu_tbl` VALUES ('1268896596561629184', 'M_CONFIG', '参数配置', '1268893422756958208', 9, '/admin/config', 0, 'C', 'show', 'active', 'system:config', 'SlidersOutlined', NULL, '', '2020-06-05 21:24:58', 'system', '2020-06-20 13:27:13');
INSERT INTO `sys_menu_tbl` VALUES ('1268897921470959616', 'C_SYS_MONITOR', '系统监控', '0', 3, NULL, 0, 'M', 'show', 'active', NULL, 'DesktopOutlined', NULL, '', '2020-06-05 21:30:14', 'system', '2020-06-20 13:25:02');

INSERT INTO `sys_menu_tbl` VALUES ('1268898457960189952', 'M_JOB', '定时任务', '1268897921470959616', 2, '/admin/job', 0, 'C', 'show', 'active', 'system:job', 'ClockCircleOutlined', NULL, '', '2020-06-05 21:32:22', 'system', '2020-06-19 23:39:25');
INSERT INTO `sys_menu_tbl` VALUES ('1268898639556775936', 'M_SERVER', '服务监控', '1268897921470959616', 3, '/admin/server', 0, 'C', 'show', 'active', 'system:server', 'DatabaseOutlined', NULL, '', '2020-06-05 21:33:05', 'system', '2020-06-19 23:38:40');
INSERT INTO `sys_menu_tbl` VALUES ('1268898849364250624', 'C_TOOLS', '系统工具', '0', 4, NULL, 0, 'M', 'show', 'active', NULL, 'ToolOutlined', NULL, '', '2020-06-05 21:33:55', 'system', '2020-06-20 13:23:45');

INSERT INTO `sys_menu_tbl` VALUES ('1268899376529543168', 'M_SWAGGER', '系统接口', '1268898849364250624', 2, '/admin/swagger', 0, 'C', 'show', 'active', 'system:swagger', 'AppstoreOutlined', NULL, '', '2020-06-05 21:36:01', 'system', '2020-06-20 00:03:06');
INSERT INTO `sys_menu_tbl` VALUES ('1274338663366529024', 'C_WORKSPACE', '工作空间', '0', 1, NULL, 0, 'M', 'hidden', 'active', NULL, 'AppstoreOutlined', NULL, 'system', '2020-06-20 21:49:48', 'system', '2020-06-20 21:50:37');
INSERT INTO `sys_menu_tbl` VALUES ('1274339412934791168', 'M_CHANGE_PWD', '个人资料', '1274338663366529024', 2, '/settings', 0, 'C', 'show', 'active', 'system:changepwd', 'UserOutlined', NULL, 'system', '2020-06-20 21:52:47', 'system', '2020-06-20 21:55:12');
INSERT INTO `sys_menu_tbl` VALUES ('1274339622348001280', 'M_LOGOUT', '退出登录', '1274338663366529024', 3, '/logout', 0, 'C', 'show', 'active', 'system:logout', 'LogoutOutlined', NULL, 'system', '2020-06-20 21:53:37', 'system', '2020-06-20 21:55:43');
INSERT INTO `sys_menu_tbl` VALUES ('1275409249979207680', 'C_COMMON_HIDE', '公用隐藏菜单', '0', 5, NULL, 0, 'M', 'hidden', 'active', NULL, NULL, NULL, 'system', '2020-06-23 20:43:56', 'system', '2020-06-23 20:44:09');
INSERT INTO `sys_menu_tbl` VALUES ('1275409543026839552', 'B_CHANGE_PWD', '修改密码', '1275409249979207680', 1, NULL, 0, 'F', 'hidden', 'active', 'common:changepwd', NULL, NULL, 'system', '2020-06-23 20:45:06', 'system', '2020-06-23 20:45:06');
INSERT INTO `sys_menu_tbl` VALUES ('1275409846069497856', 'B_DEPT_TREE_SELECT', '部门树状结构数据', '1275409249979207680', 2, NULL, 0, 'F', 'show', 'active', 'common:dept:treeselect', NULL, NULL, 'system', '2020-06-23 20:46:18', 'system', '2020-06-23 20:46:18');
INSERT INTO `sys_menu_tbl` VALUES ('1275410032867020800', 'B_MENU_TREE_SELECT', '菜单树状结构数据', '1275409249979207680', 3, NULL, 0, 'F', 'show', 'active', 'common:menu:treeselect', NULL, NULL, 'system', '2020-06-23 20:47:02', 'system', '2020-06-23 20:47:02');
INSERT INTO `sys_menu_tbl` VALUES ('1275410212509061120', 'B_ROLE_MENU_TREE', '菜单角色关系数据', '1268894555311312896', 1, NULL, 0, 'F', 'show', 'active', 'common:menu:roleMenuTreeSelect', NULL, NULL, 'system', '2020-06-23 20:47:45', 'system', '2020-06-23 20:47:45');
INSERT INTO `sys_menu_tbl` VALUES ('1275410212509061121', 'B_OSS_UPLOAD', 'OSS文件上传', '1275409249979207680', 5, NULL, 0, 'F', 'show', 'active', 'common:oss:upload', NULL, NULL, 'system', '2020-06-23 20:47:45', 'system', '2020-06-23 20:47:45');
INSERT INTO `sys_menu_tbl` VALUES ('1275410212509061122', 'B_FILE_UPLOAD', '普通文件上传', '1275409249979207680', 6, NULL, 0, 'F', 'show', 'active', 'common:file:upload', NULL, NULL, 'system', '2020-06-23 20:47:45', 'system', '2020-06-23 20:47:45');
INSERT INTO `sys_menu_tbl` VALUES ('1275410212509061123', 'B_HOME_DATA', 'Home页面数据', '1275409249979207680', 7, NULL, 0, 'F', 'show', 'active', 'common:home-data', NULL, NULL, 'system', '2020-06-23 20:47:45', 'system', '2020-06-23 20:47:45');
INSERT INTO `sys_menu_tbl` VALUES ('1275410212509061124', 'B_SYS_COMMON', '基础common权限', '1275409249979207680', 8, NULL, 0, 'F', 'show', 'active', 'system:common', NULL, NULL, 'system', '2020-06-23 20:47:45', 'system', '2020-06-23 20:47:45');

INSERT INTO `sys_user_tbl` (`col_id`, `col_user_domain`, `col_login_name`, `col_display`, `col_real_name`, `col_id_no`, `col_email`, `col_status`, `col_authenticated`, `col_mobile_no`, `col_is_system`, `col_address`, `col_pwd`, `col_birthday`, `col_sex`, `col_marital`, `col_nation`, `col_pay_no_pwd`, `col_pay_pwd`, `col_pay_no_pwd_point`, `col_avatar_url`, `col_post`, `col_profession`, `col_constellation`, `col_third_party_user_name`, `col_third_party_pwd`, `col_job_no`, `col_ext_user_id`, `col_authenticate_date`, `col_latest_login_time`, `col_latest_change_pwd_time`, `col_description`, `col_dept_no`, `col_share_code`, `col_parent_share_code`, `col_integral`, `col_province`, `col_city`, `col_district`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`)
VALUES ('1275410212509061125', 'intranet', 'test', NULL, '王小二', NULL, 'test@adminpro.com', 'active', b'0', '18913157427', b'0', NULL, '$2a$10$g.Gb5mKWBSDzThP7VX38xOyJB0THHA0oQkylNhxD/RGqVv//7TZ1q', NULL, 'male', '0', NULL, 0, NULL, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2020-06-04 00:00:00', NULL, NULL, '测试账号', '00001', NULL, NULL, 0, NULL, NULL, NULL, 'system', '2018-05-31 16:01:20', 'admin', NULL);
INSERT INTO `sys_user_tbl` (`col_id`, `col_user_domain`, `col_login_name`, `col_display`, `col_real_name`, `col_id_no`, `col_email`, `col_status`, `col_authenticated`, `col_mobile_no`, `col_is_system`, `col_address`, `col_pwd`, `col_birthday`, `col_sex`, `col_marital`, `col_nation`, `col_pay_no_pwd`, `col_pay_pwd`, `col_pay_no_pwd_point`, `col_avatar_url`, `col_post`, `col_profession`, `col_constellation`, `col_third_party_user_name`, `col_third_party_pwd`, `col_job_no`, `col_ext_user_id`, `col_authenticate_date`, `col_latest_login_time`, `col_latest_change_pwd_time`, `col_description`, `col_dept_no`, `col_share_code`, `col_parent_share_code`, `col_integral`, `col_province`, `col_city`, `col_district`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`)
VALUES ('1275410212509061126', 'system', 'admin', '系统管理员', '管理员', NULL, 'admin@adminpro.com', 'active', b'0', '18913157427', b'0', '', '$2a$10$QyFLvKmMRs.OCWKTCzo5DOD8JiJIa9hRh2djD5NhA/s//aVEZDugW', NULL, 'female', '0', NULL, 0, NULL, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2020-06-15 23:20:18', NULL, NULL, '系统管理员', '00001', NULL, NULL, 0, NULL, NULL, NULL, 'system', '2017-06-21 16:33:25', 'superadmin', NULL);
INSERT INTO `sys_user_tbl` (`col_id`, `col_user_domain`, `col_login_name`, `col_display`, `col_real_name`, `col_id_no`, `col_email`, `col_status`, `col_authenticated`, `col_mobile_no`, `col_is_system`, `col_address`, `col_pwd`, `col_birthday`, `col_sex`, `col_marital`, `col_nation`, `col_pay_no_pwd`, `col_pay_pwd`, `col_pay_no_pwd_point`, `col_avatar_url`, `col_post`, `col_profession`, `col_constellation`, `col_third_party_user_name`, `col_third_party_pwd`, `col_job_no`, `col_ext_user_id`, `col_authenticate_date`, `col_latest_login_time`, `col_latest_change_pwd_time`, `col_description`, `col_dept_no`, `col_share_code`, `col_parent_share_code`, `col_integral`, `col_province`, `col_city`, `col_district`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`)
VALUES ('1275410212509061127', 'system', 'superadmin', NULL, '超级管理员', NULL, 'superadmin@adminpro.com', 'active', b'0', '18913157426', b'0', NULL, '$2b$10$1Y1HcRDNpmnqt/ugsPup7.xeXSkVCqzgsFLyLSCT9gDwx5pcay9Ha', NULL, 'male', '0', NULL, 0, NULL, 0, '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2020-06-15 23:27:31', NULL, '超级管理员', '00001', NULL, NULL, 0, NULL, NULL, NULL, 'system', '2018-12-01 18:22:00', 'admin', NULL);

INSERT INTO `sys_dept_tbl` VALUES ('1271649734330814463', '00001', '0', '0', 'AdminPro集团', 1, NULL, NULL, '', NULL, NULL, NULL, 'active', NULL, '2020-06-06 21:49:08', NULL, '2020-06-06 21:50:26');
INSERT INTO `sys_dept_tbl` VALUES ('1271649734330814464', 'NO.002', '1271649734330814463', '0,1271649734330814463', '测试部门', 1, NULL, NULL, NULL, NULL, NULL, NULL, 'active', '', '2020-06-13 11:44:57', '', '2020-06-13 11:44:57');
INSERT INTO `sys_dept_tbl` VALUES ('1271649787011272704', 'NO.003', '1271649734330814463', '0,1271649734330814463', '开发部门', 1, NULL, NULL, NULL, NULL, NULL, NULL, 'active', '', '2020-06-13 11:45:10', '', '2020-06-13 11:45:10');

INSERT INTO `sys_user_domain_tbl` VALUES ('1', 'system', '系统用户', 1, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user_domain_tbl` VALUES ('2', 'intranet', '局域网用户', 1, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user_domain_tbl` VALUES ('3', 'internet', '因特网用户', 1, NULL, NULL, NULL, NULL);

INSERT INTO `sys_user_domain_env_tbl` VALUES ('1272436517226287104', 'SYS_COMMON_ROLE', NULL, NULL, NULL, '/admin/home', NULL, NULL, 'system');
INSERT INTO `sys_user_domain_env_tbl` VALUES ('1272436636734590976', 'INTRANET_COMMON_ROLE', NULL, NULL, NULL, '/biz/home', NULL, NULL, 'intranet');
INSERT INTO `sys_user_domain_env_tbl` VALUES ('1272436693781319680', 'INTERNET_COMMON_ROLE', NULL, NULL, NULL, '/biz/home', NULL, NULL, 'internet');

INSERT INTO `sys_role_tbl` VALUES ('1271976532176543744', 'SYS_ADMIN_ROLE', '系统管理员', 'active', b'1', '', '2020-06-14 09:23:32', '', '2020-06-14 09:23:32');
INSERT INTO `sys_role_tbl` VALUES ('1271976532176543745', 'SYS_SUPER_ADMIN_ROLE', '系统超级管理员', 'active', b'1', '', '2020-06-14 09:23:32', 'system', '2020-06-15 15:40:39');
INSERT INTO `sys_role_tbl` VALUES ('1271976758429884416', 'NORMAL_USER', '普通用户', 'active', b'1', '', '2020-06-14 09:24:26', '', '2020-06-14 09:24:26');
INSERT INTO `sys_role_tbl` VALUES ('1272436037200777216', 'SYS_COMMON_ROLE', '系统用户公共角色', 'active', b'1', 'system', '2020-06-15 15:49:27', 'system', '2020-06-15 15:49:27');
INSERT INTO `sys_role_tbl` VALUES ('1272436191047847936', 'INTERNET_COMMON_ROLE', '因特网用户公共角色', 'active', b'1', 'system', '2020-06-15 15:50:03', 'system', '2020-06-15 15:50:03');
INSERT INTO `sys_role_tbl` VALUES ('1272436278025129984', 'INTRANET_COMMON_ROLE', '局域网用户公共角色', 'active', b'1', 'system', '2020-06-15 15:50:24', 'system', '2020-06-15 15:50:24');

INSERT INTO `sys_post_tbl` VALUES ('1271976331739144192', 'CEO', '总裁', 1, 'active', NULL, '', '2020-06-14 09:22:44', '', '2020-06-14 09:23:02');
INSERT INTO `sys_post_tbl` VALUES ('1271976387271729152', 'COO', '财务总监', 2, 'active', NULL, '', '2020-06-14 09:22:58', '', '2020-06-14 09:22:58');

INSERT INTO `sys_user_role_assign_tbl` (`col_id`, `col_user_id`, `col_role_id`, `col_remarks`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1272433047018676224', '1275410212509061125', '1271976758429884416', NULL, '', '2020-06-15 15:37:34', '', '2020-06-15 15:37:34');
INSERT INTO `sys_user_role_assign_tbl` (`col_id`, `col_user_id`, `col_role_id`, `col_remarks`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1272433060696301568', '1275410212509061126', '1271976532176543744', NULL, '', '2020-06-15 15:37:37', '', '2020-06-15 15:37:37');
INSERT INTO `sys_user_role_assign_tbl` (`col_id`, `col_user_id`, `col_role_id`, `col_remarks`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1272433071798624256', '1275410212509061127', '1271976532176543745', NULL, '', '2020-06-15 15:37:40', '', '2020-06-15 15:37:40');

INSERT INTO `sys_user_post_assign_tbl` (`col_id`, `col_user_id`, `col_post_id`, `col_remarks`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1272059676816707584', '1275410212509061125', '1271976331739144192', NULL, '', '2020-06-14 14:53:55', '', '2020-06-14 14:53:55');
INSERT INTO `sys_user_post_assign_tbl` (`col_id`, `col_user_id`, `col_post_id`, `col_remarks`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1272059690582413312', '1275410212509061126', '1271976387271729152', NULL, '', '2020-06-14 14:53:59', '', '2020-06-14 14:53:59');
INSERT INTO `sys_user_post_assign_tbl` (`col_id`, `col_user_id`, `col_post_id`, `col_remarks`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1272059705333780480', '1275410212509061127', '1271976331739144192', NULL, '', '2020-06-14 14:54:02', '', '2020-06-14 14:54:02');

INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1278599206008918016', '1272436037200777216', '1274338663366529024', 'system', '2020-07-02 15:59:41', 'system', '2020-07-02 15:59:41');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1278599206008918017', '1272436037200777216', '1998425430192951296', 'system', '2020-07-02 15:59:41', 'system', '2020-07-02 15:59:41');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1278599206013112321', '1272436037200777216', '1274339412934791168', 'system', '2020-07-02 15:59:41', 'system', '2020-07-02 15:59:41');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1278599206013112322', '1272436037200777216', '1274339622348001280', 'system', '2020-07-02 15:59:41', 'system', '2020-07-02 15:59:41');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1278599206017306624', '1272436037200777216', '1275409543026839552', 'system', '2020-07-02 15:59:41', 'system', '2020-07-02 15:59:41');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1278599206017306625', '1272436037200777216', '1275409249979207680', 'system', '2020-07-02 15:59:41', 'system', '2020-07-02 15:59:41');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1272546672261599232', '1271976532176543745', '1268893422756958208', '', '2020-06-15 23:09:04', '', '2020-06-15 23:09:04');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1272546672265793536', '1271976532176543745', '1268894158748258304', '', '2020-06-15 23:09:04', '', '2020-06-15 23:09:04');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1272546672265793537', '1271976532176543745', '1268894323924144128', '', '2020-06-15 23:09:04', '', '2020-06-15 23:09:04');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1272546672269987840', '1271976532176543745', '1268894555311312896', '', '2020-06-15 23:09:04', '', '2020-06-15 23:09:04');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1272546672269987841', '1271976532176543745', '1268895433615347711', '', '2020-06-15 23:09:04', '', '2020-06-15 23:09:04');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1272546672274182144', '1271976532176543745', '1268895433615347712', '', '2020-06-15 23:09:04', '', '2020-06-15 23:09:04');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1272546672274182145', '1271976532176543745', '1268895694639468544', '', '2020-06-15 23:09:04', '', '2020-06-15 23:09:04');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1272546672278376448', '1271976532176543745', '1268895982188367872', '', '2020-06-15 23:09:04', '', '2020-06-15 23:09:04');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1272546672278376450', '1271976532176543745', '1268896596561629184', '', '2020-06-15 23:09:04', '', '2020-06-15 23:09:04');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1272546672282570752', '1271976532176543745', '1268897921470959616', '', '2020-06-15 23:09:04', '', '2020-06-15 23:09:04');

INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1272546672282570754', '1271976532176543745', '1268898457960189952', '', '2020-06-15 23:09:04', '', '2020-06-15 23:09:04');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1272546672286765056', '1271976532176543745', '1268898639556775936', '', '2020-06-15 23:09:04', '', '2020-06-15 23:09:04');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1272546672286765057', '1271976532176543745', '1268898849364250624', '', '2020-06-15 23:09:04', '', '2020-06-15 23:09:04');

INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1272546672290959360', '1271976532176543745', '1268899376529543168', '', '2020-06-15 23:09:04', '', '2020-06-15 23:09:04');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1275414005577551873', '1271976532176543745', '1275409249979207680', 'system', '2020-06-23 21:02:50', 'system', '2020-06-23 21:02:50');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1275414005577551874', '1271976532176543745', '1275409543026839552', 'system', '2020-06-23 21:02:50', 'system', '2020-06-23 21:02:50');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1275414005581746176', '1271976532176543745', '1275409846069497856', 'system', '2020-06-23 21:02:50', 'system', '2020-06-23 21:02:50');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1275414005581746177', '1271976532176543745', '1275410032867020800', 'system', '2020-06-23 21:02:50', 'system', '2020-06-23 21:02:50');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1275414005581746178', '1271976532176543745', '1275410212509061120', 'system', '2020-06-23 21:02:50', 'system', '2020-06-23 21:02:50');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1275414005581746179', '1271976532176543745', '1275410212509061121', 'system', '2020-06-23 21:02:50', 'system', '2020-06-23 21:02:50');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1275414005581746180', '1271976532176543745', '1275410212509061122', 'system', '2020-06-23 21:02:50', 'system', '2020-06-23 21:02:50');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1275414005581746181', '1271976532176543745', '1275410212509061123', 'system', '2020-06-23 21:02:50', 'system', '2020-06-23 21:02:50');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1275414005581746182', '1272436037200777216', '1275410212509061124', 'system', '2020-06-23 21:02:50', 'system', '2020-06-23 21:02:50');

INSERT INTO `sys_config_tbl` VALUES ('1273599214896881664', 'app.session.filter.white.list', 'Session过滤器白名单', '/sessionExpired,/sessionTerminate,/js/**,/plugins/**,/css/**,/images/**,/upload/**,/api/**,/img/**,/icons/**,/logout,/,/login/**', 0, NULL, 'system', '2020-06-18 20:51:30', 'system', '2020-06-20 23:00:42');
INSERT INTO `sys_config_tbl` VALUES ('1274356700303986688', 'app.platform.name', '平台名称', 'AdminPro开发平台', NULL, NULL, 'system', '2020-06-20 23:01:28', 'system', '2020-06-20 23:01:28');
INSERT INTO `sys_config_tbl` VALUES ('1274356700303986689', 'app.platform.short.name', '平台短名称', 'AdminPro', NULL, NULL, 'system', '2020-06-20 23:01:28', 'system', '2020-06-20 23:01:28');
INSERT INTO `sys_config_tbl` VALUES ('1274356801701285888', 'app.server.address', '服务器地址', 'http://127.0.0.1:8080', NULL, NULL, 'system', '2020-06-20 23:01:53', 'system', '2020-06-20 23:01:53');
INSERT INTO `sys_config_tbl` VALUES ('1274356897968951296', 'app.check.capture.domains', '需要校验验证码的用户域', 'system', NULL, NULL, 'system', '2020-06-20 23:02:16', 'system', '2020-06-20 23:02:16');
INSERT INTO `sys_config_tbl` VALUES ('1274357001757003776', 'login.url', '登录地址', '/login/admin', NULL, NULL, 'system', '2020-06-20 23:02:40', 'system', '2020-06-20 23:02:40');
INSERT INTO `sys_config_tbl` VALUES ('1274357264890859520', 'app.server.port', '服务器访问端口号', '8080', NULL, NULL, 'system', '2020-06-20 23:03:43', 'system', '2020-06-20 23:03:43');
INSERT INTO `sys_config_tbl` VALUES ('1274357368397893632', 'app.server.ip', '服务器访问IP', '127.0.0.1', NULL, NULL, 'system', '2020-06-20 23:04:08', 'system', '2020-06-20 23:04:08');
INSERT INTO `sys_config_tbl` VALUES ('1274357580063444992', 'app.address.enabled', '是否记录访问地址', 'true', NULL, NULL, 'system', '2020-06-20 23:04:58', 'system', '2020-06-20 23:04:58');
INSERT INTO `sys_config_tbl` VALUES ('1274357807017234432', 'app.third.party.pwd.encrypt.enable', '第三方密码是否加密存储', 'false', NULL, NULL, 'system', '2020-06-20 23:05:52', 'system', '2020-06-20 23:05:52');
INSERT INTO `sys_config_tbl` VALUES ('1274357884490223616', 'app.third.party.pwd.encrypt', '第三方密码加密key', 'szyh$123', NULL, NULL, 'system', '2020-06-20 23:06:11', 'system', '2020-06-20 23:06:11');
INSERT INTO `sys_config_tbl` VALUES ('1274358022235361280', 'app.copy.right', '版权方', 'Copyright © {year} {platformShortName}. All rights reserved.', NULL, NULL, 'system', '2020-06-20 23:06:44', 'system', '2020-06-20 23:06:44');
INSERT INTO `sys_config_tbl` VALUES ('1274358022235361282', 'app.session.does.not.exist', '/', 'Session不存在跳转URL', NULL, NULL, 'system', '2020-06-20 23:06:44', 'system', '2020-06-20 23:06:44');
-- changeset simon:202006262248
INSERT INTO `sys_config_tbl` VALUES ('1274358022235361281', 'app.ueditor.file.store.type', 'UEditor图片上传存放方式', 'local', NULL, NULL, 'system', '2020-06-20 23:06:44', 'system', '2020-06-20 23:06:44');
-- changeset simon:202007201609
UPDATE sys_config_tbl SET col_value = '/' WHERE col_key='app.session.does.not.exist';
UPDATE sys_config_tbl SET col_name = 'Session不存在跳转URL' WHERE col_key='app.session.does.not.exist';
-- changeset simon:202007242303
INSERT INTO `sys_config_tbl` VALUES ('1274358022235361382', 'app.ueditor.file.store.thumbnail', 'UEditor图片上传是否压缩', 'true', NULL, NULL, 'system', '2020-06-20 23:06:44', 'system', '2020-06-20 23:06:44');
-- changeset simon:202008222253
insert into sys_menu_tbl (col_id, col_name, col_parent_id, col_order_num, col_url, col_display, col_type, col_visible, col_permission, col_icon, col_status, col_remark, col_created_by, col_created_at, col_updated_by, col_updated_at)
values('1297184518902009856', 'M_DICT', '1268893422756958208', '10', '/admin/dict', '字典管理', 'C', 'show', 'system:dict', 'BookOutlined', 'active', '', NULL, '2020-08-22 22:51:04', '', '2020-08-22 22:51:04');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1297184518906204160', '1271976532176543745', '1297184518902009856', '', '2020-08-22 22:51:04', '', '2020-08-22 22:51:04');

-- changeset simon:202008231430
drop table sys_dict_tbl;
drop table sys_dict_data_tbl;
create table sys_dict_tbl(
  `col_id` varchar(64) NOT NULL COMMENT '字典主键',
  `col_name` varchar(128) NOT NULL COMMENT '字典名称',
  `col_key` varchar(128) NOT NULL COMMENT '字典键值',
  `col_status` varchar(64) NOT NULL COMMENT '状态',
  `col_remark` varchar(64) DEFAULT NULL COMMENT '备注',

  `col_created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `col_created_at` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `col_updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `col_updated_at` datetime(0) NULL DEFAULT NULL COMMENT '更新日期',
  primary key (col_id),
  unique (col_key)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '字典类型表';

create table sys_dict_data_tbl(
  `col_id` varchar(64) NOT NULL COMMENT 'ID',
  `col_key` varchar(64) NOT NULL COMMENT '字典键值',
  `col_order` int(4) DEFAULT 0 COMMENT '排序',
  `col_label` varchar(128) DEFAULT NULL COMMENT '标签',
  `col_value` varchar(128) DEFAULT NULL COMMENT '键值',
  `col_css_class` varchar(128) DEFAULT NULL COMMENT '样式属性',
  `col_status` varchar(64) DEFAULT NULL COMMENT '状态',

  `col_created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `col_created_at` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
  `col_updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `col_updated_at` datetime(0) NULL DEFAULT NULL COMMENT '更新日期',
  primary key (col_id)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '字典数据表';

-- changeset simon:202008241052
INSERT INTO `sys_dict_tbl` VALUES ('1297467037672808448', '公共状态', 'common_status', 'active', NULL, 'system', '2020-08-23 17:33:42', 'system', '2020-08-23 19:51:28');
INSERT INTO `sys_dict_tbl` VALUES ('1297492281137569792', '是否', 'yes_no', 'active', NULL, 'system', '2020-08-23 19:14:01', 'system', '2020-08-23 19:52:11');
INSERT INTO `sys_dict_data_tbl` VALUES ('1297501705625546752', 'common_status', 1, '正常', 'active', 'success', 'active', 'system', '2020-08-23 19:51:28', 'system', '2020-08-23 19:51:28');
INSERT INTO `sys_dict_data_tbl` VALUES ('1297501705629741056', 'common_status', 2, '停用', 'inactive', 'warning', 'active', 'system', '2020-08-23 19:51:28', 'system', '2020-08-23 19:51:28');
INSERT INTO `sys_dict_data_tbl` VALUES ('1297501886920142848', 'yes_no', 1, '是', 'true', 'success', 'active', 'system', '2020-08-23 19:52:11', 'system', '2020-08-23 19:52:11');
INSERT INTO `sys_dict_data_tbl` VALUES ('1297501886920142849', 'yes_no', 2, '否', 'false', 'warning', 'active', 'system', '2020-08-23 19:52:11', 'system', '2020-08-23 19:52:11');

-- changeset simon:202203211609
alter table sys_user_domain_env_tbl add COLUMN col_login_url varchar(255) DEFAULT NULL COMMENT '登录地址' after col_home_page_url;

-- changeset simon:202304181557
alter table sys_dept_tbl add COLUMN col_logo_path varchar(255) DEFAULT NULL COMMENT '部门图标' after col_email;
-- changeset simon:202304212218
alter table sys_dept_tbl add COLUMN col_custom_login int(1) DEFAULT 0 COMMENT '是否定制login' after col_logo_path;

-- changeset simon:202404081415
alter table sys_config_tbl MODIFY COLUMN col_value varchar(1024) NOT NULL;

-- changeset simon:202410131221
CREATE TABLE `sys_audit_log_tbl`  (
    `col_id` varchar(64) NOT NULL COMMENT '主键ID',
    `col_log_date` datetime(0) NOT NULL COMMENT '日志时间',
    `col_category` varchar(64) NOT NULL COMMENT '分类',
    `col_module` varchar(64) NOT NULL COMMENT '模块',
    `col_ip_address` varchar(80) DEFAULT NULL COMMENT '访问IP',
    `col_status` varchar(64) DEFAULT NULL COMMENT '状态',
    `col_event` varchar(255) DEFAULT NULL COMMENT '事件',
    `col_before_data` mediumtext COMMENT '事件前数据',
    `col_after_data` mediumtext COMMENT '事件后数据',
    `col_jti` varchar(100) DEFAULT NULL COMMENT 'JWT Token ID',

    `col_created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
    `col_created_at` datetime(0) NULL DEFAULT NULL COMMENT '创建日期',
    `col_updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
    `col_updated_at` datetime(0) NULL DEFAULT NULL COMMENT '更新日期',
    PRIMARY KEY (`col_id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT = '日志记录表';

-- changeset simon:2024101301
INSERT INTO `sys_menu_tbl` (`col_id`, `col_name`, `col_display`, `col_parent_id`, `col_order_num`, `col_url`, `col_is_frame`, `col_type`, `col_visible`, `col_status`, `col_permission`, `col_icon`, `col_remark`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1845326874121736192', 'M_AUDIT', '日志', '1268897921470959616', 4, '/admin/audit', 0, 'C', 'show', 'active', 'system:audit', 'FileTextOutlined', NULL, 'system', '2024-10-13 12:53:06', 'system', '2024-10-13 12:56:00');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1845327703742484480', '1271976532176543745', '1845326874121736192', 'system', '2024-10-13 12:56:24', 'system', '2024-10-13 12:56:24');

-- changeset simon:202511140945
INSERT INTO `sys_menu_tbl` VALUES ('1275410212509061132', 'B_FETCH_DOMAINS', '获取所有domains', '1275409249979207680', 7, NULL, 0, 'F', 'show', 'active', 'common:domains', NULL, NULL, 'system', '2020-06-23 20:47:45', 'system', '2020-06-23 20:47:45');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('1275414005581746190', '1271976532176543745', '1275410212509061132', 'system', '2020-06-23 21:02:50', 'system', '2020-06-23 21:02:50');

-- changeset simon:202511261626
ALTER TABLE SYS_SYS_LOG_TBL
    ADD COLUMN COL_CATEGORY VARCHAR(64) NULL COMMENT '分类',
    ADD COLUMN COL_MODULE VARCHAR(64) NULL COMMENT '模块',
    ADD COLUMN COL_STATUS VARCHAR(32) NULL COMMENT '状态',
    ADD COLUMN COL_DESCRIPTION VARCHAR(255) NULL COMMENT '操作描述';

ALTER TABLE SYS_AUDIT_LOG_TBL
    DROP COLUMN COL_LOG_DATE,
    ADD COLUMN COL_EXECUTION_TIME BIGINT NULL COMMENT '执行时间（毫秒）';

-- changeset simon:202511301748 validCheckSum:9:371b4964f605db42ed8c998476d4d169
INSERT INTO `sys_config_tbl` VALUES (
        CONCAT('PWD_RULE_', UNIX_TIMESTAMP(NOW()) * 1000),
        'app.password.rule',
        '密码规则配置',
        '{"minLength":8,"maxLength":20,"requireLowerCase":true,"requireUpperCase":true,"requireDigit":true,"requireSpecialChar":true,"specialChars":"@$!%*?&"}',
        0,
        '密码规则配置，JSON格式。包含最小长度、最大长度、是否需要大小写字母、数字、特殊字符等规则',
        'system',

        NOW(),
        'system',

        NOW()
    );

-- changeset simon:202601181513
-- ----------------------------
-- Table structure for sys_user_device
-- ----------------------------
create table if not exists sys_user_device (
  id varchar(36) not null comment '主键ID' primary key,
  user_id varchar(36) not null comment '用户ID',
  device_id varchar(100) not null comment '设备唯一标识',
  device_name varchar(100) comment '设备名称(如: iPhone 13, Chrome on Mac)',
  platform varchar(20) not null comment '平台类型(web/mobile/tablet)',
  os varchar(50) comment '操作系统',
  browser varchar(50) comment '浏览器',
  
  ip varchar(64) comment '最后登录IP',
  last_active_at datetime comment '最后活跃时间',
  refresh_token_jti varchar(100) comment '当前有效的Refresh Token JTI',
  
  is_active tinyint(1) default 1 comment '是否激活',
  created_at datetime default current_timestamp comment '创建时间',
  updated_at datetime default current_timestamp on update current_timestamp comment '更新时间',
  
  unique key uk_user_device (user_id, device_id),
  key idx_user_id (user_id),
  key idx_rt_jti (refresh_token_jti)
) engine=InnoDB default charset=utf8mb4 comment='用户设备/Token表';

-- changeset simon:202601181630
ALTER TABLE sys_user_device DROP COLUMN os;
ALTER TABLE sys_user_device DROP COLUMN browser;
ALTER TABLE sys_user_device CHANGE ip last_ip VARCHAR(64) COMMENT '最后登录IP';
ALTER TABLE sys_user_device ADD COLUMN last_user_agent VARCHAR(500) COMMENT 'User Agent' AFTER last_ip;


-- changeset simon:202601182245
-- 1. 新增“设备管理”菜单 (挂载在“工作空间”下，ID: 1274338663366529024) 用户自己的设备管理
-- 使用新ID: 1845327703742484490
INSERT INTO `sys_menu_tbl` 
(`col_id`, `col_name`, `col_display`, `col_parent_id`, `col_order_num`, `col_url`, `col_is_frame`, `col_type`, `col_visible`, `col_status`, `col_permission`, `col_icon`, `col_remark`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) 
VALUES ('1845327703742484490', 'M_DEVICE_MGR', '设备管理', '1274338663366529024', 4, '/account/devices', 0, 'C', 'show', 'active', NULL, 'MobileOutlined', NULL, 'system', NOW(), 'system', NOW());

-- 2. 分配权限给“系统用户公共角色” (SYS_COMMON_ROLE, ID: 1272436037200777216)
-- 使用新ID: 1845327703742484491
INSERT INTO `sys_role_menu_assign_tbl` 
(`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) 
VALUES ('1845327703742484491', '1272436037200777216', '1845327703742484490', 'system', NOW(), 'system', NOW());

-- 全局系统的设备管理
INSERT INTO `sys_menu_tbl`
(`col_id`, `col_name`, `col_display`, `col_parent_id`, `col_order_num`, `col_url`, `col_is_frame`, `col_type`, `col_visible`, `col_status`, `col_permission`, `col_icon`, `col_remark`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`)
VALUES ('1845327703742484500', 'M_SYS_DEVICE', '在线设备', '1268893422756958208', 10, '/admin/sys/device', 0, 'C', 'show', 'active', 'system:device:list', 'DesktopOutlined', NULL, 'system', NOW(), 'system', NOW());

INSERT INTO `sys_role_menu_assign_tbl`
(`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`)
VALUES ('1845327703742484501', '1272436037200777216', '1845327703742484500', 'system', NOW(), 'system', NOW());

INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('2019037711247216640', '1272436278025129984', '1998425430192951296', '1275410212509061127', '2026-02-04 21:17:57', '1275410212509061127', '2026-02-04 21:17:57');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('2019037711251410944', '1272436278025129984', '1274338663366529024', '1275410212509061127', '2026-02-04 21:17:57', '1275410212509061127', '2026-02-04 21:17:57');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('2019037711255605248', '1272436278025129984', '1274339412934791168', '1275410212509061127', '2026-02-04 21:17:57', '1275410212509061127', '2026-02-04 21:17:57');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('2019037711259799552', '1272436278025129984', '1274339622348001280', '1275410212509061127', '2026-02-04 21:17:57', '1275410212509061127', '2026-02-04 21:17:57');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('2019037711263993856', '1272436278025129984', '1845327703742484490', '1275410212509061127', '2026-02-04 21:17:57', '1275410212509061127', '2026-02-04 21:17:57');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('2019037711268188160', '1272436278025129984', '1275409543026839552', '1275410212509061127', '2026-02-04 21:17:57', '1275410212509061127', '2026-02-04 21:17:57');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('2019037711272382464', '1272436278025129984', '1275410212509061121', '1275410212509061127', '2026-02-04 21:17:57', '1275410212509061127', '2026-02-04 21:17:57');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('2019037711276576768', '1272436278025129984', '1275410212509061122', '1275410212509061127', '2026-02-04 21:17:57', '1275410212509061127', '2026-02-04 21:17:57');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('2019037711280771072', '1272436278025129984', '1275410212509061124', '1275410212509061127', '2026-02-04 21:17:57', '1275410212509061127', '2026-02-04 21:17:57');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('2019037711284965376', '1272436278025129984', '1275410212509061132', '1275410212509061127', '2026-02-04 21:17:57', '1275410212509061127', '2026-02-04 21:17:57');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('2019037822081699840', '1272436191047847936', '1274338663366529024', '1275410212509061127', '2026-02-04 21:18:23', '1275410212509061127', '2026-02-04 21:18:23');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('2019037822090088448', '1272436191047847936', '1274339412934791168', '1275410212509061127', '2026-02-04 21:18:23', '1275410212509061127', '2026-02-04 21:18:23');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('2019037822098477056', '1272436191047847936', '1274339622348001280', '1275410212509061127', '2026-02-04 21:18:23', '1275410212509061127', '2026-02-04 21:18:23');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('2019037822102671360', '1272436191047847936', '1275409543026839552', '1275410212509061127', '2026-02-04 21:18:23', '1275410212509061127', '2026-02-04 21:18:23');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('2019037822106865664', '1272436191047847936', '1275410212509061121', '1275410212509061127', '2026-02-04 21:18:23', '1275410212509061127', '2026-02-04 21:18:23');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('2019037822106865665', '1272436191047847936', '1275410212509061122', '1275410212509061127', '2026-02-04 21:18:23', '1275410212509061127', '2026-02-04 21:18:23');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('2019037822111059968', '1272436191047847936', '1275410212509061124', '1275410212509061127', '2026-02-04 21:18:23', '1275410212509061127', '2026-02-04 21:18:23');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('2019037822115254272', '1272436191047847936', '1275410212509061132', '1275410212509061127', '2026-02-04 21:18:23', '1275410212509061127', '2026-02-04 21:18:23');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('2019037822119448576', '1272436191047847936', '1845327703742484490', '1275410212509061127', '2026-02-04 21:18:23', '1275410212509061127', '2026-02-04 21:18:23');
INSERT INTO `sys_role_menu_assign_tbl` (`col_id`, `col_role_id`, `col_menu_id`, `col_created_by`, `col_created_at`, `col_updated_by`, `col_updated_at`) VALUES ('2019037822119448577', '1272436191047847936', '1998425430192951296', '1275410212509061127', '2026-02-04 21:18:23', '1275410212509061127', '2026-02-04 21:18:23');

