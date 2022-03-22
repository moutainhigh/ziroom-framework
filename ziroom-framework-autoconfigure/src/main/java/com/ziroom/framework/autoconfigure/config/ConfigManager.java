package com.ziroom.framework.autoconfigure.config;

import com.alibaba.nacos.api.exception.NacosException;
import com.google.common.collect.ImmutableMultimap;
import com.ziroom.gaea.gear.domain.AppAccount;
import com.ziroom.gaea.gear.domain.DatabaseMeta;

import java.util.List;

public interface ConfigManager {
    void initialize() throws NacosException;
    List<AppAccount> getAccount() throws NacosException;
    ImmutableMultimap<String, DatabaseMeta> getDatabase() throws NacosException;
    void listener(ConfigListener configListener);
}
