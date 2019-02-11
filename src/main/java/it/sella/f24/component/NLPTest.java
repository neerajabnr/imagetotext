package it.sella.f24.component;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import it.sella.f24.bean.Result;
import opennlp.tools.formats.ResourceAsStreamFactory;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.TokenSample;
import opennlp.tools.tokenize.TokenSampleStream;
import opennlp.tools.tokenize.TokenizerFactory;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;


@Component
public class NLPTest {
	
	private static TokenNameFinderModel tokenFinder = null;
	
	public TokenNameFinderModel tokenFinder(String instanceName) {String encoding = "ISO-8859-1";
	TokenNameFinderModel nameFinderModel =null;
	FileInputStream r;
	try {
		r = new FileInputStream("src/main/resources/f24_sec1model.bin");
	
		//nameFinderModel = new TokenNameFinderModel(r);
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
	
	public List<Result> NERRecogniser(String sentence,String instanceName) throws Exception {
	
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
	
	 private String[] tokenize(String sentence) throws IOException{
	       // InputStream inputStreamTokenizer = getClass().getResourceAsStream("/en-token.bin");
	        TokenizerModel tokenModel = createMaxentTokenModel();
	        TokenizerME tokenizer = new TokenizerME(tokenModel);
	        return tokenizer.tokenize(sentence);
	    }
	 
	 static TokenizerModel createMaxentTokenModel() throws IOException {

			InputStreamFactory trainDataIn = new ResourceAsStreamFactory(TokenizerModel.class,
					"/opennlp/tools/tokenize/token.train");

			ObjectStream<TokenSample> samples = new TokenSampleStream(
					new PlainTextByLineStream(trainDataIn, StandardCharsets.UTF_8));

			TrainingParameters mlParams = new TrainingParameters();
			mlParams.put(TrainingParameters.ITERATIONS_PARAM, 100);
			mlParams.put(TrainingParameters.CUTOFF_PARAM, 0);

			return TokenizerME.train(samples, TokenizerFactory.create(null, "eng", null, true, null), mlParams);
		}

}
