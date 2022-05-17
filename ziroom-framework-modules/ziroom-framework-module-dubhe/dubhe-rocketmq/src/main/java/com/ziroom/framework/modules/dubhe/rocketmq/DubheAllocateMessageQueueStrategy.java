package com.ziroom.framework.modules.dubhe.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.AllocateMessageQueueStrategy;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
public class DubheAllocateMessageQueueStrategy implements AllocateMessageQueueStrategy {

    private DubheMachineRoomResolver machineRoomResolver = new DubheMachineRoomResolver();
    private AllocateMessageQueueStrategy allocateMessageQueueStrategy = new AllocateMessageQueueAveragely();

    String env = System.getenv("APPLICATION_ENV");

    @Override
    public List<MessageQueue> allocate(String consumerGroup, String currentCID, List<MessageQueue> mqAll, List<String> cidAll) {
        log.info("alloc. consumerGroup={} currentCID={} mqAll={} cidAll={}", consumerGroup, currentCID, mqAll, cidAll);
        // 根据messageQuery中brokerName的环境标识分组
        Map<String/*machine zone */, List<MessageQueue>> mr2Mq = new TreeMap<>();
        for (MessageQueue mq : mqAll) {
            String brokerMachineZone = machineRoomResolver.brokerDeployIn(mq);
            mr2Mq.putIfAbsent(brokerMachineZone, new ArrayList<>());
            mr2Mq.get(brokerMachineZone).add(mq);
        }

        // 根据clientId的环境标识分组
        Map<String/*machine zone */, List<String/*clientId*/>> mr2c = new TreeMap<>();
        // 基准环境的clientId
        List<String> benchmarkClientIds = new ArrayList<>();
        for (String cid : cidAll) {
            String consumerMachineZone = machineRoomResolver.consumerDeployIn(cid);
            mr2c.putIfAbsent(consumerMachineZone, new ArrayList<>());
            mr2c.get(consumerMachineZone).add(cid);
            if (consumerMachineZone.equals("stable")) {
                benchmarkClientIds.add(cid);
            }
        }

        List<MessageQueue> allocateResults = new ArrayList<>();
        // 1、匹配同机房的队列
        String currentMachineZone = machineRoomResolver.consumerDeployIn(currentCID);
        List<MessageQueue> mqInThisMachineZone = mr2Mq.remove(currentMachineZone);
        List<String> consumerInThisMachineZone = mr2c.get(currentMachineZone);
        if (mqInThisMachineZone != null && !mqInThisMachineZone.isEmpty()) {
            allocateResults.addAll(allocateMessageQueueStrategy.allocate(consumerGroup, currentCID, mqInThisMachineZone, consumerInThisMachineZone));
        }

        // 寻找没有匹配上zone的MessageQueueList
        for (String machineZone : mr2Mq.keySet()) {
            if (mr2c.containsKey(machineZone)) {
                continue;
            }
            // 2、如果存在基准环境consumer，则把没有消费者的messageQueue分配给基准环境
            if (!benchmarkClientIds.isEmpty()) {
                if (machineRoomResolver.consumerIsStable(currentCID)) {
                    allocateResults.addAll(allocateMessageQueueStrategy.allocate(consumerGroup, currentCID, mr2Mq.get(machineZone), benchmarkClientIds));
                }
            } else {
                // 3、如果没有基准环境，则没有消费者的messageQueue再次分配给consumer
                allocateResults.addAll(allocateMessageQueueStrategy.allocate(consumerGroup, currentCID, mr2Mq.get(machineZone), cidAll));
            }
        }

        log.info("Allocate queues. cid={} queues={}", currentCID, allocateResults);
        return allocateResults;
    }

    @Override
    public String getName() {
        return "DubheAllocateMessageQueueStrategy";
    }
}
