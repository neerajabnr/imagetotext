/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package opennlp.tools.namefind;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import opennlp.tools.formats.ResourceAsStreamFactory;
import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.tokenize.TokenSample;
import opennlp.tools.tokenize.TokenSampleStream;
import opennlp.tools.tokenize.TokenizerFactory;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.tokenize.TokenizerTestUtil;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.MockInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;

/**
 * This is the test class for {@link NameFinderME}.
 * <p>
 * A proper testing and evaluation of the name finder is only possible with a
 * large corpus which contains a huge amount of test sentences.
 * <p>
 * The scope of this test is to make sure that the name finder code can be
 * executed. This test can not detect mistakes which lead to incorrect feature
 * generation or other mistakes which decrease the tagging performance of the
 * name finder.
 * <p>
 * In this test the {@link NameFinderME} is trained with a small amount of
 * training sentences and then the computed model is used to predict sentences
 * from the training sentences.
 */
public class NameFinderMETest {

	private static final String TYPE_OVERRIDE = "aType";
	private static final String DEFAULT = "default";

	private static TokenNameFinderModel nameFinder1 = null;
	private static TokenNameFinderModel nameFinder2 = null;

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
			// System.out.println(name.toString() + " " +
			// sentence2[name.getStart()]);
			for (int i = name.getStart(); i < name.getEnd(); i++) {
				System.out.print(sentence2[i] + " ");
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
			}
			System.out.println();
		}

	}

	@Test
	public void f24_2() throws Exception {
		TokenNameFinderModel nameFinderModel = f242Train(0);
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
			// System.out.println(name.toString() + " " +
			// sentence2[name.getStart()]);
			for (int i = name.getStart(); i < name.getEnd(); i++) {
				System.out.print(sentence2[i] + " ");
			}
			System.out.println();
		}

	}

	@Test
	public void f24_22() throws Exception {
		TokenNameFinderModel nameFinderModel = f2422Train(0);
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
			// System.out.println(name.toString() + " " +
			// sentence2[name.getStart()]);
			for (int i = name.getStart(); i < name.getEnd(); i++) {
				System.out.print(sentence2[i] + " ");
			}
			System.out.println();
		}

	}

	public Map<String, String> f24_section1(String sentence, int space_count) throws Exception {
		if (nameFinder1 == null)
			nameFinder1 = f242Train(space_count);
		TokenizerModel model = NameFinderMETest.createMaxentTokenModel();

		TokenizerME tokenizer = new TokenizerME(model);
		String[] sentence2 = tokenizer.tokenize(sentence);
		NameFinderME nameFinder = new NameFinderME(nameFinder1);

		Map<String, String> valMap = new HashMap<>();
		Span[] names2 = nameFinder.find(sentence2);
		for (Span name : names2) {
			// if(name.getType().equals("templateERARIO1")) {
			// getVauesFromTemplate(sentence2, name.getType());
			// }
			System.out.print(name.getType() + " ");
			// System.out.println(name.toString() + " " +
			// sentence2[name.getStart()]);
			for (int i = name.getStart(); i < name.getEnd(); i++) {
				int c=1;
				System.out.print(sentence2[i] + " ");
				
				String temp = name.getType();
				if (valMap.containsKey(temp)) {
					temp = temp + c;
					valMap.put(temp, sentence2[i]);
					c++;
				} else {
					valMap.put(name.getType(), sentence2[i]);
				}
			}
			System.out.println();
		}
		return valMap;

	}

	public Map<String, String> f24_section2(String sentence, int space_count) throws Exception {
		System.out.println("Printing Sentence:" + sentence);
		if (nameFinder2 == null)
			nameFinder2 = f2422Train(space_count);
		TokenizerModel model = NameFinderMETest.createMaxentTokenModel();

		TokenizerME tokenizer = new TokenizerME(model);
		String[] sentence2 = tokenizer.tokenize(sentence);
		NameFinderME nameFinder = new NameFinderME(nameFinder2);

		Span[] names2 = nameFinder.find(sentence2);
		Map<String, String> valMap = new HashMap<>();
		int k = 1;
		for (Span name : names2) {
			// if(name.getType().equals("templateERARIO1")) {
			// getVauesFromTemplate(sentence2, name.getType());
			// }
			System.out.print(name.getType() + " ");

			// System.out.println(name.toString() + " " +
			// sentence2[name.getStart()]);
			for (int i = name.getStart(); i < name.getEnd(); i++) {
				int c = 1;
				System.out.print(sentence2[i] + " ");
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
			System.out.println();
		}
		return valMap;

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
	private TokenNameFinderModel f242Train(int space_count) throws IOException, InvalidFormatException {
		String encoding = "ISO-8859-1";
		TokenNameFinderModel nameFinderModel;
//		 FileInputStream r = new FileInputStream("f24_sec1modal.bin");
//		
//		 if(r!=null){
//		 nameFinderModel = new TokenNameFinderModel(r);
//		 }else {

		ObjectStream<NameSample> sampleStream = null;
		if (space_count >= 59 && space_count <= 70) {
			sampleStream = new NameSampleDataStream(new PlainTextByLineStream(
					new MockInputStreamFactory(new File("section1trainingnew_result.txt")), encoding));
		} else {
			sampleStream = new NameSampleDataStream(new PlainTextByLineStream(
					new MockInputStreamFactory(new File("section1trainingnewspace_result.txt")), encoding));
		}

		TrainingParameters params = new TrainingParameters();
		params.put(TrainingParameters.ITERATIONS_PARAM, 100);
		params.put(TrainingParameters.CUTOFF_PARAM, 1);

		nameFinderModel = NameFinderME.train("eng", null, sampleStream, params,
				TokenNameFinderFactory.create(null, null, Collections.emptyMap(), new BioCodec()));

		BufferedOutputStream modelOut = null;
		try {
			modelOut = new BufferedOutputStream(new FileOutputStream("f24_sec1modal.bin"));
			nameFinderModel.serialize(modelOut);
		} finally {
			if (modelOut != null)
				modelOut.close();
		}
//		 }
//		 r.close();
		return nameFinderModel;
	}

	@SuppressWarnings("unused")
	private TokenNameFinderModel f2422Train(int space_count) throws IOException, InvalidFormatException {
		String encoding = "ISO-8859-1";
		TokenNameFinderModel nameFinderModel;
//		 FileInputStream r = new FileInputStream("f24_sec2modal.bin");
//		
//		 if(r!=null){
//		 nameFinderModel = new TokenNameFinderModel(r);
//		 }else {
		ObjectStream<NameSample> sampleStream = null;
		if (space_count >= 59 && space_count <= 70) {

			sampleStream = new NameSampleDataStream(new PlainTextByLineStream(
					new MockInputStreamFactory(new File("section2trainingnew_result.txt")), encoding));
		} else {
			sampleStream = new NameSampleDataStream(new PlainTextByLineStream(
					new MockInputStreamFactory(new File("section2trainingnewspace_result.txt")), encoding));
		}

		TrainingParameters params = new TrainingParameters();
		params.put(TrainingParameters.ITERATIONS_PARAM, 100);
		params.put(TrainingParameters.CUTOFF_PARAM, 1);

		nameFinderModel = NameFinderME.train("eng", null, sampleStream, params,
				TokenNameFinderFactory.create(null, null, Collections.emptyMap(), new BioCodec()));

		BufferedOutputStream modelOut = null;
		try {
			modelOut = new BufferedOutputStream(new FileOutputStream("f24_sec2modal.bin"));
			nameFinderModel.serialize(modelOut);
		} finally {
			if (modelOut != null)
				modelOut.close();
		}
//		 }
//		 r.close();
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
