package com.ziroom.framework.common.api.pojo;

import java.io.Serializable;
import lombok.Data;

/**
 * Created by liangrk on 2022/4/18.
 */
@Data
public class PageRequest implements Serializable {

    Integer pageNumber;

    Integer pageSize;

}
