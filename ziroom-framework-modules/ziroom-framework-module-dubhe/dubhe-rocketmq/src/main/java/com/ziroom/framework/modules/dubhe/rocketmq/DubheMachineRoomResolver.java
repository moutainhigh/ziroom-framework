package com.ziroom.framework.modules.dubhe.rocketmq;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMachineRoomNearby;
import org.apache.rocketmq.common.message.MessageQueue;

public class DubheMachineRoomResolver implements AllocateMachineRoomNearby.MachineRoomResolver {

    @Override
    public String brokerDeployIn(MessageQueue messageQueue) {
        String brokerName = messageQueue.getBrokerName();
        String[] parts = StringUtils.split(brokerName, "-");
        return StringUtils.join(parts, "-", 0, parts.length - 1);
    }

    @Override
    public String consumerDeployIn(String clientID) {
        String[] parts = StringUtils.split(clientID, "@");
        // parts[0]: clientAddr
        // {ip}@{env}-{0/1}-{pid}-{seq}$${origin}
        String[] p2 = StringUtils.split(parts[1], "-");

        return StringUtils.join(p2, "-", 0, p2.length - 3);
    }

    public boolean consumerIsStable(String cid) {
        return consumerDeployIn(cid).equals("stable");
    }

}
