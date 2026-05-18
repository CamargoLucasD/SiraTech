package frontend;

import backend.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.*;

public class RelatoriosScreen extends JPanel {

    private final Backend   backend;
    private final MainFrame mainFrame;

    public RelatoriosScreen(MainFrame frame, Backend backend) {
        this.mainFrame = frame;
        this.backend   = backend;
        setBackground(Tema.BG);
        setLayout(new BorderLayout());
        add(new NavBar(frame, backend, 5), BorderLayout.NORTH);
        add(criarConteudo(), BorderLayout.CENTER);
    }

    public void atualizarDados() {
        removeAll();
        add(new NavBar(mainFrame, backend, 5), BorderLayout.NORTH);
        add(criarConteudo(), BorderLayout.CENTER);
        revalidate(); repaint();
    }

    private JPanel criarConteudo() {
        JTabbedPane abas = Tema.criarAbas();
        abas.addTab("📊 GERAL",       criarAbaGeral());
        abas.addTab("🐄 POR ANIMAL",  criarAbaPorAnimal());
        abas.addTab("🏠 POR FAZENDA", criarAbaPorFazenda());

        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(Tema.BG);

        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(Tema.BG);
        h.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));
        Fazenda fa = backend.getFazendaAtiva();
        String nomeFaz = fa != null ? " — " + fa.getNome() : "";
        h.add(Tema.criarLabel("RELATÓRIOS" + nomeFaz, Tema.F_TITLE, Tema.GREENL), BorderLayout.WEST);

        JPanel hr = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        hr.setBackground(Tema.BG);
        JButton btnRef = Tema.criarBotaoRefresh();
        btnRef.addActionListener(e -> atualizarDados());
        hr.add(btnRef);
        h.add(hr, BorderLayout.EAST);

        c.add(h, BorderLayout.NORTH);
        c.add(abas, BorderLayout.CENTER);
        return c;
    }

    private JPanel criarAbaGeral() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        // Cards de resumo
        JPanel stats = new JPanel(new GridLayout(2, 3, 10, 10));
        stats.setBackground(Tema.BG);
        stats.add(new StatCard("ANIMAIS",   String.valueOf(backend.totalAnimais()),                            Tema.GREENL, "total"));
        stats.add(new StatCard("ALERTAS",   String.valueOf(backend.totalAlertas()),                            Tema.RED,    "pendentes"));
        stats.add(new StatCard("COLARES",   String.valueOf(backend.colarService.listarTodos().size()),         Tema.CYAN,   "total"));
        stats.add(new StatCard("FAZENDAS",  String.valueOf(backend.fazendaService.listarTodas().size()),       Tema.PURPLE, "cadastradas"));
        stats.add(new StatCard("EM USO",    String.valueOf(backend.totalColaresAtivos()),                      Tema.CYAN,   "colares"));
        stats.add(new StatCard("BAT. BAIXA",String.valueOf(backend.colarService.colaresBateriaBaixa(20).size()),Tema.AMBER, "<20%"));
        p.add(stats, BorderLayout.NORTH);

        // Resumo por fazenda/lote com dados reais
        JPanel card = Tema.criarCard();
        card.setLayout(new BorderLayout(0, 8));
        card.add(Tema.criarLabel("◈ RESUMO POR FAZENDA / LOTE", Tema.F_LABEL, Tema.TEXT3), BorderLayout.NORTH);

        String[] cols = {"FAZENDA", "LOTE", "ANIMAIS", "ATIVOS", "VENDIDOS"};
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        for (Fazenda f : backend.fazendaService.listarTodas()) {
            List<Animal> animaisFaz = backend.animalService.listarPorFazenda(f.getId());
            Map<String, long[]> loteStat = new LinkedHashMap<>();
            for (Animal a : animaisFaz) {
                String lote = a.getLote() != null ? a.getLote() : "Sem lote";
                loteStat.computeIfAbsent(lote, k -> new long[3]);
                loteStat.get(lote)[0]++;
                if ("Ativo".equals(a.getStatus()))   loteStat.get(lote)[1]++;
                if ("Vendido".equals(a.getStatus())) loteStat.get(lote)[2]++;
            }
            if (loteStat.isEmpty()) {
                m.addRow(new Object[]{f.getNome(), "—", 0, 0, 0});
            } else {
                for (Map.Entry<String, long[]> e : loteStat.entrySet())
                    m.addRow(new Object[]{f.getNome(), e.getKey(),
                            e.getValue()[0], e.getValue()[1], e.getValue()[2]});
            }
        }

        card.add(Tema.criarScroll(Tema.criarTabela(m)), BorderLayout.CENTER);

        // Exportar relatório geral
        JButton btnExp = Tema.criarBotaoSecundario("↓ EXPORTAR RELATÓRIO GERAL");
        btnExp.addActionListener(e -> exportarGeral());
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        botoes.setBackground(Tema.CARD);
        botoes.add(btnExp);
        card.add(botoes, BorderLayout.SOUTH);
        p.add(card, BorderLayout.CENTER);
        return p;
    }

    private JPanel criarAbaPorAnimal() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        // Filtros reais
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        filtros.setBackground(Tema.CARD);
        filtros.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.BORDER, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        filtros.add(Tema.criarLabel("STATUS:", Tema.F_SMALL, Tema.TEXT3));
        JComboBox<String> fStatus = Tema.criarCombo("Todos", "Ativo", "Vendido", "Abatido");
        filtros.add(fStatus);
        filtros.add(Tema.criarLabel("LOTE:", Tema.F_SMALL, Tema.TEXT3));

        // Lotes reais extraídos do banco
        List<String> lotesUnicos = backend.animalService.listarTodos().stream()
                .map(a -> a.getLote() != null ? a.getLote() : "Sem lote")
                .distinct().sorted().collect(Collectors.toList());
        lotesUnicos.add(0, "Todos");
        JComboBox<String> fLote = Tema.criarCombo(lotesUnicos.toArray(new String[0]));
        filtros.add(fLote);

        filtros.add(Tema.criarLabel("BUSCA:", Tema.F_SMALL, Tema.TEXT3));
        JTextField fBusca = Tema.criarCampo("");
        fBusca.setPreferredSize(new Dimension(140, 28));
        filtros.add(fBusca);
        p.add(filtros, BorderLayout.NORTH);

        String[] cols = {"NOME", "BRINCO", "RAÇA", "LOTE", "PESO", "COLAR", "STATUS", "ALERTAS"};
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        Fazenda fa = backend.getFazendaAtiva();
        List<Animal> todos = fa != null
                ? backend.animalService.listarPorFazenda(fa.getId())
                : backend.animalService.listarTodos();

        Runnable carregar = () -> {
            m.setRowCount(0);
            String st = fStatus.getSelectedItem().toString();
            String lo = fLote.getSelectedItem().toString();
            String busca = fBusca.getText().trim().toLowerCase();
            for (Animal a : todos) {
                if (!"Todos".equals(st) && !st.equals(a.getStatus())) continue;
                if (!"Todos".equals(lo) && !lo.equals(a.getLote() != null ? a.getLote() : "Sem lote")) continue;
                if (!busca.isEmpty() && !a.getNome().toLowerCase().contains(busca)
                        && !a.getNumeroBrinco().toLowerCase().contains(busca)) continue;
                long alertas = backend.alertaService.listarTodos().stream()
                        .filter(al -> al.getAnimal() != null && al.getAnimal().getId() == a.getId() && !al.isResolvido())
                        .count();
                m.addRow(new Object[]{
                        a.getNome(), a.getNumeroBrinco(), a.getRaca(),
                        a.getLote() != null ? a.getLote() : "—",
                        a.getPeso() > 0 ? a.getPeso() + " kg" : "—",
                        a.getColar() != null ? a.getColar().getId() : "—",
                        a.getStatus(), alertas});
            }
        };
        carregar.run();
        fStatus.addActionListener(e -> carregar.run());
        fLote.addActionListener(e -> carregar.run());
        fBusca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { carregar.run(); }
        });

        JTable tabela = Tema.criarTabela(m);
        tabela.getColumnModel().getColumn(6).setCellRenderer(statusRenderer());
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tabela.getSelectedRow();
                    if (row < 0) return;
                    String brinco = m.getValueAt(row, 1).toString();
                    backend.animalService.buscarPorBrinco(brinco)
                            .ifPresent(a -> new DetalhesAnimalDialog(
                                    SwingUtilities.getWindowAncestor(RelatoriosScreen.this), a, backend));
                }
            }
        });
        p.add(Tema.criarScroll(tabela), BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT));
        south.setBackground(Tema.BG);
        JButton csv = Tema.criarBotaoSecundario("↓ EXPORTAR CSV");
        csv.addActionListener(e -> {
            try {
                String n = "relatorio_animais_" + System.currentTimeMillis() + ".csv";
                FileWriter fw = new FileWriter(n);
                fw.write("Nome,Brinco,Raca,Lote,Peso,Colar,Status\n");
                for (Animal a : todos)
                    fw.write(a.getNome() + "," + a.getNumeroBrinco() + "," + a.getRaca() + ","
                            + (a.getLote() != null ? a.getLote() : "") + "," + a.getPeso() + ","
                            + (a.getColar() != null ? a.getColar().getId() : "") + "," + a.getStatus() + "\n");
                fw.close();
                LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Exportou CSV de animais");
                JOptionPane.showMessageDialog(this, "CSV salvo: " + new File(n).getAbsolutePath(), "OK",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        south.add(csv);
        p.add(south, BorderLayout.SOUTH);
        return p;
    }

    private JPanel criarAbaPorFazenda() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        p.add(Tema.criarLabel("◈ RELATÓRIO POR FAZENDA", Tema.F_LABEL, Tema.TEXT3), BorderLayout.NORTH);

        String[] cols = {"FAZENDA", "MUNICÍPIO", "ESTADO", "LOTES", "ANIMAIS", "ÁREA TOTAL"};
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Fazenda f : backend.fazendaService.listarTodas())
            m.addRow(new Object[]{
                    f.getNome(), f.getMunicipio(), f.getEstado(), f.getLotes().size(),
                    f.getLotes().stream().mapToInt(l -> l.getAnimais().size()).sum(),
                    f.getAreaTotal() + " ha"});

        JTable tabela = Tema.criarTabela(m);
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tabela.getSelectedRow();
                    if (row < 0) return;
                    String nome = m.getValueAt(row, 0).toString();
                    backend.fazendaService.listarTodas().stream()
                            .filter(f -> f.getNome().equals(nome))
                            .findFirst()
                            .ifPresent(f -> new DetalhesFazendaDialog(
                                    SwingUtilities.getWindowAncestor(RelatoriosScreen.this), f, backend));
                }
            }
        });
        p.add(Tema.criarScroll(tabela), BorderLayout.CENTER);
        p.add(Tema.criarLabel("2× clique para abrir detalhes da fazenda", Tema.F_SMALL, Tema.TEXT3), BorderLayout.SOUTH);
        return p;
    }

    private DefaultTableCellRenderer statusRenderer() {
        return new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                l.setOpaque(true);
                l.setFont(Tema.F_SMALL);
                String val = v == null ? "" : v.toString();
                if ("Ativo".equals(val)) { l.setBackground(new Color(26, 61, 28)); l.setForeground(Tema.GREEN3); }
                else if ("Vendido".equals(val) || "Abatido".equals(val)) { l.setBackground(new Color(50, 40, 10)); l.setForeground(Tema.AMBER); }
                else { l.setBackground(new Color(61, 26, 26)); l.setForeground(Tema.RED); }
                return l;
            }
        };
    }

    private void exportarGeral() {
        StringBuilder sb = new StringBuilder("=== RELATÓRIO GERAL SIRATECH ===\n");
        sb.append("Gerado em: ").append(new java.util.Date()).append("\n\n");
        sb.append("--- FAZENDAS ---\n");
        for (Fazenda f : backend.fazendaService.listarTodas())
            sb.append(String.format("  %-20s %s/%s — %d lotes\n", f.getNome(), f.getMunicipio(), f.getEstado(), f.getLotes().size()));
        sb.append("\n--- ANIMAIS ---\n");
        for (Animal a : backend.animalService.listarTodos())
            sb.append(String.format("  %-12s %-8s %-10s %s\n", a.getNome(), a.getNumeroBrinco(), a.getRaca(), a.getStatus()));
        sb.append("\n--- ALERTAS ATIVOS ---\n");
        for (Alerta a : backend.alertaService.listarAtivos())
            sb.append("  ").append(a.getDataHoraFormatada()).append(" | ").append(a.getTipo()).append(" | ").append(a.getMensagem()).append("\n");
        try {
            String n = "relatorio_geral_" + System.currentTimeMillis() + ".txt";
            FileWriter fw = new FileWriter(n);
            fw.write(sb.toString());
            fw.close();
            LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Exportou relatório geral");
            JOptionPane.showMessageDialog(this, "Salvo: " + new File(n).getAbsolutePath(), "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
