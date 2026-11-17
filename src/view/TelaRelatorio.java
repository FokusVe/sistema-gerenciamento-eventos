package view;

import controller.RelatorioController;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import model.Evento;
import model.Participante;

/**
 * Relatório: para cada evento mostra inscrições e vagas disponíveis.
 * Ao selecionar um evento, a tabela de baixo lista os participantes inscritos.
 */
public class TelaRelatorio extends TelaSecundaria {

    private final RelatorioController controller;

    private JTable tabelaEventos;
    private DefaultTableModel modeloEventos;
    private DefaultTableModel modeloInscritos;
    private JLabel lblTituloInscritos;
    private JLabel lblSubtituloInscritos;

    private final JLabel lblTotalEventos = new JLabel("0");
    private final JLabel lblTotalInscricoes = new JLabel("0");
    private final JLabel lblTotalVagas = new JLabel("0");
    private final JLabel lblOcupacao = new JLabel("0%");

    public TelaRelatorio(TelaPrincipal telaPrincipal, RelatorioController controller) {
        super("Relatório de Eventos", telaPrincipal);
        this.controller = controller;

        Estilo.Botao btnAtualizar = Estilo.botaoCabecalho("↻  Atualizar");
        btnAtualizar.addActionListener(e -> atualizarRelatorio());
        montarCabecalho("Relatório de Eventos", "Inscritos, quantidade de inscrições e vagas por evento",
                Icone.Tipo.RELATORIO, btnAtualizar);

        montarTela();
        atualizarRelatorio();
        ajustarTamanho(1300, 800);
    }

    private void montarTela() {
        JPanel corpo = criarCorpo();

        // ================= números gerais =================
        JPanel estatisticas = new JPanel(new GridLayout(1, 4, 16, 0));
        estatisticas.setOpaque(false);
        estatisticas.add(Estilo.cardEstatistica("Eventos", lblTotalEventos, Estilo.PRIMARIA, Icone.Tipo.EVENTO));
        estatisticas.add(Estilo.cardEstatistica("Inscrições", lblTotalInscricoes, Estilo.SUCESSO, Icone.Tipo.INSCRICAO));
        estatisticas.add(Estilo.cardEstatistica("Vagas disponíveis", lblTotalVagas, Estilo.ALERTA, Icone.Tipo.VAGAS));
        estatisticas.add(Estilo.cardEstatistica("Ocupação geral", lblOcupacao, Estilo.ROSA, Icone.Tipo.RELATORIO));

        // ================= tabela de eventos =================
        Estilo.Card cardEventos = new Estilo.Card();
        modeloEventos = criarModeloTabela(new String[]{
                "Cód.", "Evento", "Data", "Local", "Capac.", "Inscrições", "Vagas", "Ocupação"});
        tabelaEventos = new JTable(modeloEventos);
        Estilo.estilizarTabela(tabelaEventos);
        tabelaEventos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaEventos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarInscritosDoEventoSelecionado();
            }
        });
        definirLarguras(tabelaEventos, 50, 210, 100, 130, 70, 90, 100, 160);
        tabelaEventos.getColumnModel().getColumn(6).setCellRenderer(new Estilo.VagasRenderer());
        tabelaEventos.getColumnModel().getColumn(7).setCellRenderer(new Estilo.OcupacaoRenderer());

        JPanel topoEventos = new JPanel(new BorderLayout());
        topoEventos.setOpaque(false);
        JPanel titulosEventos = new JPanel(new GridLayout(2, 1, 0, 2));
        titulosEventos.setOpaque(false);
        titulosEventos.add(Estilo.titulo("Resumo por evento", 17));
        titulosEventos.add(Estilo.textoSuave("Clique em um evento para ver os participantes inscritos"));
        topoEventos.add(titulosEventos, BorderLayout.WEST);
        JPanel caixaBusca = new JPanel(new java.awt.GridBagLayout());
        caixaBusca.setOpaque(false);
        caixaBusca.add(Estilo.campoBusca(tabelaEventos));
        topoEventos.add(caixaBusca, BorderLayout.EAST);
        cardEventos.add(topoEventos, BorderLayout.NORTH);
        cardEventos.add(Estilo.rolagem(tabelaEventos), BorderLayout.CENTER);

        // ================= tabela de inscritos =================
        Estilo.Card cardInscritos = new Estilo.Card();
        modeloInscritos = criarModeloTabela(new String[]{"Cód.", "Participante", "Contato"});
        JTable tabelaInscritos = new JTable(modeloInscritos);
        Estilo.estilizarTabela(tabelaInscritos);
        definirLarguras(tabelaInscritos, 50, 250, 250);

        JPanel titulosInscritos = new JPanel(new GridLayout(2, 1, 0, 2));
        titulosInscritos.setOpaque(false);
        lblTituloInscritos = Estilo.titulo("Participantes inscritos", 17);
        lblSubtituloInscritos = Estilo.textoSuave("Nenhum evento selecionado");
        titulosInscritos.add(lblTituloInscritos);
        titulosInscritos.add(lblSubtituloInscritos);
        cardInscritos.add(titulosInscritos, BorderLayout.NORTH);
        cardInscritos.add(Estilo.rolagem(tabelaInscritos), BorderLayout.CENTER);

        JPanel tabelas = new JPanel(new GridLayout(2, 1, 0, 18));
        tabelas.setOpaque(false);
        tabelas.add(cardEventos);
        tabelas.add(cardInscritos);

        corpo.add(estatisticas, BorderLayout.NORTH);
        corpo.add(tabelas, BorderLayout.CENTER);
        add(corpo, BorderLayout.CENTER);
    }

    private void atualizarRelatorio() {
        modeloEventos.setRowCount(0);
        for (Evento ev : controller.listarEventos()) {
            modeloEventos.addRow(new Object[]{
                    ev.getCodigo(), ev.getNome(), ev.getData(), ev.getLocal(),
                    ev.getCapacidade(), ev.getQuantidadeInscritos(), ev.getVagasDisponiveis(),
                    RelatorioController.percentualOcupacao(ev)});
        }
        lblTotalEventos.setText(String.valueOf(controller.totalEventos()));
        lblTotalInscricoes.setText(String.valueOf(controller.totalInscricoes()));
        lblTotalVagas.setText(String.valueOf(controller.totalVagasDisponiveis()));
        lblOcupacao.setText(controller.ocupacaoGeral() + "%");

        modeloInscritos.setRowCount(0);
        lblTituloInscritos.setText("Participantes inscritos");
        lblSubtituloInscritos.setText("Nenhum evento selecionado");
    }

    private void mostrarInscritosDoEventoSelecionado() {
        modeloInscritos.setRowCount(0);
        int linhaVisivel = tabelaEventos.getSelectedRow();
        if (linhaVisivel < 0) {
            return;
        }
        int linha = tabelaEventos.convertRowIndexToModel(linhaVisivel);
        int codigo = (Integer) modeloEventos.getValueAt(linha, 0);
        String nome = modeloEventos.getValueAt(linha, 1).toString();
        int inscricoes = (Integer) modeloEventos.getValueAt(linha, 5);
        int vagas = (Integer) modeloEventos.getValueAt(linha, 6);

        for (Participante p : controller.inscritosDoEvento(codigo)) {
            modeloInscritos.addRow(new Object[]{p.getCodigo(), p.getNome(), p.getContato()});
        }
        lblTituloInscritos.setText("Inscritos em: " + nome);
        lblSubtituloInscritos.setText(inscricoes + (inscricoes == 1 ? " inscrição" : " inscrições")
                + "  ·  " + (vagas == 0 ? "evento lotado" : vagas + (vagas == 1 ? " vaga disponível" : " vagas disponíveis")));
    }
}
