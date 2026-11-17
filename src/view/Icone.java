package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import javax.swing.Icon;

/**
 * Ícones simples desenhados com Graphics2D (não depende de imagens nem de bibliotecas).
 */
public class Icone implements Icon {

    public enum Tipo { EVENTO, PARTICIPANTE, INSCRICAO, RELATORIO, SAIR, VAGAS }

    private final Tipo tipo;
    private final int tamanho;
    private final Color cor;

    public Icone(Tipo tipo, int tamanho, Color cor) {
        this.tipo = tipo;
        this.tamanho = tamanho;
        this.cor = cor;
    }

    @Override
    public int getIconWidth() {
        return tamanho;
    }

    @Override
    public int getIconHeight() {
        return tamanho;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.translate(x, y);
        float s = tamanho;
        g2.setColor(cor);
        g2.setStroke(new BasicStroke(Math.max(1.6f, s / 12f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch (tipo) {
            case EVENTO: // calendário
                g2.drawRoundRect(r(s * .1), r(s * .2), r(s * .8), r(s * .72), r(s * .18), r(s * .18));
                g2.drawLine(r(s * .1), r(s * .42), r(s * .9), r(s * .42));
                g2.drawLine(r(s * .32), r(s * .08), r(s * .32), r(s * .3));
                g2.drawLine(r(s * .68), r(s * .08), r(s * .68), r(s * .3));
                int d = Math.max(2, r(s * .1));
                g2.fillOval(r(s * .28), r(s * .56), d, d);
                g2.fillOval(r(s * .46), r(s * .56), d, d);
                g2.fillOval(r(s * .64), r(s * .56), d, d);
                g2.fillOval(r(s * .28), r(s * .73), d, d);
                g2.fillOval(r(s * .46), r(s * .73), d, d);
                break;
            case PARTICIPANTE: // pessoa
                g2.drawOval(r(s * .32), r(s * .08), r(s * .36), r(s * .36));
                g2.drawArc(r(s * .12), r(s * .55), r(s * .76), r(s * .7), 0, 180);
                break;
            case INSCRICAO: // prancheta com check
                g2.drawRoundRect(r(s * .16), r(s * .14), r(s * .68), r(s * .8), r(s * .16), r(s * .16));
                g2.drawRoundRect(r(s * .36), r(s * .06), r(s * .28), r(s * .16), r(s * .08), r(s * .08));
                Path2D check = new Path2D.Float();
                check.moveTo(s * .32, s * .56);
                check.lineTo(s * .45, s * .69);
                check.lineTo(s * .70, s * .42);
                g2.draw(check);
                break;
            case RELATORIO: // gráfico de barras
                g2.drawLine(r(s * .1), r(s * .9), r(s * .9), r(s * .9));
                g2.fillRoundRect(r(s * .18), r(s * .52), r(s * .16), r(s * .32), 3, 3);
                g2.fillRoundRect(r(s * .42), r(s * .18), r(s * .16), r(s * .66), 3, 3);
                g2.fillRoundRect(r(s * .66), r(s * .38), r(s * .16), r(s * .46), 3, 3);
                break;
            case SAIR: // porta com seta
                Path2D porta = new Path2D.Float();
                porta.moveTo(s * .45, s * .12);
                porta.lineTo(s * .15, s * .12);
                porta.lineTo(s * .15, s * .88);
                porta.lineTo(s * .45, s * .88);
                g2.draw(porta);
                g2.drawLine(r(s * .38), r(s * .5), r(s * .88), r(s * .5));
                g2.drawLine(r(s * .72), r(s * .34), r(s * .88), r(s * .5));
                g2.drawLine(r(s * .72), r(s * .66), r(s * .88), r(s * .5));
                break;
            case VAGAS: // cadeira / assento
                g2.drawRoundRect(r(s * .25), r(s * .1), r(s * .5), r(s * .45), r(s * .14), r(s * .14));
                g2.drawLine(r(s * .15), r(s * .62), r(s * .85), r(s * .62));
                g2.drawLine(r(s * .25), r(s * .62), r(s * .25), r(s * .9));
                g2.drawLine(r(s * .75), r(s * .62), r(s * .75), r(s * .9));
                break;
            default:
                break;
        }
        g2.dispose();
    }

    private static int r(double v) {
        return (int) Math.round(v);
    }
}
