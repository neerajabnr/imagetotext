package opennlp.tools.namefind;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
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

import it.sella.f24.service.F24OCRService;
import it.sella.f24.testclasses.Result;
import opennlp.tools.formats.ResourceAsStreamFactory;
import opennlp.tools.ml.model.SequenceClassificationModel;
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

public class NameFinderMETest4 {

	private static final String TYPE_OVERRIDE = "aType";
	private static final String DEFAULT = "default";

	private static TokenNameFinderModel nameFinder1 = null;
	private static TokenNameFinderModel nameFinder2 = null;
	private static Logger logger = null;
	static {
		logger=Logger.getLogger(NameFinderMETest4.class);
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

	@Test
	public void testTrainNameFinderWithTypes() throws Exception {
		TokenNameFinderModel nameFinderModel = f24Train();
		TokenizerModel model = NameFinderMETest.createMaxentTokenModel();

		TokenizerME tokenizer = new TokenizerME(model);
		String[] sentence2 = tokenizer.tokenize(
				"codice tributo.codice identificado.anno di.ferimento.Importi a debito versad.rateariome/reglonel.pro mese.d.01 01.Importi a credito compensad.261,39.6099.2015.IMPOS TE DIRETTE - IVA.RITENUTE ALLA FONTE.ALTRI TRIBUTI ED INTERESSI.codice ufficio.codice alto.+/-.SALDO (A-B).TOTALE.6139-.261,39.");
		NameFinderME nameFinder = new NameFinderME(nameFinderModel);

		Span[] names2 = nameFinder.find(sentence2);

		for (Span name : names2) {
			// if(name.getType().equals("templateERARIO1")) {
			// getVauesFromTemplate(sentence2, name.getType());
			// }
			System.out.print(name.getType() + " ");
			////logger.info(name.getType() + " ");
			// System.out.println(name.toString() + " " +
			// sentence2[name.getStart()]);
			for (int i = name.getStart(); i < name.getEnd(); i++) {
				System.out.print(sentence2[i] + " ");
				////logger.info(sentence2[i] + " ");
			}
			System.out.println();
		}

		nameFinderModel = f24Train2();
		model = NameFinderMETest.createMaxentTokenModel();
		sentence2 = tokenizer.tokenize(
				"CODICE FISCALE 0 2 0 5 0 0 8 0 4 4 5.1.cognome, denominazione o raglone sociale. in liquidazione.data di nascka.sesso (MF).comune fo Stato estero di nascita.Barrare in caso di andampos.non coincidente con anno solare.comune.prov..FM.Via e numero civico.CONTRADA CAMERA DI TORRE 19.DOMICILIO FISCALE FERMO.CODICE FISCALE del coobbligato, erede,.genitore, tutore o curatore fallimentare. DATI ANAGRAFICI GRETA S.A.S. DI DONATO MANCINI &amp; C.");
		nameFinder = new NameFinderME(nameFinderModel);

		names2 = nameFinder.find(sentence2);

		for (Span name : names2) {
			System.out.print(name.getType() + " ");
			// System.out.println(name.toString() + " " +
			// sentence2[name.getStart()]);
			for (int i = name.getStart(); i < name.getEnd(); i++) {
				System.out.print(sentence2[i] + " ");
				////logger.info(sentence2[i] + " ");
			}
		}

	}

	@Test
	public void f24_2() throws Exception {
		TokenNameFinderModel nameFinderModel = f242Train();
		TokenizerModel model = NameFinderMETest.createMaxentTokenModel();

		TokenizerME tokenizer = new TokenizerME(model);
		String[] sentence2 = tokenizer.tokenize(
				"CODICE FISCALE BRSDRN24C42A859R\ncognomie* denambazlonie o ragione sociale\nnome\nDATI ANAGRAFICI BORASIO\nADRIANA\ndala di nascita\nsessa(MF) comune lo Stato estero) di nascila\n 02 03 1924 1925 1926 F BIELLA\n�\nCODICE FISCALE del coobbligato* erede*\ngenitore* tutore o curatore fallimentare\ncodice identificalvo");
		NameFinderME nameFinder = new NameFinderME(nameFinderModel);

		Span[] names2 = nameFinder.find(sentence2);

		for (Span name : names2) {
			// if(name.getType().equals("templateERARIO1")) {
			// getVauesFromTemplate(sentence2, name.getType());
			// }
			System.out.print(name.getType() + " ");
			////logger.info(name.getType() + " ");
			// System.out.println(name.toString() + " " +
			// sentence2[name.getStart()]);
			for (int i = name.getStart(); i < name.getEnd(); i++) {
				System.out.print(sentence2[i] + " ");
				////logger.info(sentence2[i] + " ");
			}
		}

	}

	@Test
	public void f24_22() throws Exception {
		TokenNameFinderModel nameFinderModel = f2422Train();
		TokenizerModel model = NameFinderMETest.createMaxentTokenModel();

		TokenizerME tokenizer = new TokenizerME(model);
		String[] sentence2 = tokenizer.tokenize(
				"Sezione cod . tributo ob taleazionel codice ente . mw . Variati numero detrazione importi a debito imob versati importi mese rif . riferimento a credito compensati EL 3918 D600 2018 243*00 ");
		NameFinderME nameFinder = new NameFinderME(nameFinderModel);

		Span[] names2 = nameFinder.find(sentence2);

		for (Span name : names2) {
			// if(name.getType().equals("templateERARIO1")) {
			// getVauesFromTemplate(sentence2, name.getType());
			// }
			System.out.print(name.getType() + " ");
			////logger.info(name.getType() + " ");
			// System.out.println(name.toString() + " " +
			// sentence2[name.getStart()]);
			for (int i = name.getStart(); i < name.getEnd(); i++) {
				System.out.print(sentence2[i] + " ");
				////logger.info(sentence2[i] + " ");
			}
			System.out.println();
		}

	}

	public List<Result> f24_section1(String sentence) throws Exception {
		if (nameFinder1 == null)
			nameFinder1 = f242Train();
		TokenizerModel model = NameFinderMETest.createMaxentTokenModel();

		TokenizerME tokenizer = new TokenizerME(model);
		String[] sentence2 = tokenizer.tokenize(sentence);
		NameFinderME nameFinder = new NameFinderME(nameFinder1);

		Map<String, String> valMap = new HashMap<>();
		List<Result> results=new ArrayList<>();
		Span[] names2 = nameFinder.find(sentence2);
		for (Span name : names2) {
			// if(name.getType().equals("templateERARIO1")) {
			// getVauesFromTemplate(sentence2, name.getType());
			// }
			System.out.print(name.getType() + " ");
			////logger.info(name.getType() + " ");
			// System.out.println(name.toString() + " " +
			// sentence2[name.getStart()]);
			int c=1;
			for (int i = name.getStart(); i < name.getEnd(); i++) {
				results.add(new Result(name.getType(), sentence2[i]));
				System.out.print(sentence2[i] + " ");
				////logger.info(sentence2[i] + " ");
				
				String temp = name.getType();
				if (valMap.containsKey(temp)) {
					temp = temp + c;
					valMap.put(temp, sentence2[i]);
				} else {
					valMap.put(name.getType(), sentence2[i]);
				}
				c++;
				
				
			}
		}
		return results;

	}

	public List<Result> f24_section2(String sentence) throws Exception {
		if (nameFinder2 == null)
			nameFinder2 = f2422Train();
		TokenizerModel model = NameFinderMETest.createMaxentTokenModel();

		TokenizerME tokenizer = new TokenizerME(model);
		String[] sentence2 = tokenizer.tokenize(sentence);
		NameFinderME nameFinder = new NameFinderME(nameFinder2);

		Span[] names2 = nameFinder.find(sentence2);
		Map<String, String> valMap = new HashMap<>();
		List<Result> results=new ArrayList<>();
		int k = 1,index=0;
		for (Span name : names2) {
			// if(name.getType().equals("templateERARIO1")) {
			// getVauesFromTemplate(sentence2, name.getType());
			// }
			System.out.print(name.getType() + " ");
			////logger.info(name.getType() + " ");

			// System.out.println(name.toString() + " " +
			// sentence2[name.getStart()]);
			String cod="",dobito="";
			StringBuffer buffer=new StringBuffer();
			for (int i = name.getStart(); i < name.getEnd(); i++) {
				/*if(name.getType().equals("codice")){
					cod=cod+sentence2[i];
					if(cod.length()<4||cod.length()>4)
					continue;
				}
				if(cod.length()==4){
					results.add(new Result(name.getType(), cod));
					System.out.print(cod);
					continue;
				}
				*/
				buffer.append(sentence2[i]);
				
				int c = 1;
				System.out.print(sentence2[i] + " ");
				////logger.info(name.getType() + " ");
				if (valMap.get(name.getType() + 1) != null) {
					k++;
				}
				String temp = name.getType();
				if (valMap.containsKey(temp)) {
					temp = temp + c;
					valMap.put(temp, sentence2[i]);
					c++;
				} else {
					valMap.put(name.getType(), sentence2[i]);
				}
				

			}
			results.add(new Result(name.getType(), buffer.toString()));
			System.out.println();
		}
		return results;

	}

	@Test
	public void testNameFinderWithTypes() throws Exception {

		// train the name finder

		TokenNameFinderModel nameFinderModel = f24Train();
		NameFinderME nameFinder = new NameFinderME(nameFinderModel);

		// now test if it can detect the sample sentences

		String[] sentence2 = new String[] { "DATI", "ANAGRAFICI", "S.A.S.", "DI", "DONATO" };

		Span[] names2 = nameFinder.find(sentence2);

		for (Span name : names2) {
			System.out.println(name.getType());
			System.out.println(name.toString() + "  " + sentence2[name.getStart()]);
		}
		sentence2 = new String[] { "CODICE", "FISCALE", "11213145678910", "cognome", "." };

		names2 = nameFinder.find(sentence2);

		for (Span name : names2) {
			System.out.println(name.getType());
			System.out.println(name.toString() + "  " + sentence2[name.getStart()]);
		}

		sentence2 = new String[] { "6099", "." };

		names2 = nameFinder.find(sentence2);

		for (Span name : names2) {
			System.out.println(name.getType());
			System.out.println(name.toString() + "  " + sentence2[name.getStart()]);
		}

		sentence2 = new String[] { "2015", "." };

		names2 = nameFinder.find(sentence2);

		for (Span name : names2) {
			System.out.println(name.getType());
			System.out.println(name.toString() + "  " + sentence2[name.getStart()]);
		}
		sentence2 = new String[] { "261,39", "." };

		names2 = nameFinder.find(sentence2);

		for (Span name : names2) {
			System.out.println(name.getType());
			System.out.println(name.toString() + "  " + sentence2[name.getStart()]);
		}

		sentence2 = new String[] { "01", "." };

		names2 = nameFinder.find(sentence2);

		for (Span name : names2) {
			System.out.println(name.getType());
			System.out.println(name.toString() + "  " + sentence2[name.getStart()]);
		}

		sentence2 = new String[] { "01", "06", "Importi", "a", "credito" };

		names2 = nameFinder.find(sentence2);

		for (Span name : names2) {
			System.out.println(name.getType());
			System.out.println(name.toString() + "  " + sentence2[name.getStart()]);
		}
		/*
		 * System.out.println( names2[0].getType()); System.out.println(
		 * names2[0].toString());
		 * 
		 * Assert.assertEquals(2, names2.length); Assert.assertEquals(new
		 * Span(1, 2, "person"), names2[0]); Assert.assertEquals(new Span(4, 6,
		 * "person"), names2[1]); Assert.assertEquals("person",
		 * names2[0].getType()); Assert.assertEquals("person",
		 * names2[1].getType());
		 * 
		 * String[] sentence = { "Alisa", "appreciated", "the", "hint", "and",
		 * "enjoyed", "a", "delicious", "traditional", "meal." };
		 * 
		 * Span[] names = nameFinder.find(sentence);
		 * 
		 * Assert.assertEquals(1, names.length); Assert.assertEquals(new Span(0,
		 * 1, "person"), names[0]);
		 * Assert.assertTrue(hasOtherAsOutcome(nameFinderModel));
		 */
	}

	private TokenNameFinderModel f24Train() throws IOException, InvalidFormatException {
		String encoding = "ISO-8859-1";

		ObjectStream<NameSample> sampleStream = new NameSampleDataStream(new PlainTextByLineStream(
				new MockInputStreamFactory(new File("opennlp/tools/namefind/section2.txt")), encoding));

		TrainingParameters params = new TrainingParameters();
		params.put(TrainingParameters.ITERATIONS_PARAM, 100);
		params.put(TrainingParameters.CUTOFF_PARAM, 1);

		TokenNameFinderModel nameFinderModel = NameFinderME.train("eng", null, sampleStream, params,
				TokenNameFinderFactory.create(null, null, Collections.emptyMap(), new BioCodec()));
		return nameFinderModel;
	}

	private TokenNameFinderModel f24Train2() throws IOException, InvalidFormatException {
		String encoding = "ISO-8859-1";

		ObjectStream<NameSample> sampleStream = new NameSampleDataStream(new PlainTextByLineStream(
				new MockInputStreamFactory(new File("opennlp/tools/namefind/section1.txt")), encoding));

		TrainingParameters params = new TrainingParameters();
		params.put(TrainingParameters.ITERATIONS_PARAM, 100);
		params.put(TrainingParameters.CUTOFF_PARAM, 1);

		TokenNameFinderModel nameFinderModel = NameFinderME.train("eng", null, sampleStream, params,
				TokenNameFinderFactory.create(null, null, Collections.emptyMap(), new BioCodec()));
		return nameFinderModel;
	}

	@SuppressWarnings("unused")
	private TokenNameFinderModel f242Train() throws IOException, InvalidFormatException {
		String encoding = "ISO-8859-1";
		TokenNameFinderModel nameFinderModel;
		 FileInputStream r = new FileInputStream("f24_sec1modalspace.bin");
		
		 if(r!=null){
		 nameFinderModel = new TokenNameFinderModel(r);
		 }else {

//		ObjectStream<NameSample> sampleStream = new NameSampleDataStream(new PlainTextByLineStream(
//					new MockInputStreamFactory(new File("section1trainingnewspace_result.txt")), encoding));

			 

				ObjectStream<NameSample> sampleStream = new NameSampleDataStream(new PlainTextByLineStream(
							new MockInputStreamFactory(new File("section1trainingnewspace_result2.txt")), encoding));

		TrainingParameters params = new TrainingParameters();
		params.put(TrainingParameters.ITERATIONS_PARAM, 100);
		params.put(TrainingParameters.CUTOFF_PARAM, 1);

		nameFinderModel = NameFinderME.train("eng", null, sampleStream, params,
				TokenNameFinderFactory.create(null, null, Collections.emptyMap(), new BioCodec()));

		BufferedOutputStream modelOut = null;
		try {
			modelOut = new BufferedOutputStream(new FileOutputStream("f24_sec1modalspace.bin"));
			nameFinderModel.serialize(modelOut);
		} finally {
			if (modelOut != null)
				modelOut.close();
		}
		 }
		 r.close();
		return nameFinderModel;
	}

	@SuppressWarnings("unused")
	private TokenNameFinderModel f2422Train() throws IOException, InvalidFormatException {
		String encoding = "ISO-8859-1";
		TokenNameFinderModel nameFinderModel;
		 FileInputStream r = new FileInputStream("f24_sec2modalspace.bin");
		
		 if(r!=null){
		 nameFinderModel = new TokenNameFinderModel(r);
		 }else {
		ObjectStream<NameSample> sampleStream =  new NameSampleDataStream(new PlainTextByLineStream(
					new MockInputStreamFactory(new File("section2trainingnewspace_result4.txt")), encoding));

		TrainingParameters params = new TrainingParameters();
		params.put(TrainingParameters.ITERATIONS_PARAM, 100);
		params.put(TrainingParameters.CUTOFF_PARAM, 1);

		nameFinderModel = NameFinderME.train("eng", null, sampleStream, params,
				TokenNameFinderFactory.create(null, null, Collections.emptyMap(), new BioCodec()));

		BufferedOutputStream modelOut = null;
		try {
			modelOut = new BufferedOutputStream(new FileOutputStream("f24_sec2modalspace.bin"));
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
	 * Train NamefinderME using OnlyWithNamesWithTypes.train. The goal is to
	 * check if the model validator accepts it. This is related to the issue
	 * OPENNLP-9
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
	 * Train NamefinderME using voa1.train with several nameTypes and try the
	 * model in a sample text.
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



}
