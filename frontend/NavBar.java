package frontend;

import backend.*;
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

    private final MainFrame mainFrame;
    private final Backend   backend;
    private       JLabel    lblAlerta;

    public NavBar(MainFrame frame, Backend backend, int abaAtiva) {
        this.mainFrame = frame;
        this.backend   = backend;

        setBackground(Tema.BG2);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.BORDER));
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 50));

        // ── Esquerda: brand + abas ────────────────────────────────────────
        JPanel esq = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 8));
        esq.setBackground(Tema.BG2);

        JLabel brand = Tema.criarLabel("◈ SIRATECH", Tema.F_MONO, Tema.CYAN);
        brand.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 18));
        esq.add(brand);

        for (int i = 0; i < ABAS.length; i++) {
            final int idx = i;
            JButton btn = new JButton(ABAS[i]);
            btn.setFont(Tema.F_SMALL);
            Tema.semFoco(btn);
            btn.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            if (i == abaAtiva) {
                btn.setBackground(Tema.GREEN);
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(Tema.BG2);
                btn.setForeground(Tema.TEXT2);
                btn.addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { btn.setForeground(Tema.GREENL); }
                    public void mouseExited (MouseEvent e) { btn.setForeground(Tema.TEXT2);  }
                });
            }
            btn.addActionListener(e -> mainFrame.navegarPara(idx));
            esq.add(btn);
        }

        // Busca global
        JTextField campoBusca = new JTextField(14);
        campoBusca.setBackground(new Color(18, 32, 20));
        campoBusca.setForeground(Tema.TEXT3);
        campoBusca.setCaretColor(Tema.GREENL);
        campoBusca.setFont(Tema.F_SMALL);
        campoBusca.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.BORDER, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        campoBusca.setText("🔍 Buscar...");
        campoBusca.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (campoBusca.getText().startsWith("🔍")) campoBusca.setText("");
            }
            public void focusLost(FocusEvent e) {
                if (campoBusca.getText().isEmpty()) campoBusca.setText("🔍 Buscar...");
            }
        });
        campoBusca.addActionListener(e -> buscarGlobal(campoBusca.getText().trim()));
        esq.add(campoBusca);

        add(esq, BorderLayout.WEST);

        // ── Direita: sino + fazenda + usuário + logout ────────────────────
        JPanel dir = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 12));
        dir.setBackground(Tema.BG2);

        lblAlerta = new JLabel("🔔 0");
        lblAlerta.setFont(Tema.F_SMALL);
        lblAlerta.setForeground(Tema.TEXT3);
        lblAlerta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblAlerta.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { mainFrame.navegarPara(4); }
        });
        dir.add(lblAlerta);

        Fazenda fa = backend.authService.getFazendaAtiva();
        String nomeFaz = fa != null ? "🏠 " + fa.getNome() : "🏠 Fazenda";
        JButton btnFaz = new JButton(nomeFaz);
        btnFaz.setFont(Tema.F_SMALL);
        Tema.semFoco(btnFaz);
        btnFaz.setBackground(new Color(20, 50, 24));
        btnFaz.setForeground(Tema.GREEN3);
        btnFaz.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.BORDER, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        btnFaz.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnFaz.addActionListener(e -> selecionarFazenda(btnFaz));
        dir.add(btnFaz);

        String nomeUser = backend.authService.getUsuarioAtual() != null
                ? backend.authService.getUsuarioAtual().getNomeCompleto() : "";
        JLabel lblUsuario = new JLabel("● " + nomeUser);
        lblUsuario.setFont(Tema.F_SMALL);
        lblUsuario.setForeground(Tema.GREEN3);
        dir.add(lblUsuario);

        if (backend.authService.isAdmin()) {
            JLabel badge = Tema.criarLabel("ADM", new Font("Segoe UI", Font.BOLD, 9), Tema.CYAN);
            badge.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Tema.CYAN, 1),
                    BorderFactory.createEmptyBorder(1, 4, 1, 4)));
            dir.add(badge);
        }

        JButton btnTrocar = new JButton("⇄ Trocar Usu.");
        btnTrocar.setFont(Tema.F_SMALL);
        Tema.semFoco(btnTrocar);
        btnTrocar.setBackground(new Color(30, 50, 32));
        btnTrocar.setForeground(Tema.TEXT2);
        btnTrocar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.BORDER, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        btnTrocar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
        btnFaz.setText("🏠 " + f.getNome());
        LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Mudou fazenda ativa: " + f.getNome());
        mainFrame.navegarPara(0);
    }

    private void buscarGlobal(String termo) {
        if (termo.isEmpty() || termo.startsWith("🔍")) return;
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
        scroll.setPreferredSize(new Dimension(520, 360));
        scroll.setBorder(BorderFactory.createLineBorder(Tema.BORDER, 1));
        JOptionPane.showMessageDialog(this, scroll, "Resultado da Busca", JOptionPane.PLAIN_MESSAGE);
    }

    private void atualizarAlertas() {
        int total = backend.totalAlertas();
        lblAlerta.setText("🔔 " + total);
        lblAlerta.setForeground(total > 0 ? Tema.AMBER : Tema.TEXT3);
    }
}
