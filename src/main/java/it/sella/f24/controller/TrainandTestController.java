package it.sella.f24.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import it.sella.f24.bean.Result;
import it.sella.f24.service.NERImagePredictionService;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameFinderMETokenFinder;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

@RestController
@RequestMapping("/train")
public class TrainandTestController {
	
	@Autowired
	private NERImagePredictionService NERImagePredictionService;
	
	@RequestMapping(value = "/api/image/predict", method = RequestMethod.POST)
	public List<Result> test(@RequestParam("image") MultipartFile file,@RequestParam("instanceName") String instanceName) throws Exception {
		return NERImagePredictionService.predict(file, instanceName);
	}

}
