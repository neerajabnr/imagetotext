package it.sella.f24.service;

import java.io.IOException;
import java.nio.charset.Charset;


import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;

import com.google.api.gax.rpc.NotFoundException;

import it.sella.f24.exception.handler.CustomServerException;
import it.sella.f24.exception.handler.ForbiddenException;

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
      throws IOException{
 
        if (httpResponse.getStatusCode()
          .series() == HttpStatus.Series.SERVER_ERROR) {
            // handle SERVER_ERROR
        	throw new CustomServerException();
        	
        } else if (httpResponse.getStatusCode()
          .series() == HttpStatus.Series.CLIENT_ERROR) {
           throw new ForbiddenException();
        }
    }
}
