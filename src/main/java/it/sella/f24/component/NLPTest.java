package it.sella.f24.component;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
	
	private static TokenNameFinderModel tokenFinder = null;
	
	public TokenNameFinderModel tokenFinder(String instanceName) {String encoding = "ISO-8859-1";
	TokenNameFinderModel nameFinderModel =null;
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
	
	return nameFinderModel;
	}
	
	public List<Result> f24_Section1(String sentence) throws Exception {
	
			tokenFinder = tokenFinder("F24");
		String [] sentenceArray = this.tokenize(sentence);

		NameFinderME nameFinder = new NameFinderME(tokenFinder);

		List<Result> results = new ArrayList<>();
		Span[] names2 = nameFinder.find(sentenceArray);
		for (Span name : names2) {
			System.out.println(name);
			System.out.print(name.getType() + " ");

			for (int i = name.getStart(); i < name.getEnd(); i++) {

				results.add(new Result(name.getType(), sentenceArray[i]));

				System.out.print(sentenceArray[i] + " \n");


			}
		}
		return results;

	}
	
	 public String[] tokenize(String sentence) throws IOException{
	        InputStream inputStreamTokenizer = getClass().getResourceAsStream("/en-token.bin");
	        TokenizerModel tokenModel = new TokenizerModel(inputStreamTokenizer);
	        TokenizerME tokenizer = new TokenizerME(tokenModel);
	        return tokenizer.tokenize(sentence);
	    }

}
