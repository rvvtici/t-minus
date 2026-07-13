package compilador;

import java.text.CharacterIterator;

public class OperadoresSimples extends AFD { // Autômato para reconhecer operadores simples: +, -, *, /, %, &&, ||, !, =, ==, !=, >, <, >=, <=, >>, <<

    @Override
    public Token evaluate(CharacterIterator code) {
        char current = code.current();
        
        if (current == CharacterIterator.DONE) {
            return new Token("EOF", "$");
        }

        // Operadores Lógicos && e || (Precisam ter o par exato)
        if (current == '&') {
            code.next();
            if (code.current() == '&') {
                code.next();
                return new Token("op_log_e", "&&");
            }
            return null; // T-minus não aceita '&' sozinho
        }

        if (current == '|') {
            code.next();
            if (code.current() == '|') {
                code.next();
                return new Token("op_log_ou", "||");
            }
            return null; // T-minus não aceita '|' sozinho
        }

        // Família do '+' (+, ++, +=)
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

        // Família do '-' (-, --, -=)
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

        // Família do '*' (*, *=)
        if (current == '*') {
            code.next();
            if (code.current() == '*') { 
                code.next(); return new Token("op_pot", "**"); 
            }
            if (code.current() == '=') {
                code.next();
                return new Token("op_atrib_composto", "*=");
            }
            return new Token("op_mult", "*");
        }

        // Família do '/' (/, /=, //)
        if (current == '/') {
            code.next();
            if (code.current() == '=') {
                code.next();
                return new Token("op_atrib_composto", "/=");
            } else if (code.current() == '/') {
                // É um comentário! Consome tudo até a quebra de linha.
                StringBuilder comentario = new StringBuilder("//");
                code.next(); // Consome a segunda barra
                
                while (code.current() != '\n' && code.current() != CharacterIterator.DONE) {
                    comentario.append(code.current());
                    code.next();
                }
                return new Token("COMENTARIO", comentario.toString());
            }
            return new Token("op_mult", "/"); 
        }

        // Operador '%' (Apenas simples)
        if (current == '%') {
            code.next();
            return new Token("op_mult", "%"); 
        }

        // Família do '=' (==>, =>)
        if (current == '=') {
            code.next();
            if (code.current() == '=') {
                code.next();
                if (code.current() == '>') {    // ==>
                    code.next();
                    return new Token("op_rel", "==>");
                }
            }
            if (code.current() == '>') {        // =>
                code.next();
                return new Token("op_atrib", "=>");
            }
            return null; // = ou == sozinhos não existem
        }

        // Família do '>' (>, >>)
        if (current == '>') {
            code.next(); 
            if (code.current() == '>') {
                code.next();
                return new Token("fecha_transmissao", ">>"); 
            }
            return new Token("fecha_comando", ">"); 
        }

        // Família do '<' (<, <<)
        if (current == '<') {
            code.next();
            if (code.current() == '<') {
                code.next();
                return new Token("abre_transmissao", "<<");
            } 
            return new Token("abre_comando", "<"); 
        }

        // Família do '!' (!, !=)
        if (current == '!') {
            code.next();
            if (code.current() == '=') {
                code.next();
                return new Token("op_rel", "!="); 
            }
            return new Token("op_neg", "!"); 
        }

        // Separador de parâmetros e argumentos
        if (current == ',') {
            code.next();
            return new Token("sep", ",");
        }

        return null; // Não é um operador conhecido por esta classe
    }
}