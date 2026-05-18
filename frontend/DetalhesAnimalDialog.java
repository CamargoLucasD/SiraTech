package frontend;

import backend.*;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

public class DetalhesAnimalDialog extends JDialog {

    // ── helper de ícone ──────────────────────────────────────────────────────
    private static FlatSVGIcon ico(String name, int size) {
        return new FlatSVGIcon("icons/" + name + ".svg", size, size);
    }

    public DetalhesAnimalDialog(Window owner, Animal animal, Backend backend) {
        super(owner, "Detalhes: " + animal.getNome(), ModalityType.APPLICATION_MODAL);
        setSize(640, 540);
        setLocationRelativeTo(owner);
        setResizable(false);
        getContentPane().setBackground(Tema.BG);
        setLayout(new BorderLayout());

        // ── Header colorido ──────────────────────────────────────────────────
        JPanel head = new JPanel(new BorderLayout());
        head.setBackground(new Color(20, 50, 22));
        head.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.GREEN, 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        // Título com ícone de animal (users = rebanho/animais)
        JLabel lblNome = Tema.criarLabel(animal.getNome(),
                new Font("Segoe UI", Font.BOLD, 20), Tema.GREENL);
        lblNome.setIcon(ico("users", 22));
        lblNome.setIconTextGap(10);
        head.add(lblNome, BorderLayout.WEST);

        JPanel badges = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        badges.setOpaque(false);

        // Badge de brinco com ícone de tag
        JLabel badgeBrinco = criarBadgeComIcone(
                "#" + animal.getNumeroBrinco(), ico("tag", 12), Tema.CYAN, new Color(0, 40, 50));
        badges.add(badgeBrinco);

        // Badge de status
        Color badgeFg = "Ativo".equals(animal.getStatus()) ? Tema.GREEN3 : Tema.AMBER;
        Color badgeBg = "Ativo".equals(animal.getStatus()) ? new Color(20, 50, 22) : new Color(50, 40, 10);
        addBadge(badges, animal.getStatus(), badgeFg, badgeBg);

        if (animal.getFazendaNome() != null && !animal.getFazendaNome().isEmpty())
            addBadge(badges, animal.getFazendaNome(), Tema.TEXT2, Tema.BG3);

        head.add(badges, BorderLayout.EAST);
        add(head, BorderLayout.NORTH);

        // ── Abas ────────────────────────────────────────────────────────────
        JTabbedPane abas = Tema.criarAbas();

        // ── Aba DADOS ────────────────────────────────────────────────────────
        JPanel dadosP = new JPanel(new GridLayout(0, 2, 8, 8));
        dadosP.setBackground(Tema.BG);
        dadosP.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        addI(dadosP, "RAÇA",        animal.getRaca());
        addI(dadosP, "SEXO",        animal.getSexo() != null ? animal.getSexo() : "—");
        addI(dadosP, "PESO",        animal.getPeso() > 0 ? animal.getPeso() + " kg" : "—");
        addI(dadosP, "LOTE",        animal.getLote() != null ? animal.getLote() : "—");
        addI(dadosP, "STATUS",      animal.getStatus());
        addI(dadosP, "FAZENDA",     animal.getFazendaNome() != null ? animal.getFazendaNome() : "—");
        if (animal.getDataNascimento() != null)
            addI(dadosP, "NASCIMENTO", animal.getDataNascimento().toLocalDate().toString());
        addI(dadosP, "OBSERVAÇÕES", animal.getObservacoes() != null && !animal.getObservacoes().isEmpty()
                ? animal.getObservacoes() : "Nenhuma");

        // Título da aba com ícone de clipboard
        JLabel tabDados = new JLabel("DADOS");
        tabDados.setIcon(ico("clipboard-list", 14));
        tabDados.setIconTextGap(5);
        abas.addTab(null, dadosP);
        abas.setTabComponentAt(0, tabDados);

        // ── Aba COLAR GPS ────────────────────────────────────────────────────
        JPanel colarP = new JPanel(new BorderLayout());
        colarP.setBackground(Tema.BG);
        colarP.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        if (animal.getColar() != null) {
            Colar c = animal.getColar();
            JPanel grid = new JPanel(new GridLayout(0, 2, 8, 8));
            grid.setBackground(Tema.BG);
            addI(grid, "ID DO COLAR",  c.getId());
            addI(grid, "BATERIA",      c.getBateria() + "%");
            addI(grid, "SINAL GPS",    c.getNivelSinal());
            addI(grid, "FREQ. PING",   c.getFrequenciaMinutos() + " min");
            addI(grid, "FIRMWARE",     c.getFirmware());

            // Barra de bateria — lógica original preservada
            JPanel bat = new JPanel() {
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Tema.BORDER); g2.fillRoundRect(0, 6, getWidth(), 18, 8, 8);
                    int pct = c.getBateria();
                    Color cor = pct > 50 ? Tema.GREEN3 : pct > 20 ? Tema.AMBER : Tema.RED;
                    g2.setColor(cor); g2.fillRoundRect(0, 6, (int)(getWidth()*(pct/100.0)), 18, 8, 8);
                    g2.setColor(Tema.TEXT); g2.setFont(Tema.F_SMALL);
                    String s = pct + "%";
                    g2.drawString(s, (getWidth() - g2.getFontMetrics().stringWidth(s)) / 2, 19);
                }
            };
            bat.setBackground(Tema.CARD);
            bat.setPreferredSize(new Dimension(0, 30));

            JPanel batWrap = new JPanel(new BorderLayout(0, 4));
            batWrap.setBackground(Tema.BG);
            batWrap.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
            batWrap.add(Tema.criarLabel("NÍVEL DE BATERIA", Tema.F_SMALL, Tema.TEXT3), BorderLayout.NORTH);
            batWrap.add(bat, BorderLayout.CENTER);
            colarP.add(grid, BorderLayout.CENTER);
            colarP.add(batWrap, BorderLayout.SOUTH);
        } else {
            JLabel semColar = Tema.criarLabel("Sem colar GPS vinculado", Tema.F_BODY, Tema.TEXT3);
            semColar.setHorizontalAlignment(SwingConstants.CENTER);
            colarP.add(semColar, BorderLayout.CENTER);
        }

        JLabel tabColar = new JLabel("COLAR GPS");
        tabColar.setIcon(ico("zap", 14));
        tabColar.setIconTextGap(5);
        abas.addTab(null, colarP);
        abas.setTabComponentAt(1, tabColar);

        // ── Aba HISTÓRICO ────────────────────────────────────────────────────
        JPanel histP = new JPanel(new BorderLayout());
        histP.setBackground(Tema.BG);
        histP.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        String[] hc = {"HORÁRIO", "LATITUDE", "LONGITUDE", "STATUS"};
        DefaultTableModel hm = new DefaultTableModel(hc, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        List<Localizacao> hist = backend.rastreamentoService.buscarHistoricoPorAnimal(animal);
        if (hist.isEmpty()) {
            hm.addRow(new Object[]{"—", "—", "—", "Sem dados"});
        } else {
            for (Localizacao l : hist)
                hm.addRow(new Object[]{
                        l.getTimestampFormatado(),
                        String.format("%.4f", l.getLatitude()),
                        String.format("%.4f", l.getLongitude()),
                        l.getStatus()});
        }
        histP.add(Tema.criarScroll(Tema.criarTabela(hm)), BorderLayout.CENTER);

        JLabel tabHist = new JLabel("HISTÓRICO");
        tabHist.setIcon(ico("trending-up", 14));
        tabHist.setIconTextGap(5);
        abas.addTab(null, histP);
        abas.setTabComponentAt(2, tabHist);

        // ── Aba VACINAS ──────────────────────────────────────────────────────
        JPanel vacP = new JPanel(new BorderLayout());
        vacP.setBackground(Tema.BG);
        vacP.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        String[] vc = {"VACINA", "APLICAÇÃO", "PRÓXIMA DOSE", "STATUS"};
        DefaultTableModel vm = new DefaultTableModel(vc, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        List<Vacina> vacinas = backend.vacinaService.listarPorAnimal(animal.getId());
        if (vacinas.isEmpty()) {
            vm.addRow(new Object[]{"—", "—", "—", "Sem registros"});
        } else {
            for (Vacina v : vacinas)
                vm.addRow(new Object[]{
                        v.getTipoVacina(), v.getDataAplicacaoStr(),
                        v.getProximaDoseStr(), v.getStatus()});
        }
        vacP.add(Tema.criarScroll(Tema.criarTabela(vm)), BorderLayout.CENTER);

        JLabel tabVac = new JLabel("VACINAS");
        tabVac.setIcon(ico("alert-triangle", 14));
        tabVac.setIconTextGap(5);
        abas.addTab(null, vacP);
        abas.setTabComponentAt(3, tabVac);

        add(abas, BorderLayout.CENTER);

        // ── Rodapé ───────────────────────────────────────────────────────────
        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
        south.setBackground(Tema.BG);
        JButton btn = Tema.criarBotaoPrimario("FECHAR");
        btn.setIcon(ico("x", 16));
        btn.setIconTextGap(6);
        btn.addActionListener(e -> dispose());
        south.add(btn);
        add(south, BorderLayout.SOUTH);

        setVisible(true);
    }

    // ── utilitários ──────────────────────────────────────────────────────────

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

    /** Badge com ícone SVG à esquerda do texto */
    private JLabel criarBadgeComIcone(String texto, FlatSVGIcon icone, Color fg, Color bg) {
        JLabel l = new JLabel(texto, icone, SwingConstants.LEFT);
        l.setFont(Tema.F_SMALL);
        l.setForeground(fg);
        l.setBackground(bg);
        l.setOpaque(true);
        l.setIconTextGap(4);
        l.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(fg, 1),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)));
        return l;
    }
}
