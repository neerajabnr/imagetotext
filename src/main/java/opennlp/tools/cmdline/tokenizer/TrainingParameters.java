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

package opennlp.tools.cmdline.tokenizer;

import opennlp.tools.cmdline.BasicTrainingParameters;
import opennlp.tools.cmdline.CmdLineUtil;

/**
 * This class is responsible to parse and provide the training parameters.
 */
class TrainingParameters extends BasicTrainingParameters {

  private boolean isAlphaNumOpt = false;
  
  TrainingParameters(String args[]) {
    super(args);
    
    isAlphaNumOpt = CmdLineUtil.containsParam("-alphaNumOpt", args);
  }
  
  /**
   * Retrieves the optional alphaNumOpt parameter.
   * 
   * @return if parameter is set true, otherwise false (default)
   */
  boolean isAlphaNumericOptimizationEnabled() {
    return isAlphaNumOpt;
  }
  
  public static String getParameterUsage() {
    return BasicTrainingParameters.getParameterUsage() + " [-alphaNumOpt]";
  }
  
  public static String getDescription() {
    return BasicTrainingParameters.getDescription();
    // TODO: add alphaNumOpt description
  }
}
