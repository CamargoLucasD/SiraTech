package frontend;

import backend.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

public class FinanceiroScreen extends JPanel {

    private final Backend   backend;
    private final MainFrame mainFrame;
    private DefaultTableModel modelTransacoes;
    private JTable            tabelaTransacoes;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public FinanceiroScreen(MainFrame frame, Backend backend) {
        this.mainFrame = frame;
        this.backend   = backend;
        setBackground(Tema.BG);
        setLayout(new BorderLayout());
        add(new NavBar(frame, backend, 7), BorderLayout.NORTH);
        add(criarConteudo(), BorderLayout.CENTER);
    }

    public void atualizarDados() {
        carregarTransacoes();
    }

    private JPanel criarConteudo() {
        JTabbedPane abas = Tema.criarAbas();
        abas.addTab("💰 RESUMO",       criarAbaResumo());
        abas.addTab("🛒 COMPRA/VENDA", criarAbaTransacoes());

        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(Tema.BG);

        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(Tema.BG);
        h.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));
        Fazenda fa = backend.getFazendaAtiva();
        String nomeFaz = fa != null ? " — " + fa.getNome() : "";
        h.add(Tema.criarLabel("GESTÃO FINANCEIRA" + nomeFaz, Tema.F_TITLE, Tema.GREENL), BorderLayout.WEST);

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

    // ─── Aba Resumo ───────────────────────────────────────────────────────────
    private JPanel criarAbaResumo() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        Fazenda fa = backend.getFazendaAtiva();
        int total = backend.totalAnimais();
        double valEst = total * 3500.0;
        double totalVendas  = fa != null ? backend.transacaoService.totalVendasPorFazenda(fa.getId())  : 0;
        double totalCompras = fa != null ? backend.transacaoService.totalComprasPorFazenda(fa.getId()) : 0;
        double saldo = totalVendas - totalCompras;

        JPanel stats = new JPanel(new GridLayout(1, 4, 10, 0));
        stats.setBackground(Tema.BG);
        stats.add(new StatCard("REBANHO",        String.valueOf(total),                          Tema.GREENL, "animais"));
        stats.add(new StatCard("VALOR ESTIMADO",  "R$ " + String.format("%,.0f", valEst),        Tema.GREEN3, "@ R$3.500/cab."));
        stats.add(new StatCard("TOTAL VENDAS",    "R$ " + String.format("%,.2f", totalVendas),   Tema.CYAN,   "realizadas"));
        stats.add(new StatCard("SALDO",
                (saldo >= 0 ? "+" : "") + "R$ " + String.format("%,.2f", saldo),
                saldo >= 0 ? Tema.GREEN3 : Tema.RED, "receitas - custos"));
        p.add(stats, BorderLayout.NORTH);

        // Resumo por fazenda / lote com valor estimado
        JPanel card = Tema.criarCard();
        card.setLayout(new BorderLayout(0, 8));
        card.add(Tema.criarLabel("◈ RESUMO POR FAZENDA / LOTE", Tema.F_LABEL, Tema.TEXT3), BorderLayout.NORTH);

        String[] cols = {"FAZENDA", "LOTE", "ANIMAIS", "VALOR ESTIMADO", "STATUS"};
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Fazenda f : backend.fazendaService.listarTodas()) {
            List<Animal> animaisFaz = backend.animalService.listarPorFazenda(f.getId());
            Map<String, Integer> loteCount = new LinkedHashMap<>();
            for (Animal a : animaisFaz)
                loteCount.merge(a.getLote() != null ? a.getLote() : "Sem lote", 1, Integer::sum);
            if (loteCount.isEmpty()) {
                m.addRow(new Object[]{f.getNome(), "—", 0, "R$ 0,00", "—"});
            } else {
                for (Map.Entry<String, Integer> e : loteCount.entrySet())
                    m.addRow(new Object[]{
                            f.getNome(), e.getKey(), e.getValue(),
                            "R$ " + String.format("%,.2f", e.getValue() * 3500.0), "Ativo"});
            }
        }
        card.add(Tema.criarScroll(Tema.criarTabela(m)), BorderLayout.CENTER);
        p.add(card, BorderLayout.CENTER);
        return p;
    }

    // ─── Aba Transações ───────────────────────────────────────────────────────
    private JPanel criarAbaTransacoes() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        // Formulário de registro
        JPanel form = Tema.criarCard();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.add(Tema.criarLabel("◈ REGISTRAR TRANSAÇÃO", Tema.F_LABEL, Tema.TEXT3));
        form.add(Box.createVerticalStrut(10));

        Fazenda fa = backend.getFazendaAtiva();
        List<Animal> animais = fa != null
                ? backend.animalService.listarPorFazenda(fa.getId())
                : backend.animalService.listarTodos();
        String[] nomes = animais.stream().map(a -> a.getNome() + " #" + a.getNumeroBrinco()).toArray(String[]::new);
        JComboBox<String> cAnimal = nomes.length > 0 ? Tema.criarCombo(nomes) : Tema.criarCombo("Nenhum");
        JComboBox<String> cTipo   = Tema.criarCombo("Venda", "Compra", "Abate");
        JTextField cValor = Tema.criarCampo("0.00");
        JTextField cData  = Tema.criarCampo("dd/mm/aaaa");
        JTextField cDesc  = Tema.criarCampo("Descrição da transação");

        form.add(Tema.par("ANIMAL", cAnimal, "TIPO", cTipo));
        form.add(Box.createVerticalStrut(8));
        form.add(Tema.par("VALOR (R$)", cValor, "DATA (dd/mm/aaaa)", cData));
        form.add(Box.createVerticalStrut(8));
        form.add(Tema.campo("DESCRIÇÃO", cDesc));
        form.add(Box.createVerticalStrut(12));

        JButton btnReg = Tema.criarBotaoPrimario("💰 REGISTRAR");
        btnReg.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnReg.addActionListener(e -> {
            if (fa == null) {
                JOptionPane.showMessageDialog(this, "Selecione uma fazenda ativa!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int idx = cAnimal.getSelectedIndex();
            if (animais.isEmpty() || idx < 0) {
                JOptionPane.showMessageDialog(this, "Nenhum animal disponível!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                double valor = Double.parseDouble(cValor.getText().trim().replace(",", "."));
                LocalDate data = parseData(cData.getText().trim());
                Animal animal = animais.get(idx);
                String tipo = cTipo.getSelectedItem().toString();
                String desc = cDesc.getText().trim();
                if (desc.equals("Descrição da transação")) desc = tipo + " de " + animal.getNome();

                Transacao t = new Transacao(animal, fa, tipo, valor, data, desc);
                backend.transacaoService.salvar(t);
                LogAtividades.registrar(backend.authService.getUsuarioLogado(),
                        "Registrou " + tipo + " R$" + String.format("%.2f", valor) + " — " + animal.getNome());
                carregarTransacoes();
                cValor.setText("0.00");
                cData.setText("dd/mm/aaaa");
                cDesc.setText("Descrição da transação");
                JOptionPane.showMessageDialog(this, "Transação registrada!", "OK", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Valor inválido! Use formato: 1500.00", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        form.add(btnReg);
        p.add(form, BorderLayout.NORTH);

        // Tabela de histórico de transações
        JPanel card2 = Tema.criarCard();
        card2.setLayout(new BorderLayout(0, 8));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Tema.CARD);
        topo.add(Tema.criarLabel("◈ HISTÓRICO DE TRANSAÇÕES", Tema.F_LABEL, Tema.TEXT3), BorderLayout.WEST);

        JPanel btnsDir = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnsDir.setBackground(Tema.CARD);
        JButton btnDel = new JButton("✕ Excluir");
        btnDel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        Tema.semFoco(btnDel);
        btnDel.setBackground(new Color(50, 15, 15));
        btnDel.setForeground(new Color(180, 60, 60));
        btnDel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 40, 40), 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        btnDel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDel.addActionListener(e -> excluirTransacaoSelecionada());
        btnsDir.add(btnDel);
        topo.add(btnsDir, BorderLayout.EAST);
        card2.add(topo, BorderLayout.NORTH);

        String[] cols = {"DATA", "TIPO", "ANIMAL", "VALOR", "DESCRIÇÃO", "STATUS"};
        modelTransacoes = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        carregarTransacoes();
        tabelaTransacoes = Tema.criarTabela(modelTransacoes);
        tabelaTransacoes.getColumnModel().getColumn(1).setCellRenderer(tipoRenderer());
        card2.add(Tema.criarScroll(tabelaTransacoes), BorderLayout.CENTER);

        // Totalizadores na barra inferior
        JPanel totais = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 6));
        totais.setBackground(Tema.CARD);
        totais.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Tema.BORDER));
        if (fa != null) {
            double tv = backend.transacaoService.totalVendasPorFazenda(fa.getId());
            double tc = backend.transacaoService.totalComprasPorFazenda(fa.getId());
            totais.add(Tema.criarLabel("Vendas: R$ " + String.format("%,.2f", tv), Tema.F_SMALL, Tema.GREEN3));
            totais.add(Tema.criarLabel("Compras: R$ " + String.format("%,.2f", tc), Tema.F_SMALL, Tema.AMBER));
            double saldo = tv - tc;
            totais.add(Tema.criarLabel("Saldo: " + (saldo >= 0 ? "+" : "") + "R$ " + String.format("%,.2f", saldo),
                    Tema.F_SMALL, saldo >= 0 ? Tema.CYAN : Tema.RED));
        }
        card2.add(totais, BorderLayout.SOUTH);
        p.add(card2, BorderLayout.CENTER);
        return p;
    }

    private void carregarTransacoes() {
        if (modelTransacoes == null) return;
        modelTransacoes.setRowCount(0);
        Fazenda fa = backend.getFazendaAtiva();
        if (fa == null) return;
        for (Transacao t : backend.transacaoService.listarPorFazenda(fa.getId()))
            modelTransacoes.addRow(new Object[]{
                    t.getDataStr(), t.getTipo(),
                    t.getAnimal() != null ? t.getAnimal().getNome() : "—",
                    t.getValorStr(),
                    t.getDescricao() != null ? t.getDescricao() : "",
                    t.getStatus()});
    }

    private void excluirTransacaoSelecionada() {
        int row = tabelaTransacoes != null ? tabelaTransacoes.getSelectedRow() : -1;
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione uma transação!", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
        Fazenda fa = backend.getFazendaAtiva();
        if (fa == null) return;
        List<Transacao> lista = backend.transacaoService.listarPorFazenda(fa.getId());
        if (row >= lista.size()) return;
        Transacao t = lista.get(row);
        if (JOptionPane.showConfirmDialog(this,
                "Excluir transação de " + t.getValorStr() + " (" + t.getTipo() + ")?",
                "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            backend.transacaoService.excluir(t.getId());
            LogAtividades.registrar(backend.authService.getUsuarioLogado(),
                    "Excluiu transação: " + t.getTipo() + " " + t.getValorStr());
            carregarTransacoes();
        }
    }

    private LocalDate parseData(String s) {
        if (s.matches("\\d{2}/\\d{2}/\\d{4}"))
            return LocalDate.parse(s, FMT);
        if (s.matches("\\d{4}-\\d{2}-\\d{2}"))
            return LocalDate.parse(s);
        throw new IllegalArgumentException("Data inválida: " + s);
    }

    private DefaultTableCellRenderer tipoRenderer() {
        return new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                l.setOpaque(true);
                l.setFont(Tema.F_SMALL);
                String val = v == null ? "" : v.toString();
                switch (val) {
                    case "Venda":  l.setBackground(new Color(26, 61, 28)); l.setForeground(Tema.GREEN3); break;
                    case "Compra": l.setBackground(new Color(20, 40, 60)); l.setForeground(Tema.CYAN);   break;
                    case "Abate":  l.setBackground(new Color(61, 26, 26)); l.setForeground(Tema.RED);    break;
                    default:       l.setBackground(Tema.BG3);              l.setForeground(Tema.TEXT2);  break;
                }
                return l;
            }
        };
    }
}
