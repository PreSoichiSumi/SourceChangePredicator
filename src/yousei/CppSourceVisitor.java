package yousei;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;

public class CppSourceVisitor extends ASTVisitor {
	public CppSourceVisitor() {
		// TODO 自動生成されたコンストラクター・スタブ
		super(true);
	}
	@Override
	public int visit(IASTDeclarator decl){
		System.out.println("decl");
		return ASTVisitor.PROCESS_CONTINUE;
	}
	@Override
	public int visit(IASTName name){
		System.out.println("name");
		return ASTVisitor.PROCESS_CONTINUE;
	}
}
