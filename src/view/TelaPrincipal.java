package view;

import controller.EventoController;
import controller.InscricaoController;
import controller.ParticipanteController;
import controller.RelatorioController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Tela principal: nome do sistema, números gerais e os cards de navegação.
 */
public class TelaPrincipal extends JFrame {

    private final EventoController eventoController;
    private final ParticipanteController participanteController;
    private final InscricaoController inscricaoController;
    private final RelatorioController relatorioController;

    private final JLabel lblEventos = new JLabel("0");
    private final JLabel lblParticipantes = new JLabel("0");
    private final JLabel lblInscricoes = new JLabel("0");
    private final JLabel lblVagas = new JLabel("0");

    public TelaPrincipal(EventoController eventoController,
                         ParticipanteController participanteController,
                         InscricaoController inscricaoController,
                         RelatorioController relatorioController) {
        super("Gerenciador de Eventos — Grupo 09");
        this.eventoController = eventoController;
        this.participanteController = participanteController;
        this.inscricaoController = inscricaoController;
        this.relatorioController = relatorioController;

        montarTela();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        java.awt.Rectangle tela = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        setSize(Math.min(1040, tela.width), Math.min(700, tela.height));
        setMinimumSize(new Dimension(860, 600));
        setLocationRelativeTo(null);

        // Sempre que a tela principal reaparece, os números são recalculados.
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                atualizarEstatisticas();
            }
        });
        atualizarEstatisticas();
    }

    private void montarTela() {
        getContentPane().setBackground(Estilo.FUNDO);
        setLayout(new BorderLayout());

        // ---------------- cabeçalho ----------------
        Estilo.Botao btnSair = Estilo.botaoCabecalho("Sair do sistema");
        btnSair.setIcon(new Icone(Icone.Tipo.SAIR, 16, Color.WHITE));
        btnSair.setIconTextGap(8);
        btnSair.addActionListener(e -> System.exit(0));
        JPanel cabecalho = Estilo.cabecalho("Gerenciador de Eventos",
                "Organize eventos acadêmicos, participantes e inscrições em um só lugar",
                Icone.Tipo.EVENTO, btnSair);
        cabecalho.setBorder(new EmptyBorder(30, 32, 30, 32));
        add(cabecalho, BorderLayout.NORTH);

        // ---------------- corpo ----------------
        JPanel corpo = new JPanel();
        corpo.setBackground(Estilo.FUNDO);
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        corpo.setBorder(new EmptyBorder(24, 32, 20, 32));

        JPanel estatisticas = new JPanel(new GridLayout(1, 4, 16, 0));
        estatisticas.setOpaque(false);
        estatisticas.add(Estilo.cardEstatistica("Eventos cadastrados", lblEventos, Estilo.PRIMARIA, Icone.Tipo.EVENTO));
        estatisticas.add(Estilo.cardEstatistica("Participantes", lblParticipantes, Estilo.AZUL, Icone.Tipo.PARTICIPANTE));
        estatisticas.add(Estilo.cardEstatistica("Inscrições ativas", lblInscricoes, Estilo.SUCESSO, Icone.Tipo.INSCRICAO));
        estatisticas.add(Estilo.cardEstatistica("Vagas disponíveis", lblVagas, Estilo.ALERTA, Icone.Tipo.VAGAS));
        estatisticas.setAlignmentX(LEFT_ALIGNMENT);
        estatisticas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JLabel tituloMenu = Estilo.titulo("O que você quer fazer?", 18);
        tituloMenu.setAlignmentX(LEFT_ALIGNMENT);

        JPanel menu = new JPanel(new GridLayout(2, 2, 16, 16));
        menu.setOpaque(false);
        menu.setAlignmentX(LEFT_ALIGNMENT);
        menu.add(cardMenu(Icone.Tipo.EVENTO, Estilo.PRIMARIA, "Cadastro de Eventos",
                "Cadastre, edite e liste eventos com data, local e capacidade.",
                () -> abrir(new TelaCadastroEvento(this, eventoController))));
        menu.add(cardMenu(Icone.Tipo.PARTICIPANTE, Estilo.AZUL, "Cadastro de Participantes",
                "Registre participantes com código, nome e contato.",
                () -> abrir(new TelaCadastroParticipante(this, participanteController))));
        menu.add(cardMenu(Icone.Tipo.INSCRICAO, Estilo.SUCESSO, "Inscrições",
                "Inscreva participantes nos eventos e cancele inscrições.",
                () -> abrir(new TelaInscricoes(this, inscricaoController))));
        menu.add(cardMenu(Icone.Tipo.RELATORIO, Estilo.ROSA, "Relatório de Eventos",
                "Veja inscritos, quantidade de inscrições e vagas por evento.",
                () -> abrir(new TelaRelatorio(this, relatorioController))));

        corpo.add(estatisticas);
        corpo.add(Box.createVerticalStrut(26));
        corpo.add(tituloMenu);
        corpo.add(Box.createVerticalStrut(12));
        corpo.add(menu);

        // ---------------- rodapé ----------------
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.CENTER));
        rodape.setBackground(Estilo.FUNDO);
        rodape.setBorder(new EmptyBorder(0, 0, 12, 0));
        rodape.add(Estilo.textoSuave("Grupo 09 · Java Swing · Arquitetura MVC · Dados em memória"));

        add(corpo, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
    }

    /** Card grande e clicável do menu, com efeito ao passar o mouse. */
    private JPanel cardMenu(Icone.Tipo icone, Color cor, String titulo, String descricao, Runnable acao) {
        Estilo.Card card = new Estilo.Card(new BorderLayout(18, 0));
        card.setBorder(new EmptyBorder(22, 24, 24, 24));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lblIcone = new JLabel(Estilo.iconeEmCaixa(icone, 58, cor));

        JLabel lblTitulo = Estilo.titulo(titulo, 17);
        JLabel lblDescricao = new JLabel("<html>" + descricao + "</html>");
        lblDescricao.setFont(Estilo.fonte(Font.PLAIN, 13));
        lblDescricao.setForeground(Estilo.TEXTO_SUAVE);
        JLabel lblAbrir = new JLabel("Abrir  →");
        lblAbrir.setFont(Estilo.fonte(Font.BOLD, 13));
        lblAbrir.setForeground(cor);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(lblTitulo);
        textos.add(Box.createVerticalStrut(6));
        textos.add(lblDescricao);
        textos.add(Box.createVerticalStrut(10));
        textos.add(lblAbrir);

        card.add(lblIcone, BorderLayout.WEST);
        card.add(textos, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.corBorda = cor;
                card.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.corBorda = Estilo.BORDA;
                card.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                card.corBorda = Estilo.BORDA;
                acao.run();
            }
        });
        return card;
    }

    private void atualizarEstatisticas() {
        lblEventos.setText(String.valueOf(relatorioController.totalEventos()));
        lblParticipantes.setText(String.valueOf(relatorioController.totalParticipantes()));
        lblInscricoes.setText(String.valueOf(relatorioController.totalInscricoes()));
        lblVagas.setText(String.valueOf(relatorioController.totalVagasDisponiveis()));
    }

    /** Esconde a tela principal e mostra a tela escolhida. */
    private void abrir(JFrame tela) {
        setVisible(false);
        tela.setVisible(true);
    }
}
