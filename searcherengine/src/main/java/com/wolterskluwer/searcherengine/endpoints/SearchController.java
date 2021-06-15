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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@RestController
public class SearchController {

    @GetMapping(value = "/topics", params={"topic"})
    public List<Topic> topicsByLabel(@RequestParam(name="topic") String label, @RequestParam(name="limit" , defaultValue = "10") int limit) {
        SolrClient client;
        final String solrUrl = "http://localhost:8983/solr";
        client = new HttpSolrClient.Builder(solrUrl)
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();
        final SolrQuery query = new SolrQuery("label:*" + label + "*");
        query.setRows(limit);
        query.addField("id");
        query.addField("label");
        query.addField("path");
        query.setSort("path",SolrQuery.ORDER.asc);
        try {
            final QueryResponse response = client.query("topics", query);
            return response.getBeans(Topic.class);
        }catch (Exception e){
            throw  new RuntimeException(e);
        }
    }

    @GetMapping(value = "/topics", params={"topics"})
    public List<Topic> topicsById(@RequestParam(name="topics") List<String> ids) {
        SolrClient client;
        final String solrUrl = "http://localhost:8983/solr";
        client = new HttpSolrClient.Builder(solrUrl)
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();
        final SolrQuery query = new SolrQuery(buildORQuery(ids, "id"));
        query.addField("id");
        query.addField("label");
        query.addField("path");
        query.setSort("path",SolrQuery.ORDER.asc);
        try {
            final QueryResponse response = client.query("topics", query);
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
                    final SolrQuery query = new SolrQuery("*:*");
                    query.setRows(0);
                    query.setFacet(true);
                    query.addFacetField("topics");
                    String facetQuery = buildAndQuery(combination);
                    query.addFacetQuery(facetQuery);
                    final QueryResponse response = client.query("doctopics", query);
                    Map<String, Integer> resultFromQuery = response.getFacetQuery();
                    for(Map.Entry<String, Integer> entry : resultFromQuery.entrySet()){
                        result.add(new DocTopicSummary(combination,entry.getValue()));
                    }
                }
            }
            return result;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String buildAndQuery(Set<String> criteria){
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
            final SolrQuery query = new SolrQuery(buildORQuery(ids, "topics"));
            query.addField("id");
            query.addField("title");
            query.addField("topics");
            final QueryResponse response = client.query("doctopics", query);
            return response.getBeans(DocTopic.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/docs/{id}")
    public List<DocTopic> docs(@PathVariable String id) {
        SolrClient client;
        final String solrUrl = "http://localhost:8983/solr";
        client = new HttpSolrClient.Builder(solrUrl)
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();

        try {
            final SolrQuery query = new SolrQuery("id:" + id);
            query.addField("id");
            query.addField("title");
            query.addField("topics");
            final QueryResponse response = client.query("doctopics", query);
            return response.getBeans(DocTopic.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String buildORQuery(List<String> criteria, String field){
        if(criteria.isEmpty())
            return StringUtils.EMPTY;
        StringBuilder query = new StringBuilder(field + ":(");
        Iterator<String> it = criteria.iterator();
        query.append(it.next());
        while(it.hasNext()){
            query.append(" OR " + it.next());
        }
        query.append(")");
        return query.toString();
    }


    @GetMapping("/docs/suggest")
    public Long suggestTopicsFor(@RequestParam(name="topics") Set<String> ids) {
        SolrClient client;
        final String solrUrl = "http://localhost:8983/solr";
        client = new HttpSolrClient.Builder(solrUrl)
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();

        try {
            final SolrQuery query = new SolrQuery(buildAndQuery(ids));
            query.addField("id");
            query.addField("title");
            query.addField("topics");
            final QueryResponse response = client.query("doctopics", query);
            List<DocTopic> topics = response.getBeans(DocTopic.class);
            List<Long> topicIds = topics.stream().flatMap(topic -> topic.getTopics().stream())
                    .filter(id -> !ids.contains(String.valueOf(id)))
                    .collect(Collectors.toList());
            Optional<Map.Entry<Long,Long>> topic = topicIds.stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                    .entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue());
            if (topic.isPresent()){
                return topic.get().getKey();
            }
            return Long.MIN_VALUE;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
