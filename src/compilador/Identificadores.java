/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compilador;

import java.text.CharacterIterator;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author uniflferreira
 */
public class Identificadores extends AFD {

    private Map<String, String> palavrasReservadas;

    public Identificadores() {
        palavrasReservadas = new HashMap<>();
        
        // --- PREENCHENDO O DICIONÁRIO DA T-MINUS ---
        
        // Tipos de Variáveis
        palavrasReservadas.put("Unidade", "tipo");
        palavrasReservadas.put("Precisao", "tipo");
        palavrasReservadas.put("Estimativa", "tipo");
        palavrasReservadas.put("Distancia", "tipo");
        palavrasReservadas.put("Eco", "tipo");
        palavrasReservadas.put("Carga", "tipo");
        palavrasReservadas.put("Pulso", "tipo");
        palavrasReservadas.put("Sinal", "tipo");
        palavrasReservadas.put("Mensagem", "tipo");

        // Comandos e Estruturas
        palavrasReservadas.put("nave", "res_nave");
        palavrasReservadas.put("iniciar_missao", "res_iniciar");
        palavrasReservadas.put("transmitir", "res_transmitir");
        palavrasReservadas.put("capturar", "res_capturar");
        palavrasReservadas.put("retornar", "res_retorno");
        palavrasReservadas.put("trajeto", "res_condicional");
        palavrasReservadas.put("recalcular", "res_recalcular");
        palavrasReservadas.put("abortar", "res_abortar");
        palavrasReservadas.put("percorrer", "res_percorrer");
        palavrasReservadas.put("orbita", "res_orbita");

        // Operadores Relacionais (em formato de texto)
        palavrasReservadas.put("maior_que", "op_rel");
        palavrasReservadas.put("menor_que", "op_rel");
        palavrasReservadas.put("maior_igual_que", "op_rel");
        palavrasReservadas.put("menor_igual_que", "op_rel");

        // Literais Booleanos
        palavrasReservadas.put("ativo", "lit_bool");
        palavrasReservadas.put("inativo", "lit_bool");
    }

    @Override
    public Token evaluate(CharacterIterator code) {
        char current = code.current();

        // Se não começar com letra, esse autômato ignora
        if (!Character.isLetter(current)) {
            return null;
        }

        StringBuilder word = new StringBuilder();

        // Lê enquanto for letra, número ou underline (para pegar maior_que, etc)
        while (Character.isLetterOrDigit(code.current()) || code.current() == '_') {
            word.append(code.current());
            code.next();
        }

        String lexema = word.toString();

        // --- O CHEFÃO: TRATAMENTO DO 'acesso livre' ---
        if (lexema.equals("acesso")) {
            int pos = code.getIndex(); // Salva a posição atual como um "checkpoint"
            
            // Pula os espaços em branco que dividem as duas palavras
            while (code.current() == ' ' || code.current() == '\t' || code.current() == '\n') {
                code.next();
            }
            
            // Tenta ler a próxima palavra
            StringBuilder nextWord = new StringBuilder();
            while (Character.isLetter(code.current())) {
                nextWord.append(code.current());
                code.next();
            }
            
            // Se for 'livre', formou o combo!
            if (nextWord.toString().equals("livre")) {
                return new Token("mod_acesso", "acesso livre");
            } else {
                // Se não era 'livre', volta para o "checkpoint" e segue a vida
                code.setIndex(pos);
            }
        }

        // 1. Verifica se é uma palavra reservada
        if (palavrasReservadas.containsKey(lexema)) {
            return new Token(palavrasReservadas.get(lexema), lexema);
        }

        // 2. Verifica se é um id_nave (tudo maiúsculo e mínimo de 2 caracteres)
        boolean isNave = true;
        if (lexema.length() < 2) {
            isNave = false;
        } else {
            for (int i = 0; i < lexema.length(); i++) {
                char c = lexema.charAt(i);
                // Pode ter número no final, mas a primeira tem que ser letra
                if (i == 0 && !Character.isUpperCase(c)) isNave = false;
                if (!Character.isUpperCase(c) && !Character.isDigit(c)) isNave = false;
            }
        }
        if (isNave) {
            return new Token("id_nave", lexema);
        }

        // 3. Se sobrou e passou por tudo, é uma variável comum
        return new Token("id_var", lexema);
    }
}
