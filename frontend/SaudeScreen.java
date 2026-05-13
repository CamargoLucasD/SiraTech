package frontend;

import backend.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

public class SaudeScreen extends JPanel {

    private final Backend   backend;
    private final MainFrame mainFrame;

    // Formulário de vacinação (compartilhado para edição)
    private JComboBox<String> cAnimalV;
    private JComboBox<String> cVacinaV;
    private JTextField        cDataV, cProxV, cVetV, cObsV;
    private DefaultTableModel modelVacinas;
    private JTable            tabelaVacinas;
    private int               vacinaEditandoId = 0; // 0 = novo

    // Formulário de histórico vet
    private JComboBox<String> cAnimalH;
    private JTextField        cDataH, cProcH, cVetH, cObsH;
    private DefaultTableModel modelHistorico;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
        abas.addTab("💉 VACINAÇÃO",      criarAbaVacinas());
        abas.addTab("📋 HISTÓRICO VET.", criarAbaHistoricoVet());

        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(Tema.BG);

        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(Tema.BG);
        h.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));
        h.add(Tema.criarLabel("SAÚDE DO REBANHO", Tema.F_TITLE, Tema.GREENL), BorderLayout.WEST);

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

    // ─── Aba Vacinação ────────────────────────────────────────────────────────
    private JPanel criarAbaVacinas() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        // Formulário de registro/edição
        JPanel form = Tema.criarCard();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.add(Tema.criarLabel("◈ REGISTRAR / EDITAR VACINAÇÃO", Tema.F_LABEL, Tema.TEXT3));
        form.add(Box.createVerticalStrut(10));

        Fazenda fa = backend.getFazendaAtiva();
        List<Animal> animais = fa != null
                ? backend.animalService.listarPorFazenda(fa.getId())
                : backend.animalService.listarTodos();
        String[] nomes = animais.stream().map(a -> a.getNome() + " #" + a.getNumeroBrinco()).toArray(String[]::new);
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
        form.add(Box.createVerticalStrut(12));

        JPanel botoesFrm = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botoesFrm.setBackground(Tema.CARD);
        JButton btnReg = Tema.criarBotaoPrimario("💉 SALVAR VACINAÇÃO");
        JButton btnLimpar = Tema.criarBotaoSecundario("✕ LIMPAR");
        btnReg.addActionListener(e -> salvarVacina(animais));
        btnLimpar.addActionListener(e -> limparFormVacina());
        botoesFrm.add(btnReg);
        botoesFrm.add(btnLimpar);
        form.add(botoesFrm);
        p.add(form, BorderLayout.NORTH);

        // Tabela de vacinações com alertas automáticos
        JPanel card2 = Tema.criarCard();
        card2.setLayout(new BorderLayout(0, 8));
        JPanel topo2 = new JPanel(new BorderLayout());
        topo2.setBackground(Tema.CARD);
        topo2.add(Tema.criarLabel("◈ VACINAÇÕES DA FAZENDA", Tema.F_LABEL, Tema.TEXT3), BorderLayout.WEST);

        JPanel btnsDir = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnsDir.setBackground(Tema.CARD);
        JButton btnEdit = Tema.criarBotaoCyan("✎ EDITAR");
        JButton btnDel  = Tema.criarBotaoPerigo("✕ EXCLUIR");
        btnEdit.addActionListener(e -> editarVacinaSelecionada(animais));
        btnDel.addActionListener(e -> excluirVacinaSelecionada());
        btnsDir.add(btnEdit); btnsDir.add(btnDel);
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
            JOptionPane.showMessageDialog(this, "Selecione uma fazenda ativa antes de registrar!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            LocalDate dataAplic = parseData(cDataV.getText().trim());
            LocalDate proxDose  = parseData(cProxV.getText().trim());

            Vacina v;
            if (vacinaEditandoId > 0) {
                // Editar existente — busca pelo id
                List<Vacina> todasAnim = backend.vacinaService.listarPorAnimal(animal.getId());
                Optional<Vacina> opt = todasAnim.stream().filter(x -> x.getId() == vacinaEditandoId).findFirst();
                if (opt.isEmpty()) { JOptionPane.showMessageDialog(this, "Vacinação não encontrada!", "Erro", JOptionPane.ERROR_MESSAGE); return; }
                v = opt.get();
                v.setTipoVacina(cVacinaV.getSelectedItem().toString());
                v.setDataAplicacao(dataAplic);
                v.setProximaDose(proxDose);
                v.setVeterinario(cVetV.getText().trim());
                v.setObservacoes(cObsV.getText().trim());
                v.calcularStatus();
            } else {
                v = new Vacina(animal, fa,
                        cVacinaV.getSelectedItem().toString(),
                        dataAplic, proxDose,
                        cVetV.getText().trim(),
                        cObsV.getText().trim());
            }
            backend.vacinaService.salvar(v);
            LogAtividades.registrar(backend.authService.getUsuarioLogado(),
                    "Registrou vacinação: " + v.getTipoVacina() + " em " + animal.getNome());
            carregarVacinas();
            limparFormVacina();
            JOptionPane.showMessageDialog(this, "Vacinação salva com sucesso!", "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: verifique as datas (dd/mm/aaaa).", "Erro", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void editarVacinaSelecionada(List<Animal> animais) {
        int row = tabelaVacinas.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione uma vacinação!", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
        Fazenda fa = backend.getFazendaAtiva();
        if (fa == null) return;
        List<Vacina> lista = backend.vacinaService.listarPorFazenda(fa.getId());
        if (row >= lista.size()) return;
        Vacina v = lista.get(row);
        vacinaEditandoId = v.getId();

        // Preenche formulário
        String nomeAnimal = v.getAnimal() != null ? v.getAnimal().getNome() + " #" + v.getAnimal().getNumeroBrinco() : "";
        for (int i = 0; i < cAnimalV.getItemCount(); i++)
            if (cAnimalV.getItemAt(i).startsWith(v.getAnimal() != null ? v.getAnimal().getNome() : ""))
                { cAnimalV.setSelectedIndex(i); break; }
        for (int i = 0; i < cVacinaV.getItemCount(); i++)
            if (cVacinaV.getItemAt(i).equals(v.getTipoVacina()))
                { cVacinaV.setSelectedIndex(i); break; }
        cDataV.setText(v.getDataAplicacaoStr());
        cProxV.setText(v.getProximaDoseStr());
        cVetV.setText(v.getVeterinario() != null ? v.getVeterinario() : "");
        cObsV.setText(v.getObservacoes() != null ? v.getObservacoes() : "");

        JOptionPane.showMessageDialog(this,
                "Formulário preenchido com os dados da vacinação selecionada.\nFaça as alterações e clique em SALVAR.",
                "Editando vacinação", JOptionPane.INFORMATION_MESSAGE);
    }

    private void excluirVacinaSelecionada() {
        int row = tabelaVacinas.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione uma vacinação!", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
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

    // ─── Aba Histórico Veterinário ────────────────────────────────────────────
    private JPanel criarAbaHistoricoVet() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        // Formulário rápido
        JPanel form = Tema.criarCard();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.add(Tema.criarLabel("◈ REGISTRAR ATENDIMENTO VETERINÁRIO", Tema.F_LABEL, Tema.TEXT3));
        form.add(Box.createVerticalStrut(10));

        Fazenda fa = backend.getFazendaAtiva();
        List<Animal> animais = fa != null
                ? backend.animalService.listarPorFazenda(fa.getId())
                : backend.animalService.listarTodos();
        String[] nomes = animais.stream().map(a -> a.getNome() + " #" + a.getNumeroBrinco()).toArray(String[]::new);
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
        form.add(Box.createVerticalStrut(12));

        JButton btnAdd = Tema.criarBotaoPrimario("+ REGISTRAR ATENDIMENTO");
        btnAdd.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnAdd.addActionListener(e -> salvarHistorico(animais));
        form.add(btnAdd);
        p.add(form, BorderLayout.NORTH);

        // Tabela de histórico
        JPanel card = Tema.criarCard();
        card.setLayout(new BorderLayout(0, 8));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Tema.CARD);
        topo.add(Tema.criarLabel("◈ HISTÓRICO VETERINÁRIO", Tema.F_LABEL, Tema.TEXT3), BorderLayout.WEST);
        JButton btnDel = Tema.criarBotaoPerigo("✕ EXCLUIR");
        btnDel.addActionListener(e -> excluirHistoricoSelecionado());
        topo.add(btnDel, BorderLayout.EAST);
        card.add(topo, BorderLayout.NORTH);

        String[] cols = {"DATA", "ANIMAL", "PROCEDIMENTO", "VETERINÁRIO", "OBSERVAÇÕES"};
        modelHistorico = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        carregarHistorico();
        JTable tabela = Tema.criarTabela(modelHistorico);
        card.add(Tema.criarScroll(tabela), BorderLayout.CENTER);
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
        if (animais.isEmpty()) { JOptionPane.showMessageDialog(this, "Nenhum animal!", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
        int idx = cAnimalH.getSelectedIndex();
        if (idx < 0 || idx >= animais.size()) return;
        Animal animal = animais.get(idx);
        Fazenda fa = backend.getFazendaAtiva();
        if (fa == null) { JOptionPane.showMessageDialog(this, "Selecione uma fazenda ativa!", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
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
            JOptionPane.showMessageDialog(this, "Erro ao salvar: verifique a data (dd/mm/aaaa).", "Erro", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void excluirHistoricoSelecionado() {
        // Precisamos da referência à tabela — como está dentro da aba, usamos modelHistorico
        // e buscamos o item correspondente no banco
        JOptionPane.showMessageDialog(this,
                "Selecione um registro na tabela e confirme a exclusão.",
                "Excluir", JOptionPane.INFORMATION_MESSAGE);
        // Implementação real: refatorar para guardar referência à JTable de histórico
    }

    // ─── Utilidades ───────────────────────────────────────────────────────────
    private LocalDate parseData(String s) {
        // Aceita dd/mm/aaaa ou aaaa-mm-dd
        if (s.matches("\\d{2}/\\d{2}/\\d{4}")) {
            return LocalDate.parse(s, FMT);
        } else if (s.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return LocalDate.parse(s);
        }
        throw new IllegalArgumentException("Formato de data inválido: " + s);
    }

    private DefaultTableCellRenderer statusVacinaRenderer() {
        return new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                l.setOpaque(true);
                l.setFont(Tema.F_SMALL);
                String val = v == null ? "" : v.toString();
                if (val.contains("Em dia"))     { l.setBackground(new Color(26, 61, 28));  l.setForeground(Tema.GREEN3); }
                else if (val.contains("Vence")) { l.setBackground(new Color(61, 46, 10));  l.setForeground(Tema.AMBER); }
                else if (val.contains("Vencida")) { l.setBackground(new Color(61, 26, 26)); l.setForeground(Tema.RED); }
                else                             { l.setBackground(Tema.BG3);               l.setForeground(Tema.TEXT2); }
                return l;
            }
        };
    }
}
