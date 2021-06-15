package com.wolterskluwer.searcherengine.endpoints.model;

import org.apache.solr.client.solrj.beans.Field;

public class Topic {

    @Field
    private String id;
    @Field
    private String label;
    @Field
    private String path;


    public Topic(String id, String label, String path) {
        this.id = id;
        this.label = label;
        this.path = path;
    }

    public Topic() {
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getPath() {
        return path;
    }

}
