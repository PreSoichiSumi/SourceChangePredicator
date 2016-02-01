package yousei;

import org.junit.Test;

import static org.junit.Assert.*;

public class CppSourceAnalyzerTest {

    @Test
    public void testAnalyzeFile() throws Exception {
        String filePath="src/test/resources/gcd/gcd.c";
        String classPath="lib";
        String outputPath="";
        CppSourceAnalyzer analyzer=new CppSourceAnalyzer(filePath,classPath,outputPath);
        analyzer.analyzeFile();
    }
}