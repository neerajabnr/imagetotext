package it.sella.f24.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import it.sella.f24.bean.Result;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameFinderMETokenFinder;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

@RestController
@RequestMapping("/train")
public class TrainandTestController {
	
	@RequestMapping(value = "/api/image/predict", method = RequestMethod.GET)
	public void test(@RequestParam("file") MultipartFile file,@RequestParam String instanceName) {
		
	}

}
