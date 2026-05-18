package frontend;

import backend.*;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

public class CadastroFazendaScreen extends JPanel {

    private final Backend   backend;
    private final MainFrame mainFrame;
    private DefaultTableModel tabelaModel;
    private JTable            tabela;
    private List<Fazenda>     fazendas;

    // Campos do formulário (aba cadastro/edição) — nomes preservados
    private JTextField        fNome, fProp, fMun, fAreaT, fAreaM, fLat, fLon, fRaio, fTol;
    private JComboBox<String> fEst;
    private JLabel            lblTituloForm;
    private JButton           btnSalvar;
    private JTabbedPane       abas;
    private DefaultTableModel modelLotes;

    private int editandoId = 0;

    // ── helper de ícone ──────────────────────────────────────────────────────
    private static FlatSVGIcon ico(String name, int size) {
        return new FlatSVGIcon("icons/" + name + ".svg", size, size);
    }

    public CadastroFazendaScreen(MainFrame frame, Backend backend) {
        this.mainFrame = frame;
        this.backend   = backend;
        setBackground(Tema.BG);
        setLayout(new BorderLayout());
        add(new NavBar(frame, backend, 3), BorderLayout.NORTH);
        add(criarConteudo(), BorderLayout.CENTER);
    }

    private JPanel criarConteudo() {
        abas = Tema.criarAbas();

        // Abas com ícones nas labels
        JLabel tabLista = new JLabel("MINHAS FAZENDAS");
        tabLista.setIcon(ico("database", 14));
        tabLista.setIconTextGap(5);

        JLabel tabCad = new JLabel("CADASTRAR FAZENDA");
        tabCad.setIcon(ico("plus-circle", 14));
        tabCad.setIconTextGap(5);

        abas.addTab(null, criarAbaLista());
        abas.setTabComponentAt(0, tabLista);

        abas.addTab(null, criarAbaCadastro());
        abas.setTabComponentAt(1, tabCad);

        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(Tema.BG);

        // Cabeçalho com ícone
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(Tema.BG);
        h.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));

        JLabel lblTitulo = Tema.criarLabel("GESTÃO DE FAZENDAS", Tema.F_TITLE, Tema.GREENL);
        lblTitulo.setIcon(ico("database", 20));
        lblTitulo.setIconTextGap(8);
        h.add(lblTitulo, BorderLayout.WEST);

        JPanel hr = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        hr.setBackground(Tema.BG);

        JButton btnRef = Tema.criarBotaoRefresh();
        btnRef.setIcon(ico("refresh-cw", 16));
        btnRef.addActionListener(e -> recarregarTabela());
        hr.add(btnRef);
        h.add(hr, BorderLayout.EAST);

        c.add(h,    BorderLayout.NORTH);
        c.add(abas, BorderLayout.CENTER);
        return c;
    }

    // ── Aba lista ────────────────────────────────────────────────────────────

    private JPanel criarAbaLista() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        fazendas = backend.fazendaService.listarTodas();
        String[] cols = {"NOME", "MUNICÍPIO", "ESTADO", "LOTES", "ANIMAIS", "ÁREA TOTAL"};
        tabelaModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        preencherTabelaFazendas();
        tabela = Tema.criarTabela(tabelaModel);

        // double-click → detalhes — lógica original preservada
        tabela.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tabela.getSelectedRow();
                    if (row >= 0 && row < fazendas.size())
                        new DetalhesFazendaDialog(
                                SwingUtilities.getWindowAncestor(CadastroFazendaScreen.this),
                                fazendas.get(row), backend);
                }
            }
        });

        p.add(Tema.criarLabel("2× clique → detalhes  |  Selecione + botão para editar/excluir",
                Tema.F_SMALL, Tema.TEXT3), BorderLayout.NORTH);
        p.add(Tema.criarScroll(tabela), BorderLayout.CENTER);

        // Botões de ação com ícones
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acoes.setBackground(Tema.BG);

        JButton btnDet  = Tema.criarBotaoCyan("DETALHES");
        btnDet.setIcon(ico("search", 16));
        btnDet.setIconTextGap(6);

        JButton btnEdt  = Tema.criarBotaoPrimario("EDITAR");
        btnEdt.setIcon(ico("edit", 16));
        btnEdt.setIconTextGap(6);

        JButton btnExcl = Tema.criarBotaoPerigo("EXCLUIR");
        btnExcl.setIcon(ico("trash-2", 16));
        btnExcl.setIconTextGap(6);

        // Listeners — lógica original preservada
        btnDet.addActionListener(e  -> verDetalhes());
        btnEdt.addActionListener(e  -> irParaEdicao());
        btnExcl.addActionListener(e -> excluirFazenda());

        acoes.add(btnDet); acoes.add(btnEdt); acoes.add(btnExcl);
        p.add(acoes, BorderLayout.SOUTH);
        return p;
    }

    // ── Ir para edição — lógica original preservada ──────────────────────────

    private void irParaEdicao() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma fazenda para editar!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Fazenda f = fazendas.get(row);
        editandoId = f.getId();

        fNome.setText(f.getNome()            != null ? f.getNome()            : "");
        fProp.setText(f.getProprietario()    != null ? f.getProprietario()    : "");
        fMun.setText(f.getMunicipio()        != null ? f.getMunicipio()       : "");
        fAreaT.setText(f.getAreaTotal()      >  0    ? String.valueOf(f.getAreaTotal())      : "");
        fAreaM.setText(f.getAreaMonitorada() >  0    ? String.valueOf(f.getAreaMonitorada()) : "");
        fLat.setText(String.valueOf(f.getLatitudeCentro()));
        fLon.setText(String.valueOf(f.getLongitudeCentro()));
        fRaio.setText(String.valueOf(f.getRaioMetros()));
        fTol.setText(String.valueOf(f.getToleranciaMetros()));
        if (f.getEstado() != null) fEst.setSelectedItem(f.getEstado());

        modelLotes.setRowCount(0);
        for (Lote l : f.getLotes())
            modelLotes.addRow(new Object[]{
                    l.getNome(),
                    l.getAreaHectares() > 0 ? String.valueOf(l.getAreaHectares()) : "—",
                    l.getCapacidade()   > 0 ? String.valueOf(l.getCapacidade())   : "—"});

        lblTituloForm.setText("EDITANDO: " + f.getNome().toUpperCase());
        lblTituloForm.setForeground(Tema.AMBER);
        btnSalvar.setText("SALVAR ALTERAÇÕES");
        btnSalvar.setIcon(ico("save", 16));
        btnSalvar.setBackground(Tema.AMBER);
        btnSalvar.setForeground(new Color(30, 20, 0));

        abas.setSelectedIndex(1);
    }

    // ── Cancelar edição — lógica original preservada ─────────────────────────

    private void cancelarEdicao() {
        editandoId = 0;
        fNome.setText(""); fProp.setText(""); fMun.setText("");
        fAreaT.setText(""); fAreaM.setText("");
        fLat.setText("-23.0000"); fLon.setText("-47.0000");
        fRaio.setText("2000"); fTol.setText("50");
        modelLotes.setRowCount(0);
        lblTituloForm.setText("DADOS DA FAZENDA");
        lblTituloForm.setForeground(Tema.TEXT3);
        lblTituloForm.setIcon(ico("database", 14));
        btnSalvar.setText("CADASTRAR FAZENDA");
        btnSalvar.setIcon(ico("save", 16));
        btnSalvar.setBackground(Tema.GREEN);
        btnSalvar.setForeground(Color.WHITE);
        abas.setSelectedIndex(0);
    }

    // ── Aba cadastro / edição ────────────────────────────────────────────────

    private JPanel criarAbaCadastro() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JPanel grade = new JPanel(new GridLayout(1, 2, 12, 0));
        grade.setBackground(Tema.BG);

        // Card formulário
        JPanel card = Tema.criarCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        lblTituloForm = Tema.criarLabel("DADOS DA FAZENDA", Tema.F_LABEL, Tema.TEXT3);
        lblTituloForm.setIcon(ico("database", 14));
        lblTituloForm.setIconTextGap(6);
        card.add(lblTituloForm);
        card.add(Box.createVerticalStrut(10));

        fNome  = Tema.criarCampo("");
        fProp  = Tema.criarCampo("");
        fMun   = Tema.criarCampo("");
        fEst   = Tema.criarCombo("SP","MG","GO","MT","MS","PR","BA","RS","TO","PA",
                                  "AM","RO","RR","AC","AP","MA","PI","CE","RN","PB",
                                  "PE","AL","SE","ES","RJ","SC");
        fAreaT = Tema.criarCampo("");
        fAreaM = Tema.criarCampo("");
        fLat   = Tema.criarCampo("-23.0000");
        fLon   = Tema.criarCampo("-47.0000");
        fRaio  = Tema.criarCampo("2000");
        fTol   = Tema.criarCampo("50");

        card.add(Tema.par("NOME", fNome, "PROPRIETÁRIO", fProp));                 card.add(Box.createVerticalStrut(6));
        card.add(Tema.par("MUNICÍPIO", fMun, "ESTADO", fEst));                    card.add(Box.createVerticalStrut(6));
        card.add(Tema.par("ÁREA TOTAL (HA)", fAreaT, "ÁREA MON. (HA)", fAreaM)); card.add(Box.createVerticalStrut(8));

        JLabel lblGeo = Tema.criarLabel("GEOFENCE", Tema.F_LABEL, Tema.TEXT3);
        lblGeo.setIcon(ico("zap", 14));
        lblGeo.setIconTextGap(6);
        card.add(lblGeo);
        card.add(Box.createVerticalStrut(6));
        card.add(Tema.par("LATITUDE CENTRO", fLat, "LONGITUDE CENTRO", fLon));    card.add(Box.createVerticalStrut(6));
        card.add(Tema.par("RAIO (M)", fRaio, "TOLERÂNCIA (M)", fTol));

        // Card lotes
        JPanel cardLotes = Tema.criarCard();
        cardLotes.setLayout(new BorderLayout(0, 6));

        JPanel loteTopo = new JPanel(new BorderLayout());
        loteTopo.setBackground(Tema.CARD);

        JLabel lblLotes = Tema.criarLabel("LOTES DA FAZENDA", Tema.F_LABEL, Tema.TEXT3);
        lblLotes.setIcon(ico("filter", 14));
        lblLotes.setIconTextGap(6);
        loteTopo.add(lblLotes, BorderLayout.WEST);

        // Botões de lote com ícones
        JButton btnAddLote     = new JButton("Lote");
        JButton btnExcluirLote = new JButton("Excluir");

        btnAddLote.setIcon(ico("plus-circle", 12));
        btnAddLote.setIconTextGap(4);
        btnAddLote.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        Tema.semFoco(btnAddLote);
        btnAddLote.setBackground(new Color(26, 50, 28));
        btnAddLote.setForeground(Tema.GREEN3);
        btnAddLote.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        btnAddLote.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnExcluirLote.setIcon(ico("trash-2", 12));
        btnExcluirLote.setIconTextGap(4);
        btnExcluirLote.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        Tema.semFoco(btnExcluirLote);
        btnExcluirLote.setBackground(new Color(60, 20, 20));
        btnExcluirLote.setForeground(new Color(255, 120, 120));
        btnExcluirLote.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        btnExcluirLote.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel botoesLote = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        botoesLote.setBackground(Tema.CARD);
        botoesLote.add(btnExcluirLote);
        botoesLote.add(btnAddLote);
        loteTopo.add(botoesLote, BorderLayout.EAST);
        cardLotes.add(loteTopo, BorderLayout.NORTH);

        modelLotes = new DefaultTableModel(new String[]{"LOTE", "ÁREA (HA)", "CAP."}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabelaLotes = Tema.criarTabela(modelLotes);
        cardLotes.add(Tema.criarScroll(tabelaLotes), BorderLayout.CENTER);

        JPanel addL = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        addL.setBackground(Tema.CARD);
        JTextField cNL = Tema.criarCampo("Nome"); cNL.setPreferredSize(new Dimension(90, 30));
        JTextField cAL = Tema.criarCampo("Ha");   cAL.setPreferredSize(new Dimension(60, 30));
        JTextField cCL = Tema.criarCampo("Cap."); cCL.setPreferredSize(new Dimension(60, 30));
        int[] linhaEditando = {-1};
        addL.add(cNL); addL.add(cAL); addL.add(cCL);

        // Listeners originais — preservados integralmente
        tabelaLotes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tabelaLotes.getSelectedRow();
                    if (row != -1) {
                        linhaEditando[0] = row;
                        cNL.setText(modelLotes.getValueAt(row, 0).toString());
                        cAL.setText(modelLotes.getValueAt(row, 1).toString());
                        cCL.setText(modelLotes.getValueAt(row, 2).toString());
                    }
                }
            }
        });

        btnAddLote.addActionListener(e -> {
            String n = cNL.getText().trim(), a = cAL.getText().trim(), cap = cCL.getText().trim();
            if (n.isEmpty() || a.isEmpty()) return;
            if (linhaEditando[0] != -1) {
                modelLotes.setValueAt(n, linhaEditando[0], 0);
                modelLotes.setValueAt(a, linhaEditando[0], 1);
                modelLotes.setValueAt(cap.isEmpty() ? "0" : cap, linhaEditando[0], 2);
                linhaEditando[0] = -1;
            } else {
                modelLotes.addRow(new Object[]{n, a, cap.isEmpty() ? "0" : cap});
            }
            if (editandoId > 0) {
                backend.fazendaService.buscarPorId(editandoId).ifPresent(faz -> {
                    try {
                        String capLimpo = cap.replace("_", "").replace("—", "").replace("?", "").trim();
                        int capacidade  = capLimpo.isEmpty() ? 0 : Integer.parseInt(capLimpo);
                        Lote novoLote   = new Lote(0, n, capacidade);
                        try (org.hibernate.Session s = HibernateUtil.getSessionFactory().openSession()) {
                            s.beginTransaction();
                            s.persist(novoLote);
                            faz.addLote(novoLote);
                            s.merge(faz);
                            s.getTransaction().commit();
                        }
                        LogAtividades.registrar(backend.authService.getUsuarioLogado(),
                                "Adicionou lote " + n + " na fazenda " + faz.getNome());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null,
                                "Erro ao salvar lote: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                });
            }
            cNL.setText(""); cAL.setText(""); cCL.setText("");
        });

        btnExcluirLote.addActionListener(e -> {
            int row = tabelaLotes.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Selecione um lote para excluir!");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Deseja excluir este lote?", "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String nomeLote = modelLotes.getValueAt(row, 0).toString();
                try (org.hibernate.Session s = HibernateUtil.getSessionFactory().openSession()) {
                    s.beginTransaction();
                    Lote lote = s.createQuery("FROM Lote WHERE nome = :nome", Lote.class)
                            .setParameter("nome", nomeLote).setMaxResults(1).uniqueResult();
                    if (lote != null) s.remove(lote);
                    s.getTransaction().commit();
                    modelLotes.removeRow(row);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao excluir lote: " + ex.getMessage());
                }
            }
        });

        cardLotes.add(addL, BorderLayout.SOUTH);
        grade.add(card);
        grade.add(cardLotes);
        p.add(grade, BorderLayout.CENTER);

        // Botões principais com ícones
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acoes.setBackground(Tema.BG);

        btnSalvar = Tema.criarBotaoPrimario("CADASTRAR FAZENDA");
        btnSalvar.setIcon(ico("save", 16));
        btnSalvar.setIconTextGap(6);
        btnSalvar.addActionListener(e -> salvarFazenda());

        JButton btnCancelar = Tema.criarBotaoSecundario("CANCELAR");
        btnCancelar.setIcon(ico("x", 16));
        btnCancelar.setIconTextGap(6);
        btnCancelar.addActionListener(e -> cancelarEdicao());

        acoes.add(btnSalvar);
        acoes.add(btnCancelar);
        p.add(acoes, BorderLayout.SOUTH);
        return p;
    }

    // ── lógica original — não alterada ──────────────────────────────────────

    private void salvarFazenda() {
        if (fNome.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome é obrigatório!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Fazenda f = editandoId > 0
                ? backend.fazendaService.buscarPorId(editandoId).orElse(new Fazenda())
                : new Fazenda();

        f.setNome(fNome.getText().trim());
        f.setProprietario(fProp.getText().trim());
        f.setMunicipio(fMun.getText().trim());
        f.setEstado(fEst.getSelectedItem().toString());
        try { f.setAreaTotal(Double.parseDouble(fAreaT.getText().trim()));       } catch (Exception ignored) {}
        try { f.setAreaMonitorada(Double.parseDouble(fAreaM.getText().trim()));  } catch (Exception ignored) {}
        try { f.setLatitudeCentro(Double.parseDouble(fLat.getText().trim()));    } catch (Exception ignored) {}
        try { f.setLongitudeCentro(Double.parseDouble(fLon.getText().trim()));   } catch (Exception ignored) {}
        try { f.setRaioMetros(Double.parseDouble(fRaio.getText().trim()));       } catch (Exception ignored) {}
        try { f.setToleranciaMetros(Double.parseDouble(fTol.getText().trim()));  } catch (Exception ignored) {}

        if (editandoId > 0) {
            backend.fazendaService.atualizar(f);
            LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Editou fazenda: " + f.getNome());
            JOptionPane.showMessageDialog(this, "Fazenda \"" + f.getNome() + "\" atualizada!", "OK",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            backend.fazendaService.cadastrar(f);
            LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Cadastrou fazenda: " + f.getNome());
            JOptionPane.showMessageDialog(this, "Fazenda \"" + f.getNome() + "\" cadastrada!", "OK",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        cancelarEdicao();
        recarregarTabela();
    }

    private void recarregarTabela() {
        fazendas = backend.fazendaService.listarTodas();
        preencherTabelaFazendas();
    }

    private void preencherTabelaFazendas() {
        if (tabelaModel == null) return;
        tabelaModel.setRowCount(0);
        for (Fazenda f : fazendas) {
            int totalAnim = f.getLotes().stream().mapToInt(l -> l.getAnimais().size()).sum();
            tabelaModel.addRow(new Object[]{
                    f.getNome(), f.getMunicipio(), f.getEstado(),
                    f.getLotes().size(), totalAnim, f.getAreaTotal() + " ha"});
        }
    }

    private void verDetalhes() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma fazenda!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        new DetalhesFazendaDialog(SwingUtilities.getWindowAncestor(this), fazendas.get(row), backend);
    }

    private void excluirFazenda() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma fazenda!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Fazenda f = fazendas.get(row);
        if (JOptionPane.showConfirmDialog(this,
                "Excluir fazenda \"" + f.getNome() + "\" permanentemente?\nATENÇÃO: Todos os lotes e vínculos serão removidos.",
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
        String confirmacao = JOptionPane.showInputDialog(this,
                "Digite o nome da fazenda para confirmar:", "Confirmação", JOptionPane.WARNING_MESSAGE);
        if (confirmacao == null || !f.getNome().equals(confirmacao)) {
            JOptionPane.showMessageDialog(this, "Nome incorreto. Operação cancelada.", "Cancelado",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try (org.hibernate.Session s = HibernateUtil.getSessionFactory().openSession()) {
            s.beginTransaction();
            Fazenda fDel = s.get(Fazenda.class, f.getId());
            if (fDel != null) s.remove(fDel);
            s.getTransaction().commit();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Excluiu fazenda: " + f.getNome());
        recarregarTabela();
        JOptionPane.showMessageDialog(this, "Fazenda excluída!", "OK", JOptionPane.INFORMATION_MESSAGE);
    }
}
