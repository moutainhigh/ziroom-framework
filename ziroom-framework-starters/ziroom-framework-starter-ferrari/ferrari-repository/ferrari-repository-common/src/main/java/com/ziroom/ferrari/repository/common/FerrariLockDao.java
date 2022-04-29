package com.ziroom.ferrari.repository.common;

import com.ziroom.ferrari.repository.common.entity.FerrariLockEntity;
import com.ziroom.ferrari.repository.core.BaseDao;
import com.ziroom.ferrari.repository.core.annotation.DaoDescription;
import com.ziroom.ferrari.repository.core.constant.OrmFrameEnum;
import com.ziroom.ferrari.repository.core.query.Criteria;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: J.T.
 * @Date: 2021/8/30 9:59
 * @Version 1.0
 */
@Repository
@DaoDescription(ormFrame = OrmFrameEnum.JDBC_TEMPLATE, settingBean = "masterOnlyJdbcSettings")
public class FerrariLockDao extends BaseDao<FerrariLockEntity> {


    public FerrariLockEntity findByUniqueKey(String lockKey) {
        if (StringUtils.isBlank(lockKey)) {
            return null;
        }
        return super.findOne(Criteria.where("uniqueKey", lockKey));
    }

    public int updateByVersion(FerrariLockEntity entity) {
        if (entity == null || entity.getId() == 0) {
            return 0;
        }
        String sql = "update ferrari_lock SET version = version+1,`locked`=?,expire_date=? where id = ? and version=? ";
        Map<String, Object> param = new HashMap<>(4);
        param.put("lock", entity.getLocked());
        param.put("expire_date", entity.getExpireDate());
        param.put("id", entity.getId());
        param.put("version", entity.getVersion());
        return super.updateBySql(sql, param);
    }
}
