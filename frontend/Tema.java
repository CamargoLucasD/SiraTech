package frontend;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.util.*;
import java.util.List;

public class Tema {

    // ── Cores ─────────────────────────────────────────────────────────────────
    public static final Color BG     = new Color(10,  20,  12);
    public static final Color BG2    = new Color(15,  28,  17);
    public static final Color BG3    = new Color(20,  35,  22);
    public static final Color CARD   = new Color(24,  40,  26);
    public static final Color GREEN  = new Color(34,  139, 34);
    public static final Color GREEN2 = new Color(46,  160, 46);
    public static final Color GREEN3 = new Color(72,  199, 72);
    public static final Color GREENL = new Color(120, 230, 120);
    public static final Color CYAN   = new Color(0,   220, 200);
    public static final Color CYAN2  = new Color(0,   240, 220);   // hover do cyan
    public static final Color TEXT   = new Color(230, 245, 232);
    public static final Color TEXT2  = new Color(160, 200, 165);
    public static final Color TEXT3  = new Color(100, 150, 105);
    public static final Color RED    = new Color(220, 50,  50);
    public static final Color AMBER  = new Color(240, 160, 30);
    public static final Color BORDER = new Color(32,  58,  35);
    public static final Color PURPLE = new Color(150, 80,  220);
    public static final Color BLUE   = new Color(60,  140, 220);

    // ── Fontes ────────────────────────────────────────────────────────────────
    public static final Font F_TITLE = new Font("Segoe UI", Font.BOLD,  15);
    public static final Font F_LABEL = new Font("Segoe UI", Font.BOLD,  11);
    public static final Font F_BODY  = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font F_BIG   = new Font("Segoe UI", Font.BOLD,  28);
    public static final Font F_SMALL = new Font("Segoe UI", Font.PLAIN, 10);
    public static final Font F_MONO  = new Font("Courier New", Font.BOLD, 13);
    public static final Font F_H2    = new Font("Segoe UI", Font.BOLD,  13);

    // ── Utilitário de foco ────────────────────────────────────────────────────
    public static void semFoco(JButton b) {
        b.setFocusPainted(false);
        b.setBorderPainted(true);
        b.setContentAreaFilled(true);
        b.setOpaque(true);
    }

    // ── Campos de texto ───────────────────────────────────────────────────────
    public static JTextField criarCampo(String ph) {
        JTextField f = new JTextField(ph);
        f.setBackground(BG3);
        f.setForeground(TEXT);
        f.setCaretColor(GREENL);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(7, 12, 7, 12)));
        f.setFont(F_BODY);
        return f;
    }

    public static JPasswordField criarSenha() {
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

    // ── ComboBox ──────────────────────────────────────────────────────────────
    public static JComboBox<String> criarCombo(String... ops) {
        JComboBox<String> c = new JComboBox<>(ops);
        c.setBackground(BG3);
        c.setForeground(TEXT);
        c.setFont(F_BODY);
        c.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        return c;
    }

    // ── Botões ────────────────────────────────────────────────────────────────

    /** Verde — ações principais: salvar, cadastrar, confirmar */
    public static JButton criarBotaoPrimario(String t) {
        JButton b = new JButton(t);
        b.setBackground(GREEN);
        b.setForeground(Color.WHITE);
        b.setFont(F_LABEL);
        b.setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20));
        semFoco(b);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(GREEN2); }
            public void mouseExited(MouseEvent e)  { b.setBackground(GREEN);  }
        });
        return b;
    }

    /** Cinza escuro — ações comuns: cancelar, exportar, filtrar */
    public static JButton criarBotaoSecundario(String t) {
        JButton b = new JButton(t);
        b.setBackground(BG3);
        b.setForeground(TEXT2);
        b.setFont(F_LABEL);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)));
        semFoco(b);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // hover: borda e texto ficam mais claros
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setBackground(new Color(28, 48, 30));
                b.setForeground(GREENL);
                b.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(50, 90, 54), 1),
                        BorderFactory.createEmptyBorder(8, 18, 8, 18)));
            }
            public void mouseExited(MouseEvent e) {
                b.setBackground(BG3);
                b.setForeground(TEXT2);
                b.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER, 1),
                        BorderFactory.createEmptyBorder(8, 18, 8, 18)));
            }
        });
        return b;
    }

    /** Vermelho — SOMENTE exclusão/remoção permanente */
    public static JButton criarBotaoPerigo(String t) {
        JButton b = new JButton(t);
        b.setBackground(new Color(80, 20, 20));
        b.setForeground(RED);
        b.setFont(F_LABEL);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(RED, 1),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)));
        semFoco(b);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // hover: fundo fica um pouco mais aceso
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(new Color(110, 25, 25)); }
            public void mouseExited(MouseEvent e)  { b.setBackground(new Color(80,  20, 20)); }
        });
        return b;
    }

    /** Ciano — ações de detalhe/edição secundária */
    public static JButton criarBotaoCyan(String t) {
        JButton b = new JButton(t);
        b.setBackground(new Color(0, 60, 58));
        b.setForeground(CYAN);
        b.setFont(F_LABEL);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 120, 110), 1),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)));
        semFoco(b);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // hover: fundo e texto mais claros
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setBackground(new Color(0, 80, 76));
                b.setForeground(CYAN2);
            }
            public void mouseExited(MouseEvent e) {
                b.setBackground(new Color(0, 60, 58));
                b.setForeground(CYAN);
            }
        });
        return b;
    }

    /** Botão de refresh — recebe ícone SVG nas telas via setIcon() */
    public static JButton criarBotaoRefresh() {
        JButton b = new JButton();
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        b.setBackground(BG2);
        b.setForeground(TEXT3);
        b.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        semFoco(b);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setToolTipText("Atualizar dados");
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setBackground(new Color(20, 42, 22));
                b.setForeground(GREENL);
            }
            public void mouseExited(MouseEvent e) {
                b.setBackground(BG2);
                b.setForeground(TEXT3);
            }
        });
        return b;
    }

    // ── Card ──────────────────────────────────────────────────────────────────
    public static JPanel criarCard() {
        JPanel p = new JPanel();
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        return p;
    }

    // ── Separador horizontal ──────────────────────────────────────────────────
    /** Linha divisória fina para separar seções dentro de cards */
    public static JSeparator criarSeparador() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);
        sep.setBackground(CARD);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    // ── Label ─────────────────────────────────────────────────────────────────
    public static JLabel criarLabel(String t, Font f, Color c) {
        JLabel l = new JLabel(t);
        l.setFont(f);
        l.setForeground(c);
        return l;
    }

    // ── Tabela ────────────────────────────────────────────────────────────────
    public static JTable criarTabela(DefaultTableModel m) {
        JTable t = new JTable(m);
        t.setBackground(BG3);
        t.setForeground(TEXT2);
        t.setGridColor(BORDER);
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(true);
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

    // ── ScrollPane ────────────────────────────────────────────────────────────
    public static JScrollPane criarScroll(JTable tb) {
        JScrollPane s = new JScrollPane(tb);
        s.getViewport().setBackground(BG3);
        s.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        return s;
    }

    // ── TabbedPane ────────────────────────────────────────────────────────────
    public static JTabbedPane criarAbas() {
        JTabbedPane tp = new JTabbedPane();
        tp.setBackground(BG);
        tp.setForeground(TEXT2);
        tp.setFont(F_LABEL);
        tp.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        return tp;
    }

    // ── Layouts de formulário ─────────────────────────────────────────────────

    /** Dois campos lado a lado com labels acima */
    public static JPanel par(String l1, JComponent c1, String l2, JComponent c2) {
        JPanel p = new JPanel(new GridLayout(1, 2, 8, 0));
        p.setBackground(CARD);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));

        JPanel p1 = new JPanel(new BorderLayout(0, 4));
        p1.setBackground(CARD);
        p1.add(criarLabel(l1, F_SMALL, TEXT3), BorderLayout.NORTH);
        p1.add(c1, BorderLayout.CENTER);

        JPanel p2 = new JPanel(new BorderLayout(0, 4));
        p2.setBackground(CARD);
        p2.add(criarLabel(l2, F_SMALL, TEXT3), BorderLayout.NORTH);
        p2.add(c2, BorderLayout.CENTER);

        p.add(p1);
        p.add(p2);
        return p;
    }

    /** Campo único com label acima */
    public static JPanel campo(String label, JComponent c) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(CARD);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        p.add(criarLabel(label, F_SMALL, TEXT3), BorderLayout.NORTH);
        p.add(c, BorderLayout.CENTER);
        return p;
    }
}
