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

package opennlp.tools.cmdline.postag;

import opennlp.tools.cmdline.BasicTrainingParameters;
import opennlp.tools.cmdline.CmdLineUtil;
import opennlp.tools.util.ModelType;

class TrainingParameters extends BasicTrainingParameters {

  
  private final String dictPath;
  private final ModelType model;
  
  TrainingParameters(String args[]) {
    super(args);
    
    dictPath = CmdLineUtil.getParameter("-dict", args);
    String modelString = CmdLineUtil.getParameter("-model", args);
    
    if (modelString == null)
      modelString = "maxent";
    
    if (modelString.equals("maxent")) {
      model = ModelType.MAXENT; 
    }
    else if (modelString.equals("perceptron")) {
      model = ModelType.PERCEPTRON; 
    }
    else if (modelString.equals("perceptron_sequence")) {
      model = ModelType.PERCEPTRON_SEQUENCE; 
    }
    else {
      model = null;
    }
  }
  
  ModelType getModel() {
    return model;
  }
  
  String getDictionaryPath() {
    return dictPath;
  }
  
  /*public boolean isValid() {
    
    if (model == null)
      return false;
    
    return super.isValid();
  }*/
}
