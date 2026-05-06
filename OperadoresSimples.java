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


import java.text.CharacterIterator;



public class OperadoresSimples extends AFD {



    @Override

    public Token evaluate(CharacterIterator code) {

        char current = code.current();



        // 1. Tratamento de Símbolos Simples 

        if (current == '+') {

            code.next(); // Avança o iterador 

            return new Token("op_soma", "+"); 

        }

        if (current == '%') {

            code.next();

            return new Token("op_mult", "%"); 

        }

        if (current == '/') {

            code.next();

            return new Token("op_mult", "/"); 

        }

        // O sinal de menos pode ser op_soma ou parte de um número negativo pensar em como diferenciar isso


        if (current == '-') {

            code.next();

            return new Token("op_soma", "-"); 

        }



        // 2. Tratamento de Símbolos Compostos (Olhando o próximo caractere)

        

        // Caso do '=' que pode ser '==' ou '=>'

        if (current == '=') {

            code.next(); // Consome o primeiro '='

            if (code.current() == '=') {

                code.next(); // Consome o segundo '='

                return new Token("op_rel", "==");

            } else if (code.current() == '>') {

                code.next(); // Consome o '>' para formar '=>'

                return new Token("op_atrib", "=>"); 

            }


            return null; 

        }



        // Caso do '>' que pode ser '>' (fecha_comando), '>>' (fecha_transmissao) ou '>='

        if (current == '>') {

            code.next(); 

            if (code.current() == '>') {

                code.next();

                return new Token("fecha_transmissao", ">>"); 

            } else if (code.current() == '=') {


                code.next();

                return new Token("op_rel", ">="); 

            }

            return new Token("fecha_comando", ">"); 

        }

        if (current == '<') {

            code.next();

            if (code.current() == '<') {

                code.next();

                return new Token("abre_transmissao", "<<");

            } else if (code.current() == '=') {

                code.next();

                return new Token("op_rel", "<="); 

            }

            return new Token("abre_comando", "<"); 

        }



        // Caso do '!' para o '!=' (diferente)

        if (current == '!') {

            code.next();

            if (code.current() == '=') {

                code.next();

                return new Token("op_rel", "!="); 

            }

            return new Token("op_log", "!"); 

        }



        return null; // Não é um operador conhecido por esta classe [cite: 206]

    }

}