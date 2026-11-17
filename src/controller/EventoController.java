package controller;

import java.util.ArrayList;
import model.Evento;
import model.GerenciadorEventos;

/**
 * Recebe as ações da tela de cadastro de eventos e repassa ao Model.
 */
public class EventoController {

    private final GerenciadorEventos gerenciador;

    public EventoController(GerenciadorEventos gerenciador) {
        this.gerenciador = gerenciador;
    }

    public void cadastrar(String codigo, String nome, String data, String local, String capacidade) {
        int cod = Conversor.paraInteiro(codigo, "Código");
        int cap = Conversor.paraInteiro(capacidade, "Capacidade");
        gerenciador.cadastrarEvento(new Evento(cod, nome, data, local, cap));
    }

    public void atualizar(String codigo, String nome, String data, String local, String capacidade) {
        int cod = Conversor.paraInteiro(codigo, "Código");
        int cap = Conversor.paraInteiro(capacidade, "Capacidade");
        gerenciador.atualizarEvento(cod, nome, data, local, cap);
    }

    public void excluir(String codigo) {
        gerenciador.excluirEvento(Conversor.paraInteiro(codigo, "Código"));
    }

    public Evento buscar(String codigo) {
        return gerenciador.buscarEvento(Conversor.paraInteiro(codigo, "Código"));
    }

    public ArrayList<Evento> listar() {
        return gerenciador.listarEventos();
    }
}
