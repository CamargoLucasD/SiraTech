package frontend;

import backend.*;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

public class SaudeScreen extends JPanel {

    private final Backend   backend;
    private final MainFrame mainFrame;

    private JComboBox<String> cAnimalV, cVacinaV;
    private JTextField        cDataV, cProxV, cVetV, cObsV;
    private DefaultTableModel modelVacinas;
    private JTable            tabelaVacinas;
    private int               vacinaEditandoId = 0;

    private JComboBox<String> cAnimalH;
    private JTextField        cDataH, cProcH, cVetH, cObsH;
    private DefaultTableModel modelHistorico;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── Ícone SVG helper ──────────────────────────────────────────────────────
    private static FlatSVGIcon ico(String name, int size) {
        try {
            return new FlatSVGIcon("icons/" + name + ".svg", size, size);
        } catch (Exception e) {
            return null;
        }
    }

    public SaudeScreen(MainFrame frame, Backend backend) {
        this.mainFrame = frame;
        this.backend   = backend;
        setBackground(Tema.BG);
        setLayout(new BorderLayout());
        add(new NavBar(frame, backend, 6), BorderLayout.NORTH);
        add(criarConteudo(), BorderLayout.CENTER);
    }

    public void atualizarDados() {
        carregarVacinas();
        carregarHistorico();
    }

    private JPanel criarConteudo() {
        JTabbedPane abas = Tema.criarAbas();

        abas.addTab(null, criarAbaVacinas());
        abas.setTabComponentAt(0, criarTabLabel("VACINAÇÃO", ico("shield", 14)));

        abas.addTab(null, criarAbaHistoricoVet());
        abas.setTabComponentAt(1, criarTabLabel("HISTÓRICO VET.", ico("clipboard-list", 14)));

        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(Tema.BG);

        // ── Cabeçalho ────────────────────────────────────────────────────────
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(Tema.BG);
        h.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));

        JPanel titulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titulo.setBackground(Tema.BG);
        titulo.add(new JLabel(ico("heart", 20)));
        titulo.add(Tema.criarLabel("SAÚDE DO REBANHO", Tema.F_TITLE, Tema.GREENL));
        h.add(titulo, BorderLayout.WEST);

        JPanel hr = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        hr.setBackground(Tema.BG);
        JButton btnRef = Tema.criarBotaoRefresh();
        btnRef.setIcon(ico("refresh-cw", 14));
        btnRef.setToolTipText("Atualizar dados de saúde");
        btnRef.addActionListener(e -> atualizarDados());
        hr.add(btnRef);
        h.add(hr, BorderLayout.EAST);

        c.add(h, BorderLayout.NORTH);
        c.add(abas, BorderLayout.CENTER);
        return c;
    }

    // ── Utilitário: label de aba com ícone ────────────────────────────────────
    private JPanel criarTabLabel(String texto, FlatSVGIcon icone) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        p.setOpaque(false);
        if (icone != null) p.add(new JLabel(icone));
        JLabel lbl = new JLabel(texto);
        lbl.setFont(Tema.F_SMALL);
        p.add(lbl);
        return p;
    }

    // ── Aba Vacinação ─────────────────────────────────────────────────────────
    private JPanel criarAbaVacinas() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(12, 14, 10, 14));

        // Formulário de registro/edição
        JPanel form = Tema.criarCard();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JPanel formHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        formHeader.setBackground(Tema.CARD);
        formHeader.add(new JLabel(ico("shield", 14)));
        formHeader.add(Tema.criarLabel("REGISTRAR / EDITAR VACINAÇÃO", Tema.F_LABEL, Tema.TEXT3));
        form.add(formHeader);
        form.add(Box.createVerticalStrut(12));

        Fazenda fa = backend.getFazendaAtiva();
        List<Animal> animais = fa != null
                ? backend.animalService.listarPorFazenda(fa.getId())
                : backend.animalService.listarTodos();
        String[] nomes = animais.stream()
                .map(a -> a.getNome() + " #" + a.getNumeroBrinco()).toArray(String[]::new);
        cAnimalV = nomes.length > 0 ? Tema.criarCombo(nomes) : Tema.criarCombo("Nenhum");
        cVacinaV = Tema.criarCombo("Febre Aftosa", "Brucelose", "Raiva", "IBR", "BVD",
                "Clostridioses", "Leptospirose", "Outra");
        cDataV = Tema.criarCampo("dd/mm/aaaa");
        cProxV = Tema.criarCampo("dd/mm/aaaa");
        cVetV  = Tema.criarCampo("");
        cObsV  = Tema.criarCampo("");

        form.add(Tema.par("ANIMAL", cAnimalV, "VACINA", cVacinaV));
        form.add(Box.createVerticalStrut(8));
        form.add(Tema.par("DATA APLICAÇÃO (dd/mm/aaaa)", cDataV, "PRÓXIMA DOSE (dd/mm/aaaa)", cProxV));
        form.add(Box.createVerticalStrut(8));
        form.add(Tema.par("VETERINÁRIO", cVetV, "OBSERVAÇÕES", cObsV));
        form.add(Box.createVerticalStrut(14));

        JPanel botoesFrm = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botoesFrm.setBackground(Tema.CARD);
        botoesFrm.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnReg    = Tema.criarBotaoPrimario("SALVAR VACINAÇÃO");
        JButton btnLimpar = Tema.criarBotaoSecundario("LIMPAR");
        btnReg.setIcon(ico("save", 16));
        btnReg.setIconTextGap(6);
        btnLimpar.setIcon(ico("x", 16));
        btnLimpar.setIconTextGap(6);
        btnReg.addActionListener(e -> salvarVacina(animais));
        btnLimpar.addActionListener(e -> limparFormVacina());

        botoesFrm.add(btnReg);
        botoesFrm.add(btnLimpar);
        form.add(botoesFrm);
        p.add(form, BorderLayout.NORTH);

        // Card tabela de vacinações
        JPanel card2 = Tema.criarCard();
        card2.setLayout(new BorderLayout(0, 8));

        JPanel topo2 = new JPanel(new BorderLayout());
        topo2.setBackground(Tema.CARD);

        JPanel topo2Left = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        topo2Left.setBackground(Tema.CARD);
        topo2Left.add(new JLabel(ico("list", 14)));
        topo2Left.add(Tema.criarLabel("VACINAÇÕES DA FAZENDA", Tema.F_LABEL, Tema.TEXT3));
        topo2.add(topo2Left, BorderLayout.WEST);

        JPanel btnsDir = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnsDir.setBackground(Tema.CARD);

        JButton btnEdit = Tema.criarBotaoCyan("EDITAR");
        JButton btnDel  = Tema.criarBotaoPerigo("EXCLUIR");
        btnEdit.setIcon(ico("edit", 16));
        btnEdit.setIconTextGap(6);
        btnDel.setIcon(ico("trash-2", 16));
        btnDel.setIconTextGap(6);
        btnEdit.addActionListener(e -> editarVacinaSelecionada(animais));
        btnDel.addActionListener(e -> excluirVacinaSelecionada());

        btnsDir.add(btnEdit);
        btnsDir.add(btnDel);
        topo2.add(btnsDir, BorderLayout.EAST);
        card2.add(topo2, BorderLayout.NORTH);

        String[] cols = {"ANIMAL", "VACINA", "VETERINÁRIO", "APLICAÇÃO", "PRÓX. DOSE", "STATUS"};
        modelVacinas = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        carregarVacinas();
        tabelaVacinas = Tema.criarTabela(modelVacinas);
        tabelaVacinas.getColumnModel().getColumn(5).setCellRenderer(statusVacinaRenderer());
        card2.add(Tema.criarScroll(tabelaVacinas), BorderLayout.CENTER);

        p.add(card2, BorderLayout.CENTER);
        return p;
    }

    private void carregarVacinas() {
        if (modelVacinas == null) return;
        modelVacinas.setRowCount(0);
        Fazenda fa = backend.getFazendaAtiva();
        List<Vacina> lista = fa != null
                ? backend.vacinaService.listarPorFazenda(fa.getId())
                : new ArrayList<>();
        for (Vacina v : lista) {
            v.calcularStatus();
            modelVacinas.addRow(new Object[]{
                    v.getAnimal() != null ? v.getAnimal().getNome() : "—",
                    v.getTipoVacina(),
                    v.getVeterinario() != null ? v.getVeterinario() : "—",
                    v.getDataAplicacaoStr(),
                    v.getProximaDoseStr(),
                    v.getStatus()});
        }
    }

    private void salvarVacina(List<Animal> animais) {
        if (animais.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum animal disponível!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idx = cAnimalV.getSelectedIndex();
        if (idx < 0 || idx >= animais.size()) return;
        Animal animal = animais.get(idx);
        Fazenda fa = backend.getFazendaAtiva();
        if (fa == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma fazenda ativa antes de registrar!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            LocalDate dataApl  = parseData(cDataV.getText().trim());
            LocalDate proxDose = cProxV.getText().trim().equals("dd/mm/aaaa")
                    ? null : parseData(cProxV.getText().trim());
            Vacina v = vacinaEditandoId > 0
                    ? backend.vacinaService.buscarPorId(vacinaEditandoId).orElse(new Vacina())
                    : new Vacina();
            v.setAnimal(animal);
            v.setFazenda(fa);
            v.setTipoVacina(cVacinaV.getSelectedItem().toString());
            v.setDataAplicacao(dataApl);
            v.setProximaDose(proxDose);
            v.setVeterinario(cVetV.getText().trim());
            v.setObservacoes(cObsV.getText().trim());
            if (vacinaEditandoId > 0) {
                backend.vacinaService.atualizar(v);
                LogAtividades.registrar(backend.authService.getUsuarioLogado(),
                        "Editou vacinação: " + v.getTipoVacina() + " em " + animal.getNome());
            } else {
                backend.vacinaService.salvar(v);
                LogAtividades.registrar(backend.authService.getUsuarioLogado(),
                        "Registrou vacinação: " + v.getTipoVacina() + " em " + animal.getNome());
            }
            carregarVacinas();
            limparFormVacina();
            JOptionPane.showMessageDialog(this, "Vacinação salva com sucesso!", "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar: verifique a data (dd/mm/aaaa).", "Erro", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void editarVacinaSelecionada(List<Animal> animais) {
        int row = tabelaVacinas.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma vacinação!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Fazenda fa = backend.getFazendaAtiva();
        if (fa == null) return;
        List<Vacina> lista = backend.vacinaService.listarPorFazenda(fa.getId());
        if (row >= lista.size()) return;
        Vacina v = lista.get(row);
        vacinaEditandoId = v.getId();
        if (v.getAnimal() != null) {
            for (int i = 0; i < animais.size(); i++) {
                if (animais.get(i).getId() == v.getAnimal().getId()) {
                    cAnimalV.setSelectedIndex(i);
                    break;
                }
            }
        }
        cVacinaV.setSelectedItem(v.getTipoVacina());
        cDataV.setText(v.getDataAplicacaoStr());
        cProxV.setText(v.getProximaDoseStr() != null ? v.getProximaDoseStr() : "dd/mm/aaaa");
        cVetV.setText(v.getVeterinario() != null ? v.getVeterinario() : "");
        cObsV.setText(v.getObservacoes() != null ? v.getObservacoes() : "");
        JOptionPane.showMessageDialog(this,
                "Formulário preenchido com os dados da vacinação selecionada.\nFaça as alterações e clique em SALVAR.",
                "Editando vacinação", JOptionPane.INFORMATION_MESSAGE);
    }

    private void excluirVacinaSelecionada() {
        int row = tabelaVacinas.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma vacinação!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Fazenda fa = backend.getFazendaAtiva();
        if (fa == null) return;
        List<Vacina> lista = backend.vacinaService.listarPorFazenda(fa.getId());
        if (row >= lista.size()) return;
        Vacina v = lista.get(row);
        if (JOptionPane.showConfirmDialog(this,
                "Excluir vacinação de " + v.getTipoVacina() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            backend.vacinaService.excluir(v.getId());
            LogAtividades.registrar(backend.authService.getUsuarioLogado(),
                    "Excluiu vacinação: " + v.getTipoVacina());
            carregarVacinas();
        }
    }

    private void limparFormVacina() {
        vacinaEditandoId = 0;
        if (cAnimalV.getItemCount() > 0) cAnimalV.setSelectedIndex(0);
        if (cVacinaV.getItemCount() > 0) cVacinaV.setSelectedIndex(0);
        cDataV.setText("dd/mm/aaaa");
        cProxV.setText("dd/mm/aaaa");
        cVetV.setText("");
        cObsV.setText("");
    }

    // ── Aba Histórico Veterinário ─────────────────────────────────────────────
    private JPanel criarAbaHistoricoVet() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(12, 14, 10, 14));

        // Formulário rápido
        JPanel form = Tema.criarCard();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JPanel formHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        formHeader.setBackground(Tema.CARD);
        formHeader.add(new JLabel(ico("clipboard-list", 14)));
        formHeader.add(Tema.criarLabel("REGISTRAR ATENDIMENTO VETERINÁRIO", Tema.F_LABEL, Tema.TEXT3));
        form.add(formHeader);
        form.add(Box.createVerticalStrut(12));

        Fazenda fa = backend.getFazendaAtiva();
        List<Animal> animais = fa != null
                ? backend.animalService.listarPorFazenda(fa.getId())
                : backend.animalService.listarTodos();
        String[] nomes = animais.stream()
                .map(a -> a.getNome() + " #" + a.getNumeroBrinco()).toArray(String[]::new);
        cAnimalH = nomes.length > 0 ? Tema.criarCombo(nomes) : Tema.criarCombo("Nenhum");
        cDataH  = Tema.criarCampo("dd/mm/aaaa");
        cProcH  = Tema.criarCampo("Ex.: Vacinação, Consulta, Cirurgia...");
        cVetH   = Tema.criarCampo("Nome do veterinário");
        cObsH   = Tema.criarCampo("Observações");

        form.add(Tema.par("ANIMAL", cAnimalH, "DATA (dd/mm/aaaa)", cDataH));
        form.add(Box.createVerticalStrut(8));
        form.add(Tema.par("PROCEDIMENTO", cProcH, "VETERINÁRIO", cVetH));
        form.add(Box.createVerticalStrut(8));
        form.add(Tema.campo("OBSERVAÇÕES", cObsH));
        form.add(Box.createVerticalStrut(14));

        JPanel botoesForm = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botoesForm.setBackground(Tema.CARD);
        botoesForm.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnAdd = Tema.criarBotaoPrimario("REGISTRAR ATENDIMENTO");
        btnAdd.setIcon(ico("plus-circle", 16));
        btnAdd.setIconTextGap(6);
        btnAdd.addActionListener(e -> salvarHistorico(animais));
        botoesForm.add(btnAdd);
        form.add(botoesForm);
        p.add(form, BorderLayout.NORTH);

        // Card tabela histórico
        JPanel card = Tema.criarCard();
        card.setLayout(new BorderLayout(0, 8));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Tema.CARD);

        JPanel topoLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        topoLeft.setBackground(Tema.CARD);
        topoLeft.add(new JLabel(ico("clock", 14)));
        topoLeft.add(Tema.criarLabel("HISTÓRICO VETERINÁRIO", Tema.F_LABEL, Tema.TEXT3));
        topo.add(topoLeft, BorderLayout.WEST);

        JButton btnDel = Tema.criarBotaoPerigo("EXCLUIR");
        btnDel.setIcon(ico("trash-2", 16));
        btnDel.setIconTextGap(6);
        btnDel.addActionListener(e -> excluirHistoricoSelecionado());
        topo.add(btnDel, BorderLayout.EAST);
        card.add(topo, BorderLayout.NORTH);

        String[] cols = {"DATA", "ANIMAL", "PROCEDIMENTO", "VETERINÁRIO", "OBSERVAÇÕES"};
        modelHistorico = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        carregarHistorico();
        JTable tabelaHist = Tema.criarTabela(modelHistorico);
        card.add(Tema.criarScroll(tabelaHist), BorderLayout.CENTER);

        p.add(card, BorderLayout.CENTER);
        return p;
    }

    private void carregarHistorico() {
        if (modelHistorico == null) return;
        modelHistorico.setRowCount(0);
        Fazenda fa = backend.getFazendaAtiva();
        if (fa == null) return;
        for (HistoricoVet h : backend.historicoVetService.listarPorFazenda(fa.getId()))
            modelHistorico.addRow(new Object[]{
                    h.getDataStr(),
                    h.getAnimal() != null ? h.getAnimal().getNome() : "—",
                    h.getProcedimento(),
                    h.getVeterinario() != null ? h.getVeterinario() : "—",
                    h.getObservacoes() != null ? h.getObservacoes() : ""});
    }

    private void salvarHistorico(List<Animal> animais) {
        if (animais.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum animal!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idx = cAnimalH.getSelectedIndex();
        if (idx < 0 || idx >= animais.size()) return;
        Animal animal = animais.get(idx);
        Fazenda fa = backend.getFazendaAtiva();
        if (fa == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma fazenda ativa!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            LocalDate data = parseData(cDataH.getText().trim());
            String proc = cProcH.getText().trim();
            if (proc.isEmpty() || proc.startsWith("Ex.:")) {
                JOptionPane.showMessageDialog(this, "Informe o procedimento!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            HistoricoVet h = new HistoricoVet(animal, fa, data, proc,
                    cVetH.getText().trim(), cObsH.getText().trim());
            backend.historicoVetService.salvar(h);
            LogAtividades.registrar(backend.authService.getUsuarioLogado(),
                    "Registrou atendimento: " + proc + " em " + animal.getNome());
            carregarHistorico();
            cDataH.setText("dd/mm/aaaa");
            cProcH.setText("Ex.: Vacinação, Consulta, Cirurgia...");
            cVetH.setText("Nome do veterinário");
            cObsH.setText("");
            JOptionPane.showMessageDialog(this, "Atendimento registrado!", "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar: verifique a data (dd/mm/aaaa).", "Erro", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void excluirHistoricoSelecionado() {
        JOptionPane.showMessageDialog(this,
                "Selecione um registro na tabela e confirme a exclusão.",
                "Excluir", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Utilitários ───────────────────────────────────────────────────────────
    private LocalDate parseData(String s) {
        if (s.matches("\\d{2}/\\d{2}/\\d{4}")) return LocalDate.parse(s, FMT);
        if (s.matches("\\d{4}-\\d{2}-\\d{2}")) return LocalDate.parse(s);
        throw new IllegalArgumentException("Formato de data inválido: " + s);
    }

    private DefaultTableCellRenderer statusVacinaRenderer() {
        return new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                l.setOpaque(true);
                l.setFont(Tema.F_SMALL);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                String val = v == null ? "" : v.toString();
                if (val.contains("Em dia"))      { l.setBackground(new Color(26, 61, 28));  l.setForeground(Tema.GREEN3); }
                else if (val.contains("Vence"))  { l.setBackground(new Color(61, 46, 10));  l.setForeground(Tema.AMBER);  }
                else if (val.contains("Vencida")){ l.setBackground(new Color(61, 26, 26));  l.setForeground(Tema.RED);    }
                else                             { l.setBackground(Tema.BG3);               l.setForeground(Tema.TEXT2);  }
                return l;
            }
        };
    }
}
