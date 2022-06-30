package com.demo.apiserver.message.repository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.demo.apiserver.message.domain.CommonDomain;

@Repository
public class ElasticSearchDao {
    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchDao.class);
    @Value("${es.index.name}")
    private String INDEX_HEAD;

    @Autowired
    RestHighLevelClient client;

    public void insertData(final List<CommonDomain> list) {
    	
        BulkRequest request  = new BulkRequest();
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        
        try {
        	for (CommonDomain common : list) {
                Date date = new Date();
                String timeStamp = sdf.format(date);
                Map<String, Object> map = mapper.convertValue(common, new TypeReference<Map<String,Object>>(){});
                map.put("@timestamp", timeStamp);
                map.put("timestampSeconds", date.getTime());
                
                IndexRequest indexRequest = null;
                SimpleDateFormat nowsdf = new SimpleDateFormat("yyyyMMdd");
    			indexRequest = new IndexRequest(INDEX_HEAD + nowsdf.format(new Date())).source(mapper.writeValueAsString(map), XContentType.JSON);
                request.add(indexRequest);
            }
        	
        	BulkResponse bulkResponse = client.bulk(request, RequestOptions.DEFAULT);
        	if(bulkResponse.hasFailures()) {
                for (BulkItemResponse bulkItemResponse : bulkResponse) {
                    if (bulkItemResponse.isFailed()) {
                        BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
                        logger.info("Error : " + failure.toString());
                    }
                }
        	} 
        } catch(Exception e) {
        	e.printStackTrace();
        	logger.info("insertData Exception : " + e.getMessage());
        }
    }
}
