<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${package}.${className}Mapper">

    <resultMap type="${className}Entity" id="${className}Result">
        <#list columns as column>
            <result property="${column.attrname}"    column="${column.columnName}"    />
        </#list>
    </resultMap>

    <sql id="find${className}Vo">
        select<#list columns as column> ${column.columnName}<#if column_has_next>,</#if></#list> from ${tableName}
    </sql>

    <select id="findList" parameterType="${className}Entity" resultMap="${className}Result">
        <include refid="find${className}Vo"/>
        <where>
        <#list columns as column>
            <if test="${column.attrname} != null and ${column.attrname}.trim() != ''"> and ${column.columnName} = #{${column.attrname}}</if>
        </#list>
    </where>
    </select>

    <select id="findById" parameterType="${primaryKey.attrType}" resultMap="${className}Result">
        <include refid="find${className}Vo"/>
        where ${primaryKey.columnName} = #{${primaryKey.attrname}}
    </select>

    <insert id="insert" parameterType="${className}Entity"<#if primaryKey.extra == 'auto_increment'> useGeneratedKeys="true" keyProperty="${primaryKey.attrname}"</#if>>
        insert into ${tableName}
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <#list columns as column>
            <#if column.columnName != primaryKey.columnName || primaryKey.extra != 'auto_increment'>
            <if test="${column.attrname} != null and ${column.attrname} != '' ">${column.columnName},</if>
            </#if>
            </#list>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <#list columns as column>
            <#if column.columnName != primaryKey.columnName || primaryKey.extra != 'auto_increment'>
            <if test="${column.attrname} != null and ${column.attrname} != ''">#{${column.attrname}},</if>
            </#if>
            </#list>
        </trim>
    </insert>

    <update id="update" parameterType="${className}Entity">
        update ${tableName}
        <trim prefix="SET" suffixOverrides=",">
            <#list columns as column>
            <#if column.columnName != primaryKey.columnName>
            <if test="${column.attrname} != null <#if column.attrType == 'String' > and ${column.attrname} != '' </#if> ">${column.columnName} = #{${column.attrname}},</if>
            </#if>
            </#list>
        </trim>
        where ${primaryKey.columnName} = #{${primaryKey.attrname}}
    </update>

    <delete id="deleteById" parameterType="${primaryKey.attrType}">
        delete from ${tableName} where ${primaryKey.columnName} = #{${primaryKey.attrname}}
    </delete>

    <delete id="deleteByIds" parameterType="String">
        delete from ${tableName} where ${primaryKey.columnName} in
        <foreach item="${primaryKey.attrname}" collection="array" open="(" separator="," close=")">
            #{${primaryKey.attrname}}
        </foreach>
    </delete>

</mapper>

