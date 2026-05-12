package compilador;

import java.text.CharacterIterator;
import java.util.HashMap;
import java.util.Map;

public class Identificadores extends AFD { // Reconhece palavras reservadas, identificadores de variáveis e identificadores de naves. Também tem um tratamento especial para o "acesso livre", que é um modificador de acesso.

    private final Map<String, String> palavrasReservadas;

    public Identificadores() {
        palavrasReservadas = new HashMap<>(); // Mapeia cada palavra reservada para seu tipo de token correspondente
                
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
        palavrasReservadas.put("de",    "res_de");
        palavrasReservadas.put("ate",   "res_ate");
        palavrasReservadas.put("com",   "res_com");
        palavrasReservadas.put("passo", "res_passo");
        palavrasReservadas.put("onde", "res_onde");

        // Operadores Relacionais (t-minus trata em formato de texto)
        palavrasReservadas.put("maior_que", "op_rel");
        palavrasReservadas.put("menor_que", "op_rel");
        palavrasReservadas.put("maior_igual_que", "op_rel");
        palavrasReservadas.put("menor_igual_que", "op_rel");

        // Literais Booleanos
        palavrasReservadas.put("ativo", "lit_bool");
        palavrasReservadas.put("inativo", "lit_bool");
    }

    @Override
    public Token evaluate(CharacterIterator code) { // O processo de reconhecimento é o mesmo para palavras reservadas, id_nave e id_var, já que todos começam com letra e podem conter letras, números e underline. A distinção entre eles é feita no final, verificando as regras específicas de cada tipo.
        char current = code.current();

        // Se não começar com letra, esse autômato ignora
        if (!Character.isLetter(current)) {
            return null;
        }

        StringBuilder word = new StringBuilder(); // Vai construindo a palavra enquanto for letra, número ou underline

        // Lê enquanto for letra, número ou underline (para pegar maior_que, etc)
        while (Character.isLetterOrDigit(code.current()) || code.current() == '_') {
            word.append(code.current());
            code.next();
        }

        String lexema = word.toString(); // Lê o lexema completo que foi lido

        // TRATAMENTO DO 'acesso livre'
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
                // Se não era 'livre', volta para o checkpoint e segue
                code.setIndex(pos);
            }
        }

        // Verifica se é uma palavra reservada
        if (palavrasReservadas.containsKey(lexema)) {
            return new Token(palavrasReservadas.get(lexema), lexema);
        }

        // Verifica se é um id_nave (tudo maiúsculo e mínimo de 2 caracteres)
        boolean isNave = true;
        if (lexema.length() < 2) {
            isNave = false;
        } else {
            for (int i = 0; i < lexema.length(); i++) {
                char c = lexema.charAt(i);
                // Pode ter número no meio e fim, mas a primeira tem que ser letra
                if (i == 0 && !Character.isUpperCase(c)) isNave = false;
                if (!Character.isUpperCase(c) && !Character.isDigit(c)) isNave = false;
            }
        }
        if (isNave) {
            return new Token("id_nave", lexema);
        }

        // Se sobrou e passou por tudo, é uma variável comum
        return new Token("id_var", lexema);
    }
}
