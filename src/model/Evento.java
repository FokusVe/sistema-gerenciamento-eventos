package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;

/**
 * Representa um evento acadêmico.
 * Guarda a lista de participantes inscritos e aplica as regras de vagas.
 */
public class Evento {

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private int codigo;
    private String nome;
    private String data;
    private String local;
    private int capacidade;
    private final ArrayList<Participante> inscritos = new ArrayList<>();

    public Evento(int codigo, String nome, String data, String local, int capacidade) {
        setCodigo(codigo);
        setNome(nome);
        setData(data);
        setLocal(local);
        setCapacidade(capacidade);
    }

    // ----------------- getters e setters com validação -----------------

    public int getCodigo() {
        return codigo;
    }

    private void setCodigo(int codigo) {
        if (codigo <= 0) {
            throw new RegraNegocioException("O código do evento deve ser maior que zero.");
        }
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new RegraNegocioException("Informe o nome do evento.");
        }
        this.nome = nome.trim();
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        if (data == null || data.trim().isEmpty()) {
            throw new RegraNegocioException("Informe a data do evento.");
        }
        try {
            LocalDate.parse(data.trim(), FORMATO_DATA);
        } catch (DateTimeParseException e) {
            throw new RegraNegocioException("Data inválida. Use o formato dd/mm/aaaa (ex.: 25/11/2026).");
        }
        this.data = data.trim();
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        if (local == null || local.trim().isEmpty()) {
            throw new RegraNegocioException("Informe o local do evento.");
        }
        this.local = local.trim();
    }

    public int getCapacidade() {
        return capacidade;
    }

    /**
     * Regras: a capacidade não pode ser negativa nem zero,
     * e não pode ficar abaixo do número de inscritos.
     */
    public void setCapacidade(int capacidade) {
        if (capacidade <= 0) {
            throw new RegraNegocioException("A capacidade máxima deve ser maior que zero.");
        }
        if (capacidade < inscritos.size()) {
            throw new RegraNegocioException("A capacidade não pode ser menor que o número de inscritos ("
                    + inscritos.size() + ").");
        }
        this.capacidade = capacidade;
    }

    // ----------------- controle de vagas e inscrições -----------------

    public int getQuantidadeInscritos() {
        return inscritos.size();
    }

    public int getVagasDisponiveis() {
        return capacidade - inscritos.size();
    }

    public boolean isLotado() {
        return getVagasDisponiveis() <= 0;
    }

    public boolean estaInscrito(Participante participante) {
        return inscritos.contains(participante);
    }

    /** Inscreve um participante, impedindo duplicidade e excesso de vagas. */
    public void inscrever(Participante participante) {
        if (participante == null) {
            throw new RegraNegocioException("Selecione um participante.");
        }
        if (estaInscrito(participante)) {
            throw new RegraNegocioException("O participante " + participante.getNome()
                    + " já está inscrito no evento " + nome + ".");
        }
        if (isLotado()) {
            throw new RegraNegocioException("O evento " + nome + " está lotado. Não há vagas disponíveis.");
        }
        inscritos.add(participante);
    }

    /** Cancela a inscrição e libera a vaga. */
    public void cancelarInscricao(Participante participante) {
        if (!estaInscrito(participante)) {
            throw new RegraNegocioException("Esse participante não está inscrito neste evento.");
        }
        inscritos.remove(participante);
    }

    /** Devolve uma cópia da lista para que ninguém altere os inscritos sem passar pelas regras. */
    public ArrayList<Participante> getInscritos() {
        return new ArrayList<>(inscritos);
    }

    /** Texto mostrado nos JComboBox. */
    @Override
    public String toString() {
        return codigo + " - " + nome + " (" + data + ")";
    }
}
