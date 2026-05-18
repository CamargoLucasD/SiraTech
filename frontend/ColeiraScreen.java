package frontend;

import backend.*;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

public class ColeiraScreen extends JPanel {

    private final Backend        backend;
    private final MainFrame      mainFrame;
    private DefaultTableModel    tabelaModel;
    private JTable               tabela;

    // ── helper de ícone ──────────────────────────────────────────────────────
    private static FlatSVGIcon ico(String name, int size) {
        return new FlatSVGIcon("icons/" + name + ".svg", size, size);
    }

    public ColeiraScreen(MainFrame frame, Backend backend) {
        this.mainFrame = frame;
        this.backend   = backend;
        setBackground(Tema.BG);
        setLayout(new BorderLayout());
        add(new NavBar(frame, backend, 2), BorderLayout.NORTH);
        add(criarConteudo(), BorderLayout.CENTER);
    }

    // ── Layout principal ─────────────────────────────────────────────────────

    private JPanel criarConteudo() {
        JTabbedPane abas = Tema.criarAbas();

        // Abas com ícones
        JLabel tabColeiras  = new JLabel("COLEIRAS");
        tabColeiras.setIcon(ico("zap", 14));
        tabColeiras.setIconTextGap(5);

        JLabel tabManut = new JLabel("MANUTENÇÃO");
        tabManut.setIcon(ico("settings", 14));
        tabManut.setIconTextGap(5);

        abas.addTab(null, criarAbaColeiras());
        abas.setTabComponentAt(0, tabColeiras);

        abas.addTab(null, criarAbaManutencao());
        abas.setTabComponentAt(1, tabManut);

        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(Tema.BG);

        // Cabeçalho com ícone
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(Tema.BG);
        h.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));

        JLabel lblTitulo = Tema.criarLabel("GESTÃO DE COLEIRAS GPS", Tema.F_TITLE, Tema.GREENL);
        lblTitulo.setIcon(ico("zap", 20));
        lblTitulo.setIconTextGap(8);
        h.add(lblTitulo, BorderLayout.WEST);

        JPanel hr = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        hr.setBackground(Tema.BG);

        // Botão refresh
        JButton btnRef = Tema.criarBotaoRefresh();
        btnRef.setIcon(ico("refresh-cw", 16));
        btnRef.addActionListener(e -> carregarTabela());
        hr.add(btnRef);

        // Botões de admin com ícones — lógica original preservada
        if (backend.authService.isAdmin()) {
            JButton btnAdd = Tema.criarBotaoPrimario("NOVA COLEIRA");
            btnAdd.setIcon(ico("plus-circle", 16));
            btnAdd.setIconTextGap(6);

            JButton btnEdt = Tema.criarBotaoCyan("EDITAR");
            btnEdt.setIcon(ico("edit", 16));
            btnEdt.setIconTextGap(6);

            JButton btnDel = Tema.criarBotaoPerigo("EXCLUIR");
            btnDel.setIcon(ico("trash-2", 16));
            btnDel.setIconTextGap(6);

            btnAdd.addActionListener(e -> adicionarComSenha());
            btnEdt.addActionListener(e -> editarComSenha());
            btnDel.addActionListener(e -> excluirComSenha());

            hr.add(btnAdd);
            hr.add(btnEdt);
            hr.add(btnDel);
        }

        h.add(hr, BorderLayout.EAST);
        c.add(h,    BorderLayout.NORTH);
        c.add(abas, BorderLayout.CENTER);
        return c;
    }

    // ── Aba Coleiras ─────────────────────────────────────────────────────────

    private JPanel criarAbaColeiras() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        // StatCards
        List<Colar> todos = backend.colarService.listarTodos();
        long ativos   = todos.stream().filter(c -> !c.isDisponivel()).count();
        long batBaixa = backend.colarService.colaresBateriaBaixa(20).size();

        JPanel stats = new JPanel(new GridLayout(1, 3, 10, 0));
        stats.setBackground(Tema.BG);
        stats.add(new StatCard("TOTAL",      String.valueOf(todos.size()), Tema.GREENL, "coleiras"));
        stats.add(new StatCard("EM USO",     String.valueOf(ativos),       Tema.CYAN,   "vinculadas"));
        stats.add(new StatCard("BAT. BAIXA", String.valueOf(batBaixa),     Tema.AMBER,  "<20%"));
        p.add(stats, BorderLayout.NORTH);

        // Card com tabela
        JPanel card = Tema.criarCard();
        card.setLayout(new BorderLayout(0, 8));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Tema.CARD);

        JLabel lblCard = Tema.criarLabel("TODAS AS COLEIRAS", Tema.F_LABEL, Tema.TEXT3);
        lblCard.setIcon(ico("database", 14));
        lblCard.setIconTextGap(6);
        topo.add(lblCard, BorderLayout.WEST);
        topo.add(Tema.criarLabel("2× → detalhes", Tema.F_SMALL, Tema.TEXT3), BorderLayout.EAST);
        card.add(topo, BorderLayout.NORTH);

        // Tabela
        String[] cols = {"ID", "BATERIA", "SINAL", "FREQ.", "FIRMWARE", "STATUS", "ANIMAL"};
        tabelaModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        carregarTabela();
        tabela = Tema.criarTabela(tabelaModel);
        tabela.getColumnModel().getColumn(1).setCellRenderer(batRenderer());
        tabela.getColumnModel().getColumn(5).setCellRenderer(statusColRenderer());

        // Double-click → detalhes — lógica original preservada
        tabela.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) detalhes();
            }
        });
        card.add(Tema.criarScroll(tabela), BorderLayout.CENTER);

        // Botões de operação com ícones
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acoes.setBackground(Tema.CARD);

        JButton bV = Tema.criarBotaoPrimario("VINCULAR A ANIMAL");
        bV.setIcon(ico("users", 16));
        bV.setIconTextGap(6);

        JButton bL = Tema.criarBotaoSecundario("LIBERAR");
        bL.setIcon(ico("rotate-ccw", 16));
        bL.setIconTextGap(6);

        JButton bD = Tema.criarBotaoCyan("DETALHES");
        bD.setIcon(ico("search", 16));
        bD.setIconTextGap(6);

        // Listeners — lógica original preservada
        bV.addActionListener(e -> vincular());
        bL.addActionListener(e -> liberar());
        bD.addActionListener(e -> detalhes());

        acoes.add(bV); acoes.add(bL); acoes.add(bD);
        card.add(acoes, BorderLayout.SOUTH);

        p.add(card, BorderLayout.CENTER);
        return p;
    }

    // ── Aba Manutenção ───────────────────────────────────────────────────────

    private JPanel criarAbaManutencao() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel lblTit = Tema.criarLabel("COLEIRAS COM BATERIA BAIXA (< 20%)", Tema.F_LABEL, Tema.RED);
        lblTit.setIcon(ico("alert-triangle", 14));
        lblTit.setIconTextGap(6);
        p.add(lblTit, BorderLayout.NORTH);

        String[] cols = {"ID", "BATERIA", "SINAL", "ANIMAL VINCULADO"};
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        // lógica original preservada
        for (Colar c : backend.colarService.colaresBateriaBaixa(20)) {
            String an = "—";
            for (Animal a : backend.animalService.listarTodos())
                if (a.getColar() != null && a.getColar().getId().equals(c.getId())) {
                    an = a.getNome(); break;
                }
            m.addRow(new Object[]{c.getId(), c.getBateria() + "%", c.getNivelSinal(), an});
        }
        p.add(Tema.criarScroll(Tema.criarTabela(m)), BorderLayout.CENTER);

        // Aviso com ícone
        JPanel warn = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        warn.setBackground(Tema.CARD);
        warn.setBorder(BorderFactory.createLineBorder(Tema.AMBER, 1));
        JLabel lblWarn = Tema.criarLabel(
                "Coleiras abaixo de 20% devem ser recarregadas em até 48h.", Tema.F_BODY, Tema.AMBER);
        lblWarn.setIcon(ico("alert-triangle", 14));
        lblWarn.setIconTextGap(6);
        warn.add(lblWarn);
        p.add(warn, BorderLayout.SOUTH);
        return p;
    }

    // ── lógica original — não alterada ──────────────────────────────────────

    private void carregarTabela() {
        if (tabelaModel == null) return;
        tabelaModel.setRowCount(0);
        for (Colar c : backend.colarService.listarTodos()) {
            String an = "—";
            for (Animal a : backend.animalService.listarTodos())
                if (a.getColar() != null && a.getColar().getId().equals(c.getId())) {
                    an = a.getNome(); break;
                }
            tabelaModel.addRow(new Object[]{
                c.getId(),
                c.getBateria() + "%",
                c.getNivelSinal(),
                c.getFrequenciaMinutos() + " min",
                c.getFirmware(),
                c.isDisponivel() ? "Disponível" : "Em uso",
                an
            });
        }
    }

    private void detalhes() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma coleira!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        backend.colarService.buscarPorId(tabelaModel.getValueAt(row, 0).toString())
                .ifPresent(c -> new DetalhesColeiraDialog(
                        SwingUtilities.getWindowAncestor(this), c, backend, this));
    }

    private void adicionarComSenha() {
        JPasswordField pf = Tema.criarSenha();
        if (JOptionPane.showConfirmDialog(this, pf,
                "Confirme sua senha para adicionar coleira",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        if (!backend.authService.verificarSenha(new String(pf.getPassword()))) {
            JOptionPane.showMessageDialog(this, "Senha incorreta!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JTextField cId   = Tema.criarCampo("Ex: C-20");
        JTextField cBat  = Tema.criarCampo("100");
        JTextField cFreq = Tema.criarCampo("5");
        JComboBox<String> cSinal = Tema.criarCombo("Forte", "Medio", "Fraco");

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBackground(Tema.BG);
        form.add(Tema.criarLabel("ID:",              Tema.F_SMALL, Tema.TEXT3)); form.add(cId);
        form.add(Tema.criarLabel("BATERIA (%):",     Tema.F_SMALL, Tema.TEXT3)); form.add(cBat);
        form.add(Tema.criarLabel("SINAL:",           Tema.F_SMALL, Tema.TEXT3)); form.add(cSinal);
        form.add(Tema.criarLabel("FREQUÊNCIA (min):", Tema.F_SMALL, Tema.TEXT3)); form.add(cFreq);

        if (JOptionPane.showConfirmDialog(this, form, "Nova Coleira GPS",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;

        String idNovo = cId.getText().trim();
        if (idNovo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O ID da coleira é obrigatório!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int bateria, freq;
        try {
            bateria = Integer.parseInt(cBat.getText().trim());
            freq    = Integer.parseInt(cFreq.getText().trim());
            if (bateria < 0 || bateria > 100) throw new NumberFormatException("Bateria fora do intervalo");
            if (freq <= 0)                     throw new NumberFormatException("Frequência inválida");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Bateria deve ser 0–100 e frequência deve ser um número positivo.",
                "Erro de validação", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Colar nova = new Colar(idNovo, bateria, cSinal.getSelectedItem().toString(), freq);
            backend.colarService.salvar(nova);
            LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Cadastrou coleira: " + nova.getId());
            carregarTabela();
            JOptionPane.showMessageDialog(this,
                "Coleira " + nova.getId() + " cadastrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "ID Duplicado", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarComSenha() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma coleira!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String cid = tabelaModel.getValueAt(row, 0).toString();
        JPasswordField pf = Tema.criarSenha();
        if (JOptionPane.showConfirmDialog(this, pf,
                "Confirme sua senha para editar " + cid,
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        if (!backend.authService.verificarSenha(new String(pf.getPassword()))) {
            JOptionPane.showMessageDialog(this, "Senha incorreta!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        backend.colarService.buscarPorId(cid).ifPresent(c -> {
            JTextField cBat  = Tema.criarCampo(String.valueOf(c.getBateria()));
            JTextField cFreq = Tema.criarCampo(String.valueOf(c.getFrequenciaMinutos()));
            JComboBox<String> cSinal = Tema.criarCombo("Forte", "Medio", "Fraco");
            cSinal.setSelectedItem(c.getNivelSinal());

            JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
            form.setBackground(Tema.BG);
            form.add(Tema.criarLabel("BATERIA (%):",      Tema.F_SMALL, Tema.TEXT3)); form.add(cBat);
            form.add(Tema.criarLabel("SINAL:",            Tema.F_SMALL, Tema.TEXT3)); form.add(cSinal);
            form.add(Tema.criarLabel("FREQUÊNCIA (min):", Tema.F_SMALL, Tema.TEXT3)); form.add(cFreq);

            if (JOptionPane.showConfirmDialog(this, form, "Editar Coleira " + cid,
                    JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
            try {
                int bat  = Integer.parseInt(cBat.getText().trim());
                int freq = Integer.parseInt(cFreq.getText().trim());
                if (bat < 0 || bat > 100 || freq <= 0) throw new NumberFormatException();
                c.setBateria(bat);
                c.setFrequenciaMinutos(freq);
                c.setNivelSinal(cSinal.getSelectedItem().toString());
                backend.colarService.atualizar(c);
                LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Editou coleira: " + cid);
                carregarTabela();
                JOptionPane.showMessageDialog(this,
                    "Coleira " + cid + " atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "Bateria deve ser 0–100 e frequência deve ser positiva.",
                    "Erro de validação", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void excluirComSenha() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma coleira!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String cid = tabelaModel.getValueAt(row, 0).toString();
        JPasswordField pf = Tema.criarSenha();
        if (JOptionPane.showConfirmDialog(this, pf,
                "Confirme sua senha para excluir " + cid,
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        if (!backend.authService.verificarSenha(new String(pf.getPassword()))) {
            JOptionPane.showMessageDialog(this, "Senha incorreta!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int conf = JOptionPane.showConfirmDialog(this,
                "Excluir permanentemente a coleira " + cid + "?\nEsta ação não pode ser desfeita.",
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (conf != JOptionPane.YES_OPTION) return;
        try {
            backend.colarService.excluir(cid, backend.animalService);
            LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Excluiu coleira: " + cid);
            carregarTabela();
            JOptionPane.showMessageDialog(this,
                "Coleira " + cid + " excluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Não é possível excluir", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void vincular() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma coleira!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!"Disponível".equals(tabelaModel.getValueAt(row, 5).toString())) {
            JOptionPane.showMessageDialog(this, "Coleira já está em uso!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String cid = tabelaModel.getValueAt(row, 0).toString();
        List<Animal> animais = backend.animalService.listarAtivos();
        String[] nomes = animais.stream()
                .map(a -> a.getNome() + " #" + a.getNumeroBrinco())
                .toArray(String[]::new);
        if (nomes.length == 0) {
            JOptionPane.showMessageDialog(this, "Nenhum animal ativo disponível!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String esc = (String) JOptionPane.showInputDialog(this,
                "Selecione o animal para vincular à coleira " + cid + ":",
                "Vincular Coleira", JOptionPane.QUESTION_MESSAGE, null, nomes, nomes[0]);
        if (esc != null) {
            int idx = Arrays.asList(nomes).indexOf(esc);
            Animal a = animais.get(idx);
            backend.colarService.vincularAoAnimal(cid, a);
            backend.animalService.atualizar(a);
            LogAtividades.registrar(backend.authService.getUsuarioLogado(),
                    "Vinculou coleira " + cid + " ao animal " + a.getNome());
            carregarTabela();
            JOptionPane.showMessageDialog(this,
                "Coleira " + cid + " vinculada a " + a.getNome() + " com sucesso!",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void liberar() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma coleira!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String cid = tabelaModel.getValueAt(row, 0).toString();
        if ("Disponível".equals(tabelaModel.getValueAt(row, 5).toString())) {
            JOptionPane.showMessageDialog(this, "A coleira já está livre!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int conf = JOptionPane.showConfirmDialog(this,
                "Deseja liberar a coleira " + cid + " do animal?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;
        backend.colarService.liberarColar(cid);
        LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Liberou coleira: " + cid);
        carregarTabela();
        JOptionPane.showMessageDialog(this,
            "Coleira " + cid + " liberada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Renderers — lógica original preservada ───────────────────────────────

    private DefaultTableCellRenderer batRenderer() {
        return new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                l.setOpaque(true);
                try {
                    int b = Integer.parseInt(v.toString().replace("%", "").trim());
                    if      (b <= 20) { l.setBackground(new Color(61, 26, 26)); l.setForeground(Tema.RED);    }
                    else if (b <= 50) { l.setBackground(new Color(61, 46, 10)); l.setForeground(Tema.AMBER);  }
                    else              { l.setBackground(new Color(26, 61, 28)); l.setForeground(Tema.GREEN3); }
                } catch (Exception ignored) {}
                return l;
            }
        };
    }

    private DefaultTableCellRenderer statusColRenderer() {
        return new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                l.setOpaque(true);
                if ("Disponível".equals(v)) { l.setBackground(new Color(26, 61, 28)); l.setForeground(Tema.GREEN3); }
                else                        { l.setBackground(new Color(20, 40, 60)); l.setForeground(Tema.CYAN);   }
                return l;
            }
        };
    }

    // ── refresh externo (usado por DetalhesColeiraDialog) ────────────────────
    public void refresh() {
        carregarTabela();
    }
}
