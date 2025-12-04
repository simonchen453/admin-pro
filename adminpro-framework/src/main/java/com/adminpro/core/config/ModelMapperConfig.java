package com.adminpro.core.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ModelMapper 配置类
 * 
 * 用于 dto -> entity 的对象映射转换
 * 使用 ModelMapper 替代 Orika，完全支持 Java 21，无需 JVM 参数
 * 
 * @author simon
 */
@Configuration
public class ModelMapperConfig {

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
}

