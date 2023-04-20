package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.example.demo.pojo.Content;
import com.example.demo.pojo.User;
import com.example.demo.util.HtmlParseUtil;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
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
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class DemoApplicationTests {


    private static final String INDEX_TABLE = "idea_create_index";
    private static final String INDEX_TABLE_LS = "idea_ls";
    private static final String JD_GOODS_DATA = "jd_goods_data";
    @Autowired
    private RestHighLevelClient client;

    @Test
    void contextLoads() {
    }

    /**
     * 创建索引
     * idea_create_index 索引的库名
     */
    @Test
    void testElasticSearchCreateIndex() throws IOException {
        //创建索引请求
        CreateIndexRequest ideaCreateIndex = new CreateIndexRequest(INDEX_TABLE_LS);
        //执行创建请求
        CreateIndexResponse createIndexResponse = client.indices().create(ideaCreateIndex, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);
    }

    /**
     * 查询索引 是否存在
     */
    @Test
    void testElasticSearchQueryIndex() throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest(INDEX_TABLE);
        boolean exists = client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    /**
     * 删除索引
     */
    @Test
    void testElasticSearchDeleteIndex() throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(INDEX_TABLE_LS);
        AcknowledgedResponse acknowledgedResponse = client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        System.out.println(acknowledgedResponse.isAcknowledged());
    }

    /**
     * 添加文档
     */
    @Test
    void testElasticSearchAddIndex() throws IOException {
        //创建对象
        User user = new User("zs", 12);
        //创建请求
        IndexRequest request = new IndexRequest(INDEX_TABLE);
        request.id("1");
        request.timeout("1s");
        request.source(JSON.toJSONString(user), XContentType.JSON);
        IndexResponse index = client.index(request, RequestOptions.DEFAULT);
        System.out.println(index.toString());
        System.out.println(index.status());

    }

    /**
     * 查询文档是否存在， 获取文档内容
     *
     * @throws IOException
     */
    @Test
    void testIsExists() throws IOException {
        GetRequest getRequest = new GetRequest(INDEX_TABLE, "1");
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println("查询文档是否存在====" + exists);

        //重新设置
        getRequest.fetchSourceContext(new FetchSourceContext(true));
        GetResponse documentFields = client.get(getRequest, RequestOptions.DEFAULT);
        System.out.println("文档内容===" + documentFields.getSourceAsString());
        System.out.println("文档内容2===" + documentFields);
    }

    /**
     * 更新文档
     *
     * @throws IOException
     */
    @Test
    void testUpdate() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(INDEX_TABLE, "1");
        updateRequest.timeout("1s");
        User user = new User("张三", 12);
        updateRequest.doc(JSON.toJSONString(user), XContentType.JSON);
        UpdateResponse update = client.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(update.status());
    }

    /**
     * 删除文档
     *
     * @throws IOException
     */
    @Test
    void testDelete() throws IOException {
        DeleteRequest indexRequest = new DeleteRequest(INDEX_TABLE, "43");
        indexRequest.timeout("1s");
        DeleteResponse delete = client.delete(indexRequest, RequestOptions.DEFAULT);
        System.out.println(delete.status());
    }

    /**
     * 批量处理插入
     *
     * @throws IOException
     */

    @Test
    void testAddListRequest() throws IOException {

        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");

        ArrayList<User> users = new ArrayList<>();
        users.add(new User("zs1", 45));
        users.add(new User("wanger", 12));
        users.add(new User("mazi", 321));
        users.add(new User("xiaoming", 45));
        users.add(new User("xiaoyang", 53));
        users.add(new User("hk", 12));
        users.add(new User("ly", 42));

        for (int i = 0; i < users.size(); i++) {
            bulkRequest.add(new IndexRequest(INDEX_TABLE).id("" + (i + 1)).source(JSON.toJSONString(users.get(i)), XContentType.JSON));
        }
        BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulk.status());
        System.out.println(bulk.hasFailures());//false 代表插入成功、
    }

    /**
     * 搜索条件
     */
    @Test
    void testQuery() throws IOException {
        SearchRequest request = new SearchRequest(INDEX_TABLE);
        //构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //查询条件
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "ly");
        //查询所有
        //MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        request.source(searchSourceBuilder);

        SearchResponse search = client.search(request, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(search.getHits()));


        System.out.println("------------------------------");
        for (SearchHit hit : search.getHits()) {
            System.out.println(hit.getSourceAsMap());
        }


    }

    @Test
    void readJdGoodsData() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");
        List<Content> contents = HtmlParseUtil.getContent("java");
        for (int i = 0; i < contents.size(); i++) {
            bulkRequest.add(new IndexRequest(JD_GOODS_DATA).source(JSON.toJSONString(contents.get(i)), XContentType.JSON));
        }
        BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulk.status());
        System.out.println(bulk.hasFailures());//false 代表插入成功、

    }
}
