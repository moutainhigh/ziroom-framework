package com.ziroom.framework.autoconfigure.jdbc;


import org.apache.shardingsphere.api.config.masterslave.LoadBalanceStrategyConfiguration;
import org.apache.shardingsphere.api.config.masterslave.MasterSlaveRuleConfiguration;

import java.util.List;

public class RuleConfigureBuilder {

    public static MasterSlaveRuleConfiguration buildMasterSlaveRuleConfiguration(String name,
                                                                                 String masterDataSourceName,
                                                                                 List<String> slaveDataSourceNames) {
        if (GearContext.masterSlaveRuleConfiguration != null && name.equalsIgnoreCase(GearContext.masterSlaveRuleConfiguration.getName())) {
            return GearContext.masterSlaveRuleConfiguration;
        }

        MasterSlaveRuleConfiguration m = new MasterSlaveRuleConfiguration(name, masterDataSourceName, slaveDataSourceNames, new LoadBalanceStrategyConfiguration("round_robin"));

        return m;
    }
}
