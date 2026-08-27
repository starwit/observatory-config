package de.starwit.rest.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "${rest.base-path}/wolfsburg-roadworks")
public class WolfsburgRoadworksController {

    private static final String REMOTE_WMS_URL = "https://gisviewer.stadt.wolfsburg.de/default/ows/projects/gpt/baustellen";

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @GetMapping("/wms")
    public ResponseEntity<byte[]> proxyWms(@RequestParam MultiValueMap<String, String> params)
            throws IOException, InterruptedException {
        String queryString = params.entrySet().stream()
                .flatMap(entry -> toEncodedPairs(entry.getKey(), entry.getValue()).stream())
                .collect(Collectors.joining("&"));

        URI requestUri = URI.create(REMOTE_WMS_URL + (queryString.isEmpty() ? "" : "?" + queryString));

        HttpRequest request = HttpRequest.newBuilder(requestUri)
                .header("User-Agent", "observatory-config")
                .GET()
                .build();

        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        HttpHeaders headers = new HttpHeaders();
        response.headers().firstValue("content-type")
                .ifPresent(contentType -> headers.setContentType(MediaType.parseMediaType(contentType)));
        response.headers().firstValue("cache-control")
                .ifPresent(cacheControl -> headers.set(HttpHeaders.CACHE_CONTROL, cacheControl));

        return new ResponseEntity<>(response.body(), headers, HttpStatus.valueOf(response.statusCode()));
    }

    private List<String> toEncodedPairs(String key, List<String> values) {
        if (values == null || values.isEmpty()) {
            return List.of(encode(key) + "=");
        }

        return values.stream()
                .map(value -> encode(key) + "=" + encode(value))
                .collect(Collectors.toList());
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}