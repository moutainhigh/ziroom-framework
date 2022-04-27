package com.ziroom.framework.jdbc;

import com.github.pagehelper.PageInfo;
import com.ziroom.framework.common.exception.IllegalAccessRuntimeException;
import com.ziroom.framework.common.exception.InstantiationRuntimeException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
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
     * @param list 分页数据
     * @return 封装后数据
     */
    public static <T> PageResponse<T> pack(List<T> list) {
        PageResponse<T> response = new PageResponse<>();
        PageInfo<T> pageInfo = new PageInfo<>(list);
        response.setPages(pageInfo.getPages());
        response.setList(list);
        response.setTotal(pageInfo.getTotal());
        return response;
    }

    /**
     * 通用分页响应，转换为目标类
     * @param list 待转换的数据
     * @param targetType 目标类
     * @param <T> Source
     * @param <U> Target
     * @return 转换后数据
     */
    public static <T, U> PageResponse<U> pack(List<T> list, Class<U> targetType) {
        PageResponse<U> response = new PageResponse<>();
        if (list == null || list.size() <= 0) {
            response.setPages(0);
            response.setTotal(0L);
            response.setList(Collections.emptyList());
            return response;
        }
        PageInfo<U> pageInfo = new PageInfo<>();
        List<U> targetList = new ArrayList<>();
        for (T element : list) {
            U target = null;
            try {
                target = targetType.newInstance();
            } catch (InstantiationException e) {
                throw new InstantiationRuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new IllegalAccessRuntimeException(e);
            }
            BeanUtils.copyProperties(element, target);
            targetList.add(target);
        }
        response.setPages(pageInfo.getPages());
        response.setList(targetList);
        response.setTotal(pageInfo.getTotal());
        return response;
    }

}
