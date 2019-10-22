package ini.ast;

import java.util.List;

import ini.eval.function.BoundExecutable;

public class Scanner implements Visitor {

	public Scanner scan(AstNode node) {
		if (node != null) {
			node.accept(this);
		}
		return this;
	}

	public <T extends AstNode> Scanner scan(List<T> list) {
		if (list != null) {
			for (T node : list) {
				scan(node);
			}
		}
		return this;
	}

	public <T extends AstNode> Scanner scan(Sequence<T> sequence) {
		while (sequence != null) {
			scan(sequence.get());
			sequence = sequence.next();
		}
		return this;
	}

	public void visitAstElement(AstElement element) {
		scan(element.annotations);
	}

	public void visitArrayAccess(ArrayAccess arrayAccess) {
		visitAstElement(arrayAccess);
		scan(arrayAccess.indexExpression);
		scan(arrayAccess.targetExpression);
	}

	public void visitAssignment(Assignment assignment) {
		visitAstElement(assignment);
		scan(assignment.assignee);
		scan(assignment.assignment);
	}

	public void visitAtBinding(AtBinding atBinding) {
		visitAstElement(atBinding);
		scan(atBinding.configurationTypes);
		scan(atBinding.runtimeTypes);
	}

	public void visitAtPredicate(AtPredicate atPredicate) {
		visitAstElement(atPredicate);
		scan(atPredicate.outParameters);
	}

	public void visitBinaryOperator(BinaryOperator binaryOperator) {
		visitAstElement(binaryOperator);
		scan(binaryOperator.left);
		scan(binaryOperator.right);
	}

	public void visitBinding(Binding binding) {
		visitAstElement(binding);
		scan(binding.parameterTypes);
		scan(binding.returnType);
	}

	public void visitBooleanLiteral(BooleanLiteral booleanLiteral) {
		visitAstElement(booleanLiteral);
	}

	public void visitBoundExecutable(BoundExecutable boundExecutable) {
		visitAstElement(boundExecutable);
		scan(boundExecutable.binding);
		scan(boundExecutable.bindingOverloads);
		for (Parameter parameter : boundExecutable.parameters) {
			scan(parameter);
		}
	}

	public void visitCaseStatement(CaseStatement caseStatement) {
		visitAstElement(caseStatement);
		scan(caseStatement.cases);
		scan(caseStatement.defaultStatements);
	}

	public void visitChannel(Channel channel) {
		visitAstElement(channel);
		scan(channel.typeVariable);
	}

	public void visitCharLiteral(CharLiteral charLiteral) {
		visitAstElement(charLiteral);
	}

	public void visitConstructor(Constructor constructor) {
		visitAstElement(constructor);
		scan(constructor.fields);
	}

	public void visitConstructorMatchExpression(ConstructorMatchExpression constructorMatchExpression) {
		visitAstElement(constructorMatchExpression);
		scan(constructorMatchExpression.fieldMatchExpressions);
	}

	public void visitField(Field field) {
		visitAstElement(field);
	}

	public void visitFieldAccess(FieldAccess fieldAccess) {
		visitAstElement(fieldAccess);
		scan(fieldAccess.targetExpression);
	}

	public void visitFunction(Function function) {
		visitAstElement(function);
		scan(function.parameters);
		scan(function.statements);
	}

	public void visitImport(Import importStatement) {
		visitAstElement(importStatement);
	}

	public void visitInvocation(Invocation invocation) {
		visitAstElement(invocation);
		scan(invocation.arguments);
	}

	public void visitListExpression(ListExpression listExpression) {
		visitAstElement(listExpression);
		scan(listExpression.elements);
	}

	public void visitNumberLiteral(NumberLiteral numberLiteral) {
		visitAstElement(numberLiteral);
	}

	public void visitParameter(Parameter parameter) {
		visitAstElement(parameter);
		scan(parameter.defaultValue);
	}

	public void visitLTLPredicate(LTLPredicate predicate) {
		visitAstElement(predicate);
	}

	public void visitProcess(Process process) {
		visitAstElement(process);
		scan(process.parameters);
		scan(process.initRules);
		scan(process.atRules);
		scan(process.errorRules);
		scan(process.endRules);
	}

	public void visitReturnStatement(ReturnStatement returnStatement) {
		visitAstElement(returnStatement);
		scan(returnStatement.expression);
	}

	public void visitRule(Rule rule) {
		visitAstElement(rule);
		scan(rule.synchronizedAtsNames);
		scan(rule.guard);
		scan(rule.statements);
	}

	public void visitSetConstructor(SetConstructor setConstructor) {
		visitAstElement(setConstructor);
		scan(setConstructor.fieldAssignments);
	}

	public void visitSetDeclaration(SetDeclaration setDeclaration) {
		visitAstElement(setDeclaration);
		scan(setDeclaration.lowerBound);
		scan(setDeclaration.upperBound);
	}

	public void visitSetExpression(SetExpression setExpression) {
		visitAstElement(setExpression);
		scan(setExpression.set);
		scan(setExpression.expression);
	}

	public void visitStringLiteral(StringLiteral stringLiteral) {
		visitAstElement(stringLiteral);
	}

	public void visitSubArrayAccess(SubArrayAccess subArrayAccess) {
		visitAstElement(subArrayAccess);
		scan(subArrayAccess.minExpression);
		scan(subArrayAccess.maxExpression);
		scan(subArrayAccess.targetExpression);
	}

	public void visitThisLiteral(ThisLiteral thisLiteral) {
		visitAstElement(thisLiteral);
	}

	public void visitTypeVariable(TypeVariable typeVariable) {
		visitVariable(typeVariable);
		scan(typeVariable.superType);
		scan(typeVariable.component);
		scan(typeVariable.context);
	}

	public void visitUnaryOperator(UnaryOperator unaryOperator) {
		visitAstElement(unaryOperator);
		scan(unaryOperator.operand);
	}

	public void visitUserType(UserType userType) {
		visitAstElement(userType);
		scan(userType.simpleType);
		scan(userType.constructors);
	}

	public void visitVariable(Variable variable) {
		visitAstElement(variable);
	}

}
