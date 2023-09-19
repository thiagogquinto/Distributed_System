<div style="text-align: center;">
  <img src="./images/compass.png" alt="Logo Compass UOL" style="width: 100%;">
</div>

# Apresentação

<div align="center">
  <img src="./images/profile2.png" alt="Foto de perfil">
</div>

Olá, me chamo Thiago Gariani Quinto, sou de Campo Mourão e estudo na UTFPR - Universidade Tecnológica Federal do Paraná, onde curso Bacharelado em Ciência da Computação, atualmente estou no 5º período do curso. O programa de bolsas é a primeira experiência profissional que estou tendo, e estou muito feliz por ter sido selecionado para participar do programa. Tenho conhecimento em algumas tecnologias, que podem ser vistas em [minhas tecnologias](https://github.com/thiagogquinto).
Como hobbies gosto muito de jogar futebol e de jogar alguns jogos no computador. 

# Navegar entre seções
  Aqui está presente os links para ir de forma mais rápida para as seções do repositório.
- [Programa de Bolsas - Compass Uol](#programa-de-bolsas---compass-uol)
  - [Sprint 1 - Git/GitHub e Linux](#sprint-1---gitgithub-e-linux)
  - [Sprint 2 - Banco de Dados - SQL](#sprint-2---banco-de-dados---sql)
  - [Sprint 3 - Python](#sprint-3---python)
  - [Sprint 4 - Python Funcional e Docker](#sprint-4---python-funcional-e-docker)
  - [Sprint 5 - Computação em Nuvem e AWS](#sprint-5---computação-em-nuvem-e-aws)
  - [Sprint 6 - Data & Analytics](#sprint-6---data--analytics)
  - [Sprint 7 - Apache Spark](#sprint-7---apache-spark)
  - [Sprint 8 - Apache Spark + AWS](#sprint-8---apache-spark--aws)
  - [Sprint 9 - Armazenamento de Dados](#sprint-9---armazenamento-de-dados)
  - [Sprint 10 - QuickSight](#sprint-10---quicksight)
  - [Cultura Ágil e Segurança](./Cultura_Agil_Seg/)
# Programa de Bolsas - Compass Uol

O programa de bolsas é dividido em 10 sprints de duas semanas (14 dias), sendo que em cada sprint há um conteúdo disponibilizado para estudar, e em cada sprint há um exercício relacionado aos tópicos estudados. As sprints estão divididas da seguinte forma: 
## Sprint 1 - Git/GitHub e Linux

Nesta primeira sprint foi apresentado o Git e o GitHub, e também o Linux. Para realizar o aprendizado do contéudo proposto, estudei primeiramente o conteúdo relacionado ao Git e GitHub, e após isso, o conteúdo relacionado ao Linux. Ao final da Sprint foi realizado um exercício, que consistia em criar um repositório no GitHub para armazenar as atividades relacionadas ao Programa de Bolsas, e realizar o **commit** e **push** do arquivo para o repositório no GitHub. As anotações realizadas durante o estudo do conteúdo estão disponíveis no [arquivo da Sprint1](./Sprint1/notes.md). Além disso, os certificados do [curso de Git/GitHub](./Sprint1/git_github-certificado.pdf) e [Linux](./Sprint1/linux-vertificado.pdf) também estão disponíveis.

Em relação aos conteúdos de **Cultura Ágil e Segurança**, foi estudada a seção 4 do curso de *Métodos Ágeis de A a Z: o curso completo*, que consiste do assunto de **Scrum**.

## Sprint 2 - Banco de Dados - SQL
A segunda sprint começa abordando o assunto de Banco de Dados, e mais especificamente o SQL. O banco de dados sendo utilizado no curso é o PostgreSQL. Em cada pasta há um arquivo sql relacionado aos vídeos utilizados como aula, e um outro arquivo do desafio da seção. Até agora foram estudados os seguintes tópicos: 

- [Básico](./Sprint2/basic-sql/) 
    - **SELECT**: seleciona colunas de uma tabela
    - **FROM**: seleciona a tabela da qual as colunas serão selecionadas
    - **WHERE**: condição para selecionar as linhas da tabela
    - **ORDER BY**: ordena as linhas da tabela
    - **LIMIT**: limita a quantidade de linhas retornadas

- [Operadores](./Sprint2/operators/) 
    - **AND**: retorna verdadeiro se todas as condições forem verdadeiras
    - **OR**: retorna verdadeiro se pelo menos uma condição for verdadeira
    - **NOT**: retorna verdadeiro se a condição for falsa
    - **IN**: retorna verdadeiro se o valor estiver na lista
    - **BETWEEN**: retorna verdadeiro se o valor estiver entre os valores da lista
    - **LIKE**: retorna verdadeiro se o valor corresponder ao padrão
    - **IS NULL**: retorna verdadeiro se o valor for nulo
    - **IS NOT NULL**: retorna verdadeiro se o valor não for nulo

- [Funções agregadas](./Sprint2/3-funcoes_agregadas/)
    - **COUNT**: conta a quantidade de linhas
    - **SUM**: soma os valores de uma coluna
    - **AVG**: calcula a média dos valores de uma coluna
    - **MIN**: retorna o menor valor de uma coluna
    - **MAX**: retorna o maior valor de uma coluna
    - **GROUP BY**: agrupa os valores de uma coluna em grupos de acordo com o valor da coluna
    - **HAVING**: condição para selecionar os grupos em função dos valores agregados

- [Joins](./Sprint2/4-joins/)
    - **INNER JOIN**: retorna as linhas que possuem valores correspondentes nas duas tabelas
    - **LEFT JOIN**: retorna todas as linhas da tabela da esquerda e as linhas da tabela da direita que possuem valores correspondentes
    - **RIGHT JOIN**: retorna todas as linhas da tabela da direita e as linhas da tabela da esquerda que possuem valores correspondentes
    - **FULL JOIN**: retorna todas as linhas das duas tabelas

  Enquanto que nos outros tópicos eu não tive muitos problemas, este tópico foi um pouco mais confuso, tanto que na resolução do desafio eu demorei mais para entender como realizar.

- [Subqueries](./Sprint2/5-Subqueries/)
   
    Subqueries são consultas dentro de outras consultas. Elas podem ser utilizadas em qualquer lugar que uma expressão possa ser utilizada. 

    Esse é outro tópico que tive um pouco mais de dificuldade para saber como utilizá-lo, mas com o desafio consegui entender melhor.

- [Tratamento de Dados](./Sprint2/6-Tratamento_de_Dados/)

  - **Conversão de tipos de dados**
    Para converter um tipo de dado para outro, temos duas opções:
    - **CAST** : SELECT CAST('1' AS INTEGER); 
    - **DADO::TIPO**: SELECT '1'::INTEGER; 
    Ambas as formas converter o 1 tipo texto para o tipo inteiro. 

  - **Tratamento Geral**

    O tratamento geral se referiu a como fazer as *queries* quando há várias ocasiões na busca, nesses casos, um *case* pode ser utilizado. O método ocorre da seguinte forma:
    ```sql
    SELECT
      CASE
        WHEN condicao1 THEN resultado1
        WHEN condicao2 THEN resultado2
        ELSE resultado3
      END
    ```	
    O *case* é utilizado para fazer uma comparação, e caso a condição seja verdadeira, o resultado é retornado com o nome da coluna sendo oque está após o *THEN*. Caso a condição seja falsa, a próxima condição é verificada o resultado é retornado. Caso nenhuma condição seja verdadeira, o resultado3 é retornado.
     
    Outra forma é utilizando o *COALESCE*, que retorna o primeiro valor não nulo. O método ocorre da seguinte forma:
    ```sql
    SELECT
      COALESCE(coluna, operação caso seja nulo FROM tabela)
    ```
    O *COALESCE* funciona da seguinte forma, ele verifica se o valor da coluna é nulo, caso seja, ele retorna o valor da operação especifica na próxima posição e ocorre assim por diante caso tenha várias possibilidades. Caso não seja nulo, ele retorna o valor da coluna.

  - **Tratamento de Texto**

    Nesse tópico foram tratados os seguintes métodos:
    - **LOWER**: converte o texto para minúsculo
    - **UPPER**: converte o texto para maiúsculo
    - **REPLACE**: substitui um texto por outro - REPLACE('texto', 'o', 'a') -> tetxa
    - **TRIM**: remove os espaços em branco do início e do fim do texto

  - **Tratamento de Datas**

    Nesse tópico foram tratados diferentes métodos para trabalhar com datas, como por exemplo o *EXTRACT*, que extrai uma parte da data, como o dia, mês, ano, hora, etc. Houve a mostragem também do + *INTERVAL*, que adiciona um intervalo de tempo a uma data, e o *DATE_TRUNC*, que trunca uma data para um determinado período de tempo, como por exemplo, trunca a data para o mês, ano, dia, etc.

  - **Funções**
    Nesse tópico foi trarado sobre como criar uma função, sendo a sintaxe a seguinte:
    ```sql
    CREATE FUNCTION nome_da_funcao(parametro1 tipo, parametro2 tipo)
    RETURNS tipo_da_funcao
    LANGUAGE sql
    AS $$
    oque a função fará
    $$;
    ```
    Um exemplo mais prático está presente [aqui](./Sprint2/6-Tratamento_de_Dados/function.sql)

   - [Manipulação de Tabelas](./Sprint2/7-Manipulacao_Tabelas/)

      Neste tópico foi abordado como criar tabelas tanto a partir de queries quanto a partir "do zero". Além disso, também foi visto como inserir, atualizar e deletar dados de uma tabela (coluna e linha).
  
  - [Projeto 1](./Sprint2/Projeto_1/)

      O projeto 1 se refere ao projeto realizado durante o curso, o projeto propunha a partir de queries coletar dados para apresentar um relatório de vendas. Para sua conclusão foi acompanhado os vídeos e feito as queries propostas. Na pasta do projeto possui apenas o arquivo *excel* com os dados coletados e as queries realizadas.

  - [Projeto 2](./Sprint2/Projeto_2/)

      O projeto 2 se refere ao projeto realizado durante o curso, o projeto propuha a partir de queries coletar dados para apresentar um relatório de vendas. Para sua conclusão foi acompanhado os vídeos e feito as queries propostas.

  - [Exercícios](./Sprint2/Exercicios/)
    
      Nesta pasta estão os exercícios propostos pela CompassUol, os exercícios foram feitos utilizando o *SQLite*. 

  - Big Data

      No curso de Big Data foi apresentado os conceitos que envolvem a análise dos dados. Para tal, foi aprendido os conceitos de Big Data, os tipos de armazenamento (*Data Warehouse*, *Data Lake* e *Data Store*), os tipos de dados (estruturados, semi-estruturados e não estruturados), os tipos de análise, as ferramentas de análise e os tipos de arquitetura de dados.

## Sprint 3 - Python

  Nesta sprint foi apresentado os conceitos de Python desde o básico até o avançado, ou seja, foi apresentado os conceitos de variáveis, estruturas de dados, funções, Programação Orientada a Objetos, conexão do python com banco de dados, etc.

  Python é uma linguagem que eu já tinha contato, dessa forma, realizei anotção apenas dos tópicos que eu não tinha conhecimento ou que eu tinha dúvidas. Tais anotações podem ser encontradas [aqui](./Sprint3/notes.md).

  As pastas presentes nessa sprint são referentes aos exercícios propostos pelo curso, sendo que cada pasta possui uma seção referente a um tópico do exercício. Para acessar, basta clicar no tópico[aqui](./Sprint3/)
## Sprint 4 - Python Funcional e Docker

  Nesta Sprint é estudado os concetios do paradigma funcional, como por exemplo, funções de primeira classe, funções de alta ordem, funções puras, funções anônimas, funções lambda, etc. Além disso, também é estudado o Docker, que é uma ferramenta que permite a criação de ambientes isolados para a execução de aplicações. Foi visto também a orquestração de containers com o *Docker Swarm* e o *Kubernetes*.

  As pastas presentes nessa sprint são referentes aos exercícios propostos pelo curso, sendo que cada pasta possui uma seção referente a um tópico do exercício. Para acessar, basta clicar no [aqui](./Sprint4/Exercicios/). Além disso, [aqui](./Sprint4/notes.md) estão as anotações feitas durante o curso.

  Foi também abordado Python em *Estatística Preditiva*, dessa forma foi visto os diferente cálculos relacionados à tal assunto, além de como deixar os resultados à mostra de uma maneira mais visual.
## Sprint 5 - Computação em Nuvem e AWS

  Nesta Sprint foi apresentada os serviços que a Amazon oferece, desde a área de armazenamento até a área de Machine Learning. Além disso, foi apresentado os conceitos de computação em nuvem, como por exemplo, os tipos de nuvem, os tipos de serviços, os tipos de arquitetura, etc. Para que fosse possível entender tais conceitos, foi realizada uma prática dos serviços da AWS, além de cursos teóricos sobre os serviços da AWS, com [anotações](./Sprint5/) sobre os tópicos abordados em cada curso.

## Sprint 6 - Data & Analytics

  A *Sprint 6* aborda sobre como os dados têm se tornado mais importantes e os processos pelos quais eles passam pare serem utilizados como *insights*, ou seja, o tema abordado é sobre a análise de dados. Dessa forma, os conteúdos vistos foram sobre os dados e os servições disponibilizados pela AWS para a análise dos dados, anotações sobre tópicos importantes de cada curso podem ser encontradas [aqui](./Sprint6/). Importante destacar que assuntos que apesar de importantes mas que eu já tinham sido abordados em conteúdos anteriores não foram anotados.

  Além do já citado, foi realizado também exercícios usando *AWS S3*, *Amazon Athena* e *AWS Lambda* para aprender o funcionamento de tais sercições, os arquivos referentes a tais exercícios podem ser encontrados [aqui](./Sprint6/Exercicio/).
## Sprint 7 - Apache Spark

### Curso

Nesta sprint foi apresentado a ferramente *Spark*, que é uma ferramenta de processamento de dados em larga escala. A partir dela aprendemos sua arquitetura, como ela funciona, como ela é utilizada. Para aprendizado, foi realizado um exercício de *Spark* junto com o *AWS Glue*, que é um serviço da AWS que permite a criação de *ETLs* (Extract, Transform and Load), que são processos que extraem dados de diferentes fontes, os transformam e os carregam em um *Data Warehouse* ou *Data Lake*. O exercício consistia em criar um *crawler* para o *AWS Glue* e criar um *job* para o *Spark* que realizasse a transformação dos dados. O exercício pode ser encontrado [aqui](./Sprint7/Exercicios/spark_glue/).

### Desafio Final

Nesta sprint foi também realizada a primeira etapa do desafio final do *Programa de Bolsas*, que consistia em criar um script em *Python* que realizasse o upload de um arquivo para o *AWS S3*. O script seria executado por meio de um container *Docker*. Os arquivos configurados estão [aqui](./Sprint7/Exercicios/Desafio-Etapa1/)
## Sprint 8 - Apache Spark + AWS

  Nesta sprint foi reforçado a aprendizado de Apacha Spark da [Sprint 7](#sprint-7---apache-spark), além disso, também foi aparesentada a segunda etapa do desafio, que consistia de oletar dados a partir da *API do TMDB* e armazená-los no *AWS S3*. Para tal, foi utilizado o *AWS Lambda* para realizar a coleta dos dados e o upload dos arquivos. O código usado no *AWS Lambda* está [aqui](./Sprint8/Desafio-Etapa2/script.py).
## Sprint 9 - Armazenamento de Dados

## Sprint 10 - QuickSight
