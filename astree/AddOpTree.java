package astree;

import lexer.Symbol;
import lexer.Token;
import visitor.*;

public class AddOpTree extends ASTree
{
    private Symbol symbol;

    public AddOpTree(Token tok)
    {
        this.symbol = tok.getSymbol();
    }

    public Object accept(ASTreeVisitor v)
    {
        return v.visitAddOpTree(this);
    }

    public Symbol getSymbol()
    {
        return symbol;
    }

}
