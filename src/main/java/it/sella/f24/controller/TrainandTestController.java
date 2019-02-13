package it.sella.f24.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import it.sella.f24.bean.Data;
import it.sella.f24.bean.Result;
import it.sella.f24.component.FormatGoogleOCRData;
import it.sella.f24.service.GoogleService;
import it.sella.f24.service.NERImagePredictionService;


@RestController
@RequestMapping("/OCR")
public class TrainandTestController {
	
	@Autowired
	private NERImagePredictionService NERImagePredictionService;
	
	@Autowired
	private GoogleService googleOCRService;
	
	@Autowired
	private FormatGoogleOCRData formatGoogleOCRData;
	
	@RequestMapping(value = "/api/predict", method = RequestMethod.POST)
	public List<Result> test(@RequestParam("image") MultipartFile file,@RequestParam("instanceName") String instanceName) throws Exception {
		return NERImagePredictionService.predict(file, instanceName);
	}
	
	@RequestMapping(value = "/api/googleOCR", method = RequestMethod.POST)
	public String callGoogleOCR(@RequestParam("image") MultipartFile file) throws IOException {
		//System.out.println("Calling Skew Service for image skewing");
		byte[] decodeBase64 =file.getBytes();

		System.out.println("Calling Google Service for processing of the Image data");
		Data data = googleOCRService.readText(decodeBase64, "");
		String sentence = formatGoogleOCRData.getImageText(data);
		System.out.println("Google Data : "+sentence);
		return sentence;
	}
	
	@RequestMapping(value = "/api/googleOCR/new", method = RequestMethod.POST)
	public String callGoogleOCRnew(@RequestParam("image") MultipartFile file) throws IOException {
		//System.out.println("Calling Skew Service for image skewing");
		byte[] decodeBase64 =file.getBytes();

		System.out.println("Calling Google Service for processing of the Image data");
		Data data =  googleOCRService.readText_new(decodeBase64, "");
		String sentence = formatGoogleOCRData.getImageText(data);
		System.out.println("Google Data : "+sentence);
		return sentence;
	}

}
