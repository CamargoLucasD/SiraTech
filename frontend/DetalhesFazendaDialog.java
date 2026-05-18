package frontend;

import backend.*;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.*;

public class DetalhesFazendaDialog extends JDialog {

    // ── helper de ícone ──────────────────────────────────────────────────────
    private static FlatSVGIcon ico(String name, int size) {
        return new FlatSVGIcon("icons/" + name + ".svg", size, size);
    }

    public DetalhesFazendaDialog(Window owner, Fazenda f, Backend backend) {
        super(owner, "Fazenda: " + f.getNome(), ModalityType.APPLICATION_MODAL);
        setSize(700, 560);
        setLocationRelativeTo(owner);
        setResizable(false);
        getContentPane().setBackground(Tema.BG);
        setLayout(new BorderLayout());

        // ── Header ───────────────────────────────────────────────────────────
        JPanel head = new JPanel(new BorderLayout());
        head.setBackground(new Color(20, 44, 22));
        head.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.GREEN, 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        // Título com ícone de database (fazenda/gestão)
        JLabel lblNome = Tema.criarLabel(f.getNome(),
                new Font("Segoe UI", Font.BOLD, 20), Tema.GREENL);
        lblNome.setIcon(ico("database", 22));
        lblNome.setIconTextGap(10);
        head.add(lblNome, BorderLayout.WEST);

        JPanel badges = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        badges.setOpaque(false);
        if (f.getMunicipio() != null && f.getEstado() != null)
            addBadge(badges, f.getMunicipio() + "/" + f.getEstado(), Tema.TEXT2, Tema.BG3);
        addBadge(badges, f.getAreaTotal() + " ha", Tema.GREEN3, new Color(20, 50, 22));
        head.add(badges, BorderLayout.EAST);
        add(head, BorderLayout.NORTH);

        // ── Abas ─────────────────────────────────────────────────────────────
        JTabbedPane abas = Tema.criarAbas();

        // ── Aba DADOS ────────────────────────────────────────────────────────
        JPanel dados = new JPanel(new GridLayout(0, 2, 8, 8));
        dados.setBackground(Tema.BG);
        dados.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        addI(dados, "PROPRIETÁRIO",    f.getProprietario());
        addI(dados, "ÁREA TOTAL",      f.getAreaTotal() + " ha");
        addI(dados, "ÁREA MONITORADA", f.getAreaMonitorada() + " ha");
        addI(dados, "GEOFENCE RAIO",   f.getRaioMetros() + " m");
        addI(dados, "TOLERÂNCIA",      f.getToleranciaMetros() + " m");
        addI(dados, "TIPO DE ÁREA",    f.getTipoArea() != null ? f.getTipoArea() : "—");
        int totalAnim = f.getLotes().stream().mapToInt(l -> l.getAnimais().size()).sum();
        addI(dados, "TOTAL ANIMAIS",   String.valueOf(totalAnim));
        addI(dados, "TOTAL LOTES",     String.valueOf(f.getLotes().size()));

        JLabel tabDados = new JLabel("DADOS");
        tabDados.setIcon(ico("clipboard-list", 14));
        tabDados.setIconTextGap(5);
        abas.addTab(null, dados);
        abas.setTabComponentAt(0, tabDados);

        // ── Aba LOTES ────────────────────────────────────────────────────────
        JPanel lotesP = new JPanel(new BorderLayout(0, 6));
        lotesP.setBackground(Tema.BG);
        lotesP.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel loteTopo = new JPanel(new BorderLayout());
        loteTopo.setBackground(Tema.BG);
        JLabel lblLotes = Tema.criarLabel("Lotes desta fazenda", Tema.F_BODY, Tema.TEXT2);
        lblLotes.setIcon(ico("filter", 14));
        lblLotes.setIconTextGap(5);
        loteTopo.add(lblLotes, BorderLayout.WEST);

        JButton btnNL = new JButton("Novo Lote");
        btnNL.setIcon(ico("plus-circle", 12));
        btnNL.setIconTextGap(4);
        btnNL.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        Tema.semFoco(btnNL);
        btnNL.setBackground(new Color(26, 50, 28));
        btnNL.setForeground(Tema.GREEN3);
        btnNL.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        btnNL.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loteTopo.add(btnNL, BorderLayout.EAST);
        lotesP.add(loteTopo, BorderLayout.NORTH);

        String[] lc = {"LOTE", "ÁREA (HA)", "ANIMAIS", "STATUS"};
        DefaultTableModel lm = new DefaultTableModel(lc, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Lote l : f.getLotes())
            lm.addRow(new Object[]{l.getNome(), l.getAreaHa(), l.getAnimais().size(), l.getStatus()});
        lotesP.add(Tema.criarScroll(Tema.criarTabela(lm)), BorderLayout.CENTER);

        // Listener do botão Novo Lote — lógica original preservada
        btnNL.addActionListener(e -> {
            JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
            form.setBackground(Tema.BG);
            JTextField cN = Tema.criarCampo(""), cA = Tema.criarCampo("");
            form.add(Tema.criarLabel("NOME:",      Tema.F_SMALL, Tema.TEXT3)); form.add(cN);
            form.add(Tema.criarLabel("ÁREA (HA):", Tema.F_SMALL, Tema.TEXT3)); form.add(cA);
            if (JOptionPane.showConfirmDialog(this, form, "Novo Lote",
                    JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    double area = Double.parseDouble(cA.getText().trim());
                    Lote novoLote = new Lote(0, cN.getText().trim(), area);
                    try (org.hibernate.Session s = HibernateUtil.getSessionFactory().openSession()) {
                        s.beginTransaction();
                        s.persist(novoLote);
                        f.addLote(novoLote);
                        s.merge(f);
                        s.getTransaction().commit();
                    }
                    lm.addRow(new Object[]{novoLote.getNome(), novoLote.getAreaHa(), 0, "Ativo"});
                    LogAtividades.registrar(backend.authService.getUsuarioLogado(),
                            "Adicionou lote " + novoLote.getNome() + " à fazenda " + f.getNome());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JLabel tabLotes = new JLabel("LOTES");
        tabLotes.setIcon(ico("filter", 14));
        tabLotes.setIconTextGap(5);
        abas.addTab(null, lotesP);
        abas.setTabComponentAt(1, tabLotes);

        // ── Aba RELATÓRIO ────────────────────────────────────────────────────
        JPanel relP = new JPanel(new BorderLayout(0, 6));
        relP.setBackground(Tema.BG);
        relP.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Conteúdo do relatório — lógica original preservada
        StringBuilder sb = new StringBuilder();
        sb.append("=== RELATÓRIO DA FAZENDA ===\n");
        sb.append("Nome:          ").append(f.getNome()).append("\n");
        sb.append("Proprietário:  ").append(f.getProprietario()).append("\n");
        sb.append("Localização:   ").append(f.getMunicipio()).append("/").append(f.getEstado()).append("\n");
        sb.append("Área Total:    ").append(f.getAreaTotal()).append(" ha\n");
        sb.append("Área Monitor.: ").append(f.getAreaMonitorada()).append(" ha\n");
        sb.append("Geofence Raio: ").append(f.getRaioMetros()).append(" m\n\n");
        sb.append("--- LOTES ---\n");
        for (Lote l : f.getLotes())
            sb.append(String.format("  %-14s %6.1f ha  %3d animais  %s\n",
                    l.getNome(), l.getAreaHa(), l.getAnimais().size(), l.getStatus()));
        sb.append("\nTotal de animais: ").append(totalAnim).append("\n");
        sb.append("Gerado em: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date())).append("\n");

        JTextArea area = new JTextArea(sb.toString());
        area.setBackground(Tema.BG3);
        area.setForeground(Tema.TEXT);
        area.setFont(new Font("Courier New", Font.PLAIN, 11));
        area.setEditable(false);
        relP.add(new JScrollPane(area), BorderLayout.CENTER);

        // Botão exportar com ícone
        JButton btnExp = Tema.criarBotaoSecundario("EXPORTAR");
        btnExp.setIcon(ico("download", 16));
        btnExp.setIconTextGap(6);
        btnExp.addActionListener(e -> {
            try {
                String n = "fazenda_" + f.getNome().replace(" ", "_")
                        + "_" + System.currentTimeMillis() + ".txt";
                FileWriter fw = new FileWriter(n);
                fw.write(sb.toString());
                fw.close();
                JOptionPane.showMessageDialog(this,
                        "Salvo: " + new File(n).getAbsolutePath(), "OK", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        JPanel be = new JPanel(new FlowLayout(FlowLayout.LEFT));
        be.setBackground(Tema.BG);
        be.add(btnExp);
        relP.add(be, BorderLayout.SOUTH);

        JLabel tabRel = new JLabel("RELATÓRIO");
        tabRel.setIcon(ico("clipboard-list", 14));
        tabRel.setIconTextGap(5);
        abas.addTab(null, relP);
        abas.setTabComponentAt(2, tabRel);

        add(abas, BorderLayout.CENTER);

        // ── Rodapé ───────────────────────────────────────────────────────────
        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
        south.setBackground(Tema.BG);
        JButton btn = Tema.criarBotaoPrimario("FECHAR");
        btn.setIcon(ico("x", 16));
        btn.setIconTextGap(6);
        btn.addActionListener(e -> dispose());
        south.add(btn);
        add(south, BorderLayout.SOUTH);

        setVisible(true);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private void addI(JPanel p, String l, String v) {
        JPanel item = new JPanel(new BorderLayout(0, 2));
        item.setBackground(Tema.CARD);
        item.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        item.add(Tema.criarLabel(l, Tema.F_SMALL, Tema.TEXT3), BorderLayout.NORTH);
        item.add(Tema.criarLabel(v != null ? v : "—", Tema.F_BODY, Tema.TEXT), BorderLayout.CENTER);
        p.add(item);
    }

    private void addBadge(JPanel p, String t, Color fg, Color bg) {
        JLabel l = new JLabel(t);
        l.setFont(Tema.F_SMALL);
        l.setForeground(fg);
        l.setBackground(bg);
        l.setOpaque(true);
        l.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(fg, 1),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)));
        p.add(l);
    }
}
