package yousei;

/**
 * @author s-sumi
 */
public class Main {
	public static final String reposPath="testdata/thrift";
	public static void main(String[] args)throws Exception{
		RepositoryAnalyzer ra=new RepositoryAnalyzer(reposPath);
		ra.analyzeRepository();
		System.out.println("fin");
	}

	/*
		String filePath="testdata/hello.c";
		String classPath="lib";
		String outputPath="";
		CppSourceAnalyzer analyzer=new CppSourceAnalyzer(filePath,classPath,outputPath);
		analyzer.analyzeFile();
	*/

}
