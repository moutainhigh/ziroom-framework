package com.ziroom.framework.jdbc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 通用分页查询请求实体
 *
 * @author baoll
 * @date 2021/12/13
 */
@Data
@ApiModel(value = "通用分页查询请求实体")
public class PageRequest<T> implements Serializable {

    @ApiModelProperty(value = "分页查询过滤条件")
    private T filter;

    @ApiModelProperty(value = "分页查询页码")
    private Integer pageIndex = 1;

    @ApiModelProperty(value = "分页查询数据条数")
    private Integer pageSize = 10;

}
