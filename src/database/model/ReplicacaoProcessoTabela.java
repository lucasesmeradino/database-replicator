package database.model;

public class ReplicacaoProcessoTabela {

    private long id;
    private long processoId;
    private String tabelaOrigem;
    private String tabelaDestino;
    private int ordem;
    private boolean habilitado;
    private String dsWhere;

    public ReplicacaoProcessoTabela() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProcessoId() {
        return processoId;
    }

    public void setProcessoId(long processoId) {
        this.processoId = processoId;
    }

    public String getTabelaOrigem() {
        return tabelaOrigem;
    }

    public void setTabelaOrigem(String tabelaOrigem) {
        this.tabelaOrigem = tabelaOrigem;
    }

    public String getTabelaDestino() {
        return tabelaDestino;
    }

    public void setTabelaDestino(String tabelaDestino) {
        this.tabelaDestino = tabelaDestino;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public String getDsWhere() {
        return dsWhere;
    }

    public void setDsWhere(String dsWhere) {
        this.dsWhere = dsWhere;
    }

    @Override
    public String toString() {
        return "ReplicacaoProcessoTabela{" +
                "id=" + id +
                ", processoId=" + processoId +
                ", tabelaOrigem='" + tabelaOrigem + '\'' +
                ", tabelaDestino='" + tabelaDestino + '\'' +
                ", ordem=" + ordem +
                ", habilitado=" + habilitado +
                ", dsWhere='" + dsWhere + '\'' +
                '}';
    }
}
