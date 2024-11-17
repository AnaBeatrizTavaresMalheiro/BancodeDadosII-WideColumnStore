package cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.config.ProgrammaticDriverConfigLoaderBuilder;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import tabelas.*;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CassandraService {

    private CqlSession session;
    private String keyspace;
    private List<Professor> professores;
    private List<Departamento> departamentos;
    private List<Curso> cursos;
    private List<Aluno> alunos;
    private List<Disciplina> disciplinas;
    private List<MatrizCurricular> matrizCurricular;
    private List<HistoricoAluno> historicoAlunos;
    private List<GrupoAluno> grupoAlunos;
    private List<HistoricoTCC> historicoTCC;
    private List<HistoricoProfessor> historicoProfessores;

    public CassandraService(String keyspace, String secureBundlePath, String clientId, String secret,
                            List<Professor> professores,
                            List<Departamento> departamentos,
                            List<Curso> cursos,
                            List<Aluno> alunos,
                            List<Disciplina> disciplinas,
                            List<MatrizCurricular> matrizCurricular,
                            List<HistoricoAluno> historicoAlunos,
                            List<GrupoAluno> grupoAlunos,
                            List<HistoricoTCC> historicoTCC,
                            List<HistoricoProfessor> historicoProfessores) {

        this.keyspace = keyspace;
        connectToDatabase(secureBundlePath, clientId, secret);
        this.professores = professores;
        this.departamentos = departamentos;
        this.cursos = cursos;
        this.alunos = alunos;
        this.disciplinas = disciplinas;
        this.matrizCurricular = matrizCurricular;
        this.historicoAlunos = historicoAlunos;
        this.grupoAlunos = grupoAlunos;
        this.historicoTCC = historicoTCC;
        this.historicoProfessores = historicoProfessores;
    }

    public void close() {
        if (session != null) {
            session.close();
            System.out.println("Conexão com o Astra DB encerrada.");
        }
    }


    private void connectToDatabase(String secureBundlePath, String clientId, String secret) {
        try {
            ProgrammaticDriverConfigLoaderBuilder configBuilder = DriverConfigLoader.programmaticBuilder();
            configBuilder.withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.ofSeconds(10));


            session = CqlSession.builder()
                    .withCloudSecureConnectBundle(Paths.get(secureBundlePath))
                    .withAuthCredentials(clientId, secret)
                    .withKeyspace(keyspace)
                    .withConfigLoader(configBuilder.build())
                    .build();

            ResultSet rs = session.execute("select release_version from system.local");
            Row row = rs.one();
            if (row != null) {
                System.out.println("Versão do Cassandra: " + row.getString("release_version"));
            }
        } catch (Exception e) {
            System.out.println("Erro ao conectar ao Astra DB: " + e.getMessage());
        }
    }

    public void apagarTodasAsTabelas() {
        try {
            // Obter todas as tabelas do keyspace
            String queryListTables = String.format("SELECT table_name FROM system_schema.tables WHERE keyspace_name = '%s';", keyspace);
            ResultSet resultSet = session.execute(queryListTables);

            // Apagar cada tabela individualmente
            for (Row row : resultSet) {
                String tableName = row.getString("table_name");
                if (tableName != null) {
                    String dropTableQuery = String.format("DROP TABLE IF EXISTS %s.%s;", keyspace, tableName);
                    System.out.println("Apagando tabela: " + tableName);
                    session.execute(dropTableQuery);
                }
            }

            System.out.println("Todas as tabelas do keyspace foram apagadas com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao apagar as tabelas: " + e.getMessage());
        }
    }

    public void insertDataIntoDB() {
        System.out.println("Apagando tabelas existentes");
        apagarTodasAsTabelas();
        System.out.println("Cassandra - Inserindo dados");
        insertProfessores(professores);
        System.out.println("Inseriu professores");
        insertDepartamentos(departamentos);
        System.out.println("Inseriu departamentos");
        insertCursos(cursos);
        System.out.println("Inseriu cursos");
        insertAlunos(alunos);
        System.out.println("Inseriu alunos");
        insertDisciplinas(disciplinas);
        System.out.println("Inseriu disciplinas");
        insertMatrizCurricular(matrizCurricular);
        System.out.println("Inseriu matrizCurricular");
        insertHistoricoAlunos(historicoAlunos);
        System.out.println("Inseriu historicoAlunos");
        insertGrupoAlunos(grupoAlunos);
        System.out.println("Inseriu grupoAlunos");
        insertHistoricoTCC(historicoTCC);
        System.out.println("Inseriu historicoTCC");
        insertHistoricoProfessores(historicoProfessores);
        System.out.println("Inseriu historicoProfessores");
    }

    private void executeQuery(String query) {
        try {
            ResultSet result = session.execute(query);
        } catch (Exception e) {
            System.out.println("Erro ao executar a query: " + query + " - " + e.getMessage());
        }
    }

    private void insertProfessores(List<Professor> professores) {
        String createTableQuery = String.format(
                "CREATE TABLE IF NOT EXISTS %s.professores (" +
                        "idProfessor int PRIMARY KEY, " +
                        "nome text, " +
                        "sobrenome text, " +
                        "formacao text, " +
                        "cpf text);",
                keyspace
        );
        executeQuery(createTableQuery);

        for (Professor professor : professores) {
            String queryProfessor = String.format(
                    "INSERT INTO %s.professores (idProfessor, nome, sobrenome, formacao, cpf) VALUES (%d, '%s', '%s', '%s', '%s')",
                    keyspace,
                    professor.getIdProfessor(),
                    professor.getNome(),
                    professor.getSobrenome(),
                    professor.getFormacao(),
                    professor.getCpf()
            );
            executeQuery(queryProfessor);
        }
    }

    private void insertDepartamentos(List<Departamento> departamentos) {

        String createTableQuery = String.format(
                "CREATE TABLE IF NOT EXISTS %s.departamentos (" +
                        "idDepartamento int PRIMARY KEY, " +
                        "titulo text, " +
                        "verba double, " +
                        "idProfessor int);",
                keyspace
        );
        executeQuery(createTableQuery);

        for (Departamento departamento : departamentos) {
            String insertQuery = String.format(
                    "INSERT INTO %s.departamentos (idDepartamento, titulo, verba, idProfessor) VALUES (%d, '%s', %f, %d);",
                    keyspace,
                    departamento.getIdDepartamento(),
                    departamento.getTitulo(),
                    departamento.getVerba(),
                    departamento.getIdProfessor()
            );
            executeQuery(insertQuery);
        }
    }

    private void insertCursos(List<Curso> cursos) {

        String createTableQuery = String.format(
                "CREATE TABLE IF NOT EXISTS %s.cursos (" +
                        "idCurso int PRIMARY KEY, " +
                        "idProfessor int, " +
                        "idDepartamento int, " +
                        "titulo text);",
                keyspace
        );
        executeQuery(createTableQuery);

        for (Curso curso : cursos) {
            String insertQuery = String.format(
                    "INSERT INTO %s.cursos (idCurso, idProfessor, idDepartamento, titulo) VALUES (%d, %d, %d, '%s');",
                    keyspace,
                    curso.getIdCurso(),
                    curso.getIdProfessor(),
                    curso.getIdDepartamento(),
                    curso.getTitulo()
            );
            executeQuery(insertQuery);
        }
    }

    private void insertAlunos(List<Aluno> alunos) {

        String createTableQuery = String.format(
                "CREATE TABLE IF NOT EXISTS %s.alunos (" +
                        "idAluno int PRIMARY KEY, " +
                        "idCurso int, " +
                        "nome text, " +
                        "sobrenome text, " +
                        "dataAdesao timestamp, " +
                        "cpf text);",
                keyspace
        );
        executeQuery(createTableQuery);

        for (Aluno aluno : alunos) {
            String insertQuery = String.format(
                    "INSERT INTO %s.alunos (idAluno, idCurso, nome, sobrenome, dataAdesao, cpf) VALUES (%d, %d, '%s', '%s', '%s', '%s');",
                    keyspace,
                    aluno.getIdAluno(),
                    aluno.getIdCurso(),
                    aluno.getNome(),
                    aluno.getSobrenome(),
                    aluno.getDataAdesao().toString(),
                    aluno.getCpf()
            );
            executeQuery(insertQuery);
        }
    }

    private void insertDisciplinas(List<Disciplina> disciplinas) {

        String createTableQuery = String.format(
                "CREATE TABLE IF NOT EXISTS %s.disciplinas (" +
                        "idDisciplina int PRIMARY KEY, " +
                        "titulo text);",
                keyspace
        );
        executeQuery(createTableQuery);

        for (Disciplina disciplina : disciplinas) {
            String insertQuery = String.format(
                    "INSERT INTO %s.disciplinas (idDisciplina, titulo) VALUES (%d, '%s');",
                    keyspace,
                    disciplina.getIdDisciplina(),
                    disciplina.getTitulo()
            );
            executeQuery(insertQuery);
        }
    }

    private void insertMatrizCurricular(List<MatrizCurricular> matrizCurricular) {

        String createTableQuery = String.format(
                "CREATE TABLE IF NOT EXISTS %s.matriz_curricular (" +
                        "idMatrizCurricular int PRIMARY KEY, " +
                        "idCurso int, " +
                        "idDisciplina int, " +
                        "notaDeCorte double);",
                keyspace
        );
        executeQuery(createTableQuery);

        for (MatrizCurricular mc : matrizCurricular) {
            String insertQuery = String.format(
                    "INSERT INTO %s.matriz_curricular (idMatrizCurricular, idCurso, idDisciplina, notaDeCorte) VALUES (%d, %d, %d, %f);",
                    keyspace,
                    mc.getIdMatrizCurricular(),
                    mc.getIdCurso(),
                    mc.getIdDisciplina(),
                    mc.getNotaDeCorte()
            );
            executeQuery(insertQuery);
        }
    }

    private void insertHistoricoAlunos(List<HistoricoAluno> historicoAlunos) {

        String createTableQuery = String.format(
                "CREATE TABLE IF NOT EXISTS %s.historico_alunos (" +
                        "idHistoricoAluno int PRIMARY KEY, " +
                        "idAluno int, " +
                        "idDisciplina int, " +
                        "media double, " +
                        "semestre text, " +
                        "ano int);",
                keyspace
        );
        executeQuery(createTableQuery);

        for (HistoricoAluno ha : historicoAlunos) {
            String insertQuery = String.format(
                    "INSERT INTO %s.historico_alunos (idHistoricoAluno, idAluno, idDisciplina, media, semestre, ano) VALUES (%d, %d, %d, %f, '%s', %d);",
                    keyspace,
                    ha.getIdHistoricoAluno(),
                    ha.getIdAluno(),
                    ha.getIdDisciplina(),
                    ha.getMedia(),
                    ha.getSemestre(),
                    ha.getAno()
            );
            executeQuery(insertQuery);
        }
    }

    private void insertGrupoAlunos(List<GrupoAluno> grupoAlunos) {

        String createTableQuery = String.format(
                "CREATE TABLE IF NOT EXISTS %s.grupo_alunos (" +
                        "idGrupoAluno int PRIMARY KEY, " +
                        "idAluno int, " +
                        "idGrupo int);",
                keyspace
        );
        executeQuery(createTableQuery);

        for (GrupoAluno ga : grupoAlunos) {
            String insertQuery = String.format(
                    "INSERT INTO %s.grupo_alunos (idGrupoAluno, idAluno, idGrupo) VALUES (%d, %d, %d);",
                    keyspace,
                    ga.getIdGrupoAluno(),
                    ga.getIdAluno(),
                    ga.getIdGrupo()
            );
            executeQuery(insertQuery);
        }
    }

    private void insertHistoricoTCC(List<HistoricoTCC> historicoTCCs) {

        String createTableQuery = String.format(
                "CREATE TABLE IF NOT EXISTS %s.historico_tcc (" +
                        "idHistoricoTCC int PRIMARY KEY, " +
                        "idGrupoAluno int, " +
                        "idProfessor int, " +
                        "semestre text, " +
                        "ano int, " +
                        "nota double);",
                keyspace
        );
        executeQuery(createTableQuery);

        for (HistoricoTCC ht : historicoTCCs) {
            String insertQuery = String.format(
                    "INSERT INTO %s.historico_tcc (idHistoricoTCC, idGrupoAluno, idProfessor, semestre, ano, nota) VALUES (%d, %d, %d, '%s', %d, %f);",
                    keyspace,
                    ht.getIdHistoricoTCC(),
                    ht.getIdGrupoAluno(),
                    ht.getIdProfessor(),
                    ht.getSemestre(),
                    ht.getAno(),
                    ht.getNota()
            );
            executeQuery(insertQuery);
        }
    }

    private void insertHistoricoProfessores(List<HistoricoProfessor> historicoProfessores) {

        String createTableQuery = String.format(
                "CREATE TABLE IF NOT EXISTS %s.historico_professores (" +
                        "idHistoricoProfessor int PRIMARY KEY, " +
                        "idProfessor int, " +
                        "idDisciplina int, " +
                        "semestre text, " +
                        "ano int);",
                keyspace
        );
        executeQuery(createTableQuery);

        for (HistoricoProfessor hp : historicoProfessores) {
            String insertQuery = String.format(
                    "INSERT INTO %s.historico_professores (idHistoricoProfessor, idProfessor, idDisciplina, semestre, ano) VALUES (%d, %d, %d, '%s', %d);",
                    keyspace,
                    hp.getIdHistoricoProfessor(),
                    hp.getIdProfessor(),
                    hp.getIdDisciplina(),
                    hp.getSemestre(),
                    hp.getAno()
            );
            executeQuery(insertQuery);
        }
    }

    public void showAllData() {
        // Lista de tabelas que serão exibidas
        String[] tables = {
                "professores",
                "departamentos",
                "cursos",
                "alunos",
                "disciplinas",
                "matriz_curricular",
                "historico_alunos",
                "grupo_alunos",
                "historico_tcc",
                "historico_professores"
        };

        for (String table : tables) {
            System.out.println("Tabela: " + table);
            String query = String.format("SELECT * FROM %s.%s;", keyspace, table);
            try {
                ResultSet resultSet = session.execute(query);
                for (Row row : resultSet) {
                    System.out.println(row.getFormattedContents());
                }
            } catch (Exception e) {
                System.out.println("Erro ao mostrar dados da tabela " + table + ": " + e.getMessage());
            }
        }
    }

    //Histórico Escolar de Alunos: Permite gerar o histórico escolar de qualquer aluno,
    // listando as disciplinas cursadas, semestre, ano e nota final.
    public void buscarHistoricoEscolarAluno(String nomeAluno) {
        System.out.println("Histórico escolar de qualquer aluno -> " + nomeAluno);
        String queryAluno = String.format("SELECT * FROM %s.alunos WHERE nome = '%s' ALLOW FILTERING;", keyspace, nomeAluno);
        ResultSet resultSetAluno = session.execute(queryAluno);
        for (Row aluno : resultSetAluno) {
            int idAluno = aluno.getInt("idAluno");
            int idCurso = aluno.getInt("idCurso");
            String nome = aluno.getString("nome");
            String sobrenome = aluno.getString("sobrenome");

            String queryHistAluno = String.format("SELECT * FROM %s.historico_alunos WHERE idAluno = %d ALLOW FILTERING;", keyspace, idAluno);
            ResultSet resultHistAluno = session.execute(queryHistAluno);

            for (Row histAluno : resultHistAluno) {
                int idDisciplina = histAluno.getInt("iddisciplina");
                double media = histAluno.getDouble("media");
                String semestre = histAluno.getString("semestre");
                int ano = histAluno.getInt("ano");

                String queryDisciplina = String.format(
                        "SELECT * FROM %s.disciplinas WHERE iddisciplina = %d ALLOW FILTERING;", keyspace, idDisciplina);
                ResultSet resultSetDisciplina = session.execute(queryDisciplina);

                for (Row disciplina : resultSetDisciplina) {
                    String tituloDisciplina = disciplina.getString("titulo");

                    System.out.printf("Aluno: %s %s, ID Disciplina: %s, Titulo Disciplina: %s, Semestre: %s, Ano: %d, Média: %.2f%n",
                            nome, sobrenome, idDisciplina, tituloDisciplina, semestre, ano, media);
                }

            }
        }


    }

    //Histórico de Disciplinas Ministradas por Professores: Mostra as disciplinas ministradas
    // por um determinado professor, com informações sobre o semestre e ano.
    public void buscarHistoricoDisciplinasProfessor(String nomeProfessor) {
        System.out.println("Histórico de disciplinas de um professor -> " + nomeProfessor);

        String queryProfessor = String.format("SELECT * FROM %s.professores WHERE nome = '%s' ALLOW FILTERING;", keyspace, nomeProfessor);
        ResultSet resultSetProfessor = session.execute(queryProfessor);

        for (Row professor : resultSetProfessor) {
            String nome = professor.getString("nome");
            String sobrenome = professor.getString("sobrenome");
            int idProfessor = professor.getInt("idProfessor");

            String queryHistProf = String.format("SELECT * FROM %s.historico_professores WHERE idprofessor = %d ALLOW FILTERING;", keyspace, idProfessor);
            ResultSet resultHistProf = session.execute(queryHistProf);

            for (Row histProfessor : resultHistProf) {
                int idDisciplina = histProfessor.getInt("iddisciplina");
                String semestre = histProfessor.getString("semestre");
                int ano = histProfessor.getInt("ano");

                String queryDisciplina = String.format("SELECT * FROM %s.disciplinas WHERE iddisciplina = %d ALLOW FILTERING;", keyspace, idDisciplina);
                ResultSet resultDisciplina = session.execute(queryDisciplina);

                for (Row disciplina : resultDisciplina) {
                    String titulo = disciplina.getString("titulo");

                    System.out.printf("Professor: %s %s, Disciplina: %s, Semestre: %s, Ano: %d%n",
                            nome, sobrenome, titulo, semestre, ano);

                }
            }

        }

    }

    //Alunos Formados em um Semestre Específico: Lista os alunos que se formaram em um
    // determinado semestre de um ano, ou seja, foram aprovados em todos os cursos da
    // matriz curricular.
    public void listarAlunosFormadosNoAno(int ano) {
        System.out.println("Listar alunos formados no ano " + ano);

        String histAlunosParaVerificacao = String.format(
                "SELECT * FROM %s.historico_alunos WHERE ano = %d ALLOW FILTERING;", keyspace, ano);
        ResultSet resultHistAlunosParaVerificacao = session.execute(histAlunosParaVerificacao);

        for (Row histAlunoParaVerificar : resultHistAlunosParaVerificacao) {
            double mediaParaVerificar = histAlunoParaVerificar.getDouble("media");
            String semestreParaVerificar = histAlunoParaVerificar.getString("semestre");
            //Verifica se passou no segundo semestre
            if (mediaParaVerificar >= 4.9 && "Segundo".equals(semestreParaVerificar)) {
                int idAluno = histAlunoParaVerificar.getInt("idAluno");

                String queryAluno = String.format("SELECT * FROM %s.alunos WHERE idAluno = %d ALLOW FILTERING;", keyspace, idAluno);
                Row resultSetAluno = session.execute(queryAluno).one();

                String nome = resultSetAluno.getString("nome");
                String sobrenome = resultSetAluno.getString("sobrenome");
                int idCurso = resultSetAluno.getInt("idCurso");

                String queryCurso = String.format("SELECT * FROM %s.cursos WHERE idCurso = %d ALLOW FILTERING;", keyspace, idCurso);
                ResultSet resultSetCurso = session.execute(queryCurso);

                String tituloCurso = resultSetCurso.one().getString("titulo");

                String histAlunos = String.format(
                        "SELECT * FROM %s.historico_alunos WHERE idaluno = %d ALLOW FILTERING;", keyspace, idAluno);
                ResultSet resultHistAlunos = session.execute(histAlunos);

                ArrayList<ArrayList<String>> todosOsDadosDoHistAluno = new ArrayList<ArrayList<String>>();

                for (Row histAluno : resultHistAlunos) {

                    int idDisciplina = histAluno.getInt("idDisciplina");
                    double media = histAluno.getDouble("media");
                    String semestre = histAluno.getString("semestre");

                    String queryDisciplina = String.format("SELECT * FROM %s.disciplinas WHERE iddisciplina = %d ALLOW FILTERING;", keyspace, idDisciplina);
                    ResultSet resultSetDisciplina = session.execute(queryDisciplina);

                    String tituloDisciplina = resultSetDisciplina.one().getString("titulo");
                    ArrayList<String> dadosHistAluno = new ArrayList<String>();
                    dadosHistAluno.add(tituloDisciplina);
                    dadosHistAluno.add(semestre);
                    dadosHistAluno.add(String.valueOf(ano));
                    dadosHistAluno.add(String.valueOf(media));
                    todosOsDadosDoHistAluno.add(dadosHistAluno);
                }

                System.out.printf("Aluno: %s %s, Curso: %s, Historico: {",
                        nome, sobrenome, tituloCurso);

                for (ArrayList<String> dadosHistDoAluno : todosOsDadosDoHistAluno) {
                    System.out.printf("Disciplina: %s, Semestre: %s, Ano: %s, Média: %s | ",
                            dadosHistDoAluno.get(0), dadosHistDoAluno.get(1), dadosHistDoAluno.get(2), dadosHistDoAluno.get(3));
                }
                System.out.println("}");

            }
        }

    }

    //Professores que são Chefes de Departamento: Apresenta uma lista dos professores
    // que ocupam o cargo de chefe de departamento, juntamente com o nome do
    // departamento que lideram.
    public void listarChefesDepartamentos() {
        System.out.println("Lista de chefes de departamento");

        String queryDepartamentos = String.format("SELECT * FROM %s.departamentos ALLOW FILTERING;", keyspace);
        ResultSet resultDepartamentos = session.execute(queryDepartamentos);

        for (Row departamento : resultDepartamentos) {
            int idProfessor = departamento.getInt("idProfessor");
            String titulo = departamento.getString("titulo");

            String queryProfessor = String.format("SELECT * FROM %s.professores WHERE idProfessor = %d ALLOW FILTERING;", keyspace, idProfessor);
            Row professor = session.execute(queryProfessor).one();

            String nomeProfessor = professor.getString("nome");
            String sobrenomeProfessor = professor.getString("sobrenome");

            System.out.printf("Departamento: %s, Chefe: %s %s%n", titulo, nomeProfessor, sobrenomeProfessor);
        }

    }

    //Grupos de TCC e Orientadores: Permite identificar quais alunos formaram
    // grupos de Trabalho de Conclusão de Curso (TCC) e quem foi o professor
    // orientador de cada grupo.
    public void listarAlunosEOrientadoresTCC() {
        System.out.println("Alunos que formaram um grupo de TCC e seu professor orientador");

        String queryGruposAlunos = String.format("SELECT * FROM %s.grupo_alunos;", keyspace);
        ResultSet resultSetGrupoAlunos = session.execute(queryGruposAlunos);
        HashMap<Integer, ArrayList<String>> listaDeAlunosNoGrupo = new HashMap<Integer, ArrayList<String>>();
        HashMap<Integer, String> listaDeOrientadores = new HashMap<Integer, String>();


        for (Row grupoAluno : resultSetGrupoAlunos) {
            int idGrupo = grupoAluno.getInt("idgrupo");
            int idAluno = grupoAluno.getInt("idAluno");

            String queryAluno = String.format("SELECT * FROM %s.alunos WHERE idAluno = %d ALLOW FILTERING;", keyspace, idAluno);
            Row resultAluno = session.execute(queryAluno).one();
            String nomeAluno = resultAluno.getString("nome");
            String sobrenomeAluno = resultAluno.getString("sobrenome");


            if (listaDeAlunosNoGrupo.get(idGrupo) != null) {
                listaDeAlunosNoGrupo.get(idGrupo).add(nomeAluno + " " + sobrenomeAluno);
            } else {
                ArrayList<String> grupo = new ArrayList<String>();
                grupo.add(nomeAluno + " " + sobrenomeAluno);
                listaDeAlunosNoGrupo.put(idGrupo, grupo);
            }

            if (listaDeOrientadores.get(idGrupo) == null) {
                int idGrupoAluno = grupoAluno.getInt("idgrupoaluno");
                String queryHistTcc = String.format("SELECT * FROM %s.historico_tcc WHERE idgrupoaluno = %d ALLOW FILTERING;", keyspace, idGrupoAluno);
                ResultSet resultHistTcc = session.execute(queryHistTcc);
                int idProfessor = resultHistTcc.one().getInt("idprofessor");

                String queryProfessor = String.format("SELECT * FROM %s.professores WHERE idProfessor = %d ALLOW FILTERING;", keyspace, idProfessor);
                Row professor = session.execute(queryProfessor).one();
                String nomeProfessor = professor.getString("nome");
                String sobrenomeProfessor = professor.getString("sobrenome");
                listaDeOrientadores.put(idGrupo, nomeProfessor + " " + sobrenomeProfessor);
            }
        }
        for (Integer idGrupo : listaDeAlunosNoGrupo.keySet()) {
            System.out.printf("Id do grupo: " + idGrupo);
            System.out.printf(", Professor Orientador: " + listaDeOrientadores.get(idGrupo));
            System.out.printf(", Alunos: {");
            for (String nomeAluno : listaDeAlunosNoGrupo.get(idGrupo)) {
                System.out.print(nomeAluno + ", ");
            }
            System.out.println("}");
        }

    }


}
