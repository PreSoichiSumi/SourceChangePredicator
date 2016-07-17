package yousei;

import org.junit.Test;
import yousei.util.ClangAstElementCounter;

import java.util.Map;

public class ClangAstElementCounterTest {

    @Test
    public void testParseAst() throws Exception {
        String filePath="src/test/resources/gcd/gcd-ast-clang.txt";
        ClangAstElementCounter caec = new ClangAstElementCounter(filePath, "gcd.c");
        Map<String, Integer> elememts = caec.parseAst();
        elememts.forEach((k,v)-> System.out.printf("%s , %d%n",k, v));
    }
}