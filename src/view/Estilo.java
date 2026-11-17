package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * Identidade visual do sistema (só Java Swing, sem bibliotecas externas).
 * Reúne cores, fontes e "fábricas" de componentes estilizados usados por todas as telas.
 */
public final class Estilo {

    // ======================== PALETA DE CORES ========================
    public static final Color PRIMARIA = new Color(79, 70, 229);        // índigo
    public static final Color SECUNDARIA = new Color(124, 58, 237);     // violeta (fim do degradê)
    public static final Color AZUL = new Color(14, 165, 233);
    public static final Color SUCESSO = new Color(16, 185, 129);
    public static final Color ALERTA = new Color(245, 158, 11);
    public static final Color PERIGO = new Color(239, 68, 68);
    public static final Color ROSA = new Color(236, 72, 153);

    public static final Color FUNDO = new Color(241, 244, 250);
    public static final Color CARD = Color.WHITE;
    public static final Color BORDA = new Color(226, 232, 240);
    public static final Color TEXTO = new Color(30, 41, 59);
    public static final Color TEXTO_SUAVE = new Color(100, 116, 139);
    public static final Color LINHA_ALTERNADA = new Color(248, 250, 252);
    public static final Color SELECAO = new Color(224, 231, 255);

    private static final String FONTE = escolherFonte();

    private Estilo() {
    }

    // ============================ FONTES ============================

    private static String escolherFonte() {
        List<String> instaladas = Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        for (String preferida : new String[]{"Segoe UI", "Inter", "Helvetica Neue", "Arial"}) {
            if (instaladas.contains(preferida)) {
                return preferida;
            }
        }
        return Font.SANS_SERIF;
    }

    public static Font fonte(int estilo, int tamanho) {
        return new Font(FONTE, estilo, tamanho);
    }

    /** Ajustes gerais (fontes das mensagens JOptionPane etc.). Chamado uma vez no Main. */
    public static void aplicarPadroesGlobais() {
        UIManager.put("OptionPane.messageFont", fonte(Font.PLAIN, 14));
        UIManager.put("OptionPane.buttonFont", fonte(Font.BOLD, 13));
        UIManager.put("ComboBox.font", fonte(Font.PLAIN, 14));
        UIManager.put("ToolTip.font", fonte(Font.PLAIN, 12));
    }

    /** Mistura duas cores (usado para tons claros dos ícones e botões). */
    public static Color misturar(Color a, Color b, double proporcaoDeB) {
        double p = Math.max(0, Math.min(1, proporcaoDeB));
        return new Color(
                (int) (a.getRed() * (1 - p) + b.getRed() * p),
                (int) (a.getGreen() * (1 - p) + b.getGreen() * p),
                (int) (a.getBlue() * (1 - p) + b.getBlue() * p));
    }

    public static Color clara(Color cor) {
        return misturar(cor, Color.WHITE, 0.88);
    }

    private static Graphics2D suave(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        return g2;
    }

    // ============================ BOTÕES ============================

    /** Botão arredondado com efeito ao passar o mouse. */
    public static class Botao extends JButton {
        private final Color fundo;
        private final Color fundoHover;
        private final Color borda;
        private boolean mouseEmCima;

        public Botao(String texto, Color fundo, Color fundoHover, Color corTexto, Color borda) {
            super(texto);
            this.fundo = fundo;
            this.fundoHover = fundoHover;
            this.borda = borda;
            setForeground(corTexto);
            setFont(fonte(Font.BOLD, 13));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setBorder(new EmptyBorder(10, 18, 10, 18));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    mouseEmCima = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    mouseEmCima = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = suave(g);
            if (!isEnabled()) {
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.4f));
            }
            boolean ativo = mouseEmCima && isEnabled();
            g2.setColor(ativo || getModel().isPressed() ? fundoHover : fundo);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            if (borda != null) {
                g2.setColor(borda);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Botão cheio (ação principal). */
    public static Botao botao(String texto, Color cor) {
        return new Botao(texto, cor, cor.darker(), Color.WHITE, null);
    }

    /** Botão branco com contorno colorido (ação secundária). */
    public static Botao botaoContorno(String texto, Color cor) {
        return new Botao(texto, Color.WHITE, clara(cor), cor, misturar(cor, Color.WHITE, 0.55));
    }

    /** Botão translúcido para ficar em cima do cabeçalho colorido. */
    public static Botao botaoCabecalho(String texto) {
        return new Botao(texto, new Color(255, 255, 255, 38), new Color(255, 255, 255, 70),
                Color.WHITE, new Color(255, 255, 255, 90));
    }

    // ======================= CAMPOS DE TEXTO =======================

    /** Borda arredondada que muda de cor quando o campo recebe o foco. */
    private static class BordaArredondada extends AbstractBorder {
        private Color cor;

        BordaArredondada(Color cor) {
            this.cor = cor;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = suave(g);
            g2.setColor(cor);
            g2.setStroke(new BasicStroke(cor == PRIMARIA ? 2f : 1f));
            g2.drawRoundRect(x + 1, y + 1, w - 3, h - 3, 10, 10);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(8, 12, 8, 12);
        }
    }

    /** Campo de texto com texto de dica (placeholder) quando está vazio. */
    public static class Campo extends JTextField {
        private final String dica;

        public Campo(String dica) {
            this.dica = dica;
            setFont(fonte(Font.PLAIN, 14));
            setForeground(TEXTO);
            setCaretColor(PRIMARIA);
            setBackground(Color.WHITE);
            BordaArredondada borda = new BordaArredondada(BORDA);
            setBorder(borda);
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    borda.cor = PRIMARIA;
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    borda.cor = BORDA;
                    repaint();
                }
            });
        }

        @Override
        public void setEditable(boolean editavel) {
            super.setEditable(editavel);
            setBackground(editavel ? Color.WHITE : LINHA_ALTERNADA);
            setForeground(editavel ? TEXTO : TEXTO_SUAVE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty() && dica != null) {
                Graphics2D g2 = suave(g);
                g2.setColor(new Color(148, 163, 184));
                g2.setFont(getFont().deriveFont(Font.PLAIN));
                Insets i = getInsets();
                int y = (getHeight() - g2.getFontMetrics().getHeight()) / 2 + g2.getFontMetrics().getAscent();
                g2.drawString(dica, i.left, y);
                g2.dispose();
            }
        }
    }

    public static Campo campo(String dica) {
        return new Campo(dica);
    }

    public static <T> JComboBox<T> combo() {
        JComboBox<T> combo = new JComboBox<>();
        combo.setFont(fonte(Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        combo.setForeground(TEXTO);
        combo.setPreferredSize(new Dimension(200, 38));
        combo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return combo;
    }

    /** Campo de busca que filtra as linhas da tabela enquanto o usuário digita. */
    public static Campo campoBusca(JTable tabela) {
        Campo busca = new Campo("Buscar...");
        busca.setPreferredSize(new Dimension(220, 36));
        busca.getDocument().addDocumentListener(new DocumentListener() {
            private void filtrar() {
                @SuppressWarnings("unchecked")
                TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) tabela.getRowSorter();
                String texto = busca.getText().trim();
                sorter.setRowFilter(texto.isEmpty() ? null
                        : RowFilter.regexFilter("(?i)" + Pattern.quote(texto)));
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrar();
            }
        });
        return busca;
    }

    // ============================ TEXTOS ============================

    public static JLabel rotulo(String texto) {
        JLabel l = new JLabel(texto.toUpperCase());
        l.setFont(fonte(Font.BOLD, 11));
        l.setForeground(TEXTO_SUAVE);
        return l;
    }

    public static JLabel titulo(String texto, int tamanho) {
        JLabel l = new JLabel(texto);
        l.setFont(fonte(Font.BOLD, tamanho));
        l.setForeground(TEXTO);
        return l;
    }

    public static JLabel textoSuave(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(fonte(Font.PLAIN, 13));
        l.setForeground(TEXTO_SUAVE);
        return l;
    }

    // ========================= PAINÉIS / CARDS =========================

    /** Painel branco com cantos arredondados e uma sombra leve. */
    public static class Card extends JPanel {
        protected Color corBorda = BORDA;

        public Card() {
            this(new BorderLayout(0, 12));
        }

        public Card(java.awt.LayoutManager layout) {
            super(layout);
            setOpaque(false);
            setBorder(new EmptyBorder(20, 22, 20, 22));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = suave(g);
            int w = getWidth();
            int h = getHeight();
            for (int i = 0; i < 4; i++) { // sombra
                g2.setColor(new Color(15, 23, 42, 6 + i * 2));
                g2.fillRoundRect(i / 2, i + 1, w - i, h - i - 1, 18, 18);
            }
            g2.setColor(CARD);
            g2.fillRoundRect(0, 0, w - 2, h - 4, 16, 16);
            g2.setColor(corBorda);
            g2.drawRoundRect(0, 0, w - 2, h - 4, 16, 16);
            g2.dispose();
        }
    }

    /** Painel com degradê índigo → violeta (cabeçalhos). */
    public static class PainelGradiente extends JPanel {
        public PainelGradiente(java.awt.LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = suave(g);
            g2.setPaint(new GradientPaint(0, 0, PRIMARIA, getWidth(), getHeight(), SECUNDARIA));
            g2.fillRect(0, 0, getWidth(), getHeight());
            // círculos decorativos
            g2.setColor(new Color(255, 255, 255, 18));
            g2.fillOval(getWidth() - 260, -120, 300, 300);
            g2.fillOval(getWidth() - 480, getHeight() - 40, 180, 180);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Cabeçalho das telas: ícone + título + subtítulo à esquerda e botões à direita. */
    public static JPanel cabecalho(String titulo, String subtitulo, Icone.Tipo icone, JComponent... direita) {
        PainelGradiente painel = new PainelGradiente(new BorderLayout(16, 0));
        painel.setBorder(new EmptyBorder(20, 28, 20, 28));

        JLabel lblIcone = new JLabel(new Icone(icone, 26, Color.WHITE)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = suave(g);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblIcone.setPreferredSize(new Dimension(52, 52));
        lblIcone.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(fonte(Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(fonte(Font.PLAIN, 13));
        lblSub.setForeground(new Color(255, 255, 255, 210));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(Box.createVerticalGlue());
        textos.add(lblTitulo);
        textos.add(Box.createVerticalStrut(2));
        textos.add(lblSub);
        textos.add(Box.createVerticalGlue());

        JPanel esquerda = new JPanel(new BorderLayout(14, 0));
        esquerda.setOpaque(false);
        esquerda.add(lblIcone, BorderLayout.WEST);
        esquerda.add(textos, BorderLayout.CENTER);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoes.setOpaque(false);
        for (JComponent c : direita) {
            botoes.add(c);
        }
        JPanel botoesCentralizados = new JPanel(new java.awt.GridBagLayout());
        botoesCentralizados.setOpaque(false);
        botoesCentralizados.add(botoes);

        painel.add(esquerda, BorderLayout.CENTER);
        painel.add(botoesCentralizados, BorderLayout.EAST);
        return painel;
    }

    /** Monta um formulário vertical: rótulo em cima, campo embaixo. Recebe pares (texto, componente). */
    public static JPanel formulario(Object... rotulosECampos) {
        JPanel painel = new JPanel();
        painel.setOpaque(false);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        for (int i = 0; i < rotulosECampos.length; i += 2) {
            JLabel rotulo = rotulo((String) rotulosECampos[i]);
            JComponent campo = (JComponent) rotulosECampos[i + 1];
            rotulo.setAlignmentX(Component.LEFT_ALIGNMENT);
            campo.setAlignmentX(Component.LEFT_ALIGNMENT);
            campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            campo.setPreferredSize(new Dimension(200, 40));
            painel.add(rotulo);
            painel.add(Box.createVerticalStrut(6));
            painel.add(campo);
            painel.add(Box.createVerticalStrut(14));
        }
        return painel;
    }

    /** Card de número grande (estatística). */
    public static Card cardEstatistica(String rotulo, JLabel valor, Color cor, Icone.Tipo icone) {
        Card card = new Card(new BorderLayout(14, 0));
        card.setBorder(new EmptyBorder(16, 18, 18, 18));

        JLabel lblIcone = new JLabel(new Icone(icone, 22, cor)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = suave(g);
                g2.setColor(clara(cor));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblIcone.setPreferredSize(new Dimension(46, 46));
        lblIcone.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel caixaIcone = new JPanel(new java.awt.GridBagLayout());
        caixaIcone.setOpaque(false);
        caixaIcone.add(lblIcone);

        valor.setFont(fonte(Font.BOLD, 26));
        valor.setForeground(TEXTO);
        JPanel textos = new JPanel(new java.awt.GridLayout(2, 1));
        textos.setOpaque(false);
        textos.add(valor);
        textos.add(textoSuave(rotulo));

        card.add(caixaIcone, BorderLayout.WEST);
        card.add(textos, BorderLayout.CENTER);
        return card;
    }

    // ============================ TABELAS ============================

    /** Aplica o visual moderno: linhas altas, zebrado, cabeçalho limpo e ordenação por coluna. */
    public static void estilizarTabela(JTable tabela) {
        tabela.setFont(fonte(Font.PLAIN, 13));
        tabela.setRowHeight(38);
        tabela.setShowVerticalLines(false);
        tabela.setShowHorizontalLines(true);
        tabela.setGridColor(new Color(241, 245, 249));
        tabela.setIntercellSpacing(new Dimension(0, 1));
        tabela.setSelectionBackground(SELECAO);
        tabela.setSelectionForeground(TEXTO);
        tabela.setFillsViewportHeight(true);
        tabela.setBackground(Color.WHITE);
        tabela.setForeground(TEXTO);
        tabela.setAutoCreateRowSorter(true);

        CelulaRenderer celula = new CelulaRenderer();
        tabela.setDefaultRenderer(Object.class, celula);
        tabela.setDefaultRenderer(Number.class, celula);
        tabela.setDefaultRenderer(Integer.class, celula);

        JTableHeader header = tabela.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 40));
        header.setDefaultRenderer(new CabecalhoRenderer());
        header.setBackground(LINHA_ALTERNADA);
        header.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static JScrollPane rolagem(JTable tabela) {
        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.setBorder(BorderFactory.createLineBorder(BORDA));
        rolagem.getViewport().setBackground(Color.WHITE);
        rolagem.getVerticalScrollBar().setUnitIncrement(16);
        return rolagem;
    }

    private static class CelulaRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object valor, boolean selecionada,
                                                       boolean foco, int linha, int coluna) {
            super.getTableCellRendererComponent(t, valor, selecionada, false, linha, coluna);
            setBorder(new EmptyBorder(0, 10, 0, 10));
            setHorizontalAlignment(LEFT);
            setFont(coluna == 1 ? fonte(Font.BOLD, 13) : fonte(Font.PLAIN, 13));
            if (!selecionada) {
                setBackground(linha % 2 == 0 ? Color.WHITE : LINHA_ALTERNADA);
                setForeground(coluna == 0 ? TEXTO_SUAVE : TEXTO);
            }
            return this;
        }
    }

    private static class CabecalhoRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object valor, boolean selecionada,
                                                       boolean foco, int linha, int coluna) {
            super.getTableCellRendererComponent(t, valor, false, false, linha, coluna);
            setText(valor == null ? "" : valor.toString().toUpperCase());
            setFont(fonte(Font.BOLD, 11));
            setForeground(TEXTO_SUAVE);
            setBackground(LINHA_ALTERNADA);
            setOpaque(true);
            setHorizontalAlignment(LEFT);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, BORDA),
                    new EmptyBorder(0, 10, 0, 10)));
            return this;
        }
    }

    /** Cor da ocupação: verde até 70%, amarelo até 99%, vermelho quando lota. */
    public static Color corOcupacao(int percentual) {
        if (percentual >= 100) {
            return PERIGO;
        }
        if (percentual >= 70) {
            return ALERTA;
        }
        return SUCESSO;
    }

    /** Barra de progresso arredondada mostrando a ocupação do evento. */
    public static class BarraOcupacao extends JComponent {
        protected int percentual;
        protected boolean mostrarTexto = true;

        public void setPercentual(int percentual) {
            this.percentual = Math.max(0, Math.min(100, percentual));
            repaint();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(160, 22);
        }

        protected void desenharBarra(Graphics2D g2, int x, int y, int largura) {
            int altura = 8;
            g2.setColor(new Color(226, 232, 240));
            g2.fillRoundRect(x, y, largura, altura, altura, altura);
            int preenchido = (int) Math.round(largura * percentual / 100.0);
            if (preenchido > 0) {
                g2.setColor(corOcupacao(percentual));
                g2.fillRoundRect(x, y, Math.max(preenchido, altura), altura, altura, altura);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = suave(g);
            int espacoTexto = mostrarTexto ? 46 : 0;
            desenharBarra(g2, 0, (getHeight() - 8) / 2, getWidth() - espacoTexto);
            if (mostrarTexto) {
                g2.setFont(fonte(Font.BOLD, 12));
                g2.setColor(TEXTO);
                String txt = percentual + "%";
                int y = (getHeight() + g2.getFontMetrics().getAscent()) / 2 - 2;
                g2.drawString(txt, getWidth() - g2.getFontMetrics().stringWidth(txt), y);
            }
            g2.dispose();
        }
    }

    /** Renderer de tabela que desenha a barra de ocupação (valor = percentual inteiro). */
    public static class OcupacaoRenderer extends BarraOcupacao implements TableCellRenderer {
        private Color fundo;

        @Override
        public Component getTableCellRendererComponent(JTable t, Object valor, boolean selecionada,
                                                       boolean foco, int linha, int coluna) {
            setPercentual(valor instanceof Integer ? (Integer) valor : 0);
            fundo = selecionada ? SELECAO : (linha % 2 == 0 ? Color.WHITE : LINHA_ALTERNADA);
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = suave(g);
            g2.setColor(fundo);
            g2.fillRect(0, 0, getWidth(), getHeight());
            int largura = getWidth() - 28 - 46;
            desenharBarra(g2, 14, (getHeight() - 8) / 2, Math.max(20, largura));
            g2.setFont(fonte(Font.BOLD, 12));
            g2.setColor(TEXTO);
            String txt = percentual + "%";
            int y = (getHeight() + g2.getFontMetrics().getAscent()) / 2 - 2;
            g2.drawString(txt, getWidth() - 14 - g2.getFontMetrics().stringWidth(txt), y);
            g2.dispose();
        }
    }

    /** Renderer que mostra as vagas como uma "etiqueta" colorida (Lotado / N vagas). */
    public static class VagasRenderer extends JComponent implements TableCellRenderer {
        private int vagas;
        private Color fundo;

        @Override
        public Component getTableCellRendererComponent(JTable t, Object valor, boolean selecionada,
                                                       boolean foco, int linha, int coluna) {
            vagas = valor instanceof Integer ? (Integer) valor : 0;
            fundo = selecionada ? SELECAO : (linha % 2 == 0 ? Color.WHITE : LINHA_ALTERNADA);
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = suave(g);
            g2.setColor(fundo);
            g2.fillRect(0, 0, getWidth(), getHeight());
            Color cor = vagas == 0 ? PERIGO : (vagas <= 3 ? ALERTA : SUCESSO);
            String texto = vagas == 0 ? "Lotado" : vagas + (vagas == 1 ? " vaga" : " vagas");
            g2.setFont(fonte(Font.BOLD, 12));
            int larguraTexto = g2.getFontMetrics().stringWidth(texto);
            int w = larguraTexto + 22;
            int h = 24;
            int x = 14;
            int y = (getHeight() - h) / 2;
            g2.setColor(clara(cor));
            g2.fillRoundRect(x, y, w, h, h, h);
            g2.setColor(cor.darker());
            g2.drawString(texto, x + 11, y + (h + g2.getFontMetrics().getAscent()) / 2 - 2);
            g2.dispose();
        }
    }

    /** Ícone de dentro de um círculo colorido (cards do menu e linhas de destaque). */
    public static Icon iconeEmCaixa(Icone.Tipo tipo, int tamanho, Color cor) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = suave(g);
                g2.setColor(clara(cor));
                g2.fillRoundRect(x, y, tamanho, tamanho, 16, 16);
                g2.dispose();
                int interno = tamanho / 2;
                new Icone(tipo, interno, cor).paintIcon(c, g, x + (tamanho - interno) / 2, y + (tamanho - interno) / 2);
            }

            @Override
            public int getIconWidth() {
                return tamanho;
            }

            @Override
            public int getIconHeight() {
                return tamanho;
            }
        };
    }
}
