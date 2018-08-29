import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import java.io.*;
import java.util.*;
import java.lang.*;

public class Lambda {

  static boolean reduceApplied = false;
  static boolean evalError = false;
  static int newVarCount = 0;
  static Map<String,LambdaNode> defs = new HashMap<String,LambdaNode>();

  static public void main(String argv[]) {    
    System.out.print("LAMBDA> ");
    do {
      String input = readInput().trim();
      if (input.equals("exit")) 
        break;
      else input += ";";
      try {
        newVarCount = 0;
        CharStream in = CharStreams.fromString(input);
        LambdaLexer lexer = new LambdaLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LambdaParser parser = new LambdaParser(tokens);
        parser.setErrorHandler(new BailErrorStrategy());
        LambdaRootNode rtree = (LambdaRootNode) parser.exprStart().value;
        if (rtree.getNodeType().equals("subst")) {
          populateFreeVariables(rtree.getExprValue());
          populateFreeVariables(rtree.getVariableValue());
          LambdaNode newTerm = 
            applySubstitution(rtree.getExprValue(),rtree.getVariable(),rtree.getVariableValue());
          Integer level = new Integer(1);
          //displayTree(newTerm,level);
          displayFlatTree(newTerm);
          System.out.println();
        }
        else if (rtree.getNodeType().equals("free")) {
          populateFreeVariables(rtree.getExprValue());
          System.out.println("Free Variables: "+rtree.getExprValue().getFreeVariables());
        }
        else if (rtree.getNodeType().equals("def")) {
          String fname = rtree.getVariable();
          LambdaNode n = rtree.getExprValue();
          if (defs.get(fname) == null) {
            defs.put(fname,n);
            System.out.println(fname+" DEFINED");
          }
          else
            System.out.println("Definition for "+fname+" already exists!");
        }
        else if (rtree.getNodeType().equals("showdefs")) {
          for (Map.Entry<String,LambdaNode> entry : defs.entrySet()) {
            System.out.print(entry.getKey()+" = ");
            displayFlatTree(entry.getValue());
            System.out.println();
          }
        }
        else { // evaluate the lambda expression
          // phase 2
          LambdaNode tree = rtree.getExprValue();
          populateFreeVariables(tree);
          //displayFlatTree(tree);
          //System.out.println();
          replaceDefs(tree);
          displayFlatTree(tree);
          System.out.println();
          //Integer level = new Integer(1);
          //displayTree(tree,level);
          do {
            reduceApplied = false;
            tree = reduceLambda(tree);
            if (!reduceApplied)
              break;
            displayFlatTree(tree);
            System.out.println();
          } while (reduceApplied);
          LambdaNode answer = mathEval(tree);
          displayFlatTree(answer);
          System.out.println();
          //if (evalError)
          //  System.out.println("\nEVALUATION ERROR\n");
          //else
          //  System.out.println("\nThe value is "+answer+"\n");
        }
      } catch (Exception e) {
          System.out.println("\nSYNTAX ERROR\n");
          e.printStackTrace();
        }
    } while (true); 
  }

  static LambdaNode reduceLambda(LambdaNode t) {
    if (reduceApplied)
      return t;
    if (t.getNodeType().equals("num") ||
        t.getNodeType().equals("var"))
      return t;
    if (t.getNodeType().equals("op")) {
      LambdaNode n = reduceLambda(t.getChild1());
      if (reduceApplied) {
        t.setChild1(n);
        return t;
      }
      n = reduceLambda(t.getChild2());
      if (reduceApplied)
        t.setChild2(n);
      return t;
    }
    else if (t.getNodeType().equals("lambda")) {
      LambdaNode n = reduceLambda(t.getChild1());
      if (reduceApplied)
        t.setChild1(n);
      return t;
    }
    else { // Must be app
      LambdaNode n = reduceLambda(t.getChild1());
      if (reduceApplied) {
        t.setChild1(n);
        return t;
      }
      if (t.getChild1().getNodeType().equals("lambda")) {
        n = reduceLambda(t.getChild2());
        if (reduceApplied) {
          t.setChild2(n);
          return t;
        }
        LambdaNode newNode = applySubstitution(t.getChild1().getChild1(),t.getChild1().getVariable(),n);
        reduceApplied = true;
        return newNode;
      }
      else {
        n = reduceLambda(t.getChild1());
        if (reduceApplied) {
          t.setChild1(n);
          return t;
        }
        n = reduceLambda(t.getChild2());
        if (reduceApplied)
          t.setChild2(n);
        return t;
      }
    }
  }

  static LambdaNode mathEval(LambdaNode t) {
    if (t.getNodeType().equals("num")) {
      return t;
    }
    else if (t.getNodeType().equals("op")) {
      t.setChild1(mathEval(t.getChild1()));
      t.setChild2(mathEval(t.getChild2()));
      if (t.getChild1().getNodeType().equals("num") && t.getChild2().getNodeType().equals("num")) {
        double ans = 0.0;
        //if (t.getOpValue().equals("+"))
        //  ans = t.getChild1().getNumValue()+t.getChild2().getNumValue();
        //else if (t.getOpValue().equals("-"))
        //  ans = t.getChild1().getNumValue()-t.getChild2().getNumValue();
        //else if (t.getOpValue().equals("*"))
        //  ans = t.getChild1().getNumValue()*t.getChild2().getNumValue();
        //else
        //  ans = t.getChild1().getNumValue()/t.getChild2().getNumValue();
        ans = (t.getOpValue().equals("+"))?
              t.getChild1().getNumValue()+t.getChild2().getNumValue():
              (t.getOpValue().equals("-"))?
              t.getChild1().getNumValue()-t.getChild2().getNumValue():
              (t.getOpValue().equals("*"))?
              t.getChild1().getNumValue()*t.getChild2().getNumValue():
              t.getChild1().getNumValue()/t.getChild2().getNumValue();
        LambdaNode n = new LambdaNode();
        n.setNodeType("num");
        n.setNumValue(ans);
        return n;
      }
      else
        return t;
    }
    else
      return t;
  }

  static void populateFreeVariables(LambdaNode t) {
    if (t.getNodeType().equals("num"))
      return;
    else if (t.getNodeType().equals("var"))
      t.addFreeVariable(t.getVariable());
    else if (t.getNodeType().equals("app") || t.getNodeType().equals("op")) {
      populateFreeVariables(t.getChild1());
      populateFreeVariables(t.getChild2());
      t.addFreeVariables(t.getChild1().getFreeVariables());
      t.addFreeVariables(t.getChild2().getFreeVariables());
    }
    else { //(t.getNodeType().equals("lambda"))
      populateFreeVariables(t.getChild1());
      t.addFreeVariables(t.getChild1().getFreeVariables());
      t.removeFreeVariable(t.getVariable());
    }
  }
 
  static void replaceDefs(LambdaNode t) {
    if (t.getNodeType().equals("num"))
      return;
    else if (t.getNodeType().equals("var")) {
      LambdaNode f = defs.get(t.getVariable());
      if (f != null) {
        LambdaNode n = deepCopy(f); 
        t.setNodeType(n.getNodeType());
        t.setNumValue(n.getNumValue());
        t.setOpValue(n.getOpValue());
        t.setVariable(n.getVariable());
        t.setChild1(n.getChild1());
        t.setChild2(n.getChild2());
        t.setFreeVariables(n.getFreeVariables());
      }  
    }
    else if (t.getNodeType().equals("app") || t.getNodeType().equals("op")) {
      replaceDefs(t.getChild1());
      replaceDefs(t.getChild2());
    }
    else { //(t.getNodeType().equals("lambda"))
      replaceDefs(t.getChild1());
    }
  }
 
  static void renameBoundVariable(LambdaNode t, String oldVar, String newVar) {
    if (t.getNodeType().equals("num"))
      return;
    else if (t.getNodeType().equals("var") && t.getVariable().equals(oldVar))
      t.setVariable(newVar);
    else if (t.getNodeType().equals("var") && !t.getVariable().equals(oldVar))
      return;
    else if (t.getNodeType().equals("app") || t.getNodeType().equals("op")) {
      renameBoundVariable(t.getChild1(),oldVar,newVar);
      renameBoundVariable(t.getChild2(),oldVar,newVar);
    }
    else { // must be lambda
      if (t.getVariable().equals(oldVar))
        return;
      else
        renameBoundVariable(t.getChild1(),oldVar,newVar);
    }
  }

  static LambdaNode applySubstitution(LambdaNode t, String var, LambdaNode term) {
    if (t.getNodeType().equals("num"))
      return t;
    else if (t.getNodeType().equals("var")) {
      if (t.getVariable().equals(var))
        return deepCopy(term);
      else
        return t;
    }
    else if (t.getNodeType().equals("app") || t.getNodeType().equals("op")) {
      LambdaNode t1 = applySubstitution(t.getChild1(),var,term);
      t.setChild1(t1);
      LambdaNode t2 = applySubstitution(t.getChild2(),var,term);
      t.setChild2(t2);
      return t;
    }
    else { // must be a lambda term
      if (t.getVariable().equals(var))
        return t;
      else if (!term.getFreeVariables().contains(t.getVariable())) {
        LambdaNode t1 = applySubstitution(t.getChild1(),var,term);
        t.setChild1(t1);
        return t;
      }
      else { // capture case
        String oldVariable = t.getVariable();
        String newVariable = t.getVariable()+"_"+newVarCount++;
        t.setVariable(newVariable);
        renameBoundVariable(t,oldVariable,newVariable);
        LambdaNode t1 = applySubstitution(t.getChild1(),var,term);
        t.setChild1(t1);
        return t;
      }
    }
  }

  static LambdaNode deepCopy(LambdaNode t) {
    if (t.getNodeType().equals("num")) {
      LambdaNode n = new LambdaNode();
      n.setNodeType("num");
      n.setNumValue(t.getNumValue());
      return n;
    }
    else if (t.getNodeType().equals("var")) {
      LambdaNode n = new LambdaNode();
      n.setNodeType("var");
      n.setVariable(t.getVariable());
      return n;
    }
    else if (t.getNodeType().equals("app") || t.getNodeType().equals("op")) {
      LambdaNode t1 = deepCopy(t.getChild1());
      LambdaNode t2 = deepCopy(t.getChild2());
      LambdaNode n = new LambdaNode();
      n.setNodeType(t.getNodeType());
      n.setChild1(t1);
      n.setChild2(t2);
      if (t.getNodeType().equals("op"))
        n.setOpValue(t.getOpValue());
      return n;
    }
    else { // must be a lambda term
      LambdaNode t1 = deepCopy(t.getChild1());
      LambdaNode n = new LambdaNode();
      n.setNodeType(t.getNodeType());
      n.setVariable(t.getVariable());
      n.setChild1(t1);
      return n;
    }
  }

  static void displayTree(LambdaNode t, Integer level) {
    for (int i=1; i<level.intValue(); i++)
      System.out.print("    ");
    System.out.print("NodeType="+t.getNodeType()+"  ");
    System.out.print("Free Variables: "+t.getFreeVariables()+"  ");
    if (t.getNodeType().equals("num"))
      System.out.println("Value="+t.getNumValue());
    else if (t.getNodeType().equals("var"))
      System.out.println("Variable="+t.getVariable());
    else if (t.getNodeType().equals("op")) {
      System.out.println(t.getOpValue());
      int lval = level.intValue();
      lval++;
      level = new Integer(lval);
      displayTree(t.getChild1(),level);
      displayTree(t.getChild2(),level);
    }
    else if (t.getNodeType().equals("app")) {
      System.out.println();
      int lval = level.intValue();
      lval++;
      level = new Integer(lval);
      displayTree(t.getChild1(),level);
      displayTree(t.getChild2(),level);
    }
    else { // must be lambda
      System.out.println(t.getVariable());
      int lval = level.intValue();
      lval++;
      level = new Integer(lval);
      displayTree(t.getChild1(),level);
    }
  }

  static void displayFlatTree(LambdaNode t) {
    if (t.getNodeType().equals("num"))
      System.out.print(t.getNumValue()+" ");
    else if (t.getNodeType().equals("var"))
      System.out.print(t.getVariable()+" ");
    else if (t.getNodeType().equals("op")) {
      System.out.print("( "+t.getOpValue()+" ");
      displayFlatTree(t.getChild1());
      displayFlatTree(t.getChild2());
      System.out.print(") ");
    }
    else if (t.getNodeType().equals("app")) {
      System.out.print("( ");
      displayFlatTree(t.getChild1());
      displayFlatTree(t.getChild2());
      System.out.print(") ");
    }
    else { // must be lambda
      System.out.print("( lambda ");
      System.out.print(t.getVariable()+" ");
      displayFlatTree(t.getChild1());
      System.out.print(") ");
    }
  }

  static String readInput() {
     try {
       StringBuffer buffer = new StringBuffer();
       System.out.flush();
       int c = System.in.read();
       while(c != ';' && c != -1) {
         if (c != '\n') 
           buffer.append((char)c);
         else {
           buffer.append(" ");
           System.out.print("LAMBDA> ");
           System.out.flush();
         }
         c = System.in.read();
       }
       return buffer.toString().trim();
     } catch (IOException e) {
         return "";
       }
   }
}
