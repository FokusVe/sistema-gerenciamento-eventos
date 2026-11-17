package model;

/**
 * Representa um participante que pode se inscrever em eventos.
 */
public class Participante {

    private int codigo;
    private String nome;
    private String contato;

    public Participante(int codigo, String nome, String contato) {
        setCodigo(codigo);
        setNome(nome);
        setContato(contato);
    }

    public int getCodigo() {
        return codigo;
    }

    private void setCodigo(int codigo) {
        if (codigo <= 0) {
            throw new RegraNegocioException("O código do participante deve ser maior que zero.");
        }
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new RegraNegocioException("Informe o nome do participante.");
        }
        this.nome = nome.trim();
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        if (contato == null || contato.trim().isEmpty()) {
            throw new RegraNegocioException("Informe o contato do participante (e-mail ou telefone).");
        }
        this.contato = contato.trim();
    }

    /** Texto mostrado nos JComboBox. */
    @Override
    public String toString() {
        return codigo + " - " + nome;
    }
}
