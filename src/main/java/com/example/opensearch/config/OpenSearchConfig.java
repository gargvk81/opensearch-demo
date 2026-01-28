package com.example.opensearch.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenSearchConfig {

    @Value("${opensearch.host}")
    private String host;

    @Value("${opensearch.port}")
    private int port;

    @Value("${opensearch.scheme}")
    private String scheme;

    @Value("${opensearch.username}")
    private String username;

    @Value("${opensearch.password}")
    private String password;

    @Bean
    public OpenSearchClient openSearchClient() {
        // Create credentials provider
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
            AuthScope.ANY,
            new UsernamePasswordCredentials(username, password)
        );

        // Create REST client
        RestClient restClient = RestClient.builder(
            new HttpHost(host, port, scheme)
        ).setHttpClientConfigCallback(httpClientBuilder -> 
            httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
        ).build();

        // Create transport layer
        RestClientTransport transport = new RestClientTransport(
            restClient,
            new JacksonJsonpMapper()
        );

        // Create OpenSearch client
        return new OpenSearchClient(transport);
    }
}
