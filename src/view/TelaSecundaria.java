package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Base para todas as telas abertas a partir da Tela Principal.
 * Ao fechar (botão Voltar ou X da janela), a Tela Principal volta a aparecer.
 * Também reúne o cabeçalho padrão e os métodos de mensagem (JOptionPane).
 */
public abstract class TelaSecundaria extends JFrame {

    protected final TelaPrincipal telaPrincipal;

    public TelaSecundaria(String titulo, TelaPrincipal telaPrincipal) {
        super(titulo + " — Gerenciador de Eventos");
        this.telaPrincipal = telaPrincipal;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Estilo.FUNDO);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(900, 560));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                telaPrincipal.setVisible(true);
            }
        });
    }

    /** Coloca o cabeçalho colorido no topo, sempre com o botão Voltar no fim. */
    protected void montarCabecalho(String titulo, String subtitulo, Icone.Tipo icone, JComponent... botoesExtras) {
        Estilo.Botao btnVoltar = Estilo.botaoCabecalho("←  Voltar ao início");
        btnVoltar.addActionListener(e -> voltar());
        JComponent[] botoes = new JComponent[botoesExtras.length + 1];
        System.arraycopy(botoesExtras, 0, botoes, 0, botoesExtras.length);
        botoes[botoes.length - 1] = btnVoltar;
        add(Estilo.cabecalho(titulo, subtitulo, icone, botoes), BorderLayout.NORTH);
    }

    /** Painel de conteúdo com o fundo cinza-claro e margem. */
    protected JPanel criarCorpo() {
        JPanel corpo = new JPanel(new BorderLayout(18, 18));
        corpo.setBackground(Estilo.FUNDO);
        corpo.setBorder(new EmptyBorder(22, 24, 24, 24));
        return corpo;
    }

    /** Define o tamanho desejado, sem passar do tamanho da tela do computador. */
    protected void ajustarTamanho(int largura, int altura) {
        java.awt.Rectangle tela = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        setSize(Math.min(largura, tela.width), Math.min(altura, tela.height));
        setLocationRelativeTo(null);
    }

    /** Larguras preferidas das colunas; colunas curtas recebem largura mínima para não cortar o texto. */
    protected static void definirLarguras(javax.swing.JTable tabela, int... larguras) {
        for (int i = 0; i < larguras.length; i++) {
            javax.swing.table.TableColumn coluna = tabela.getColumnModel().getColumn(i);
            coluna.setPreferredWidth(larguras[i]);
            if (larguras[i] <= 110) {
                coluna.setMinWidth(larguras[i]);
            }
        }
    }

    /** Fecha esta tela e retorna à principal. */
    protected void voltar() {
        dispose();
    }

    /** Modelo de tabela não editável e que sabe o tipo de cada coluna (para ordenar números certo). */
    protected DefaultTableModel criarModeloTabela(String[] colunas) {
        return new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int coluna) {
                if (getRowCount() > 0 && getValueAt(0, coluna) != null) {
                    return getValueAt(0, coluna).getClass();
                }
                return Object.class;
            }
        };
    }

    protected void mostrarSucesso(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Tudo certo!", JOptionPane.INFORMATION_MESSAGE);
    }

    protected void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Não foi possível concluir", JOptionPane.ERROR_MESSAGE);
    }

    protected boolean confirmar(String mensagem) {
        int resposta = JOptionPane.showConfirmDialog(this, mensagem, "Confirmação",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return resposta == JOptionPane.YES_OPTION;
    }

    /** Cor usada em textos de contagem nos títulos dos cards. */
    protected static final Color COR_CONTADOR = Estilo.PRIMARIA;
}
