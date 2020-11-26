package com.gdut.secondhandmall.Util;

import com.alibaba.fastjson.JSON;
import com.gdut.secondhandmall.Bo.ProductEssentialsBO;
import com.gdut.secondhandmall.Dto.ProductEssentialsDTO;
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
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author GGXian
 * @project secondhandmall
 * @createTIme 2020/8/8-16:16
 * @description
 **/
@Component
public final class ESUtil<T> {
    public static final String PRODUCT_NAME = "productName";
    public static final String PRODUCT_DESCRIPTION = "description";
    public static final String PRODUCT_TAG = "secondaryDirectory";
    public static final String PRODUCT_TIME = "createTime";
    public static final String PRODUCT_PRICE = "price";


    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;
    /**
     * 判断索引是否存在
     * @param index
     * @return
     * @throws IOException
     */
    public boolean existsIndex(String index) throws IOException {
        GetIndexRequest request = new GetIndexRequest(index);
        boolean exists = client.indices().exists(request,
                RequestOptions.DEFAULT);
        return exists;
    }
    /**
     * 创建索引
     * @param index
     * @throws IOException
     */
    public boolean createIndex(String index) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(index);
//        Map<String, Object> jsonMap = new HashMap<>();
//        Map<String, Object> message = new HashMap<>();
//        Map<String, Object> properties = new HashMap<>();
//        message.put("productId", "keyword");
//        message.put("productName", "text");
//        message.put("description", "text");
//        message.put("price", "keyword");
//        message.put("picUrl", "keyword");
//        properties.put("message", message);
//        jsonMap.put("properties", properties);
//        request.mapping(jsonMap);
        CreateIndexResponse createIndexResponse
                =client.indices().create(request,RequestOptions.DEFAULT);
        return createIndexResponse.isAcknowledged();
    }

    /**
     *  创建映射规则
     * @param
     * @return
     * @throws IOException
     */
    public Map<String, Object> putMapping(CreateIndexRequest request) throws IOException {
        Map<String, Object> jsonMap = new HashMap<>();
        Map<String, Object> message = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();
        message.put("productId", "keyword");
        message.put("productName", "text");
        message.put("description", "text");
        message.put("price", "keyword");
        message.put("picUrl", "keyword");
        properties.put("message", message);
        jsonMap.put("properties", properties);
        return jsonMap;
//        request.source(jsonMap);
//        AcknowledgedResponse putMappingResponse = client.indices().putMapping(request, RequestOptions.DEFAULT);
//        return putMappingResponse.isAcknowledged();
    }
    /**
    * 删除索引
    * @param index
    * @return
    * @throws IOException
    */
    public boolean deleteIndex(String index) throws IOException {
        DeleteIndexRequest deleteIndexRequest = new
                DeleteIndexRequest(index);
        AcknowledgedResponse response =
                client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        return response.isAcknowledged();
    }
    /**
    * 判断某索引下文档id是否存在
    * @param index
    * @param id
    * @return
    *@throws IOException
    */
    public boolean docExists(String index, String id) throws IOException {
        GetRequest getRequest = new GetRequest(index,id);
        //只判断索引是否存在不需要获取_source
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");
        boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
        return exists;
    }
    /**
    * 添加文档记录
    * @param index
    * @param id
    * @param t 要添加的数据实体类
    * @return
    * @throws IOException
    */
    public boolean addDoc(String index,String id,T t) throws IOException {
        IndexRequest request = new IndexRequest(index);
        request.id(id);
        //timeout
        request.timeout(TimeValue.timeValueSeconds(1));
        request.timeout("1s");
        request.source(JSON.toJSONString(t), XContentType.JSON);
        IndexResponse indexResponse = client.index(request,
                RequestOptions.DEFAULT);
        RestStatus Status = indexResponse.status();
        return Status==RestStatus.OK||Status== RestStatus.CREATED;
    }
    /**
    * 根据id来获取记录
    * @param index
    * @param id
    * @return
    * @throws IOException
    */
    public GetResponse getDoc(String index, String id) throws IOException {
        GetRequest request = new GetRequest(index,id);
        GetResponse getResponse = client.get(request,
                RequestOptions.DEFAULT);
        return getResponse;
    }
    /**
    * 批量添加文档记录
    * 没有设置id ES会自动生成一个，如果要设置 IndexRequest的对象.id()即可
    * @param index
    * @param list
    * @return
    * @throws IOException
    */
    public boolean bulkAdd(String index, List<T> list) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        //timeout
        bulkRequest.timeout(TimeValue.timeValueMinutes(2));
        bulkRequest.timeout("2m");
        for (int i =0;i<list.size();i++){
            bulkRequest.add(new IndexRequest(index).id(String.valueOf(list.get(i)))
                    .source(JSON.toJSONString(list.get(i)), XContentType.JSON));
        }
        BulkResponse bulkResponse = client.bulk(bulkRequest,
                RequestOptions.DEFAULT);
        return !bulkResponse.hasFailures();
    }
    /**
     * 批量添加文档记录
     * 没有设置id ES会自动生成一个，如果要设置 IndexRequest的对象.id()即可
     * @param index
     * @param list
     * @return
     * @throws IOException
     */
    public boolean bulkAddForOnlineDTO(String index, List<ProductEssentialsDTO> list) throws IOException {
        List<ProductEssentialsBO> newList = transferAll(list);
        BulkRequest bulkRequest = new BulkRequest();
        //timeout
        bulkRequest.timeout(TimeValue.timeValueMinutes(2));
        bulkRequest.timeout("2m");
        for (int i =0;i<newList.size();i++){
            bulkRequest.add(new IndexRequest(index).id(String.valueOf(newList.get(i).getProductId()))
                    .source(JSON.toJSONString(newList.get(i)), XContentType.JSON));
        }
        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulkResponse.hasFailures();
    }


    /**
    * 批量删除和更新就不写了可根据上面几个方法来写
    */
    /**
    * 更新文档记录
    * @param index
    * @param id
    * @param t
    * @return
    * @throws IOException
    */
    public boolean updateDoc(String index,String id,T t) throws IOException {
        UpdateRequest request = new UpdateRequest(index,id);
        request.doc(JSON.toJSONString(t), XContentType.JSON);
        request.timeout(TimeValue.timeValueSeconds(1));
        request.timeout("1s");
        UpdateResponse updateResponse = client.update(
                request, RequestOptions.DEFAULT);
        return updateResponse.status()==RestStatus.OK;
    }
    /**
    * 删除文档记录
    * @param index
    * @param id
    * @return
    * @throws IOException
    */
    public boolean deleteDoc(String index,String id) throws IOException {
        DeleteRequest request = new DeleteRequest(index,id);
        //timeout
        request.timeout(TimeValue.timeValueSeconds(1));
        request.timeout("1s");
        DeleteResponse deleteResponse = client.delete(
                request, RequestOptions.DEFAULT);
        return deleteResponse.status()== RestStatus.OK;
    }
    /**
    * 根据某字段来搜索
    * @param index
    * @param field
    * @param key 要收搜的关键字
    * @throws IOException
    */
    public void search(String index,String field ,String key,Integer from,Integer size) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery(field, key));
        //控制搜素
        sourceBuilder.from(from);
        sourceBuilder.size(size);
        //最大搜索时间
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest,
                RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(searchResponse.getHits()));
    }

    /**
     * 根据某关键字来搜索商品名和商品描述
     * @param index
     * @param key
     * @param from
     * @param size
     * @throws IOException
     */
    public List<ProductEssentialsDTO> searchForNameAndDescription(String index, String key, String sort,
                                                                  String order, Integer from, Integer size) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.termQuery(PRODUCT_NAME, key));
        boolQueryBuilder.must(QueryBuilders.termQuery(PRODUCT_DESCRIPTION, key));
        sourceBuilder.query(boolQueryBuilder);
        //控制搜素
        sourceBuilder.from(from);
        sourceBuilder.size(size);
        //控制排序
        switch (sort) {
            case "time":
                if (order.equals("DESC")) {
                    sourceBuilder.sort(new FieldSortBuilder(PRODUCT_TIME).order(SortOrder.DESC));
                    break;
                }
                sourceBuilder.sort(new FieldSortBuilder(PRODUCT_TIME).order(SortOrder.ASC));
                break;
            case "price":
                if (order.equals("DESC")) {
                    sourceBuilder.sort(new FieldSortBuilder(PRODUCT_PRICE).order(SortOrder.DESC));
                    break;
                }
                sourceBuilder.sort(new FieldSortBuilder(PRODUCT_PRICE).order(SortOrder.ASC));
                break;
            default: ;
        }
        //最大搜索时间
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest,
                RequestOptions.DEFAULT);
        List<ProductEssentialsBO> sourceFromHit = getSourceFromHit(searchResponse.getHits());
        List<ProductEssentialsDTO> list = reverseAll(sourceFromHit);
        return list;
    }

    /**
     * 获取二级目录下的商品
     * @param index
     * @param
     * @param from
     * @param size
     * @return
     * @throws IOException
     */
    public List<ProductEssentialsDTO> searchByTag(String index, short tag, String sort,
                                                  String order, Integer from, Integer size) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery(PRODUCT_TAG, tag));
        //控制搜素

        sourceBuilder.from(from);
        sourceBuilder.size(size);
        //最大搜索时间
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest,
                RequestOptions.DEFAULT);
        List<ProductEssentialsBO> sourceFromHit = getSourceFromHit(searchResponse.getHits());
        List<ProductEssentialsDTO> list = reverseAll(sourceFromHit);
        return list;
    }

    /**
     * 批量将mysql中存储的商品信息模型转化为es中存储的商品信息模型
     * @param beforeList
     * @return
     */
    public List<ProductEssentialsBO> transferAll(List<ProductEssentialsDTO> beforeList){
        ArrayList<ProductEssentialsBO> afterList = new ArrayList<>();
        ProductEssentialsBO essentialsBO = null;
        System.out.println(beforeList);
        System.out.println(afterList);
        for (ProductEssentialsDTO essential : beforeList) {
            essentialsBO = new ProductEssentialsBO();
            essentialsBO.setProductId(essential.getProductId());
            essentialsBO.setProductName(essential.getProductName());
            essentialsBO.setSecondaryDirectory(essential.getSecondaryDirectory());
            essentialsBO.setDescription(essential.getDescription());
            essentialsBO.setPicUrl(essential.getPicUrl());
            essentialsBO.setPrice(essential.getPrice());
            essentialsBO.setCreateTime(Long.valueOf(TimeUtil.getTimeForEs(essential.getCreateTime())));
            afterList.add(essentialsBO);
            essentialsBO = null;
        }
        return afterList;
    }

    /**
     * 将mysql中存储的商品信息模型转化为es中存储的商品信息模型
     * @param essential
     * @return
     */
    private ProductEssentialsBO transfer(ProductEssentialsDTO essential){
        ProductEssentialsBO essentialsBO = new ProductEssentialsBO();
        essentialsBO.setProductId(essential.getProductId());
        essentialsBO.setProductName(essential.getProductName());
        essentialsBO.setSecondaryDirectory(essential.getSecondaryDirectory());
        essentialsBO.setDescription(essential.getDescription());
        essentialsBO.setPicUrl(essential.getPicUrl());
        essentialsBO.setPrice(essential.getPrice());
        essentialsBO.setCreateTime(Long.valueOf(TimeUtil.getTimeForEs(essential.getCreateTime())));
        return essentialsBO;
    }

    /**
     * 将es中存储的商品信息模型转化为mysql中存储的商品信息模型
     * @param essential
     * @return
     */
    private ProductEssentialsDTO reverse(ProductEssentialsBO essential){
        String year = null, month = null, day = null;
        String time = String.valueOf(essential.getCreateTime());
        ProductEssentialsDTO essentialsDTO = new ProductEssentialsDTO();
        year = time.substring(0, 4);
        month = time.substring(4, 6);
        day = time.substring(6);
        time = year.concat("-").concat(month).concat("-").concat(day);
        essentialsDTO.setProductId(essential.getProductId());
        essentialsDTO.setProductName(essential.getProductName());
        essentialsDTO.setSecondaryDirectory(essential.getSecondaryDirectory());
        essentialsDTO.setDescription(essential.getDescription());
        essentialsDTO.setPicUrl(essential.getPicUrl());
        essentialsDTO.setPrice(essential.getPrice());
        try {
            essentialsDTO.setCreateTime(TimeUtil.getTimeForEs(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return essentialsDTO;
    }

    /**
     * 批量将es中存储的商品信息模型转化为mysql中存储的商品信息模型
     * @param essentials
     * @return
     */
    private List<ProductEssentialsDTO> reverseAll(List<ProductEssentialsBO> essentials){
        List<ProductEssentialsDTO> list = new ArrayList<>();
        for (ProductEssentialsBO essential : essentials) {
            String year = null, month = null, day = null;
            String time = String.valueOf(essential.getCreateTime());
            ProductEssentialsDTO essentialsDTO = new ProductEssentialsDTO();
            year = time.substring(0, 4);
            month = time.substring(4, 6);
            day = time.substring(6);
            time = year.concat("-").concat(month).concat("-").concat(day);
            essentialsDTO.setProductId(essential.getProductId());
            essentialsDTO.setProductName(essential.getProductName());
            essentialsDTO.setSecondaryDirectory(essential.getSecondaryDirectory());
            essentialsDTO.setDescription(essential.getDescription());
            essentialsDTO.setPicUrl(essential.getPicUrl());
            essentialsDTO.setPrice(essential.getPrice());
            try {
                essentialsDTO.setCreateTime(TimeUtil.getTimeForEs(time));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            list.add(essentialsDTO);
        }
        return list;
    }

    /**
     * 将hits中的数据模型列表还原出来
     * @param hits
     * @return
     */
    private List<ProductEssentialsBO> getSourceFromHit(SearchHits hits){
        List<ProductEssentialsBO> list = new ArrayList<>();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            ProductEssentialsBO essentialsBO = new ProductEssentialsBO();
            essentialsBO.setProductId((Integer) sourceAsMap.get("productId"));
            essentialsBO.setProductName((String) sourceAsMap.get("productName"));
            essentialsBO.setDescription((String) sourceAsMap.get("description"));
            essentialsBO.setSecondaryDirectory((Short) sourceAsMap.get("secondaryDirectory"));
            essentialsBO.setCreateTime((Long) sourceAsMap.get("createTime"));
            essentialsBO.setPrice((Double) sourceAsMap.get("price"));
            essentialsBO.setPicUrl((String) sourceAsMap.get("picUrl"));
            list.add(essentialsBO);
        }
        return list;
    }
}
