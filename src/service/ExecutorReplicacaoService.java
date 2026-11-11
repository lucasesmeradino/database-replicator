package service;

import database.dao.*;
import database.dao.result.QueryResult;
import database.model.PendenciaReplicacao;
import database.model.ReplicacaoDirecao;
import database.model.ReplicacaoProcesso;
import database.model.ReplicacaoProcessoTabela;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExecutorReplicacaoService {

    private Connection conexaoBancoControle;

    private Connection conexaoBancoOrigem;

    private Connection conexaoBancoDestino;

    private final ReplicacaoProcessoDAO replicacaoProcessoDao;

    private final ReplicacaoDirecaoDAO replicacaoDirecaoDao;

    private final ReplicacaoProcessoTabelaDAO replicacaoProcessoTabelaDao;

    private final Map<String, String> descricaoOperacaoMap = Map.of("D", "Deleção", "U", "Atualização");

    public ExecutorReplicacaoService(Connection conexaoBancoControle) throws SQLException {
        this.conexaoBancoControle = conexaoBancoControle;
        this.replicacaoProcessoDao = new ReplicacaoProcessoDAO(conexaoBancoControle);
        this.replicacaoDirecaoDao = new ReplicacaoDirecaoDAO(conexaoBancoControle);
        this.replicacaoProcessoTabelaDao = new ReplicacaoProcessoTabelaDAO(conexaoBancoControle);

        System.out.println("Replicação iniciada! Acompanhe os logs da replicação durante a execução.");
        executarReplicacaoRegistrosNovos();
        executarReplicacoesAlteracoes();
        System.out.println("Execução finalizada. Aguardar o início da próxima replicação\n");
    }

    public void executarReplicacaoRegistrosNovos() {
        try {
            List<ReplicacaoProcesso> processos = replicacaoProcessoDao.selectAllProcessosHabilitados();
            if (processos == null || processos.isEmpty()) {
                System.out.println("Não encontrados processos habilitados para execução!");
                return;
            }

            for (ReplicacaoProcesso processo : processos) {
                System.out.printf("Iniciando replicações do processo %s%n", processo.getProcesso());
                List<ReplicacaoDirecao> direcoes = replicacaoDirecaoDao.selectAllDirecoesHabilitadasByProcesso(processo.getId());
                if (direcoes == null || direcoes.isEmpty()) {
                    System.out.printf("Não encontrado direções habilitadas para o processo %s!%n", processo.getProcesso());
                    continue;
                }

                List<ReplicacaoProcessoTabela> tabelas = replicacaoProcessoTabelaDao.selectAllTabelasHabilitadasByProcesso(processo.getId());
                if (tabelas == null || tabelas.isEmpty()) {
                    System.out.printf("Não encontrado tabelas habilitadas para o processo %s!%n", processo.getProcesso());
                    continue;
                }

                for (ReplicacaoDirecao direcao : direcoes) {
                    System.out.printf("Replicando dados direção (%s) -> (%s)%n", direcao.getConexaoOrigem(), direcao.getConexaoDestino());
                    try {
                        conexaoBancoOrigem = DriverManager.getConnection(direcao.getConexaoOrigem(), direcao.getUsuarioOrigem(), direcao.getSenhaOrigem());
                        conexaoBancoDestino = DriverManager.getConnection(direcao.getConexaoDestino(), direcao.getUsuarioDestino(), direcao.getSenhaDestino());
                        if (conexaoBancoOrigem == null) {
                            System.out.printf("Falha ao conectar no banco de origem (%s)%n", direcao.getConexaoOrigem());
                            continue;
                        }
                        if (conexaoBancoDestino == null) {
                            System.out.printf("Falha ao conectar no banco de destino (%s)%n", direcao.getConexaoDestino());
                            continue;
                        }


                        tabelas.forEach(this::executarReplicacaoPorTabela);
                        System.out.printf("Finalizado replicação direção (%s) -> (%s)%n", direcao.getConexaoOrigem(), direcao.getConexaoDestino());
                    } catch (Exception ex) {
                        System.out.printf("Falha ao conectar nos bancos de origem e/ou destino da direção (%s) <---> (%s)%n", direcao.getConexaoOrigem(), direcao.getConexaoDestino());
                    } finally {
                        if (conexaoBancoOrigem != null) {
                            conexaoBancoOrigem.close();
                            conexaoBancoOrigem = null;
                        }
                        if (conexaoBancoDestino != null) {
                            conexaoBancoDestino.close();
                            conexaoBancoDestino = null;
                        }
                    }
                }
                System.out.printf("Finalizado replicações do processo %s%n", processo.getProcesso());
            }
        } catch (Exception ex) {
            System.out.println("Falha na execução da replicação dos novos registros: " + ex.getLocalizedMessage());
            throw new RuntimeException(ex);
        }
    }

    public void executarReplicacaoPorTabela(ReplicacaoProcessoTabela tabela) {
        ReplicacaoDadosOrigemDAO replicacaoDadosOrigemDao = new ReplicacaoDadosOrigemDAO(conexaoBancoOrigem);
        ReplicacaoDadosDestinoDAO replicacaoDadosDestinoDao = new ReplicacaoDadosDestinoDAO(conexaoBancoDestino);
        try {
            QueryResult queryResultDadosOrigem = replicacaoDadosOrigemDao.executeSelectTableByMaxIdDestinationDb(tabela.getTabelaOrigem(),
                    tabela.getDsWhere(), replicacaoDadosDestinoDao.getTableMaxId(tabela.getTabelaDestino()));
            int columnCount = queryResultDadosOrigem.getColumnNames().size();
            String insertBancoDestino = replicacaoDadosDestinoDao.createInsertByResultSet(queryResultDadosOrigem.getColumnNames(), tabela.getTabelaDestino());
            conexaoBancoDestino.setAutoCommit(false);
            try (PreparedStatement psInsertDadosDestino = conexaoBancoDestino.prepareStatement(insertBancoDestino)) {
                for (Map<String, Object> rowColumn : queryResultDadosOrigem.getRows()) {
                    for (int i = 1; i <= columnCount; i++) {
                        psInsertDadosDestino.setObject(i, rowColumn.get(queryResultDadosOrigem.getColumnNames().get(i - 1)));
                    }
                    psInsertDadosDestino.addBatch();
                }
                psInsertDadosDestino.executeBatch();
                conexaoBancoDestino.commit();
            }
            System.out.printf("Replicação dos novos dados da tabela %s para a %s realizada com sucesso!%n", tabela.getTabelaOrigem(), tabela.getTabelaDestino());
        } catch (Exception ex) {
            System.out.printf("Falha na replicação dos dados da tabela %s do banco origem para a %s do banco destino%n", tabela.getTabelaOrigem(), tabela.getTabelaDestino());
            ex.printStackTrace();
        }
    }

    public void executarReplicacoesAlteracoes() {
        try {
            List<ReplicacaoProcesso> processos = replicacaoProcessoDao.selectAllProcessosHabilitados();
            if (processos == null || processos.isEmpty()) {
                return;
            }

            for (ReplicacaoProcesso processo : processos) {
                List<ReplicacaoDirecao> direcoes = replicacaoDirecaoDao.selectAllDirecoesHabilitadasByProcesso(processo.getId());
                if (direcoes == null || direcoes.isEmpty()) {
                    continue;
                }

                for (ReplicacaoDirecao direcao : direcoes) {
                    System.out.printf("Processando atualizações dos registros do banco - %s%n", direcao.getConexaoOrigem());

                    try {
                        conexaoBancoOrigem = DriverManager.getConnection(direcao.getConexaoOrigem(), direcao.getUsuarioOrigem(), direcao.getSenhaOrigem());
                        conexaoBancoDestino = DriverManager.getConnection(direcao.getConexaoDestino(), direcao.getUsuarioDestino(), direcao.getSenhaDestino());

                        ReplicacaoDadosOrigemDAO replicacaoDadosOrigemDao = new ReplicacaoDadosOrigemDAO(conexaoBancoOrigem);
                        ReplicacaoDadosDestinoDAO replicacaoDadosDestinoDao = new ReplicacaoDadosDestinoDAO(conexaoBancoDestino);
                        List<PendenciaReplicacao> pendenciasReplicacao = replicacaoDadosOrigemDao.selectAllUnprocessedReplications();

                        pendenciasReplicacao.forEach(pen -> {
                            executarOperacaoPendenciaReplicacao(pen, processo.getId(), replicacaoDadosOrigemDao, replicacaoDadosDestinoDao);
                        });
                    } catch (SQLException ex) {
                        System.out.printf("Falha ao conectar nos bancos de origem e/ou destino da direção (%s) <---> (%s)%n", direcao.getConexaoOrigem(), direcao.getConexaoDestino());
                    } finally {
                        if (conexaoBancoOrigem != null) {
                            conexaoBancoOrigem.close();
                            conexaoBancoOrigem = null;
                        }
                        if (conexaoBancoDestino != null) {
                            conexaoBancoDestino.close();
                            conexaoBancoDestino = null;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Falha na execução das atualizações de registros: " + ex.getLocalizedMessage());
            throw new RuntimeException(ex);
        }
    }

    private void executarOperacaoPendenciaReplicacao(PendenciaReplicacao pendenciaReplicacao, long processoId, ReplicacaoDadosOrigemDAO replicacaoDadosOrigemDao, ReplicacaoDadosDestinoDAO replicacaoDadosDestinoDao) {

        System.out.printf("Processando pendência do tipo %s, registro %d da tabela %s%n", descricaoOperacaoMap.get(pendenciaReplicacao.getOperation()),
                pendenciaReplicacao.getRowId(), pendenciaReplicacao.getTableName());

        String nomeTabelaBancoDestino = null;
        try {
            nomeTabelaBancoDestino = replicacaoProcessoTabelaDao.selectTabelaDestinoByTabelaOrigem(pendenciaReplicacao.getTableName(), processoId);
        } catch (SQLException e) {
            System.out.printf("Falha na busca da configuração da tabela destino da tabela origem %s: %s%n", pendenciaReplicacao.getTableName(), e.getLocalizedMessage());
        }

        if (nomeTabelaBancoDestino == null || nomeTabelaBancoDestino.isBlank()) {
            System.out.printf("Não encontrado uma configuração de tabela habilitada do processo %d com base no nome da tabela do registro de pendência %d%n", processoId, pendenciaReplicacao.getId());
            return;
        }

        if (pendenciaReplicacao.getOperation().equalsIgnoreCase("D")) {
            try {
                replicacaoDadosDestinoDao.executeDeleteTableById(nomeTabelaBancoDestino, pendenciaReplicacao.getRowId());
            } catch (SQLException e) {
                System.out.printf("Falha na deleção do registro %d da tabela %s: %s%n", pendenciaReplicacao.getId(), pendenciaReplicacao.getTableName(), e.getLocalizedMessage());
                return;
            }
            System.out.printf("Registro %d removido da tabela %s do banco destino%n", pendenciaReplicacao.getRowId(), nomeTabelaBancoDestino);
        } else if (pendenciaReplicacao.getOperation().equalsIgnoreCase("U")) {
            try {
                QueryResult queryResultRegistroOrigem = replicacaoDadosOrigemDao.executeSelectTableById(pendenciaReplicacao.getTableName(), pendenciaReplicacao.getRowId());
                List<String> columnNames = queryResultRegistroOrigem.getColumnNames();

                StringBuilder dsColumnsUpdate = new StringBuilder();
                List<Integer> columnsIndexAdded = new ArrayList<>();
                for (int i = 1 ; i <= columnNames.size(); i++) {
                    String columnName = columnNames.get(i);
                    if (columnName.equalsIgnoreCase("id")) continue;
                    dsColumnsUpdate.append(columnName).append(" = ?, ");
                    columnsIndexAdded.add(i);
                }

                String sqlUpdateDadosDestino = String.format("UPDATE %s SET %s WHERE id = %d", nomeTabelaBancoDestino, dsColumnsUpdate.substring(0, dsColumnsUpdate.length() - 2), processoId);
                try (PreparedStatement psUpdateDadosDestino = conexaoBancoDestino.prepareStatement(sqlUpdateDadosDestino)) {
                    if (!queryResultRegistroOrigem.getRows().isEmpty()) {
                        int parameterIndex = 1;
                        for (Integer columnIndex : columnsIndexAdded) {
                            psUpdateDadosDestino.setObject(parameterIndex++, queryResultRegistroOrigem.getRows().getFirst().get(columnNames.get(columnIndex)));
                        }
                        psUpdateDadosDestino.executeUpdate();
                        System.out.printf("Registro %d da tabela %s atualizado no banco destino%n", pendenciaReplicacao.getRowId(), nomeTabelaBancoDestino);
                    } else {
                        replicacaoDadosDestinoDao.executeDeleteTableById(nomeTabelaBancoDestino, pendenciaReplicacao.getRowId());
                        System.out.printf("Registro %d da tabela %s removido do banco destino por ausência no banco origem%n", pendenciaReplicacao.getRowId(), nomeTabelaBancoDestino);
                    }
                }
            } catch (SQLException e) {
                System.out.printf("Falha na atualização do registro %d da tabela %s: %s%n", pendenciaReplicacao.getRowId(), pendenciaReplicacao.getTableName(), e.getLocalizedMessage());
                return;
            }
        }

        try {
            replicacaoDadosOrigemDao.updateReplicationProcessed(pendenciaReplicacao.getId());
        } catch (SQLException e) {
            System.out.printf("Falha ao marcar pendência de replicação %d como processada %n", pendenciaReplicacao.getId());
        }

    }
}
