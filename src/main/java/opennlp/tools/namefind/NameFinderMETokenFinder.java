package opennlp.tools.namefind;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;

import it.sella.f24.bean.Result;
import it.sella.f24.service.F24OCRService;
import opennlp.tools.formats.ResourceAsStreamFactory;
import opennlp.tools.ml.maxent.quasinewton.QNTrainer;
import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.ml.naivebayes.NaiveBayesTrainer;
import opennlp.tools.tokenize.TokenSample;
import opennlp.tools.tokenize.TokenSampleStream;
import opennlp.tools.tokenize.TokenizerFactory;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.MockInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;

public class NameFinderMETokenFinder {

	private static final String TYPE_OVERRIDE = "aType";
	private static final String DEFAULT = "default";

	private static TokenNameFinderModel tokenFinder = null;
	private static TokenNameFinderModel tokenFinderSection1 = null;
	private static TokenNameFinderModel tokenFinderSection2Constants = null;
	private static TokenNameFinderModel tokenFinderSection2Variables = null;
	private static Logger logger = null;
	static {
		logger = Logger.getLogger(NameFinderMETokenFinder.class);
		PropertyConfigurator.configure("src/main/resources/log4j.properties");
	}

	@Test
	public void testNameFinder() throws Exception {

		// train the name finder
		String encoding = "ISO-8859-1";

		ObjectStream<NameSample> sampleStream = new NameSampleDataStream(new PlainTextByLineStream(
				new MockInputStreamFactory(new File("opennlp/tools/namefind/AnnotatedSentences.txt")), encoding));

		TrainingParameters params = new TrainingParameters();
		params.put(TrainingParameters.ITERATIONS_PARAM, 70);
		params.put(TrainingParameters.CUTOFF_PARAM, 1);

		TokenNameFinderModel nameFinderModel = NameFinderME.train("eng", null, sampleStream, params,
				TokenNameFinderFactory.create(null, null, Collections.emptyMap(), new BioCodec()));

		TokenNameFinder nameFinder = new NameFinderME(nameFinderModel);

		// now test if it can detect the sample sentences

		String[] sentence = { "Alisa", "appreciated", "the", "hint", "and", "enjoyed", "a", "delicious", "traditional",
				"meal." };

		Span[] names = nameFinder.find(sentence);

		Assert.assertEquals(1, names.length);
		Assert.assertEquals(new Span(0, 1, DEFAULT), names[0]);

		sentence = new String[] { "Hi", "Mike", ",", "it's", "Stefanie", "Schmidt", "." };

		names = nameFinder.find(sentence);

		Assert.assertEquals(2, names.length);
		Assert.assertEquals(new Span(1, 2, DEFAULT), names[0]);
		Assert.assertEquals(new Span(4, 6, DEFAULT), names[1]);
	}

	/**
	 * Train NamefinderME using AnnotatedSentencesWithTypes.txt with "person"
	 * nameType and try the model in a sample text.
	 */

	/**
	 * Train NamefinderME using AnnotatedSentencesWithTypes.txt with "person"
	 * nameType and try the model in a sample text.
	 */

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
	
	
	
	
	@SuppressWarnings("unused")
	public String f24PrepareTrainingFile(String filename) throws IOException, InvalidFormatException {

		String encoding = "ISO-8859-1";
		
		String outputfilepath="src/main/resources/f24_training.bin";
		TokenNameFinderModel nameFinderModel;
//		FileInputStream r = new FileInputStream("src/main/resources/f24_training.bin");
//
//		if (r != null) {
//			nameFinderModel = new TokenNameFinderModel(r);
//		} else {


			ObjectStream<NameSample> sampleStream = new NameSampleDataStream(new PlainTextByLineStream(
					new MockInputStreamFactory(new File(filename)), encoding));

			TrainingParameters params = new TrainingParameters();
			params.put(TrainingParameters.ITERATIONS_PARAM, 100);
			params.put(TrainingParameters.CUTOFF_PARAM, 1);

			nameFinderModel = NameFinderME.train("eng", null, sampleStream, params,
					TokenNameFinderFactory.create(null, null, Collections.emptyMap(), new BioCodec()));

			BufferedOutputStream modelOut = null;
			try {
				modelOut = new BufferedOutputStream(new FileOutputStream(outputfilepath));
				nameFinderModel.serialize(modelOut);
			} finally {
				if (modelOut != null)
					modelOut.close();
			}
//		}
//		r.close();
		return outputfilepath;

	}
	
	

	public List<Result> f24SectionSplit(String sentence) throws Exception {
		if (tokenFinder == null)
			tokenFinder = f24SectionSplit();
		TokenizerModel model = NameFinderMETest.createMaxentTokenModel();

		TokenizerME tokenizer = new TokenizerME(model);
		String[] sentence2 = tokenizer.tokenize(sentence);
		NameFinderME nameFinder = new NameFinderME(tokenFinder);

		Map<String, String> valMap = new HashMap<>();
		List<Result> results = new ArrayList<>();
		Span[] names2 = nameFinder.find(sentence2);

		for (Span name : names2) {
			System.out.print(name.getType() + " ");
			int c = 1;
			StringBuffer buffer = new StringBuffer();
			for (int i = name.getStart(); i < name.getEnd(); i++) {

				System.out.print(sentence2[i] + " ");

				buffer.append(sentence2[i] + " ");
				String temp = name.getType();
				if (valMap.containsKey(temp)) {
					temp = temp + c;
					valMap.put(temp, sentence2[i]);
				} else {
					valMap.put(name.getType(), sentence2[i]);
				}
				c++;

			}

			results.add(new Result(name.getType(), buffer.toString()));

		}
		return results;

	}
	
	
	
	
	
	@SuppressWarnings("unused")
	private TokenNameFinderModel f24SectionSplit() throws IOException, InvalidFormatException {

		String encoding = "ISO-8859-1";
		TokenNameFinderModel nameFinderModel;
		FileInputStream r = new FileInputStream("src/main/resources/f24_sectionmodel.bin");

		if (r != null) {
			nameFinderModel = new TokenNameFinderModel(r);
		} else {


			ObjectStream<NameSample> sampleStream = new NameSampleDataStream(new PlainTextByLineStream(
					new MockInputStreamFactory(new File("sectiontrainingdata3.txt")), encoding));

			TrainingParameters params = new TrainingParameters();
			params.put(TrainingParameters.ITERATIONS_PARAM, 100);
			params.put(TrainingParameters.CUTOFF_PARAM, 1);

			nameFinderModel = NameFinderME.train("eng", null, sampleStream, params,
					TokenNameFinderFactory.create(null, null, Collections.emptyMap(), new BioCodec()));

			BufferedOutputStream modelOut = null;
			try {
				modelOut = new BufferedOutputStream(new FileOutputStream("src/main/resources/f24_sectionmodel.bin"));
				nameFinderModel.serialize(modelOut);
			} finally {
				if (modelOut != null)
					modelOut.close();
			}
		}
		r.close();
		return nameFinderModel;

	}

	public List<Result> f24_Section1(String sentence) throws Exception {
		if (tokenFinderSection1 == null)
			tokenFinderSection1 = f24_Section1_Train();
		TokenizerModel model = NameFinderMETokenFinder.createMaxentTokenModel();

		TokenizerME tokenizer = new TokenizerME(model);
		String[] sentence2 = tokenizer.tokenize(sentence);
//		logger.info("Sentence");
//		for(String sentencex : sentence2) {
//			logger.info(sentencex+"\n");	
//		}
		NameFinderME nameFinder = new NameFinderME(tokenFinderSection1);

		List<Result> results = new ArrayList<>();
		Span[] names2 = nameFinder.find(sentence2);
		for (Span name : names2) {
			System.out.println(name);
			System.out.print(name.getType() + " ");

			for (int i = name.getStart(); i < name.getEnd(); i++) {

				results.add(new Result(name.getType(), sentence2[i]));

				System.out.print(sentence2[i] + " \n");
//				logger.info(sentence2[i] + " ");

			}
		}
		return results;

	}
	
	@SuppressWarnings("unused")
	private TokenNameFinderModel f24_Section1_Train() throws IOException, InvalidFormatException {
		String encoding = "ISO-8859-1";
		TokenNameFinderModel nameFinderModel;
		FileInputStream r = new FileInputStream("src/main/resources/f24_sec1model.bin");

		if (r != null) {
			nameFinderModel = new TokenNameFinderModel(r);
		} else {

		ObjectStream<NameSample> sampleStream = new NameSampleDataStream(new PlainTextByLineStream(
					new MockInputStreamFactory(new File("section1trainingnewspace_result.txt")), encoding));

			TrainingParameters params = new TrainingParameters();
			params.put(TrainingParameters.ITERATIONS_PARAM, 100);
			params.put(TrainingParameters.CUTOFF_PARAM, 1);
//	
			
//			params.put(TrainingParameters.ALGORITHM_PARAM, QNTrainer.MAXENT_QN_VALUE);

			nameFinderModel = NameFinderME.train("eng", null, sampleStream, params,
					TokenNameFinderFactory.create(null, null, Collections.emptyMap(), new BioCodec()));

			BufferedOutputStream modelOut = null;
			try {
				modelOut = new BufferedOutputStream(new FileOutputStream("src/main/resources/f24_sec1model.bin"));
				nameFinderModel.serialize(modelOut);
			} finally {
				if (modelOut != null)
					modelOut.close();
			}
		}
		r.close();
	return nameFinderModel;
	}

	public List<Result> f24_Section2_Constants(String sentence) throws Exception {
		if (tokenFinderSection2Constants == null)
			tokenFinderSection2Constants = f24_Section2_Constants_Train();
		TokenizerModel model = NameFinderMETokenFinder.createMaxentTokenModel();

		TokenizerME tokenizer = new TokenizerME(model);
		String[] sentence2 = tokenizer.tokenize(sentence);
		NameFinderME nameFinder = new NameFinderME(tokenFinderSection2Constants);

		Span[] names2 = nameFinder.find(sentence2);
		List<Result> results = new ArrayList<>();
		for (Span name : names2) {

			System.out.print(name.getType() + " ");

			StringBuffer buffer = new StringBuffer();

			for (int i = name.getStart(); i < name.getEnd(); i++) {

				buffer.append(sentence2[i]);

			}

			results.add(new Result(name.getType(), buffer.toString()));
			System.out.println();
		}
		return results;

	}

	

	@SuppressWarnings("unused")
	private TokenNameFinderModel f24_Section2_Constants_Train() throws IOException, InvalidFormatException {
		String encoding = "ISO-8859-1";
		TokenNameFinderModel nameFinderModel;
		FileInputStream r = new FileInputStream("src/main/resources/f24_sec2part1model.bin");

		if (r != null) {
			nameFinderModel = new TokenNameFinderModel(r);
		} else {
			ObjectStream<NameSample> sampleStream = new NameSampleDataStream(new PlainTextByLineStream(
					new MockInputStreamFactory(new File("section2trainingnewspace_result_part1.txt")), encoding));

			TrainingParameters params = new TrainingParameters();
			params.put(TrainingParameters.ITERATIONS_PARAM, 100);
			params.put(TrainingParameters.CUTOFF_PARAM, 1);

			nameFinderModel = NameFinderME.train("eng", null, sampleStream, params,
					TokenNameFinderFactory.create(null, null, Collections.emptyMap(), new BioCodec()));

			BufferedOutputStream modelOut = null;
			try {
				modelOut = new BufferedOutputStream(new FileOutputStream("src/main/resources/f24_sec2part1model.bin"));
				nameFinderModel.serialize(modelOut);
			} finally {
				if (modelOut != null)
					modelOut.close();
			}
		}
		r.close();
		return nameFinderModel;
	}
	
	public List<Result> f24_Section2_Variables(String sentence) throws Exception {
		if (tokenFinderSection2Variables == null)
			tokenFinderSection2Variables = f24_Section2_Variables_Train();
		TokenizerModel model = NameFinderMETokenFinder.createMaxentTokenModel();

		TokenizerME tokenizer = new TokenizerME(model);
		String[] sentence2 = tokenizer.tokenize(sentence);
		NameFinderME nameFinder = new NameFinderME(tokenFinderSection2Variables);

		Span[] names2 = nameFinder.find(sentence2);
		List<Result> results = new ArrayList<>();
		for (Span name : names2) {

			System.out.print(name.getType() + " ");

			StringBuffer buffer = new StringBuffer();

			for (int i = name.getStart(); i < name.getEnd(); i++) {

				buffer.append(sentence2[i]);

			}

			results.add(new Result(name.getType(), buffer.toString()));
			System.out.println();
		}
		return results;

	}

	

	@SuppressWarnings("unused")
	private TokenNameFinderModel f24_Section2_Variables_Train() throws IOException, InvalidFormatException {
		String encoding = "ISO-8859-1";
		TokenNameFinderModel nameFinderModel;
		FileInputStream r = new FileInputStream("src/main/resources/f24_sec2part2model.bin");

		if (r != null) {
			nameFinderModel = new TokenNameFinderModel(r);
		} else {
			ObjectStream<NameSample> sampleStream = new NameSampleDataStream(new PlainTextByLineStream(
					new MockInputStreamFactory(new File("section2trainingnewspace_result_part2.txt")), encoding));

			TrainingParameters params = new TrainingParameters();
			params.put(TrainingParameters.ITERATIONS_PARAM, 100);
			params.put(TrainingParameters.CUTOFF_PARAM, 1);
			
			nameFinderModel = NameFinderME.train("eng", null, sampleStream, params,
					TokenNameFinderFactory.create(null, null, Collections.emptyMap(), new BioCodec()));

			BufferedOutputStream modelOut = null;
			try {
				modelOut = new BufferedOutputStream(new FileOutputStream("src/main/resources/f24_sec2part2model.bin"));
				nameFinderModel.serialize(modelOut);
			} finally {
				if (modelOut != null)
					modelOut.close();
			}
		}
		r.close();
		return nameFinderModel;
	}

	/**
	 * Train NamefinderME using OnlyWithNames.train. The goal is to check if the
	 * model validator accepts it. This is related to the issue OPENNLP-9
	 */
	@Test
	public void testOnlyWithNames() throws Exception {

		// train the name finder
		ObjectStream<NameSample> sampleStream = new NameSampleDataStream(new PlainTextByLineStream(
				new MockInputStreamFactory(new File("opennlp/tools/namefind/OnlyWithNames.train")), "UTF-8"));

		TrainingParameters params = new TrainingParameters();
		params.put(TrainingParameters.ITERATIONS_PARAM, 70);
		params.put(TrainingParameters.CUTOFF_PARAM, 1);

		TokenNameFinderModel nameFinderModel = NameFinderME.train("eng", null, sampleStream, params,
				TokenNameFinderFactory.create(null, null, Collections.emptyMap(), new BioCodec()));

		NameFinderME nameFinder = new NameFinderME(nameFinderModel);

		// now test if it can detect the sample sentences

		String[] sentence = ("Neil Abercrombie Anibal Acevedo-Vila Gary Ackerman "
				+ "Robert Aderholt Daniel Akaka Todd Akin Lamar Alexander Rodney Alexander").split("\\s+");

		Span[] names1 = nameFinder.find(sentence);

		Assert.assertEquals(new Span(0, 2, DEFAULT), names1[0]);
		Assert.assertEquals(new Span(2, 4, DEFAULT), names1[1]);
		Assert.assertEquals(new Span(4, 6, DEFAULT), names1[2]);
		Assert.assertTrue(!hasOtherAsOutcome(nameFinderModel));
	}

	@Test
	public void testOnlyWithNamesTypeOverride() throws Exception {

		// train the name finder
		ObjectStream<NameSample> sampleStream = new NameSampleDataStream(new PlainTextByLineStream(
				new MockInputStreamFactory(new File("opennlp/tools/namefind/OnlyWithNames.train")), "UTF-8"));

		TrainingParameters params = new TrainingParameters();
		params.put(TrainingParameters.ITERATIONS_PARAM, 70);
		params.put(TrainingParameters.CUTOFF_PARAM, 1);

		TokenNameFinderModel nameFinderModel = NameFinderME.train("eng", TYPE_OVERRIDE, sampleStream, params,
				TokenNameFinderFactory.create(null, null, Collections.emptyMap(), new BioCodec()));

		NameFinderME nameFinder = new NameFinderME(nameFinderModel);

		// now test if it can detect the sample sentences

		String[] sentence = ("Neil Abercrombie Anibal Acevedo-Vila Gary Ackerman "
				+ "Robert Aderholt Daniel Akaka Todd Akin Lamar Alexander Rodney Alexander").split("\\s+");

		Span[] names1 = nameFinder.find(sentence);

		Assert.assertEquals(new Span(0, 2, TYPE_OVERRIDE), names1[0]);
		Assert.assertEquals(new Span(2, 4, TYPE_OVERRIDE), names1[1]);
		Assert.assertEquals(new Span(4, 6, TYPE_OVERRIDE), names1[2]);
		Assert.assertTrue(!hasOtherAsOutcome(nameFinderModel));
	}

	/**
	 * Train NamefinderME using OnlyWithNamesWithTypes.train. The goal is to check
	 * if the model validator accepts it. This is related to the issue OPENNLP-9
	 */
	@Test
	public void testOnlyWithNamesWithTypes() throws Exception {

		// train the name finder
		ObjectStream<NameSample> sampleStream = new NameSampleDataStream(new PlainTextByLineStream(
				new MockInputStreamFactory(new File("opennlp/tools/namefind/OnlyWithNamesWithTypes.train")), "UTF-8"));

		TrainingParameters params = new TrainingParameters();
		params.put(TrainingParameters.ITERATIONS_PARAM, 70);
		params.put(TrainingParameters.CUTOFF_PARAM, 1);

		TokenNameFinderModel nameFinderModel = NameFinderME.train("eng", null, sampleStream, params,
				TokenNameFinderFactory.create(null, null, Collections.emptyMap(), new BioCodec()));

		NameFinderME nameFinder = new NameFinderME(nameFinderModel);

		// now test if it can detect the sample sentences

		String[] sentence = ("Neil Abercrombie Anibal Acevedo-Vila Gary Ackerman "
				+ "Robert Aderholt Daniel Akaka Todd Akin Lamar Alexander Rodney Alexander").split("\\s+");

		Span[] names1 = nameFinder.find(sentence);

		Assert.assertEquals(new Span(0, 2, "person"), names1[0]);
		Assert.assertEquals(new Span(2, 4, "person"), names1[1]);
		Assert.assertEquals(new Span(4, 6, "person"), names1[2]);
		Assert.assertEquals("person", names1[2].getType());
		Assert.assertTrue(!hasOtherAsOutcome(nameFinderModel));
	}

	/**
	 * Train NamefinderME using OnlyWithNames.train. The goal is to check if the
	 * model validator accepts it. This is related to the issue OPENNLP-9
	 */
	@Test
	public void testOnlyWithEntitiesWithTypes() throws Exception {

		// train the name finder
		ObjectStream<NameSample> sampleStream = new NameSampleDataStream(new PlainTextByLineStream(
				new MockInputStreamFactory(new File("opennlp/tools/namefind/OnlyWithEntitiesWithTypes.train")),
				"UTF-8"));

		TrainingParameters params = new TrainingParameters();
		params.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT");
		params.put(TrainingParameters.ITERATIONS_PARAM, 70);
		params.put(TrainingParameters.CUTOFF_PARAM, 1);

		TokenNameFinderModel nameFinderModel = NameFinderME.train("eng", null, sampleStream, params,
				TokenNameFinderFactory.create(null, null, Collections.emptyMap(), new BioCodec()));

		NameFinderME nameFinder = new NameFinderME(nameFinderModel);

		// now test if it can detect the sample sentences

		String[] sentence = "NATO United States Barack Obama".split("\\s+");

		Span[] names1 = nameFinder.find(sentence);

		Assert.assertEquals(new Span(0, 1, "organization"), names1[0]); // NATO
		Assert.assertEquals(new Span(1, 3, "location"), names1[1]); // United
																	// States
		Assert.assertEquals("person", names1[2].getType());
		Assert.assertTrue(!hasOtherAsOutcome(nameFinderModel));
	}

	private boolean hasOtherAsOutcome(TokenNameFinderModel nameFinderModel) {
		SequenceClassificationModel<String> model = nameFinderModel.getNameFinderSequenceModel();
		String[] outcomes = model.getOutcomes();
		for (String outcome : outcomes) {
			if (outcome.equals(NameFinderME.OTHER)) {
				return true;
			}
		}
		return false;
	}

	@Test
	public void testDropOverlappingSpans() {
		Span[] spans = new Span[] { new Span(1, 10), new Span(1, 11), new Span(1, 11), new Span(5, 15) };
		Span[] remainingSpan = NameFinderME.dropOverlappingSpans(spans);
		Assert.assertEquals(new Span(1, 11), remainingSpan[0]);
	}

	/**
	 * Train NamefinderME using voa1.train with several nameTypes and try the model
	 * in a sample text.
	 */
	@Test
	public void testNameFinderWithMultipleTypes() throws Exception {

		// train the name finder
		ObjectStream<NameSample> sampleStream = new NameSampleDataStream(new PlainTextByLineStream(
				new MockInputStreamFactory(new File("opennlp/tools/namefind/voa1.train")), "UTF-8"));

		TrainingParameters params = new TrainingParameters();
		params.put(TrainingParameters.ITERATIONS_PARAM, 70);
		params.put(TrainingParameters.CUTOFF_PARAM, 1);

		TokenNameFinderModel nameFinderModel = NameFinderME.train("eng", null, sampleStream, params,
				TokenNameFinderFactory.create(null, null, Collections.emptyMap(), new BioCodec()));

		NameFinderME nameFinder = new NameFinderME(nameFinderModel);

		// now test if it can detect the sample sentences

		String[] sentence = new String[] { "U", ".", "S", ".", "President", "Barack", "Obama", "has", "arrived", "in",
				"South", "Korea", ",", "where", "he", "is", "expected", "to", "show", "solidarity", "with", "the",
				"country", "'", "s", "president", "in", "demanding", "North", "Korea", "move", "toward", "ending",
				"its", "nuclear", "weapons", "programs", "." };

		Span[] names1 = nameFinder.find(sentence);

		Assert.assertEquals(new Span(0, 4, "location"), names1[0]);
		Assert.assertEquals(new Span(5, 7, "person"), names1[1]);
		Assert.assertEquals(new Span(10, 12, "location"), names1[2]);
		Assert.assertEquals(new Span(28, 30, "location"), names1[3]);
		Assert.assertEquals("location", names1[0].getType());
		Assert.assertEquals("person", names1[1].getType());
		Assert.assertEquals("location", names1[2].getType());
		Assert.assertEquals("location", names1[3].getType());

		sentence = new String[] { "Scott", "Snyder", "is", "the", "director", "of", "the", "Center", "for", "U", ".",
				"S", ".", "Korea", "Policy", "." };

		Span[] names2 = nameFinder.find(sentence);

		Assert.assertEquals(2, names2.length);
		Assert.assertEquals(new Span(0, 2, "person"), names2[0]);
		Assert.assertEquals(new Span(7, 15, "organization"), names2[1]);
		Assert.assertEquals("person", names2[0].getType());
		Assert.assertEquals("organization", names2[1].getType());
	}
	
	
	public List<Result> f24_SectionDemo(String sentence) throws Exception {
		if (tokenFinderSection2Variables == null)
			tokenFinderSection2Variables = f24_SectionSplitDemo();
		TokenizerModel model = NameFinderMETokenFinder.createMaxentTokenModel();

		TokenizerME tokenizer = new TokenizerME(model);
		String[] sentence2 = tokenizer.tokenize(sentence);
		NameFinderME nameFinder = new NameFinderME(tokenFinderSection2Variables);

		Span[] names2 = nameFinder.find(sentence2);
		List<Result> results = new ArrayList<>();
		for (Span name : names2) {

			System.out.print(name.getType() + " ");

			StringBuffer buffer = new StringBuffer();

			for (int i = name.getStart(); i < name.getEnd(); i++) {

				buffer.append(sentence2[i]+" ");

			}

			results.add(new Result(name.getType(), buffer.toString()));
			System.out.println();
		}
		return results;

	}

	

	@SuppressWarnings("unused")
	private TokenNameFinderModel f24_SectionSplitDemo() throws IOException, InvalidFormatException {
		String encoding = "ISO-8859-1";
		TokenNameFinderModel nameFinderModel;
//		FileInputStream r = new FileInputStream("src/main/resources/f24_secdemo.bin");
//
//		if (r != null) {
//			nameFinderModel = new TokenNameFinderModel(r);
//		} else {
			ObjectStream<NameSample> sampleStream = new NameSampleDataStream(new PlainTextByLineStream(
					new MockInputStreamFactory(new File("sectionsplit_result.txt")), encoding));

			TrainingParameters params = new TrainingParameters();
			params.put(TrainingParameters.ITERATIONS_PARAM, 100);
			params.put(TrainingParameters.CUTOFF_PARAM, 1);

			nameFinderModel = NameFinderME.train("eng", null, sampleStream, params,
					TokenNameFinderFactory.create(null, null, Collections.emptyMap(), new BioCodec()));

			BufferedOutputStream modelOut = null;
			try {
				modelOut = new BufferedOutputStream(new FileOutputStream("src/main/resources/f24_secdemo.bin"));
				nameFinderModel.serialize(modelOut);
			} finally {
				if (modelOut != null)
					modelOut.close();
			}
//		}
//		r.close();
		return nameFinderModel;
	}

}
