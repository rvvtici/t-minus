/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compilador;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator; 
import java.util.ArrayList;
import java.util.List; 

/**
 *
 * @author uniflferreira
 */

public class Lexer { 
    private List<Token> tokens; 
    private List<AFD> afds; 
    private CharacterIterator code; 

    public Lexer(String code) { 
        tokens = new ArrayList<>(); 
        this.code = new StringCharacterIterator(code); 
        afds = new ArrayList<>(); 
        
        // Adicione seus autômatos aqui! A ordem importa.
        afds.add(new OperadoresSimples());
        afds.add(new Numeros());
        afds.add(new Identificadores());
        afds.add(new Mensagens ());
      
    }
   // Método para pular espaços em branco, quebras de linha e tabs
    public void skipWhiteSpace() {
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

    // O método principal que vai ler o código inteiro e devolver a lista de tokens
   // Método que faz a quebra de uma sentença em lexemas
    public List<Token> getTokens() {
        Token t;
        do {
            skipWhiteSpace();
            t = searchNextToken();
            
            if (t == null) {
                error();
            }
            
            tokens.add(t);
            
        } while (!t.tipo.equals("EOF"));
        
        return tokens;
    }
   
}
