package org.example;

import cassandra.CassandraService;
import tabelas.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    ///################################################################
    //COLOQUE A SENHA DO POSTGRESQL!!!
    private static final String URL = "jdbc:postgresql://autorack.proxy.rlwy.net:57661/railway";
    private static final String USER = "postgres";
    private static final String PASSWORD = "BvzKsFbCslzRtGiWrrelANloUarxsVCZ"; // <---- COLOQUE A SENHA AQUI
    //################################################################

    ///################################################################
    // COLOQUE A SENHA DO CASSANDRA!!!
    private static String keyspace = "faculdade";
    private static String secureBundlePath = "src/main/java/cassandra/Secure Connect Project Cassandra.zip";
    private static String clientId = "FPnPSyMUoBwnxEAtKKsKBrpo";
    private static String secret = "tGf60do1+BFk9xZGZ5x56RoJ0xneRkDydzlGU4-J616zSqqE+.jzyqkUSHxOyUOkAsjMEMqKUktCOmFcHT11965b8z33X7QmXDIRBWxSRm7AHYxF5c.5oL0ZdLFq4r1o";
    ///################################################################


    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            List<Professor> professores = getProfessores(connection);
            List<Departamento> departamentos = getDepartamentos(connection);
            List<Curso> cursos = getCursos(connection);
            List<Aluno> alunos = getAlunos(connection);
            List<Disciplina> disciplinas = getDisciplinas(connection);
            List<MatrizCurricular> matrizCurricular = getMatrizCurricular(connection);
            List<HistoricoAluno> historicoAlunos = getHistoricoAlunos(connection);
            List<GrupoAluno> grupoAlunos = getGrupoAlunos(connection);
            List<HistoricoTCC> historicoTCC = getHistoricoTCC(connection);
            List<HistoricoProfessor> historicoProfessores = getHistoricoProfessores(connection);
            System.out.println("Pegou tudo do postgres");

            CassandraService cassandraService = new CassandraService(
                    keyspace,
                    secureBundlePath,
                    clientId,
                    secret,
                    professores,
                    departamentos,
                    cursos,
                    alunos,
                    disciplinas,
                    matrizCurricular,
                    historicoAlunos,
                    grupoAlunos,
                    historicoTCC,
                    historicoProfessores
            );

            cassandraService.insertDataIntoDB();
            cassandraService.showAllData();


            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\nEscolha uma opção:");
                System.out.println("0 - Mostrar todas as tabelas e dados");
                System.out.println("1 - Buscar histórico escolar do aluno");
                System.out.println("2 - Buscar histórico de disciplinas do professor");
                System.out.println("3 - Listar alunos formados no ano");
                System.out.println("4 - Listar chefes de departamentos");
                System.out.println("5 - Listar alunos e orientadores de TCC");
                System.out.println("6 - Sair");

                System.out.print("Digite sua escolha: ");
                int escolha = scanner.nextInt();
                scanner.nextLine(); // Consumir a quebra de linha
                System.out.println("----------------------------------------------------------------");
                switch (escolha) {
                    case 0:
                        cassandraService.showAllData();
                        break;
                    case 1:
                        System.out.print("Digite o nome do aluno: ");
                        String nomeAluno = scanner.nextLine();
                        cassandraService.buscarHistoricoEscolarAluno(nomeAluno);
                        break;

                    case 2:
                        System.out.print("Digite o nome do professor: ");
                        String nomeProfessor = scanner.nextLine();
                        cassandraService.buscarHistoricoDisciplinasProfessor(nomeProfessor);
                        break;

                    case 3:
                        System.out.print("Digite o ano: ");
                        int ano = scanner.nextInt();
                        cassandraService.listarAlunosFormadosNoAno(ano);
                        break;

                    case 4:
                        cassandraService.listarChefesDepartamentos();
                        break;

                    case 5:
                        cassandraService.listarAlunosEOrientadoresTCC();
                        break;

                    case 6:
                        System.out.println("Encerrando o programa.");
                        cassandraService.close();
                        scanner.close();
                        return;

                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                }
                System.out.println("----------------------------------------------------------------");

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static List<Professor> getProfessores(Connection connection) throws SQLException {
        List<Professor> professores = new ArrayList<>();
        String query = "SELECT * FROM faculdade.professor";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Professor professor = new Professor(
                        rs.getInt("Id_Professor"),
                        rs.getString("Nome"),
                        rs.getString("Sobrenome"),
                        rs.getString("Formacao"),
                        rs.getString("CPF")
                );
                professores.add(professor);
            }
        }
        return professores;
    }

    private static List<Departamento> getDepartamentos(Connection connection) throws SQLException {
        List<Departamento> departamentos = new ArrayList<>();
        String query = "SELECT * FROM faculdade.departamento";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Departamento departamento = new Departamento(
                        rs.getInt("Id_Departamento"),
                        rs.getString("Titulo"),
                        rs.getDouble("Verba"),
                        rs.getInt("Id_Professor")
                );
                departamentos.add(departamento);
            }
        }
        return departamentos;
    }

    private static List<Curso> getCursos(Connection connection) throws SQLException {
        List<Curso> cursos = new ArrayList<>();
        String query = "SELECT * FROM faculdade.curso";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Curso curso = new Curso(
                        rs.getInt("Id_Curso"),
                        rs.getInt("Id_Professor"),
                        rs.getInt("Id_Departamento"),
                        rs.getString("Titulo")
                );
                cursos.add(curso);
            }
        }
        return cursos;
    }

    private static List<Aluno> getAlunos(Connection connection) throws SQLException {
        List<Aluno> alunos = new ArrayList<>();
        String query = "SELECT * FROM faculdade.aluno";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Aluno aluno = new Aluno(
                        rs.getInt("Id_Aluno"),
                        rs.getInt("Id_Curso"),
                        rs.getString("Nome"),
                        rs.getString("Sobrenome"),
                        rs.getDate("Data_Adesao"),
                        rs.getString("CPF")
                );
                alunos.add(aluno);
            }
        }
        return alunos;
    }

    private static List<Disciplina> getDisciplinas(Connection connection) throws SQLException {
        List<Disciplina> disciplinas = new ArrayList<>();
        String query = "SELECT * FROM faculdade.disciplina";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Disciplina disciplina = new Disciplina(
                        rs.getInt("Id_Disciplina"),
                        rs.getString("Titulo")
                );
                disciplinas.add(disciplina);
            }
        }
        return disciplinas;
    }

    private static List<MatrizCurricular> getMatrizCurricular(Connection connection) throws SQLException {
        List<MatrizCurricular> matrizCurricular = new ArrayList<>();
        String query = "SELECT * FROM faculdade.matriz_curricular";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                MatrizCurricular matriz = new MatrizCurricular(
                        rs.getInt("Id_Matriz_Curricular"),
                        rs.getInt("Id_curso"),
                        rs.getInt("Id_disciplina"),
                        rs.getDouble("nota_de_corte")
                );
                matrizCurricular.add(matriz);
            }
        }
        return matrizCurricular;
    }

    private static List<HistoricoAluno> getHistoricoAlunos(Connection connection) throws SQLException {
        List<HistoricoAluno> historicoAlunos = new ArrayList<>();
        String query = "SELECT * FROM faculdade.historico_aluno";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                HistoricoAluno historico = new HistoricoAluno(
                        rs.getInt("Id_Historico_Escolar"),
                        rs.getInt("Id_Aluno"),
                        rs.getInt("Id_Disciplina"),
                        rs.getDouble("Media"),
                        rs.getString("Semestre"),
                        rs.getInt("Ano")
                );
                historicoAlunos.add(historico);
            }
        }
        return historicoAlunos;
    }

    private static List<GrupoAluno> getGrupoAlunos(Connection connection) throws SQLException {
        List<GrupoAluno> grupoAlunos = new ArrayList<>();
        String query = "SELECT * FROM faculdade.grupo_aluno";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                GrupoAluno grupo = new GrupoAluno(
                        rs.getInt("Id_Grupo_Aluno"),
                        rs.getInt("Id_Aluno"),
                        rs.getInt("Id_grupo")
                );
                grupoAlunos.add(grupo);
            }
        }
        return grupoAlunos;
    }

    private static List<HistoricoTCC> getHistoricoTCC(Connection connection) throws SQLException {
        List<HistoricoTCC> historicoTCC = new ArrayList<>();
        String query = "SELECT * FROM faculdade.historico_tcc";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                HistoricoTCC historico = new HistoricoTCC(
                        rs.getInt("Id_Historico_TCC"),
                        rs.getInt("Id_Grupo_Aluno"),
                        rs.getInt("Id_Professor"),
                        rs.getString("Semestre"),
                        rs.getInt("Ano"),
                        rs.getDouble("Nota")
                );
                historicoTCC.add(historico);
            }
        }
        return historicoTCC;
    }

    private static List<HistoricoProfessor> getHistoricoProfessores(Connection connection) throws SQLException {
        List<HistoricoProfessor> historicoProfessores = new ArrayList<>();
        String query = "SELECT * FROM faculdade.historico_professor";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                HistoricoProfessor historico = new HistoricoProfessor(
                        rs.getInt("Id_Historico_Professor"),
                        rs.getInt("Id_Professor"),
                        rs.getInt("Id_Disciplina"),
                        rs.getString("Semestre"),
                        rs.getInt("Ano")
                );
                historicoProfessores.add(historico);
            }
        }
        return historicoProfessores;
    }


}
