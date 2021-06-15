package com.wolterskluwer.searcherengine.endpoints.model;

import java.util.Objects;
import java.util.Set;

public class DocTopicSummary {

    private Set<String> ids;
    private int count;

    public DocTopicSummary(Set<String> ids, int count) {
        this.ids = ids;
        this.count = count;
    }

    public Set<String> getIds() {
        return ids;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocTopicSummary that = (DocTopicSummary) o;
        return count == that.count && Objects.equals(ids, that.ids);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ids, count);
    }
}
