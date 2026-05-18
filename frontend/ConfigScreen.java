package frontend;

import backend.*;
import java.awt.*;
import java.io.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

public class ConfigScreen extends JPanel {

    private final Backend   backend;
    private final MainFrame mainFrame;

    public ConfigScreen(MainFrame frame, Backend backend) {
        this.mainFrame = frame;
        this.backend   = backend;
        setBackground(Tema.BG);
        setLayout(new BorderLayout());
        add(new NavBar(frame, backend, 8), BorderLayout.NORTH);
        add(criarConteudo(), BorderLayout.CENTER);
    }

    private JPanel criarConteudo() {
        JTabbedPane abas = Tema.criarAbas();
        abas.addTab("⚙ SISTEMA",        criarAbaSistema());
        abas.addTab("👥 USUÁRIOS",       criarAbaUsuarios());
        abas.addTab("🔒 SEGURANÇA",      criarAbaSeguranca());
        abas.addTab("🔔 NOTIFICAÇÕES",   criarAbaNotif());
        abas.addTab("📋 LOG ATIVIDADES", criarAbaLog());
        abas.addTab("🗄 BANCO DE DADOS", criarAbaBanco());

        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(Tema.BG);

        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(Tema.BG);
        h.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));
        h.add(Tema.criarLabel("CONFIGURAÇÕES DO SISTEMA", Tema.F_TITLE, Tema.GREENL), BorderLayout.WEST);
        if (!backend.authService.isAdmin())
            h.add(Tema.criarLabel("⚠ Acesso limitado — ADM requerido para algumas funções",
                    Tema.F_SMALL, Tema.AMBER), BorderLayout.EAST);

        c.add(h, BorderLayout.NORTH);
        c.add(abas, BorderLayout.CENTER);
        return c;
    }

    // ─── Aba Sistema ──────────────────────────────────────────────────────────
    private JPanel criarAbaSistema() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JPanel card = Tema.criarCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(Tema.criarLabel("◈ CONFIGURAÇÕES GERAIS", Tema.F_LABEL, Tema.TEXT3));
        card.add(Box.createVerticalStrut(10));

        JTextField cNome    = Tema.criarCampo("SIRATECH");    cNome.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        JTextField cLimBat  = Tema.criarCampo("20");          cLimBat.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        JTextField cFreq    = Tema.criarCampo("5");           cFreq.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        JTextField cTimeout = Tema.criarCampo("30");          cTimeout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        JComboBox<String> cIdioma = Tema.criarCombo("Português (BR)", "English", "Español");
        cIdioma.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        JComboBox<String> cTema = Tema.criarCombo("Verde Escuro (Padrão)", "Azul Escuro", "Cinza");
        cTema.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        card.add(Tema.criarLabel("NOME DO SISTEMA", Tema.F_SMALL, Tema.TEXT3));
        card.add(Box.createVerticalStrut(4)); card.add(cNome); card.add(Box.createVerticalStrut(10));
        card.add(Tema.criarLabel("LIMIAR BATERIA BAIXA (%)", Tema.F_SMALL, Tema.TEXT3));
        card.add(Box.createVerticalStrut(4)); card.add(cLimBat); card.add(Box.createVerticalStrut(10));
        card.add(Tema.criarLabel("FREQUÊNCIA RASTREAMENTO (MIN)", Tema.F_SMALL, Tema.TEXT3));
        card.add(Box.createVerticalStrut(4)); card.add(cFreq); card.add(Box.createVerticalStrut(10));
        card.add(Tema.criarLabel("TIMEOUT SESSÃO (MIN)", Tema.F_SMALL, Tema.TEXT3));
        card.add(Box.createVerticalStrut(4)); card.add(cTimeout); card.add(Box.createVerticalStrut(10));
        card.add(Tema.criarLabel("IDIOMA", Tema.F_SMALL, Tema.TEXT3));
        card.add(Box.createVerticalStrut(4)); card.add(cIdioma); card.add(Box.createVerticalStrut(10));
        card.add(Tema.criarLabel("TEMA DO SISTEMA", Tema.F_SMALL, Tema.TEXT3));
        card.add(Box.createVerticalStrut(4)); card.add(cTema); card.add(Box.createVerticalStrut(14));

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botoes.setBackground(Tema.CARD);
        JButton bSalvar = Tema.criarBotaoPrimario("✔ SALVAR");
        bSalvar.addActionListener(e -> {
            LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Alterou configurações gerais");
            JOptionPane.showMessageDialog(this, "Configurações salvas!", "OK", JOptionPane.INFORMATION_MESSAGE);
        });
        JButton bReset = Tema.criarBotaoSecundario("↺ RESTAURAR PADRÕES");
        bReset.addActionListener(e -> {
            cNome.setText("SIRATECH");
            cLimBat.setText("20");
            cFreq.setText("5");
            cTimeout.setText("30");
            cIdioma.setSelectedIndex(0);
            cTema.setSelectedIndex(0);
        });
        botoes.add(bSalvar); botoes.add(bReset);
        card.add(botoes);
        p.add(card, BorderLayout.NORTH);
        return p;
    }

    // ─── Aba Usuários ─────────────────────────────────────────────────────────
    private JPanel criarAbaUsuarios() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        if (!backend.authService.isAdmin()) {
            p.add(Tema.criarLabel("⚠ Apenas administradores podem gerenciar usuários.", Tema.F_BODY, Tema.AMBER),
                    BorderLayout.NORTH);
            return p;
        }

        String[] cols = {"LOGIN", "NOME", "PERFIL", "ATIVO"};
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        List<Usuario> usuarios = backend.authService.listarUsuarios();
        for (Usuario u : usuarios)
            m.addRow(new Object[]{u.getLogin(), u.getNomeCompleto(), u.getPerfil(), u.isAtivo() ? "✔ Sim" : "✘ Não"});
        JTable tabela = Tema.criarTabela(m);

        JPanel card = Tema.criarCard();
        card.setLayout(new BorderLayout(0, 8));
        card.add(Tema.criarLabel("◈ USUÁRIOS DO SISTEMA", Tema.F_LABEL, Tema.TEXT3), BorderLayout.NORTH);
        card.add(Tema.criarScroll(tabela), BorderLayout.CENTER);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acoes.setBackground(Tema.CARD);

        JButton btnNovo = Tema.criarBotaoPrimario("+ NOVO USUÁRIO");
        btnNovo.addActionListener(e -> {
            JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
            form.setBackground(Tema.BG);
            JTextField cL = Tema.criarCampo(""), cN = Tema.criarCampo(""), cS = Tema.criarCampo("");
            JComboBox<String> cP = Tema.criarCombo("Operador", "Administrador");
            form.add(Tema.criarLabel("LOGIN:",  Tema.F_SMALL, Tema.TEXT3)); form.add(cL);
            form.add(Tema.criarLabel("NOME:",   Tema.F_SMALL, Tema.TEXT3)); form.add(cN);
            form.add(Tema.criarLabel("SENHA:",  Tema.F_SMALL, Tema.TEXT3)); form.add(cS);
            form.add(Tema.criarLabel("PERFIL:", Tema.F_SMALL, Tema.TEXT3)); form.add(cP);
            if (JOptionPane.showConfirmDialog(this, form, "Novo Usuário", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                String login = cL.getText().trim(), nome = cN.getText().trim(), senha = cS.getText().trim();
                if (login.isEmpty() || senha.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Preencha login e senha!", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                boolean ok = backend.authService.cadastrarUsuario(login, senha, cP.getSelectedItem().toString(),
                        nome.isEmpty() ? login : nome);
                if (ok) {
                    m.addRow(new Object[]{login, nome.isEmpty() ? login : nome, cP.getSelectedItem(), "✔ Sim"});
                    LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Criou usuário: " + login);
                } else {
                    JOptionPane.showMessageDialog(this, "Login já existe!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton btnEdit = Tema.criarBotaoCyan("✎ EDITAR SENHA");
        btnEdit.addActionListener(e -> {
            int row = tabela.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um usuário!", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
            String login = m.getValueAt(row, 0).toString();
            JPasswordField pf = Tema.criarSenha();

            JPanel senhaPanel = new JPanel(new BorderLayout(0, 6));
            senhaPanel.setBackground(Tema.BG);
            senhaPanel.add(Tema.criarLabel("Nova senha para: " + login, Tema.F_SMALL, Tema.TEXT3), BorderLayout.NORTH);
            senhaPanel.add(pf, BorderLayout.CENTER);

            if (JOptionPane.showConfirmDialog(this, senhaPanel, "Alterar Senha", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                String novaSenha = new String(pf.getPassword()).trim();
                if (novaSenha.isEmpty()) { JOptionPane.showMessageDialog(this, "Senha não pode ser vazia!", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
                backend.authService.alterarSenha(login, novaSenha);
                LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Alterou senha de: " + login);
                JOptionPane.showMessageDialog(this, "Senha alterada!", "OK", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton btnDes = Tema.criarBotaoPerigo("DESATIVAR");
        btnDes.addActionListener(e -> {
            int row = tabela.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um usuário!", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
            String login = m.getValueAt(row, 0).toString();
            if (login.equals(backend.authService.getUsuarioLogado())) {
                JOptionPane.showMessageDialog(this, "Não pode desativar o próprio usuário!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (JOptionPane.showConfirmDialog(this, "Desativar usuário \"" + login + "\"?",
                    "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                Usuario u = backend.authService.listarUsuarios().get(row);
                backend.authService.desativarUsuario(u.getId());
                m.setValueAt("✘ Não", row, 3);
                LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Desativou usuário: " + login);
            }
        });

        acoes.add(btnNovo); acoes.add(btnEdit); acoes.add(btnDes);
        card.add(acoes, BorderLayout.SOUTH);
        p.add(card, BorderLayout.CENTER);
        return p;
    }

    // ─── Aba Segurança ────────────────────────────────────────────────────────
    private JPanel criarAbaSeguranca() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JPanel card = Tema.criarCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(Tema.criarLabel("◈ CONFIGURAÇÕES DE SEGURANÇA", Tema.F_LABEL, Tema.TEXT3));
        card.add(Box.createVerticalStrut(10));

        JTextField cMaxTent  = Tema.criarCampo("5");         cMaxTent.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        JTextField cBloquear = Tema.criarCampo("5 minutos"); cBloquear.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        JCheckBox chkLog     = criarCheck("Registrar tentativas de login falhas", true);
        JCheckBox chkTimeout = criarCheck("Expirar sessão por inatividade (30 min)", true);
        JCheckBox chkSenha   = criarCheck("Exigir senha forte (mín. 8 caracteres)", false);

        card.add(Tema.criarLabel("MÁXIMO DE TENTATIVAS DE LOGIN", Tema.F_SMALL, Tema.TEXT3));
        card.add(Box.createVerticalStrut(4)); card.add(cMaxTent); card.add(Box.createVerticalStrut(10));
        card.add(Tema.criarLabel("TEMPO DE BLOQUEIO", Tema.F_SMALL, Tema.TEXT3));
        card.add(Box.createVerticalStrut(4)); card.add(cBloquear); card.add(Box.createVerticalStrut(12));
        card.add(chkLog); card.add(Box.createVerticalStrut(6));
        card.add(chkTimeout); card.add(Box.createVerticalStrut(6));
        card.add(chkSenha); card.add(Box.createVerticalStrut(14));

        JButton bSalvar = Tema.criarBotaoPrimario("✔ SALVAR");
        bSalvar.addActionListener(e -> {
            LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Alterou configurações de segurança");
            JOptionPane.showMessageDialog(this, "Configurações de segurança salvas!", "OK", JOptionPane.INFORMATION_MESSAGE);
        });
        card.add(bSalvar);
        p.add(card, BorderLayout.NORTH);
        return p;
    }

    // ─── Aba Notificações ─────────────────────────────────────────────────────
    private JPanel criarAbaNotif() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JPanel card = Tema.criarCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(Tema.criarLabel("◈ CONFIGURAÇÕES DE NOTIFICAÇÕES", Tema.F_LABEL, Tema.TEXT3));
        card.add(Box.createVerticalStrut(10));

        JCheckBox chkFora    = criarCheck("Alerta quando animal sair do geofence", true);
        JCheckBox chkBateria = criarCheck("Alerta de bateria baixa (<20%)", true);
        JCheckBox chkVacina  = criarCheck("Lembrete de vacinação (30 dias antes)", true);
        JCheckBox chkSinal   = criarCheck("Alerta de coleira sem sinal", true);

        card.add(chkFora);    card.add(Box.createVerticalStrut(8));
        card.add(chkBateria); card.add(Box.createVerticalStrut(8));
        card.add(chkVacina);  card.add(Box.createVerticalStrut(8));
        card.add(chkSinal);   card.add(Box.createVerticalStrut(14));

        JTextField cEmail = Tema.criarCampo("email@fazenda.com");
        cEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        card.add(Tema.criarLabel("E-MAIL PARA NOTIFICAÇÕES", Tema.F_SMALL, Tema.TEXT3));
        card.add(Box.createVerticalStrut(4)); card.add(cEmail); card.add(Box.createVerticalStrut(14));

        JButton bSalvar = Tema.criarBotaoPrimario("✔ SALVAR");
        bSalvar.addActionListener(e -> {
            LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Alterou preferências de notificação");
            JOptionPane.showMessageDialog(this, "Preferências de notificação salvas!", "OK", JOptionPane.INFORMATION_MESSAGE);
        });
        card.add(bSalvar);
        p.add(card, BorderLayout.NORTH);
        return p;
    }

    // ─── Aba Log de Atividades ────────────────────────────────────────────────
    private JPanel criarAbaLog() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        p.add(Tema.criarLabel("◈ LOG DE ATIVIDADES DO SISTEMA", Tema.F_LABEL, Tema.TEXT3), BorderLayout.NORTH);

        DefaultListModel<String> lm = new DefaultListModel<>();
        for (String l : LogAtividades.getLogs()) lm.addElement(l);
        JList<String> lista = new JList<>(lm);
        lista.setBackground(Tema.BG3);
        lista.setForeground(Tema.TEXT2);
        lista.setFont(new Font("Courier New", Font.PLAIN, 11));
        lista.setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean sel, boolean foc) {
                JLabel lb = (JLabel) super.getListCellRendererComponent(l, v, i, sel, foc);
                lb.setBackground(i % 2 == 0 ? Tema.BG3 : new Color(22, 38, 24));
                lb.setForeground(Tema.TEXT2);
                lb.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
                return lb;
            }
        });
        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(BorderFactory.createLineBorder(Tema.BORDER, 1));
        p.add(scroll, BorderLayout.CENTER);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acoes.setBackground(Tema.BG);
        JButton btnLimpar = Tema.criarBotaoSecundario("LIMPAR LOG");
        btnLimpar.addActionListener(e -> lm.clear());
        JButton btnExport = Tema.criarBotaoSecundario("↓ EXPORTAR LOG");
        btnExport.addActionListener(e -> {
            try {
                String n = "log_siratech_" + System.currentTimeMillis() + ".txt";
                FileWriter fw = new FileWriter(n);
                for (String l : LogAtividades.getLogs()) fw.write(l + "\n");
                fw.close();
                JOptionPane.showMessageDialog(this, "Log exportado: " + new File(n).getAbsolutePath(), "OK",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao exportar.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        acoes.add(btnLimpar); acoes.add(btnExport);
        p.add(acoes, BorderLayout.SOUTH);
        return p;
    }

    // ─── Aba Banco de Dados ───────────────────────────────────────────────────
    private JPanel criarAbaBanco() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        if (!backend.authService.isAdmin()) {
            p.add(Tema.criarLabel("⚠ Apenas administradores podem acessar o banco de dados.", Tema.F_BODY, Tema.AMBER),
                    BorderLayout.NORTH);
            return p;
        }

        JPanel card = Tema.criarCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(Tema.criarLabel("◈ GERENCIAMENTO DO BANCO DE DADOS", Tema.F_LABEL, Tema.TEXT3));
        card.add(Box.createVerticalStrut(10));

        JPanel info = new JPanel(new GridLayout(0, 2, 8, 8));
        info.setBackground(Tema.CARD);
        addI2(info, "TIPO DE BANCO",  "SQLite");
        addI2(info, "ARQUIVO",        "siratech.db");
        addI2(info, "ANIMAIS",        String.valueOf(backend.animalService.totalAnimais()));
        addI2(info, "ALERTAS",        String.valueOf(backend.alertaService.listarTodos().size()));
        addI2(info, "COLARES",        String.valueOf(backend.colarService.listarTodos().size()));
        addI2(info, "FAZENDAS",       String.valueOf(backend.fazendaService.listarTodas().size()));
        card.add(info); card.add(Box.createVerticalStrut(14));

        JPanel bots = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bots.setBackground(Tema.CARD);

        JButton btnBackup = Tema.criarBotaoSecundario("↓ BACKUP DO BANCO");
        btnBackup.addActionListener(e -> {
            try {
                File src = new File("siratech.db");
                File dst = new File("siratech_backup_" + System.currentTimeMillis() + ".db");
                java.nio.file.Files.copy(src.toPath(), dst.toPath());
                LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Gerou backup do banco");
                JOptionPane.showMessageDialog(this, "Backup salvo: " + dst.getAbsolutePath(), "OK",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        JButton btnOtimizar = Tema.criarBotaoPrimario("OTIMIZAR");
        btnOtimizar.addActionListener(e -> {
            LogAtividades.registrar(backend.authService.getUsuarioLogado(), "Otimizou banco de dados");
            JOptionPane.showMessageDialog(this, "Banco otimizado!", "OK", JOptionPane.INFORMATION_MESSAGE);
        });
        bots.add(btnBackup); bots.add(btnOtimizar);
        card.add(bots);
        p.add(card, BorderLayout.NORTH);
        return p;
    }

    // ─── Utilitários ──────────────────────────────────────────────────────────
    private JCheckBox criarCheck(String texto, boolean selecionado) {
        JCheckBox c = new JCheckBox(texto, selecionado);
        c.setBackground(Tema.CARD);
        c.setForeground(Tema.TEXT2);
        c.setFont(Tema.F_BODY);
        return c;
    }

    private void addI2(JPanel p, String l, String v) {
        JPanel item = new JPanel(new BorderLayout(0, 2));
        item.setBackground(new Color(28, 46, 30));
        item.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        item.add(Tema.criarLabel(l, Tema.F_SMALL, Tema.TEXT3), BorderLayout.NORTH);
        item.add(Tema.criarLabel(v != null ? v : "—", Tema.F_BODY, Tema.GREENL), BorderLayout.CENTER);
        p.add(item);
    }
}
