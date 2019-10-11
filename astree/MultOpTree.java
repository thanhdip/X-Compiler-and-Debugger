package astree;

import lexer.Symbol;
import lexer.Token;
import visitor.*;

public class MultOpTree extends ASTree
{
    private Symbol symbol;

    /**
     * @param tok
     *            contains the Symbol that indicates the specific multiplying
     *            operator
     */
    public MultOpTree(Token tok)
    {
        this.symbol = tok.getSymbol();
    }

    public Object accept(ASTreeVisitor v)
    {
        return v.visitMultOpTree(this);
    }

    public Symbol getSymbol()
    {
        return symbol;
    }

}
