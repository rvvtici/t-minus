package compilador;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator; 
import java.util.ArrayList;
import java.util.List; 

public class Lexer { // O Lexer é a classe principal que vai ler o código fonte e usar os autômatos (AFDs) para quebrar o código em tokens. Ele mantém uma lista de tokens reconhecidos, uma lista de AFDs para testar e um iterador de caracteres para percorrer o código.
    private final List<Token> tokens;
    private final List<AFD> afds; 
    private final CharacterIterator code; 

    public Lexer(String code) {  // O construtor do Lexer recebe o código fonte como string, inicializa a lista de tokens, o iterador de caracteres e a lista de AFDs com os autômatos que criamos para reconhecer os diferentes tipos de tokens.
        tokens = new ArrayList<>(); 
        this.code = new StringCharacterIterator(code); 
        afds = new ArrayList<>(); 
        
        // Ordem importa
        afds.add(new OperadoresSimples());
        afds.add(new Numeros());
        afds.add(new Identificadores());
        afds.add(new Mensagens ());
    }

   // Método para pular espaços em branco, quebras de linha e tabs
    private void skipWhiteSpace() {
        while (code.current() == ' ' || 
               code.current() == '\n' || 
               code.current() == '\t' || 
               code.current() == '\r') {
            code.next();
        }
    }

    // Método que testa todos os autômatos para ver quem reconhece o token
    private Token searchNextToken() {
        int pos = code.getIndex();
        for (AFD afd : afds) {
            Token t = afd.evaluate(code);
            if (t != null) return t; // Encontrou um token válido
            code.setIndex(pos); // Se não encontrou, volta a posição para o próximo autômato tentar
        }
        return null;
    }

    // Método de erro
    private void error() {
        throw new RuntimeException("Erro: token não reconhecido: " + code.current());
    }

    // Método principal vai ler o código inteiro e devolver a lista de tokens
    public List<Token> getTokens() {
        Token t;
        do {
            skipWhiteSpace(); // Pula os espaços em branco antes de tentar reconhecer o próximo token
            t = searchNextToken();
            
            if (t == null) { // Se nenhum autômato reconheceu um token, é um erro léxico
                error();
                break; // Para evitar loop infinito, embora o error() já lance uma exceção
            }
            
            if (!t.tipo.equals("COMENTARIO")) { 
                tokens.add(t); // Adiciona o token reconhecido à lista (se não for comentário)
            }
        } while (!t.tipo.equals("EOF"));
        
        return tokens;
    }
   
}
