package com.ajd.tryout.javagraphqldataloaderdemo.controller;

import com.ajd.tryout.javagraphqldataloaderdemo.graphql.JsonSerializer;
import com.ajd.tryout.javagraphqldataloaderdemo.graphql.GraphQLExecutor;
import com.ajd.tryout.javagraphqldataloaderdemo.graphql.GraphQLRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
public class GraphQLController {

    static String APPLICATION_GRAPHQL_VALUE = "application/graphql";
    static MediaType APPLICATION_GRAPHQL = MediaType.parseMediaType(APPLICATION_GRAPHQL_VALUE);

    @Autowired
    private JsonSerializer jsonSerializer;

    @Autowired
    GraphQLExecutor graphQLExecutor;


    @RequestMapping(
            value = "${graphql.url:graphql}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Object ExecuteGraphQLRequest(
        @RequestHeader(value = HttpHeaders.CONTENT_TYPE, required = false) String contentType,
        @RequestParam(value = "query", required = false) String query,
        @RequestParam(value = "operationName", required = false) String operationName,
        @RequestParam(value = "variables", required = false) String variablesJson,
        @RequestBody(required = false) String body,
        WebRequest webRequest
    ) {

        MediaType mediaType = null;
        if (!StringUtils.isEmpty(contentType)) {
            try {
                mediaType = MediaType.parseMediaType(contentType);
            } catch (InvalidMediaTypeException ignore) {
            }
        }


        if (body == null) {
            body = "";
        }


        if (MediaType.APPLICATION_JSON.equalsTypeAndSubtype(mediaType)) {
            GraphQLRequest graphQLRequest = jsonSerializer.deserialize(body, GraphQLRequest.class);
            if (graphQLRequest.getQuery() == null) {
                graphQLRequest.setQuery("");
            }

            if (graphQLRequest.getVariables() == null) {
                graphQLRequest.setVariables(new HashMap<>());
            }

            return executeGraphQLRequest(graphQLRequest, webRequest);
        }

        if (query != null) {
            GraphQLRequest graphQLRequest = new GraphQLRequest(query, operationName, jsonSerializer.deserialize(variablesJson, Map.class));
            return executeGraphQLRequest(graphQLRequest, webRequest);
        }

        if (APPLICATION_GRAPHQL.equalsTypeAndSubtype(mediaType)) {
            GraphQLRequest graphQLRequest = new GraphQLRequest(body, "", new HashMap<>());
            return executeGraphQLRequest(graphQLRequest, webRequest);
        }

        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Could not process GraphQL request");
    }

    private Object executeGraphQLRequest(GraphQLRequest graphQLRequest, WebRequest webRequest) {
        return graphQLExecutor.execute(graphQLRequest, webRequest);
    }
}
