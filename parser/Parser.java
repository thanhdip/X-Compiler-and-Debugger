package parser;

import java.util.*;

import lexer.*;
import astree.*;
import visitor.*;

public class Parser
{

    private Token currentToken;
    private Lexer lex;
    private EnumSet<Tokens> addingOps = EnumSet.of(Tokens.Plus, Tokens.Minus,
            Tokens.Or);
    private EnumSet<Tokens> multiplyingOps = EnumSet.of(Tokens.Multiply,
            Tokens.Divide, Tokens.And);

    public static void main(String args[])
    {
        try
        {
            Parser parse = new Parser("expr.x");
            
            ASTree simpleAst = parse.rSimpleExpr();
            
            //Pretty Print after for test
            System.out.println("\nPRINTING AST:");
            PrintVisitor2 test = new PrintVisitor2();          
            simpleAst.accept(test);
           

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Construct a new Parser;
     *
     * @param sourceProgram
     *            - source file name
     * @exception Exception
     *                - thrown for any problems at startup (e.g. I/O)
     */
    public Parser(String sourceProgram) throws Exception
    {
        try
        {
            lex = new Lexer(sourceProgram);
            //scan();
            currentToken = lex.nextToken();
            System.out.println("Token is: "+currentToken.getSymbol());
        } catch (Exception e)
        {
            System.out.println("********exception*******" + e.toString());
            throw e;
        } ;
    }

    public Lexer getLex()
    {
        return lex;
    }

    boolean startingDecl()
    {
        if (isNextTok(Tokens.Int) || isNextTok(Tokens.BOOLean))
        {
            return true;
        }
        return false;
    }

    /**
     * < pre> t -> f -> t '*' f ==> * -> t '/' f ==> / -> t '&' f ==> and This
     * rule indicates we should pick up as many <i>f</i>'s as possible; the
     * <i>f</i>'s will be left associative
     * </pre>
     *
     * @return the tree corresponding to the multiplying expression
     * @exception SyntaxError
     *                - thrown for any syntax error
     */
    public ASTree rTerm() throws SyntaxError
    {
        
        System.out.println("Entering Term");
        System.out.println("Current token is: "+ currentToken.getSymbol());
        
        
        ASTree t, kid = rFactor();
        while ((t = getMultOperTree()) != null)
        {
            t.addKid(kid);
            t.addKid(rFactor());
            kid = t;
        }
        
        System.out.println("Leaving term");       
        
        return kid;      
    }

    /**
     * < pre> f -> '(' e ')' -> name -> <int> -> name '(' (e list ',')? ')' ==>
     * call
     * </pre>
     *
     * @return the tree corresponding to the factor expression
     * @exception SyntaxError
     *                - thrown for any syntax error
     */
    public ASTree rFactor() throws SyntaxError
    {
        System.out.println("Entering Factor");
        System.out.println("Current token is: "+ currentToken.getSymbol());
        
        ASTree t;
        if (isNextTok(Tokens.LeftParen))
        { // -> (e)           
            System.out.println("Processing left paren");
            System.out.println("Next Token is:");
           
            scan();
         
            t = rSimpleExpr();
            
            expect(Tokens.RightParen);       
            
            System.out.println("Leaving Factor");
            
            return t;
        }
        if (isNextTok(Tokens.INTeger))
        { // -> <int>
            t = new IntTree(currentToken);
            
            System.out.println("Making an INT node");
            System.out.println("Next Token is:");
            
            scan();
            
            System.out.println("Leaving Factor");
            System.out.println("Current token is: "+ currentToken.getSymbol());
            
            return t;
        }

        t = null;
        return t;
    }

    /**
     * < pre> se -> t -> se '+' t ==> + -> se '-' t ==> - -> se '|' t ==> or
     * This rule indicates we should pick up as many <i>t</i>'s as possible; the
     * <i>t</i>'s will be left associative
     * </pre>
     *
     * @return the tree corresponding to the adding expression
     * @exception SyntaxError
     *                - thrown for any syntax error
     */
    public ASTree rSimpleExpr() throws SyntaxError
    {
        System.out.println("Entering Simple Expression");
        System.out.println("Current token is: "+ currentToken.getSymbol());
        ASTree t, kid = rTerm();      
        while ((t = getAddOperTree()) != null)
        {        
            t.addKid(kid);
            t.addKid(rTerm());
            kid = t;           
        }
        System.out.println("Exiting simple Expression");
       
        return kid;
    }

    private ASTree getAddOperTree()
    {       
        if(currentToken == null)
        {
            return null;
        }
        System.out.println("Now current tok is: "+ currentToken.getSymbol());
        
        Tokens kind = currentToken.getKind();
        if (addingOps.contains(kind))
        {
            ASTree t = new AddOpTree(currentToken);
            
            System.out.println("Making add op");
            System.out.println("Next Token is:");
            scan();
            return t;
        } else
        {
            return null;
        }
    }

    private ASTree getMultOperTree()
    {
        if(currentToken == null)
        {
            return null;
        }
        
        Tokens kind = currentToken.getKind();
        if (multiplyingOps.contains(kind))
        {
            ASTree t = new MultOpTree(currentToken);
            System.out.println("Next Token is:");
            scan();
            return t;
        } else
        {
            return null;
        }
    }


    private boolean isNextTok(Tokens kind)
    {
        if ((currentToken == null) || (currentToken.getKind() != kind))
        {
            return false;
        }
        return true;
    }

    private void expect(Tokens kind) throws SyntaxError
    {
        if (isNextTok(kind))
        {
            scan();
            return;
        }
        throw new SyntaxError(currentToken, kind);
    }

    private void scan()
    {
        currentToken = lex.nextToken();
        if (currentToken != null)
        {
            currentToken.print(); // debug printout
        }
        return;
    }
}

class SyntaxError extends Exception
{

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    private Token tokenFound;
    private Tokens kindExpected;

    /**
     * record the syntax error just encountered
     *
     * @param tokenFound
     *            is the token just found by the parser
     * @param kindExpected
     *            is the token we expected to find based on the current context
     */
    public SyntaxError(Token tokenFound, Tokens kindExpected)
    {
        this.tokenFound = tokenFound;
        this.kindExpected = kindExpected;
    }

    void print()
    {
        System.out.println("Expected: " + kindExpected);
        return;
    }
}
