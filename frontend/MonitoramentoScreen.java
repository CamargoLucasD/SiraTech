package frontend;

import backend.*;
import java.awt.*;
import java.io.*;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

public class MonitoramentoScreen extends JPanel {

    private final Backend   backend;
    private final MainFrame mainFrame;
    private DefaultTableModel modelAtivos;
    private JTable            tabelaAtivos;

    public MonitoramentoScreen(MainFrame frame, Backend backend) {
        this.mainFrame = frame;
        this.backend   = backend;
        setBackground(Tema.BG);
        setLayout(new BorderLayout());
        add(new NavBar(frame, backend, 4), BorderLayout.NORTH);
        add(criarConteudo(), BorderLayout.CENTER);
    }

    public void atualizarDados() {
        carregarAlertasAtivos();
    }

    private JPanel criarConteudo() {
        JTabbedPane abas = Tema.criarAbas();
        abas.addTab("🚨 ALERTAS ATIVOS", criarAbaAtivos());
        abas.addTab("➕ CRIAR ALERTA",   criarAbaCriar());
        abas.addTab("📜 HISTÓRICO",      criarAbaHistorico());

        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(Tema.BG);

        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(Tema.BG);
        h.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));
        h.add(Tema.criarLabel("MONITORAMENTO & ALERTAS", Tema.F_TITLE, Tema.GREENL), BorderLayout.WEST);

        JPanel hr = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        hr.setBackground(Tema.BG);
        JButton btnRef = Tema.criarBotaoRefresh();
        btnRef.addActionListener(e -> carregarAlertasAtivos());
        JButton btnExp = Tema.criarBotaoSecundario("↓ EXPORTAR");
        btnExp.addActionListener(e -> exportar());
        JButton btnRes = Tema.criarBotaoPrimario("✔ RESOLVER TODOS");
        btnRes.addActionListener(e -> resolverTodos());
        hr.add(btnRef); hr.add(btnExp); hr.add(btnRes);
        h.add(hr, BorderLayout.EAST);

        c.add(h, BorderLayout.NORTH);
        c.add(abas, BorderLayout.CENTER);
        return c;
    }

    private JPanel criarAbaAtivos() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JPanel stats = new JPanel(new GridLayout(1, 3, 10, 0));
        stats.setBackground(Tema.BG);
        stats.add(new StatCard("ALERTAS ATIVOS",
                String.valueOf(backend.totalAlertas()), Tema.RED, "pendentes"));
        stats.add(new StatCard("BATERIA BAIXA",
                String.valueOf(backend.colarService.colaresBateriaBaixa(20).size()), Tema.AMBER, "<20%"));
        stats.add(new StatCard("RASTREAMENTOS",
                String.valueOf(backend.rastreamentoService.totalRegistros()), Tema.CYAN, "posições"));
        p.add(stats, BorderLayout.NORTH);

        JPanel card = Tema.criarCard();
        card.setLayout(new BorderLayout(0, 8));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Tema.CARD);
        topo.add(Tema.criarLabel("◈ ALERTAS ATIVOS", Tema.F_LABEL, Tema.RED), BorderLayout.WEST);
        topo.add(Tema.criarLabel("2× clique para detalhes do animal", Tema.F_SMALL, Tema.TEXT3), BorderLayout.EAST);
        card.add(topo, BorderLayout.NORTH);

        String[] cols = {"TIPO", "ANIMAL", "MENSAGEM", "DATA/HORA"};
        modelAtivos = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        carregarAlertasAtivos();

        tabelaAtivos = Tema.criarTabela(modelAtivos);
        tabelaAtivos.getColumnModel().getColumn(0).setCellRenderer(tipoRenderer());
        tabelaAtivos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tabelaAtivos.getSelectedRow();
                    if (row < 0) return;
                    String nomeAnimal = modelAtivos.getValueAt(row, 1).toString();
                    if (!"—".equals(nomeAnimal)) {
                        backend.animalService.listarTodos().stream()
                                .filter(a -> a.getNome().equals(nomeAnimal))
                                .findFirst()
                                .ifPresent(a -> new DetalhesAnimalDialog(
                                        SwingUtilities.getWindowAncestor(MonitoramentoScreen.this), a, backend));
                    }
                }
            }
        });
        card.add(Tema.criarScroll(tabelaAtivos), BorderLayout.CENTER);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acoes.setBackground(Tema.CARD);
        JButton btnR = Tema.criarBotaoPrimario("RESOLVER SELECIONADO");
        btnR.addActionListener(e -> {
            int row = tabelaAtivos.getSelectedRow();
            if (row < 0) return;
            List<Alerta> ativos = backend.alertaService.listarAtivos();
            if (row < ativos.size()) {
                // ✔ CORRIGIDO: resolverAlerta() em vez de resolver()
                backend.alertaService.resolverAlerta(ativos.get(row).getId());
                LogAtividades.registrar(backend.authService.getUsuarioLogado(),
                        "Resolveu alerta selecionado");
                carregarAlertasAtivos();
            }
        });
        acoes.add(btnR);
        card.add(acoes, BorderLayout.SOUTH);
        p.add(card, BorderLayout.CENTER);
        return p;
    }

    private void carregarAlertasAtivos() {
        if (modelAtivos == null) return;
        modelAtivos.setRowCount(0);
        for (Alerta a : backend.alertaService.listarAtivos())
            modelAtivos.addRow(new Object[]{
                    a.getTipo().toString(),
                    a.getAnimal() != null ? a.getAnimal().getNome() : "—",
                    a.getMensagem(),
                    a.getDataHoraFormatada()});
    }

    private JPanel criarAbaCriar() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JPanel card = Tema.criarCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(Tema.criarLabel("◈ CRIAR ALERTA MANUAL", Tema.F_LABEL, Tema.TEXT3));
        card.add(Box.createVerticalStrut(12));

        List<Animal> animais = backend.animalService.listarTodos();
        String[] nomes = animais.stream()
                .map(a -> a.getNome() + " #" + a.getNumeroBrinco()).toArray(String[]::new);
        JComboBox<String> cAnimal = nomes.length > 0 ? Tema.criarCombo(nomes) : Tema.criarCombo("Nenhum");
        JComboBox<String> cTipo = Tema.criarCombo("FORA_DA_AREA", "BATERIA_BAIXA", "SEM_SINAL", "RETORNOU");
        JTextField cMsg = Tema.criarCampo("Descreva o alerta...");
        cAnimal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cTipo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cMsg.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        card.add(Tema.campo("ANIMAL", cAnimal));
        card.add(Box.createVerticalStrut(8));
        card.add(Tema.campo("TIPO DE ALERTA", cTipo));
        card.add(Box.createVerticalStrut(8));
        card.add(Tema.campo("MENSAGEM", cMsg));
        card.add(Box.createVerticalStrut(14));

        JButton btnCriar = Tema.criarBotaoPrimario("⚠ CRIAR ALERTA");
        btnCriar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCriar.addActionListener(e -> {
            int idx = cAnimal.getSelectedIndex();
            if (animais.isEmpty() || idx < 0) return;
            Animal a = animais.get(idx);
            String ts = cTipo.getSelectedItem().toString();
            String msg = cMsg.getText().trim();
            if (msg.isEmpty() || msg.equals("Descreva o alerta..."))
                msg = ts + " - " + a.getNome();
            backend.alertaService.gerarAlerta(Alerta.Tipo.valueOf(ts), a, msg);
            LogAtividades.registrar(backend.authService.getUsuarioLogado(),
                    "Criou alerta: " + ts + " para " + a.getNome());
            carregarAlertasAtivos();
            cMsg.setText("");
            JOptionPane.showMessageDialog(this, "Alerta criado para " + a.getNome() + "!",
                    "OK", JOptionPane.INFORMATION_MESSAGE);
        });
        card.add(btnCriar);
        p.add(card, BorderLayout.NORTH);
        return p;
    }

    private JPanel criarAbaHistorico() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Tema.BG);
        topo.add(Tema.criarLabel("◈ TODOS OS ALERTAS (incluindo resolvidos)",
                Tema.F_LABEL, Tema.TEXT3), BorderLayout.WEST);
        p.add(topo, BorderLayout.NORTH);

        String[] cols = {"TIPO", "ANIMAL", "MENSAGEM", "DATA/HORA", "RESOLVIDO"};
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Alerta a : backend.alertaService.listarTodos())
            m.addRow(new Object[]{
                    a.getTipo().toString(),
                    a.getAnimal() != null ? a.getAnimal().getNome() : "—",
                    a.getMensagem(),
                    a.getDataHoraFormatada(),
                    a.isResolvido() ? "✔" : "✘"});

        JTable tabHist = Tema.criarTabela(m);
        tabHist.getColumnModel().getColumn(0).setCellRenderer(tipoRenderer());
        tabHist.getColumnModel().getColumn(4).setCellRenderer(resolvidoRenderer());
        p.add(Tema.criarScroll(tabHist), BorderLayout.CENTER);
        return p;
    }

    private DefaultTableCellRenderer tipoRenderer() {
        return new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                l.setOpaque(true);
                String val = v == null ? "" : v.toString();
                if (val.contains("FORA"))         { l.setBackground(new Color(61,26,26)); l.setForeground(Tema.RED);   }
                else if (val.contains("BATERIA")) { l.setBackground(new Color(61,46,10)); l.setForeground(Tema.AMBER); }
                else                              { l.setBackground(new Color(26,61,28)); l.setForeground(Tema.GREEN3);}
                return l;
            }
        };
    }

    private DefaultTableCellRenderer resolvidoRenderer() {
        return new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                l.setOpaque(true);
                if ("✔".equals(v)) { l.setBackground(new Color(26,61,28)); l.setForeground(Tema.GREEN3); }
                else               { l.setBackground(new Color(61,26,26)); l.setForeground(Tema.RED);   }
                return l;
            }
        };
    }

    private void resolverTodos() {
        backend.alertaService.resolverTodos();
        carregarAlertasAtivos();
        LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Resolveu todos os alertas");
        JOptionPane.showMessageDialog(this, "Todos os alertas foram resolvidos!",
                "OK", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportar() {
        StringBuilder sb = new StringBuilder("=== RELATÓRIO SIRATECH ===\nData: ")
                .append(new Date()).append("\n\n");
        sb.append("--- ANIMAIS ---\n");
        Fazenda fa = backend.getFazendaAtiva();
        List<Animal> animais = fa != null
                ? backend.animalService.listarPorFazenda(fa.getId())
                : backend.animalService.listarTodos();
        for (Animal a : animais)
            sb.append(String.format("%-15s %-8s %s\n",
                    a.getNome(), a.getNumeroBrinco(), a.getStatus()));
        sb.append("\n--- ALERTAS ATIVOS ---\n");
        for (Alerta a : backend.alertaService.listarAtivos())
            sb.append(a.getDataHoraFormatada()).append(" | ")
              .append(a.getTipo()).append(" | ").append(a.getMensagem()).append("\n");
        try {
            String n = "relatorio_alertas_" + System.currentTimeMillis() + ".txt";
            FileWriter fw = new FileWriter(n);
            fw.write(sb.toString());
            fw.close();
            LogAtividades.registrar(backend.authService.getUsuarioLogado(),
                    "Exportou relatório de alertas");
            JOptionPane.showMessageDialog(this,
                    "Salvo: " + new File(n).getAbsolutePath(), "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
