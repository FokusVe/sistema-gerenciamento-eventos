package view;

import controller.ParticipanteController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import model.Participante;
import model.RegraNegocioException;

/**
 * Tela de cadastro e listagem de participantes.
 */
public class TelaCadastroParticipante extends TelaSecundaria {

    private final ParticipanteController controller;

    private Estilo.Campo txtCodigo;
    private Estilo.Campo txtNome;
    private Estilo.Campo txtContato;
    private JLabel lblTituloFormulario;
    private JLabel lblSubtituloFormulario;
    private JLabel lblContador;
    private Estilo.Botao btnCadastrar;
    private Estilo.Botao btnAtualizar;
    private Estilo.Botao btnExcluir;
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    public TelaCadastroParticipante(TelaPrincipal telaPrincipal, ParticipanteController controller) {
        super("Cadastro de Participantes", telaPrincipal);
        this.controller = controller;
        montarCabecalho("Cadastro de Participantes", "Registre quem vai participar dos eventos",
                Icone.Tipo.PARTICIPANTE);
        montarTela();
        atualizarTabela();
        modoEdicao(false);
        ajustarTamanho(1120, 680);
    }

    private void montarTela() {
        JPanel corpo = criarCorpo();

        // ================= card do formulário =================
        Estilo.Card cardFormulario = new Estilo.Card();
        cardFormulario.setPreferredSize(new Dimension(340, 0));

        lblTituloFormulario = Estilo.titulo("Novo participante", 18);
        lblSubtituloFormulario = Estilo.textoSuave("Preencha os dados abaixo");
        JPanel tituloForm = new JPanel(new GridLayout(2, 1, 0, 2));
        tituloForm.setOpaque(false);
        tituloForm.add(lblTituloFormulario);
        tituloForm.add(lblSubtituloFormulario);

        txtCodigo = Estilo.campo("Ex.: 5");
        txtNome = Estilo.campo("Nome completo");
        txtContato = Estilo.campo("E-mail ou telefone");

        JPanel campos = Estilo.formulario(
                "Código", txtCodigo,
                "Nome", txtNome,
                "Contato", txtContato);

        btnCadastrar = Estilo.botao("+  Cadastrar participante", Estilo.AZUL);
        btnAtualizar = Estilo.botaoContorno("Salvar", Estilo.AZUL);
        btnExcluir = Estilo.botaoContorno("Excluir", Estilo.PERIGO);
        Estilo.Botao btnLimpar = new Estilo.Botao("Limpar formulário", Color.WHITE,
                Estilo.LINHA_ALTERNADA, Estilo.TEXTO_SUAVE, null);

        btnCadastrar.addActionListener(e -> cadastrar());
        btnAtualizar.addActionListener(e -> atualizar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limparFormulario());

        JPanel linhaEdicao = new JPanel(new GridLayout(1, 2, 8, 0));
        linhaEdicao.setOpaque(false);
        linhaEdicao.add(btnAtualizar);
        linhaEdicao.add(btnExcluir);

        JPanel botoes = new JPanel(new GridLayout(3, 1, 0, 8));
        botoes.setOpaque(false);
        botoes.add(btnCadastrar);
        botoes.add(linhaEdicao);
        botoes.add(btnLimpar);

        cardFormulario.add(tituloForm, BorderLayout.NORTH);
        cardFormulario.add(campos, BorderLayout.CENTER);
        cardFormulario.add(botoes, BorderLayout.SOUTH);

        // ================= card da tabela =================
        Estilo.Card cardTabela = new Estilo.Card();

        modeloTabela = criarModeloTabela(new String[]{"Cód.", "Nome", "Contato", "Eventos inscritos"});
        tabela = new JTable(modeloTabela);
        Estilo.estilizarTabela(tabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherFormularioComSelecao();
            }
        });
        definirLarguras(tabela, 50, 220, 200, 130);

        JPanel topoTabela = new JPanel(new BorderLayout());
        topoTabela.setOpaque(false);
        JPanel titulos = new JPanel(new GridLayout(2, 1, 0, 2));
        titulos.setOpaque(false);
        titulos.add(Estilo.titulo("Participantes cadastrados", 18));
        lblContador = Estilo.textoSuave("");
        titulos.add(lblContador);
        topoTabela.add(titulos, BorderLayout.WEST);
        JPanel caixaBusca = new JPanel(new java.awt.GridBagLayout());
        caixaBusca.setOpaque(false);
        caixaBusca.add(Estilo.campoBusca(tabela));
        topoTabela.add(caixaBusca, BorderLayout.EAST);

        cardTabela.add(topoTabela, BorderLayout.NORTH);
        cardTabela.add(Estilo.rolagem(tabela), BorderLayout.CENTER);
        cardTabela.add(Estilo.textoSuave("Dica: clique em um participante para editar ou excluir."),
                BorderLayout.SOUTH);

        corpo.add(cardFormulario, BorderLayout.WEST);
        corpo.add(cardTabela, BorderLayout.CENTER);
        add(corpo, BorderLayout.CENTER);
    }

    private void cadastrar() {
        try {
            controller.cadastrar(txtCodigo.getText(), txtNome.getText(), txtContato.getText());
            mostrarSucesso("Participante cadastrado com sucesso!");
            limparFormulario();
            atualizarTabela();
        } catch (RegraNegocioException ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private void atualizar() {
        if (txtCodigo.isEditable()) {
            mostrarErro("Selecione um participante na tabela para atualizar.");
            return;
        }
        try {
            controller.atualizar(txtCodigo.getText(), txtNome.getText(), txtContato.getText());
            mostrarSucesso("Participante atualizado com sucesso!");
            limparFormulario();
            atualizarTabela();
        } catch (RegraNegocioException ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private void excluir() {
        if (txtCodigo.isEditable()) {
            mostrarErro("Selecione um participante na tabela para excluir.");
            return;
        }
        if (!confirmar("Deseja realmente excluir o participante \"" + txtNome.getText() + "\"?")) {
            return;
        }
        try {
            controller.excluir(txtCodigo.getText());
            mostrarSucesso("Participante excluído com sucesso!");
            limparFormulario();
            atualizarTabela();
        } catch (RegraNegocioException ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private void atualizarTabela() {
        modeloTabela.setRowCount(0);
        for (Participante p : controller.listar()) {
            modeloTabela.addRow(new Object[]{
                    p.getCodigo(), p.getNome(), p.getContato(), controller.quantidadeDeEventos(p)});
        }
        int total = modeloTabela.getRowCount();
        lblContador.setText(total + (total == 1 ? " participante no sistema" : " participantes no sistema"));
    }

    private void preencherFormularioComSelecao() {
        int linhaVisivel = tabela.getSelectedRow();
        if (linhaVisivel < 0) {
            return;
        }
        int linha = tabela.convertRowIndexToModel(linhaVisivel);
        txtCodigo.setText(modeloTabela.getValueAt(linha, 0).toString());
        txtNome.setText(modeloTabela.getValueAt(linha, 1).toString());
        txtContato.setText(modeloTabela.getValueAt(linha, 2).toString());
        modoEdicao(true);
    }

    private void modoEdicao(boolean editando) {
        txtCodigo.setEditable(!editando);
        btnCadastrar.setEnabled(!editando);
        btnAtualizar.setEnabled(editando);
        btnExcluir.setEnabled(editando);
        if (editando) {
            lblTituloFormulario.setText("Editando participante #" + txtCodigo.getText());
            lblSubtituloFormulario.setText("Altere os dados e clique em Salvar");
        } else {
            lblTituloFormulario.setText("Novo participante");
            lblSubtituloFormulario.setText("Preencha os dados abaixo");
        }
    }

    private void limparFormulario() {
        txtCodigo.setText("");
        txtNome.setText("");
        txtContato.setText("");
        tabela.clearSelection();
        modoEdicao(false);
        txtCodigo.requestFocus();
    }
}
