package com.example.configuration;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import com.couchbase.client.java.env.ClusterEnvironment;

@Configuration
public class CouchbaseVPNCheckConfig {

    @Value("${spring.couchbase.connection-string}")
    private String connectionString;

    @Value("${spring.couchbase.username}")
    private String username;

    @Value("${spring.couchbase.password}")
    private String password;

    @PostConstruct
    public void checkCouchbaseConnection() {
        try {
            ClusterEnvironment environment = ClusterEnvironment.builder().build();
            Cluster cluster = Cluster.connect(connectionString,
                    ClusterOptions.clusterOptions(username, password)
                            .environment(environment));

            cluster.ping(); // Actively test connection

        } catch (Exception e) {
            System.err.println("❌ Couchbase not reachable. General error.");
            throw new RuntimeException("Couchbase connection failed", e);
        }
    }
}
