package it.sella.f24.service;



import java.io.File;
//// This sample uses the Apache HTTP client from HTTP Components (http://hc.apache.org/httpcomponents-client-ga/)
import java.net.URI;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class MicrosoftService 
{
 public static void main(String[] args) 
 {
     HttpClient httpclient = HttpClients.createDefault();

     try
     {
         URIBuilder builder = new URIBuilder("https://westeurope.api.cognitive.microsoft.com/vision/v2.0/ocr");

         builder.setParameter("language", "it");
         builder.setParameter("detectOrientation", "true");

         URI uri = builder.build();
         HttpPost request = new HttpPost(uri);
         request.setHeader("Content-Type", "application/octet-stream");
         request.setHeader("Ocp-Apim-Subscription-Key", "key");


         // Request body
         //https://blog.optimizely.com/wp-content/uploads/2016/01/shutterstock.jpg
         //StringEntity reqEntity = new StringEntity("{\"url\" : \"https://homepages.cae.wisc.edu/~ece533/images/airplane.png\"}");
         File file = new File("1.jpg");
        FileEntity reqEntity = new FileEntity(file,ContentType.APPLICATION_OCTET_STREAM);
         request.setEntity(reqEntity);

         HttpResponse response = httpclient.execute(request);
         HttpEntity entity = response.getEntity();

         if (entity != null) 
         {
             System.out.println(EntityUtils.toString(entity));
         }
     }
     catch (Exception e)
     {
         System.out.println(e.getMessage());
     }
 }
}


