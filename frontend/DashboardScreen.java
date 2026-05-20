package frontend;

import backend.*;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

public class DashboardScreen extends JPanel {

    private final Backend    backend;
    private final MainFrame  mainFrame;
    private StatCard         scAnimais, scArea, scAlertas, scColares;
    private DefaultTableModel modelAnimais;
    private MapaPanel        mapaPanel;

    // ── helper de ícone ──────────────────────────────────────────────────────
    private static FlatSVGIcon ico(String name, int size) {
        return new FlatSVGIcon("icons/" + name + ".svg", size, size);
    }

    public DashboardScreen(MainFrame frame, Backend backend) {
        this.mainFrame = frame;
        this.backend   = backend;
        setBackground(Tema.BG);
        setLayout(new BorderLayout());
        add(new NavBar(frame, backend, 0), BorderLayout.NORTH);
        add(criarConteudo(), BorderLayout.CENTER);
    }

    private JPanel criarConteudo() {
        JPanel c = new JPanel(new BorderLayout(0, 10));
        c.setBackground(Tema.BG);
        c.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        // ── Header ──────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Tema.BG);
        Fazenda fa = backend.getFazendaAtiva();
        String nomeFaz = fa != null ? " — " + fa.getNome() : " — TODAS AS FAZENDAS";

        JLabel lblTitulo = Tema.criarLabel("PAINEL DE CONTROLE" + nomeFaz, Tema.F_TITLE, Tema.GREENL);
        lblTitulo.setIcon(ico("zap", 20));
        lblTitulo.setIconTextGap(8);
        header.add(lblTitulo, BorderLayout.WEST);

        JPanel headerR = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        headerR.setBackground(Tema.BG);
        JLabel data = Tema.criarLabel(
                new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()),
                Tema.F_SMALL, Tema.TEXT3);

        JButton btnRefresh = Tema.criarBotaoRefresh();
        btnRefresh.setIcon(ico("refresh-cw", 16));
        btnRefresh.addActionListener(e -> {
            atualizarDados();
            data.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
        });
        headerR.add(data);
        headerR.add(btnRefresh);
        header.add(headerR, BorderLayout.EAST);

        // ── StatCards ───────────────────────────────────────────────────────
        JPanel stats = new JPanel(new GridLayout(1, 4, 10, 0));
        stats.setBackground(Tema.BG);

        scAnimais = new StatCard("ANIMAIS ATIVOS",  String.valueOf(totalAnimaisFazenda()),  Tema.GREENL, "cadastrados");
        scArea    = new StatCard("FAZENDAS/ÁREAS",  fazendaAreaLabel(),                     Tema.GREEN3, "fazendas/lotes");
        scAlertas = new StatCard("ALERTAS",         String.valueOf(totalAlertasFazenda()),  Tema.RED,    "pendentes");
        scColares = new StatCard("BRINCOES ATIVOS",  String.valueOf(totalColaresAtivosFA()), Tema.CYAN,   "Brincos Conectados");

        scAnimais.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        scAnimais.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { mainFrame.navegarPara(1); } });
        scArea.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        scArea.addMouseListener(new MouseAdapter()    { public void mouseClicked(MouseEvent e) { mainFrame.navegarPara(3); } });
        scColares.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        scColares.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { mainFrame.navegarPara(2); } });
        scAlertas.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        scAlertas.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { mainFrame.navegarPara(4); } });

        stats.add(scAnimais); stats.add(scArea); stats.add(scAlertas); stats.add(scColares);

        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.setBackground(Tema.BG);
        top.add(header, BorderLayout.NORTH);
        top.add(stats,  BorderLayout.CENTER);
        c.add(top, BorderLayout.NORTH);

        // ── Grade: mapa + lista animais ─────────────────────────────────────
        JPanel grade = new JPanel(new GridLayout(1, 2, 12, 0));
        grade.setBackground(Tema.BG);

        // Card mapa
        JPanel cardMapa = Tema.criarCard();
        cardMapa.setLayout(new BorderLayout(0, 6));

        JLabel lblMapa = Tema.criarLabel("MAPA", Tema.F_LABEL, Tema.TEXT3);
        lblMapa.setIcon(ico("zap", 14));
        lblMapa.setIconTextGap(6);
        cardMapa.add(lblMapa, BorderLayout.NORTH);

        mapaPanel = new MapaPanel(backend, animaisDaFazenda());

        // ── Ativa modo ao vivo se houver fazenda ativa ───────────────────────
        Fazenda fazendaAtiva = backend.getFazendaAtiva();
        if (fazendaAtiva == null) {
             fazendaAtiva = backend.fazendaService.getFazendaPrincipal();
        }
        if (fazendaAtiva != null) {
            mapaPanel.iniciarModoAoVivo(backend.rastreamentoService, fazendaAtiva);
        } else {
            System.out.println("[Dashboard] Nenhuma fazenda encontrada — modo ao vivo desativado.");
        }

        cardMapa.add(mapaPanel, BorderLayout.CENTER);

        JPanel leg = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        leg.setBackground(Tema.CARD);
        leg.add(Tema.criarLabel("● DENTRO",   Tema.F_SMALL, Tema.GREEN3));
        leg.add(Tema.criarLabel("● FORA",     Tema.F_SMALL, Tema.RED));
        leg.add(Tema.criarLabel("◌ GEOFENCE", Tema.F_SMALL, Tema.CYAN));
        leg.add(Tema.criarLabel("— TRILHA",   Tema.F_SMALL, new Color(200, 80, 80)));
        cardMapa.add(leg, BorderLayout.SOUTH);
        grade.add(cardMapa);

        // Card lista de animais
        JPanel cardLista = Tema.criarCard();
        cardLista.setLayout(new BorderLayout(0, 6));

        JPanel topoLista = new JPanel(new BorderLayout());
        topoLista.setBackground(Tema.CARD);

        JLabel lblAnimais = Tema.criarLabel("ANIMAIS MONITORADOS", Tema.F_LABEL, Tema.TEXT3);
        lblAnimais.setIcon(ico("users", 14));
        lblAnimais.setIconTextGap(6);
        topoLista.add(lblAnimais, BorderLayout.WEST);
        topoLista.add(Tema.criarLabel("2× → detalhes", Tema.F_SMALL, Tema.TEXT3), BorderLayout.EAST);
        cardLista.add(topoLista, BorderLayout.NORTH);

        String[] cols = {"NOME", "BRINCO", "RAÇA", "LOTE", "STATUS"};
        modelAnimais = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        carregarTabelaAnimais();
        JTable tabela = Tema.criarTabela(modelAnimais);
        tabela.getColumnModel().getColumn(4).setCellRenderer(statusRenderer());

        tabela.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tabela.getSelectedRow();
                    if (row < 0) return;
                    backend.animalService.buscarPorBrinco(modelAnimais.getValueAt(row, 1).toString())
                            .ifPresent(a -> new DetalhesAnimalDialog(
                                    SwingUtilities.getWindowAncestor(DashboardScreen.this), a, backend));
                }
            }
        });
        cardLista.add(Tema.criarScroll(tabela), BorderLayout.CENTER);
        grade.add(cardLista);
        c.add(grade, BorderLayout.CENTER);

        // Timer de auto-refresh
        new javax.swing.Timer(30000, e -> atualizarDados()).start();
        return c;
    }

    // ── lógica original — não alterada ──────────────────────────────────────

    private List<Animal> animaisDaFazenda() {
        Fazenda fa = backend.getFazendaAtiva();
        return fa != null
                ? backend.animalService.listarPorFazenda(fa.getId())
                : backend.animalService.listarTodos();
    }

    private long totalAnimaisFazenda() {
        return animaisDaFazenda().stream()
                .filter(a -> "Ativo".equals(a.getStatus()))
                .count();
    }

    private long totalAlertasFazenda() {
        Fazenda fa = backend.getFazendaAtiva();
        if (fa == null) return backend.totalAlertas();
        return backend.alertaService.listarAtivos().stream()
                .filter(al -> al.getAnimal() != null
                        && al.getAnimal().getFazendaId() != null
                        && fa.getId() == al.getAnimal().getFazendaId())
                .count();
    }

    private long totalColaresAtivosFA() {
        Fazenda fa = backend.getFazendaAtiva();
        if (fa == null) return backend.totalColaresAtivos();
        return animaisDaFazenda().stream()
                .filter(a -> a.getColar() != null)
                .count();
    }

    public void atualizarDados() {
        scAnimais.atualizar(String.valueOf(totalAnimaisFazenda()));
        scArea.atualizar(fazendaAreaLabel());
        scAlertas.atualizar(String.valueOf(totalAlertasFazenda()));
        scColares.atualizar(String.valueOf(totalColaresAtivosFA()));
        carregarTabelaAnimais();
        // Só atualiza no modo estático — no modo ao vivo o MapaPanel se atualiza sozinho
        if (mapaPanel != null && !mapaPanel.isModoAoVivo())
            mapaPanel.atualizarAnimais(animaisDaFazenda());
    }

    private void carregarTabelaAnimais() {
        if (modelAnimais == null) return;
        modelAnimais.setRowCount(0);
        for (Animal a : animaisDaFazenda())
            modelAnimais.addRow(new Object[]{
                    a.getNome(), a.getNumeroBrinco(), a.getRaca(), a.getLote(), a.getStatus()});
    }

    private String fazendaAreaLabel() {
        Fazenda fa = backend.getFazendaAtiva();
        if (fa != null) {
            long lotes = fa.getLotes() != null ? fa.getLotes().size() : 0;
            return "1/" + lotes;
        }
        long faz   = backend.fazendaService.listarTodas().size();
        long lotes = backend.fazendaService.listarTodas().stream()
                .mapToLong(f -> f.getLotes() != null ? f.getLotes().size() : 0).sum();
        return faz + "/" + lotes;
    }

    private DefaultTableCellRenderer statusRenderer() {
        return new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                l.setOpaque(true);
                l.setFont(Tema.F_SMALL);
                String val = v == null ? "" : v.toString();
                if ("Ativo".equals(val)) {
                    l.setBackground(new Color(26, 61, 28));  l.setForeground(Tema.GREEN3);
                } else if ("Vendido".equals(val) || "Abatido".equals(val)) {
                    l.setBackground(new Color(50, 40, 10));  l.setForeground(Tema.AMBER);
                } else {
                    l.setBackground(new Color(61, 26, 26));  l.setForeground(Tema.RED);
                }
                return l;
            }
        };
    }
}
