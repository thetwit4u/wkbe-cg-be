package com.wolterskluwer.searcherengine.endpoints;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.wolterskluwer.searcherengine.endpoints.model.DocTopic;
import com.wolterskluwer.searcherengine.endpoints.model.DocTopicSummary;
import com.wolterskluwer.searcherengine.endpoints.model.Topic;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.MapSolrParams;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


@RestController
public class SearchController {

    @GetMapping("/topics")
    public List<Topic> topics(@RequestParam(name="topic") String label) {
        SolrClient client;
        final String solrUrl = "http://localhost:8983/solr";
        client = new HttpSolrClient.Builder(solrUrl)
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();
        final Map<String, String> queryParamMap = new HashMap<>();
        queryParamMap.put("q", "label:" + label);
        MapSolrParams queryParams = new MapSolrParams(queryParamMap);
        try {
            final QueryResponse response = client.query("topics", queryParams);
            return response.getBeans(Topic.class);
        }catch (Exception e){
            throw  new RuntimeException(e);
        }
    }

    @GetMapping("/docs/summary")
    public Set<DocTopicSummary> docsSummaries(@RequestParam(name="topics") List<String> ids) {
        SolrClient client;
        final String solrUrl = "http://localhost:8983/solr";
        client = new HttpSolrClient.Builder(solrUrl)
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();

        try {
            Set<DocTopicSummary> result = new HashSet<>();
            for (int i = 0; i < ids.size(); ++i) {
                Set<Set<String>> combinations = Sets.combinations(ImmutableSet.copyOf(ids), i + 1);
                for(Set<String> combination : combinations) {
                    DocTopicSummary summary = new DocTopicSummary(combination);
                    final SolrQuery query = new SolrQuery("*:*");
                    query.setRows(0);
                    query.setFacet(true);
                    query.addFacetField("topics");
                    String facetQuery = buildFacetQuery(combination);
                    query.addFacetQuery(facetQuery);
                    final QueryResponse response = client.query("doctopics", query);
                    Map<String, Integer> resultFromQuery = response.getFacetQuery();
                    for(Map.Entry<String, Integer> entry : resultFromQuery.entrySet()){
                        summary.setCount(entry.getValue());
                    }
                    result.add(summary);
                }
            }
            return result;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String buildFacetQuery(Set<String> criteria){
        if(criteria.isEmpty())
            return StringUtils.EMPTY;
        StringBuilder query = new StringBuilder("topics:(");
        Iterator<String> it = criteria.iterator();
        query.append(it.next());
        while(it.hasNext()){
            query.append(" AND " + it.next());
        }
        query.append(")");
        return query.toString();
    }


    @GetMapping("/docs")
    public List<DocTopic> docs(@RequestParam(name="topics") List<String> ids) {
        SolrClient client;
        final String solrUrl = "http://localhost:8983/solr";
        client = new HttpSolrClient.Builder(solrUrl)
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();

        try {
            final SolrQuery query = new SolrQuery(buildORQuery(ids));
            query.addField("id");
            query.addField("title");
            query.addField("topics");
            final QueryResponse response = client.query("doctopics", query);
            return response.getBeans(DocTopic.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String buildORQuery(List<String> criteria){
        if(criteria.isEmpty())
            return StringUtils.EMPTY;
        StringBuilder query = new StringBuilder("topics:(");
        Iterator<String> it = criteria.iterator();
        query.append(it.next());
        while(it.hasNext()){
            query.append(" OR " + it.next());
        }
        query.append(")");
        return query.toString();
    }
}
