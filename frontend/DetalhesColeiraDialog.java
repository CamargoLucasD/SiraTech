package frontend;

import backend.*;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.*;
import javax.swing.*;

public class DetalhesColeiraDialog extends JDialog {

    // ── helper de ícone ──────────────────────────────────────────────────────
    private static FlatSVGIcon ico(String name, int size) {
        return new FlatSVGIcon("icons/" + name + ".svg", size, size);
    }

    /**
     * @param owner   janela pai
     * @param c       coleira a exibir
     * @param backend acesso aos serviços
     * @param screen  referência à ColeiraScreen para refresh após ações (pode ser null)
     */
    public DetalhesColeiraDialog(Window owner, Colar c, Backend backend, ColeiraScreen screen) {
        super(owner, "Coleira: " + c.getId(), ModalityType.APPLICATION_MODAL);
        setSize(560, 520);
        setLocationRelativeTo(owner);
        setResizable(false);
        getContentPane().setBackground(Tema.BG);
        setLayout(new BorderLayout());

        // ── Header ───────────────────────────────────────────────────────────
        JPanel head = new JPanel(new BorderLayout());
        head.setBackground(new Color(18, 38, 48));
        head.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.CYAN, 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        // Título com ícone de coleira (zap = sinal GPS/ativo)
        JLabel lblTitulo = Tema.criarLabel("Coleira " + c.getId(),
                new Font("Segoe UI", Font.BOLD, 20), Tema.CYAN);
        lblTitulo.setIcon(ico("zap", 22));
        lblTitulo.setIconTextGap(10);
        head.add(lblTitulo, BorderLayout.WEST);

        // Badge de status
        JLabel lblStatus = Tema.criarLabel(
                c.isDisponivel() ? "DISPONÍVEL" : "EM USO",
                Tema.F_LABEL,
                c.isDisponivel() ? Tema.GREEN3 : Tema.AMBER);
        lblStatus.setIcon(c.isDisponivel()
                ? ico("zap", 14)
                : ico("users", 14));
        lblStatus.setIconTextGap(5);
        head.add(lblStatus, BorderLayout.EAST);
        add(head, BorderLayout.NORTH);

        // ── Corpo ─────────────────────────────────────────────────────────────
        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setBackground(Tema.BG);
        body.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JPanel grid = new JPanel(new GridLayout(0, 2, 8, 8));
        grid.setBackground(Tema.BG);
        addInfo(grid, "BATERIA",         c.getBateria() + "%");
        addInfo(grid, "NÍVEL SINAL",     c.getNivelSinal());
        addInfo(grid, "FREQUÊNCIA PING", c.getFrequenciaMinutos() + " min");
        addInfo(grid, "FIRMWARE",        c.getFirmware());

        // Animal vinculado — lógica original preservada
        String animalVinculado = "Nenhum";
        for (Animal a : backend.animalService.listarTodos())
            if (a.getColar() != null && a.getColar().getId().equals(c.getId())) {
                animalVinculado = a.getNome() + " #" + a.getNumeroBrinco();
                break;
            }
        addInfo(grid, "ANIMAL VINCULADO", animalVinculado);

        if (c.getUltimaLocalizacao() != null) {
            addInfo(grid, "ÚLTIMA LAT.", String.format("%.4f", c.getUltimaLocalizacao().getLatitude()));
            addInfo(grid, "ÚLTIMA LON.", String.format("%.4f", c.getUltimaLocalizacao().getLongitude()));
        }
        body.add(grid, BorderLayout.CENTER);

        // Barra de bateria — lógica original preservada
        JPanel batWrap = new JPanel(new BorderLayout(0, 4));
        batWrap.setBackground(Tema.CARD);
        batWrap.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JLabel lblBat = Tema.criarLabel("NÍVEL DE BATERIA", Tema.F_SMALL, Tema.TEXT3);
        lblBat.setIcon(ico("zap", 12));
        lblBat.setIconTextGap(5);
        batWrap.add(lblBat, BorderLayout.NORTH);

        JPanel barra = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Tema.BORDER);
                g2.fillRoundRect(0, 5, getWidth(), 22, 10, 10);
                int pct = c.getBateria();
                Color cor = pct > 50 ? Tema.GREEN3 : pct > 20 ? Tema.AMBER : Tema.RED;
                g2.setColor(cor);
                g2.fillRoundRect(0, 5, (int) (getWidth() * (pct / 100.0)), 22, 10, 10);
                g2.setColor(Tema.TEXT);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                String s = pct + "%";
                g2.drawString(s, (getWidth() - g2.getFontMetrics().stringWidth(s)) / 2, 21);
            }
        };
        barra.setBackground(Tema.CARD);
        barra.setPreferredSize(new Dimension(0, 32));
        batWrap.add(barra, BorderLayout.CENTER);
        body.add(batWrap, BorderLayout.SOUTH);

        add(body, BorderLayout.CENTER);

        // ── Rodapé com botões ─────────────────────────────────────────────────
        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
        south.setBackground(Tema.BG);

        // Botões admin — lógica original preservada
        if (backend.authService.isAdmin()) {
            JButton btnEditar = Tema.criarBotaoCyan("EDITAR");
            btnEditar.setIcon(ico("edit", 16));
            btnEditar.setIconTextGap(6);
            btnEditar.addActionListener(e -> {
                if (editarColeira(c, backend)) {
                    if (screen != null) screen.refresh();
                    dispose();
                }
            });
            south.add(btnEditar);

            JButton btnExcluir = Tema.criarBotaoPerigo("EXCLUIR");
            btnExcluir.setIcon(ico("trash-2", 16));
            btnExcluir.setIconTextGap(6);
            btnExcluir.addActionListener(e -> {
                if (excluirColeira(c, backend)) {
                    if (screen != null) screen.refresh();
                    dispose();
                }
            });
            south.add(btnExcluir);
        }

        JButton btnFechar = Tema.criarBotaoSecundario("FECHAR");
        btnFechar.setIcon(ico("x", 16));
        btnFechar.setIconTextGap(6);
        btnFechar.addActionListener(e -> dispose());
        south.add(btnFechar);

        add(south, BorderLayout.SOUTH);
        setVisible(true);
    }

    // ── Ação: Editar — lógica original preservada ────────────────────────────

    private boolean editarColeira(Colar c, Backend backend) {
        JPasswordField pf = Tema.criarSenha();
        if (JOptionPane.showConfirmDialog(this, pf,
                "Confirme sua senha para editar " + c.getId(),
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return false;
        if (!backend.authService.verificarSenha(new String(pf.getPassword()))) {
            JOptionPane.showMessageDialog(this, "Senha incorreta!", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        JTextField cBat  = Tema.criarCampo(String.valueOf(c.getBateria()));
        JTextField cFreq = Tema.criarCampo(String.valueOf(c.getFrequenciaMinutos()));
        JComboBox<String> cSinal = Tema.criarCombo("Forte", "Medio", "Fraco");
        cSinal.setSelectedItem(c.getNivelSinal());

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBackground(Tema.BG);
        form.add(Tema.criarLabel("BATERIA (%):",      Tema.F_SMALL, Tema.TEXT3)); form.add(cBat);
        form.add(Tema.criarLabel("SINAL:",            Tema.F_SMALL, Tema.TEXT3)); form.add(cSinal);
        form.add(Tema.criarLabel("FREQUÊNCIA (min):", Tema.F_SMALL, Tema.TEXT3)); form.add(cFreq);

        if (JOptionPane.showConfirmDialog(this, form, "Editar Coleira " + c.getId(),
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return false;

        try {
            int bat  = Integer.parseInt(cBat.getText().trim());
            int freq = Integer.parseInt(cFreq.getText().trim());
            if (bat < 0 || bat > 100 || freq <= 0) throw new NumberFormatException();
            c.setBateria(bat);
            c.setFrequenciaMinutos(freq);
            c.setNivelSinal(cSinal.getSelectedItem().toString());
            backend.colarService.atualizar(c);
            LogAtividades.registrar(backend.authService.getUsuarioLogado(),
                    "Editou coleira via detalhes: " + c.getId());
            JOptionPane.showMessageDialog(this,
                "Coleira " + c.getId() + " atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Bateria deve ser 0–100 e frequência deve ser positiva.",
                "Erro de validação", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erro ao atualizar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // ── Ação: Excluir — lógica original preservada ───────────────────────────

    private boolean excluirColeira(Colar c, Backend backend) {
        JPasswordField pf = Tema.criarSenha();
        if (JOptionPane.showConfirmDialog(this, pf,
                "Confirme sua senha para excluir " + c.getId(),
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return false;
        if (!backend.authService.verificarSenha(new String(pf.getPassword()))) {
            JOptionPane.showMessageDialog(this, "Senha incorreta!", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        int conf = JOptionPane.showConfirmDialog(this,
                "Excluir permanentemente a coleira " + c.getId() + "?\nEsta ação não pode ser desfeita.",
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (conf != JOptionPane.YES_OPTION) return false;

        try {
            backend.colarService.excluir(c.getId(), backend.animalService);
            LogAtividades.registrar(backend.authService.getUsuarioLogado(),
                    "Excluiu coleira via detalhes: " + c.getId());
            JOptionPane.showMessageDialog(this,
                "Coleira " + c.getId() + " excluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Não é possível excluir", JOptionPane.WARNING_MESSAGE);
            return false;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erro ao excluir: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // ── helper ───────────────────────────────────────────────────────────────

    private void addInfo(JPanel p, String label, String valor) {
        JPanel item = new JPanel(new BorderLayout(0, 2));
        item.setBackground(Tema.CARD);
        item.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        item.add(Tema.criarLabel(label, Tema.F_SMALL, Tema.TEXT3), BorderLayout.NORTH);
        item.add(Tema.criarLabel(valor != null ? valor : "—", Tema.F_BODY, Tema.TEXT), BorderLayout.CENTER);
        p.add(item);
    }
}
