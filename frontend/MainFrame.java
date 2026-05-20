package frontend;

import backend.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * MainFrame — janela principal do SIRATECH.
 * Construtor sem parâmetros (busca Backend.getInstance() internamente),
 * compatível com MainFrame::new usado no main.
 */
public class MainFrame extends JFrame {

    // ── Índices de tela (coincidem com NavBar.ABAS) ──────────────────────────
    public static final int TELA_DASHBOARD   = 0;
    public static final int TELA_ANIMAIS     = 1;
    public static final int TELA_COLEIRAS    = 2;
    public static final int TELA_FAZENDAS    = 3;
    public static final int TELA_ALERTAS     = 4;
    public static final int TELA_RELATORIOS  = 5;
    public static final int TELA_SAUDE       = 6;
    public static final int TELA_FINANCEIRO  = 7;
    public static final int TELA_CONFIG      = 8;

    private static final String[] NOMES = {
        "DASHBOARD", "ANIMAIS", "BRINCOS", "FAZENDAS",
        "ALERTAS", "RELATORIOS", "SAUDE", "FINANCEIRO", "CONFIG"
    };

    private final Backend    backend;
    private final CardLayout cardLayout;
    private final JPanel     painelPrincipal;

    // Referências para poder chamar atualizarDados() ao navegar
    private DashboardScreen      dashboardScreen;
    private CadastroAnimalScreen animaisScreen;
    private MonitoramentoScreen  monitoramentoScreen;
    private RelatoriosScreen     relatoriosScreen;
    private SaudeScreen          saudeScreen;
    private FinanceiroScreen     financeiroScreen;

    public MainFrame() {
        this.backend = Backend.getInstance();

        // ── Configuração da janela ────────────────────────────────────────────
        setTitle("SIRATECH — Sistema Integrado de Rastreamento Agro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1240, 740));
        setPreferredSize(new Dimension(1440, 840));

        // Fundo global garantido — evita flicker branco no resize
        getContentPane().setBackground(Tema.BG);

        // ── Painel principal com CardLayout ───────────────────────────────────
        cardLayout      = new CardLayout();
        painelPrincipal = new JPanel(cardLayout);
        painelPrincipal.setBackground(Tema.BG);

        // ── Montar telas ──────────────────────────────────────────────────────
        painelPrincipal.add(new LoginScreen(this, backend), "LOGIN");

        dashboardScreen = new DashboardScreen(this, backend);
        painelPrincipal.add(dashboardScreen, "DASHBOARD");

        animaisScreen = new CadastroAnimalScreen(this, backend);
        painelPrincipal.add(animaisScreen, "ANIMAIS");

        painelPrincipal.add(new ColeiraScreen(this, backend),         "BRINCOS");
        painelPrincipal.add(new CadastroFazendaScreen(this, backend), "FAZENDAS");

        monitoramentoScreen = new MonitoramentoScreen(this, backend);
        painelPrincipal.add(monitoramentoScreen, "ALERTAS");

        relatoriosScreen = new RelatoriosScreen(this, backend);
        painelPrincipal.add(relatoriosScreen, "RELATORIOS");

        saudeScreen = new SaudeScreen(this, backend);
        painelPrincipal.add(saudeScreen, "SAUDE");

        financeiroScreen = new FinanceiroScreen(this, backend);
        painelPrincipal.add(financeiroScreen, "FINANCEIRO");

        painelPrincipal.add(new ConfigScreen(this, backend), "CONFIG");

        setContentPane(painelPrincipal);
        cardLayout.show(painelPrincipal, "LOGIN");

        // ── Registro de atividade para timeout ───────────────────────────────
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (backend.authService.isLogado())
                LoginScreen.registrarAtividade();
        }, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);

        // ── Timer de timeout de sessão (30 min) ──────────────────────────────
        new javax.swing.Timer(60_000, e -> {
            if (backend.authService.isLogado() && LoginScreen.sessaoExpirou()) {
                LogAtividades.registrar(backend.authService.getUsuarioLogado(),
                        "Sessão expirada por inatividade");
                backend.authService.logout();
                irParaLogin();
                JOptionPane.showMessageDialog(this,
                        "Sessão expirada por inatividade. Faça login novamente.",
                        "Sessão Expirada", JOptionPane.WARNING_MESSAGE);
            }
        }).start();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ── Navegação ─────────────────────────────────────────────────────────────

    public void navegarPara(int tela) {
        if (!backend.authService.isLogado()) {
            cardLayout.show(painelPrincipal, "LOGIN");
            return;
        }
        LoginScreen.registrarAtividade();
        if (tela < 0 || tela >= NOMES.length) return;

        cardLayout.show(painelPrincipal, NOMES[tela]);
        atualizarTela(tela);
    }

    public void irParaLogin() {
        cardLayout.show(painelPrincipal, "LOGIN");
    }

    // ── Atualização de dados ao navegar ───────────────────────────────────────

    private void atualizarTela(int tela) {
        try {
            switch (tela) {
                case TELA_DASHBOARD  -> dashboardScreen.atualizarDados();
                case TELA_ANIMAIS    -> animaisScreen.atualizar();
                case TELA_ALERTAS    -> monitoramentoScreen.atualizarDados();
                case TELA_RELATORIOS -> relatoriosScreen.atualizarDados();
                case TELA_SAUDE      -> saudeScreen.atualizarDados();
                case TELA_FINANCEIRO -> financeiroScreen.atualizarDados();
                // Coleiras, Fazendas e Config têm refresh via botão interno
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
