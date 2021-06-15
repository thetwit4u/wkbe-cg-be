package com.wolterskluwer.searcherengine.endpoints.model;

import org.apache.solr.client.solrj.beans.Field;

import java.util.List;

public class DocTopic {

    @Field
    private String id;
    @Field
    private String title;
    @Field
    private List<Long> topics;

    public DocTopic(){}

    public DocTopic(String id, String title, List<Long> topics) {
        this.id = id;
        this.title = title;
        this.topics = topics;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Long> getTopics() {
        return topics;
    }

    public void setTopics(List<Long> topics) {
        this.topics = topics;
    }
}
