package compilador;

import java.text.CharacterIterator;

/**
 *
 * @author uniflferreira
 */
public class OperadoresSimples extends AFD {

    @Override
    public Token evaluate(CharacterIterator code) {
        char current = code.current();
        
        if (current == CharacterIterator.DONE) {
            return new Token("EOF", "$");
        }

        // 1. Operadores Lógicos && e || (Precisam ter o par exato)
        if (current == '&') {
            code.next();
            if (code.current() == '&') {
                code.next();
                return new Token("op_log", "&&");
            }
            return null; // T-minus não aceita '&' sozinho
        }

        if (current == '|') {
            code.next();
            if (code.current() == '|') {
                code.next();
                return new Token("op_log", "||");
            }
            return null; // T-minus não aceita '|' sozinho
        }

        // 2. Família do '+' (+, ++, +=)
        if (current == '+') {
            code.next(); 
            if (code.current() == '+') {
                code.next();
                return new Token("op_incremento", "++");
            } else if (code.current() == '=') {
                code.next();
                return new Token("op_atrib_composto", "+=");
            }
            return new Token("op_soma", "+"); 
        }

        // 3. Família do '-' (-, --, -=)
        if (current == '-') {
            code.next();
            if (code.current() == '-') {
                code.next();
                return new Token("op_incremento", "--");
            } else if (code.current() == '=') {
                code.next();
                return new Token("op_atrib_composto", "-=");
            }
            return new Token("op_soma", "-"); 
        }

        // 4. Família do '*' (*, *=)
        if (current == '*') {
            code.next();
            if (code.current() == '=') {
                code.next();
                return new Token("op_atrib_composto", "*=");
            }
            return new Token("op_mult", "*");
        }

        // 5. Família do '/' (/, /=, //)
        if (current == '/') {
            code.next();
            if (code.current() == '=') {
                code.next();
                return new Token("op_atrib_composto", "/=");
            } else if (code.current() == '/') {
                // É um comentário! Consome tudo até a quebra de linha.
                StringBuilder comentario = new StringBuilder("//");
                code.next(); // consome a segunda barra
                
                while (code.current() != '\n' && code.current() != CharacterIterator.DONE) {
                    comentario.append(code.current());
                    code.next();
                }
                return new Token("COMENTARIO", comentario.toString());
            }
            return new Token("op_mult", "/"); 
        }

        // 6. Operador '%' (Apenas simples)
        if (current == '%') {
            code.next();
            return new Token("op_mult", "%"); 
        }

        // 7. Família do '=' (==, =>)
        if (current == '=') {
            code.next(); 
            if (code.current() == '=') {
                code.next(); 
                return new Token("op_rel", "==");
            } else if (code.current() == '>') {
                code.next(); 
                return new Token("op_atrib", "=>"); 
            }
            return null; 
        }

        // 8. Família do '>' (>, >>)
        if (current == '>') {
            code.next(); 
            if (code.current() == '>') {
                code.next();
                return new Token("fecha_transmissao", ">>"); 
            }
            return new Token("fecha_comando", ">"); 
        }

        // 9. Família do '<' (<, <<)
        if (current == '<') {
            code.next();
            if (code.current() == '<') {
                code.next();
                return new Token("abre_transmissao", "<<");
            } 
            return new Token("abre_comando", "<"); 
        }

        // 10. Família do '!' (!, !=)
        if (current == '!') {
            code.next();
            if (code.current() == '=') {
                code.next();
                return new Token("op_rel", "!="); 
            }
            return new Token("op_log", "!"); 
        }

        return null; // Não é um operador conhecido por esta classe
    }
}