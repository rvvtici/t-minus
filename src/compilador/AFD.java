package compilador;
import java.text.CharacterIterator; 

public abstract class AFD {
    public abstract Token evaluate(CharacterIterator code); 

    public boolean isTokenSeparator(CharacterIterator code) { 
        char c = code.current();
        return c == ' ' || 
               c == '\n' || 
               c == '\t' ||
               c == '<' || 
               c == '>' || 
               c == '=' ||  
               c == CharacterIterator.DONE; 
    }
}