package com.ajd.tryout.javagraphqldataloaderdemo.graphql;

public interface JsonSerializer {
    public String serialize(Object object);
    public <T> T deserialize(String json, Class<T> requiredType);
}
