package com.acquire.eurekaserviceretriever;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryFactory;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.PingUrl;
import com.netflix.loadbalancer.WeightedResponseTimeRule;

@SpringBootApplication
@RibbonClient(name="student-service", configuration=RibbonConfiguration.class)
public class EurekaServiceCapturerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaServiceCapturerApplication.class, args);
	}
}

class RibbonConfiguration {
	 
    @Autowired
    IClientConfig ribbonClientConfig;
 
    @Bean
    public IPing ribbonPing(IClientConfig config) {
        return new PingUrl();
    }
 
    @Bean
    public IRule ribbonRule(IClientConfig config) {
        return new WeightedResponseTimeRule();
    }
    //test
    @Bean
    LoadBalancedRetryFactory retryFactory() {
        return new LoadBalancedRetryFactory() {
            @Override
            public BackOffPolicy createBackOffPolicy(String service) {
                return new ExponentialBackOffPolicy();
            }
        };
    }
}
