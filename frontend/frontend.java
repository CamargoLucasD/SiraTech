package siratech.frontend;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

// ============================================================
//  AGROTECH - FRONTEND COMPLETO (Java Swing)
//  Telas: Login, Dashboard, Cadastro Animal,
//         Cadastro Fazenda, Monitoramento/Alertas
// ============================================================
class Tema {
    static final Color BG          = new Color(13, 26, 15);
    static final Color BG2         = new Color(17, 31, 19);
    static final Color BG3         = new Color(22, 32, 24);
    static final Color CARD        = new Color(26, 43, 28);
    static final Color GREEN       = new Color(45, 106, 48);
    static final Color GREEN2      = new Color(58, 138, 62);
    static final Color GREEN3      = new Color(76, 175, 82);
    static final Color GREENL      = new Color(125, 214, 138);
    static final Color TEXT        = new Color(232, 245, 233);
    static final Color TEXT2       = new Color(165, 200, 168);
    static final Color TEXT3       = new Color(106, 155, 110);
    static final Color RED         = new Color(229, 57, 53);
    static final Color AMBER       = new Color(249, 168, 37);
    static final Color BORDER      = new Color(36, 61, 38);

    static final Font FONT_TITLE  = new Font("Courier New", Font.BOLD, 15);
    static final Font FONT_LABEL  = new Font("Courier New", Font.BOLD, 11);
    static final Font FONT_BODY   = new Font("Courier New", Font.PLAIN, 12);
    static final Font FONT_BIG    = new Font("Courier New", Font.BOLD, 26);
    static final Font FONT_SMALL  = new Font("Courier New", Font.PLAIN, 10);

    static JTextField criarCampo(String placeholder) {
        JTextField f = new JTextField(placeholder);
        f.setBackground(BG3);
        f.setForeground(TEXT);
        f.setCaretColor(GREENL);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        f.setFont(FONT_BODY);
        return f;
    }

    static JPasswordField criarSenha() {
        JPasswordField f = new JPasswordField();
        f.setBackground(BG3);
        f.setForeground(TEXT);
        f.setCaretColor(GREENL);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        f.setFont(FONT_BODY);
        return f;
    }

    static JComboBox<String> criarCombo(String... opcoes) {
        JComboBox<String> c = new JComboBox<>(opcoes);
        c.setBackground(BG3);
        c.setForeground(TEXT);
        c.setFont(FONT_BODY);
        c.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        ((JLabel) c.getRenderer()).setBackground(BG3);
        return c;
    }

    static JButton criarBotaoPrimario(String texto) {
        JButton b = new JButton(texto);
        b.setBackground(GREEN);
        b.setForeground(Color.WHITE);
        b.setFont(FONT_LABEL);
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
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
        b.setFont(FONT_LABEL);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(7, 16, 7, 16)));
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
}

// ─────────────────────────────────────────────────────────────
// COMPONENTE: NavBar reutilizável
// ─────────────────────────────────────────────────────────────
class NavBar extends JPanel {
    private String[] abas = {"DASHBOARD", "ANIMAIS", "FAZENDA", "ALERTAS"};
    private JButton[] botoes = new JButton[4];
    private int abaAtiva;
    private MainFrame mainFrame;

    public NavBar(MainFrame frame, int abaAtiva) {
        this.mainFrame = frame;
        this.abaAtiva = abaAtiva;
        setBackground(Tema.BG2);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.BORDER));
        setLayout(new FlowLayout(FlowLayout.LEFT, 4, 8));
        setPreferredSize(new Dimension(0, 48));

        JLabel brand = Tema.criarLabel("▶ AGROTECH", Tema.FONT_TITLE, Tema.GREENL);
        brand.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 20));
        add(brand);

        for (int i = 0; i < abas.length; i++) {
            final int idx = i;
            botoes[i] = new JButton(abas[i]);
            botoes[i].setFont(Tema.FONT_SMALL);
            botoes[i].setFocusPainted(false);
            botoes[i].setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            botoes[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            if (i == abaAtiva) {
                botoes[i].setBackground(Tema.GREEN);
                botoes[i].setForeground(Color.WHITE);
            } else {
                botoes[i].setBackground(Tema.BG2);
                botoes[i].setForeground(Tema.TEXT2);
            }
            botoes[i].addActionListener(e -> mainFrame.navegarPara(idx));
            add(botoes[i]);
        }

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setBackground(Tema.BG2);
        right.setOpaque(false);
        JLabel status = Tema.criarLabel("● ADMIN | FAZENDA BOI VERDE", Tema.FONT_SMALL, Tema.TEXT3);
        right.add(status);
        add(right);
    }
}

// ─────────────────────────────────────────────────────────────
// COMPONENTE: StatCard (card de métrica)
// ─────────────────────────────────────────────────────────────
class StatCard extends JPanel {
    public StatCard(String label, String valor, Color corValor, String sub) {
        setBackground(Tema.CARD);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.BORDER, 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(Tema.criarLabel(label, Tema.FONT_SMALL, Tema.TEXT3));
        add(Box.createVerticalStrut(6));
        JLabel val = Tema.criarLabel(valor, Tema.FONT_BIG, corValor);
        add(val);
        add(Box.createVerticalStrut(2));
        add(Tema.criarLabel(sub, Tema.FONT_SMALL, Tema.TEXT3));
    }
}

// ─────────────────────────────────────────────────────────────
// COMPONENTE: MapaPanel — desenha o geofence e animais
// ─────────────────────────────────────────────────────────────
class MapaPanel extends JPanel {
    private double[][] animaisDentro = {
        {0.30, 0.35}, {0.50, 0.50}, {0.65, 0.30}, {0.40, 0.65}, {0.55, 0.70}
    };
    private double[][] animaisForaDaArea = {
        {0.88, 0.20}, {0.05, 0.80}
    };
    private String[] nomesForaDaArea = {"Flor #A18", "Nuvem #A02"};

    public MapaPanel() {
        setBackground(Tema.BG3);
        setPreferredSize(new Dimension(400, 240));
        setBorder(BorderFactory.createLineBorder(Tema.BORDER, 1));

        Timer timer = new Timer(1200, e -> repaint());
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();

        // Grade
        g2.setColor(new Color(58, 138, 62, 40));
        g2.setStroke(new BasicStroke(0.5f));
        for (int x = 0; x < w; x += 30) g2.drawLine(x, 0, x, h);
        for (int y = 0; y < h; y += 30) g2.drawLine(0, y, w, y);

        // Geofence
        g2.setColor(new Color(58, 138, 62, 100));
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10, new float[]{8, 6}, 0));
        g2.drawRoundRect(18, 14, w - 36, h - 28, 14, 14);

        // Animais dentro — verde
        for (double[] pos : animaisDentro) {
            int ax = (int) (pos[0] * w);
            int ay = (int) (pos[1] * h);
            g2.setColor(Tema.GREEN3);
            g2.fillOval(ax - 5, ay - 5, 10, 10);
            g2.setColor(Tema.GREENL);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(ax - 5, ay - 5, 10, 10);
        }

        // Animais fora — vermelho pulsante
        long t = System.currentTimeMillis();
        float alpha = 0.4f + 0.6f * Math.abs((float) Math.sin(t / 500.0));
        for (int i = 0; i < animaisForaDaArea.length; i++) {
            int ax = (int) (animaisForaDaArea[i][0] * w);
            int ay = (int) (animaisForaDaArea[i][1] * h);
            g2.setColor(new Color(229, 57, 53, (int) (alpha * 255)));
            g2.fillOval(ax - 5, ay - 5, 10, 10);
            g2.setColor(new Color(255, 107, 107, (int) (alpha * 255)));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(ax - 5, ay - 5, 10, 10);
            // Label
            g2.setColor(Tema.RED);
            g2.setFont(Tema.FONT_SMALL);
            g2.drawString(nomesForaDaArea[i], ax + 8, ay + 4);
        }
    }
}

// ─────────────────────────────────────────────────────────────
// TELA 1: LoginScreen
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
        add(criarBoxLogin());
    }

    private JPanel criarBoxLogin() {
        JPanel box = new JPanel();
        box.setBackground(Tema.CARD);
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.BORDER, 1),
                BorderFactory.createEmptyBorder(40, 40, 40, 40)));
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setPreferredSize(new Dimension(360, 400));

        // Logo
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(Tema.CARD);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        JLabel iconLabel = criarIconeLogo();
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.add(iconLabel);
        logoPanel.add(Box.createVerticalStrut(10));
        JLabel titulo = Tema.criarLabel("AGROTECH", Tema.FONT_TITLE, Tema.GREENL);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.add(titulo);
        JLabel sub = Tema.criarLabel("SISTEMA DE RASTREAMENTO BOVINO", Tema.FONT_SMALL, Tema.TEXT3);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.add(sub);
        box.add(logoPanel);
        box.add(Box.createVerticalStrut(28));

        // Campo usuário
        box.add(Tema.criarLabel("USUÁRIO", Tema.FONT_SMALL, Tema.TEXT3));
        box.add(Box.createVerticalStrut(4));
        campoUsuario = Tema.criarCampo("admin");
        box.add(campoUsuario);
        box.add(Box.createVerticalStrut(14));

        // Campo senha
        box.add(Tema.criarLabel("SENHA", Tema.FONT_SMALL, Tema.TEXT3));
        box.add(Box.createVerticalStrut(4));
        campoSenha = Tema.criarSenha();
        campoSenha.setText("12345");
        box.add(campoSenha);
        box.add(Box.createVerticalStrut(20));

        // Botão
        JButton btnLogin = Tema.criarBotaoPrimario("ENTRAR NO SISTEMA");
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnLogin.addActionListener(e -> realizarLogin());
        box.add(btnLogin);

        // Enter para login
        Action loginAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) { realizarLogin(); }
        };
        campoSenha.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "login");
        campoSenha.getActionMap().put("login", loginAction);

        return box;
    }

    private JLabel criarIconeLogo() {
        JLabel label = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Tema.GREEN);
                g2.fillRoundRect(0, 0, 52, 52, 12, 12);
                g2.setColor(Tema.GREENL);
                g2.setFont(new Font("Courier New", Font.BOLD, 24));
                g2.drawString("AT", 10, 34);
            }
        };
        label.setPreferredSize(new Dimension(52, 52));
        label.setMinimumSize(new Dimension(52, 52));
        return label;
    }

    private void realizarLogin() {
        String usuario = campoUsuario.getText().trim();
        String senha   = new String(campoSenha.getPassword()).trim();
        if (backend.login(usuario, senha)) {
            mainFrame.navegarPara(0); // vai para Dashboard
        } else {
            JOptionPane.showMessageDialog(this,
                    "Usuário ou senha inválidos!\nUse: admin / 12345",
                    "Erro de Login", JOptionPane.ERROR_MESSAGE);
        }
    }
}

// ─────────────────────────────────────────────────────────────
// TELA 2: DashboardScreen
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

        // Cards de métricas
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 10, 0));
        statsRow.setBackground(Tema.BG);
        statsRow.add(new StatCard("ANIMAIS ATIVOS",
                String.valueOf(backend.totalAnimais()), Tema.GREENL, "+2 esta semana"));
        statsRow.add(new StatCard("NA ÁREA",
                String.valueOf(backend.animaisDentroArea()), Tema.GREEN3, "dentro do geofence"));
        statsRow.add(new StatCard("ALERTAS",
                String.valueOf(backend.totalAlertas()), Tema.RED, "fora da área hoje"));
        statsRow.add(new StatCard("COLARES ATIVOS",
                String.valueOf(backend.totalColaresAtivos()), Tema.GREENL, "100% conectados"));
        content.add(statsRow, BorderLayout.NORTH);

        // Grade inferior: mapa + lista
        JPanel grade = new JPanel(new GridLayout(1, 2, 12, 0));
        grade.setBackground(Tema.BG);

        // --- Mapa ---
        JPanel cardMapa = Tema.criarCard();
        cardMapa.setLayout(new BorderLayout(0, 8));
        cardMapa.add(Tema.criarLabel("MAPA EM TEMPO REAL", Tema.FONT_SMALL, Tema.TEXT3),
                BorderLayout.NORTH);
        cardMapa.add(new MapaPanel(), BorderLayout.CENTER);
        JPanel legendas = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        legendas.setBackground(Tema.CARD);
        legendas.add(criarLegenda("● DENTRO DA ÁREA", Tema.GREEN3));
        legendas.add(criarLegenda("● FORA DA ÁREA",   Tema.RED));
        cardMapa.add(legendas, BorderLayout.SOUTH);
        grade.add(cardMapa);

        // --- Lista de animais ---
        JPanel cardLista = Tema.criarCard();
        cardLista.setLayout(new BorderLayout(0, 8));
        cardLista.add(Tema.criarLabel("ANIMAIS MONITORADOS", Tema.FONT_SMALL, Tema.TEXT3),
                BorderLayout.NORTH);

        String[] colunas = {"NOME", "BRINCO", "RAÇA", "LOTE", "STATUS"};
        Object[][] dados = {
            {"Mimosa",  "A-012", "Nelore",  "Lote A", "DENTRO"},
            {"Flor",    "A-018", "Angus",   "Lote B", "FORA!"},
            {"Estrela", "A-007", "Gir",     "Lote A", "DENTRO"},
            {"Nuvem",   "A-002", "Nelore",  "Lote A", "FORA!"},
            {"Bela",    "A-023", "Brahman", "Lote B", "DENTRO"},
            {"Rosa",    "A-031", "Senepol", "Lote C", "LOW BAT"},
        };

        DefaultTableModel model = new DefaultTableModel(dados, colunas) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabela = new JTable(model);
        tabela.setBackground(Tema.BG3);
        tabela.setForeground(Tema.TEXT2);
        tabela.setGridColor(Tema.BORDER);
        tabela.setFont(Tema.FONT_SMALL);
        tabela.setRowHeight(28);
        tabela.getTableHeader().setBackground(Tema.CARD);
        tabela.getTableHeader().setForeground(Tema.TEXT3);
        tabela.getTableHeader().setFont(Tema.FONT_SMALL);

        // Colorir coluna STATUS
        tabela.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                String val = v.toString();
                l.setFont(Tema.FONT_SMALL);
                l.setOpaque(true);
                if ("DENTRO".equals(val)) {
                    l.setBackground(new Color(26, 61, 28));
                    l.setForeground(Tema.GREEN3);
                } else if ("FORA!".equals(val)) {
                    l.setBackground(new Color(61, 26, 26));
                    l.setForeground(Tema.RED);
                } else {
                    l.setBackground(new Color(61, 46, 10));
                    l.setForeground(Tema.AMBER);
                }
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBackground(Tema.BG3);
        scroll.getViewport().setBackground(Tema.BG3);
        scroll.setBorder(BorderFactory.createLineBorder(Tema.BORDER, 1));
        cardLista.add(scroll, BorderLayout.CENTER);
        grade.add(cardLista);

        content.add(grade, BorderLayout.CENTER);
        return content;
    }

    private JLabel criarLegenda(String texto, Color cor) {
        JLabel l = Tema.criarLabel(texto, Tema.FONT_SMALL, cor);
        return l;
    }
}

// ─────────────────────────────────────────────────────────────
// TELA 3: CadastroAnimalScreen
// ─────────────────────────────────────────────────────────────
class CadastroAnimalScreen extends JPanel {
    private Backend backend;
    private MainFrame mainFrame;
    private JTextField campoNome, campoBrinco, campoPeso, campoObs;
    private JComboBox<String> comboRaca, comboSexo, comboLote, comboStatus, comboColar;
    private DefaultTableModel tabelaModel;

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

        // Cabeçalho
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Tema.BG);
        header.add(Tema.criarLabel("CADASTRO DE ANIMAIS", Tema.FONT_TITLE, Tema.GREENL),
                BorderLayout.WEST);
        JButton btnNovo = Tema.criarBotaoPrimario("+ NOVO ANIMAL");
        btnNovo.addActionListener(e -> limparFormulario());
        header.add(btnNovo, BorderLayout.EAST);
        content.add(header, BorderLayout.NORTH);

        // Grade: formulário + tabela
        JPanel grade = new JPanel(new GridLayout(1, 2, 12, 0));
        grade.setBackground(Tema.BG);
        grade.add(criarFormulario());
        grade.add(criarPainelTabela());
        content.add(grade, BorderLayout.CENTER);

        // Botões
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acoes.setBackground(Tema.BG);
        JButton btnSalvar = Tema.criarBotaoPrimario("SALVAR ANIMAL");
        btnSalvar.addActionListener(e -> salvarAnimal());
        JButton btnCancelar = Tema.criarBotaoSecundario("CANCELAR");
        btnCancelar.addActionListener(e -> limparFormulario());
        acoes.add(btnSalvar);
        acoes.add(btnCancelar);
        content.add(acoes, BorderLayout.SOUTH);

        return content;
    }

    private JPanel criarFormulario() {
        JPanel card = Tema.criarCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(Tema.criarLabel("DADOS DO ANIMAL", Tema.FONT_SMALL, Tema.TEXT3));
        card.add(Box.createVerticalStrut(10));

        card.add(criarLinha("NOME", campoNome = Tema.criarCampo(""),
                             "Nº BRINCO", campoBrinco = Tema.criarCampo("")));
        card.add(Box.createVerticalStrut(8));

        comboRaca = Tema.criarCombo("Nelore", "Angus", "Gir", "Brahman", "Senepol", "Tabapuã");
        comboSexo = Tema.criarCombo("Femea", "Macho");
        card.add(criarLinha("RAÇA", comboRaca, "SEXO", comboSexo));
        card.add(Box.createVerticalStrut(8));

        card.add(criarLinha("PESO (KG)", campoPeso = Tema.criarCampo(""),
                             "LOTE", comboLote = Tema.criarCombo("Lote A", "Lote B", "Lote C")));
        card.add(Box.createVerticalStrut(8));

        comboStatus = Tema.criarCombo("Ativo", "Vendido", "Abatido");
        comboColar  = Tema.criarCombo("C-07 | Disponível", "C-14 | Disponível");
        card.add(criarLinha("STATUS", comboStatus, "COLAR GPS", comboColar));
        card.add(Box.createVerticalStrut(8));

        card.add(Tema.criarLabel("OBSERVAÇÕES", Tema.FONT_SMALL, Tema.TEXT3));
        card.add(Box.createVerticalStrut(4));
        campoObs = Tema.criarCampo("");
        campoObs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        card.add(campoObs);

        // Status do colar
        card.add(Box.createVerticalStrut(12));
        card.add(Tema.criarLabel("STATUS DO COLAR SELECIONADO", Tema.FONT_SMALL, Tema.TEXT3));
        card.add(Box.createVerticalStrut(6));
        JPanel statusColar = new JPanel(new GridLayout(2, 2, 8, 6));
        statusColar.setBackground(Tema.BG3);
        statusColar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statusColar.add(criarMetrica("BATERIA", "87%", Tema.GREEN3));
        statusColar.add(criarMetrica("SINAL GPS", "FORTE", Tema.GREENL));
        statusColar.add(criarMetrica("ÚLTIMO PING", "há 2 min", Tema.TEXT2));
        statusColar.add(criarMetrica("FIRMWARE", "v2.4.1", Tema.TEXT2));
        card.add(statusColar);

        return card;
    }

    private JPanel criarPainelTabela() {
        JPanel card = Tema.criarCard();
        card.setLayout(new BorderLayout(0, 8));
        card.add(Tema.criarLabel("ANIMAIS CADASTRADOS", Tema.FONT_SMALL, Tema.TEXT3),
                BorderLayout.NORTH);

        String[] colunas = {"NOME", "BRINCO", "RAÇA", "COLAR", "STATUS"};
        Object[][] dados = {};
        tabelaModel = new DefaultTableModel(dados, colunas) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        // Popular com dados do backend
        for (Animal a : backend.animalService.listarTodos()) {
            tabelaModel.addRow(new Object[]{
                a.getNome(), a.getNumeroBrinco(), a.getRaca(),
                a.getColar() != null ? a.getColar().getId() : "—",
                a.getStatus()
            });
        }

        JTable tabela = new JTable(tabelaModel);
        tabela.setBackground(Tema.BG3);
        tabela.setForeground(Tema.TEXT2);
        tabela.setGridColor(Tema.BORDER);
        tabela.setFont(Tema.FONT_SMALL);
        tabela.setRowHeight(26);
        tabela.getTableHeader().setBackground(Tema.CARD);
        tabela.getTableHeader().setForeground(Tema.TEXT3);
        tabela.getTableHeader().setFont(Tema.FONT_SMALL);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.getViewport().setBackground(Tema.BG3);
        scroll.setBorder(BorderFactory.createLineBorder(Tema.BORDER, 1));
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarLinha(String l1, JComponent c1, String l2, JComponent c2) {
        JPanel p = new JPanel(new GridLayout(1, 2, 10, 0));
        p.setBackground(Tema.CARD);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        JPanel p1 = new JPanel(new BorderLayout(0, 4));
        p1.setBackground(Tema.CARD);
        p1.add(Tema.criarLabel(l1, Tema.FONT_SMALL, Tema.TEXT3), BorderLayout.NORTH);
        p1.add(c1, BorderLayout.CENTER);

        JPanel p2 = new JPanel(new BorderLayout(0, 4));
        p2.setBackground(Tema.CARD);
        p2.add(Tema.criarLabel(l2, Tema.FONT_SMALL, Tema.TEXT3), BorderLayout.NORTH);
        p2.add(c2, BorderLayout.CENTER);

        p.add(p1);
        p.add(p2);
        return p;
    }

    private JPanel criarMetrica(String label, String valor, Color cor) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Tema.BG3);
        p.add(Tema.criarLabel(label, Tema.FONT_SMALL, Tema.TEXT3), BorderLayout.NORTH);
        p.add(Tema.criarLabel(valor, new Font("Courier New", Font.BOLD, 16), cor),
                BorderLayout.CENTER);
        return p;
    }

    private void salvarAnimal() {
        String nome   = campoNome.getText().trim();
        String brinco = campoBrinco.getText().trim();
        if (nome.isEmpty() || brinco.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e brinco são obrigatórios!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Animal animal = new Animal();
        animal.setNome(nome);
        animal.setNumeroBrinco(brinco);
        animal.setRaca(comboRaca.getSelectedItem().toString());
        animal.setSexo(comboSexo.getSelectedItem().toString());
        animal.setLote(comboLote.getSelectedItem().toString());
        animal.setStatus(comboStatus.getSelectedItem().toString());
        try {
            animal.setPeso(Double.parseDouble(campoPeso.getText().trim()));
        } catch (NumberFormatException ignored) {}
        animal.setObservacoes(campoObs.getText().trim());

        Animal salvo = backend.animalService.cadastrar(animal);
        tabelaModel.addRow(new Object[]{
            salvo.getNome(), salvo.getNumeroBrinco(), salvo.getRaca(), "—", salvo.getStatus()
        });
        limparFormulario();
        JOptionPane.showMessageDialog(this, "Animal " + salvo.getNome() + " cadastrado com sucesso!",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void limparFormulario() {
        campoNome.setText("");
        campoBrinco.setText("");
        campoPeso.setText("");
        campoObs.setText("");
    }
}

// ─────────────────────────────────────────────────────────────
// TELA 4: CadastroFazendaScreen
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
        add(new NavBar(frame, 2), BorderLayout.NORTH);
        add(criarConteudo(), BorderLayout.CENTER);
    }

    private JPanel criarConteudo() {
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBackground(Tema.BG);
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Tema.BG);
        header.add(Tema.criarLabel("CADASTRO DE FAZENDA / GEOFENCE",
                Tema.FONT_TITLE, Tema.GREENL), BorderLayout.WEST);
        content.add(header, BorderLayout.NORTH);

        JPanel grade = new JPanel(new GridLayout(1, 2, 12, 0));
        grade.setBackground(Tema.BG);
        grade.add(criarFormFazenda());
        grade.add(criarPainelLotes());
        content.add(grade, BorderLayout.CENTER);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acoes.setBackground(Tema.BG);
        JButton btnSalvar = Tema.criarBotaoPrimario("SALVAR FAZENDA");
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
        card.add(Tema.criarLabel("DADOS DA FAZENDA", Tema.FONT_SMALL, Tema.TEXT3));
        card.add(Box.createVerticalStrut(10));

        campoNome         = Tema.criarCampo("");
        campoProprietario = Tema.criarCampo("");
        campoMunicipio    = Tema.criarCampo("");
        comboEstado       = Tema.criarCombo("SP","MG","GO","MT","MS","PR","BA","RS");
        campoAreaTotal    = Tema.criarCampo("");
        campoAreaMon      = Tema.criarCampo("");
        campoLat          = Tema.criarCampo("");
        campoLon          = Tema.criarCampo("");
        campoRaio         = Tema.criarCampo("2000");
        campoTol          = Tema.criarCampo("50");
        comboTipoArea     = Tema.criarCombo("Circular", "Polígono", "Retangular");

        card.add(criarPar("NOME DA FAZENDA", campoNome, "PROPRIETÁRIO", campoProprietario));
        card.add(Box.createVerticalStrut(8));
        card.add(criarPar("MUNICÍPIO", campoMunicipio, "ESTADO", comboEstado));
        card.add(Box.createVerticalStrut(8));
        card.add(criarPar("ÁREA TOTAL (HA)", campoAreaTotal, "ÁREA MONITORADA (HA)", campoAreaMon));
        card.add(Box.createVerticalStrut(14));
        card.add(Tema.criarLabel("CONFIGURAÇÃO DO GEOFENCE", Tema.FONT_SMALL, Tema.TEXT3));
        card.add(Box.createVerticalStrut(8));
        card.add(criarPar("LAT. CENTRO", campoLat, "LONG. CENTRO", campoLon));
        card.add(Box.createVerticalStrut(8));
        card.add(criarPar("RAIO (METROS)", campoRaio, "TOLERÂNCIA (M)", campoTol));
        card.add(Box.createVerticalStrut(8));

        JPanel tipoPanel = new JPanel(new BorderLayout(0, 4));
        tipoPanel.setBackground(Tema.CARD);
        tipoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        tipoPanel.add(Tema.criarLabel("TIPO DE ÁREA", Tema.FONT_SMALL, Tema.TEXT3), BorderLayout.NORTH);
        tipoPanel.add(comboTipoArea, BorderLayout.CENTER);
        card.add(tipoPanel);
        return card;
    }

    private JPanel criarPainelLotes() {
        JPanel card = Tema.criarCard();
        card.setLayout(new BorderLayout(0, 8));
        card.add(Tema.criarLabel("LOTES DA FAZENDA", Tema.FONT_SMALL, Tema.TEXT3),
                BorderLayout.NORTH);

        String[] cols = {"LOTE", "ÁREA (HA)", "ANIMAIS", "STATUS"};
        tabelaLotes = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabela = new JTable(tabelaLotes);
        tabela.setBackground(Tema.BG3);
        tabela.setForeground(Tema.TEXT2);
        tabela.setGridColor(Tema.BORDER);
        tabela.setFont(Tema.FONT_SMALL);
        tabela.setRowHeight(26);
        tabela.getTableHeader().setBackground(Tema.CARD);
        tabela.getTableHeader().setForeground(Tema.TEXT3);
        tabela.getTableHeader().setFont(Tema.FONT_SMALL);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.getViewport().setBackground(Tema.BG3);
        scroll.setBorder(BorderFactory.createLineBorder(Tema.BORDER, 1));
        scroll.setPreferredSize(new Dimension(0, 120));
        card.add(scroll, BorderLayout.CENTER);

        // Adicionar lote
        JPanel addLote = new JPanel(new BorderLayout(0, 6));
        addLote.setBackground(Tema.CARD);
        addLote.add(Tema.criarLabel("ADICIONAR LOTE", Tema.FONT_SMALL, Tema.TEXT3),
                BorderLayout.NORTH);
        JPanel campos = new JPanel(new GridLayout(1, 2, 8, 0));
        campos.setBackground(Tema.CARD);
        JTextField campoNomeLote = Tema.criarCampo("Nome do lote");
        JTextField campoAreaLote = Tema.criarCampo("Área (ha)");
        campos.add(campoNomeLote);
        campos.add(campoAreaLote);
        addLote.add(campos, BorderLayout.CENTER);
        JButton btnAdd = Tema.criarBotaoPrimario("+ ADICIONAR");
        btnAdd.addActionListener(e -> {
            String n = campoNomeLote.getText().trim();
            String a = campoAreaLote.getText().trim();
            if (!n.isEmpty() && !a.isEmpty()) {
                tabelaLotes.addRow(new Object[]{n, a, "0", "Ativo"});
                campoNomeLote.setText("");
                campoAreaLote.setText("");
            }
        });
        addLote.add(btnAdd, BorderLayout.SOUTH);
        card.add(addLote, BorderLayout.SOUTH);
        return card;
    }

    private JPanel criarPar(String l1, JComponent c1, String l2, JComponent c2) {
        JPanel p = new JPanel(new GridLayout(1, 2, 8, 0));
        p.setBackground(Tema.CARD);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        JPanel p1 = new JPanel(new BorderLayout(0, 4));
        p1.setBackground(Tema.CARD);
        p1.add(Tema.criarLabel(l1, Tema.FONT_SMALL, Tema.TEXT3), BorderLayout.NORTH);
        p1.add(c1, BorderLayout.CENTER);
        JPanel p2 = new JPanel(new BorderLayout(0, 4));
        p2.setBackground(Tema.CARD);
        p2.add(Tema.criarLabel(l2, Tema.FONT_SMALL, Tema.TEXT3), BorderLayout.NORTH);
        p2.add(c2, BorderLayout.CENTER);
        p.add(p1);
        p.add(p2);
        return p;
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
            tabelaLotes.addRow(new Object[]{
                l.getNome(), l.getAreaHa(), l.getAnimais().size(), l.getStatus()
            });
        }
    }

    private void salvarFazenda() {
        JOptionPane.showMessageDialog(this, "Fazenda salva com sucesso!",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }
}

// ─────────────────────────────────────────────────────────────
// TELA 5: MonitoramentoScreen
// ─────────────────────────────────────────────────────────────
class MonitoramentoScreen extends JPanel {
    private Backend backend;
    private MainFrame mainFrame;

    public MonitoramentoScreen(MainFrame frame, Backend backend) {
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

        // Cabeçalho
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Tema.BG);
        header.add(Tema.criarLabel("MONITORAMENTO E ALERTAS", Tema.FONT_TITLE, Tema.GREENL),
                BorderLayout.WEST);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setBackground(Tema.BG);
        JButton btnExportar = Tema.criarBotaoSecundario("EXPORTAR RELATÓRIO");
        btnExportar.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Relatório exportado com sucesso!", "Exportar", JOptionPane.INFORMATION_MESSAGE));
        JButton btnLidos = Tema.criarBotaoPrimario("MARCAR TODOS COMO LIDOS");
        btnLidos.addActionListener(e -> {
            backend.alertaService.resolverTodos();
            JOptionPane.showMessageDialog(this, "Todos os alertas foram resolvidos!",
                    "Alertas", JOptionPane.INFORMATION_MESSAGE);
        });
        btnPanel.add(btnExportar);
        btnPanel.add(btnLidos);
        header.add(btnPanel, BorderLayout.EAST);
        content.add(header, BorderLayout.NORTH);

        // Cards de métricas
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 10, 0));
        statsRow.setBackground(Tema.BG);
        statsRow.add(new StatCard("ALERTAS HOJE", "2", Tema.RED, "animais fora da área"));
        statsRow.add(new StatCard("BATERIA BAIXA", "3", Tema.AMBER, "colares abaixo de 20%"));
        statsRow.add(new StatCard("SEM SINAL", "0", Tema.TEXT3, "colares offline"));

        JPanel grade = new JPanel(new BorderLayout(0, 12));
        grade.setBackground(Tema.BG);
        grade.add(statsRow, BorderLayout.NORTH);

        // Lista alertas + histórico
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
        card.add(Tema.criarLabel("ALERTAS ATIVOS", Tema.FONT_SMALL, Tema.TEXT3),
                BorderLayout.NORTH);

        JPanel lista = new JPanel();
        lista.setBackground(Tema.CARD);
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));

        lista.add(criarItemAlerta("FORA", "Flor #A18 — FORA DA ÁREA",
                "Colar C-09 | Angus | Lote B | Distância: 340m", "14:28"));
        lista.add(Box.createVerticalStrut(6));
        lista.add(criarItemAlerta("FORA", "Nuvem #A02 — FORA DA ÁREA",
                "Colar C-01 | Nelore | Lote A | Distância: 120m", "14:15"));
        lista.add(Box.createVerticalStrut(6));
        lista.add(criarItemAlerta("WARN", "Rosa #A31 — BATERIA BAIXA",
                "Colar C-15 | Senepol | Bateria: 14%", "13:55"));
        lista.add(Box.createVerticalStrut(6));
        lista.add(criarItemAlerta("WARN", "Dalila #A44 — BATERIA BAIXA",
                "Colar C-18 | Gir | Bateria: 18%", "12:40"));
        lista.add(Box.createVerticalStrut(6));
        lista.add(criarItemAlerta("OK", "Mimosa #A12 — RETORNOU À ÁREA",
                "Colar C-04 | Nelore | Resolvido automaticamente", "11:20"));

        JScrollPane scroll = new JScrollPane(lista);
        scroll.getViewport().setBackground(Tema.CARD);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarItemAlerta(String tipo, String titulo, String desc, String hora) {
        JPanel item = new JPanel(new BorderLayout(10, 0));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        Color borda, bg;
        String simbolo;
        Color corSimbolo;
        switch (tipo) {
            case "FORA":
                borda = Tema.RED; bg = new Color(61, 26, 26);
                simbolo = "!"; corSimbolo = Tema.RED; break;
            case "WARN":
                borda = Tema.AMBER; bg = new Color(61, 46, 10);
                simbolo = "~"; corSimbolo = Tema.AMBER; break;
            default:
                borda = Tema.GREEN3; bg = new Color(26, 61, 28);
                simbolo = "✓"; corSimbolo = Tema.GREEN3; break;
        }

        item.setBackground(Tema.BG3);
        item.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, borda),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        JLabel ico = new JLabel(simbolo);
        ico.setFont(new Font("Courier New", Font.BOLD, 16));
        ico.setForeground(corSimbolo);
        ico.setPreferredSize(new Dimension(28, 28));
        item.add(ico, BorderLayout.WEST);

        JPanel texto = new JPanel(new BorderLayout());
        texto.setBackground(Tema.BG3);
        texto.add(Tema.criarLabel(titulo, Tema.FONT_LABEL, Tema.TEXT), BorderLayout.NORTH);
        texto.add(Tema.criarLabel(desc, Tema.FONT_SMALL, Tema.TEXT3), BorderLayout.CENTER);
        item.add(texto, BorderLayout.CENTER);

        item.add(Tema.criarLabel(hora, Tema.FONT_SMALL, Tema.TEXT3), BorderLayout.EAST);
        return item;
    }

    private JPanel criarPainelHistorico() {
        JPanel card = Tema.criarCard();
        card.setLayout(new BorderLayout(0, 8));
        card.add(Tema.criarLabel("HISTÓRICO DE POSIÇÕES", Tema.FONT_SMALL, Tema.TEXT3),
                BorderLayout.NORTH);

        JPanel selector = new JPanel(new BorderLayout(0, 4));
        selector.setBackground(Tema.CARD);
        selector.add(Tema.criarLabel("SELECIONAR ANIMAL", Tema.FONT_SMALL, Tema.TEXT3),
                BorderLayout.NORTH);
        JComboBox<String> combo = Tema.criarCombo(
                "Flor #A18", "Mimosa #A12", "Estrela #A07", "Nuvem #A02");
        selector.add(combo, BorderLayout.CENTER);
        card.add(selector, BorderLayout.NORTH);

        String[] cols = {"HORÁRIO", "LATITUDE", "LONGITUDE", "STATUS"};
        Object[][] dados = {
            {"14:28:01", "-23.5701", "-47.4289", "FORA"},
            {"14:23:01", "-23.5698", "-47.4295", "FORA"},
            {"14:18:01", "-23.5694", "-47.4301", "LIMITE"},
            {"14:13:01", "-23.5689", "-47.4310", "DENTRO"},
            {"14:08:01", "-23.5685", "-47.4318", "DENTRO"},
            {"14:03:01", "-23.5680", "-47.4325", "DENTRO"},
        };
        DefaultTableModel model = new DefaultTableModel(dados, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabela = new JTable(model);
        tabela.setBackground(Tema.BG3);
        tabela.setForeground(Tema.TEXT2);
        tabela.setGridColor(Tema.BORDER);
        tabela.setFont(Tema.FONT_SMALL);
        tabela.setRowHeight(26);
        tabela.getTableHeader().setBackground(Tema.CARD);
        tabela.getTableHeader().setForeground(Tema.TEXT3);
        tabela.getTableHeader().setFont(Tema.FONT_SMALL);

        // Colorir STATUS
        tabela.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                l.setFont(Tema.FONT_SMALL);
                l.setOpaque(true);
                String val = v.toString();
                if ("FORA".equals(val)) {
                    l.setBackground(new Color(61, 26, 26)); l.setForeground(Tema.RED);
                } else if ("LIMITE".equals(val)) {
                    l.setBackground(new Color(61, 46, 10)); l.setForeground(Tema.AMBER);
                } else {
                    l.setBackground(new Color(26, 61, 28)); l.setForeground(Tema.GREEN3);
                }
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.getViewport().setBackground(Tema.BG3);
        scroll.setBorder(BorderFactory.createLineBorder(Tema.BORDER, 1));
        card.add(scroll, BorderLayout.CENTER);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acoes.setBackground(Tema.CARD);
        JButton btnRel = Tema.criarBotaoPrimario("GERAR RELATÓRIO");
        btnRel.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Relatório gerado com sucesso!", "Relatório", JOptionPane.INFORMATION_MESSAGE));
        JButton btnRes = Tema.criarBotaoSecundario("MARCAR RESOLVIDO");
        btnRes.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Alerta marcado como resolvido!", "Alertas", JOptionPane.INFORMATION_MESSAGE));
        acoes.add(btnRel);
        acoes.add(btnRes);
        card.add(acoes, BorderLayout.SOUTH);
        return card;
    }
}

// ─────────────────────────────────────────────────────────────
// JANELA PRINCIPAL: MainFrame — gerencia a navegação
// ─────────────────────────────────────────────────────────────
class MainFrame extends JFrame {
    private Backend backend;
    private CardLayout cardLayout;
    private JPanel painelPrincipal;

    public MainFrame() {
        this.backend = Backend.getInstance();

        setTitle("AgroTech — Sistema de Rastreamento Bovino");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setPreferredSize(new Dimension(1200, 750));

        cardLayout = new CardLayout();
        painelPrincipal = new JPanel(cardLayout);

        painelPrincipal.add(new LoginScreen(this, backend),             "LOGIN");
        painelPrincipal.add(new DashboardScreen(this, backend),         "DASHBOARD");
        painelPrincipal.add(new CadastroAnimalScreen(this, backend),    "ANIMAIS");
        painelPrincipal.add(new CadastroFazendaScreen(this, backend),   "FAZENDA");
        painelPrincipal.add(new MonitoramentoScreen(this, backend),     "ALERTAS");

        setContentPane(painelPrincipal);
        cardLayout.show(painelPrincipal, "LOGIN");

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /** 0=Dashboard, 1=Animais, 2=Fazenda, 3=Alertas */
    public void navegarPara(int tela) {
        String[] nomes = {"DASHBOARD", "ANIMAIS", "FAZENDA", "ALERTAS"};
        if (!backend.authService.isLogado() && tela != -1) {
            cardLayout.show(painelPrincipal, "LOGIN");
            return;
        }
        cardLayout.show(painelPrincipal, nomes[tela]);
    }
}

// ─────────────────────────────────────────────────────────────
// MAIN: Ponto de entrada da aplicação
// ─────────────────────────────────────────────────────────────
public class Frontend {
    public static void main(String[] args) {
        // Tema escuro do sistema operacional
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Inicia na thread de UI do Swing
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
