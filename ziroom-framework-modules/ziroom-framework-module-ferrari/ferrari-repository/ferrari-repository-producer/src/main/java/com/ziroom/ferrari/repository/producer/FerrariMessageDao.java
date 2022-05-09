package com.ziroom.ferrari.repository.producer;

import com.ziroom.ferrari.repository.core.BaseDao;
import com.ziroom.ferrari.repository.core.annotation.DaoDescription;
import com.ziroom.ferrari.repository.core.constant.OrmFrameEnum;
import com.ziroom.ferrari.repository.core.query.Criteria;
import com.ziroom.ferrari.repository.producer.entity.FerrariMessage;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Author: J.T.
 * @Date: 2021/8/30 9:59
 * @Version 1.0
 */
@Repository
@DaoDescription(ormFrame = OrmFrameEnum.JDBC_TEMPLATE, settingBean = "masterOnlyJdbcSettings")
public class FerrariMessageDao extends BaseDao<FerrariMessage> {


    public List<FerrariMessage> findByStatus(Integer messageStatus, String messageType) {
        if (messageStatus == null) {
            return null;
        }
        return super.findList(Criteria.where("msgStatus", messageStatus).eq("msgType", messageType));
    }

    public void updateSendStatus(FerrariMessage ferrariMessage) {
        if (ferrariMessage == null || ferrariMessage.getId() == 0) {
            return;
        }
        super.update(ferrariMessage, Arrays.asList("msgStatus", "sendTime"));
    }


    public List<FerrariMessage> findBySendTime(Integer messageStatus, Date sendDate) {
        if (messageStatus == null) {
            return null;
        }
        return super.findList(Criteria.where("msgStatus", messageStatus).lt("sendTime", sendDate));
    }
}
