package com.livehealth.shared.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

@ApplicationScoped
public class EnvConfig {
    static {
        // Load .env file into system properties (ignored if missing)
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")
                    .ignoreIfMissing()
                    .load();
            dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        } catch (Exception e) {
            // Ignore if dotenv is not available
        }
    }
}
