# T-Minus

https://rvvtici.vercel.app/t-minus

## Objetivo do Projeto
T-Minus é uma linguagem de programação desenvolvida em Java e com compilador para Pascal. Com temática espacial e inspiração em missões espaciais, sua narrativa é modelar a execução de programas nos quais as classes representam naves e os métodos representam comandos ou etapas da missão. Desenvolvida como prova de conceito, teve como objetivo compreender a complexidade por trás das linguagens de programação e compiladores. Dessa forma, são permitidos algoritmos básicos e simulações lógicas.

## Desenvolvimento da Linguagem
Para o seu desenvolvimento, foi necessário compreender as etapas dos analisadores léxico, sintático e semântico, as quais são essnencias para o compilador do T-Minus, que é traduzido para Pascal.
- A parte léxica da linguagem é responsável por criar expressões regulares que atribuem um token à cada valor e lexema no formato TOKEN: <VALOR, LEXEMA>. Um exemplo de token é <NUM_INT, [-+]?[0-9]+>, que representa um numero inteiro de um ou mais dígitos ([0-9]+) e que pode ou não ser iniciado por um sinal ([-+]?).
- Já a parte sintática da linguagem cria a gramática e estrutura em que ordem cada token deve ser inserido. Por exemplo, a gramática para o "for" da linguagem é:
```
cmdPercorrer → 'percorrer' abre_comando tipo ‘de’ id_var expr 'ate' expr 'com passo' expr fecha_comando abre_transmissao bloco fecha_transmissao.
```
A lista de todos os tokens está disponível no Lexico.java e as instruções para acesso estão em (Acessar Lista de Tokens Conhecidos)[#acessar-lista-de-tokens-conhecidos] e gramática inteira está localizada em gramatica.md.

## Como Executar o Compilador
Nele, um código na linguagem T-minus deve ser inserido e, em uma linha vazia, deve ser digitado "FIM" para printar o equivalente na linguagem Pascal ou "tree" para printar a árvore sintática (ASD) do código.
```
cd src/compilador
javac *.java
cd ..
java compilador.Main 
```

## Exemplos de Código 
#### Happy Path
- Básico
```
acesso_livre nave TESTE<<
	acesso_livre iniciar_missao <> <<
    	transmitir<"Missao iniciada">
 >>
>>
```
- Condicional
```
acesso_livre nave HAILMARY <<
	Unidade nivel => 30
	trajeto<nivel menor_que 20> <<
		 transmitir<"Critico">
	>>  recalcular trajeto<nivel menor_que 50> <<
		transmitir<"Baixo">
	>> abortar <<
		transmitir<"OK">
	>>
>>
```
Todos os exemplos de código podem ser acessados em exemplos-codigo.md. Eles abrangem:
- Declaração de tipos e variáveis
- Leitura (input)
- Expressões Aritméticas
- Condicional
- Laço For & While
- Funções

#### Sad Path
- Declaração sem classe:
```
Unidade combustivel 
Precisao velocidade => 27.5 
```
- Transmitir sem parâmetro:
```
acesso_livre nave TESTE << 
	acesso_livre iniciar_missao <Unidade final> << 
		transmitir<> 
	>> 
>> 
```
- Duas classes em um só programa:
```
acesso_livre nave TESTE << 
	transmitir<"Ola, terraqueos!"> 
>> 
acesso_livre nave TESTE2 <> << 
	transmitir<"Ola, marcianos!"> 
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
