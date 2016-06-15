package yousei;

import org.eclipse.cdt.internal.core.dom.parser.c.*;

import org.eclipse.cdt.internal.core.dom.parser.cpp.*;

import java.util.LinkedHashMap;
import java.util.Map;
//astnodeの種類はhttps://www.cct.lsu.edu/~rguidry/eclipse-doc36/org/eclipse/cdt/internal/core/dom/parser/ASTNode.html
//からC,CPPがついたもののみを抜き出した
//c99はないっぽい？

/**
 * Created by s-sumi on 2016/05/31.
 */
public class NodeClasses {
    public final Map<String, Integer> dictionary = new LinkedHashMap<>();

    public NodeClasses() {
        this.dictionary.put(CASTArrayDesignator.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTArrayModifier.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTArrayRangeDesignator.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTArraySubscriptExpression.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTASMDeclaration.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTBaseDeclSpecifier.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTBinaryExpression.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTBreakStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTCaseStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTCastExpression.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTCompoundStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTCompoundStatementExpression.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTConditionalExpression.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTContinueStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTDeclarationStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTDeclarator.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTDefaultStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTDesignatedInitializer.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTDoStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTExpressionList.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTExpressionStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTFieldDesignator.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTFieldReference.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTForStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTFunctionCallExpression.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTFunctionDefinition.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTGotoStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTIdExpression.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTIfStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTInitializerList.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTLabelStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTLiteralExpression.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTName.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTNullStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTParameterDeclaration.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTPointer.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTProblemDeclaration.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTProblemExpression.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTProblemStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTReturnStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTSimpleDeclaration.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTSwitchStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTTypeId.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTTypeIdExpression.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTUnaryExpression.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CASTWhileStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTArrayModifier.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTArraySubscriptExpression.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTASMDeclaration.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTBaseDeclSpecifier.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTBaseSpecifier.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTBinaryExpression.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTBreakStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTCaseStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTCastExpression.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTCatchHandler.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTCompoundStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTCompoundStatementExpression.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTConditionalExpression.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTConstructorChainInitializer.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTConstructorInitializer.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTContinueStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTDeclarationStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTDeclarator.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTDefaultStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTDeleteExpression.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTDoStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTExplicitTemplateInstantiation.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTExpressionList.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTExpressionStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTFieldReference.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTForStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTFunctionCallExpression.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTFunctionDefinition.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTGotoStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTIdExpression.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTIfStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTInitializerList.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTLabelStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTLinkageSpecification.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTLiteralExpression.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTNameBase.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTNamespaceAlias.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTNamespaceDefinition.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTNewExpression.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTNullStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTPackExpansionExpression.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTParameterDeclaration.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTPointer.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTProblemDeclaration.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTProblemExpression.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTProblemStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTProblemTypeId.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTReferenceOperator.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTReturnStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTSimpleDeclaration.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTSimpleTypeConstructorExpression.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTSimpleTypeTemplateParameter.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTStaticAssertionDeclaration.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTSwitchStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTTemplateDeclaration.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTTemplatedTypeTemplateParameter.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTTemplateSpecialization.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTTryBlockStatement.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTTypeId.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTTypeIdExpression.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTUnaryExpression.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTUsingDeclaration.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTUsingDirective.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTVisibilityLabel.class.getSimpleName(), this.dictionary.size());
        this.dictionary.put(CPPASTWhileStatement.class.getSimpleName(), this.dictionary.size());
    }
}
