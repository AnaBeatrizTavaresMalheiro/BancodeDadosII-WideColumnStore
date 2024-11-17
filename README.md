# Projeto de Migração de Dados: PostgreSQL para Cassandra (NoSQL)

Este projeto tem como objetivo migrar dados de um banco de dados local PostgreSQL para um banco de dados na nuvem Cassandra, através de um script em Java. Além disso, a solução inclui funcionalidades de busca que permitirão a geração de relatórios a partir dos dados armazenados no Cassandra.

## Estrutura do Projeto

- **Java**: Linguagem de programação utilizada.
- **Gradle**: Ferramenta de gerenciamento de build.
- **PostgreSQL**: Banco de dados relacional de origem.
- **Cassandra**: Banco de dados NoSQL de destino.
- **ReilWay**: Servidor onde está armazenado o banco de dados relacional PostgreSQL.

## Funcionalidades

1. Conectar ao banco de dados PostgreSQL local e extrair dados das tabelas.
2. Criar objetos em Java para cada instância das tabelas.
3. Inserir os dados extraídos no Cassandra, utilizando um modelo de dados otimizado para consultas específicas.
4. Implementar funcionalidades para geração de relatórios com base nos dados no Cassandra.

## Tecnologias Utilizadas

- **Cassandra Driver for Java**: Para conectar e manipular o Cassandra a partir do Java.
- **PostgreSQL JDBC Driver**: Para acessar e consultar o banco de dados PostgreSQL.

## Sobre o Banco

Este projeto consiste em um modelo de banco de dados desenvolvido para uma instituição acadêmica, com o objetivo de gerenciar eficientemente informações sobre alunos, professores, cursos, departamentos, disciplinas e a estrutura curricular dos cursos oferecidos.

## Entidades Principais

1. **Alunos**: Contém informações sobre os alunos, como código, nome, data de nascimento, e-mail, etc.
2. **Professores**: Armazena detalhes sobre os professores, incluindo código, nome, data de admissão, departamento, etc.
3. **Cursos**: Representa os cursos oferecidos pela faculdade, com informações como código, nome, duração, departamento responsável, etc.
4. **Departamentos**: Registra os departamentos acadêmicos da instituição, com detalhes como código, nome, chefe de departamento, etc.
5. **Disciplinas**: Descreve as disciplinas oferecidas, incluindo código, nome, departamento responsável, carga horária, etc.
6. **Matriz Curricular**: Define a estrutura curricular dos cursos, especificando quais disciplinas são necessárias para completar o curso.

## Relatórios Possíveis

1. **Histórico Escolar de Alunos**: Consultar as disciplinas cursadas por um aluno, incluindo semestre, ano e nota final.
2. **Histórico de Disciplinas Ministradas por Professores**: Mostrar as disciplinas ministradas por um professor específico.
3. **Alunos Formados em um Semestre Específico**: Listar os alunos que se formaram em um determinado semestre de um ano.
4. **Professores Chefes de Departamento**: Obter uma lista de professores que lideram departamentos e os nomes desses departamentos.
5. **Grupos de TCC e Orientadores**: Identificar grupos de TCC e seus orientadores.

## Instruções de Uso

1. Clone o projeto **"ProjetoBancodeDadosCassandra"** do repositório.
2. Navegue até a pasta do projeto e execute o build com **Gradle**.
3. Acesse a pasta `src/main/java/cassandra/org/example/`.
4. Abra o arquivo `Main.java`.
5. Insira as credenciais fornecidas via Moodle para conectar ao Cassandra.
 <img width="1001" alt="image" src="https://github.com/user-attachments/assets/2f6da379-53ce-4694-9221-0c51f5278e4f">
6. Execute o arquivo `Main.java` para realizar a migração de dados,criar as relações no Cassandra e verificar consultas.
7. Use o terminal para interagir com o menu para fazer as querys e visualizar os dados

## Autores
<img src="https://avatars.githubusercontent.com/u/84588132?v=4" alt="Ana Beatriz Tavares" width="150"/> | <img src="https://avatars.githubusercontent.com/u/103201200?v=4" alt="Bruno Andwele" width="150"/> |
| ------------- | ------------- |
**Ana Beatriz Tavares** | **Bruno Andwele** |
*24.122.019-3* | *24.122.030-0*
