package com.demo.apiserver.message;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kafka")
public class KafkaSettings {

    private String zookeeperConnect;
    private String groupId;
    private int consumerThreadNum;
    private String autoCommitIntervalMs;
    private String fetchMessageMaxBytes;
    private List<String> topicList;

    public String getZookeeperConnect() {
        return zookeeperConnect;
    }

    public void setZookeeperConnect(String zookeeperConnect) {
        this.zookeeperConnect = zookeeperConnect;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getConsumerThreadNum() {
        return consumerThreadNum;
    }

    public void setConsumerThreadNum(int consumerThreadNum) {
        this.consumerThreadNum = consumerThreadNum;
    }

    public List<String> getTopicList() {
        return topicList;
    }

    public void setTopicList(List<String> topicList) {
        this.topicList = topicList;
    }

	public String getAutoCommitIntervalMs() {
		return autoCommitIntervalMs;
	}

	public void setAutoCommitIntervalMs(String autoCommitIntervalMs) {
		this.autoCommitIntervalMs = autoCommitIntervalMs;
	}

	public String getFetchMessageMaxBytes() {
		return fetchMessageMaxBytes;
	}

	public void setFetchMessageMaxBytes(String fetchMessageMaxBytes) {
		this.fetchMessageMaxBytes = fetchMessageMaxBytes;
	}

}
