package database.model;

public class ReplicacaoProcesso {

    private long id;
    private String processo;
    private String descricao;
    private boolean habilitado;

    public ReplicacaoProcesso() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProcesso() {
        return processo;
    }

    public void setProcesso(String processo) {
        this.processo = processo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    @Override
    public String toString() {
        return "ReplicacaoProcesso{" +
                "id=" + id +
                ", processo='" + processo + '\'' +
                ", descricao='" + descricao + '\'' +
                ", habilitado=" + habilitado +
                '}';
    }
}
