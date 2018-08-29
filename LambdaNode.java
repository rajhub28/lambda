import java.util.*;

public class LambdaNode {
  String nodeType; // "num", "var", "op", "lambda", "app"
  double numValue; 
  String opValue;
  String variable;
  LambdaNode child1;
  LambdaNode child2;
  // Phase 2 variables
  Set<String> freeVariables = new HashSet<String>();

  void setNodeType(String t) {
    nodeType = t;
  }

  void setNumValue(double d) {
    numValue = d;
  }

  void setOpValue(String op) {
    opValue = op;
  }

  void setVariable(String var) {
    variable = var;
  }

  void setChild1(LambdaNode n) {
    child1 = n;
  }

  void setChild2(LambdaNode n) {
    child2 = n;
  }

  void addFreeVariable(String v) {
    freeVariables.add(v);
  }

  void addFreeVariables(Set<String> s) {
    freeVariables.addAll(s);
  }

  void removeFreeVariable(String v) {
    freeVariables.remove(v);
  }

  void setFreeVariables(Set<String> s) {
    freeVariables = s;
  }

  String getNodeType() {
    return nodeType;
  }

  double getNumValue() {
    return numValue;
  }

  String getOpValue() {
    return opValue;
  }

  String getVariable() {
    return variable;
  }

  LambdaNode getChild1() {
    return child1;
  }

  LambdaNode getChild2() {
    return child2;
  }

  Set<String> getFreeVariables() {
    return freeVariables;
  }

}
