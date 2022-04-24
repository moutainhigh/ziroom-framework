package com.ziroom.ferrari.repository.consumer;

import com.ziroom.ferrari.repository.consumer.entity.FerrariMessageConsumer;
import com.ziroom.ferrari.repository.core.BaseDao;
import com.ziroom.ferrari.repository.core.annotation.DaoDescription;
import com.ziroom.ferrari.repository.core.constant.OrmFrameEnum;
import org.springframework.stereotype.Repository;

/**
 * @Author: J.T.
 * @Date: 2021/8/30 9:59
 * @Version 1.0
 */
@Repository
@DaoDescription(ormFrame = OrmFrameEnum.JDBC_TEMPLATE, settingBean = "masterOnlyJdbcSettings")
public class FerrariMessageConsumerDao extends BaseDao<FerrariMessageConsumer> {


}
