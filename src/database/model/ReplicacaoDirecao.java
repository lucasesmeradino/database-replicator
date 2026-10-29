package database.model;

public class ReplicacaoDirecao {

    private long id;
    private String conexaoOrigem;
    private String conexaoDestino;
    private String usuarioOrigem;
    private String usuarioDestino;
    private String senhaOrigem;
    private String senhaDestino;
    private boolean habilitado;
    private long processoId;

    public ReplicacaoDirecao() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getConexaoOrigem() {
        return conexaoOrigem;
    }

    public void setConexaoOrigem(String conexaoOrigem) {
        this.conexaoOrigem = conexaoOrigem;
    }

    public String getConexaoDestino() {
        return conexaoDestino;
    }

    public void setConexaoDestino(String conexaoDestino) {
        this.conexaoDestino = conexaoDestino;
    }

    public String getUsuarioOrigem() {
        return usuarioOrigem;
    }

    public void setUsuarioOrigem(String usuarioOrigem) {
        this.usuarioOrigem = usuarioOrigem;
    }

    public String getUsuarioDestino() {
        return usuarioDestino;
    }

    public void setUsuarioDestino(String usuarioDestino) {
        this.usuarioDestino = usuarioDestino;
    }

    public String getSenhaOrigem() {
        return senhaOrigem;
    }

    public void setSenhaOrigem(String senhaOrigem) {
        this.senhaOrigem = senhaOrigem;
    }

    public String getSenhaDestino() {
        return senhaDestino;
    }

    public void setSenhaDestino(String senhaDestino) {
        this.senhaDestino = senhaDestino;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public long getProcessoId() {
        return processoId;
    }

    public void setProcessoId(long processoId) {
        this.processoId = processoId;
    }

    @Override
    public String toString() {
        return "ReplicacoDirecao{" +
                "id=" + id +
                ", conexaoOrigem='" + conexaoOrigem + '\'' +
                ", conexaoDestino='" + conexaoDestino + '\'' +
                ", conexaoOrigem='" + conexaoOrigem + '\'' +
                ", usuarioOrigem='" + usuarioOrigem + '\'' +
                ", usuarioDestino='" + usuarioDestino + '\'' +
                ", senhaOrigem='" + senhaOrigem + '\'' +
                ", senhaDestino='" + senhaDestino + '\'' +
                ", habilitado=" + habilitado +
                ", processoId=" + processoId +
                '}';
    }
}
