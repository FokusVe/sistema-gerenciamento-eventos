import controller.EventoController;
import controller.InscricaoController;
import controller.ParticipanteController;
import controller.RelatorioController;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import model.Evento;
import model.GerenciadorEventos;
import model.Participante;
import view.Estilo;
import view.TelaPrincipal;

/**
 * Classe principal: cria o Model, os Controllers e abre a Tela Principal.
 */
public class Main {

    public static void main(String[] args) {
        // Texto mais suave e aparência do sistema operacional (biblioteca padrão do Java).
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // se não conseguir, usa a aparência padrão do Swing
        }
        Estilo.aplicarPadroesGlobais(); // cores e fontes do sistema (pacote view)

        // MODEL: armazenamento em memória compartilhado por todo o sistema
        GerenciadorEventos gerenciador = new GerenciadorEventos();
        carregarDadosDeExemplo(gerenciador);

        // CONTROLLERS: ligam as telas ao Model
        EventoController eventoController = new EventoController(gerenciador);
        ParticipanteController participanteController = new ParticipanteController(gerenciador);
        InscricaoController inscricaoController = new InscricaoController(gerenciador);
        RelatorioController relatorioController = new RelatorioController(gerenciador);

        // VIEW: abre a tela principal
        SwingUtilities.invokeLater(() -> new TelaPrincipal(
                eventoController, participanteController,
                inscricaoController, relatorioController).setVisible(true));
    }

    /**
     * Alguns registros para facilitar a apresentação.
     * Para começar com o sistema vazio, basta apagar a chamada deste método no main.
     */
    private static void carregarDadosDeExemplo(GerenciadorEventos g) {
        g.cadastrarEvento(new Evento(1, "Semana de Tecnologia", "25/11/2026", "Auditório A", 50));
        g.cadastrarEvento(new Evento(2, "Workshop de Java Swing", "02/12/2026", "Laboratório 3", 3));
        g.cadastrarEvento(new Evento(3, "Palestra de Carreira em TI", "10/12/2026", "Sala 105", 30));

        g.cadastrarParticipante(new Participante(1, "Ana Souza", "ana@email.com"));
        g.cadastrarParticipante(new Participante(2, "Bruno Lima", "(61) 99999-1111"));
        g.cadastrarParticipante(new Participante(3, "Carla Mendes", "carla@email.com"));
        g.cadastrarParticipante(new Participante(4, "Diego Rocha", "(61) 98888-2222"));

        g.buscarEvento(1).inscrever(g.buscarParticipante(1));
        g.buscarEvento(1).inscrever(g.buscarParticipante(2));
        g.buscarEvento(2).inscrever(g.buscarParticipante(3));
    }
}
