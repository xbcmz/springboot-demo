package com.luoyu.elasticsearch.service.impl;

import com.luoyu.elasticsearch.entity.User;
import com.luoyu.elasticsearch.service.ElasticSearchService;
import com.luoyu.elasticsearch.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * RabbitMqUtils
 * @author luoyu
 * @date 2019/03/16 22:08
 * @description
 */
@Slf4j
@Component
public class ElasticSearchServiceImpl implements ElasticSearchService {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    /**
     * ????????????
     * @param index
     */
    @Override
    public boolean createIndex(String index) throws Exception {
        // ????????????????????????
        if(this.existIndex(index)){
           return true;
        }
        // ????????????
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(index);
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        return createIndexResponse.isAcknowledged();
    }

    /**
     * ????????????????????????
     * @param index
     */
    @Override
    public boolean existIndex(String index) throws Exception {
        // ????????????????????????
        GetIndexRequest getIndexRequest = new GetIndexRequest(index);
        return restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
    }

    /**
     * ????????????
     * @param index
     */
    @Override
    public boolean deleteIndex(String index) throws Exception {
        // ????????????????????????
        if(!this.existIndex(index)){
            return true;
        }
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(index);
        AcknowledgedResponse acknowledgedResponse = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        return acknowledgedResponse.isAcknowledged();
    }

    /**
     * ????????????
     * @param index
     * @param id
     * @param content
     */
    @Override
    public boolean addDocument(String index, String id, String content) throws Exception {
        if(!this.createIndex(index)){
            return false;
        }

        IndexRequest indexRequest = new IndexRequest(index);
        // ??????????????????
        indexRequest.id(id);
        indexRequest.timeout(TimeValue.timeValueSeconds(1));
        // ?????????json?????????
        indexRequest.source(content, XContentType.JSON);
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        return indexResponse.status().getStatus() == 200;
    }

    /**
     * ????????????????????????
     * @param index
     * @param id
     */
    @Override
    public boolean isExistsDocument(String index, String id) throws Exception {
        // ????????????????????????
        GetRequest getRequest = new GetRequest(index, id);
        // ??????????????????_source????????????
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");
        return restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
    }

    /**
     * ????????????
     * @param index
     * @param id
     */
    @Override
    public String getDocument(String index, String id) throws Exception {
        // ????????????
        GetRequest getRequest = new GetRequest(index, id);
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        return getResponse.getSourceAsString();
    }

    /**
     * ????????????
     * @param index
     * @param id
     * @param content
     */
    @Override
    public boolean updateDocument(String index, String id, String content) throws Exception {
        // ????????????
        UpdateRequest updateRequest = new UpdateRequest(index, id);
        updateRequest.timeout(TimeValue.timeValueSeconds(1));
        updateRequest.doc(content, XContentType.JSON);
        UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        return updateResponse.status().getStatus() == 200;
    }

    /**
     * ????????????
     * @param index
     * @param id
     */
    @Override
    public boolean deleteDocument(String index, String id) throws Exception {
        if(!this.isExistsDocument(index, id)){
            return true;
        }

        // ????????????
        DeleteRequest deleteRequest = new DeleteRequest(index, id);
        deleteRequest.timeout(TimeValue.timeValueSeconds(1));
        DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        return deleteResponse.status().getStatus() == 200;
    }

    /**
     * ????????????
     * @param index
     * @param contents
     */
    @Override
    public boolean bulkRequest(String index, List<User> contents) throws Exception {
        // ????????????
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout(TimeValue.timeValueSeconds(1));
        contents.forEach(x -> {
            bulkRequest.add(
                    new IndexRequest(index)
                            .id(x.getId().toString())
                            .source(JsonUtils.objectToJson(x), XContentType.JSON));
        });
        BulkResponse bulkItemResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulkItemResponse.hasFailures();
    }

    /**
     * ????????????
     * @param index
     * @param keyword
     */
    @Override
    public List<Map<String, Object>> searchRequest(String index, String keyword) throws Exception {
        // ????????????
        SearchRequest searchRequest;
        if(StringUtils.isEmpty(index)){
            searchRequest = new SearchRequest();
        }else {
            searchRequest = new SearchRequest(index);
        }
        // ????????????
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // ?????????
        searchSourceBuilder.from(0);
        // ?????????????????????
        searchSourceBuilder.size(1000);
        // ????????????
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name").field("description");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);
        // ????????????
//        QueryBuilders.termQuery();
        // ????????????
//        QueryBuilders.matchAllQuery();
        // ?????????????????????ik_max_word????????????????????????ik_smart
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery(keyword,"name", "description").analyzer("ik_max_word"));
//        searchSourceBuilder.query(QueryBuilders.matchQuery("content", keyWord));
        searchSourceBuilder.timeout(TimeValue.timeValueSeconds(10));

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        List<Map<String, Object>> results = new ArrayList<>();
        for (SearchHit searchHit : searchResponse.getHits().getHits()){
            Map<String, HighlightField> highlightFieldMap = searchHit.getHighlightFields();
            HighlightField title = highlightFieldMap.get("name");
            HighlightField description = highlightFieldMap.get("description");
            // ???????????????
            Map<String, Object> sourceMap = searchHit.getSourceAsMap();
            // ?????????????????????????????????????????????
            if (title != null){
                Text[] fragments = title.getFragments();
                StringBuilder n_title = new StringBuilder();
                for (Text text : fragments){
                    n_title.append(text);
                }
                sourceMap.put("name", n_title.toString());
            }
            if (description != null){
                Text[] fragments = description.getFragments();
                StringBuilder n_description = new StringBuilder();
                for (Text text : fragments){
                    n_description.append(text);
                }
                sourceMap.put("description", n_description.toString());
            }
            results.add(sourceMap);
        }
        return results;
    }

    /**
     * ????????????id
     * @param index
     */
    @Override
    public List<Integer> searchAllRequest(String index) throws Exception {
        // ????????????
        SearchRequest searchRequest;
        if(StringUtils.isEmpty(index)){
            searchRequest = new SearchRequest();
        }else {
            searchRequest = new SearchRequest(index);
        }
        // ????????????
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // ?????????
        searchSourceBuilder.from(0);
        // ?????????????????????
        searchSourceBuilder.size(1000);
        // ????????????
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.timeout(TimeValue.timeValueSeconds(10));

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        List<Integer> results = new ArrayList<>();
        for (SearchHit searchHit : searchResponse.getHits().getHits()){
            results.add(Integer.valueOf(searchHit.getId()));
        }
        return results;
    }
}
