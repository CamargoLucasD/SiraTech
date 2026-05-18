package frontend;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class StatCard extends JPanel {

    private JLabel lblValor;

    // ── Ícone SVG helper ──────────────────────────────────────────────────────
    private static FlatSVGIcon ico(String name, int size) {
        try {
            return new FlatSVGIcon("icons/" + name + ".svg", size, size);
        } catch (Exception e) {
            return null;
        }
    }

    // ── Construtor original — mantém compatibilidade total ────────────────────
    public StatCard(String label, String valor, Color corValor, String sub) {
        this(label, valor, corValor, sub, null);
    }

    // ── Construtor estendido — aceita nome do ícone SVG opcional ──────────────
    public StatCard(String label, String valor, Color corValor, String sub, String nomeIcone) {
        setBackground(Tema.CARD);
        setLayout(new BorderLayout());

        // Barra de acento colorida no topo do card
        JPanel barraTop = new JPanel();
        barraTop.setBackground(corValor);
        barraTop.setPreferredSize(new Dimension(0, 3));
        add(barraTop, BorderLayout.NORTH);

        // Borda composta: linha + padding interno
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.BORDER, 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));

        // Painel interno com BoxLayout vertical
        JPanel inner = new JPanel();
        inner.setBackground(Tema.CARD);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        // Linha do topo: label + ícone (se houver)
        JPanel topoRow = new JPanel(new BorderLayout());
        topoRow.setBackground(Tema.CARD);
        topoRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        JLabel lblNome = Tema.criarLabel(label, Tema.F_SMALL, Tema.TEXT3);
        topoRow.add(lblNome, BorderLayout.WEST);

        if (nomeIcone != null) {
            FlatSVGIcon icone = ico(nomeIcone, 14);
            if (icone != null) {
                JLabel icoLbl = new JLabel(icone);
                icoLbl.setOpaque(false);
                topoRow.add(icoLbl, BorderLayout.EAST);
            }
        }
        inner.add(topoRow);
        inner.add(Box.createVerticalStrut(10));

        // Valor principal em destaque
        lblValor = Tema.criarLabel(valor, Tema.F_BIG, corValor);
        inner.add(lblValor);
        inner.add(Box.createVerticalStrut(5));

        // Sublabel descritivo
        JLabel lblSub = Tema.criarLabel(sub, Tema.F_SMALL, Tema.TEXT3);
        inner.add(lblSub);

        add(inner, BorderLayout.CENTER);

        // Hover sutil: aumenta brilho da borda
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(corValor.darker(), 1),
                        BorderFactory.createEmptyBorder(14, 16, 14, 16)));
                repaint();
            }
            public void mouseExited(MouseEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Tema.BORDER, 1),
                        BorderFactory.createEmptyBorder(14, 16, 14, 16)));
                repaint();
            }
        });
    }

    // ── Atualização do valor em tempo real ────────────────────────────────────
    public void atualizar(String novoValor) {
        lblValor.setText(novoValor);
        revalidate();
        repaint();
    }
}
