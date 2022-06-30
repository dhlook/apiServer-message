package com.demo.apiserver.message.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.demo.apiserver.message.domain.BaseDomain;
import com.demo.apiserver.message.domain.CommonDomain;
import com.demo.apiserver.message.repository.ElasticSearchDao;

@Service
public class TopicService {
	private static final Logger logger = LoggerFactory.getLogger(TopicService.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private ElasticSearchDao esDao;
    
    @PostConstruct
    public void init() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(Include.NON_NULL);
    }

    public void messageList(List<BaseDomain> list) {
    	try {
    		List<CommonDomain> commonList = new ArrayList<CommonDomain>();
            for (BaseDomain domain : list) {
            	CommonDomain type = mapper.readerFor(CommonDomain.class).readValue(domain.getPayload());
            	commonList.add(type);
            }
            esDao.insertData(commonList);
        } catch (JsonProcessingException e) {
            logger.error("JsonProcessingException :" + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            logger.error("Exception : " + e.toString());
            e.printStackTrace();
        }
    }
}
