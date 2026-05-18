package frontend;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class StatCard extends JPanel {
    private JLabel lblValor;

    public StatCard(String label, String valor, Color corValor, String sub) {
        setBackground(Tema.CARD);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.BORDER, 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(Tema.criarLabel(label, Tema.F_SMALL, Tema.TEXT3));
        add(Box.createVerticalStrut(8));
        lblValor = Tema.criarLabel(valor, Tema.F_BIG, corValor);
        add(lblValor);
        add(Box.createVerticalStrut(4));
        add(Tema.criarLabel(sub, Tema.F_SMALL, Tema.TEXT3));
    }

    public void atualizar(String novoValor) {
        lblValor.setText(novoValor);
        revalidate(); repaint();
    }
}
