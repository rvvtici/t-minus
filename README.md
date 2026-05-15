# 🚀 T-minus

## Objetivo do Projeto
T-minus é uma linguagem de programação didática e experimental, inspirada em Java, com temática espacial. Sua narrativa é de modelar a execução de programas como missões espaciais, nas quais as classes representam naves e os métodos representam comandos ou etapas da missão. Desenvolvida como prova de conceito. Permite algoritmos básicos e simulações lógicas.

## Desenvolvimento da Linguagem
Para o seu desenvolvimento, foi necessário compreender a funcionalidade dos analisadores léxico e sintático. A parte léxica da linguagem é responsável por criar expressões regulares que atribuem um token à cada valor e lexema no formato <VALOR, LEXEMA>. Um exemplo de token é <NUM_INT, [-+]?[0-9]+>, que representa um numero inteiro de um ou mais dígitos e que pode ou não ser iniciado por um sinal. A lista de todos os tokens está disponível no Lexico.java e as instruções para acesso estão em (Acessar Lista de Tokens Conhecidos)[#acessar-lista-de-tokens-conhecidos]. Já a parte sintática da linguagem cria a gramática e estrutura em que ordem cada token deve ser inserido. Por exemplo, a gramática para o "for" da linguagem é 
```
cmdPercorrer → 'percorrer' abre_comando tipo ‘de’ id_var expr 'ate' expr 'com passo' expr fecha_comando abre_transmissao bloco fecha_transmissao.
```
A lista da gramática inteira está localizada em gramatica.md.

## Linguagem Base e Tradutora
Para o desenvolvimento da linguagem T-minus, foi utilizado Java como base. Já para tradução equivalente do código do T-minus, foi utilizada a linguagem Pascal.

## Como Executar o Compilador
```
cd src/compilador
javac *.java
cd ..
java compilador.Main 
```

## Explicação da Main
Um código na linguagem T-minus deve ser inserido e, em uma linha vazia, deve ser digitado "FIM" para printar o equivalente na linguagem Pascal ou "tree" para printar a árvore sintática (ASD) do código.

## Exemplos de Código 
#### Exemplo com Main e Print
```
//T-Minus [Entrada]
acesso_livre nave TESTE<<
	acesso_livre iniciar_missao <> <<
    	transmitir<"Missao iniciada">
 >>
>>
```
#### Exemplo com Leitura (scanf)
```
acesso_livre nave HAILMARY <<          
        Unidade combustivel => capturar
>>
```
Para mais exemplos, consulte exemplos-codigo.md.

## Acessar Lista de Tokens Conhecidos
```
cd src/compilador
javac *.java
cd ..
java compilador.Lexico
```

## Exemplos de Árvore Sintática
```
cd src/compilador
javac *.java
cd ..
java compilador.Sintatico 
```

## Características da Linguagem Criada
- O programa é declarado como uma nave, que é o equivalente a uma classe em Java.
- O nome da classe deve ser escrito em letra maiúscula e ter no mínimo dois caracteres. Permite números no meio e fim.
acesso_livre serve como modificador de acesso, equivalente ao public do java. É o único modificador existente da linguagem (no momento).
- Blocos são delimitados por << e >> em vez das chaves.
- Parâmetros e condições são delimitados por < e > em vez dos parênteses. Em contrapartida, os operadores lógicos > e < de java são tratados por extenso (maior_que e menor_que).
- O método principal é declarado com iniciar_missao, equivalente a main do Java.
- T-minus não possui delimitador de fim de comando (;), a quebra de linha fica com esse papel
- Os operadores de atribuição e igualdade são tratados como setas (=> e ==>). Não existe = sozinho em T-minus.
- Há algumas palavras-chave no laço for e while (percorrer e orbita) que não são identificadas pela AST (servem apenas esteticamente para os comandos). Os lexemas são: de, ate, com e passo.

## Observações
- Imports implícitos: Scanner, Math e outros são injetados automaticamente pelo - transpilador;
- Capturar infere o método Scanner (nextInt, nextDouble…) pelo tipo da variável;
na tabela de símbolos. Capturar possui duas opções: ler o tipo ou buscar após
declaração;
- Espaços em branco, tabs e quebras de linha são ignorados pelo analisador
léxico.;
- id_nave e id_var usam o mesmo token ID no léxico; o parser distingue pelo
contexto.

## Autoria
Desenvolvido por **Ana Lima**, **Luana De Almeida** e **Ravi Macedo**.

## Professor Responsável: Charles Henrique P Ferreira
