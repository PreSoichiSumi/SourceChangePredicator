package yousei;

import java.io.FileReader;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.gnu.c.GCCLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.DefaultLogService;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.core.parser.ScannerInfo;
//import org.eclipse.*;
public class CppSourceAnalyzer {
	public String filePath;
	public String classPath;
	public String outputPath;

	public CppSourceAnalyzer(String filePath, String classPath,
			String outputPath) {
		super();
		this.filePath = filePath;
		this.classPath = classPath;
		this.outputPath = outputPath;
	}
	public int[] analyzeFile()throws Exception{
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
		return new int[2];
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

}
