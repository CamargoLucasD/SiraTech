package frontend;

import backend.*;
import java.awt.*;
import javax.swing.*;

public class DetalhesColeiraDialog extends JDialog {

    public DetalhesColeiraDialog(Window owner, Colar c, Backend backend) {
        super(owner, "Coleira: " + c.getId(), ModalityType.APPLICATION_MODAL);
        setSize(560, 460);
        setLocationRelativeTo(owner);
        setResizable(false);
        getContentPane().setBackground(Tema.BG);
        setLayout(new BorderLayout());

        // Header
        JPanel head = new JPanel(new BorderLayout());
        head.setBackground(new Color(18, 38, 48));
        head.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.CYAN, 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        head.add(Tema.criarLabel("📡  Coleira " + c.getId(),
                new Font("Segoe UI", Font.BOLD, 20), Tema.CYAN), BorderLayout.WEST);
        head.add(Tema.criarLabel(c.isDisponivel() ? "DISPONÍVEL" : "EM USO",
                Tema.F_LABEL, c.isDisponivel() ? Tema.GREEN3 : Tema.AMBER), BorderLayout.EAST);
        add(head, BorderLayout.NORTH);

        // Corpo
        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setBackground(Tema.BG);
        body.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JPanel grid = new JPanel(new GridLayout(0, 2, 8, 8));
        grid.setBackground(Tema.BG);
        addI(grid, "BATERIA",         c.getBateria() + "%");
        addI(grid, "NÍVEL SINAL",     c.getNivelSinal());
        addI(grid, "FREQUÊNCIA PING", c.getFrequenciaMinutos() + " min");
        addI(grid, "FIRMWARE",        c.getFirmware());

        // Localizar o animal vinculado
        String an = "Nenhum";
        for (Animal a : backend.animalService.listarTodos())
            if (a.getColar() != null && a.getColar().getId().equals(c.getId())) {
                an = a.getNome() + " #" + a.getNumeroBrinco();
                break;
            }
        addI(grid, "ANIMAL VINCULADO", an);

        if (c.getUltimaLocalizacao() != null) {
            addI(grid, "ÚLTIMA LAT.", String.format("%.4f", c.getUltimaLocalizacao().getLatitude()));
            addI(grid, "ÚLTIMA LON.", String.format("%.4f", c.getUltimaLocalizacao().getLongitude()));
        }
        body.add(grid, BorderLayout.CENTER);

        // Barra de bateria
        JPanel batWrap = new JPanel(new BorderLayout(0, 4));
        batWrap.setBackground(Tema.CARD);
        batWrap.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        batWrap.add(Tema.criarLabel("NÍVEL DE BATERIA", Tema.F_SMALL, Tema.TEXT3), BorderLayout.NORTH);
        JPanel barra = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Tema.BORDER); g2.fillRoundRect(0, 5, getWidth(), 22, 10, 10);
                int pct = c.getBateria();
                Color cor = pct > 50 ? Tema.GREEN3 : pct > 20 ? Tema.AMBER : Tema.RED;
                g2.setColor(cor); g2.fillRoundRect(0, 5, (int)(getWidth()*(pct/100.0)), 22, 10, 10);
                g2.setColor(Tema.TEXT); g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                String s = pct + "%";
                g2.drawString(s, (getWidth() - g2.getFontMetrics().stringWidth(s)) / 2, 21);
            }
        };
        barra.setBackground(Tema.CARD);
        barra.setPreferredSize(new Dimension(0, 32));
        batWrap.add(barra, BorderLayout.CENTER);
        body.add(batWrap, BorderLayout.SOUTH);

        add(body, BorderLayout.CENTER);

        // Botão fechar
        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
        south.setBackground(Tema.BG);
        JButton btn = Tema.criarBotaoPrimario("FECHAR");
        btn.addActionListener(e -> dispose());
        south.add(btn);
        add(south, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void addI(JPanel p, String l, String v) {
        JPanel item = new JPanel(new BorderLayout(0, 2));
        item.setBackground(Tema.CARD);
        item.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        item.add(Tema.criarLabel(l, Tema.F_SMALL, Tema.TEXT3), BorderLayout.NORTH);
        item.add(Tema.criarLabel(v != null ? v : "—", Tema.F_BODY, Tema.TEXT), BorderLayout.CENTER);
        p.add(item);
    }
}
