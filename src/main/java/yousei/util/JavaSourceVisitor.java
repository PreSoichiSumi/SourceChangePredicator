package yousei.util;

import java.util.*;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * @author s-sumi
 */
public class JavaSourceVisitor extends ASTVisitor {

    public final List<Integer> vector;

    /**
     * コンストラクタ
     */
    public JavaSourceVisitor() {
        this.vector = new ArrayList<>(93);
        Collections.fill(this.vector,0);
    }

    @Override
    public void preVisit(ASTNode node) {
        final int nodeType = node.getNodeType();
        this.vector.set(nodeType,this.vector.get(nodeType)+1);
    }

}
