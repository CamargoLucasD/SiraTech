package frontend;

import backend.*;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.*;
import java.awt.event.*;
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

    // ── Ícone SVG helper ──────────────────────────────────────────────────────
    private static FlatSVGIcon ico(String name, int size) {
        try {
            return new FlatSVGIcon("icons/" + name + ".svg", size, size);
        } catch (Exception e) {
            return null;
        }
    }

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

        abas.addTab(null, criarAbaAtivos());
        abas.setTabComponentAt(0, criarTabLabel("ALERTAS ATIVOS", ico("alert-triangle", 14), Tema.RED));

        abas.addTab(null, criarAbaCriar());
        abas.setTabComponentAt(1, criarTabLabel("CRIAR ALERTA", ico("plus-circle", 14), Tema.TEXT2));

        abas.addTab(null, criarAbaHistorico());
        abas.setTabComponentAt(2, criarTabLabel("HISTÓRICO", ico("clock", 14), Tema.TEXT2));

        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(Tema.BG);

        // ── Cabeçalho ────────────────────────────────────────────────────────
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(Tema.BG);
        h.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));

        JPanel titulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titulo.setBackground(Tema.BG);
        titulo.add(new JLabel(ico("activity", 20)));
        titulo.add(Tema.criarLabel("MONITORAMENTO & ALERTAS", Tema.F_TITLE, Tema.GREENL));
        h.add(titulo, BorderLayout.WEST);

        JPanel hr = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        hr.setBackground(Tema.BG);

        JButton btnRef = Tema.criarBotaoRefresh();
        btnRef.setIcon(ico("refresh-cw", 14));
        btnRef.setToolTipText("Atualizar alertas");
        btnRef.addActionListener(e -> carregarAlertasAtivos());

        JButton btnExp = Tema.criarBotaoSecundario("EXPORTAR");
        btnExp.setIcon(ico("download", 16));
        btnExp.setIconTextGap(6);
        btnExp.addActionListener(e -> exportar());

        JButton btnRes = Tema.criarBotaoPrimario("RESOLVER TODOS");
        btnRes.setIcon(ico("check-circle", 16));
        btnRes.setIconTextGap(6);
        btnRes.addActionListener(e -> resolverTodos());

        hr.add(btnRef);
        hr.add(btnExp);
        hr.add(btnRes);
        h.add(hr, BorderLayout.EAST);

        c.add(h, BorderLayout.NORTH);
        c.add(abas, BorderLayout.CENTER);
        return c;
    }

    // ── Utilitário: label de aba com ícone e cor ──────────────────────────────
    private JPanel criarTabLabel(String texto, FlatSVGIcon icone, Color cor) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        p.setOpaque(false);
        if (icone != null) {
            JLabel icoLbl = new JLabel(icone);
            p.add(icoLbl);
        }
        JLabel lbl = new JLabel(texto);
        lbl.setFont(Tema.F_SMALL);
        lbl.setForeground(cor);
        p.add(lbl);
        return p;
    }

    // ── Helpers de fazenda ────────────────────────────────────────────────────
    private List<Animal> animaisDaFazenda() {
        Fazenda fa = backend.getFazendaAtiva();
        return fa != null
                ? backend.animalService.listarPorFazenda(fa.getId())
                : backend.animalService.listarTodos();
    }

    private List<Alerta> alertasDaFazenda() {
        Fazenda fa = backend.getFazendaAtiva();
        List<Alerta> todos = backend.alertaService.listarAtivos();
        if (fa == null) return todos;
        return todos.stream()
                .filter(a -> a.getAnimal() != null
                        && a.getAnimal().getFazendaId() != null
                        && fa.getId() == a.getAnimal().getFazendaId())
                .collect(java.util.stream.Collectors.toList());
    }

    private List<Alerta> historicosDaFazenda() {
        Fazenda fa = backend.getFazendaAtiva();
        List<Alerta> todos = backend.alertaService.listarTodos();
        if (fa == null) return todos;
        return todos.stream()
                .filter(a -> a.getAnimal() != null
                        && a.getAnimal().getFazendaId() != null
                        && fa.getId() == a.getAnimal().getFazendaId())
                .collect(java.util.stream.Collectors.toList());
    }

    // ── Aba Alertas Ativos ────────────────────────────────────────────────────
    private JPanel criarAbaAtivos() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(12, 14, 10, 14));

        // Cards de resumo
        JPanel stats = new JPanel(new GridLayout(1, 3, 10, 0));
        stats.setBackground(Tema.BG);

        StatCard cardAlertas = new StatCard("ALERTAS ATIVOS",
                String.valueOf(alertasDaFazenda().size()), Tema.RED, "pendentes");
        StatCard cardBat = new StatCard("BATERIA BAIXA",
                String.valueOf(backend.colarService.colaresBateriaBaixa(20).size()), Tema.AMBER, "< 20%");
        StatCard cardRast = new StatCard("RASTREAMENTOS",
                String.valueOf(backend.rastreamentoService.totalRegistros()), Tema.CYAN, "posições");

        stats.add(cardAlertas);
        stats.add(cardBat);
        stats.add(cardRast);
        p.add(stats, BorderLayout.NORTH);

        // Card tabela
        JPanel card = Tema.criarCard();
        card.setLayout(new BorderLayout(0, 8));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Tema.CARD);

        JPanel topoLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        topoLeft.setBackground(Tema.CARD);
        topoLeft.add(new JLabel(ico("alert-triangle", 14)));
        JLabel lblTopo = Tema.criarLabel("ALERTAS ATIVOS", Tema.F_LABEL, Tema.RED);
        topoLeft.add(lblTopo);
        topo.add(topoLeft, BorderLayout.WEST);
        topo.add(Tema.criarLabel("2× clique para detalhes do animal", Tema.F_SMALL, Tema.TEXT3), BorderLayout.EAST);
        card.add(topo, BorderLayout.NORTH);

        String[] cols = {"TIPO", "ANIMAL", "MENSAGEM", "DATA/HORA"};
        modelAtivos = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        carregarAlertasAtivos();

        tabelaAtivos = Tema.criarTabela(modelAtivos);
        tabelaAtivos.getColumnModel().getColumn(0).setCellRenderer(tipoRenderer());
        tabelaAtivos.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
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

        // Botão resolver selecionado
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        acoes.setBackground(Tema.CARD);
        acoes.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Tema.BORDER));

        JButton btnR = Tema.criarBotaoPrimario("RESOLVER SELECIONADO");
        btnR.setIcon(ico("check", 16));
        btnR.setIconTextGap(6);
        btnR.addActionListener(e -> {
            int row = tabelaAtivos.getSelectedRow();
            if (row < 0) return;
            List<Alerta> ativos = alertasDaFazenda();
            if (row < ativos.size()) {
                backend.alertaService.resolverAlerta(ativos.get(row).getId());
                LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Resolveu alerta selecionado");
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
        for (Alerta a : alertasDaFazenda())
            modelAtivos.addRow(new Object[]{
                    a.getTipo().toString(),
                    a.getAnimal() != null ? a.getAnimal().getNome() : "—",
                    a.getMensagem(),
                    a.getDataHoraFormatada()});
    }

    // ── Aba Criar Alerta ──────────────────────────────────────────────────────
    private JPanel criarAbaCriar() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(12, 14, 10, 14));

        JPanel card = Tema.criarCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JPanel secHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        secHeader.setBackground(Tema.CARD);
        secHeader.add(new JLabel(ico("plus-circle", 14)));
        secHeader.add(Tema.criarLabel("CRIAR ALERTA MANUAL", Tema.F_LABEL, Tema.TEXT3));
        card.add(secHeader);
        card.add(Box.createVerticalStrut(14));

        List<Animal> animais = animaisDaFazenda();
        String[] nomes = animais.stream()
                .map(a -> a.getNome() + " #" + a.getNumeroBrinco()).toArray(String[]::new);
        JComboBox<String> cAnimal = nomes.length > 0 ? Tema.criarCombo(nomes) : Tema.criarCombo("Nenhum");
        JComboBox<String> cTipo   = Tema.criarCombo("FORA_DA_AREA", "BATERIA_BAIXA", "SEM_SINAL", "RETORNOU");
        JTextField        cMsg    = Tema.criarCampo("Descreva o alerta...");

        cAnimal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cTipo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cMsg.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        card.add(Tema.campo("ANIMAL", cAnimal));
        card.add(Box.createVerticalStrut(8));
        card.add(Tema.campo("TIPO DE ALERTA", cTipo));
        card.add(Box.createVerticalStrut(8));
        card.add(Tema.campo("MENSAGEM", cMsg));
        card.add(Box.createVerticalStrut(16));

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botoes.setBackground(Tema.CARD);
        botoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnCriar = Tema.criarBotaoPrimario("CRIAR ALERTA");
        btnCriar.setIcon(ico("alert-triangle", 16));
        btnCriar.setIconTextGap(6);
        btnCriar.addActionListener(e -> {
            int idx = cAnimal.getSelectedIndex();
            if (animais.isEmpty() || idx < 0) return;
            Animal a   = animais.get(idx);
            String ts  = cTipo.getSelectedItem().toString();
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
        botoes.add(btnCriar);
        card.add(botoes);

        p.add(card, BorderLayout.NORTH);
        return p;
    }

    // ── Aba Histórico ─────────────────────────────────────────────────────────
    private JPanel criarAbaHistorico() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(12, 14, 10, 14));

        JPanel card = Tema.criarCard();
        card.setLayout(new BorderLayout(0, 8));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Tema.CARD);

        JPanel topoLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        topoLeft.setBackground(Tema.CARD);
        topoLeft.add(new JLabel(ico("clock", 14)));
        topoLeft.add(Tema.criarLabel("TODOS OS ALERTAS (incluindo resolvidos)", Tema.F_LABEL, Tema.TEXT3));
        topo.add(topoLeft, BorderLayout.WEST);
        card.add(topo, BorderLayout.NORTH);

        String[] cols = {"TIPO", "ANIMAL", "MENSAGEM", "DATA/HORA", "RESOLVIDO"};
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Alerta a : historicosDaFazenda())
            m.addRow(new Object[]{
                    a.getTipo().toString(),
                    a.getAnimal() != null ? a.getAnimal().getNome() : "—",
                    a.getMensagem(),
                    a.getDataHoraFormatada(),
                    a.isResolvido() ? "✔" : "✘"});

        JTable tabHist = Tema.criarTabela(m);
        tabHist.getColumnModel().getColumn(0).setCellRenderer(tipoRenderer());
        tabHist.getColumnModel().getColumn(4).setCellRenderer(resolvidoRenderer());
        card.add(Tema.criarScroll(tabHist), BorderLayout.CENTER);

        p.add(card, BorderLayout.CENTER);
        return p;
    }

    // ── Renderers ─────────────────────────────────────────────────────────────
    private DefaultTableCellRenderer tipoRenderer() {
        return new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                l.setOpaque(true);
                l.setFont(Tema.F_SMALL);
                l.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                String val = v == null ? "" : v.toString();
                if (val.contains("FORA"))         { l.setBackground(new Color(61, 26, 26)); l.setForeground(Tema.RED);    }
                else if (val.contains("BATERIA")) { l.setBackground(new Color(61, 46, 10)); l.setForeground(Tema.AMBER);  }
                else if (val.contains("SEM"))     { l.setBackground(new Color(30, 30, 60)); l.setForeground(Tema.BLUE);   }
                else                              { l.setBackground(new Color(26, 61, 28)); l.setForeground(Tema.GREEN3); }
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
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setFont(Tema.F_LABEL);
                if ("✔".equals(v)) { l.setBackground(new Color(26, 61, 28)); l.setForeground(Tema.GREEN3); }
                else               { l.setBackground(new Color(61, 26, 26)); l.setForeground(Tema.RED);   }
                return l;
            }
        };
    }

    // ── Ações ─────────────────────────────────────────────────────────────────
    private void resolverTodos() {
        for (Alerta a : alertasDaFazenda())
            backend.alertaService.resolverAlerta(a.getId());
        carregarAlertasAtivos();
        LogAtividades.registrar(backend.authService.getUsuarioLogado(),
                "Resolveu todos os alertas da fazenda ativa");
        JOptionPane.showMessageDialog(this, "Todos os alertas foram resolvidos!",
                "OK", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportar() {
        StringBuilder sb = new StringBuilder("=== RELATÓRIO SIRATECH ===\nData: ")
                .append(new Date()).append("\n\n");
        Fazenda fa = backend.getFazendaAtiva();
        sb.append("Fazenda: ").append(fa != null ? fa.getNome() : "TODAS").append("\n\n");
        sb.append("--- ANIMAIS ---\n");
        for (Animal a : animaisDaFazenda())
            sb.append(String.format("%-15s %-8s %s\n", a.getNome(), a.getNumeroBrinco(), a.getStatus()));
        sb.append("\n--- ALERTAS ATIVOS ---\n");
        for (Alerta a : alertasDaFazenda())
            sb.append(a.getDataHoraFormatada()).append(" | ")
              .append(a.getTipo()).append(" | ").append(a.getMensagem()).append("\n");
        try {
            String n = "relatorio_alertas_" + System.currentTimeMillis() + ".txt";
            FileWriter fw = new FileWriter(n);
            fw.write(sb.toString());
            fw.close();
            LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Exportou relatório de alertas");
            JOptionPane.showMessageDialog(this, "Salvo: " + new File(n).getAbsolutePath(),
                    "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
