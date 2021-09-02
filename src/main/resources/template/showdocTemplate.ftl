[TOC]

| 请求方式 | ${requestMethod!''} |
|:--- |:---  |
| 数据请求格式 |<#if requestDomain??>${requestDomain.consumes!'json'}</#if> |
| 数据响应格式 |<#if requestDomain??>${requestDomain.produces!'json'}</#if> |
| 公共URL前缀 |<#if baseConfiguration??>${baseConfiguration.apiCommonAddress!''}</#if> |
<#if urlList??>
    <#list urlList as url><#if url??>| URL_${url_index!''} | ${url!''} |
    </#if>
    </#list>
</#if>
| 请求报文示例 | 见该文档尾部 |
| 响应报文示例 | 同上 |
| 接口详细说明 | ${description!''}||


## 请求参数

| 编号 | 变量名 | 变量描述 | 类型 | 必填 | 备注 |
|:--- |:--- |:--- |:--- |:--- |:--- |
<#if requestParam??><#list requestParam as request><#if request??>|${request_index}|${request.name!''}|${request.description!''} |${request.type!''} |${request.must!''} |${request.remark!''} |
</#if></#list>
</#if>


### 请求报文示例

```
${reqExample!''}
```

## 公共响应参数

| 编号 | 变量名 | 变量描述 | 类型 | 必填 | 备注 |
|:--- |:--- |:--- |:--- |:--- |:--- |
<#if commonParam??><#list commonParam as resp><#if resp??>|${resp_index}|${resp.name!''}|${resp.description!''} |${resp.type!''} |${resp.must!''} |${resp.remark!''} |
</#if></#list>
</#if>

## 业务响应参数 ${businessDataName!''}

| 编号 | 变量名 | 变量描述 | 类型 | 必填 | 备注 |
|:--- |:--- |:--- |:--- |:--- |:--- |
<#if requestReturn??><#list requestReturn as resp><#if resp??>|${resp_index}|${resp.name!''}|${resp.description!''} |${resp.type!''} |${resp.must!''} |${resp.remark!''} |
</#if></#list>
</#if>


### 响应报文示例

```
${respExample!''}
```
