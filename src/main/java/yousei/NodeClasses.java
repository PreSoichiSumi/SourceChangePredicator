package yousei;

import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.c.*;
import org.eclipse.cdt.core.dom.ast.gnu.*;
import org.eclipse.cdt.core.dom.ast.gnu.c.ICASTKnRFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.gnu.c.IGCCASTArrayRangeDesignator;
import org.eclipse.cdt.core.dom.ast.gnu.c.IGCCASTSimpleDeclSpecifier;
import org.eclipse.cdt.utils.IGnuToolFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by s-sumi on 2016/05/31.
 */
public class NodeClasses {
    public final Map<String,Integer> definitions=new LinkedHashMap<>();
    public NodeClasses(){
        this.definitions.put(IASTArrayDeclarator.class.getSimpleName(),0);
        this.definitions.put(IASTArrayModifier.class.getSimpleName(),1);
        this.definitions.put(IASTArraySubscriptExpression.class.getSimpleName(),2);
        this.definitions.put(IASTASMDeclaration.class.getSimpleName(),3);
        this.definitions.put(IASTAttribute.class.getSimpleName(),4);
        this.definitions.put(IASTAttributeOwner.class.getSimpleName(),5);
        this.definitions.put(IASTBinaryExpression.class.getSimpleName(),6);
        this.definitions.put(IASTBinaryTypeIdExpression.class.getSimpleName(),7);
        this.definitions.put(IASTBreakStatement.class.getSimpleName(),8);
        this.definitions.put(IASTCaseStatement.class.getSimpleName(),9);
        this.definitions.put(IASTCastExpression.class.getSimpleName(),10);
        this.definitions.put(IASTComment.class.getSimpleName(),11);
        this.definitions.put(IASTCompositeTypeSpecifier.class.getSimpleName(),12);
        this.definitions.put(IASTCompoundStatement.class.getSimpleName(),13);
        this.definitions.put(IASTConditionalExpression.class.getSimpleName(),14);
        this.definitions.put(IASTContinueStatement.class.getSimpleName(),15);
        this.definitions.put(IASTDeclaration.class.getSimpleName(),16);
        this.definitions.put(IASTDeclarationListOwner.class.getSimpleName(),17);
        this.definitions.put(IASTDeclarationStatement.class.getSimpleName(),18);
        this.definitions.put(IASTDeclarator.class.getSimpleName(),19);
        this.definitions.put(IASTDeclSpecifier.class.getSimpleName(),20);
        this.definitions.put(IASTDefaultStatement.class.getSimpleName(),21);
        this.definitions.put(IASTDoStatement.class.getSimpleName(),22);
        this.definitions.put(IASTElaboratedTypeSpecifier.class.getSimpleName(),23);
        this.definitions.put(IASTEnumerationSpecifier.class.getSimpleName(),24);
        this.definitions.put(IASTEnumerationSpecifier.IASTEnumerator.class.getSimpleName(),25);
        this.definitions.put(IASTEqualsInitializer.class.getSimpleName(),26);
        this.definitions.put(IASTExpression.class.getSimpleName(),27);
        this.definitions.put(IASTExpressionList.class.getSimpleName(),28);
        this.definitions.put(IASTExpressionStatement.class.getSimpleName(),29);
        this.definitions.put(IASTFieldReference.class.getSimpleName(),30);
        this.definitions.put(IASTForStatement.class.getSimpleName(),31);
        this.definitions.put(IASTFunctionCallExpression.class.getSimpleName(),32);
        this.definitions.put(IASTFunctionDeclarator.class.getSimpleName(),33);
        this.definitions.put(IASTFunctionDefinition.class.getSimpleName(),34);
        this.definitions.put(IASTFunctionStyleMacroParameter.class.getSimpleName(),35);
        this.definitions.put(IASTGotoStatement.class.getSimpleName(),36);
        this.definitions.put(IASTIdExpression.class.getSimpleName(),37);
        this.definitions.put(IASTIfStatement.class.getSimpleName(),38);
        this.definitions.put(IASTImplicitName.class.getSimpleName(),39);
        this.definitions.put(IASTImplicitNameOwner.class.getSimpleName(),40);
        this.definitions.put(IASTInitializer.class.getSimpleName(),41);
        this.definitions.put(IASTInitializerClause.class.getSimpleName(),42);
        this.definitions.put(IASTInitializerExpression.class.getSimpleName(),43);
        this.definitions.put(IASTInitializerList.class.getSimpleName(),44);
        this.definitions.put(IASTLabelStatement.class.getSimpleName(),45);
        this.definitions.put(IASTLiteralExpression.class.getSimpleName(),46);
        this.definitions.put(IASTName.class.getSimpleName(),47);
        this.definitions.put(IASTNamedTypeSpecifier.class.getSimpleName(),48);
        this.definitions.put(IASTNullStatement.class.getSimpleName(),49);
        this.definitions.put(IASTParameterDeclaration.class.getSimpleName(),50);
        this.definitions.put(IASTPointer.class.getSimpleName(),51);
        this.definitions.put(IASTPointerOperator.class.getSimpleName(),52);
        this.definitions.put(IASTPreprocessorElifStatement.class.getSimpleName(),53);
        this.definitions.put(IASTPreprocessorElseStatement.class.getSimpleName(),54);
        this.definitions.put(IASTPreprocessorEndifStatement.class.getSimpleName(),55);
        this.definitions.put(IASTPreprocessorErrorStatement.class.getSimpleName(),56);
        this.definitions.put(IASTPreprocessorFunctionStyleMacroDefinition.class.getSimpleName(),57);
        this.definitions.put(IASTPreprocessorIfdefStatement.class.getSimpleName(),58);
        this.definitions.put(IASTPreprocessorIfndefStatement.class.getSimpleName(),59);
        this.definitions.put(IASTPreprocessorIfStatement.class.getSimpleName(),60);
        this.definitions.put(IASTPreprocessorIncludeStatement.class.getSimpleName(),61);
        this.definitions.put(IASTPreprocessorMacroDefinition.class.getSimpleName(),62);
        this.definitions.put(IASTPreprocessorMacroExpansion.class.getSimpleName(),63);
        this.definitions.put(IASTPreprocessorObjectStyleMacroDefinition.class.getSimpleName(),64);
        this.definitions.put(IASTPreprocessorPragmaStatement.class.getSimpleName(),65);
        this.definitions.put(IASTPreprocessorStatement.class.getSimpleName(),66);
        this.definitions.put(IASTPreprocessorUndefStatement.class.getSimpleName(),67);
        this.definitions.put(IASTProblem.class.getSimpleName(),68);
        this.definitions.put(IASTProblemDeclaration.class.getSimpleName(),69);
        this.definitions.put(IASTProblemExpression.class.getSimpleName(),70);
        this.definitions.put(IASTProblemHolder.class.getSimpleName(),71);
        this.definitions.put(IASTProblemStatement.class.getSimpleName(),72);
        this.definitions.put(IASTProblemTypeId.class.getSimpleName(),73);
        this.definitions.put(IASTReturnStatement.class.getSimpleName(),74);
        this.definitions.put(IASTSimpleDeclaration.class.getSimpleName(),75);
        this.definitions.put(IASTSimpleDeclSpecifier.class.getSimpleName(),76);
        this.definitions.put(IASTStandardFunctionDeclarator.class.getSimpleName(),77);
        this.definitions.put(IASTStatement.class.getSimpleName(),78);
        this.definitions.put(IASTSwitchStatement.class.getSimpleName(),79);
        this.definitions.put(IASTToken.class.getSimpleName(),80);
        this.definitions.put(IASTTokenList.class.getSimpleName(),81);
        this.definitions.put(IASTTranslationUnit.class.getSimpleName(),82);
        this.definitions.put(IASTTypeId.class.getSimpleName(),83);
        this.definitions.put(IASTTypeIdExpression.class.getSimpleName(),84);
        this.definitions.put(IASTTypeIdInitializerExpression.class.getSimpleName(),85);
        this.definitions.put(IASTUnaryExpression.class.getSimpleName(),86);
        this.definitions.put(IASTWhileStatement.class.getSimpleName(),87);
        this.definitions.put(ICASTArrayDesignator.class.getSimpleName(),88);
        this.definitions.put(ICASTDesignatedInitializer.class.getSimpleName(),89);//
        this.definitions.put(ICASTArrayModifier.class.getSimpleName(),90);
        this.definitions.put(ICASTCompositeTypeSpecifier.class.getSimpleName(),91);
        this.definitions.put(ICASTDeclSpecifier.class.getSimpleName(),92);//
        this.definitions.put(ICASTDesignator.class.getSimpleName(),93);//
        this.definitions.put(ICASTElaboratedTypeSpecifier.class.getSimpleName(),94);
        this.definitions.put(ICASTEnumerationSpecifier.class.getSimpleName(),95);
        this.definitions.put(ICASTFieldDesignator.class.getSimpleName(),96);
        this.definitions.put(ICASTKnRFunctionDeclarator.class.getSimpleName(),97);
        this.definitions.put(ICASTPointer.class.getSimpleName(),98);
        this.definitions.put(ICASTSimpleDeclSpecifier.class.getSimpleName(),99);
        this.definitions.put(ICASTTypedefNameSpecifier.class.getSimpleName(),100);
        this.definitions.put(ICASTTypeIdInitializerExpression.class.getSimpleName(),101);
        this.definitions.put(IGCCASTArrayRangeDesignator.class.getSimpleName(),102);//skipCPP
        this.definitions.put(IGCCASTAttributeSpecifier.class.getSimpleName(),103);
        this.definitions.put(IGCCASTSimpleDeclSpecifier.class.getSimpleName(),104);
        this.definitions.put(IGNUASTCompoundStatementExpression.class.getSimpleName(),105);
        this.definitions.put(IGNUASTGotoStatement.class.getSimpleName(),106);
        this.definitions.put(IGNUASTTypeIdExpression.class.getSimpleName(),107);
        this.definitions.put(IGNUASTUnaryExpression.class.getSimpleName(),108);
    }
}
