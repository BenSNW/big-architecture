package hx.sboot;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;


public class Main {

	public static void main(String[] args) throws Exception {
		SimpleClientHttpRequestFactory cf = new SimpleClientHttpRequestFactory();
    	cf.setConnectTimeout(300);
    	cf.setReadTimeout(300000);
		RestTemplate client = new RestTemplate(cf);
        try {
        	String url = "http://music.163.com";
        	 ResponseEntity<String> response = client.getForEntity(url, String.class);
             System.out.println(response.getStatusCode() + " " + response.getBody());
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
       
        String url = "https://www.gitbook.com/download/pdf/book/jaceklaskowski/mastering-apache-spark";
        ClientHttpRequest request = client.getRequestFactory().createRequest(new URI(url), HttpMethod.GET);
        ClientHttpResponse response = request.execute();
        StreamUtils.copy(response.getBody(), new FileOutputStream("Mastering Apache Spark 2.0.pdf"));
        
        byte[] content = client.getForObject(url, byte[].class);
        StreamUtils.copy(new ByteArrayInputStream(content), new FileOutputStream("Mastering Apache Spark 2.0.pdf", false));
	}
}
