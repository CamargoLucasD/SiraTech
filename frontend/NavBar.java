package frontend;

import backend.*;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class NavBar extends JPanel {

    private static final String[] ABAS = {
        "DASHBOARD","ANIMAIS","COLEIRAS","FAZENDAS",
        "ALERTAS","RELATÓRIOS","SAÚDE","FINANCEIRO","CONFIG"
    };

    // Ícones correspondentes a cada aba (mesma ordem de ABAS)
    private static final String[] ABAS_ICONS = {
        "layout","tag","radio","database",
        "alert-triangle","bar-chart-2","heart","dollar-sign","settings"
    };

    private final MainFrame mainFrame;
    private final Backend   backend;
    private       JLabel    lblAlerta;

    // ── Ícone SVG helper ──────────────────────────────────────────────────────
    private static FlatSVGIcon ico(String name, int size) {
        try {
            return new FlatSVGIcon("icons/" + name + ".svg", size, size);
        } catch (Exception e) {
            return null;
        }
    }

    public NavBar(MainFrame frame, Backend backend, int abaAtiva) {
        this.mainFrame = frame;
        this.backend   = backend;

        setBackground(Tema.BG2);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.BORDER));
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 52));

        // ── Esquerda: brand + abas ────────────────────────────────────────────
        JPanel esq = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 9));
        esq.setBackground(Tema.BG2);

        // Brand com ícone
        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        brand.setBackground(Tema.BG2);
        brand.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 14));
        JLabel icoB = new JLabel(ico("fundologin", 16));
        JLabel lblB = Tema.criarLabel("SIRATECH", Tema.F_MONO, Tema.CYAN);
        brand.add(icoB);
        brand.add(lblB);
        esq.add(brand);

        // Separador visual entre brand e abas
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setForeground(Tema.BORDER);
        sep.setPreferredSize(new Dimension(1, 28));
        esq.add(sep);

        // Botões de navegação com ícones
        for (int i = 0; i < ABAS.length; i++) {
            final int idx = i;
            JButton btn = new JButton(ABAS[i]);
            btn.setIcon(ico(ABAS_ICONS[i], 13));
            btn.setIconTextGap(5);
            btn.setFont(Tema.F_SMALL);
            Tema.semFoco(btn);
            btn.setBorder(BorderFactory.createEmptyBorder(5, 9, 5, 9));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setHorizontalTextPosition(SwingConstants.RIGHT);

            if (i == abaAtiva) {
                btn.setBackground(Tema.GREEN);
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(Tema.BG2);
                btn.setForeground(Tema.TEXT2);
                btn.addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        btn.setBackground(new Color(20, 38, 22));
                        btn.setForeground(Tema.GREENL);
                    }
                    public void mouseExited(MouseEvent e) {
                        btn.setBackground(Tema.BG2);
                        btn.setForeground(Tema.TEXT2);
                    }
                });
            }
            btn.addActionListener(e -> mainFrame.navegarPara(idx));
            esq.add(btn);
        }

        // Campo de busca global
        JTextField campoBusca = new JTextField(13);
        campoBusca.setBackground(new Color(18, 32, 20));
        campoBusca.setForeground(Tema.TEXT3);
        campoBusca.setCaretColor(Tema.GREENL);
        campoBusca.setFont(Tema.F_SMALL);
        campoBusca.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.BORDER, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        campoBusca.setText("Buscar...");
        campoBusca.setToolTipText("Busca global: animais, coleiras, fazendas");
        campoBusca.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if ("Buscar...".equals(campoBusca.getText())) campoBusca.setText("");
                campoBusca.setForeground(Tema.TEXT);
            }
            public void focusLost(FocusEvent e) {
                if (campoBusca.getText().isEmpty()) {
                    campoBusca.setText("Buscar...");
                    campoBusca.setForeground(Tema.TEXT3);
                }
            }
        });
        campoBusca.addActionListener(e -> buscarGlobal(campoBusca.getText().trim()));
        esq.add(campoBusca);

        add(esq, BorderLayout.WEST);

        // ── Direita: alertas + fazenda + usuário + trocar ─────────────────────
        JPanel dir = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 12));
        dir.setBackground(Tema.BG2);

        // Sino de alertas com ícone SVG
        lblAlerta = new JLabel();
        lblAlerta.setIcon(ico("bell", 14));
        lblAlerta.setText(" 0");
        lblAlerta.setFont(Tema.F_SMALL);
        lblAlerta.setForeground(Tema.TEXT3);
        lblAlerta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblAlerta.setToolTipText("Ver alertas ativos");
        lblAlerta.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { mainFrame.navegarPara(4); }
        });
        dir.add(lblAlerta);

        // Separador
        JSeparator sep2 = new JSeparator(SwingConstants.VERTICAL);
        sep2.setForeground(Tema.BORDER);
        sep2.setPreferredSize(new Dimension(1, 24));
        dir.add(sep2);

        // Botão fazenda ativa
        Fazenda fa = backend.authService.getFazendaAtiva();
        String nomeFaz = fa != null ? fa.getNome() : "Fazenda";
        JButton btnFaz = new JButton(nomeFaz);
        btnFaz.setIcon(ico("map-pin", 13));
        btnFaz.setIconTextGap(5);
        btnFaz.setFont(Tema.F_SMALL);
        Tema.semFoco(btnFaz);
        btnFaz.setBackground(new Color(20, 50, 24));
        btnFaz.setForeground(Tema.GREEN3);
        btnFaz.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(34, 68, 38), 1),
                BorderFactory.createEmptyBorder(4, 9, 4, 9)));
        btnFaz.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnFaz.setToolTipText("Clique para trocar de fazenda");
        btnFaz.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnFaz.setBackground(new Color(26, 62, 30)); }
            public void mouseExited(MouseEvent e)  { btnFaz.setBackground(new Color(20, 50, 24)); }
        });
        btnFaz.addActionListener(e -> selecionarFazenda(btnFaz));
        dir.add(btnFaz);

        // Nome do usuário com ícone
        String nomeUser = backend.authService.getUsuarioAtual() != null
                ? backend.authService.getUsuarioAtual().getNomeCompleto() : "";
        JPanel usuarioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        usuarioPanel.setBackground(Tema.BG2);
        usuarioPanel.add(new JLabel(ico("user", 13)));
        JLabel lblUsuario = Tema.criarLabel(nomeUser, Tema.F_SMALL, Tema.GREEN3);
        usuarioPanel.add(lblUsuario);
        dir.add(usuarioPanel);

        // Badge ADM
        if (backend.authService.isAdmin()) {
            JLabel badge = Tema.criarLabel("ADM", new Font("Segoe UI", Font.BOLD, 9), Tema.CYAN);
            badge.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Tema.CYAN, 1),
                    BorderFactory.createEmptyBorder(1, 4, 1, 4)));
            dir.add(badge);
        }

        // Botão trocar usuário / logout
        JButton btnTrocar = new JButton("TROCAR");
        btnTrocar.setIcon(ico("log-out", 13));
        btnTrocar.setIconTextGap(5);
        btnTrocar.setFont(Tema.F_SMALL);
        Tema.semFoco(btnTrocar);
        btnTrocar.setBackground(new Color(30, 50, 32));
        btnTrocar.setForeground(Tema.TEXT2);
        btnTrocar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.BORDER, 1),
                BorderFactory.createEmptyBorder(4, 9, 4, 9)));
        btnTrocar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnTrocar.setToolTipText("Trocar usuário / Logout");
        btnTrocar.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnTrocar.setBackground(new Color(50, 20, 20));
                btnTrocar.setForeground(Tema.RED);
            }
            public void mouseExited(MouseEvent e) {
                btnTrocar.setBackground(new Color(30, 50, 32));
                btnTrocar.setForeground(Tema.TEXT2);
            }
        });
        btnTrocar.addActionListener(e -> {
            int opt = JOptionPane.showConfirmDialog(this,
                    "Deseja sair e trocar de usuário?", "Trocar Usuário",
                    JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Logout");
                backend.authService.logout();
                mainFrame.irParaLogin();
            }
        });
        dir.add(btnTrocar);

        add(dir, BorderLayout.EAST);

        new javax.swing.Timer(5000, e -> atualizarAlertas()).start();
        atualizarAlertas();
    }

    // ── Selecionar fazenda ────────────────────────────────────────────────────
    private void selecionarFazenda(JButton btnFaz) {
        List<Fazenda> fazendas = backend.fazendaService.listarTodas();
        if (fazendas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhuma fazenda cadastrada!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String[] nomes = fazendas.stream()
                .map(f -> f.getNome() + " — " + f.getMunicipio() + "/" + f.getEstado())
                .toArray(String[]::new);
        String escolhida = (String) JOptionPane.showInputDialog(this,
                "Selecione a fazenda:", "Trocar Fazenda",
                JOptionPane.QUESTION_MESSAGE, null, nomes, nomes[0]);
        if (escolhida == null) return;

        JPasswordField pf = Tema.criarSenha();
        int r = JOptionPane.showConfirmDialog(this, pf,
                "Confirme sua senha para acessar", JOptionPane.OK_CANCEL_OPTION);
        if (r != JOptionPane.OK_OPTION) return;
        if (!backend.authService.verificarSenha(new String(pf.getPassword()))) {
            JOptionPane.showMessageDialog(this, "Senha incorreta!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int idx = Arrays.asList(nomes).indexOf(escolhida);
        Fazenda f = fazendas.get(idx);
        backend.authService.setFazendaAtiva(f);
        btnFaz.setText(f.getNome());
        LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Mudou fazenda ativa: " + f.getNome());
        mainFrame.navegarPara(0);
    }

    // ── Busca global ──────────────────────────────────────────────────────────
    private void buscarGlobal(String termo) {
        if (termo.isEmpty() || "Buscar...".equals(termo)) return;
        String t = termo.toLowerCase();
        StringBuilder sb = new StringBuilder("=== BUSCA: \"").append(termo).append("\" ===\n\n");

        sb.append("--- ANIMAIS ---\n");
        Fazenda fa = backend.authService.getFazendaAtiva();
        List<Animal> animais = fa != null
                ? backend.animalService.buscarPorTermo(termo, fa.getId())
                : backend.animalService.listarTodos();
        if (animais.isEmpty()) sb.append("  Nenhum resultado\n");
        for (Animal a : animais)
            sb.append(String.format("  %-12s %-8s %-10s %s\n",
                    a.getNome(), a.getNumeroBrinco(), a.getRaca(), a.getStatus()));

        sb.append("\n--- COLEIRAS ---\n");
        boolean achouColar = false;
        for (Colar c : backend.colarService.listarTodos()) {
            if (c.getId().toLowerCase().contains(t)) {
                sb.append(String.format("  %-6s Bat:%3d%% %s\n",
                        c.getId(), c.getBateria(), c.isDisponivel() ? "Disponível" : "Em uso"));
                achouColar = true;
            }
        }
        if (!achouColar) sb.append("  Nenhum resultado\n");

        sb.append("\n--- FAZENDAS ---\n");
        boolean achouFaz = false;
        for (Fazenda f : backend.fazendaService.listarTodas()) {
            if (f.getNome().toLowerCase().contains(t)
                    || (f.getMunicipio() != null && f.getMunicipio().toLowerCase().contains(t))) {
                sb.append(String.format("  %-20s %s/%s\n", f.getNome(), f.getMunicipio(), f.getEstado()));
                achouFaz = true;
            }
        }
        if (!achouFaz) sb.append("  Nenhum resultado\n");

        JTextArea area = new JTextArea(sb.toString());
        area.setBackground(Tema.BG3);
        area.setForeground(Tema.TEXT);
        area.setFont(new Font("Courier New", Font.PLAIN, 11));
        area.setEditable(false);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(540, 380));
        scroll.setBorder(BorderFactory.createLineBorder(Tema.BORDER, 1));
        JOptionPane.showMessageDialog(this, scroll, "Resultado da Busca", JOptionPane.PLAIN_MESSAGE);
    }

    // ── Atualizar contador de alertas ─────────────────────────────────────────
    private void atualizarAlertas() {
        int total = backend.totalAlertas();
        lblAlerta.setText(" " + total);
        lblAlerta.setForeground(total > 0 ? Tema.AMBER : Tema.TEXT3);
        lblAlerta.setIcon(total > 0 ? ico("bell", 14) : ico("bell", 14));
    }
}
