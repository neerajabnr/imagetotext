package it.sella.f24.service;

import java.io.IOException;
import java.nio.charset.Charset;


import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;

import com.google.api.gax.rpc.NotFoundException;

@Component
public class RestTemplateResponseErrorHandler 
  implements ResponseErrorHandler {
 
    @Override
    public boolean hasError(ClientHttpResponse httpResponse) 
      throws IOException {
 
        return (
          httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR 
          || httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR);
    }
 
    @Override
    public void handleError(ClientHttpResponse httpResponse) 
      throws IOException {
 
        if (httpResponse.getStatusCode()
          .series() == HttpStatus.Series.SERVER_ERROR) {
            // handle SERVER_ERROR
        	//System.out.println("Response body: {}"+ StreamUtils.copyToString(httpResponse.getBody(), Charset.defaultCharset()));
        	System.out.println("stat text"+httpResponse.getStatusText());
        	System.out.println("stat code" +httpResponse.getStatusCode());
        	System.out.println("Raw stat code"+httpResponse.getRawStatusCode());
        	System.out.println("header"+httpResponse.getHeaders());
        	System.out.println("class" +httpResponse.getClass());
        } else if (httpResponse.getStatusCode()
          .series() == HttpStatus.Series.CLIENT_ERROR) {
            // handle CLIENT_ERROR
        	//System.out.println("Response body: {}"+ StreamUtils.copyToString(httpResponse.getBody(), Charset.defaultCharset()));
        	System.out.println("stat text"+httpResponse.getStatusText());
        	System.out.println("stat code" +httpResponse.getStatusCode());
        	System.out.println("Raw stat code"+httpResponse.getRawStatusCode());
        	System.out.println("header"+httpResponse.getHeaders());
        	System.out.println("class" +httpResponse.getClass());
        }
    }
}
