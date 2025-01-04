package com.polarbookshop.order_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
	
	SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
		http.authorizeExchange(exchanges -> exchanges
				.pathMatchers("/actuator/**").permitAll()
				.anyExchange().authenticated())
			.oauth2ResourceServer(oauth2 -> oauth2
				.jwt(Customizer.withDefaults()))
			.requestCache(requestCacheSpec-> requestCacheSpec
				.requestCache(NoOpServerRequestCache.getInstance()))
			.csrf(csrf -> csrf
				.disable());
		    
		return http.build();
	}

}
