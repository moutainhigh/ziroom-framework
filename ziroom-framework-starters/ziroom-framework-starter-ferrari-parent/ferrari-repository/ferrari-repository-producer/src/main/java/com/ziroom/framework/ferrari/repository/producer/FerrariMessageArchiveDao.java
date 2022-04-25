package com.ziroom.framework.ferrari.repository.producer;

import com.ziroom.framework.ferrari.repository.core.BaseDao;
import com.ziroom.framework.ferrari.repository.core.annotation.DaoDescription;
import com.ziroom.framework.ferrari.repository.core.constant.OrmFrameEnum;
import com.ziroom.framework.ferrari.repository.producer.entity.FerrariMessageArchive;
import org.springframework.stereotype.Repository;

/**
 * @Author: J.T.
 * @Date: 2021/8/30 9:59
 * @Version 1.0
 */
@Repository
@DaoDescription(ormFrame = OrmFrameEnum.JDBC_TEMPLATE, settingBean = "masterOnlyJdbcSettings")
public class FerrariMessageArchiveDao extends BaseDao<FerrariMessageArchive> {


}
