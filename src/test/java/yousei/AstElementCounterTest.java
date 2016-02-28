package yousei;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.gnu.c.GCCLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.*;
import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;

public class AstElementCounterTest {
    @Test
    public void test() throws IOException, CoreException {
        String filePath = "src/test/resources/gcd/gcd.c";
        String classPath = "lib";
        String outputPath = "";

        StringBuilder source = new StringBuilder();
        try (FileReader fr = new FileReader(filePath)) {
            int c;
            while ((c = fr.read()) >= 0) {
                source.append((char) c);
            }
        }

        ILanguage language = GCCLanguage.getDefault();
        FileContent reader = FileContent.create(filePath, source.toString().toCharArray());

        Map<String, String> macroDefinitions = null;
        String[] includeSearchPath = null;
        IScannerInfo scanInfo = new ScannerInfo(macroDefinitions, includeSearchPath);

        IncludeFileContentProvider fileCreator = IncludeFileContentProvider.getEmptyFilesProvider();
        IIndex index = null;
        int options = 0;
        IParserLogService log = new DefaultLogService();

        IASTTranslationUnit translationUnit = language.getASTTranslationUnit(reader, scanInfo, fileCreator, index, options, log);
        AstElementCounter counter = new AstElementCounter();
        translationUnit.accept(counter);
        Map<String, Integer> expected = elementsByYouseiVisitor();
        assertEquals(counter.elements.size(), expected.size());
        for (Map.Entry<Class<? extends IASTNode>, Integer> i : counter.elements.entrySet()) {
            String name = i.getKey().getSimpleName();
            assertTrue(expected.containsKey(name));
            assertEquals(expected.get(name), i.getValue());
        }
    }

    Map<String, Integer> elementsByYouseiVisitor() throws IOException, CoreException {
        String filePath = "src/test/resources/gcd/gcd.c";
        StringBuilder source = new StringBuilder();
        try(FileReader fr = new FileReader(filePath)) {
            int c;
            while ((c = fr.read()) >= 0) {
                source.append((char)c);
            }
        }

        ILanguage language = GCCLanguage.getDefault();

        FileContent reader = FileContent.create(filePath, source.toString().toCharArray());

        Map<String, String> macroDefinitions = null;
        String[] includeSearchPath = null;
        IScannerInfo scanInfo = new ScannerInfo(macroDefinitions, includeSearchPath );

        IncludeFileContentProvider fileCreator = IncludeFileContentProvider.getEmptyFilesProvider();
        IIndex index = null;
        int options = 0;
        IParserLogService log = new DefaultLogService();

        IASTTranslationUnit translationUnit = language.getASTTranslationUnit(reader, scanInfo, fileCreator, index, options, log);
        CppSourceVisitor cppSourceVisitor=new CppSourceVisitor();
        translationUnit.accept(cppSourceVisitor);
        return cppSourceVisitor.counter;
    }

}