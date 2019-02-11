package it.sella.f24.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.sella.f24.bean.Data;
import it.sella.f24.bean.Result;
import it.sella.f24.component.FormatGoogleOCRData;
import it.sella.f24.component.NLPTest;

@Service
public class NERService {
	
	@Autowired
	private NLPTest NLPTest;
	
	@Autowired
	private FormatGoogleOCRData formatGoogleOCRData;
	
	public List<Result> trainandTest(Data data, String instanceName) throws Exception {
		String sentence = formatGoogleOCRData.getImageText(data);
		List<Result> result = NLPTest.NERRecogniser(sentence, instanceName);
		return result;
	}

}
