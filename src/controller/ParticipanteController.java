package controller;

import java.util.ArrayList;
import model.GerenciadorEventos;
import model.Participante;

/**
 * Recebe as ações da tela de cadastro de participantes e repassa ao Model.
 */
public class ParticipanteController {

    private final GerenciadorEventos gerenciador;

    public ParticipanteController(GerenciadorEventos gerenciador) {
        this.gerenciador = gerenciador;
    }

    public void cadastrar(String codigo, String nome, String contato) {
        int cod = Conversor.paraInteiro(codigo, "Código");
        gerenciador.cadastrarParticipante(new Participante(cod, nome, contato));
    }

    public void atualizar(String codigo, String nome, String contato) {
        int cod = Conversor.paraInteiro(codigo, "Código");
        gerenciador.atualizarParticipante(cod, nome, contato);
    }

    public void excluir(String codigo) {
        gerenciador.excluirParticipante(Conversor.paraInteiro(codigo, "Código"));
    }

    public ArrayList<Participante> listar() {
        return gerenciador.listarParticipantes();
    }

    /** Quantidade de eventos em que o participante está inscrito (mostrado na tabela). */
    public int quantidadeDeEventos(Participante participante) {
        return gerenciador.eventosDoParticipante(participante).size();
    }
}
