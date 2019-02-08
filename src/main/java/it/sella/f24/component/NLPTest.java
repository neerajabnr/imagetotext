package it.sella.f24.component;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.sella.f24.bean.Result;
import opennlp.tools.namefind.BioCodec;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameFinderMETokenFinder;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderFactory;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.MockInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;

public class NLPTest {
	
	public void testNLP(String instanceName) {String encoding = "ISO-8859-1";
	TokenNameFinderModel nameFinderModel;
	FileInputStream r;
	try {
		r = new FileInputStream("src/main/resources/f24_sec2part1model.bin");
	
		nameFinderModel = new TokenNameFinderModel(r);
		if (r != null) {
			nameFinderModel = new TokenNameFinderModel(r);
		} 
		r.close();
	} catch (IOException e) {
		System.out.println("Not a valid image");
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	//return nameFinderModel;}
	}

}
