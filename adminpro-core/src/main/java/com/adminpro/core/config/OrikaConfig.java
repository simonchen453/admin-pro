/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */

package com.adminpro.core.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ModelMapper 用于 dto -> entity 的转换
 * 
 * 使用 ModelMapper 替代 Orika，完全支持 Java 21，无需 JVM 参数
 * 
 * @author simon
 */
@Configuration
public class OrikaConfig {

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		// 配置匹配策略：严格匹配字段名
		modelMapper.getConfiguration()
			.setMatchingStrategy(MatchingStrategies.STRICT)
			.setSkipNullEnabled(true)
			.setFieldMatchingEnabled(true)
			.setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
		return modelMapper;
	}
	
	/**
	 * 为了保持向后兼容，保留 mapperFacade 方法名
	 * 但返回 ModelMapper 实例
	 * 
	 * @deprecated 建议直接使用 modelMapper() 方法
	 */
	@Bean
	@Deprecated
	public ModelMapper mapperFacade() {
		return modelMapper();
	} 
}
