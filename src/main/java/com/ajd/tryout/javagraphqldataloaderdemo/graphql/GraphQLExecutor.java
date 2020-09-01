package com.ajd.tryout.javagraphqldataloaderdemo.graphql;

import graphql.ExecutionResult;
import org.springframework.web.context.request.WebRequest;

import javax.jws.WebResult;
import java.util.concurrent.CompletableFuture;

public interface GraphQLExecutor {
    CompletableFuture<ExecutionResult> execute(GraphQLRequest graphQLRequest, WebRequest webRequest);
}
