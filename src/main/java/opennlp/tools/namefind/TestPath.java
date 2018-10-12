package opennlp.tools.namefind;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import opennlp.tools.util.MockInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

public class TestPath {
	public static void main(String[] args) throws IOException {
		TestPath path=new TestPath();
		path.test();
	}
	public void test() throws IOException{
		String encoding = "ISO-8859-1"; 
		
		ObjectStream<NameSample> sampleStream = new NameSampleDataStream(new PlainTextByLineStream(
				new MockInputStreamFactory(new File("opennlp/tools/namefind/section1training_result.txt")), encoding));
	}

}
