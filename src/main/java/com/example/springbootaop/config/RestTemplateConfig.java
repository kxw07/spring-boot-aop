package com.example.springbootaop.config;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@Configuration
public class RestTemplateConfig {
    @Bean("restCustomizer")
    public RestTemplateCustomizer customRestTemplateCustomizer(ClientHttpRequestFactory clientHttpRequestFactory) {
        return restTemplate -> {
            restTemplate.setRequestFactory(clientHttpRequestFactory);
        };
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(CloseableHttpClient closeableHttpClient) {
        return new HttpComponentsClientHttpRequestFactory(closeableHttpClient);
    }

    @Bean
    public CloseableHttpClient proxyHttpClient(RequestConfig requestConfig, PoolingHttpClientConnectionManager connectionManager, ConnectionKeepAliveStrategy keepAliveStrategy) {
        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .setKeepAliveStrategy(keepAliveStrategy)
                .build();
    }

    @Bean
    public RequestConfig requestConfig() {
        return RequestConfig
                .custom()
                .setConnectionRequestTimeout(3000)
                .setSocketTimeout(3000)
                .setConnectTimeout(3000)
                .build();
    }

    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(500);
        connectionManager.setDefaultMaxPerRoute(500);

        return connectionManager;
    }

    @Bean
    public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
        return (response, context) -> {
            HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();

                if (value != null && param.equalsIgnoreCase("timeout")) {
                    return Long.parseLong(value) * 1000;
                }
            }

            return 20 * 1000L;
        };
    }
}
