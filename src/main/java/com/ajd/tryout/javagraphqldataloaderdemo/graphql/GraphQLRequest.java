package com.ajd.tryout.javagraphqldataloaderdemo.graphql;

import java.util.Map;

public class GraphQLRequest {
    private String query;
    private String operationName;
    private Map<String, Object> variables;

    public GraphQLRequest() {}
    public GraphQLRequest(String query, String operationName, Map<String, Object> variables) {
        this.query = query;
        this.operationName = operationName;
        this.variables = variables;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }


    @Override
    public String toString() {
        return "GraphQLRequest{" +
                "query='" + query + '\'' +
                ", operationName='" + operationName + '\'' +
                ", variables=" + variables +
                '}';
    }
}