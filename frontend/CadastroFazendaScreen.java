package frontend;

import backend.*;
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

    public CadastroFazendaScreen(MainFrame frame, Backend backend) {
        this.mainFrame = frame;
        this.backend   = backend;
        setBackground(Tema.BG);
        setLayout(new BorderLayout());
        add(new NavBar(frame, backend, 3), BorderLayout.NORTH);
        add(criarConteudo(), BorderLayout.CENTER);
    }

    private JPanel criarConteudo() {
        JTabbedPane abas = Tema.criarAbas();
        abas.addTab("🏠 MINHAS FAZENDAS",  criarAbaLista());
        abas.addTab("➕ CADASTRAR FAZENDA", criarAbaCadastro());

        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(Tema.BG);

        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(Tema.BG);
        h.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));
        h.add(Tema.criarLabel("GESTÃO DE FAZENDAS", Tema.F_TITLE, Tema.GREENL), BorderLayout.WEST);

        JPanel hr = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        hr.setBackground(Tema.BG);
        JButton btnRef = Tema.criarBotaoRefresh();
        btnRef.addActionListener(e -> recarregarTabela());
        hr.add(btnRef);
        h.add(hr, BorderLayout.EAST);

        c.add(h, BorderLayout.NORTH);
        c.add(abas, BorderLayout.CENTER);
        return c;
    }

    private JPanel criarAbaLista() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        fazendas = backend.fazendaService.listarTodas();
        String[] cols = {"NOME","MUNICÍPIO","ESTADO","LOTES","ANIMAIS","ÁREA TOTAL"};
        tabelaModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        preencherTabelaFazendas();
        tabela = Tema.criarTabela(tabelaModel);
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

        p.add(Tema.criarLabel("2× clique para ver detalhes | Selecione + botões para editar/excluir",
                Tema.F_SMALL, Tema.TEXT3), BorderLayout.NORTH);
        p.add(Tema.criarScroll(tabela), BorderLayout.CENTER);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acoes.setBackground(Tema.BG);
        JButton btnDet  = Tema.criarBotaoCyan("DETALHES");
        JButton btnEdt  = Tema.criarBotaoPrimario("✎ EDITAR");
        JButton btnExcl = Tema.criarBotaoPerigo("✕ EXCLUIR");
        btnDet.addActionListener(e -> verDetalhes());
        btnEdt.addActionListener(e -> editarFazenda());
        btnExcl.addActionListener(e -> excluirFazenda());
        acoes.add(btnDet); acoes.add(btnEdt); acoes.add(btnExcl);
        p.add(acoes, BorderLayout.SOUTH);
        return p;
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

    private void editarFazenda() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma fazenda!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Fazenda f = fazendas.get(row);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBackground(Tema.BG);
        JTextField cNome  = Tema.criarCampo(f.getNome() != null ? f.getNome() : "");
        JTextField cProp  = Tema.criarCampo(f.getProprietario() != null ? f.getProprietario() : "");
        JTextField cMun   = Tema.criarCampo(f.getMunicipio() != null ? f.getMunicipio() : "");
        JTextField cAreaT = Tema.criarCampo(String.valueOf(f.getAreaTotal()));
        JTextField cRaio  = Tema.criarCampo(String.valueOf(f.getRaioMetros()));
        form.add(Tema.criarLabel("NOME:",         Tema.F_SMALL, Tema.TEXT3)); form.add(cNome);
        form.add(Tema.criarLabel("PROPRIETÁRIO:", Tema.F_SMALL, Tema.TEXT3)); form.add(cProp);
        form.add(Tema.criarLabel("MUNICÍPIO:",    Tema.F_SMALL, Tema.TEXT3)); form.add(cMun);
        form.add(Tema.criarLabel("ÁREA TOTAL:",   Tema.F_SMALL, Tema.TEXT3)); form.add(cAreaT);
        form.add(Tema.criarLabel("RAIO (m):",     Tema.F_SMALL, Tema.TEXT3)); form.add(cRaio);

        if (JOptionPane.showConfirmDialog(this, form, "Editar Fazenda: " + f.getNome(),
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            if (!cNome.getText().trim().isEmpty()) f.setNome(cNome.getText().trim());
            f.setProprietario(cProp.getText().trim());
            f.setMunicipio(cMun.getText().trim());
            try { f.setAreaTotal(Double.parseDouble(cAreaT.getText().trim())); } catch (Exception ignored) {}
            try { f.setRaioMetros(Double.parseDouble(cRaio.getText().trim())); } catch (Exception ignored) {}
            backend.fazendaService.atualizar(f);
            LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Editou fazenda: " + f.getNome());
            recarregarTabela();
            JOptionPane.showMessageDialog(this, "Fazenda atualizada!", "OK", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void excluirFazenda() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma fazenda!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Fazenda f = fazendas.get(row);

        if (JOptionPane.showConfirmDialog(this,
                "Excluir fazenda \"" + f.getNome() + "\" permanentemente?\n" +
                "ATENÇÃO: Todos os lotes e vínculos serão removidos.",
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;

        // Segunda confirmação: digitar o nome
        String confirmacao = JOptionPane.showInputDialog(this,
                "Digite o nome da fazenda para confirmar:", "Confirmação", JOptionPane.WARNING_MESSAGE);
        if (!f.getNome().equals(confirmacao)) {
            JOptionPane.showMessageDialog(this, "Nome incorreto. Operação cancelada.",
                    "Cancelado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // ✔ CORRIGIDO: FazendaService não tem remover() — usamos Hibernate diretamente
        try (org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Fazenda managed = session.get(Fazenda.class, f.getId());
            if (managed != null) session.remove(managed);
            session.getTransaction().commit();
            LogAtividades.registrar(backend.authService.getUsuarioLogado(),
                    "Excluiu fazenda: " + f.getNome());
            recarregarTabela();
            JOptionPane.showMessageDialog(this, "Fazenda excluída!", "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel criarAbaCadastro() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JPanel grade = new JPanel(new GridLayout(1, 2, 12, 0));
        grade.setBackground(Tema.BG);

        // Formulário principal
        JPanel card = Tema.criarCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(Tema.criarLabel("◈ DADOS DA FAZENDA", Tema.F_LABEL, Tema.TEXT3));
        card.add(Box.createVerticalStrut(10));

        JTextField cNome  = Tema.criarCampo("");
        JTextField cProp  = Tema.criarCampo("");
        JTextField cMun   = Tema.criarCampo("");
        JComboBox<String> cEst = Tema.criarCombo(
                "SP","MG","GO","MT","MS","PR","BA","RS","TO","PA",
                "AM","RO","RR","AC","AP","MA","PI","CE","RN","PB",
                "PE","AL","SE","ES","RJ","SC");
        JTextField cAreaT = Tema.criarCampo("");
        JTextField cAreaM = Tema.criarCampo("");
        JTextField cLat   = Tema.criarCampo("-23.0000");
        JTextField cLon   = Tema.criarCampo("-47.0000");
        JTextField cRaio  = Tema.criarCampo("2000");
        JTextField cTol   = Tema.criarCampo("50");

        card.add(Tema.par("NOME", cNome, "PROPRIETÁRIO", cProp));        card.add(Box.createVerticalStrut(6));
        card.add(Tema.par("MUNICÍPIO", cMun, "ESTADO", cEst));           card.add(Box.createVerticalStrut(6));
        card.add(Tema.par("ÁREA TOTAL (HA)", cAreaT, "ÁREA MON. (HA)", cAreaM)); card.add(Box.createVerticalStrut(8));
        card.add(Tema.criarLabel("◈ GEOFENCE", Tema.F_LABEL, Tema.TEXT3)); card.add(Box.createVerticalStrut(6));
        card.add(Tema.par("LATITUDE CENTRO", cLat, "LONGITUDE CENTRO", cLon)); card.add(Box.createVerticalStrut(6));
        card.add(Tema.par("RAIO (M)", cRaio, "TOLERÂNCIA (M)", cTol));

        // Painel de lotes
        JPanel cardLotes = Tema.criarCard();
        cardLotes.setLayout(new BorderLayout(0, 6));
        JPanel loteTopo = new JPanel(new BorderLayout());
        loteTopo.setBackground(Tema.CARD);
        loteTopo.add(Tema.criarLabel("◈ LOTES DA FAZENDA", Tema.F_LABEL, Tema.TEXT3), BorderLayout.WEST);
        JButton btnAddLote = new JButton("+ Lote");
        btnAddLote.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        Tema.semFoco(btnAddLote);
        btnAddLote.setBackground(new Color(26, 50, 28));
        btnAddLote.setForeground(Tema.GREEN3);
        btnAddLote.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        btnAddLote.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loteTopo.add(btnAddLote, BorderLayout.EAST);
        cardLotes.add(loteTopo, BorderLayout.NORTH);

        DefaultTableModel mL = new DefaultTableModel(new String[]{"LOTE","ÁREA (HA)","CAP."}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tL = Tema.criarTabela(mL);
        cardLotes.add(Tema.criarScroll(tL), BorderLayout.CENTER);

        JPanel addL = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        addL.setBackground(Tema.CARD);
        JTextField cNL = Tema.criarCampo("Nome"); cNL.setPreferredSize(new Dimension(90, 30));
        JTextField cAL = Tema.criarCampo("Ha");   cAL.setPreferredSize(new Dimension(60, 30));
        JTextField cCL = Tema.criarCampo("Cap."); cCL.setPreferredSize(new Dimension(60, 30));
        addL.add(cNL); addL.add(cAL); addL.add(cCL);
        btnAddLote.addActionListener(e -> {
            String n = cNL.getText().trim(), a = cAL.getText().trim();
            if (!n.isEmpty() && !a.isEmpty()) {
                mL.addRow(new Object[]{n, a, cCL.getText().trim()});
                cNL.setText(""); cAL.setText(""); cCL.setText("");
            }
        });
        cardLotes.add(addL, BorderLayout.SOUTH);

        grade.add(card);
        grade.add(cardLotes);
        p.add(grade, BorderLayout.CENTER);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acoes.setBackground(Tema.BG);
        JButton btnSalvar = Tema.criarBotaoPrimario("✔ CADASTRAR FAZENDA");
        btnSalvar.addActionListener(e -> {
            if (cNome.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome é obrigatório!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Fazenda f = new Fazenda();
            f.setNome(cNome.getText().trim());
            f.setProprietario(cProp.getText().trim());
            f.setMunicipio(cMun.getText().trim());
            f.setEstado(cEst.getSelectedItem().toString());
            try { f.setAreaTotal(Double.parseDouble(cAreaT.getText().trim())); }      catch (Exception ignored) {}
            try { f.setAreaMonitorada(Double.parseDouble(cAreaM.getText().trim())); } catch (Exception ignored) {}
            try { f.setLatitudeCentro(Double.parseDouble(cLat.getText().trim())); }   catch (Exception ignored) {}
            try { f.setLongitudeCentro(Double.parseDouble(cLon.getText().trim())); }  catch (Exception ignored) {}
            try { f.setRaioMetros(Double.parseDouble(cRaio.getText().trim())); }      catch (Exception ignored) {}
            try { f.setToleranciaMetros(Double.parseDouble(cTol.getText().trim())); } catch (Exception ignored) {}
            backend.fazendaService.cadastrar(f);
            LogAtividades.registrar(backend.authService.getUsuarioLogado(),
                    "Cadastrou fazenda: " + f.getNome());
            recarregarTabela();
            JOptionPane.showMessageDialog(this, "Fazenda " + f.getNome() + " cadastrada!",
                    "OK", JOptionPane.INFORMATION_MESSAGE);
            cNome.setText(""); cProp.setText(""); cMun.setText("");
            cAreaT.setText(""); cAreaM.setText("");
        });
        acoes.add(btnSalvar);
        p.add(acoes, BorderLayout.SOUTH);
        return p;
    }
}
