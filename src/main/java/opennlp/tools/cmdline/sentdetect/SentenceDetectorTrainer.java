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

package opennlp.tools.cmdline.sentdetect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import opennlp.tools.cmdline.CLI;
import opennlp.tools.cmdline.CmdLineTool;
import opennlp.tools.cmdline.CmdLineUtil;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.sentdetect.SentenceSample;
import opennlp.tools.sentdetect.SentenceSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

public class SentenceDetectorTrainer {

  public String getName() {
    return "SentenceDetectorTrainer";
  }
  
  public String getShortDescription() {
    return "trainer for the learnable sentence detector";
  }
  
  public String getHelp() {
    return "Usage: " + CLI.CMD + " " + getName() + " " + TrainingParameters.getParameterUsage() +
        " trainingData model";
  }

  public void run(String[] args) {/*
    if (args.length < 6) {
      System.out.println(getHelp());
      System.exit(1);
    }
    
    TrainingParameters parameters = new TrainingParameters(args);
    
    if(!parameters.isValid()) {
      System.out.println(getHelp());
      System.exit(1);
    }
    
    File trainingDataInFile = new File(args[args.length - 2]);
    CmdLineUtil.checkInputFile("Training Data", trainingDataInFile);
    
    try {
      FileInputStream trainingDataIn = new FileInputStream(trainingDataInFile);
      ObjectStream<String> lineStream = new PlainTextByLineStream(trainingDataIn.getChannel(),
          parameters.getEncoding());
      ObjectStream<SentenceSample> sampleStream = new SentenceSampleStream(lineStream);
      
      SentenceModel model = SentenceDetectorME.train(parameters.getLanguage(), sampleStream, true, null);
      
      sampleStream.close();
      
      File modelOutFile = new File(args[args.length - 1]);
      OutputStream modelOut = new FileOutputStream(modelOutFile);
      model.serialize(modelOut);
      modelOut.close();
      
      System.out.println("Wrote sentence detector model.");
      System.out.println("Path: " + modelOutFile.getAbsolutePath());
    } catch (Exception e) {
      e.printStackTrace();
    }
  */}
}
