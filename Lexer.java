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
      
    }
    
   
}
