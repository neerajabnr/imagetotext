package it.sella.f24.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.cloud.vision.v1.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.protobuf.ByteString;

import it.sella.f24.bean.BoundingPoly;
import it.sella.f24.bean.Data;
import it.sella.f24.bean.TextAnnotation;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.codehaus.jackson.annotate.JsonMethod;
import org.json.JSONArray;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

@Service
public class GoogleService {

	private static Logger logger = null;
	static {
		logger = Logger.getLogger(GoogleService.class);
		PropertyConfigurator.configure("src/main/resources/log4j.properties");
	}
	// private static final Gson gson = new
	// GsonBuilder().setPrettyPrinting().create();
	private static final Gson gson = new GsonBuilder().create();
	private Map<String, String> responseCache = new HashMap<String, String>();

	public static void main(String[] args) throws IOException {
		GoogleService service = new GoogleService();

		service.readText(new File("/home/bsindia/Documents/images/20181024_190928.jpg"), "");
	}

	public Data readText(File file, String hash) throws IOException {
		synchronized (hash) {
			if (responseCache.containsKey(hash)) {
				// return this.responseCache.get(hash);
			}

			System.out.println("Google OCR: processing file");
			//// logger.info("Google OCR: processing file");
			PrintStream out = System.out;
			List<AnnotateImageRequest> requests = new ArrayList<>();

			ByteString imgBytes = ByteString.readFrom(new FileInputStream(file));

			Image img = Image.newBuilder().setContent(imgBytes).build();
			Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
			AnnotateImageRequest.Builder request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img);

			if (ConfigProperties.getInstance().getLang() == "ENGLISH") {
				ImageContext imageContext = ImageContext.newBuilder().addLanguageHints("en").build();
				request.setImageContext(imageContext);
			} else if (ConfigProperties.getInstance().getLang() == "ITALIAN") {
				ImageContext imageContext = ImageContext.newBuilder().addLanguageHints("it").build();
				request.setImageContext(imageContext);
			}

			requests.add(request.build());

			try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
				BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
				// System.out.println(response);
				List<AnnotateImageResponse> responses = response.getResponsesList();
				client.close();
				System.out.println("responsef24");
//				System.out.println("Response from Google OCR:\n" + responses);
				//// logger.info("Response from Google OCR:\n"+responses);

				Data returnData = new Data();

				List<it.sella.f24.bean.TextAnnotation> textAnnotations = new ArrayList<>();

				for (AnnotateImageResponse res : responses) {
					if (res.hasError()) {
						out.printf("Error: %s\n", res.getError().getMessage());
						//// logger.info("Error in processing the image with Google OCR: %s\n"+
						//// res.getError().getMessage());
						throw new RuntimeException("Error Google OCR");
					}

					for (EntityAnnotation entityAnnotation : res.getTextAnnotationsList()) {
						it.sella.f24.bean.TextAnnotation textAnn = new it.sella.f24.bean.TextAnnotation();
						// System.out.println(entityAnnotation);
						textAnn.setDescription(entityAnnotation.getDescription());
						textAnn.setLocale(entityAnnotation.getLocale());
						BoundingPoly BondingPolyF24 = new BoundingPoly();
						List<it.sella.f24.bean.Vertex> vertexF24List = new ArrayList<>();
						List<Vertex> verticesList = entityAnnotation.getBoundingPoly().getVerticesList();
						for (Vertex vertex : verticesList) {
							it.sella.f24.bean.Vertex vertexF24 = new it.sella.f24.bean.Vertex();
							vertexF24.setX(vertex.getX());
							vertexF24.setY(vertex.getY());
							vertexF24List.add(vertexF24);
						}
						BondingPolyF24.setVertices(vertexF24List);
						textAnn.setBoundingPoly(BondingPolyF24);
						textAnnotations.add(textAnn);
					}

					// Map m=res.getAllFields();
					// System.out.println(m);
					// For full list of available annotations, see http://g.co/cloud/vision/docs
					// TextAnnotation annotation = res.getFullTextAnnotation();

					/*
					 * annotation.
					 */ // System.out.println("Google OCR: finished");
						// String responseToCache = gson.toJson(annotation);
						// System.out.println(res);
						// String section1 = fetchData(responseToCache,"CONTRIBUENTE","SEZIONE ERARIO");
						// Map<String, String> elementMap = new HashMap<>();
						// elementMap.put("FISCALE", fetchData(responseToCache, "FISCALE",
						// "\\\\ncognome"));
						// elementMap.put("FISCALE", fetchData(responseToCache, "DATI ANAGRAFICI",
						// "\\\\n"));
						// String COD = fetchData(responseToCache, "DATI ANAGRAFICI", "\\\\n");

					// System.out.println(COD);
					// System.out.println(responseToCache);
					// this.responseCache.put(hash, responseToCache);
					// return responseToCache;
				}
				returnData.setTextAnnotation(textAnnotations);
				// System.out.println("Data---"+returnData);
				return returnData;
			} catch (Exception e) {
				throw new RuntimeException("Error Google OCR: " + e.getMessage(), e);
			}
		}
	}

	public Data readText(byte[] decodeBase64, String hash) throws IOException {
		synchronized (hash) {
			if (responseCache.containsKey(hash)) {
				// return this.responseCache.get(hash);
			}

			System.out.println("Google OCR: processing file");
			//// logger.info("Google OCR: processing file");
			PrintStream out = System.out;
			List<AnnotateImageRequest> requests = new ArrayList<>();

			// ByteString imgBytes = ByteString.readFrom(new FileInputStream(decodeBase64));
			ByteString imgBytes = ByteString.copyFrom(decodeBase64);

			Image img = Image.newBuilder().setContent(imgBytes).build();

			// Saving the image

			// ByteArrayInputStream input_stream= new ByteArrayInputStream(decodeBase64);
			// BufferedImage final_buffered_image = ImageIO.read(input_stream);
			// ImageIO.write(final_buffered_image , "jpg", new File("D:\\Sample.jpg") );
			// System.out.println("Saved the image");
			// ImageIO.write(final_buffered_image , "jpg", new File("Sample.jpg") );

			Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
			AnnotateImageRequest.Builder request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img);

			if (ConfigProperties.getInstance().getLang() == "ENGLISH") {
				ImageContext imageContext = ImageContext.newBuilder().addLanguageHints("en").build();
				request.setImageContext(imageContext);
			} else if (ConfigProperties.getInstance().getLang() == "ITALIAN") {
				ImageContext imageContext = ImageContext.newBuilder().addLanguageHints("it").build();
				request.setImageContext(imageContext);
			}

			requests.add(request.build());

			try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
				BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
				// System.out.println(response);
				List<AnnotateImageResponse> responses = response.getResponsesList();
				client.close();
				//logger.info("responsef24"+responses);
//				System.out.println("responsef24"+responses);
				//// logger.info("Response data from google vision");
				//// logger.info(responses);

				Data returnData = new Data();

				List<it.sella.f24.bean.TextAnnotation> textAnnotations = new ArrayList<>();

				for (AnnotateImageResponse res : responses) {
					if (res.hasError()) {
						out.printf("Error: %s\n", res.getError().getMessage());
						//// logger.info("Error: %s\n"+res.getError().getMessage());
						//// logger.info("Error in Google OCR");
						throw new RuntimeException("Error Google OCR");
					}

					for (EntityAnnotation entityAnnotation : res.getTextAnnotationsList()) {
						it.sella.f24.bean.TextAnnotation textAnn = new it.sella.f24.bean.TextAnnotation();
						// System.out.println(entityAnnotation);
						textAnn.setDescription(entityAnnotation.getDescription());
						textAnn.setLocale(entityAnnotation.getLocale());
						BoundingPoly BondingPolyF24 = new BoundingPoly();
						List<it.sella.f24.bean.Vertex> vertexF24List = new ArrayList<>();
						List<Vertex> verticesList = entityAnnotation.getBoundingPoly().getVerticesList();
						for (Vertex vertex : verticesList) {
							it.sella.f24.bean.Vertex vertexF24 = new it.sella.f24.bean.Vertex();
							vertexF24.setX(vertex.getX());
							vertexF24.setY(vertex.getY());
							vertexF24List.add(vertexF24);
						}
						BondingPolyF24.setVertices(vertexF24List);
						textAnn.setBoundingPoly(BondingPolyF24);
						textAnnotations.add(textAnn);
					}

					// Map m=res.getAllFields();
					// System.out.println(m);
					// For full list of available annotations, see http://g.co/cloud/vision/docs
					// TextAnnotation annotation = res.getFullTextAnnotation();

					/*
					 * annotation.
					 */ // System.out.println("Google OCR: finished");
						// String responseToCache = gson.toJson(annotation);
						// System.out.println(res);
						// String section1 = fetchData(responseToCache,"CONTRIBUENTE","SEZIONE ERARIO");
						// Map<String, String> elementMap = new HashMap<>();
						// elementMap.put("FISCALE", fetchData(responseToCache, "FISCALE",
						// "\\\\ncognome"));
						// elementMap.put("FISCALE", fetchData(responseToCache, "DATI ANAGRAFICI",
						// "\\\\n"));
						// String COD = fetchData(responseToCache, "DATI ANAGRAFICI", "\\\\n");

					// System.out.println(COD);
					// System.out.println(responseToCache);
					// this.responseCache.put(hash, responseToCache);
					// return responseToCache;
				}
				returnData.setTextAnnotation(textAnnotations);
				//System.out.println(returnData);
				return returnData;
			} catch (Exception e) {
				throw new RuntimeException("Error Google OCR: " + e.getMessage(), e);
			}
		}
	}

	private String fetchData(String responseToCache, String string1, String string2) {
		Pattern pattern = Pattern.compile(string1 + "(.*?)" + string2);
		Matcher m = pattern.matcher(responseToCache);
		String section1 = "";
		while (m.find())
			section1 = m.group(1);
		return section1;
	}

	public Data readText(MultipartFile file, String hash) {
		synchronized (hash) {
			if (responseCache.containsKey(hash)) {
				// return this.responseCache.get(hash);
			}

			System.out.println("Google OCR: processing file");
			//// logger.info("Google OCR: processing file");
			PrintStream out = System.out;
			List<AnnotateImageRequest> requests = new ArrayList<>();

			ByteString imgBytes = null;
			try {
				imgBytes = ByteString.readFrom(file.getInputStream());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			Image img = Image.newBuilder().setContent(imgBytes).build();
			Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
			AnnotateImageRequest.Builder request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img);

			if (ConfigProperties.getInstance().getLang() == "ENGLISH") {
				ImageContext imageContext = ImageContext.newBuilder().addLanguageHints("en").build();
				request.setImageContext(imageContext);
			} else if (ConfigProperties.getInstance().getLang() == "ITALIAN") {
				ImageContext imageContext = ImageContext.newBuilder().addLanguageHints("it").build();
				request.setImageContext(imageContext);
			}

			requests.add(request.build());

			try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
				BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
				// System.out.println("flag");
				// System.out.println(response.getInitializationErrorString());

				List<AnnotateImageResponse> responses = response.getResponsesList();
				client.close();

				//// logger.info("Response from google ocr:\n"+responses);
				//System.out.println("Response from google ocr:\n" + responses);
				Data returnData = new Data();

				List<it.sella.f24.bean.TextAnnotation> textAnnotations = new ArrayList<>();

				for (AnnotateImageResponse res : responses) {
					if (res.hasError()) {
						out.printf("Error: %s\n", res.getError().getMessage());
						//// logger.info("Error: %s\n"+ res.getError().getMessage());
						throw new RuntimeException("Error Google OCR");
					}
					//System.out.println("resp"+res.getTextAnnotationsList());
					//logger.info("resp"+res.getTextAnnotationsList());

					for (EntityAnnotation entityAnnotation : res.getTextAnnotationsList()) {
						it.sella.f24.bean.TextAnnotation textAnn = new it.sella.f24.bean.TextAnnotation();
						// System.out.println(entityAnnotation);
						textAnn.setDescription(entityAnnotation.getDescription());
						textAnn.setLocale(entityAnnotation.getLocale());
						BoundingPoly BondingPolyF24 = new BoundingPoly();
						List<it.sella.f24.bean.Vertex> vertexF24List = new ArrayList<>();
						List<Vertex> verticesList = entityAnnotation.getBoundingPoly().getVerticesList();
						for (Vertex vertex : verticesList) {
							it.sella.f24.bean.Vertex vertexF24 = new it.sella.f24.bean.Vertex();
							vertexF24.setX(vertex.getX());
							vertexF24.setY(vertex.getY());
							vertexF24List.add(vertexF24);
						}
						BondingPolyF24.setVertices(vertexF24List);
						textAnn.setBoundingPoly(BondingPolyF24);
						textAnnotations.add(textAnn);
					}

					// Map m=res.getAllFields();
					// System.out.println(m);
					// For full list of available annotations, see http://g.co/cloud/vision/docs

					/*
					 * TextAnnotation annotation = res.getFullTextAnnotation();
					 * 
					 * System.out.println("Google OCR: finished"); String responseToCache =
					 * gson.toJson(annotation); System.out.println(responseToCache); String section1
					 * = fetchData(responseToCache,"CONTRIBUENTE","SEZIONE ERARIO"); Map<String,
					 * String> elementMap = new HashMap<>(); elementMap.put("FISCALE",
					 * fetchData(responseToCache, "FISCALE", "\\\\ncognome"));
					 * elementMap.put("FISCALE", fetchData(responseToCache, "DATI ANAGRAFICI",
					 * "\\\\n")); String COD = fetchData(responseToCache, "DATI ANAGRAFICI",
					 * "\\\\n");
					 * 
					 * System.out.println(COD); //System.out.println(responseToCache);
					 * this.responseCache.put(hash, responseToCache);
					 */
					returnData.setTextAnnotation(textAnnotations);
					// System.out.println("Data-=-=-"+returnData);
					return returnData;
				}
			} catch (Exception e) {
				throw new RuntimeException("Error Google OCR: " + e.getMessage(), e);
			}
			return null;
		}
	}
}