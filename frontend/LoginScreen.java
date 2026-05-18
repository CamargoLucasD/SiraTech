package frontend;

import backend.*;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class LoginScreen extends JPanel {

    private JTextField     campoUsuario;
    private JPasswordField campoSenha;
    private JLabel         lblErro;
    private final MainFrame mainFrame;
    private final Backend   backend;

    // Imagem de fundo carregada uma única vez
    private BufferedImage imgFundo;

    // Timeout de sessão: 30 minutos
    private static final long TIMEOUT_MS = 30 * 60 * 1000L;
    static long ultimaAtividade = System.currentTimeMillis();

    public static void registrarAtividade() { ultimaAtividade = System.currentTimeMillis(); }
    public static boolean sessaoExpirou()   { return System.currentTimeMillis() - ultimaAtividade > TIMEOUT_MS; }

    // ── helper de ícone ──────────────────────────────────────────────────────
    private static FlatSVGIcon ico(String name, int size) {
        try {
            return new FlatSVGIcon("icons/" + name + ".svg", size, size);
        } catch (Exception e) {
            return null;
        }
    }

    public LoginScreen(MainFrame frame, Backend backend) {
        this.mainFrame = frame;
        this.backend   = backend;

        // Carrega imagem de fundo uma única vez
        try {
            File f = new File("resources/imagens/fundologin.jpeg");
            if (f.exists()) imgFundo = ImageIO.read(f);
        } catch (Exception ignored) {}

        setLayout(new GridBagLayout());
        setOpaque(false);
        add(criarBox());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Tema.BG);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Imagem de fundo — usa cache carregado no construtor
        if (imgFundo != null) {
            int sz = Math.min(getWidth(), getHeight()) * 3 / 4;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.08f));
            g2.drawImage(imgFundo, (getWidth()-sz)/2, (getHeight()-sz)/2, sz, sz, null);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        // Círculos decorativos — lógica original preservada
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.07f));
        g2.setColor(Tema.GREEN);
        g2.fillOval(-120, -120, 500, 500);
        g2.setColor(new Color(0, 180, 170));
        g2.fillOval(getWidth()-250, getHeight()-250, 500, 500);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    private JPanel criarBox() {
        JPanel box = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(24, 40, 26, 185));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(Tema.BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            }
        };
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setPreferredSize(new Dimension(400, 520));
        box.setMaximumSize(new Dimension(400, 520));
        box.setBorder(BorderFactory.createEmptyBorder(36, 40, 36, 40));

        // ── Logo e título ─────────────────────────────────────────────────────
        JPanel logoP = new JPanel();
        logoP.setOpaque(false);
        logoP.setLayout(new BoxLayout(logoP, BoxLayout.Y_AXIS));

        JLabel logo = carregarLogo();
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoP.add(logo);
        logoP.add(Box.createVerticalStrut(12));

        JLabel titulo = Tema.criarLabel("SIRATECH", new Font("Segoe UI", Font.BOLD, 24), Tema.CYAN);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoP.add(titulo);

        JLabel sub = Tema.criarLabel("SISTEMA INTEGRADO DE RASTREAMENTO AGRO", Tema.F_SMALL, Tema.TEXT3);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoP.add(sub);

        box.add(logoP);
        box.add(Box.createVerticalStrut(20));

        // Separador
        JSeparator sep = Tema.criarSeparador();
        box.add(sep);
        box.add(Box.createVerticalStrut(18));

        // ── Formulário com GridBagLayout ──────────────────────────────────────
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx   = 0;
        gbc.insets  = new Insets(0, 0, 0, 0);

        // Label usuário
        JPanel lUsuarioRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        lUsuarioRow.setOpaque(false);
        JLabel icoUser = new JLabel(ico("users", 12));
        lUsuarioRow.add(icoUser);
        lUsuarioRow.add(Tema.criarLabel("USUÁRIO", Tema.F_SMALL, Tema.TEXT3));
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 4, 0);
        formPanel.add(lUsuarioRow, gbc);

        // Campo usuário
        campoUsuario = Tema.criarCampo("");
        campoUsuario.setPreferredSize(new Dimension(300, 38));
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 12, 0);
        formPanel.add(campoUsuario, gbc);

        // Label senha
        JPanel lSenhaRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        lSenhaRow.setOpaque(false);
        JLabel icoLock = new JLabel(ico("lock", 12));
        lSenhaRow.add(icoLock);
        lSenhaRow.add(Tema.criarLabel("SENHA", Tema.F_SMALL, Tema.TEXT3));
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 4, 0);
        formPanel.add(lSenhaRow, gbc);

        // Campo senha + botão olho
        JPanel senhaRow = new JPanel(new BorderLayout(0, 0));
        senhaRow.setOpaque(false);
        senhaRow.setPreferredSize(new Dimension(300, 38));
        campoSenha = Tema.criarSenha();
        senhaRow.add(campoSenha, BorderLayout.CENTER);

        // Botão olho — sem emoji, com ícone SVG de busca como toggle visual
        JButton olho = new JButton();
        olho.setIcon(ico("search", 13));
        olho.setFont(Tema.F_SMALL);
        olho.setBackground(Tema.BG3);
        olho.setForeground(Tema.TEXT3);
        olho.setBorder(BorderFactory.createLineBorder(Tema.BORDER, 1));
        Tema.semFoco(olho);
        olho.setPreferredSize(new Dimension(38, 38));
        olho.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        olho.setToolTipText("Mostrar / ocultar senha");
        final boolean[] visivel = {false};
        olho.addActionListener(e -> {
            visivel[0] = !visivel[0];
            campoSenha.setEchoChar(visivel[0] ? (char) 0 : '●');
            olho.setBackground(visivel[0] ? new Color(26, 61, 28) : Tema.BG3);
            olho.setForeground(visivel[0] ? Tema.GREEN3 : Tema.TEXT3);
        });
        senhaRow.add(olho, BorderLayout.EAST);
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 8, 0);
        formPanel.add(senhaRow, gbc);

        // Label erro — sem ícone hardcoded, texto limpo
        lblErro = Tema.criarLabel("", Tema.F_SMALL, Tema.RED);
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 10, 0);
        formPanel.add(lblErro, gbc);

        // ── Botão entrar com ícone lock ───────────────────────────────────────
        JButton btnLogin = Tema.criarBotaoPrimario("ENTRAR NO SISTEMA");
        btnLogin.setIcon(ico("lock", 14));
        btnLogin.setIconTextGap(8);
        btnLogin.setPreferredSize(new Dimension(300, 42));
        btnLogin.addActionListener(e -> realizarLogin());
        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 10, 0);
        formPanel.add(btnLogin, gbc);

        // ── Botão criar conta ─────────────────────────────────────────────────
        JButton btnConta = new JButton("Criar nova conta");
        btnConta.setFont(Tema.F_SMALL);
        btnConta.setForeground(Tema.TEXT3);
        btnConta.setContentAreaFilled(false);
        btnConta.setBorderPainted(false);
        btnConta.setHorizontalAlignment(SwingConstants.CENTER);
        Tema.semFoco(btnConta);
        btnConta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnConta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btnConta.setForeground(Tema.GREENL); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btnConta.setForeground(Tema.TEXT3);  }
        });
        btnConta.addActionListener(e -> abrirCadastro());
        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 0, 0);
        formPanel.add(btnConta, gbc);

        box.add(formPanel);

        // Enter no campo de senha também faz login — lógica original preservada
        campoSenha.addActionListener(e -> realizarLogin());
        campoUsuario.addActionListener(e -> campoSenha.requestFocus());
        return box;
    }

    // ── lógica original — não alterada ────────────────────────────────────────

    private void realizarLogin() {
        String u = campoUsuario.getText().trim();
        String s = new String(campoSenha.getPassword()).trim();
        if (u.isEmpty() || s.isEmpty()) {
            lblErro.setForeground(Tema.AMBER);
            lblErro.setText("Preencha usuário e senha!");
            return;
        }

        lblErro.setForeground(Tema.TEXT3);
        lblErro.setText("Verificando...");

        String result = backend.authService.tentarLogin(u, s);
        if ("OK".equals(result)) {
            lblErro.setText("");
            lblErro.setForeground(Tema.RED);
            ultimaAtividade = System.currentTimeMillis();
            LogAtividades.registrar(u, "Login no sistema");
            campoUsuario.setText("");
            campoSenha.setText("");
            mainFrame.navegarPara(0);
        } else if (result.startsWith("BLOQUEADO:")) {
            long seg = Long.parseLong(result.split(":")[1]);
            lblErro.setForeground(Tema.RED);
            lblErro.setText("Conta bloqueada por " + (seg / 60) + "min " + (seg % 60) + "s");
        } else if (result.startsWith("FALHA:")) {
            int rest = Integer.parseInt(result.split(":")[1]);
            lblErro.setForeground(Tema.RED);
            lblErro.setText("Senha incorreta. " + rest + " tentativa(s) restante(s)");
            campoSenha.setText("");
        }
    }

    private void abrirCadastro() {
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBackground(Tema.BG);
        JTextField cL = Tema.criarCampo(""), cN = Tema.criarCampo(""), cS = Tema.criarCampo("");
        JComboBox<String> cP = Tema.criarCombo("Operador", "Administrador");
        form.add(Tema.criarLabel("LOGIN:",         Tema.F_SMALL, Tema.TEXT3)); form.add(cL);
        form.add(Tema.criarLabel("NOME COMPLETO:", Tema.F_SMALL, Tema.TEXT3)); form.add(cN);
        form.add(Tema.criarLabel("SENHA:",         Tema.F_SMALL, Tema.TEXT3)); form.add(cS);
        form.add(Tema.criarLabel("PERFIL:",        Tema.F_SMALL, Tema.TEXT3)); form.add(cP);
        if (JOptionPane.showConfirmDialog(this, form, "Criar Conta",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String login = cL.getText().trim(), nome = cN.getText().trim(), senha = cS.getText().trim();
            if (login.isEmpty() || senha.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha login e senha!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean ok = backend.authService.cadastrarUsuario(
                    login, senha, cP.getSelectedItem().toString(),
                    nome.isEmpty() ? login : nome);
            if (ok) JOptionPane.showMessageDialog(this, "Conta criada! Faça login.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            else    JOptionPane.showMessageDialog(this, "Login já existe!", "Erro",   JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel carregarLogo() {
        try {
            File f = new File("resources/imagens/fundologin.jpeg");
            if (!f.exists()) f = new File("logo.png");
            if (f.exists()) {
                BufferedImage img = ImageIO.read(f);
                return new JLabel(new ImageIcon(img.getScaledInstance(110, 110, Image.SCALE_SMOOTH)));
            }
        } catch (Exception ignored) {}

        // Logo vetorial fallback — lógica original preservada
        JLabel l = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 180, 170, 80));
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(2, 2, 106, 106);
                g2.setColor(new Color(15, 40, 20));
                g2.fillOval(6, 6, 98, 98);
                g2.setColor(Tema.CYAN);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 32));
                FontMetrics fm = g2.getFontMetrics();
                String s = "ST";
                g2.drawString(s, (110 - fm.stringWidth(s)) / 2, 64);
                g2.setColor(Tema.GREEN3);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(6, 6, 98, 98);
            }
        };
        l.setPreferredSize(new Dimension(110, 110));
        l.setMinimumSize(new Dimension(110, 110));
        return l;
    }
}
