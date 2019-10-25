package ini.ast;

import ini.eval.function.BoundExecutable;

public interface Visitor {

	void visitAstElement(AstElement element);

	void visitArrayAccess(ArrayAccess arrayAccess);

	void visitAssignment(Assignment assignment);
	
	void visitAtBinding(AtBinding atBinding);
	
	void visitAtPredicate(AtPredicate atPredicate);
	
	void visitBinaryOperator(BinaryOperator binaryOperator);
	
	void visitBinding(Binding binding);
	
	void visitBooleanLiteral(BooleanLiteral booleanLiteral);

	void visitBoundExecutable(BoundExecutable boundExecutable);
	
	void visitCaseStatement(CaseStatement caseStatement);
	
	void visitChannel(Channel channel);
	
	void visitCharLiteral(CharLiteral charLiteral);
	
	void visitConditionalExpression(ConditionalExpression conditionalExpression);

	void visitConstructor(Constructor constructor);

	void visitConstructorMatchExpression(ConstructorMatchExpression constructorMatchExpression);

	void visitField(Field field);

	void visitFieldAccess(FieldAccess fieldAccess);

	void visitFunction(Function function);

	void visitImport(Import importStatement);

	void visitInvocation(Invocation invocation);

	void visitListExpression(ListExpression listExpression);

	void visitNumberLiteral(NumberLiteral numberLiteral);

	void visitParameter(Parameter parameter);

	void visitLTLPredicate(LTLPredicate predicate);

	void visitProcess(Process process);

	void visitReturnStatement(ReturnStatement returnStatement);

	void visitRule(Rule rule);

	void visitSetConstructor(SetConstructor constructor);

	void visitSetDeclaration(SetDeclaration setDeclaration);

	void visitSetExpression(SetExpression setExpression);

	void visitStringLiteral(StringLiteral stringLiteral);

	void visitSubArrayAccess(SubArrayAccess subArrayAccess);
	
	void visitThisLiteral(ThisLiteral thisLiteral);

	void visitTypeVariable(TypeVariable typeVariable);
	
	void visitUnaryOperator(UnaryOperator unaryOperator);
	
	void visitUserType(UserType userType);
	
	void visitVariable(Variable variable);
	
}

