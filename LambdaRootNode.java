public class LambdaRootNode {
  String nodeType; // "expr", "subst", "free", "def"
  LambdaNode exprValue;
  String variable;
  LambdaNode variableValue;

  void setNodeType(String t) {
    nodeType = t;
  }

  void setExprValue(LambdaNode n) {
    exprValue = n;
  }

  void setVariable(String var) {
    variable = var;
  }

  void setVariableValue(LambdaNode n) {
    variableValue = n;
  }

  String getNodeType() {
    return nodeType;
  }

  LambdaNode getExprValue() {
    return exprValue;
  }

  String getVariable() {
    return variable;
  }

  LambdaNode getVariableValue() {
    return variableValue;
  }

}
