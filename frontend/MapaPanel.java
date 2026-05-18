package frontend;

import backend.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.*;

/**
 * MapaPanel — exibe animais no mapa da fazenda.
 *
 * Modos de operação:
 *
 *   1. MODO ESTÁTICO (padrão original):
 *      Animais distribuídos em posições fixas por status ("Ativo" = dentro).
 *      Usado quando não há simulador rodando.
 *
 *   2. MODO AO VIVO (com simulador):
 *      Ativado via iniciarModoAoVivo(RastreamentoService, Fazenda).
 *      Um Timer consulta as últimas localizações do banco a cada 2 segundos
 *      e converte lat/lon reais em coordenadas relativas (0.0–1.0) do painel,
 *      refletindo exatamente o que o simulador está fazendo.
 */
public class MapaPanel extends JPanel {

    // ── Posições fixas — modo estático (compatibilidade original) ─────────────
    private static final double[][] POSICOES_DENTRO = {
        {0.22,0.38},{0.48,0.50},{0.63,0.28},{0.38,0.62},
        {0.54,0.70},{0.30,0.55},{0.70,0.55},{0.58,0.35},
        {0.42,0.42},{0.25,0.65},{0.68,0.45},{0.50,0.30}
    };
    private static final double[][] POSICOES_FORA = {
        {0.90,0.16},{0.04,0.84},{0.92,0.80},{0.05,0.12}
    };
    private static final double[][] TRILHA = {
        {0.90,0.16},{0.86,0.20},{0.82,0.25},{0.77,0.28},{0.72,0.32}
    };

    // ── Estado dos animais no mapa ────────────────────────────────────────────
    private List<double[]> posDentro = new ArrayList<>();
    private List<String>   nomDentro = new ArrayList<>();
    private List<double[]> posFora   = new ArrayList<>();
    private List<String>   nomFora   = new ArrayList<>();

    // ── Modo ao vivo ──────────────────────────────────────────────────────────
    private boolean modoAoVivo = false;
    private Timer   timerAoVivo;

    // Última posição conhecida por animal (id → [lat, lon, dentroFlag])
    private final Map<Integer, double[]> ultimasPosicoes = new HashMap<>();

    // Referência à fazenda para converter coordenadas
    private Fazenda fazendaAtiva;

    // ── Animação ──────────────────────────────────────────────────────────────
    private int     frame        = 0;
    private boolean mostrarNomes = false;
    private final   Random rnd   = new Random(42);

    // Badge "AO VIVO" — pisca enquanto modo ao vivo estiver ativo
    private boolean badgePulse = false;

    // ══════════════════════════════════════════════════════════════════════════
    // Construtores
    // ══════════════════════════════════════════════════════════════════════════

    /** Construtor legado — sem animais reais (compatibilidade). */
    public MapaPanel() {
        this(null, new ArrayList<>());
    }

    /** Construtor principal — recebe lista de animais da fazenda ativa. */
    public MapaPanel(Backend backend, List<Animal> animais) {
        setBackground(Tema.BG3);
        setPreferredSize(new Dimension(400, 270));
        setBorder(BorderFactory.createLineBorder(Tema.BORDER, 1));
        distribuirAnimais(animais);

        // Timer de animação
        new Timer(700, e -> { frame++; repaint(); }).start();

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                mostrarNomes = !mostrarNomes;
                repaint();
            }
        });
        setToolTipText("Clique para mostrar/ocultar nomes");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Modo Ao Vivo — API pública
    // ══════════════════════════════════════════════════════════════════════════

    public void iniciarModoAoVivo(RastreamentoService rastreamentoService, Fazenda fazenda) {
        if (modoAoVivo) return;
        this.modoAoVivo   = true;
        this.fazendaAtiva = fazenda;

        atualizarDosBanco(rastreamentoService);

        timerAoVivo = new Timer(2000, e -> {
            atualizarDosBanco(rastreamentoService);
            badgePulse = !badgePulse;
        });
        timerAoVivo.start();
        repaint();
    }

    public void pararModoAoVivo() {
        modoAoVivo = false;
        if (timerAoVivo != null) {
            timerAoVivo.stop();
            timerAoVivo = null;
        }
        repaint();
    }

    public boolean isModoAoVivo() {
        return modoAoVivo;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Modo Estático — API pública (original, preservada)
    // ══════════════════════════════════════════════════════════════════════════

    public void atualizarAnimais(List<Animal> animais) {
        if (modoAoVivo) return;
        rnd.setSeed(42);
        distribuirAnimais(animais);
        repaint();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Lógica interna — Modo Ao Vivo (original, preservada)
    // ══════════════════════════════════════════════════════════════════════════

    private void atualizarDosBanco(RastreamentoService rastreamentoService) {
        if (fazendaAtiva == null) return;
        try {
            List<Localizacao> recentes = rastreamentoService.buscarUltimasPosicoes(200);

            Map<Integer, Localizacao> porAnimal = new HashMap<>();
            for (Localizacao loc : recentes) {
                if (loc.getAnimal() == null) continue;
                int animalId = loc.getAnimal().getId();
                porAnimal.putIfAbsent(animalId, loc);
            }

            List<double[]> novoDentro    = new ArrayList<>();
            List<String>   nomNovoDentro = new ArrayList<>();
            List<double[]> novoFora      = new ArrayList<>();
            List<String>   nomNovoFora   = new ArrayList<>();

            for (Localizacao loc : porAnimal.values()) {
                double[] relativo = latLonParaRelativo(loc.getLatitude(), loc.getLongitude());
                relativo[0] = Math.max(0.02, Math.min(0.98, relativo[0]));
                relativo[1] = Math.max(0.02, Math.min(0.98, relativo[1]));

                String nome = loc.getAnimal().getNome() != null
                        ? loc.getAnimal().getNome() : "Animal";

                boolean fora = "Fora".equals(loc.getStatus());
                if (fora) { novoFora.add(relativo);   nomNovoFora.add(nome);   }
                else      { novoDentro.add(relativo);  nomNovoDentro.add(nome); }
            }

            SwingUtilities.invokeLater(() -> {
                posDentro = novoDentro;
                nomDentro = nomNovoDentro;
                posFora   = novoFora;
                nomFora   = nomNovoFora;
                repaint();
            });

        } catch (Exception e) {
            System.err.println("[MapaPanel] Erro ao atualizar do banco: " + e.getMessage());
        }
    }

    private double[] latLonParaRelativo(double lat, double lon) {
        double latCentro  = fazendaAtiva.getLatitudeCentro();
        double lonCentro  = fazendaAtiva.getLongitudeCentro();
        double raioMetros = fazendaAtiva.getRaioMetros() > 0
                ? fazendaAtiva.getRaioMetros() : 500.0;

        double dLat       = lat - latCentro;
        double dLon       = lon - lonCentro;
        double dLatMetros = dLat * 111320.0;
        double cosLat     = Math.cos(Math.toRadians(latCentro));
        double dLonMetros = dLon * 111320.0 * cosLat;

        double nx = dLonMetros / raioMetros;
        double ny = -dLatMetros / raioMetros;

        return new double[]{ 0.5 + nx * 0.40, 0.5 + ny * 0.40 };
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Lógica interna — Modo Estático (original, preservada)
    // ══════════════════════════════════════════════════════════════════════════

    private void distribuirAnimais(List<Animal> animais) {
        posDentro.clear(); nomDentro.clear();
        posFora.clear();   nomFora.clear();
        if (animais == null || animais.isEmpty()) return;

        int idxDentro = 0, idxFora = 0;
        for (Animal a : animais) {
            boolean ativo = "Ativo".equalsIgnoreCase(a.getStatus());
            if (ativo && idxDentro < POSICOES_DENTRO.length) {
                posDentro.add(POSICOES_DENTRO[idxDentro++]);
                nomDentro.add(a.getNome());
            } else if (!ativo && idxFora < POSICOES_FORA.length) {
                posFora.add(POSICOES_FORA[idxFora++]);
                nomFora.add(a.getNome()
                        + (a.getNumeroBrinco() != null ? " #" + a.getNumeroBrinco() : ""));
            } else if (idxDentro < POSICOES_DENTRO.length) {
                posDentro.add(POSICOES_DENTRO[idxDentro++]);
                nomDentro.add(a.getNome());
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Renderização
    // ══════════════════════════════════════════════════════════════════════════

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Antialiasing completo
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,        RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,   RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,      RenderingHints.VALUE_STROKE_PURE);

        int w = getWidth(), h = getHeight();

        // ── Fundo gradiente ───────────────────────────────────────────────────
        g2.setPaint(new GradientPaint(0, 0, new Color(12, 26, 14), w, h, new Color(7, 18, 9)));
        g2.fillRect(0, 0, w, h);

        // ── Área interna ──────────────────────────────────────────────────────
        g2.setColor(new Color(30, 80, 32, 40));
        g2.fillRoundRect(10, 8, w - 20, h - 16, 20, 20);

        // ── Grid ──────────────────────────────────────────────────────────────
        g2.setColor(new Color(46, 160, 46, 15));
        g2.setStroke(new BasicStroke(0.5f));
        for (int x = 0; x < w; x += 28) g2.drawLine(x, 0, x, h);
        for (int y = 0; y < h; y += 28) g2.drawLine(0, y, w, y);

        // ── Geofence animado ──────────────────────────────────────────────────
        g2.setColor(new Color(0, 180, 170, 18));
        g2.fillRoundRect(16, 12, w - 32, h - 24, 18, 18);
        g2.setColor(new Color(0, 180, 170, 160));
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10, new float[]{8, 5}, frame % 13));
        g2.drawRoundRect(16, 12, w - 32, h - 24, 18, 18);

        // ── Trilha (modo estático) ────────────────────────────────────────────
        if (!modoAoVivo) {
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i = 0; i < TRILHA.length - 1; i++) {
                g2.setColor(new Color(220, 50, 50, 40 + i * 35));
                g2.drawLine((int)(TRILHA[i][0]   * w), (int)(TRILHA[i][1]   * h),
                            (int)(TRILHA[i+1][0] * w), (int)(TRILHA[i+1][1] * h));
            }
        }

        // ── Mensagem quando não há animais ────────────────────────────────────
        if (posDentro.isEmpty() && posFora.isEmpty()) {
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRoundRect(w / 2 - 100, h / 2 - 14, 200, 28, 8, 8);
            g2.setColor(Tema.TEXT3);
            g2.setFont(Tema.F_SMALL);
            g2.drawString("Nenhum animal nesta fazenda", w / 2 - 88, h / 2 + 4);
            return;
        }

        // ── Animais DENTRO ────────────────────────────────────────────────────
        for (int i = 0; i < posDentro.size(); i++) {
            int ax = (int)(posDentro.get(i)[0] * w);
            int ay = (int)(posDentro.get(i)[1] * h);

            // Sombra
            g2.setColor(new Color(0, 0, 0, 50));
            g2.fillOval(ax - 4, ay - 2, 9, 5);

            // Ponto verde
            g2.setColor(Tema.GREEN3);
            g2.fillOval(ax - 6, ay - 6, 12, 12);
            g2.setColor(Tema.GREENL);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(ax - 6, ay - 6, 12, 12);

            // Nome
            if (mostrarNomes && i < nomDentro.size()) {
                String nome = nomDentro.get(i);
                g2.setColor(new Color(0, 0, 0, 150));
                g2.fillRoundRect(ax + 8, ay - 9, nome.length() * 5 + 6, 14, 4, 4);
                g2.setColor(Tema.GREENL);
                g2.setFont(Tema.F_SMALL);
                g2.drawString(nome, ax + 11, ay + 2);
            }
        }

        // ── Animais FORA (alerta pulsante) ────────────────────────────────────
        float alpha = 0.3f + 0.7f * Math.abs((float) Math.sin(frame * 0.4));
        for (int i = 0; i < posFora.size(); i++) {
            int ax = (int)(posFora.get(i)[0] * w);
            int ay = (int)(posFora.get(i)[1] * h);

            g2.setColor(new Color(220, 50, 50, (int)(alpha * 55)));
            g2.fillOval(ax - 16, ay - 16, 32, 32);
            g2.setColor(new Color(220, 50, 50, (int)(alpha * 255)));
            g2.fillOval(ax - 6, ay - 6, 12, 12);
            g2.setColor(new Color(255, 100, 100, (int)(alpha * 255)));
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(ax - 6, ay - 6, 12, 12);

            if (i < nomFora.size()) {
                String nome = nomFora.get(i);
                g2.setColor(new Color(0, 0, 0, 160));
                g2.fillRoundRect(ax + 10, ay - 9, nome.length() * 5 + 6, 14, 4, 4);
                g2.setColor(Tema.RED);
                g2.setFont(Tema.F_SMALL);
                g2.drawString(nome, ax + 13, ay + 2);
            }
        }

        // ── Contador de animais — pill mais legível ───────────────────────────
        String contador = posDentro.size() + " dentro   " + posFora.size() + " fora";
        int pillW = contador.length() * 5 + 20;
        g2.setColor(new Color(10, 24, 12, 180));
        g2.fillRoundRect(6, 6, pillW, 20, 8, 8);
        g2.setColor(new Color(34, 80, 36, 200));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(6, 6, pillW, 20, 8, 8);

        // Pontos coloridos no contador
        g2.setColor(Tema.GREEN3);
        g2.fillOval(13, 13, 6, 6);
        g2.setColor(Tema.TEXT2);
        g2.setFont(Tema.F_SMALL);
        g2.drawString(String.valueOf(posDentro.size()), 22, 21);

        int xMeio = 22 + String.valueOf(posDentro.size()).length() * 6 + 6;
        g2.setColor(new Color(100, 150, 105, 180));
        g2.drawString("  |  ", xMeio - 4, 21);

        g2.setColor(Tema.RED);
        g2.fillOval(xMeio + 22, 13, 6, 6);
        g2.setColor(posFora.size() > 0 ? Tema.RED : Tema.TEXT3);
        g2.drawString(String.valueOf(posFora.size()), xMeio + 31, 21);

        // ── Badge "AO VIVO" ───────────────────────────────────────────────────
        if (modoAoVivo) {
            int badgeAlpha = badgePulse ? 230 : 150;
            int bw = 72, bh = 20, bx = w - bw - 6, by = 6;
            g2.setColor(new Color(100, 15, 15, badgeAlpha));
            g2.fillRoundRect(bx, by, bw, bh, 8, 8);
            g2.setColor(new Color(220, 50, 50, badgeAlpha));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(bx, by, bw, bh, 8, 8);
            // Ponto pulsante
            g2.setColor(new Color(255, 80, 80, badgeAlpha));
            g2.fillOval(bx + 8, by + 7, 6, 6);
            g2.setColor(Color.WHITE);
            g2.setFont(Tema.F_SMALL);
            g2.drawString("AO VIVO", bx + 18, by + 14);
        } else {
            // Dica de clique (modo estático)
            g2.setColor(new Color(0, 0, 0, 120));
            g2.fillRoundRect(w - 108, h - 20, 104, 16, 5, 5);
            g2.setColor(Tema.TEXT3);
            g2.setFont(Tema.F_SMALL);
            g2.drawString("clique: nomes", w - 104, h - 8);
        }
    }
}
