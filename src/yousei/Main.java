package yousei;

public class Main {
	public static void main(String[] args)throws Exception{
		String filePath="testdata/hello.c";
		String classPath="lib";
		String outputPath="";
		CppSourceAnalyzer analyzer=new CppSourceAnalyzer(filePath,classPath,outputPath);
		analyzer.analyzeFile();
	}

}
