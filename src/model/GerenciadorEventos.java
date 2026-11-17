package model;

import java.util.ArrayList;

/**
 * "Banco de dados" em memória do sistema.
 * Guarda eventos e participantes em ArrayList enquanto a aplicação estiver aberta.
 * Uma única instância é criada no Main e compartilhada por todos os controllers,
 * por isso os dados continuam disponíveis ao fechar e reabrir as janelas.
 */
public class GerenciadorEventos {

    private final ArrayList<Evento> eventos = new ArrayList<>();
    private final ArrayList<Participante> participantes = new ArrayList<>();

    // ============================ EVENTOS ============================

    public void cadastrarEvento(Evento evento) {
        if (buscarEvento(evento.getCodigo()) != null) {
            throw new RegraNegocioException("Já existe um evento com o código " + evento.getCodigo() + ".");
        }
        eventos.add(evento);
    }

    /** Localiza um evento percorrendo a lista. Retorna null se não encontrar. */
    public Evento buscarEvento(int codigo) {
        for (Evento evento : eventos) {
            if (evento.getCodigo() == codigo) {
                return evento;
            }
        }
        return null;
    }

    public void atualizarEvento(int codigo, String nome, String data, String local, int capacidade) {
        Evento evento = buscarEvento(codigo);
        if (evento == null) {
            throw new RegraNegocioException("Evento com código " + codigo + " não encontrado.");
        }
        // Valida tudo em um objeto temporário antes de alterar o evento real,
        // assim um erro não deixa o evento "pela metade".
        new Evento(codigo, nome, data, local, capacidade);
        evento.setCapacidade(capacidade); // verifica se não fica abaixo dos inscritos
        evento.setNome(nome);
        evento.setData(data);
        evento.setLocal(local);
    }

    public void excluirEvento(int codigo) {
        Evento evento = buscarEvento(codigo);
        if (evento == null) {
            throw new RegraNegocioException("Evento com código " + codigo + " não encontrado.");
        }
        eventos.remove(evento);
    }

    public ArrayList<Evento> listarEventos() {
        return new ArrayList<>(eventos);
    }

    // ========================== PARTICIPANTES ==========================

    public void cadastrarParticipante(Participante participante) {
        if (buscarParticipante(participante.getCodigo()) != null) {
            throw new RegraNegocioException("Já existe um participante com o código "
                    + participante.getCodigo() + ".");
        }
        participantes.add(participante);
    }

    public Participante buscarParticipante(int codigo) {
        for (Participante participante : participantes) {
            if (participante.getCodigo() == codigo) {
                return participante;
            }
        }
        return null;
    }

    public void atualizarParticipante(int codigo, String nome, String contato) {
        Participante participante = buscarParticipante(codigo);
        if (participante == null) {
            throw new RegraNegocioException("Participante com código " + codigo + " não encontrado.");
        }
        new Participante(codigo, nome, contato); // valida antes de alterar
        participante.setNome(nome);
        participante.setContato(contato);
    }

    /** Só permite excluir participante que não esteja inscrito em nenhum evento. */
    public void excluirParticipante(int codigo) {
        Participante participante = buscarParticipante(codigo);
        if (participante == null) {
            throw new RegraNegocioException("Participante com código " + codigo + " não encontrado.");
        }
        int total = eventosDoParticipante(participante).size();
        if (total > 0) {
            throw new RegraNegocioException("Não é possível excluir: o participante está inscrito em "
                    + total + " evento(s). Cancele as inscrições primeiro.");
        }
        participantes.remove(participante);
    }

    public ArrayList<Participante> listarParticipantes() {
        return new ArrayList<>(participantes);
    }

    /** Percorre os eventos e devolve aqueles em que o participante está inscrito. */
    public ArrayList<Evento> eventosDoParticipante(Participante participante) {
        ArrayList<Evento> resultado = new ArrayList<>();
        for (Evento evento : eventos) {
            if (evento.estaInscrito(participante)) {
                resultado.add(evento);
            }
        }
        return resultado;
    }

    /** Soma as inscrições de todos os eventos. */
    public int getTotalInscricoes() {
        int total = 0;
        for (Evento evento : eventos) {
            total += evento.getQuantidadeInscritos();
        }
        return total;
    }
}
