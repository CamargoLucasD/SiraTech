package frontend;

import backend.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

// ============================================================
//  SIRATECH - FRONTEND COMPLETO (Java Swing)
// ============================================================

// ─────────────────────────────────────────────────────────────
// TEMA
// ─────────────────────────────────────────────────────────────
class Tema {
    static final Color BG     = new Color(10, 20, 12);
    static final Color BG2    = new Color(15, 28, 17);
    static final Color BG3    = new Color(20, 35, 22);
    static final Color CARD   = new Color(24, 40, 26);
    static final Color GREEN  = new Color(34, 139, 34);
    static final Color GREEN2 = new Color(46, 160, 46);
    static final Color GREEN3 = new Color(72, 199, 72);
    static final Color GREENL = new Color(120, 230, 120);
    static final Color CYAN   = new Color(0, 220, 200);
    static final Color TEXT   = new Color(230, 245, 232);
    static final Color TEXT2  = new Color(160, 200, 165);
    static final Color TEXT3  = new Color(100, 150, 105);
    static final Color RED    = new Color(220, 50, 50);
    static final Color AMBER  = new Color(240, 160, 30);
    static final Color BORDER = new Color(32, 58, 35);
    static final Color ACCENT = new Color(0, 180, 170);

    static final Font F_TITLE  = new Font("Segoe UI", Font.BOLD, 15);
    static final Font F_LABEL  = new Font("Segoe UI", Font.BOLD, 11);
    static final Font F_BODY   = new Font("Segoe UI", Font.PLAIN, 12);
    static final Font F_BIG    = new Font("Segoe UI", Font.BOLD, 28);
    static final Font F_SMALL  = new Font("Segoe UI", Font.PLAIN, 10);
    static final Font F_MONO   = new Font("Courier New", Font.BOLD, 13);

    static JTextField criarCampo(String placeholder) {
        JTextField f = new JTextField(placeholder);
        f.setBackground(BG3);
        f.setForeground(TEXT);
        f.setCaretColor(GREENL);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(7, 12, 7, 12)));
        f.setFont(F_BODY);
        return f;
    }

    static JPasswordField criarSenha() {
        JPasswordField f = new JPasswordField();
        f.setBackground(BG3);
        f.setForeground(TEXT);
        f.setCaretColor(GREENL);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(7, 12, 7, 12)));
        f.setFont(F_BODY);
        return f;
    }

    static JComboBox<String> criarCombo(String... opcoes) {
        JComboBox<String> c = new JComboBox<>(opcoes);
        c.setBackground(BG3);
        c.setForeground(TEXT);
        c.setFont(F_BODY);
        c.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        return c;
    }

    static JButton criarBotaoPrimario(String texto) {
        JButton b = new JButton(texto);
        b.setBackground(GREEN);
        b.setForeground(Color.WHITE);
        b.setFont(F_LABEL);
        b.setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(GREEN2); }
            public void mouseExited(MouseEvent e)  { b.setBackground(GREEN); }
        });
        return b;
    }

    static JButton criarBotaoSecundario(String texto) {
        JButton b = new JButton(texto);
        b.setBackground(BG3);
        b.setForeground(TEXT2);
        b.setFont(F_LABEL);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    static JButton criarBotaoPerigo(String texto) {
        JButton b = new JButton(texto);
        b.setBackground(new Color(80, 20, 20));
        b.setForeground(RED);
        b.setFont(F_LABEL);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(RED, 1),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    static JPanel criarCard() {
        JPanel p = new JPanel();
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        return p;
    }

    static JLabel criarLabel(String texto, Font font, Color cor) {
        JLabel l = new JLabel(texto);
        l.setFont(font);
        l.setForeground(cor);
        return l;
    }

    static JTable criarTabela(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setBackground(BG3);
        t.setForeground(TEXT2);
        t.setGridColor(BORDER);
        t.setFont(F_BODY);
        t.setRowHeight(30);
        t.setSelectionBackground(new Color(34, 80, 36));
        t.setSelectionForeground(TEXT);
        t.getTableHeader().setBackground(CARD);
        t.getTableHeader().setForeground(TEXT3);
        t.getTableHeader().setFont(F_LABEL);
        t.getTableHeader().setPreferredSize(new Dimension(0, 32));
        return t;
    }

    static JScrollPane criarScroll(JTable tabela) {
        JScrollPane s = new JScrollPane(tabela);
        s.getViewport().setBackground(BG3);
        s.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        s.getVerticalScrollBar().setBackground(BG3);
        return s;
    }
}

// ─────────────────────────────────────────────────────────────
// NAVBAR
// ─────────────────────────────────────────────────────────────
class NavBar extends JPanel {
    private String[] abas = {"DASHBOARD", "ANIMAIS", "COLEIRAS", "FAZENDA", "ALERTAS"};
    private MainFrame mainFrame;
    private int abaAtiva;

    public NavBar(MainFrame frame, int abaAtiva) {
        this.mainFrame = frame;
        this.abaAtiva = abaAtiva;
        setBackground(Tema.BG2);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.BORDER));
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 50));

        // Esquerda: brand + abas
        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 8));
        esquerda.setBackground(Tema.BG2);

        JLabel brand = Tema.criarLabel("◈ SIRATECH", Tema.F_MONO, Tema.CYAN);
        brand.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 24));
        esquerda.add(brand);

        for (int i = 0; i < abas.length; i++) {
            final int idx = i;
            JButton btn = new JButton(abas[i]);
            btn.setFont(Tema.F_SMALL);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            if (i == abaAtiva) {
                btn.setBackground(Tema.GREEN);
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(Tema.BG2);
                btn.setForeground(Tema.TEXT2);
            }
            btn.addActionListener(e -> mainFrame.navegarPara(idx));
            esquerda.add(btn);
        }
        add(esquerda, BorderLayout.WEST);

        // Direita: status
        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        direita.setBackground(Tema.BG2);
        direita.add(Tema.criarLabel("● ONLINE", Tema.F_SMALL, Tema.GREEN3));
        direita.add(Tema.criarLabel("ADMIN", Tema.F_SMALL, Tema.TEXT3));
        add(direita, BorderLayout.EAST);
    }
}

// ─────────────────────────────────────────────────────────────
// STATCARD
// ─────────────────────────────────────────────────────────────
class StatCard extends JPanel {
    public StatCard(String label, String valor, Color corValor, String sub) {
        setBackground(Tema.CARD);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.BORDER, 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(Tema.criarLabel(label, Tema.F_SMALL, Tema.TEXT3));
        add(Box.createVerticalStrut(8));
        add(Tema.criarLabel(valor, Tema.F_BIG, corValor));
        add(Box.createVerticalStrut(4));
        add(Tema.criarLabel(sub, Tema.F_SMALL, Tema.TEXT3));
    }
}

// ─────────────────────────────────────────────────────────────
// MAPA PANEL
// ─────────────────────────────────────────────────────────────
class MapaPanel extends JPanel {
    private double[][] animaisDentro = {{0.30,0.35},{0.50,0.50},{0.65,0.30},{0.40,0.65},{0.55,0.70}};
    private double[][] animaisForaDaArea = {{0.88,0.20},{0.05,0.80}};
    private String[] nomesForaDaArea = {"Flor #A18","Nuvem #A02"};

    public MapaPanel() {
        setBackground(Tema.BG3);
        setPreferredSize(new Dimension(400, 240));
        setBorder(BorderFactory.createLineBorder(Tema.BORDER, 1));
        new Timer(1200, e -> repaint()).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();

        // Grade
        g2.setColor(new Color(46, 160, 46, 30));
        g2.setStroke(new BasicStroke(0.5f));
        for (int x = 0; x < w; x += 30) g2.drawLine(x, 0, x, h);
        for (int y = 0; y < h; y += 30) g2.drawLine(0, y, w, y);

        // Geofence
        g2.setColor(new Color(0, 180, 170, 120));
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10, new float[]{8, 6}, 0));
        g2.drawRoundRect(18, 14, w - 36, h - 28, 14, 14);

        // Animais dentro
        for (double[] pos : animaisDentro) {
            int ax = (int)(pos[0] * w), ay = (int)(pos[1] * h);
            g2.setColor(Tema.GREEN3);
            g2.fillOval(ax - 5, ay - 5, 10, 10);
            g2.setColor(Tema.GREENL);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(ax - 5, ay - 5, 10, 10);
        }

        // Animais fora — pulsante
        long t = System.currentTimeMillis();
        float alpha = 0.4f + 0.6f * Math.abs((float) Math.sin(t / 500.0));
        for (int i = 0; i < animaisForaDaArea.length; i++) {
            int ax = (int)(animaisForaDaArea[i][0] * w), ay = (int)(animaisForaDaArea[i][1] * h);
            g2.setColor(new Color(220, 50, 50, (int)(alpha * 255)));
            g2.fillOval(ax - 5, ay - 5, 10, 10);
            g2.setColor(new Color(255, 100, 100, (int)(alpha * 255)));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(ax - 5, ay - 5, 10, 10);
            g2.setColor(Tema.RED);
            g2.setFont(Tema.F_SMALL);
            g2.drawString(nomesForaDaArea[i], ax + 8, ay + 4);
        }
    }
}

// ─────────────────────────────────────────────────────────────
// TELA LOGIN
// ─────────────────────────────────────────────────────────────
class LoginScreen extends JPanel {
    private JTextField campoUsuario;
    private JPasswordField campoSenha;
    private MainFrame mainFrame;
    private Backend backend;

    public LoginScreen(MainFrame frame, Backend backend) {
        this.mainFrame = frame;
        this.backend = backend;
        setBackground(Tema.BG);
        setLayout(new GridBagLayout());

        // Fundo com gradiente
        setOpaque(true);
        add(criarBoxLogin());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // Gradiente sutil no fundo
        GradientPaint gp = new GradientPaint(0, 0, new Color(10, 20, 12),
                getWidth(), getHeight(), new Color(5, 30, 20));
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
        // Círculos decorativos
        g2.setColor(new Color(34, 139, 34, 20));
        g2.fillOval(-100, -100, 400, 400);
        g2.setColor(new Color(0, 180, 170, 15));
        g2.fillOval(getWidth() - 200, getHeight() - 200, 400, 400);
    }

    private JPanel criarBoxLogin() {
        JPanel box = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Tema.CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(Tema.BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            }
        };
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setPreferredSize(new Dimension(380, 500));
        box.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Logo
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));

        // Tenta carregar logo do arquivo
        JLabel logoLabel = carregarLogo();
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.add(logoLabel);
        logoPanel.add(Box.createVerticalStrut(14));

        JLabel titulo = Tema.criarLabel("SIRATECH", new Font("Segoe UI", Font.BOLD, 22), Tema.CYAN);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.add(titulo);

        JLabel sub = Tema.criarLabel("SISTEMA DE RASTREAMENTO AGRO", Tema.F_SMALL, Tema.TEXT3);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.add(sub);

        box.add(logoPanel);
        box.add(Box.createVerticalStrut(30));

        // Separador
        JSeparator sep = new JSeparator();
        sep.setForeground(Tema.BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        box.add(sep);
        box.add(Box.createVerticalStrut(24));

        // Campos
        JLabel lblUser = Tema.criarLabel("USUÁRIO", Tema.F_SMALL, Tema.TEXT3);
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(lblUser);
        box.add(Box.createVerticalStrut(5));
        campoUsuario = Tema.criarCampo("admin");
        campoUsuario.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        campoUsuario.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(campoUsuario);
        box.add(Box.createVerticalStrut(14));

        JLabel lblSenha = Tema.criarLabel("SENHA", Tema.F_SMALL, Tema.TEXT3);
        lblSenha.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(lblSenha);
        box.add(Box.createVerticalStrut(5));
        campoSenha = Tema.criarSenha();
        campoSenha.setText("12345");
        campoSenha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        campoSenha.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(campoSenha);
        box.add(Box.createVerticalStrut(24));

        JButton btnLogin = Tema.criarBotaoPrimario("ENTRAR NO SISTEMA  →");
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnLogin.addActionListener(e -> realizarLogin());
        box.add(btnLogin);

        // Enter
        campoSenha.addActionListener(e -> realizarLogin());
        campoUsuario.addActionListener(e -> realizarLogin());

        box.add(Box.createVerticalStrut(16));
        JLabel hint = Tema.criarLabel("admin / 12345  ·  gerente / fazenda", Tema.F_SMALL, Tema.TEXT3);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(hint);

        return box;
    }

    private JLabel carregarLogo() {
        // Tenta carregar do arquivo logo.png na pasta do projeto
        try {
            File f = new File("logo.png");
            if (f.exists()) {
                BufferedImage img = ImageIO.read(f);
                Image scaled = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                return new JLabel(new ImageIcon(scaled));
            }
        } catch (Exception ignored) {}

        // Fallback: logo desenhada
        JLabel label = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Círculo externo
                g2.setColor(new Color(0, 180, 170, 80));
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(2, 2, 96, 96);
                // Fundo
                g2.setColor(new Color(15, 40, 20));
                g2.fillOval(6, 6, 88, 88);
                // Texto
                g2.setColor(Tema.CYAN);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
                FontMetrics fm = g2.getFontMetrics();
                String s = "ST";
                int x = (100 - fm.stringWidth(s)) / 2;
                g2.drawString(s, x, 58);
                // Borda verde
                g2.setColor(Tema.GREEN3);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(6, 6, 88, 88);
            }
        };
        label.setPreferredSize(new Dimension(100, 100));
        label.setMinimumSize(new Dimension(100, 100));
        return label;
    }

    private void realizarLogin() {
        String usuario = campoUsuario.getText().trim();
        String senha   = new String(campoSenha.getPassword()).trim();
        if (backend.login(usuario, senha)) {
            mainFrame.navegarPara(0);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Usuário ou senha inválidos!\nUse: admin / 12345",
                    "Erro de Login", JOptionPane.ERROR_MESSAGE);
        }
    }
}

// ─────────────────────────────────────────────────────────────
// TELA DASHBOARD
// ─────────────────────────────────────────────────────────────
class DashboardScreen extends JPanel {
    private Backend backend;
    private MainFrame mainFrame;

    public DashboardScreen(MainFrame frame, Backend backend) {
        this.mainFrame = frame;
        this.backend = backend;
        setBackground(Tema.BG);
        setLayout(new BorderLayout());
        add(new NavBar(frame, 0), BorderLayout.NORTH);
        add(criarConteudo(), BorderLayout.CENTER);
    }

    private JPanel criarConteudo() {
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBackground(Tema.BG);
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Tema.BG);
        header.add(Tema.criarLabel("PAINEL DE CONTROLE", Tema.F_TITLE, Tema.GREENL), BorderLayout.WEST);
        JLabel data = Tema.criarLabel(new java.util.Date().toString().substring(0, 19), Tema.F_SMALL, Tema.TEXT3);
        header.add(data, BorderLayout.EAST);
        content.add(header, BorderLayout.NORTH);

        // Cards métricas
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 10, 0));
        statsRow.setBackground(Tema.BG);
        statsRow.add(new StatCard("ANIMAIS ATIVOS",   String.valueOf(backend.totalAnimais()),       Tema.GREENL, "cadastrados no sistema"));
        statsRow.add(new StatCard("NA ÁREA",          String.valueOf(backend.animaisDentroArea()),  Tema.GREEN3,  "dentro do geofence"));
        statsRow.add(new StatCard("ALERTAS",          String.valueOf(backend.totalAlertas()),        Tema.RED,    "aguardando resolução"));
        statsRow.add(new StatCard("COLARES ATIVOS",   String.valueOf(backend.totalColaresAtivos()), Tema.CYAN,   "GPS conectados"));

        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.setBackground(Tema.BG);
        top.add(statsRow, BorderLayout.NORTH);
        content.add(top, BorderLayout.NORTH);

        // Grade: mapa + lista animais
        JPanel grade = new JPanel(new GridLayout(1, 2, 12, 0));
        grade.setBackground(Tema.BG);

        // Mapa
        JPanel cardMapa = Tema.criarCard();
        cardMapa.setLayout(new BorderLayout(0, 8));
        cardMapa.add(Tema.criarLabel("◈ MAPA EM TEMPO REAL", Tema.F_LABEL, Tema.TEXT3), BorderLayout.NORTH);
        cardMapa.add(new MapaPanel(), BorderLayout.CENTER);
        JPanel leg = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leg.setBackground(Tema.CARD);
        leg.add(Tema.criarLabel("● DENTRO", Tema.F_SMALL, Tema.GREEN3));
        leg.add(Tema.criarLabel("● FORA",   Tema.F_SMALL, Tema.RED));
        leg.add(Tema.criarLabel("◌ GEOFENCE", Tema.F_SMALL, Tema.CYAN));
        cardMapa.add(leg, BorderLayout.SOUTH);
        grade.add(cardMapa);

        // Lista animais com dados reais
        JPanel cardLista = Tema.criarCard();
        cardLista.setLayout(new BorderLayout(0, 8));
        cardLista.add(Tema.criarLabel("◈ ANIMAIS MONITORADOS", Tema.F_LABEL, Tema.TEXT3), BorderLayout.NORTH);

        String[] colunas = {"NOME", "BRINCO", "RAÇA", "LOTE", "STATUS"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Animal a : backend.animalService.listarTodos()) {
            model.addRow(new Object[]{a.getNome(), a.getNumeroBrinco(), a.getRaca(), a.getLote(), a.getStatus()});
        }
        JTable tabela = Tema.criarTabela(model);
        tabela.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                l.setOpaque(true); l.setFont(Tema.F_SMALL);
                String val = v == null ? "" : v.toString();
                if ("Ativo".equals(val) || "DENTRO".equals(val)) { l.setBackground(new Color(26,61,28)); l.setForeground(Tema.GREEN3); }
                else if ("FORA".equals(val)) { l.setBackground(new Color(61,26,26)); l.setForeground(Tema.RED); }
                else { l.setBackground(new Color(61,46,10)); l.setForeground(Tema.AMBER); }
                return l;
            }
        });
        // Clique para ver detalhes
        tabela.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tabela.getSelectedRow();
                    if (row >= 0) {
                        String brinco = model.getValueAt(row, 1).toString();
                        backend.animalService.buscarPorBrinco(brinco).ifPresent(
                            animal -> new DetalhesAnimalDialog(SwingUtilities.getWindowAncestor(DashboardScreen.this), animal)
                        );
                    }
                }
            }
        });
        JLabel hint = Tema.criarLabel("Duplo clique para ver detalhes", Tema.F_SMALL, Tema.TEXT3);
        cardLista.add(hint, BorderLayout.SOUTH);
        cardLista.add(Tema.criarScroll(tabela), BorderLayout.CENTER);
        grade.add(cardLista);

        content.add(grade, BorderLayout.CENTER);
        return content;
    }
}

// ─────────────────────────────────────────────────────────────
// DIALOG: DETALHES DO ANIMAL
// ─────────────────────────────────────────────────────────────
class DetalhesAnimalDialog extends JDialog {
    public DetalhesAnimalDialog(Window owner, Animal animal) {
        super(owner, "Detalhes: " + animal.getNome(), ModalityType.APPLICATION_MODAL);
        setBackground(Tema.BG);
        getContentPane().setBackground(Tema.BG);
        setSize(460, 420);
        setLocationRelativeTo(owner);

        JPanel panel = new JPanel();
        panel.setBackground(Tema.BG);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Cabeçalho
        JPanel head = new JPanel(new BorderLayout());
        head.setBackground(Tema.CARD);
        head.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        JLabel nome = Tema.criarLabel("🐄 " + animal.getNome(), new Font("Segoe UI", Font.BOLD, 18), Tema.GREENL);
        head.add(nome, BorderLayout.WEST);
        JLabel brinco = Tema.criarLabel("# " + animal.getNumeroBrinco(), Tema.F_LABEL, Tema.CYAN);
        head.add(brinco, BorderLayout.EAST);
        panel.add(head);
        panel.add(Box.createVerticalStrut(12));

        // Grid de informações
        JPanel grid = new JPanel(new GridLayout(0, 2, 8, 8));
        grid.setBackground(Tema.BG);
        adicionarInfo(grid, "RAÇA",         animal.getRaca());
        adicionarInfo(grid, "SEXO",         animal.getSexo() != null ? animal.getSexo() : "—");
        adicionarInfo(grid, "PESO",         animal.getPeso() > 0 ? animal.getPeso() + " kg" : "—");
        adicionarInfo(grid, "LOTE",         animal.getLote() != null ? animal.getLote() : "—");
        adicionarInfo(grid, "STATUS",       animal.getStatus());
        adicionarInfo(grid, "COLAR GPS",    animal.getColar() != null ? animal.getColar().getId() : "Sem colar");
        if (animal.getColar() != null) {
            adicionarInfo(grid, "BATERIA",  animal.getColar().getBateria() + "%");
            adicionarInfo(grid, "SINAL",    animal.getColar().getNivelSinal());
        }
        adicionarInfo(grid, "OBSERVAÇÕES",  animal.getObservacoes() != null && !animal.getObservacoes().isEmpty()
                ? animal.getObservacoes() : "Nenhuma");
        panel.add(grid);
        panel.add(Box.createVerticalStrut(16));

        JButton btnFechar = Tema.criarBotaoPrimario("FECHAR");
        btnFechar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnFechar.addActionListener(e -> dispose());
        panel.add(btnFechar);

        setContentPane(new JScrollPane(panel));
        setVisible(true);
    }

    private void adicionarInfo(JPanel grid, String label, String valor) {
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setBackground(Tema.CARD);
        p.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        p.add(Tema.criarLabel(label, Tema.F_SMALL, Tema.TEXT3), BorderLayout.NORTH);
        p.add(Tema.criarLabel(valor, Tema.F_BODY, Tema.TEXT), BorderLayout.CENTER);
        grid.add(p);
    }
}

// ─────────────────────────────────────────────────────────────
// TELA CADASTRO ANIMAL
// ─────────────────────────────────────────────────────────────
class CadastroAnimalScreen extends JPanel {
    private Backend backend;
    private MainFrame mainFrame;
    private JTextField campoNome, campoBrinco, campoPeso, campoObs;
    private JComboBox<String> comboRaca, comboSexo, comboLote, comboStatus, comboColar;
    private DefaultTableModel tabelaModel;
    private JTable tabela;

    public CadastroAnimalScreen(MainFrame frame, Backend backend) {
        this.mainFrame = frame;
        this.backend = backend;
        setBackground(Tema.BG);
        setLayout(new BorderLayout());
        add(new NavBar(frame, 1), BorderLayout.NORTH);
        add(criarConteudo(), BorderLayout.CENTER);
    }

    private JPanel criarConteudo() {
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBackground(Tema.BG);
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Tema.BG);
        header.add(Tema.criarLabel("GESTÃO DE ANIMAIS", Tema.F_TITLE, Tema.GREENL), BorderLayout.WEST);
        JButton btnNovo = Tema.criarBotaoPrimario("+ NOVO ANIMAL");
        btnNovo.addActionListener(e -> limparFormulario());
        header.add(btnNovo, BorderLayout.EAST);
        content.add(header, BorderLayout.NORTH);

        JPanel grade = new JPanel(new GridLayout(1, 2, 12, 0));
        grade.setBackground(Tema.BG);
        grade.add(criarFormulario());
        grade.add(criarPainelTabela());
        content.add(grade, BorderLayout.CENTER);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acoes.setBackground(Tema.BG);
        JButton btnSalvar = Tema.criarBotaoPrimario("✔ SALVAR ANIMAL");
        btnSalvar.addActionListener(e -> salvarAnimal());
        JButton btnDetalhes = Tema.criarBotaoSecundario("DETALHES");
        btnDetalhes.addActionListener(e -> verDetalhes());
        JButton btnRemover = Tema.criarBotaoPerigo("REMOVER");
        btnRemover.addActionListener(e -> removerAnimal());
        JButton btnCancelar = Tema.criarBotaoSecundario("CANCELAR");
        btnCancelar.addActionListener(e -> limparFormulario());
        acoes.add(btnSalvar);
        acoes.add(btnDetalhes);
        acoes.add(btnRemover);
        acoes.add(btnCancelar);
        content.add(acoes, BorderLayout.SOUTH);
        return content;
    }

    private JPanel criarFormulario() {
        JPanel card = Tema.criarCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(Tema.criarLabel("◈ DADOS DO ANIMAL", Tema.F_LABEL, Tema.TEXT3));
        card.add(Box.createVerticalStrut(12));

        card.add(criarLinha("NOME", campoNome = Tema.criarCampo(""),
                             "Nº BRINCO", campoBrinco = Tema.criarCampo("")));
        card.add(Box.createVerticalStrut(8));
        comboRaca = Tema.criarCombo("Nelore","Angus","Gir","Brahman","Senepol","Tabapuã","Simmental");
        comboSexo = Tema.criarCombo("Femea","Macho");
        card.add(criarLinha("RAÇA", comboRaca, "SEXO", comboSexo));
        card.add(Box.createVerticalStrut(8));
        card.add(criarLinha("PESO (KG)", campoPeso = Tema.criarCampo(""),
                             "LOTE", comboLote = Tema.criarCombo("Lote A","Lote B","Lote C")));
        card.add(Box.createVerticalStrut(8));
        comboStatus = Tema.criarCombo("Ativo","Vendido","Abatido");

        // Carregar colares disponíveis
        List<Colar> disponiveis = backend.colarService.listarDisponiveis();
        String[] opcoesColares = new String[disponiveis.size() + 1];
        opcoesColares[0] = "Sem colar";
        for (int i = 0; i < disponiveis.size(); i++) {
            opcoesColares[i+1] = disponiveis.get(i).getId() + " | " + disponiveis.get(i).getBateria() + "%";
        }
        comboColar = Tema.criarCombo(opcoesColares);
        card.add(criarLinha("STATUS", comboStatus, "COLAR GPS", comboColar));
        card.add(Box.createVerticalStrut(8));
        card.add(Tema.criarLabel("OBSERVAÇÕES", Tema.F_SMALL, Tema.TEXT3));
        card.add(Box.createVerticalStrut(4));
        campoObs = Tema.criarCampo("");
        campoObs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        card.add(campoObs);
        return card;
    }

    private JPanel criarPainelTabela() {
        JPanel card = Tema.criarCard();
        card.setLayout(new BorderLayout(0, 8));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Tema.CARD);
        topo.add(Tema.criarLabel("◈ ANIMAIS CADASTRADOS", Tema.F_LABEL, Tema.TEXT3), BorderLayout.WEST);
        JLabel dica = Tema.criarLabel("Duplo clique → detalhes", Tema.F_SMALL, Tema.TEXT3);
        topo.add(dica, BorderLayout.EAST);
        card.add(topo, BorderLayout.NORTH);

        String[] colunas = {"NOME","BRINCO","RAÇA","LOTE","COLAR","STATUS"};
        tabelaModel = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Animal a : backend.animalService.listarTodos()) {
            tabelaModel.addRow(new Object[]{a.getNome(), a.getNumeroBrinco(), a.getRaca(),
                    a.getLote(), a.getColar() != null ? a.getColar().getId() : "—", a.getStatus()});
        }
        tabela = Tema.criarTabela(tabelaModel);
        tabela.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) verDetalhes();
            }
        });
        card.add(Tema.criarScroll(tabela), BorderLayout.CENTER);
        return card;
    }

    private JPanel criarLinha(String l1, JComponent c1, String l2, JComponent c2) {
        JPanel p = new JPanel(new GridLayout(1, 2, 10, 0));
        p.setBackground(Tema.CARD);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        JPanel p1 = new JPanel(new BorderLayout(0, 4)); p1.setBackground(Tema.CARD);
        p1.add(Tema.criarLabel(l1, Tema.F_SMALL, Tema.TEXT3), BorderLayout.NORTH);
        p1.add(c1, BorderLayout.CENTER);
        JPanel p2 = new JPanel(new BorderLayout(0, 4)); p2.setBackground(Tema.CARD);
        p2.add(Tema.criarLabel(l2, Tema.F_SMALL, Tema.TEXT3), BorderLayout.NORTH);
        p2.add(c2, BorderLayout.CENTER);
        p.add(p1); p.add(p2);
        return p;
    }

    private void verDetalhes() {
        int row = tabela.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um animal!", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
        String brinco = tabelaModel.getValueAt(row, 1).toString();
        backend.animalService.buscarPorBrinco(brinco).ifPresent(
            animal -> new DetalhesAnimalDialog(SwingUtilities.getWindowAncestor(this), animal)
        );
    }

    private void removerAnimal() {
        int row = tabela.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um animal!", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
        String nome = tabelaModel.getValueAt(row, 0).toString();
        int confirma = JOptionPane.showConfirmDialog(this,
                "Remover o animal " + nome + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String brinco = tabelaModel.getValueAt(row, 1).toString();
            backend.animalService.buscarPorBrinco(brinco).ifPresent(a -> {
                backend.animalService.remover(a.getId());
                tabelaModel.removeRow(row);
            });
        }
    }

    private void salvarAnimal() {
        String nome = campoNome.getText().trim(), brinco = campoBrinco.getText().trim();
        if (nome.isEmpty() || brinco.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e brinco são obrigatórios!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Animal animal = new Animal();
        animal.setNome(nome);
        animal.setNumeroBrinco(brinco);
        animal.setRaca(comboRaca.getSelectedItem().toString());
        animal.setSexo(comboSexo.getSelectedItem().toString());
        animal.setLote(comboLote.getSelectedItem().toString());
        animal.setStatus(comboStatus.getSelectedItem().toString());
        animal.setObservacoes(campoObs.getText().trim());
        try { animal.setPeso(Double.parseDouble(campoPeso.getText().trim())); } catch (Exception ignored) {}
        Animal salvo = backend.animalService.cadastrar(animal);
        tabelaModel.addRow(new Object[]{salvo.getNome(), salvo.getNumeroBrinco(), salvo.getRaca(),
                salvo.getLote(), "—", salvo.getStatus()});
        limparFormulario();
        JOptionPane.showMessageDialog(this, "Animal " + salvo.getNome() + " cadastrado!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void limparFormulario() {
        campoNome.setText(""); campoBrinco.setText(""); campoPeso.setText(""); campoObs.setText("");
    }
}

// ─────────────────────────────────────────────────────────────
// TELA COLEIRAS
// ─────────────────────────────────────────────────────────────
class ColeiraScreen extends JPanel {
    private Backend backend;
    private MainFrame mainFrame;
    private DefaultTableModel tabelaModel;

    public ColeiraScreen(MainFrame frame, Backend backend) {
        this.mainFrame = frame;
        this.backend = backend;
        setBackground(Tema.BG);
        setLayout(new BorderLayout());
        add(new NavBar(frame, 2), BorderLayout.NORTH);
        add(criarConteudo(), BorderLayout.CENTER);
    }

    private JPanel criarConteudo() {
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBackground(Tema.BG);
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Tema.BG);
        header.add(Tema.criarLabel("GESTÃO DE COLEIRAS GPS", Tema.F_TITLE, Tema.GREENL), BorderLayout.WEST);
        JButton btnAdicionar = Tema.criarBotaoPrimario("+ NOVA COLEIRA");
        btnAdicionar.addActionListener(e -> adicionarColeira());
        header.add(btnAdicionar, BorderLayout.EAST);
        content.add(header, BorderLayout.NORTH);

        // Stats
        List<Colar> todos = backend.colarService.listarTodos();
        long ativos = todos.stream().filter(c -> !c.isDisponivel()).count();
        long disponiveis = todos.stream().filter(Colar::isDisponivel).count();
        long batBaixa = backend.colarService.colaresBateriaBaixa(20).size();

        JPanel stats = new JPanel(new GridLayout(1, 3, 10, 0));
        stats.setBackground(Tema.BG);
        stats.add(new StatCard("TOTAL",       String.valueOf(todos.size()), Tema.GREENL, "coleiras cadastradas"));
        stats.add(new StatCard("EM USO",      String.valueOf(ativos),       Tema.CYAN,   "vinculadas a animais"));
        stats.add(new StatCard("BATERIA BAIXA", String.valueOf(batBaixa),   Tema.AMBER,  "abaixo de 20%"));

        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.setBackground(Tema.BG);
        top.add(stats, BorderLayout.NORTH);
        content.add(top, BorderLayout.NORTH);

        // Tabela
        JPanel card = Tema.criarCard();
        card.setLayout(new BorderLayout(0, 8));
        card.add(Tema.criarLabel("◈ TODAS AS COLEIRAS", Tema.F_LABEL, Tema.TEXT3), BorderLayout.NORTH);

        String[] colunas = {"ID","BATERIA","SINAL","FREQ. (MIN)","FIRMWARE","STATUS","ANIMAL"};
        tabelaModel = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        carregarTabela();

        JTable tabela = Tema.criarTabela(tabelaModel);

        // Colorir coluna BATERIA
        tabela.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                l.setOpaque(true);
                try {
                    int bat = Integer.parseInt(v.toString().replace("%","").trim());
                    if (bat <= 20) { l.setBackground(new Color(61,26,26)); l.setForeground(Tema.RED); }
                    else if (bat <= 50) { l.setBackground(new Color(61,46,10)); l.setForeground(Tema.AMBER); }
                    else { l.setBackground(new Color(26,61,28)); l.setForeground(Tema.GREEN3); }
                } catch (Exception ignored) {}
                return l;
            }
        });

        // Colorir STATUS
        tabela.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                l.setOpaque(true);
                if ("Disponível".equals(v)) { l.setBackground(new Color(26,61,28)); l.setForeground(Tema.GREEN3); }
                else { l.setBackground(new Color(20,40,60)); l.setForeground(Tema.CYAN); }
                return l;
            }
        });

        card.add(Tema.criarScroll(tabela), BorderLayout.CENTER);

        // Ações
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acoes.setBackground(Tema.CARD);
        JButton btnVincular = Tema.criarBotaoPrimario("VINCULAR A ANIMAL");
        btnVincular.addActionListener(e -> vincularColeira(tabela));
        JButton btnLiberar = Tema.criarBotaoSecundario("LIBERAR COLEIRA");
        btnLiberar.addActionListener(e -> liberarColeira(tabela));
        acoes.add(btnVincular);
        acoes.add(btnLiberar);
        card.add(acoes, BorderLayout.SOUTH);

        content.add(card, BorderLayout.CENTER);
        return content;
    }

    private void carregarTabela() {
        tabelaModel.setRowCount(0);
        for (Colar c : backend.colarService.listarTodos()) {
            // Buscar animal vinculado
            String animalNome = "—";
            for (Animal a : backend.animalService.listarTodos()) {
                if (a.getColar() != null && a.getColar().getId().equals(c.getId())) {
                    animalNome = a.getNome();
                    break;
                }
            }
            tabelaModel.addRow(new Object[]{
                c.getId(), c.getBateria() + "%", c.getNivelSinal(),
                c.getFrequenciaMinutos(), c.getFirmware(),
                c.isDisponivel() ? "Disponível" : "Em uso",
                animalNome
            });
        }
    }

    private void adicionarColeira() {
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBackground(Tema.BG);
        JTextField campoId = Tema.criarCampo("Ex: C-20");
        JTextField campoBat = Tema.criarCampo("100");
        JComboBox<String> comboSinal = Tema.criarCombo("Forte","Medio","Fraco");
        JTextField campoFreq = Tema.criarCampo("5");
        form.add(Tema.criarLabel("ID DA COLEIRA:", Tema.F_SMALL, Tema.TEXT3)); form.add(campoId);
        form.add(Tema.criarLabel("BATERIA (%):", Tema.F_SMALL, Tema.TEXT3)); form.add(campoBat);
        form.add(Tema.criarLabel("NÍVEL SINAL:", Tema.F_SMALL, Tema.TEXT3)); form.add(comboSinal);
        form.add(Tema.criarLabel("FREQUÊNCIA (min):", Tema.F_SMALL, Tema.TEXT3)); form.add(campoFreq);

        int result = JOptionPane.showConfirmDialog(this, form, "Nova Coleira GPS", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String id = campoId.getText().trim();
            if (id.isEmpty()) return;
            try {
                int bat = Integer.parseInt(campoBat.getText().trim());
                int freq = Integer.parseInt(campoFreq.getText().trim());
                Colar nova = new Colar(id, bat, comboSinal.getSelectedItem().toString(), freq);
                // Salvar via service
                try (org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession()) {
                    session.beginTransaction();
                    session.persist(nova);
                    session.getTransaction().commit();
                }
                carregarTabela();
                JOptionPane.showMessageDialog(this, "Coleira " + id + " cadastrada!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void vincularColeira(JTable tabela) {
        int row = tabela.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione uma coleira!", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
        String status = tabelaModel.getValueAt(row, 5).toString();
        if (!"Disponível".equals(status)) { JOptionPane.showMessageDialog(this, "Esta coleira já está em uso!", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
        String colarId = tabelaModel.getValueAt(row, 0).toString();

        List<Animal> animais = backend.animalService.listarAtivos();
        String[] nomes = animais.stream().map(a -> a.getNome() + " #" + a.getNumeroBrinco()).toArray(String[]::new);
        if (nomes.length == 0) { JOptionPane.showMessageDialog(this, "Nenhum animal ativo!", "Aviso", JOptionPane.WARNING_MESSAGE); return; }

        String escolhido = (String) JOptionPane.showInputDialog(this, "Selecione o animal:", "Vincular Coleira",
                JOptionPane.QUESTION_MESSAGE, null, nomes, nomes[0]);
        if (escolhido != null) {
            int idx = java.util.Arrays.asList(nomes).indexOf(escolhido);
            Animal animal = animais.get(idx);
            backend.colarService.vincularAoAnimal(colarId, animal);
            backend.animalService.atualizar(animal);
            carregarTabela();
            JOptionPane.showMessageDialog(this, "Coleira " + colarId + " vinculada a " + animal.getNome() + "!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void liberarColeira(JTable tabela) {
        int row = tabela.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione uma coleira!", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
        String colarId = tabelaModel.getValueAt(row, 0).toString();
        backend.colarService.liberarColar(colarId);
        carregarTabela();
        JOptionPane.showMessageDialog(this, "Coleira " + colarId + " liberada!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }
}

// ─────────────────────────────────────────────────────────────
// TELA FAZENDA
// ─────────────────────────────────────────────────────────────
class CadastroFazendaScreen extends JPanel {
    private Backend backend;
    private MainFrame mainFrame;
    private JTextField campoNome, campoProprietario, campoMunicipio;
    private JTextField campoAreaTotal, campoAreaMon, campoLat, campoLon, campoRaio, campoTol;
    private JComboBox<String> comboEstado, comboTipoArea;
    private DefaultTableModel tabelaLotes;

    public CadastroFazendaScreen(MainFrame frame, Backend backend) {
        this.mainFrame = frame;
        this.backend = backend;
        setBackground(Tema.BG);
        setLayout(new BorderLayout());
        add(new NavBar(frame, 3), BorderLayout.NORTH);
        add(criarConteudo(), BorderLayout.CENTER);
    }

    private JPanel criarConteudo() {
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBackground(Tema.BG);
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Tema.BG);
        header.add(Tema.criarLabel("FAZENDA & GEOFENCE", Tema.F_TITLE, Tema.GREENL), BorderLayout.WEST);
        content.add(header, BorderLayout.NORTH);

        JPanel grade = new JPanel(new GridLayout(1, 2, 12, 0));
        grade.setBackground(Tema.BG);
        grade.add(criarFormFazenda());
        grade.add(criarPainelLotes());
        content.add(grade, BorderLayout.CENTER);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acoes.setBackground(Tema.BG);
        JButton btnSalvar = Tema.criarBotaoPrimario("✔ SALVAR FAZENDA");
        btnSalvar.addActionListener(e -> salvarFazenda());
        acoes.add(btnSalvar);
        acoes.add(Tema.criarBotaoSecundario("CANCELAR"));
        content.add(acoes, BorderLayout.SOUTH);

        preencherComDadosExistentes();
        return content;
    }

    private JPanel criarFormFazenda() {
        JPanel card = Tema.criarCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(Tema.criarLabel("◈ DADOS DA FAZENDA", Tema.F_LABEL, Tema.TEXT3));
        card.add(Box.createVerticalStrut(12));

        campoNome = Tema.criarCampo(""); campoProprietario = Tema.criarCampo("");
        campoMunicipio = Tema.criarCampo(""); comboEstado = Tema.criarCombo("SP","MG","GO","MT","MS","PR","BA","RS");
        campoAreaTotal = Tema.criarCampo(""); campoAreaMon = Tema.criarCampo("");
        campoLat = Tema.criarCampo(""); campoLon = Tema.criarCampo("");
        campoRaio = Tema.criarCampo("2000"); campoTol = Tema.criarCampo("50");
        comboTipoArea = Tema.criarCombo("Circular","Polígono","Retangular");

        card.add(criarPar("NOME DA FAZENDA", campoNome, "PROPRIETÁRIO", campoProprietario));
        card.add(Box.createVerticalStrut(8));
        card.add(criarPar("MUNICÍPIO", campoMunicipio, "ESTADO", comboEstado));
        card.add(Box.createVerticalStrut(8));
        card.add(criarPar("ÁREA TOTAL (HA)", campoAreaTotal, "ÁREA MONITORADA (HA)", campoAreaMon));
        card.add(Box.createVerticalStrut(14));
        card.add(Tema.criarLabel("◈ CONFIGURAÇÃO GEOFENCE", Tema.F_LABEL, Tema.TEXT3));
        card.add(Box.createVerticalStrut(8));
        card.add(criarPar("LATITUDE CENTRO", campoLat, "LONGITUDE CENTRO", campoLon));
        card.add(Box.createVerticalStrut(8));
        card.add(criarPar("RAIO (METROS)", campoRaio, "TOLERÂNCIA (M)", campoTol));
        card.add(Box.createVerticalStrut(8));
        JPanel tipoP = new JPanel(new BorderLayout(0,4)); tipoP.setBackground(Tema.CARD);
        tipoP.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        tipoP.add(Tema.criarLabel("TIPO DE ÁREA", Tema.F_SMALL, Tema.TEXT3), BorderLayout.NORTH);
        tipoP.add(comboTipoArea, BorderLayout.CENTER);
        card.add(tipoP);
        return card;
    }

    private JPanel criarPainelLotes() {
        JPanel card = Tema.criarCard();
        card.setLayout(new BorderLayout(0, 8));
        card.add(Tema.criarLabel("◈ LOTES DA FAZENDA", Tema.F_LABEL, Tema.TEXT3), BorderLayout.NORTH);

        String[] cols = {"LOTE","ÁREA (HA)","ANIMAIS","STATUS"};
        tabelaLotes = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabela = Tema.criarTabela(tabelaLotes);
        JScrollPane scroll = Tema.criarScroll(tabela);
        scroll.setPreferredSize(new Dimension(0, 130));
        card.add(scroll, BorderLayout.CENTER);

        JPanel addLote = new JPanel(new BorderLayout(0, 6));
        addLote.setBackground(Tema.CARD);
        addLote.add(Tema.criarLabel("ADICIONAR LOTE", Tema.F_SMALL, Tema.TEXT3), BorderLayout.NORTH);
        JPanel campos = new JPanel(new GridLayout(1, 2, 8, 0));
        campos.setBackground(Tema.CARD);
        JTextField campoNomeLote = Tema.criarCampo("Nome do lote");
        JTextField campoAreaLote = Tema.criarCampo("Área (ha)");
        campos.add(campoNomeLote); campos.add(campoAreaLote);
        addLote.add(campos, BorderLayout.CENTER);
        JButton btnAdd = Tema.criarBotaoPrimario("+ ADICIONAR");
        btnAdd.addActionListener(e -> {
            String n = campoNomeLote.getText().trim(), a = campoAreaLote.getText().trim();
            if (!n.isEmpty() && !a.isEmpty()) {
                tabelaLotes.addRow(new Object[]{n, a, "0", "Ativo"});
                campoNomeLote.setText(""); campoAreaLote.setText("");
            }
        });
        addLote.add(btnAdd, BorderLayout.SOUTH);
        card.add(addLote, BorderLayout.SOUTH);
        return card;
    }

    private JPanel criarPar(String l1, JComponent c1, String l2, JComponent c2) {
        JPanel p = new JPanel(new GridLayout(1, 2, 8, 0));
        p.setBackground(Tema.CARD); p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        JPanel p1 = new JPanel(new BorderLayout(0,4)); p1.setBackground(Tema.CARD);
        p1.add(Tema.criarLabel(l1, Tema.F_SMALL, Tema.TEXT3), BorderLayout.NORTH); p1.add(c1, BorderLayout.CENTER);
        JPanel p2 = new JPanel(new BorderLayout(0,4)); p2.setBackground(Tema.CARD);
        p2.add(Tema.criarLabel(l2, Tema.F_SMALL, Tema.TEXT3), BorderLayout.NORTH); p2.add(c2, BorderLayout.CENTER);
        p.add(p1); p.add(p2); return p;
    }

    private void preencherComDadosExistentes() {
        Fazenda f = backend.fazendaService.getFazendaPrincipal();
        if (f == null) return;
        campoNome.setText(f.getNome());
        campoProprietario.setText(f.getProprietario());
        campoMunicipio.setText(f.getMunicipio());
        campoAreaTotal.setText(String.valueOf(f.getAreaTotal()));
        campoAreaMon.setText(String.valueOf(f.getAreaMonitorada()));
        campoLat.setText(String.valueOf(f.getLatitudeCentro()));
        campoLon.setText(String.valueOf(f.getLongitudeCentro()));
        campoRaio.setText(String.valueOf(f.getRaioMetros()));
        campoTol.setText(String.valueOf(f.getToleranciaMetros()));
        for (Lote l : f.getLotes()) {
            tabelaLotes.addRow(new Object[]{l.getNome(), l.getAreaHa(), l.getAnimais().size(), l.getStatus()});
        }
    }

    private void salvarFazenda() {
        Fazenda f = backend.fazendaService.getFazendaPrincipal();
        if (f == null) f = new Fazenda();
        f.setNome(campoNome.getText().trim());
        f.setProprietario(campoProprietario.getText().trim());
        f.setMunicipio(campoMunicipio.getText().trim());
        try { f.setAreaTotal(Double.parseDouble(campoAreaTotal.getText().trim())); } catch (Exception ignored) {}
        try { f.setAreaMonitorada(Double.parseDouble(campoAreaMon.getText().trim())); } catch (Exception ignored) {}
        try { f.setLatitudeCentro(Double.parseDouble(campoLat.getText().trim())); } catch (Exception ignored) {}
        try { f.setLongitudeCentro(Double.parseDouble(campoLon.getText().trim())); } catch (Exception ignored) {}
        try { f.setRaioMetros(Double.parseDouble(campoRaio.getText().trim())); } catch (Exception ignored) {}
        try { f.setToleranciaMetros(Double.parseDouble(campoTol.getText().trim())); } catch (Exception ignored) {}
        backend.fazendaService.atualizar(f);
        JOptionPane.showMessageDialog(this, "Fazenda salva com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }
}

// ─────────────────────────────────────────────────────────────
// TELA MONITORAMENTO / ALERTAS
// ─────────────────────────────────────────────────────────────
class MonitoramentoScreen extends JPanel {
    private Backend backend;
    private MainFrame mainFrame;
    private DefaultTableModel modelAlertas;

    public MonitoramentoScreen(MainFrame frame, Backend backend) {
        this.mainFrame = frame;
        this.backend = backend;
        setBackground(Tema.BG);
        setLayout(new BorderLayout());
        add(new NavBar(frame, 4), BorderLayout.NORTH);
        add(criarConteudo(), BorderLayout.CENTER);
    }

    private JPanel criarConteudo() {
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBackground(Tema.BG);
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Tema.BG);
        header.add(Tema.criarLabel("MONITORAMENTO E ALERTAS", Tema.F_TITLE, Tema.GREENL), BorderLayout.WEST);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setBackground(Tema.BG);
        JButton btnExportar = Tema.criarBotaoSecundario("↓ EXPORTAR RELATÓRIO");
        btnExportar.addActionListener(e -> exportarRelatorio());
        JButton btnLidos = Tema.criarBotaoPrimario("✔ RESOLVER TODOS");
        btnLidos.addActionListener(e -> resolverTodos());
        btnPanel.add(btnExportar);
        btnPanel.add(btnLidos);
        header.add(btnPanel, BorderLayout.EAST);
        content.add(header, BorderLayout.NORTH);

        // Stats reais
        List<Colar> batBaixa = backend.colarService.colaresBateriaBaixa(20);
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 10, 0));
        statsRow.setBackground(Tema.BG);
        statsRow.add(new StatCard("ALERTAS ATIVOS", String.valueOf(backend.totalAlertas()), Tema.RED, "aguardando resolução"));
        statsRow.add(new StatCard("BATERIA BAIXA",  String.valueOf(batBaixa.size()), Tema.AMBER, "colares abaixo de 20%"));
        statsRow.add(new StatCard("RASTREAMENTOS",  String.valueOf(backend.rastreamentoService.totalRegistros()), Tema.CYAN, "posições registradas"));

        JPanel grade = new JPanel(new BorderLayout(0, 12));
        grade.setBackground(Tema.BG);
        grade.add(statsRow, BorderLayout.NORTH);

        JPanel inferior = new JPanel(new GridLayout(1, 2, 12, 0));
        inferior.setBackground(Tema.BG);
        inferior.add(criarPainelAlertas());
        inferior.add(criarPainelHistorico());
        grade.add(inferior, BorderLayout.CENTER);
        content.add(grade, BorderLayout.CENTER);
        return content;
    }

    private JPanel criarPainelAlertas() {
        JPanel card = Tema.criarCard();
        card.setLayout(new BorderLayout(0, 8));
        card.add(Tema.criarLabel("◈ ALERTAS ATIVOS", Tema.F_LABEL, Tema.TEXT3), BorderLayout.NORTH);

        String[] cols = {"TIPO","ANIMAL","MENSAGEM","HORA"};
        modelAlertas = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        // Dados reais do banco
        for (Alerta a : backend.alertaService.listarAtivos()) {
            modelAlertas.addRow(new Object[]{
                a.getTipo().toString(),
                a.getAnimal() != null ? a.getAnimal().getNome() : "—",
                a.getMensagem(),
                a.getDataHoraFormatada()
            });
        }

        JTable tabela = Tema.criarTabela(modelAlertas);
        tabela.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                l.setOpaque(true);
                String val = v == null ? "" : v.toString();
                if (val.contains("FORA")) { l.setBackground(new Color(61,26,26)); l.setForeground(Tema.RED); }
                else if (val.contains("BATERIA")) { l.setBackground(new Color(61,46,10)); l.setForeground(Tema.AMBER); }
                else { l.setBackground(new Color(26,61,28)); l.setForeground(Tema.GREEN3); }
                return l;
            }
        });

        card.add(Tema.criarScroll(tabela), BorderLayout.CENTER);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acoes.setBackground(Tema.CARD);
        JButton btnResolver = Tema.criarBotaoPrimario("RESOLVER SELECIONADO");
        btnResolver.addActionListener(e -> {
            int row = tabela.getSelectedRow();
            if (row >= 0) {
                modelAlertas.removeRow(row);
                JOptionPane.showMessageDialog(this, "Alerta resolvido!", "OK", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        acoes.add(btnResolver);
        card.add(acoes, BorderLayout.SOUTH);
        return card;
    }

    private JPanel criarPainelHistorico() {
        JPanel card = Tema.criarCard();
        card.setLayout(new BorderLayout(0, 8));
        card.add(Tema.criarLabel("◈ HISTÓRICO DE POSIÇÕES", Tema.F_LABEL, Tema.TEXT3), BorderLayout.NORTH);

        // Selector de animal
        JPanel selector = new JPanel(new BorderLayout(0, 4));
        selector.setBackground(Tema.CARD);
        selector.add(Tema.criarLabel("SELECIONAR ANIMAL", Tema.F_SMALL, Tema.TEXT3), BorderLayout.NORTH);
        List<Animal> animais = backend.animalService.listarTodos();
        String[] nomes = animais.stream()
                .map(a -> a.getNome() + " #" + a.getNumeroBrinco())
                .toArray(String[]::new);
        JComboBox<String> combo = nomes.length > 0 ? Tema.criarCombo(nomes) : Tema.criarCombo("Nenhum animal");
        selector.add(combo, BorderLayout.CENTER);
        card.add(selector, BorderLayout.NORTH);

        String[] cols = {"HORÁRIO","LATITUDE","LONGITUDE","STATUS"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        // Carregar histórico do primeiro animal
        if (!animais.isEmpty()) {
            carregarHistorico(model, animais.get(0));
        }

        combo.addActionListener(e -> {
            int idx = combo.getSelectedIndex();
            if (idx >= 0 && idx < animais.size()) {
                carregarHistorico(model, animais.get(idx));
            }
        });

        JTable tabela = Tema.criarTabela(model);
        tabela.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                l.setOpaque(true);
                String val = v == null ? "" : v.toString();
                if ("Fora".equals(val)) { l.setBackground(new Color(61,26,26)); l.setForeground(Tema.RED); }
                else if ("Limite".equals(val)) { l.setBackground(new Color(61,46,10)); l.setForeground(Tema.AMBER); }
                else { l.setBackground(new Color(26,61,28)); l.setForeground(Tema.GREEN3); }
                return l;
            }
        });
        card.add(Tema.criarScroll(tabela), BorderLayout.CENTER);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acoes.setBackground(Tema.CARD);
        JButton btnRel = Tema.criarBotaoPrimario("GERAR RELATÓRIO");
        btnRel.addActionListener(e -> gerarRelatorio(combo, animais));
        acoes.add(btnRel);
        card.add(acoes, BorderLayout.SOUTH);
        return card;
    }

    private void carregarHistorico(DefaultTableModel model, Animal animal) {
        model.setRowCount(0);
        List<backend.Localizacao> hist = backend.rastreamentoService.buscarHistoricoPorAnimal(animal);
        if (hist.isEmpty()) {
            // Dados de exemplo se não houver histórico
            model.addRow(new Object[]{"—", "—", "—", "Sem dados"});
        } else {
            for (backend.Localizacao loc : hist) {
                model.addRow(new Object[]{
                    loc.getTimestampFormatado(),
                    String.format("%.4f", loc.getLatitude()),
                    String.format("%.4f", loc.getLongitude()),
                    loc.getStatus()
                });
            }
        }
    }

    private void resolverTodos() {
        backend.alertaService.resolverTodos();
        modelAlertas.setRowCount(0);
        JOptionPane.showMessageDialog(this, "Todos os alertas foram resolvidos!", "Alertas", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportarRelatorio() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RELATÓRIO SIRATECH ===\n");
        sb.append("Data: ").append(new java.util.Date()).append("\n\n");
        sb.append("--- ANIMAIS ---\n");
        for (Animal a : backend.animalService.listarTodos()) {
            sb.append(String.format("%-15s %-8s %-10s %s\n",
                    a.getNome(), a.getNumeroBrinco(), a.getRaca(), a.getStatus()));
        }
        sb.append("\n--- ALERTAS ATIVOS ---\n");
        for (Alerta al : backend.alertaService.listarAtivos()) {
            sb.append(al.getDataHoraFormatada()).append(" | ")
              .append(al.getTipo()).append(" | ")
              .append(al.getMensagem()).append("\n");
        }
        sb.append("\n--- COLEIRAS ---\n");
        for (Colar c : backend.colarService.listarTodos()) {
            sb.append(String.format("%-6s Bateria: %3d%% Sinal: %-6s %s\n",
                    c.getId(), c.getBateria(), c.getNivelSinal(),
                    c.isDisponivel() ? "Disponível" : "Em uso"));
        }

        // Salvar arquivo
        try {
            String nomeArq = "relatorio_siratech_" + System.currentTimeMillis() + ".txt";
            java.io.FileWriter fw = new java.io.FileWriter(nomeArq);
            fw.write(sb.toString());
            fw.close();
            JOptionPane.showMessageDialog(this,
                    "Relatório salvo em:\n" + new java.io.File(nomeArq).getAbsolutePath(),
                    "Relatório Exportado", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void gerarRelatorio(JComboBox<String> combo, List<Animal> animais) {
        int idx = combo.getSelectedIndex();
        if (idx < 0 || idx >= animais.size()) return;
        Animal animal = animais.get(idx);
        List<backend.Localizacao> hist = backend.rastreamentoService.buscarHistoricoPorAnimal(animal);

        StringBuilder sb = new StringBuilder();
        sb.append("=== RELATÓRIO DE RASTREAMENTO ===\n");
        sb.append("Animal: ").append(animal.getNome())
          .append(" | Brinco: ").append(animal.getNumeroBrinco()).append("\n");
        sb.append("Gerado em: ").append(new java.util.Date()).append("\n\n");
        if (hist.isEmpty()) {
            sb.append("Nenhum registro de posição encontrado.\n");
        } else {
            sb.append(String.format("%-22s %-12s %-12s %s\n", "HORÁRIO", "LATITUDE", "LONGITUDE", "STATUS"));
            for (backend.Localizacao loc : hist) {
                sb.append(String.format("%-22s %-12.4f %-12.4f %s\n",
                        loc.getTimestampFormatado(), loc.getLatitude(), loc.getLongitude(), loc.getStatus()));
            }
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setBackground(Tema.BG3);
        area.setForeground(Tema.TEXT);
        area.setFont(new Font("Courier New", Font.PLAIN, 11));
        area.setEditable(false);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(500, 300));
        JOptionPane.showMessageDialog(this, scroll, "Relatório: " + animal.getNome(), JOptionPane.PLAIN_MESSAGE);
    }
}

// ─────────────────────────────────────────────────────────────
// JANELA PRINCIPAL
// ─────────────────────────────────────────────────────────────
class MainFrame extends JFrame {
    private Backend backend;
    private CardLayout cardLayout;
    private JPanel painelPrincipal;
    public backend.HibernateUtil HibernateUtil = null; // referência para ColeiraScreen

    public MainFrame() {
        this.backend = Backend.getInstance();
        setTitle("SIRATECH — Sistema de Rastreamento Agro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setPreferredSize(new Dimension(1280, 780));

        cardLayout = new CardLayout();
        painelPrincipal = new JPanel(cardLayout);
        painelPrincipal.add(new LoginScreen(this, backend),          "LOGIN");
        painelPrincipal.add(new DashboardScreen(this, backend),      "DASHBOARD");
        painelPrincipal.add(new CadastroAnimalScreen(this, backend), "ANIMAIS");
        painelPrincipal.add(new ColeiraScreen(this, backend),        "COLEIRAS");
        painelPrincipal.add(new CadastroFazendaScreen(this, backend),"FAZENDA");
        painelPrincipal.add(new MonitoramentoScreen(this, backend),  "ALERTAS");

        setContentPane(painelPrincipal);
        cardLayout.show(painelPrincipal, "LOGIN");
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /** 0=Dashboard, 1=Animais, 2=Coleiras, 3=Fazenda, 4=Alertas */
    public void navegarPara(int tela) {
        String[] nomes = {"DASHBOARD","ANIMAIS","COLEIRAS","FAZENDA","ALERTAS"};
        if (!backend.authService.isLogado()) {
            cardLayout.show(painelPrincipal, "LOGIN");
            return;
        }
        cardLayout.show(painelPrincipal, nomes[tela]);
    }
}

// ─────────────────────────────────────────────────────────────
// MAIN
// ─────────────────────────────────────────────────────────────
public class frontend {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) { e.printStackTrace(); }
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
