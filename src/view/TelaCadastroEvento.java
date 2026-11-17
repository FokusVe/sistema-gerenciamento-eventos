package view;

import controller.EventoController;
import controller.RelatorioController;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import model.Evento;
import model.RegraNegocioException;

/**
 * Tela de cadastro e listagem de eventos (JTable).
 * Clique em uma linha da tabela para carregar o evento no formulário e atualizar/excluir.
 */
public class TelaCadastroEvento extends TelaSecundaria {

    private final EventoController controller;

    private Estilo.Campo txtCodigo;
    private Estilo.Campo txtNome;
    private Estilo.Campo txtData;
    private Estilo.Campo txtLocal;
    private Estilo.Campo txtCapacidade;
    private JLabel lblTituloFormulario;
    private JLabel lblSubtituloFormulario;
    private JLabel lblContador;
    private Estilo.Botao btnCadastrar;
    private Estilo.Botao btnAtualizar;
    private Estilo.Botao btnExcluir;
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    public TelaCadastroEvento(TelaPrincipal telaPrincipal, EventoController controller) {
        super("Cadastro de Eventos", telaPrincipal);
        this.controller = controller;
        montarCabecalho("Cadastro de Eventos", "Cadastre, edite e acompanhe as vagas de cada evento",
                Icone.Tipo.EVENTO);
        montarTela();
        atualizarTabela();
        modoEdicao(false);
        ajustarTamanho(1300, 720);
    }

    private void montarTela() {
        JPanel corpo = criarCorpo();

        // ================= card do formulário =================
        Estilo.Card cardFormulario = new Estilo.Card();
        cardFormulario.setPreferredSize(new Dimension(340, 0));

        lblTituloFormulario = Estilo.titulo("Novo evento", 18);
        lblSubtituloFormulario = Estilo.textoSuave("Preencha os dados abaixo");
        JPanel tituloForm = new JPanel(new GridLayout(2, 1, 0, 2));
        tituloForm.setOpaque(false);
        tituloForm.add(lblTituloFormulario);
        tituloForm.add(lblSubtituloFormulario);

        txtCodigo = Estilo.campo("Ex.: 10");
        txtNome = Estilo.campo("Ex.: Semana de Tecnologia");
        txtData = Estilo.campo("dd/mm/aaaa");
        txtLocal = Estilo.campo("Ex.: Auditório A");
        txtCapacidade = Estilo.campo("Ex.: 50");

        JPanel campos = Estilo.formulario(
                "Código", txtCodigo,
                "Nome do evento", txtNome,
                "Data", txtData,
                "Local", txtLocal,
                "Capacidade máxima", txtCapacidade);

        btnCadastrar = Estilo.botao("+  Cadastrar evento", Estilo.PRIMARIA);
        btnAtualizar = Estilo.botaoContorno("Salvar", Estilo.PRIMARIA);
        btnExcluir = Estilo.botaoContorno("Excluir", Estilo.PERIGO);
        Estilo.Botao btnLimpar = new Estilo.Botao("Limpar formulário", java.awt.Color.WHITE,
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

        modeloTabela = criarModeloTabela(new String[]{
                "Cód.", "Evento", "Data", "Local", "Capac.", "Inscritos", "Vagas", "Ocupação"});
        tabela = new JTable(modeloTabela);
        Estilo.estilizarTabela(tabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherFormularioComSelecao();
            }
        });
        configurarColunas();

        JPanel topoTabela = new JPanel(new BorderLayout());
        topoTabela.setOpaque(false);
        JPanel titulos = new JPanel(new GridLayout(2, 1, 0, 2));
        titulos.setOpaque(false);
        titulos.add(Estilo.titulo("Eventos cadastrados", 18));
        lblContador = Estilo.textoSuave("");
        titulos.add(lblContador);
        topoTabela.add(titulos, BorderLayout.WEST);
        JPanel caixaBusca = new JPanel(new java.awt.GridBagLayout());
        caixaBusca.setOpaque(false);
        caixaBusca.add(Estilo.campoBusca(tabela));
        topoTabela.add(caixaBusca, BorderLayout.EAST);

        cardTabela.add(topoTabela, BorderLayout.NORTH);
        cardTabela.add(Estilo.rolagem(tabela), BorderLayout.CENTER);
        cardTabela.add(Estilo.textoSuave("Dica: clique em um evento para editar ou excluir. "
                + "Clique no título de uma coluna para ordenar."), BorderLayout.SOUTH);

        corpo.add(cardFormulario, BorderLayout.WEST);
        corpo.add(cardTabela, BorderLayout.CENTER);
        add(corpo, BorderLayout.CENTER);
    }

    private void configurarColunas() {
        definirLarguras(tabela, 50, 210, 100, 130, 70, 85, 100, 150);
        tabela.getColumnModel().getColumn(6).setCellRenderer(new Estilo.VagasRenderer());
        tabela.getColumnModel().getColumn(7).setCellRenderer(new Estilo.OcupacaoRenderer());
    }

    // ===================== ações dos botões =====================

    private void cadastrar() {
        try {
            controller.cadastrar(txtCodigo.getText(), txtNome.getText(), txtData.getText(),
                    txtLocal.getText(), txtCapacidade.getText());
            mostrarSucesso("Evento cadastrado com sucesso!");
            limparFormulario();
            atualizarTabela();
        } catch (RegraNegocioException ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private void atualizar() {
        if (txtCodigo.isEditable()) {
            mostrarErro("Selecione um evento na tabela para atualizar.");
            return;
        }
        try {
            controller.atualizar(txtCodigo.getText(), txtNome.getText(), txtData.getText(),
                    txtLocal.getText(), txtCapacidade.getText());
            mostrarSucesso("Evento atualizado com sucesso!");
            limparFormulario();
            atualizarTabela();
        } catch (RegraNegocioException ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private void excluir() {
        if (txtCodigo.isEditable()) {
            mostrarErro("Selecione um evento na tabela para excluir.");
            return;
        }
        try {
            Evento evento = controller.buscar(txtCodigo.getText());
            String aviso = "Deseja realmente excluir o evento \"" + evento.getNome() + "\"?";
            if (evento.getQuantidadeInscritos() > 0) {
                aviso += "\nAtenção: ele possui " + evento.getQuantidadeInscritos()
                        + " inscrição(ões), que também serão removidas.";
            }
            if (confirmar(aviso)) {
                controller.excluir(txtCodigo.getText());
                mostrarSucesso("Evento excluído com sucesso!");
                limparFormulario();
                atualizarTabela();
            }
        } catch (RegraNegocioException ex) {
            mostrarErro(ex.getMessage());
        }
    }

    // ===================== atualização da tela =====================

    private void atualizarTabela() {
        modeloTabela.setRowCount(0);
        for (Evento ev : controller.listar()) {
            modeloTabela.addRow(new Object[]{
                    ev.getCodigo(), ev.getNome(), ev.getData(), ev.getLocal(),
                    ev.getCapacidade(), ev.getQuantidadeInscritos(), ev.getVagasDisponiveis(),
                    RelatorioController.percentualOcupacao(ev)});
        }
        int total = modeloTabela.getRowCount();
        lblContador.setText(total + (total == 1 ? " evento no sistema" : " eventos no sistema"));
    }

    private void preencherFormularioComSelecao() {
        int linhaVisivel = tabela.getSelectedRow();
        if (linhaVisivel < 0) {
            return;
        }
        int linha = tabela.convertRowIndexToModel(linhaVisivel); // necessário por causa da ordenação/busca
        txtCodigo.setText(modeloTabela.getValueAt(linha, 0).toString());
        txtNome.setText(modeloTabela.getValueAt(linha, 1).toString());
        txtData.setText(modeloTabela.getValueAt(linha, 2).toString());
        txtLocal.setText(modeloTabela.getValueAt(linha, 3).toString());
        txtCapacidade.setText(modeloTabela.getValueAt(linha, 4).toString());
        modoEdicao(true);
    }

    /** Alterna o formulário entre "novo evento" e "editando evento". */
    private void modoEdicao(boolean editando) {
        txtCodigo.setEditable(!editando); // o código identifica o evento e não pode mudar
        btnCadastrar.setEnabled(!editando);
        btnAtualizar.setEnabled(editando);
        btnExcluir.setEnabled(editando);
        if (editando) {
            lblTituloFormulario.setText("Editando evento #" + txtCodigo.getText());
            lblSubtituloFormulario.setText("Altere os dados e clique em Salvar");
        } else {
            lblTituloFormulario.setText("Novo evento");
            lblSubtituloFormulario.setText("Preencha os dados abaixo");
        }
    }

    private void limparFormulario() {
        txtCodigo.setText("");
        txtNome.setText("");
        txtData.setText("");
        txtLocal.setText("");
        txtCapacidade.setText("");
        tabela.clearSelection();
        modoEdicao(false);
        txtCodigo.requestFocus();
    }
}
