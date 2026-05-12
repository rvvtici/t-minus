package compilador;
import java.text.CharacterIterator; 

public abstract class AFD { // Toda classe que herdar de AFD deve implementar o método evaluate, que tenta reconhecer um token a partir do estado atual do iterador de caracteres. Se conseguir, retorna o token; se não, retorna null para indicar que esse autômato não reconhece um token naquele ponto e o próximo autômato deve tentar.
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