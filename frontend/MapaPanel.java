package frontend;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MapaPanel extends JPanel {

    private final double[][] dentro      = {{0.22,0.38},{0.48,0.50},{0.63,0.28},{0.38,0.62},{0.54,0.70},{0.30,0.55},{0.70,0.55},{0.58,0.35}};
    private final String[]   nomesDentro = {"Mimosa","Estrela","Bela","Rosa","Nuvem","Flor","Cris","Luna"};
    private final double[][] fora        = {{0.90,0.16},{0.04,0.84}};
    private final String[]   nomesFora   = {"Flor #A18","Nuvem #A02"};
    private final double[][] trilha      = {{0.90,0.16},{0.86,0.20},{0.82,0.25},{0.77,0.28},{0.72,0.32}};
    private int     frame        = 0;
    private boolean mostrarNomes = false;

    public MapaPanel() {
        setBackground(Tema.BG3);
        setPreferredSize(new Dimension(400, 270));
        setBorder(BorderFactory.createLineBorder(Tema.BORDER, 1));
        new javax.swing.Timer(700, e -> { frame++; repaint(); }).start();
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { mostrarNomes = !mostrarNomes; repaint(); }
        });
        setToolTipText("Clique para mostrar/ocultar nomes");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();

        g2.setPaint(new GradientPaint(0, 0, new Color(12,26,14), w, h, new Color(7,18,9)));
        g2.fillRect(0, 0, w, h);

        g2.setColor(new Color(30, 80, 32, 40));
        g2.fillRoundRect(10, 8, w-20, h-16, 20, 20);

        g2.setColor(new Color(46, 160, 46, 15));
        g2.setStroke(new BasicStroke(0.5f));
        for (int x = 0; x < w; x += 28) g2.drawLine(x, 0, x, h);
        for (int y = 0; y < h; y += 28) g2.drawLine(0, y, w, y);

        g2.setColor(new Color(0, 180, 170, 18));
        g2.fillRoundRect(16, 12, w-32, h-24, 18, 18);
        g2.setColor(new Color(0, 180, 170, 160));
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{8,5}, frame%13));
        g2.drawRoundRect(16, 12, w-32, h-24, 18, 18);

        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < trilha.length-1; i++) {
            g2.setColor(new Color(220, 50, 50, 40+i*35));
            g2.drawLine((int)(trilha[i][0]*w),(int)(trilha[i][1]*h),(int)(trilha[i+1][0]*w),(int)(trilha[i+1][1]*h));
        }

        for (int i = 0; i < dentro.length; i++) {
            int ax=(int)(dentro[i][0]*w), ay=(int)(dentro[i][1]*h);
            g2.setColor(new Color(0,0,0,50)); g2.fillOval(ax-4,ay-2,9,5);
            g2.setColor(Tema.GREEN3); g2.fillOval(ax-6,ay-6,12,12);
            g2.setColor(Tema.GREENL); g2.setStroke(new BasicStroke(1.5f)); g2.drawOval(ax-6,ay-6,12,12);
            if (mostrarNomes && i < nomesDentro.length) {
                g2.setColor(new Color(0,0,0,140));
                g2.fillRoundRect(ax+8,ay-9,nomesDentro[i].length()*5+4,13,4,4);
                g2.setColor(Tema.GREENL); g2.setFont(Tema.F_SMALL); g2.drawString(nomesDentro[i],ax+10,ay+2);
            }
        }

        float alpha = 0.3f + 0.7f * Math.abs((float) Math.sin(frame * 0.4));
        for (int i = 0; i < fora.length; i++) {
            int ax=(int)(fora[i][0]*w), ay=(int)(fora[i][1]*h);
            g2.setColor(new Color(220,50,50,(int)(alpha*55))); g2.fillOval(ax-16,ay-16,32,32);
            g2.setColor(new Color(220,50,50,(int)(alpha*255))); g2.fillOval(ax-6,ay-6,12,12);
            g2.setColor(new Color(255,100,100,(int)(alpha*255))); g2.setStroke(new BasicStroke(2f)); g2.drawOval(ax-6,ay-6,12,12);
            g2.setColor(new Color(0,0,0,150)); g2.fillRoundRect(ax+10,ay-9,nomesFora[i].length()*5+4,13,4,4);
            g2.setColor(Tema.RED); g2.setFont(Tema.F_SMALL); g2.drawString(nomesFora[i],ax+12,ay+2);
        }

        g2.setColor(new Color(0,0,0,150)); g2.fillRoundRect(4,4,140,18,6,6);
        g2.setColor(Tema.TEXT3); g2.setFont(Tema.F_SMALL);
        g2.drawString("⬤ "+dentro.length+" dentro  ⚠ "+fora.length+" fora", 8, 16);

        g2.setColor(new Color(0,0,0,120)); g2.fillRoundRect(w-104,h-18,100,14,4,4);
        g2.setColor(Tema.TEXT3); g2.drawString("clique: nomes", w-102, h-7);
    }
}
