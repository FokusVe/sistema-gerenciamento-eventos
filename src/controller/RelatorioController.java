package controller;

import java.util.ArrayList;
import model.Evento;
import model.GerenciadorEventos;
import model.Participante;

/**
 * Fornece os dados consolidados para a tela de relatório.
 */
public class RelatorioController {

    private final GerenciadorEventos gerenciador;

    public RelatorioController(GerenciadorEventos gerenciador) {
        this.gerenciador = gerenciador;
    }

    public ArrayList<Evento> listarEventos() {
        return gerenciador.listarEventos();
    }

    public ArrayList<Participante> inscritosDoEvento(int codigoEvento) {
        Evento evento = gerenciador.buscarEvento(codigoEvento);
        if (evento == null) {
            return new ArrayList<>();
        }
        return evento.getInscritos();
    }

    public int totalEventos() {
        return gerenciador.listarEventos().size();
    }

    public int totalParticipantes() {
        return gerenciador.listarParticipantes().size();
    }

    public int totalInscricoes() {
        return gerenciador.getTotalInscricoes();
    }

    /** Soma das vagas livres de todos os eventos. */
    public int totalVagasDisponiveis() {
        int total = 0;
        for (Evento evento : gerenciador.listarEventos()) {
            total += evento.getVagasDisponiveis();
        }
        return total;
    }

    /** Ocupação média (%) considerando a capacidade total de todos os eventos. */
    public int ocupacaoGeral() {
        int capacidade = 0;
        for (Evento evento : gerenciador.listarEventos()) {
            capacidade += evento.getCapacidade();
        }
        return capacidade == 0 ? 0 : (int) Math.round(totalInscricoes() * 100.0 / capacidade);
    }

    /** Percentual de ocupação de um evento (usado nas barras das tabelas). */
    public static int percentualOcupacao(Evento evento) {
        return (int) Math.round(evento.getQuantidadeInscritos() * 100.0 / evento.getCapacidade());
    }
}
