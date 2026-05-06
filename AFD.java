/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compilador;
import java.text.CharacterIterator; 

/**
 *
 * @author uniflferreira
 */


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