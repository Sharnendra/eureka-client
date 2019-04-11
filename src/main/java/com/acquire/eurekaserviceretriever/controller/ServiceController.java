package com.acquire.eurekaserviceretriever.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ServiceController {
	
	
	@Autowired
    private DiscoveryClient discoveryClient;
 
    @RequestMapping("/service-instances/{applicationName}")
    public List<ServiceInstance> serviceInstancesByApplicationName(@PathVariable String applicationName) {
        return this.discoveryClient.getInstances("STUDENT-SERVICE");
    }
    
    
    @RequestMapping("/service-instances/dataGetter")
    public String datagetter(@RequestParam("name") String applicationName) {
        return applicationName;
    }
    
    @Autowired
    private LoadBalancerClient loadBalanced;
 
    @RequestMapping("/service-instances/get")
    public ResponseEntity<String> serviceInstancesByApplicationNameFromLoadBalancer() {
    	ServiceInstance serviceInstance=this.loadBalanced.choose("STUDENT-SERVICE");
    	String Uri=serviceInstance.getUri().toString();
    	Uri=Uri+"/getStudentDetailsForSchool/abcschool";
    	
    	RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response=null;
		try{
		response=restTemplate.exchange(Uri,
				HttpMethod.GET, getHeaders(),String.class);
		}catch (Exception ex)
		{
			System.out.println(ex);
		}
        return response;
    }
    
    
	@RequestMapping("/service-instances/get2")
    public @ResponseBody ResponseEntity<?> serviceInstances() {
		
		Boolean isException=false;
		
    	ServiceInstance serviceInstance=this.loadBalanced.choose("STUDENT-SERVICE");
    	String Uri=serviceInstance.getUri().toString();
    	Uri=Uri+"/product/bnschool";
    	
    	RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<?> response = null;
		System.out.println("Service Call ");
		try{
		response=restTemplate.exchange(Uri,
				HttpMethod.GET, getHeaders(),String.class);
		}catch (Exception ex)
		{
			System.err.println("Exception "+ex.getMessage());
			isException=true;
		}
        return isException==true?ResponseEntity.status(HttpStatus.NOT_FOUND).body("Data was not found"):response;
    }
	
	@RequestMapping("/service-instances/exception")
    public @ResponseBody ResponseEntity<?> serviceException() {
	
    	ServiceInstance serviceInstance=this.loadBalanced.choose("EXCEPTION-HANDLER");
    	String Uri=serviceInstance.getUri().toString();
    	Uri=Uri+"/error/404 notFound";
    	
    	RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<?> response = null;
		System.out.println("Service Call ");
		try{
		response=restTemplate.exchange(Uri,
				HttpMethod.GET, getHeaders(),Map.class);
		}catch (Exception ex)
		{
			System.err.println("Exception "+ex.getMessage());
		}
        return response;
    }
    
    private static org.springframework.http.HttpEntity<?> getHeaders() throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		return new org.springframework.http.HttpEntity<>(headers);
	}
    
}
