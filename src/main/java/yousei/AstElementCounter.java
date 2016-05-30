package yousei;

import org.eclipse.cdt.core.dom.ast.ASTGenericVisitor;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.internal.core.dom.parser.ASTAmbiguousNode;

import java.util.HashMap;
import java.util.Map;

public class AstElementCounter extends ASTGenericVisitor {
    public final Map<String, Integer> elements = new HashMap<>();

    public AstElementCounter() {
        super(true);
        this.shouldVisitAmbiguousNodes = true;
    }

    @Override
    protected int genericVisit(IASTNode node) {
        elements.merge(node.getClass().getSimpleName(), 1, (a, b) -> a + b);
        return ASTVisitor.PROCESS_CONTINUE;
    }

    @Override
    public int visit(ASTAmbiguousNode node) {
        elements.merge(node.getClass().getSimpleName(), 1, (a, b) -> a + b);
        for (IASTNode child : node.getNodes()) {
            child.accept(this);
        }
        return ASTVisitor.PROCESS_CONTINUE;
    }
}
