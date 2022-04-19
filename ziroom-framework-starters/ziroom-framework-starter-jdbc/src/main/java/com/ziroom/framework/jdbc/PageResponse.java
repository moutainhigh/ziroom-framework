package com.ziroom.framework.jdbc;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 通用分页查询响应实体
 *
 * @author baoll
 * @date 2021/12/20
 */
@Data
@ApiModel(value = "通用分页查询响应实体")
public class PageResponse<T> implements Serializable {

    private PageResponse() {
    }

    @ApiModelProperty(value = "结果集")
    private List<T> list;

    @ApiModelProperty(value = "总数据条数")
    private Long total;

    @ApiModelProperty("总页数")
    private Integer pages;

    /**
     * 封装通用分页响应
     *
     * @param list 分页数据
     * @return
     */
    public static <T> PageResponse<T> pack(List<T> list) {
        PageResponse<T> response = new PageResponse<>();
        PageInfo<T> pageInfo = new PageInfo<>(list);
        response.setPages(pageInfo.getPages());
        response.setList(list);
        response.setTotal(pageInfo.getTotal());
        return response;
    }

}
