export interface ${className}Entity {
<#list entityColumns as column>
  ${column.attrname}: <#if tsTypeMap[column.dataType]??>${tsTypeMap[column.dataType]}<#else>string</#if>;
</#list>
}

export interface ${className}SearchForm {
<#list entityColumns as column>
<#if column.attrname != primaryKey.attrname>
  ${column.attrname}?: <#if tsTypeMap[column.dataType]??>${tsTypeMap[column.dataType]}<#else>string</#if>;
</#if>
</#list>
  pageNo?: number;
  pageSize?: number;
}

export interface ${className}CreateRequest {
<#list entityColumns as column>
<#if column.attrname != primaryKey.attrname>
  ${column.attrname}: <#if tsTypeMap[column.dataType]??>${tsTypeMap[column.dataType]}<#else>string</#if>;
</#if>
</#list>
}

export interface ${className}UpdateRequest {
  ${primaryKey.attrname}: <#if tsTypeMap[primaryKey.dataType]??>${tsTypeMap[primaryKey.dataType]}<#else>string</#if>;
<#list entityColumns as column>
<#if column.attrname != primaryKey.attrname>
  ${column.attrname}?: <#if tsTypeMap[column.dataType]??>${tsTypeMap[column.dataType]}<#else>string</#if>;
</#if>
</#list>
}

export interface ${className}DetailResponse extends ${className}Entity {
}

export interface ${className}ListResponse {
  list?: ${className}Entity[];
  records?: ${className}Entity[];
  totalCount?: number;
  pagination?: {
    total: number;
  };
}

