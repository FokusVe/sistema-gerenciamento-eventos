package controller;

import java.util.ArrayList;
import model.Evento;
import model.GerenciadorEventos;
import model.Participante;
import model.RegraNegocioException;

/**
 * Coordena as inscrições: inscrever, cancelar e listar.
 * As regras (duplicidade e vagas) ficam na classe Evento (Model).
 */
public class InscricaoController {

    private final GerenciadorEventos gerenciador;

    public InscricaoController(GerenciadorEventos gerenciador) {
        this.gerenciador = gerenciador;
    }

    public void inscrever(Evento evento, Participante participante) {
        if (evento == null) {
            throw new RegraNegocioException("Selecione um evento.");
        }
        if (participante == null) {
            throw new RegraNegocioException("Selecione um participante.");
        }
        evento.inscrever(participante);
    }

    public void cancelar(int codigoEvento, int codigoParticipante) {
        Evento evento = gerenciador.buscarEvento(codigoEvento);
        Participante participante = gerenciador.buscarParticipante(codigoParticipante);
        if (evento == null || participante == null) {
            throw new RegraNegocioException("Inscrição não encontrada.");
        }
        evento.cancelarInscricao(participante);
    }

    public ArrayList<Evento> listarEventos() {
        return gerenciador.listarEventos();
    }

    public ArrayList<Participante> listarParticipantes() {
        return gerenciador.listarParticipantes();
    }
}
