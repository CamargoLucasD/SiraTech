package frontend;

import backend.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import javax.swing.*;

/**
 * MapaPanel — exibe animais no mapa da fazenda.
 *
 * Modos de operação:
 *
 *   1. MODO ESTÁTICO (padrão):
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

    // ── Posições fixas — modo estático ────────────────────────────────────────
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
    private Fazenda fazendaAtiva;

    // ── Animação ──────────────────────────────────────────────────────────────
    private int     frame        = 0;
    private boolean mostrarNomes = false;
    private boolean badgePulse   = false;
    private final   Random rnd   = new Random(42);

    // ── API da V1 ─────────────────────────────────────────────────────────────
    private Animal           animalSelecionado = null;
    private Consumer<Animal> onAnimalClick;
    private final List<Notificacao> notificacoes = new ArrayList<>();

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
        setFocusable(true);
        distribuirAnimais(animais);

        // Timer de animação
        new Timer(700, e -> { frame++; repaint(); }).start();

        addMouseListener(new MouseAdapter() {
            @Override
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
    // Modo Estático — API pública
    // ══════════════════════════════════════════════════════════════════════════

    public void atualizarAnimais(List<Animal> animais) {
        if (modoAoVivo) return;
        rnd.setSeed(42);
        distribuirAnimais(animais);
        repaint();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // API da V1 — seleção, clique e notificações
    // ══════════════════════════════════════════════════════════════════════════

    public void setOnAnimalClick(Consumer<Animal> cb) {
        this.onAnimalClick = cb;
    }

    public void selecionarAnimal(Animal a) {
        this.animalSelecionado = a;
        repaint();
    }

    public void deselecionarAnimal() {
        this.animalSelecionado = null;
        repaint();
    }

    public void adicionarNotificacao(String texto, Color cor) {
        notificacoes.add(new Notificacao(texto, cor));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Lógica interna — Modo Ao Vivo
    // ══════════════════════════════════════════════════════════════════════════

    private void atualizarDosBanco(RastreamentoService rastreamentoService) {
        if (fazendaAtiva == null) return;
        try {
            List<Localizacao> recentes = rastreamentoService.buscarUltimasPosicoes(200);

            Map<Integer, Localizacao> porAnimal = new HashMap<>();
            for (Localizacao loc : recentes) {
                if (loc.getAnimal() == null) continue;
                porAnimal.putIfAbsent(loc.getAnimal().getId(), loc);
            }

            List<double[]> novoDentro    = new ArrayList<>();
            List<String>   nomNovoDentro = new ArrayList<>();
            List<double[]> novoFora      = new ArrayList<>();
            List<String>   nomNovoFora   = new ArrayList<>();

            for (Localizacao loc : porAnimal.values()) {
                double[] rel = latLonParaRelativo(loc.getLatitude(), loc.getLongitude());
                rel[0] = Math.max(0.02, Math.min(0.98, rel[0]));
                rel[1] = Math.max(0.02, Math.min(0.98, rel[1]));

                String nome = loc.getAnimal().getNome() != null
                        ? loc.getAnimal().getNome() : "Animal";

                if ("Fora".equals(loc.getStatus())) {
                    novoFora.add(rel);   nomNovoFora.add(nome);
                } else {
                    novoDentro.add(rel); nomNovoDentro.add(nome);
                }
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
    // Lógica interna — Modo Estático
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
    // Renderização — gráficos da V2
    // ══════════════════════════════════════════════════════════════════════════

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,    RenderingHints.VALUE_STROKE_PURE);

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
            desenharNotificacoes(g2, w, h);
            return;
        }

        // ── Animais DENTRO ────────────────────────────────────────────────────
        for (int i = 0; i < posDentro.size(); i++) {
            int ax = (int)(posDentro.get(i)[0] * w);
            int ay = (int)(posDentro.get(i)[1] * h);

            boolean selecionado = (animalSelecionado != null && i < nomDentro.size()
                    && nomDentro.get(i).equals(animalSelecionado.getNome()));

            // Anel de seleção
            if (selecionado) {
                float pulso = (float)(0.6 + 0.4 * Math.sin(frame * 0.4));
                int r = (int)(18 + 4 * pulso);
                g2.setColor(new Color(255, 230, 60, (int)(200 * pulso)));
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawOval(ax - r, ay - r, r * 2, r * 2);
                g2.setColor(new Color(255, 240, 100, 50));
                g2.fillOval(ax - r, ay - r, r * 2, r * 2);
            }

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

        // ── Contador de animais — pill ────────────────────────────────────────
        String contador = posDentro.size() + " dentro   " + posFora.size() + " fora";
        int pillW = contador.length() * 5 + 20;
        g2.setColor(new Color(10, 24, 12, 180));
        g2.fillRoundRect(6, 6, pillW, 20, 8, 8);
        g2.setColor(new Color(34, 80, 36, 200));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(6, 6, pillW, 20, 8, 8);

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
            g2.setColor(new Color(255, 80, 80, badgeAlpha));
            g2.fillOval(bx + 8, by + 7, 6, 6);
            g2.setColor(Color.WHITE);
            g2.setFont(Tema.F_SMALL);
            g2.drawString("AO VIVO", bx + 18, by + 14);
        } else {
            g2.setColor(new Color(0, 0, 0, 120));
            g2.fillRoundRect(w - 108, h - 20, 104, 16, 5, 5);
            g2.setColor(Tema.TEXT3);
            g2.setFont(Tema.F_SMALL);
            g2.drawString("clique: nomes", w - 104, h - 8);
        }

        // ── Notificações flutuantes ───────────────────────────────────────────
        desenharNotificacoes(g2, w, h);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Notificações (da V1)
    // ══════════════════════════════════════════════════════════════════════════

    private void desenharNotificacoes(Graphics2D g, int w, int h) {
        notificacoes.removeIf(Notificacao::expirou);
        int baseY = h - 28;
        for (int i = notificacoes.size() - 1; i >= 0; i--) {
            notificacoes.get(i).desenhar(g, 16, baseY);
            baseY -= 44;
        }
    }

    private static class Notificacao {
        private final String texto;
        private final Color  cor;
        private final long   criado;
        private static final long DURACAO = 4000;

        Notificacao(String texto, Color cor) {
            this.texto  = texto;
            this.cor    = cor;
            this.criado = System.currentTimeMillis();
        }

        boolean expirou() { return System.currentTimeMillis() - criado > DURACAO; }

        void desenhar(Graphics2D g, int x, int y) {
            long age = System.currentTimeMillis() - criado;
            float al = age < 300 ? age / 300f
                     : age > DURACAO - 500 ? (DURACAO - age) / 500f : 1f;
            al = Math.max(0, Math.min(1, al));

            Font f = new Font("SansSerif", Font.BOLD, 12);
            g.setFont(f);
            FontMetrics fm = g.getFontMetrics();
            int tw = fm.stringWidth(texto), pw = tw + 24, ph = 28;
            int bx = x, by = y - ph;

            g.setColor(new Color(20, 22, 26, (int)(210 * al)));
            g.fillRoundRect(bx, by, pw, ph, 8, 8);

            Stroke s = g.getStroke();
            g.setColor(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), (int)(190 * al)));
            g.setStroke(new BasicStroke(1.5f));
            g.drawRoundRect(bx, by, pw, ph, 8, 8);
            g.setStroke(s);

            // Barra colorida lateral
            g.setColor(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), (int)(230 * al)));
            g.fillRoundRect(bx, by + 4, 4, ph - 8, 3, 3);

            g.setColor(new Color(230, 230, 220, (int)(225 * al)));
            g.drawString(texto, bx + 12, by + ph / 2 + fm.getAscent() / 2 - 2);
        }
    }
}
