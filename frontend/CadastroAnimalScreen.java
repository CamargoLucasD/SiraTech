package frontend;

import backend.*;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

public class CadastroAnimalScreen extends JPanel {

    private final Backend   backend;
    private final MainFrame mainFrame;

    private JTextField    campoNome, campoBrinco, campoPeso, campoObs, campoDataNasc;
    private JComboBox<String> comboRaca, comboSexo, comboLote, comboStatus, comboColar, comboFazenda;
    private DefaultTableModel tabelaModel;
    private JTable            tabela;
    private int editandoId = 0;

    // ─── Ícones FlatLaf ───────────────────────────────────────────────────────
    private static FlatSVGIcon ico(String name, int size) {
    try {
        return new FlatSVGIcon("icons/" + name + ".svg", size, size);
    } catch (Exception e) {
        return null;
    }
}

    public CadastroAnimalScreen(MainFrame frame, Backend backend) {
        this.mainFrame = frame;
        this.backend   = backend;
        setBackground(Tema.BG);
        setLayout(new BorderLayout());
        add(new NavBar(frame, backend, 1), BorderLayout.NORTH);
        add(criarConteudo(), BorderLayout.CENTER);
    }

    public void atualizar() { recarregarTabela(); }

    private JPanel criarConteudo() {
        JTabbedPane abas = Tema.criarAbas();
        abas.addTab("CADASTRO & LISTA",  ico("clipboard-list", 14), criarAbaLista());
        abas.addTab("HISTÓRICO DE PESO", ico("trending-up", 14),    criarAbaPeso());
        abas.addTab("FILTROS & BUSCA",   ico("filter", 14),         criarAbaFiltros());

        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(Tema.BG);

        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(Tema.BG);
        h.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));

        // Título com ícone
        JPanel titulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        titulo.setBackground(Tema.BG);
        titulo.add(new JLabel(ico("tag", 20)));
        titulo.add(Tema.criarLabel("GESTÃO DE ANIMAIS", Tema.F_TITLE, Tema.GREENL));
        h.add(titulo, BorderLayout.WEST);

        JPanel hr = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        hr.setBackground(Tema.BG);

        JButton btnRefresh = Tema.criarBotaoRefresh();
        btnRefresh.setIcon(ico("refresh-cw", 14));
        btnRefresh.addActionListener(e -> recarregarTabela());

        JButton btnNovo = Tema.criarBotaoPrimario("NOVO ANIMAL");
        btnNovo.setIcon(ico("plus-circle", 14));
        btnNovo.addActionListener(e -> limpar());

        hr.add(btnRefresh);
        hr.add(btnNovo);
        h.add(hr, BorderLayout.EAST);

        c.add(h, BorderLayout.NORTH);
        c.add(abas, BorderLayout.CENTER);
        return c;
    }

    private JPanel criarAbaLista() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JPanel grade = new JPanel(new GridLayout(1, 2, 12, 0));
        grade.setPreferredSize(new Dimension(0, 700));
        grade.setBackground(Tema.BG);
        grade.add(criarForm());
        grade.add(criarTabela2());
        p.add(grade, BorderLayout.CENTER);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acoes.setBackground(Tema.BG);

        JButton bS = Tema.criarBotaoPrimario("SALVAR");
        bS.setIcon(ico("save", 14));

        JButton bD = Tema.criarBotaoSecundario("DETALHES");
        bD.setIcon(ico("search", 14));

        JButton bE = Tema.criarBotaoCyan("EDITAR");
        bE.setIcon(ico("edit", 14));

        JButton bR = Tema.criarBotaoPerigo("REMOVER");
        bR.setIcon(ico("trash-2", 14));

        JButton bC = Tema.criarBotaoSecundario("CANCELAR");
        bC.setIcon(ico("x", 14));

        JButton bO = Tema.criarBotaoPrimario("EXPORTAR OBSIDIAN");
        bO.setIcon(ico("download", 14));

        bS.addActionListener(e -> salvar());
        bD.addActionListener(e -> detalhes());
        bE.addActionListener(e -> editar());
        bR.addActionListener(e -> remover());
        bC.addActionListener(e -> limpar());
        bO.addActionListener(e -> exportarParaObsidian());

        acoes.add(bS); acoes.add(bD); acoes.add(bE); acoes.add(bR); acoes.add(bC); acoes.add(bO);
        p.add(acoes, BorderLayout.SOUTH);
        return p;
    }

    private JPanel criarForm() {
        JPanel card = Tema.criarCard();
        card.setLayout(new BorderLayout());

        // Painel interno com GridBagLayout para controle total do alinhamento
        JPanel inner = new JPanel(new GridBagLayout());
        inner.setBackground(Tema.CARD);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx   = 0;
        gbc.gridy   = 0;
        gbc.insets  = new Insets(0, 0, 0, 0);

        // -- Cabecalho ------------------------------------------------------
        JPanel secHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        secHeader.setBackground(Tema.CARD);
        secHeader.add(new JLabel(ico("tag", 14)));
        secHeader.add(Tema.criarLabel("DADOS DO ANIMAL", Tema.F_LABEL, Tema.TEXT3));
        inner.add(secHeader, gbc);

        // -- NOME / No BRINCO -----------------------------------------------
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 0, 0);
        campoNome   = Tema.criarCampo("");
        campoBrinco = Tema.criarCampo("");
        inner.add(Tema.par("NOME", campoNome, "Nº BRINCO", campoBrinco), gbc);

        // -- RACA / SEXO ----------------------------------------------------
        gbc.gridy++;
        gbc.insets = new Insets(6, 0, 0, 0);
        comboRaca = Tema.criarCombo("Nelore","Angus","Gir","Brahman","Senepol","Tabapuã","Simmental","Hereford");
        comboSexo = Tema.criarCombo("Femea","Macho");
        inner.add(Tema.par("RAÇA", comboRaca, "SEXO", comboSexo), gbc);

        // -- PESO / DATA NASC. ----------------------------------------------
        gbc.gridy++;
        campoPeso     = Tema.criarCampo("");
        campoDataNasc = Tema.criarCampo("dd/mm/aaaa");
        inner.add(Tema.par("PESO (KG)", campoPeso, "DATA NASC.", campoDataNasc), gbc);

        // -- FAZENDA / LOTE -------------------------------------------------
        gbc.gridy++;
        List<Fazenda> fazendas = backend.fazendaService.listarTodas();
        String[] nomFaz = fazendas.stream().map(Fazenda::getNome).toArray(String[]::new);
        comboFazenda = nomFaz.length > 0 ? Tema.criarCombo(nomFaz) : Tema.criarCombo("Nenhuma fazenda");

        Fazenda fa = backend.getFazendaAtiva();
        if (fa != null) {
            for (int i = 0; i < fazendas.size(); i++) {
                if (fazendas.get(i).getId() == fa.getId()) {
                    comboFazenda.setSelectedIndex(i);
                    break;
                }
            }
        }
        comboLote = Tema.criarCombo("Lote A","Lote B","Lote C");
        inner.add(Tema.par("FAZENDA", comboFazenda, "LOTE", comboLote), gbc);

        // -- STATUS / COLAR GPS ---------------------------------------------
        gbc.gridy++;
        comboStatus = Tema.criarCombo("Ativo","Vendido","Abatido");
        List<Colar> disp = backend.colarService.listarDisponiveis();
        String[] opts = new String[disp.size() + 1];
        opts[0] = "Sem brinco";
        for (int i = 0; i < disp.size(); i++)
            opts[i+1] = disp.get(i).getId() + " | " + disp.get(i).getBateria() + "%";
        comboColar = Tema.criarCombo(opts);
        inner.add(Tema.par("STATUS", comboStatus, "BRINCO", comboColar), gbc);

        // -- OBSERVACOES ----------------------------------------------------
        gbc.gridy++;
        gbc.insets = new Insets(6, 0, 2, 0);
        inner.add(Tema.criarLabel("OBSERVAÇÕES", Tema.F_SMALL, Tema.TEXT3), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        campoObs = Tema.criarCampo("");
        inner.add(campoObs, gbc);

        // Empurra tudo para o topo com glue no final
        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.fill    = GridBagConstraints.VERTICAL;
        inner.add(Box.createVerticalGlue(), gbc);

        card.add(inner, BorderLayout.CENTER);
        card.setPreferredSize(new Dimension(0, 650));
        card.setMinimumSize(new Dimension(0, 600));
        return card;
    }

    private JPanel criarTabela2() {
        JPanel card = Tema.criarCard();
        card.setLayout(new BorderLayout(0, 6));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Tema.CARD);

        JPanel topoLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        topoLeft.setBackground(Tema.CARD);
        topoLeft.add(new JLabel(ico("list", 14)));
        topoLeft.add(Tema.criarLabel("ANIMAIS CADASTRADOS", Tema.F_LABEL, Tema.TEXT3));
        topo.add(topoLeft, BorderLayout.WEST);
        topo.add(Tema.criarLabel("2× → detalhes", Tema.F_SMALL, Tema.TEXT3), BorderLayout.EAST);
        card.add(topo, BorderLayout.NORTH);

        String[] cols = {"NOME","BRINCO","RAÇA","LOTE","BRINCO","STATUS"};
        tabelaModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        recarregarTabela();
        tabela = Tema.criarTabela(tabelaModel);
        tabela.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) detalhes();
            }
        });
        card.add(Tema.criarScroll(tabela), BorderLayout.CENTER);
        return card;
    }

    private void recarregarTabela() {
        if (tabelaModel == null) return;
        tabelaModel.setRowCount(0);
        Fazenda fa = backend.getFazendaAtiva();
        List<Animal> lista = fa != null
                ? backend.animalService.listarPorFazenda(fa.getId())
                : backend.animalService.listarTodos();
        for (Animal a : lista)
            tabelaModel.addRow(new Object[]{
                    a.getNome(), a.getNumeroBrinco(), a.getRaca(), a.getLote(),
                    a.getColar() != null ? a.getColar().getId() : "—", a.getStatus()});
    }

    private JPanel criarAbaPeso() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JPanel secHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        secHeader.setBackground(Tema.BG);
        secHeader.add(new JLabel(ico("trending-up", 14)));
        secHeader.add(Tema.criarLabel("HISTÓRICO DE PESO", Tema.F_LABEL, Tema.TEXT3));
        p.add(secHeader, BorderLayout.NORTH);

        String[] cols = {"ANIMAL", "DATA", "PESO (KG)", "VARIAÇÃO"};
        DefaultTableModel mp = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        p.add(Tema.criarScroll(Tema.criarTabela(mp)), BorderLayout.CENTER);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acoes.setBackground(Tema.BG);
        JButton btnAdd = Tema.criarBotaoPrimario("REGISTRAR PESO");
        btnAdd.setIcon(ico("plus", 14));
        acoes.add(btnAdd);
        p.add(acoes, BorderLayout.SOUTH);
        return p;
    }

    private JPanel criarAbaFiltros() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        filtros.setBackground(Tema.BG);
        filtros.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.BORDER, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        filtros.add(Tema.criarLabel("LOTE:",  Tema.F_SMALL, Tema.TEXT3));
        JComboBox<String> fLote   = Tema.criarCombo("Todos","Lote A","Lote B","Lote C");
        filtros.add(fLote);
        filtros.add(Tema.criarLabel("STATUS:",Tema.F_SMALL, Tema.TEXT3));
        JComboBox<String> fStatus = Tema.criarCombo("Todos","Ativo","Vendido","Abatido");
        filtros.add(fStatus);
        filtros.add(Tema.criarLabel("RAÇA:",  Tema.F_SMALL, Tema.TEXT3));
        JComboBox<String> fRaca   = Tema.criarCombo("Todas","Nelore","Angus","Gir","Brahman","Senepol");
        filtros.add(fRaca);
        JTextField busca = Tema.criarCampo("Buscar...");
        busca.setPreferredSize(new Dimension(180, 32));
        filtros.add(busca);
        p.add(filtros, BorderLayout.NORTH);

        String[] cols = {"NOME","BRINCO","RAÇA","LOTE","PESO","STATUS"};
        DefaultTableModel mf = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        Fazenda fa = backend.getFazendaAtiva();
        List<Animal> todos = fa != null
                ? backend.animalService.listarPorFazenda(fa.getId())
                : backend.animalService.listarTodos();
        Runnable carregar = () -> {
            mf.setRowCount(0);
            String lo = fLote.getSelectedItem().toString();
            String st = fStatus.getSelectedItem().toString();
            String ra = fRaca.getSelectedItem().toString();
            String bu = busca.getText().trim().toLowerCase();
            for (Animal a : todos) {
                if (!"Todos".equals(lo) && !lo.equals(a.getLote())) continue;
                if (!"Todos".equals(st) && !st.equals(a.getStatus())) continue;
                if (!"Todas".equals(ra) && !ra.equals(a.getRaca())) continue;
                if (!bu.isEmpty() && !bu.equals("buscar...")
                        && !a.getNome().toLowerCase().contains(bu)
                        && !a.getNumeroBrinco().toLowerCase().contains(bu)) continue;
                mf.addRow(new Object[]{a.getNome(), a.getNumeroBrinco(), a.getRaca(),
                        a.getLote(), a.getPeso() > 0 ? a.getPeso() + " kg" : "—", a.getStatus()});
            }
        };
        carregar.run();
        fLote.addActionListener(e -> carregar.run());
        fStatus.addActionListener(e -> carregar.run());
        fRaca.addActionListener(e -> carregar.run());
        busca.addActionListener(e -> carregar.run());

        JButton csv = Tema.criarBotaoSecundario("EXPORTAR CSV");
        csv.setIcon(ico("download", 14));
        csv.addActionListener(e -> exportarCSV(todos));
        JPanel s = new JPanel(new FlowLayout(FlowLayout.LEFT));
        s.setBackground(Tema.BG);
        s.add(csv);

        p.add(Tema.criarScroll(Tema.criarTabela(mf)), BorderLayout.CENTER);
        p.add(s, BorderLayout.SOUTH);
        return p;
    }

    private void salvar() {
        String nome   = campoNome.getText().trim();
        String brinco = campoBrinco.getText().trim();
        if (nome.isEmpty() || brinco.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e brinco são obrigatórios!", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Animal a = editandoId > 0
                ? backend.animalService.buscarPorId(editandoId).orElse(new Animal())
                : new Animal();

        a.setNome(nome);
        a.setNumeroBrinco(brinco);
        a.setRaca(comboRaca.getSelectedItem().toString());
        a.setSexo(comboSexo.getSelectedItem().toString());
        a.setLote(comboLote.getSelectedItem().toString());
        a.setStatus(comboStatus.getSelectedItem().toString());
        a.setObservacoes(campoObs.getText().trim());
        try { a.setPeso(Double.parseDouble(campoPeso.getText().trim())); } catch (Exception ignored) {}

        List<Fazenda> fazendas = backend.fazendaService.listarTodas();
        int fIdx = comboFazenda.getSelectedIndex();
        if (fIdx >= 0 && fIdx < fazendas.size()) {
            Fazenda f = fazendas.get(fIdx);
            a.setFazendaId(f.getId());
            a.setFazendaNome(f.getNome());
        }

        String dataStr = campoDataNasc.getText().trim();
        if (!dataStr.isEmpty() && !dataStr.equals("dd/mm/aaaa")) {
            try {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                a.setDataNascimento(java.time.LocalDate.parse(dataStr, fmt).atStartOfDay());
            } catch (Exception ignored) {}
        }

        if (editandoId > 0) {
            backend.animalService.atualizar(a);
            LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Editou animal: " + a.getNome());
            JOptionPane.showMessageDialog(this, "Animal " + a.getNome() + " atualizado!", "OK",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            backend.animalService.cadastrar(a);
            LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Cadastrou animal: " + a.getNome());
            JOptionPane.showMessageDialog(this, "Animal " + a.getNome() + " cadastrado!", "OK",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        editandoId = 0;
        recarregarTabela();
        limpar();
    }

    private void detalhes() {
        int row = tabela.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um animal!", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
        backend.animalService.buscarPorBrinco(tabelaModel.getValueAt(row, 1).toString())
                .ifPresent(a -> new DetalhesAnimalDialog(SwingUtilities.getWindowAncestor(this), a, backend));
    }

    private void editar() {
        int row = tabela.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um animal!", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
        backend.animalService.buscarPorBrinco(tabelaModel.getValueAt(row, 1).toString()).ifPresent(a -> {
            editandoId = a.getId();
            campoNome.setText(a.getNome());
            campoBrinco.setText(a.getNumeroBrinco());
            campoPeso.setText(String.valueOf(a.getPeso()));
            campoObs.setText(a.getObservacoes() != null ? a.getObservacoes() : "");
            if (a.getLote()   != null) comboLote.setSelectedItem(a.getLote());
            if (a.getStatus() != null) comboStatus.setSelectedItem(a.getStatus());
            if (a.getRaca()   != null) comboRaca.setSelectedItem(a.getRaca());
            if (a.getSexo()   != null) comboSexo.setSelectedItem(a.getSexo());
            if (a.getDataNascimento() != null)
                campoDataNasc.setText(a.getDataNascimento().toLocalDate()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        });
    }

    private void remover() {
        int row = tabela.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um animal!", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
        String nome = tabelaModel.getValueAt(row, 0).toString();
        if (JOptionPane.showConfirmDialog(this, "Remover " + nome + "?", "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            backend.animalService.buscarPorBrinco(tabelaModel.getValueAt(row, 1).toString())
                    .ifPresent(a -> {
                        backend.animalService.remover(a.getId());
                        LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Removeu: " + a.getNome());
                        tabelaModel.removeRow(row);
                    });
        }
    }

    private void limpar() {
        editandoId = 0;
        campoNome.setText(""); campoBrinco.setText("");
        campoPeso.setText(""); campoObs.setText("");
        campoDataNasc.setText("dd/mm/aaaa");
    }

    private void exportarCSV(List<Animal> animais) {
        try {
            String n = "animais_" + System.currentTimeMillis() + ".csv";
            FileWriter fw = new FileWriter(n);
            fw.write("Nome,Brinco,Raca,Lote,Peso,Status\n");
            for (Animal a : animais)
                fw.write(a.getNome()+","+a.getNumeroBrinco()+","+a.getRaca()+","
                        +a.getLote()+","+a.getPeso()+","+a.getStatus()+"\n");
            fw.close();
            JOptionPane.showMessageDialog(this, "CSV salvo: " + n, "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportarParaObsidian() {

    int row = tabela.getSelectedRow();

    if (row < 0) {
        JOptionPane.showMessageDialog(
                this,
                "Selecione um animal!",
                "Aviso",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    String brinco = tabelaModel.getValueAt(row, 1).toString();

    backend.animalService.buscarPorBrinco(brinco).ifPresent(a -> {

        ObsidianExportService.exportarAnimal(
                a.getId(),
                a.getNome(),
                a.getStatus()
        );

        JOptionPane.showMessageDialog(
                this,
                "Exportado para Obsidian!",
                "OK",
                JOptionPane.INFORMATION_MESSAGE
        );
    });
  }
}
