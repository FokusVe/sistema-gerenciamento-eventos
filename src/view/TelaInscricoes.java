package view;

import controller.InscricaoController;
import controller.RelatorioController;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import model.Evento;
import model.Participante;
import model.RegraNegocioException;

/**
 * Tela de inscrições: associa participantes a eventos e cancela inscrições.
 */
public class TelaInscricoes extends TelaSecundaria {

    private final InscricaoController controller;

    private JComboBox<Evento> cbEvento;
    private JComboBox<Participante> cbParticipante;
    private JLabel lblOcupacaoNumero;
    private JLabel lblVagas;
    private Estilo.BarraOcupacao barraOcupacao;
    private JLabel lblContador;
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    public TelaInscricoes(TelaPrincipal telaPrincipal, InscricaoController controller) {
        super("Inscrições", telaPrincipal);
        this.controller = controller;
        montarCabecalho("Inscrições", "Associe participantes aos eventos e controle as vagas",
                Icone.Tipo.INSCRICAO);
        montarTela();
        carregarCombos();
        atualizarTabela();
        ajustarTamanho(1300, 700);
    }

    private void montarTela() {
        JPanel corpo = criarCorpo();

        // ================= card de nova inscrição =================
        Estilo.Card cardFormulario = new Estilo.Card();
        cardFormulario.setPreferredSize(new Dimension(360, 0));

        JPanel tituloForm = new JPanel(new GridLayout(2, 1, 0, 2));
        tituloForm.setOpaque(false);
        tituloForm.add(Estilo.titulo("Nova inscrição", 18));
        tituloForm.add(Estilo.textoSuave("Escolha o evento e o participante"));

        cbEvento = Estilo.combo();
        cbParticipante = Estilo.combo();
        cbEvento.addActionListener(e -> atualizarInfoVagas());

        JPanel campos = Estilo.formulario(
                "Evento", cbEvento,
                "Participante", cbParticipante);

        // caixa com a situação de vagas do evento selecionado
        JPanel situacao = new JPanel();
        situacao.setLayout(new BoxLayout(situacao, BoxLayout.Y_AXIS));
        situacao.setBackground(Estilo.LINHA_ALTERNADA);
        situacao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDA), new EmptyBorder(14, 16, 14, 16)));
        situacao.setAlignmentX(LEFT_ALIGNMENT);
        situacao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        JLabel rotuloSituacao = Estilo.rotulo("Ocupação do evento");
        lblOcupacaoNumero = new JLabel("-");
        lblOcupacaoNumero.setFont(Estilo.fonte(Font.BOLD, 24));
        lblOcupacaoNumero.setForeground(Estilo.TEXTO);
        barraOcupacao = new Estilo.BarraOcupacao();
        barraOcupacao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        lblVagas = new JLabel(" ");
        lblVagas.setFont(Estilo.fonte(Font.BOLD, 13));
        for (javax.swing.JComponent c : new javax.swing.JComponent[]{
                rotuloSituacao, lblOcupacaoNumero, barraOcupacao, lblVagas}) {
            c.setAlignmentX(LEFT_ALIGNMENT);
        }
        situacao.add(rotuloSituacao);
        situacao.add(Box.createVerticalStrut(4));
        situacao.add(lblOcupacaoNumero);
        situacao.add(Box.createVerticalStrut(6));
        situacao.add(barraOcupacao);
        situacao.add(Box.createVerticalStrut(6));
        situacao.add(lblVagas);

        JPanel meio = new JPanel();
        meio.setOpaque(false);
        meio.setLayout(new BoxLayout(meio, BoxLayout.Y_AXIS));
        campos.setAlignmentX(LEFT_ALIGNMENT);
        meio.add(campos);
        meio.add(situacao);
        meio.add(Box.createVerticalGlue());

        Estilo.Botao btnInscrever = Estilo.botao("✓  Confirmar inscrição", Estilo.SUCESSO);
        btnInscrever.addActionListener(e -> inscrever());

        cardFormulario.add(tituloForm, BorderLayout.NORTH);
        cardFormulario.add(meio, BorderLayout.CENTER);
        cardFormulario.add(btnInscrever, BorderLayout.SOUTH);

        // ================= card da tabela =================
        Estilo.Card cardTabela = new Estilo.Card();

        modeloTabela = criarModeloTabela(new String[]{
                "Cód.", "Evento", "Data", "Cód. part.", "Participante", "Contato"});
        tabela = new JTable(modeloTabela);
        Estilo.estilizarTabela(tabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        definirLarguras(tabela, 50, 200, 100, 85, 150, 190);

        Estilo.Botao btnCancelar = Estilo.botaoContorno("Cancelar inscrição", Estilo.PERIGO);
        btnCancelar.addActionListener(e -> cancelarInscricao());

        JPanel topoTabela = new JPanel(new BorderLayout());
        topoTabela.setOpaque(false);
        JPanel titulos = new JPanel(new GridLayout(2, 1, 0, 2));
        titulos.setOpaque(false);
        titulos.add(Estilo.titulo("Inscrições realizadas", 18));
        lblContador = Estilo.textoSuave("");
        titulos.add(lblContador);
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        acoes.setOpaque(false);
        acoes.add(Estilo.campoBusca(tabela));
        acoes.add(btnCancelar);
        topoTabela.add(titulos, BorderLayout.WEST);
        topoTabela.add(acoes, BorderLayout.EAST);

        cardTabela.add(topoTabela, BorderLayout.NORTH);
        cardTabela.add(Estilo.rolagem(tabela), BorderLayout.CENTER);
        cardTabela.add(Estilo.textoSuave("Dica: selecione uma inscrição e clique em "
                + "\"Cancelar inscrição\" para liberar a vaga."), BorderLayout.SOUTH);

        corpo.add(cardFormulario, BorderLayout.WEST);
        corpo.add(cardTabela, BorderLayout.CENTER);
        add(corpo, BorderLayout.CENTER);
    }

    private void carregarCombos() {
        cbEvento.removeAllItems();
        for (Evento evento : controller.listarEventos()) {
            cbEvento.addItem(evento);
        }
        cbParticipante.removeAllItems();
        for (Participante participante : controller.listarParticipantes()) {
            cbParticipante.addItem(participante);
        }
        atualizarInfoVagas();
    }

    /** Mostra capacidade, inscritos e vagas do evento escolhido no combo. */
    private void atualizarInfoVagas() {
        Evento evento = (Evento) cbEvento.getSelectedItem();
        if (evento == null) {
            lblOcupacaoNumero.setText("-");
            barraOcupacao.setPercentual(0);
            lblVagas.setText("Nenhum evento cadastrado");
            lblVagas.setForeground(Estilo.TEXTO_SUAVE);
            return;
        }
        int percentual = RelatorioController.percentualOcupacao(evento);
        lblOcupacaoNumero.setText(evento.getQuantidadeInscritos() + " / " + evento.getCapacidade() + " inscritos");
        barraOcupacao.setPercentual(percentual);
        if (evento.isLotado()) {
            lblVagas.setText("Evento lotado — sem vagas");
        } else {
            int vagas = evento.getVagasDisponiveis();
            lblVagas.setText(vagas + (vagas == 1 ? " vaga disponível" : " vagas disponíveis"));
        }
        lblVagas.setForeground(Estilo.corOcupacao(percentual).darker());
    }

    private void inscrever() {
        try {
            Evento evento = (Evento) cbEvento.getSelectedItem();
            Participante participante = (Participante) cbParticipante.getSelectedItem();
            controller.inscrever(evento, participante);
            mostrarSucesso(participante.getNome() + " foi inscrito(a) em " + evento.getNome() + "!");
            atualizarTabela();
            atualizarInfoVagas();
        } catch (RegraNegocioException ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private void cancelarInscricao() {
        int linhaVisivel = tabela.getSelectedRow();
        if (linhaVisivel < 0) {
            mostrarErro("Selecione uma inscrição na tabela para cancelar.");
            return;
        }
        int linha = tabela.convertRowIndexToModel(linhaVisivel);
        int codEvento = (Integer) modeloTabela.getValueAt(linha, 0);
        String nomeEvento = modeloTabela.getValueAt(linha, 1).toString();
        int codParticipante = (Integer) modeloTabela.getValueAt(linha, 3);
        String nomeParticipante = modeloTabela.getValueAt(linha, 4).toString();

        if (!confirmar("Cancelar a inscrição de " + nomeParticipante + " no evento " + nomeEvento + "?")) {
            return;
        }
        try {
            controller.cancelar(codEvento, codParticipante);
            mostrarSucesso("Inscrição cancelada. A vaga foi liberada.");
            atualizarTabela();
            atualizarInfoVagas();
        } catch (RegraNegocioException ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private void atualizarTabela() {
        modeloTabela.setRowCount(0);
        for (Evento evento : controller.listarEventos()) {
            for (Participante p : evento.getInscritos()) {
                modeloTabela.addRow(new Object[]{
                        evento.getCodigo(), evento.getNome(), evento.getData(),
                        p.getCodigo(), p.getNome(), p.getContato()});
            }
        }
        int total = modeloTabela.getRowCount();
        lblContador.setText(total + (total == 1 ? " inscrição ativa" : " inscrições ativas"));
    }
}
