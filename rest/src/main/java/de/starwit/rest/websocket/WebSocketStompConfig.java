package de.starwit.rest.websocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {

  // Origins that are allowed to open a websocket connection, e.g. https://observatory.example.com.
  // If empty, Spring falls back to its same-origin check, which compares scheme/host/port as seen
  // by the container. That check fails behind a TLS-terminating reverse proxy (the proxy forwards
  // plain http, and traefik even sets X-Forwarded-Proto: wss), so deployments behind an ingress
  // have to name their external origin explicitly.
  @Value("${websocket.allowedOrigins:}")
  private String[] allowedOrigins;

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic");
    config.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    StompWebSocketEndpointRegistration endpoint = registry.addEndpoint("/location-websocket");
    if (allowedOrigins.length > 0) {
      endpoint.setAllowedOrigins(allowedOrigins);
    }
  }
}
