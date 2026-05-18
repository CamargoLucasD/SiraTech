package frontend;

import backend.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

public class ColeiraScreen extends JPanel {

    private final Backend   backend;
    private final MainFrame mainFrame;
    private DefaultTableModel tabelaModel;
    private JTable            tabela;

    public ColeiraScreen(MainFrame frame, Backend backend) {
        this.mainFrame = frame;
        this.backend   = backend;
        setBackground(Tema.BG);
        setLayout(new BorderLayout());
        add(new NavBar(frame, backend, 2), BorderLayout.NORTH);
        add(criarConteudo(), BorderLayout.CENTER);
    }

    private JPanel criarConteudo() {
        JTabbedPane abas = Tema.criarAbas();
        abas.addTab("📡 COLEIRAS",   criarAbaColeiras());
        abas.addTab("🔧 MANUTENÇÃO", criarAbaManutencao());

        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(Tema.BG);

        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(Tema.BG);
        h.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));
        h.add(Tema.criarLabel("GESTÃO DE COLEIRAS GPS", Tema.F_TITLE, Tema.GREENL), BorderLayout.WEST);

        JPanel hr = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        hr.setBackground(Tema.BG);
        JButton btnRef = Tema.criarBotaoRefresh();
        btnRef.addActionListener(e -> carregarTabela());
        hr.add(btnRef);

        if (backend.authService.isAdmin()) {
            JButton btnAdd = Tema.criarBotaoPrimario("+ NOVA COLEIRA");
            JButton btnDel = Tema.criarBotaoPerigo("✕ EXCLUIR");
            JButton btnEdt = Tema.criarBotaoCyan("✎ EDITAR");
            btnAdd.addActionListener(e -> adicionarComSenha());
            btnDel.addActionListener(e -> excluirComSenha());
            btnEdt.addActionListener(e -> editarComSenha());
            hr.add(btnAdd); hr.add(btnEdt); hr.add(btnDel);
        }
        h.add(hr, BorderLayout.EAST);
        c.add(h, BorderLayout.NORTH);
        c.add(abas, BorderLayout.CENTER);
        return c;
    }

    private JPanel criarAbaColeiras() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        List<Colar> todos    = backend.colarService.listarTodos();
        long ativos          = todos.stream().filter(c -> !c.isDisponivel()).count();
        long batBaixa        = backend.colarService.colaresBateriaBaixa(20).size();

        JPanel stats = new JPanel(new GridLayout(1, 3, 10, 0));
        stats.setBackground(Tema.BG);
        stats.add(new StatCard("TOTAL",     String.valueOf(todos.size()), Tema.GREENL, "coleiras"));
        stats.add(new StatCard("EM USO",    String.valueOf(ativos),       Tema.CYAN,   "vinculadas"));
        stats.add(new StatCard("BAT. BAIXA",String.valueOf(batBaixa),     Tema.AMBER,  "<20%"));
        p.add(stats, BorderLayout.NORTH);

        JPanel card = Tema.criarCard();
        card.setLayout(new BorderLayout(0, 8));
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Tema.CARD);
        topo.add(Tema.criarLabel("◈ TODAS AS COLEIRAS", Tema.F_LABEL, Tema.TEXT3), BorderLayout.WEST);
        topo.add(Tema.criarLabel("2× → detalhes", Tema.F_SMALL, Tema.TEXT3), BorderLayout.EAST);
        card.add(topo, BorderLayout.NORTH);

        String[] cols = {"ID","BATERIA","SINAL","FREQ.","FIRMWARE","STATUS","ANIMAL"};
        tabelaModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        carregarTabela();
        tabela = Tema.criarTabela(tabelaModel);
        tabela.getColumnModel().getColumn(1).setCellRenderer(batRenderer());
        tabela.getColumnModel().getColumn(5).setCellRenderer(statusColRenderer());
        tabela.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) detalhes();
            }
        });
        card.add(Tema.criarScroll(tabela), BorderLayout.CENTER);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acoes.setBackground(Tema.CARD);
        JButton bV = Tema.criarBotaoPrimario("VINCULAR A ANIMAL");
        JButton bL = Tema.criarBotaoSecundario("LIBERAR");
        JButton bD = Tema.criarBotaoCyan("DETALHES");
        bV.addActionListener(e -> vincular());
        bL.addActionListener(e -> liberar());
        bD.addActionListener(e -> detalhes());
        acoes.add(bV); acoes.add(bL); acoes.add(bD);
        card.add(acoes, BorderLayout.SOUTH);
        p.add(card, BorderLayout.CENTER);
        return p;
    }

    private JPanel criarAbaManutencao() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        p.add(Tema.criarLabel("◈ COLEIRAS COM BATERIA BAIXA (< 20%)", Tema.F_LABEL, Tema.RED), BorderLayout.NORTH);

        String[] cols = {"ID","BATERIA","SINAL","ANIMAL VINCULADO"};
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Colar c : backend.colarService.colaresBateriaBaixa(20)) {
            String an = "—";
            for (Animal a : backend.animalService.listarTodos())
                if (a.getColar() != null && a.getColar().getId().equals(c.getId())) { an = a.getNome(); break; }
            m.addRow(new Object[]{c.getId(), c.getBateria() + "%", c.getNivelSinal(), an});
        }
        p.add(Tema.criarScroll(Tema.criarTabela(m)), BorderLayout.CENTER);

        JPanel warn = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        warn.setBackground(Tema.CARD);
        warn.setBorder(BorderFactory.createLineBorder(Tema.AMBER, 1));
        warn.add(Tema.criarLabel("⚠ Coleiras abaixo de 20% devem ser recarregadas em até 48h.",
                Tema.F_BODY, Tema.AMBER));
        p.add(warn, BorderLayout.SOUTH);
        return p;
    }

    private void carregarTabela() {
        if (tabelaModel == null) return;
        tabelaModel.setRowCount(0);
        for (Colar c : backend.colarService.listarTodos()) {
            String an = "—";
            for (Animal a : backend.animalService.listarTodos())
                if (a.getColar() != null && a.getColar().getId().equals(c.getId())) { an = a.getNome(); break; }
            tabelaModel.addRow(new Object[]{
                    c.getId(), c.getBateria() + "%", c.getNivelSinal(),
                    c.getFrequenciaMinutos() + "min", c.getFirmware(),
                    c.isDisponivel() ? "Disponível" : "Em uso", an});
        }
    }

    private void detalhes() {
        int row = tabela.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione uma coleira!", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
        backend.colarService.buscarPorId(tabelaModel.getValueAt(row, 0).toString())
                .ifPresent(c -> new DetalhesColeiraDialog(SwingUtilities.getWindowAncestor(this), c, backend));
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
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBackground(Tema.BG);
        JTextField cId   = Tema.criarCampo("Ex: C-20");
        JTextField cBat  = Tema.criarCampo("100");
        JTextField cFreq = Tema.criarCampo("5");
        JComboBox<String> cSinal = Tema.criarCombo("Forte","Medio","Fraco");
        form.add(Tema.criarLabel("ID:",                Tema.F_SMALL, Tema.TEXT3)); form.add(cId);
        form.add(Tema.criarLabel("BATERIA (%):",       Tema.F_SMALL, Tema.TEXT3)); form.add(cBat);
        form.add(Tema.criarLabel("SINAL:",             Tema.F_SMALL, Tema.TEXT3)); form.add(cSinal);
        form.add(Tema.criarLabel("FREQUÊNCIA (min):",  Tema.F_SMALL, Tema.TEXT3)); form.add(cFreq);
        if (JOptionPane.showConfirmDialog(this, form, "Nova Coleira GPS",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                Colar nova = new Colar(cId.getText().trim(),
                        Integer.parseInt(cBat.getText().trim()),
                        cSinal.getSelectedItem().toString(),
                        Integer.parseInt(cFreq.getText().trim()));
                try (org.hibernate.Session s = HibernateUtil.getSessionFactory().openSession()) {
                    s.beginTransaction(); s.persist(nova); s.getTransaction().commit();
                }
                LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Cadastrou coleira: " + nova.getId());
                carregarTabela();
                JOptionPane.showMessageDialog(this, "Coleira cadastrada!", "OK", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarComSenha() {
        int row = tabela.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione uma coleira!", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
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
            JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
            form.setBackground(Tema.BG);
            JTextField cBat  = Tema.criarCampo(String.valueOf(c.getBateria()));
            JTextField cFreq = Tema.criarCampo(String.valueOf(c.getFrequenciaMinutos()));
            JComboBox<String> cSinal = Tema.criarCombo("Forte","Medio","Fraco");
            cSinal.setSelectedItem(c.getNivelSinal());
            form.add(Tema.criarLabel("BATERIA (%):",      Tema.F_SMALL, Tema.TEXT3)); form.add(cBat);
            form.add(Tema.criarLabel("SINAL:",            Tema.F_SMALL, Tema.TEXT3)); form.add(cSinal);
            form.add(Tema.criarLabel("FREQUÊNCIA (min):", Tema.F_SMALL, Tema.TEXT3)); form.add(cFreq);
            if (JOptionPane.showConfirmDialog(this, form, "Editar Coleira " + cid,
                    JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    c.setBateria(Integer.parseInt(cBat.getText().trim()));
                    c.setFrequenciaMinutos(Integer.parseInt(cFreq.getText().trim()));
                    c.setNivelSinal(cSinal.getSelectedItem().toString());
                    try (org.hibernate.Session s = HibernateUtil.getSessionFactory().openSession()) {
                        s.beginTransaction(); s.merge(c); s.getTransaction().commit();
                    }
                    LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Editou coleira: " + cid);
                    carregarTabela();
                    JOptionPane.showMessageDialog(this, "Coleira atualizada!", "OK", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void excluirComSenha() {
        int row = tabela.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione uma coleira!", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
        String cid = tabelaModel.getValueAt(row, 0).toString();

        JPasswordField pf = Tema.criarSenha();
        if (JOptionPane.showConfirmDialog(this, pf,
                "Confirme sua senha para excluir " + cid,
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        if (!backend.authService.verificarSenha(new String(pf.getPassword()))) {
            JOptionPane.showMessageDialog(this, "Senha incorreta!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this,
                "Excluir coleira " + cid + " permanentemente?", "Confirmar",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            try (org.hibernate.Session s = HibernateUtil.getSessionFactory().openSession()) {
                s.beginTransaction();
                Colar c = s.get(Colar.class, cid);
                if (c != null) s.remove(c);
                s.getTransaction().commit();
            }
            LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Excluiu coleira: " + cid);
            carregarTabela();
            JOptionPane.showMessageDialog(this, "Coleira " + cid + " excluída!", "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void vincular() {
        int row = tabela.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione uma coleira!", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
        if (!"Disponível".equals(tabelaModel.getValueAt(row, 5).toString())) {
            JOptionPane.showMessageDialog(this, "Coleira já está em uso!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String cid = tabelaModel.getValueAt(row, 0).toString();
        List<Animal> animais = backend.animalService.listarAtivos();
        String[] nomes = animais.stream().map(a -> a.getNome() + " #" + a.getNumeroBrinco()).toArray(String[]::new);
        if (nomes.length == 0) { JOptionPane.showMessageDialog(this, "Nenhum animal ativo!", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
        String esc = (String) JOptionPane.showInputDialog(this, "Selecione o animal:",
                "Vincular Coleira", JOptionPane.QUESTION_MESSAGE, null, nomes, nomes[0]);
        if (esc != null) {
            int idx = Arrays.asList(nomes).indexOf(esc);
            Animal a = animais.get(idx);
            backend.colarService.vincularAoAnimal(cid, a);
            backend.animalService.atualizar(a);
            LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Vinculou coleira " + cid + " a " + a.getNome());
            carregarTabela();
        }
    }

    private void liberar() {
        int row = tabela.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione uma coleira!", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
        String cid = tabelaModel.getValueAt(row, 0).toString();
        backend.colarService.liberarColar(cid);
        LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Liberou coleira: " + cid);
        carregarTabela();
    }

    private DefaultTableCellRenderer batRenderer() {
        return new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                l.setOpaque(true);
                try {
                    int b = Integer.parseInt(v.toString().replace("%","").trim());
                    if      (b <= 20) { l.setBackground(new Color(61,26,26)); l.setForeground(Tema.RED);   }
                    else if (b <= 50) { l.setBackground(new Color(61,46,10)); l.setForeground(Tema.AMBER); }
                    else              { l.setBackground(new Color(26,61,28)); l.setForeground(Tema.GREEN3);}
                } catch (Exception ignored) {}
                return l;
            }
        };
    }

    private DefaultTableCellRenderer statusColRenderer() {
        return new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                l.setOpaque(true);
                if ("Disponível".equals(v)) { l.setBackground(new Color(26,61,28)); l.setForeground(Tema.GREEN3); }
                else                        { l.setBackground(new Color(20,40,60)); l.setForeground(Tema.CYAN);   }
                return l;
            }
        };
    }
}
