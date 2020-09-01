package com.ajd.tryout.javagraphqldataloaderdemo.graphql.component;

import com.ajd.tryout.javagraphqldataloaderdemo.graphql.GraphQLExecutor;
import com.ajd.tryout.javagraphqldataloaderdemo.graphql.GraphQLRequest;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import org.dataloader.DataLoaderRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.concurrent.CompletableFuture;

@Component
public class DefaultGraphQLExecutor implements GraphQLExecutor {

    @Autowired
    GraphQL graphQL;

    @Autowired(required = false)
    DataLoaderRegistry dataLoaderRegistry;


    @Override
    public CompletableFuture<ExecutionResult> execute(GraphQLRequest graphQLRequest, WebRequest webRequest) {

        ExecutionInput.Builder executionInputBuilder = ExecutionInput.newExecutionInput()
                .query(graphQLRequest.getQuery())
                .operationName(graphQLRequest.getOperationName())
                .variables(graphQLRequest.getVariables());

        if (dataLoaderRegistry != null) {
            executionInputBuilder.dataLoaderRegistry(dataLoaderRegistry);
        }

        ExecutionInput executionInput = executionInputBuilder.build();
        return CompletableFuture.completedFuture(executionInput).thenCompose(graphQL::executeAsync);
    }
}
