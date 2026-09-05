package principal;

import model.CarData;
import model.Piloto;
import model.Sessao;
import model.Volta;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Window extends JFrame {

    // PALETA
    private static final Color COR_FUNDO       = new Color(245, 246, 248);
    private static final Color COR_PRIMARIA    = new Color(30, 30, 46);
    private static final Color COR_DESTAQUE    = new Color(220, 38, 38);   // vermelho estilo F1
    private static final Color COR_TEXTO       = new Color(30, 30, 30);

    // FONTES
    private static final Font  FONTE_LABEL     = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font  FONTE_BOTAO     = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font  FONTE_TITULO    = new Font("Segoe UI", Font.BOLD, 18);

    // VARIÁVEIS
    Sessao sessao;

    // JPANELS
    JPanel painelSuperior = new JPanel();
    JPanel painelMedio = new JPanel();
    JPanel painelInferior = new JPanel();

    // CAMPOS DA SESSAO
    JTextField campoAno = new JTextField();
    JTextField campoPais = new JTextField();
    JTextField campoTipo = new JTextField();

    // COMBOBOX
    JComboBox<Piloto> combo = new JComboBox<>();

    // BOTÕES
    JButton buscarSessao = new JButton("Buscar Sessão");
    JButton buscarPiloto = new JButton("Buscar Piloto");

    // LABEL DO PILOTO
    JLabel nomePiloto = new JLabel("Selecione um piloto");

    // GRÁFICOS
    ChartPanel tempoVolta = this.getEmptyChart("Tempo de Volta", "Volta", "Duração");
    ChartPanel dadosSetor1 = this.getEmptyChart("Dados do setor 1", "Segundo", "Valor");
    ChartPanel dadosSetor2 = this.getEmptyChart("Dados do setor 2", "Segundo", "Valor");
    ChartPanel dadosSetor3 = this.getEmptyChart("Dados do setor 3", "Segundo", "Valor");
    ChartPanel speedSetor1 = this.getEmptyChart("Velocidades do setor 1", "Segundo", "Valor(km/h)");
    ChartPanel speedSetor2 = this.getEmptyChart("Velocidades do setor 2", "Segundo", "Valor(km/h)");
    ChartPanel speedSetor3 = this.getEmptyChart("Velocidades do setor 3", "Segundo", "Valor(km/h)");

    public Window(Service servico) {
        // CONFIGURAÇÕES BÁSICAS
        setTitle("Driver Data");
        setSize(950, 700);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COR_FUNDO);
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));

        // LISTENERS
        buscarSessao.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ano = campoAno.getText();
                String tipo = campoTipo.getText();
                String pais = campoPais.getText();

                sessao = servico.getSessao(ano, tipo, pais);

                combo.removeAllItems();
                for (Piloto p : sessao.getPilotos().values()) {
                    combo.addItem(p);
                }
            }
        });

        buscarPiloto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Piloto selecionado = (Piloto) combo.getSelectedItem();
                if (selecionado == null) return;

                if (!selecionado.isComplete()) {
                    servico.fetchVoltas(sessao, selecionado);
                    servico.fetchPits(sessao, selecionado);
                    selecionado.setComplete();
                }

                if (!selecionado.getFastest_lap().isComplete()) {
                    servico.fetchCarData(sessao, selecionado, selecionado.getFastest_lap().getLap_number());
                    selecionado.getFastest_lap().setComplete();
                }

                nomePiloto.setText(selecionado.toString());

                painelInferior.removeAll();
                tempoVolta = getLapTimeChart(selecionado);
                dadosSetor1 = getLapBrakeThrottleChart(selecionado, selecionado.getFastest_lap().getLap_number(), 1);
                dadosSetor2 = getLapBrakeThrottleChart(selecionado, selecionado.getFastest_lap().getLap_number(), 2);
                dadosSetor3 = getLapBrakeThrottleChart(selecionado, selecionado.getFastest_lap().getLap_number(), 3);
                speedSetor1 = getLapSpeedChart(selecionado, selecionado.getFastest_lap().getLap_number(), 1);
                speedSetor2 = getLapSpeedChart(selecionado, selecionado.getFastest_lap().getLap_number(), 2);
                speedSetor3 = getLapSpeedChart(selecionado, selecionado.getFastest_lap().getLap_number(), 3);
                painelInferior.add(tempoVolta);
                painelInferior.add(dadosSetor1);
                painelInferior.add(dadosSetor2);
                painelInferior.add(dadosSetor3);
                painelInferior.add(speedSetor1);
                painelInferior.add(speedSetor2);
                painelInferior.add(speedSetor3);
                painelInferior.revalidate();
                painelInferior.repaint();
            }
        });

        // PAINEL SUPERIOR
        painelSuperior.setLayout(new GridBagLayout());
        painelSuperior.setBackground(COR_FUNDO);
        painelSuperior.setBorder(criarBordaTitulada("Buscar Sessão"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        adicionarCampoComLabel(painelSuperior, gbc, "Ano", campoAno, 0);
        adicionarCampoComLabel(painelSuperior, gbc, "País", campoPais, 1);
        adicionarCampoComLabel(painelSuperior, gbc, "Tipo", campoTipo, 2);

        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        estilizarBotaoPrimario(buscarSessao);
        painelSuperior.add(buscarSessao, gbc);

        // PAINEL MEDIO
        painelMedio.setLayout(new BoxLayout(painelMedio, BoxLayout.X_AXIS));
        painelMedio.setBackground(COR_FUNDO);
        painelMedio.setBorder(criarBordaTitulada("Piloto"));

        combo.setFont(FONTE_LABEL);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        combo.setPreferredSize(new Dimension(250, 32));

        estilizarBotaoPrimario(buscarPiloto);
        buscarPiloto.setMaximumSize(new Dimension(150, 32));
        buscarPiloto.setPreferredSize(new Dimension(150, 32));

        nomePiloto.setFont(FONTE_TITULO);
        nomePiloto.setForeground(COR_PRIMARIA);
        nomePiloto.setBorder(new EmptyBorder(0, 15, 0, 0));

        painelMedio.add(combo);
        painelMedio.add(Box.createHorizontalStrut(10));
        painelMedio.add(buscarPiloto);
        painelMedio.add(Box.createHorizontalStrut(15));
        painelMedio.add(nomePiloto);
        painelMedio.add(Box.createHorizontalGlue());

        // PAINEL INFERIOR
        painelInferior.setLayout(new BoxLayout(painelInferior, BoxLayout.Y_AXIS));
        painelInferior.setBackground(Color.WHITE);
        painelInferior.setBorder(criarBordaTitulada("Gráfico de Desempenho"));

        painelInferior.add(tempoVolta);
        painelInferior.add(dadosSetor1);
        painelInferior.add(dadosSetor2);
        painelInferior.add(dadosSetor3);
        painelInferior.add(speedSetor1);
        painelInferior.add(speedSetor2);
        painelInferior.add(speedSetor3);

        JScrollPane scroll = new JScrollPane(painelInferior);
        scroll.getVerticalScrollBar().setUnitIncrement(10);

        // MONTAGEM FINAL
        JPanel topo = new JPanel(new BorderLayout(0, 10));
        topo.setBackground(COR_FUNDO);
        topo.add(painelSuperior, BorderLayout.NORTH);
        topo.add(painelMedio, BorderLayout.SOUTH);

        add(topo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        setVisible(true);
    }

    // HELPERS DE ESTILO

    // CRIA CAMPOS JTEXTFIELD COM SEUS RESPECTIVOS JLABEL
    private void adicionarCampoComLabel(JPanel painel, GridBagConstraints gbc, String texto, JTextField campo, int coluna) {
        JLabel label = new JLabel(texto);
        label.setFont(FONTE_LABEL);
        label.setForeground(COR_TEXTO);

        campo.setFont(FONTE_LABEL);
        campo.setPreferredSize(new Dimension(100, 30));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(4, 8, 4, 8)
        ));

        gbc.gridx = coluna;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.gridheight = 1;
        painel.add(label, gbc);

        gbc.gridy = 1;
        painel.add(campo, gbc);
    }

    // ESTILIZA BOTÃO
    private void estilizarBotaoPrimario(JButton botao) {
        botao.setFont(FONTE_BOTAO);
        botao.setForeground(Color.WHITE);
        botao.setBackground(COR_DESTAQUE);
        botao.setFocusPainted(false);
        botao.setBorder(new EmptyBorder(8, 16, 8, 16));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // CRIA UMA BORDA COM TEXTO PARA UM COMPONENTE
    private TitledBorder criarBordaTitulada(String titulo) {
        TitledBorder borda = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)), titulo);
        borda.setTitleFont(new Font("Segoe UI", Font.BOLD, 12));
        borda.setTitleColor(COR_PRIMARIA);
        return borda;
    }

    // RETORNA UM CHARTPANEL PREENCHIDO DOS TEMPOS DE VOLTA
    private ChartPanel getLapTimeChart(Piloto piloto) {
        XYSeries lapSerie = new XYSeries("Tempo de volta");
        XYSeries setor1Serie = new XYSeries("Setor 1");
        XYSeries setor2Serie = new XYSeries("Setor 2");
        XYSeries setor3Serie = new XYSeries("Setor 3");
        for (Volta volta : piloto.getVoltas().values()) {
            lapSerie.add(volta.getLap_number(), volta.getLap_duration());
            setor1Serie.add(volta.getLap_number(), volta.getSectorDurations().get(1));
            setor2Serie.add(volta.getLap_number(), volta.getSectorDurations().get(2));
            setor3Serie.add(volta.getLap_number(), volta.getSectorDurations().get(3));
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(lapSerie);
        dataset.addSeries(setor1Serie);
        dataset.addSeries(setor2Serie);
        dataset.addSeries(setor3Serie);
        JFreeChart chart = ChartFactory.createXYLineChart("Tempo de volta", "Volta", "Tempo", dataset);
        chart.setBackgroundPaint(Color.WHITE);

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));
        renderer.setSeriesStroke(1, new BasicStroke(3.0f));
        renderer.setSeriesStroke(2, new BasicStroke(3.0f));
        renderer.setSeriesStroke(3, new BasicStroke(3.0f));

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(new Color(250, 250, 250));
        plot.setDomainGridlinePaint(new Color(230, 230, 230));
        plot.setRangeGridlinePaint(new Color(230, 230, 230));
        plot.getRenderer().setSeriesPaint(0, COR_DESTAQUE);

        NumberAxis eixoy = (NumberAxis) plot.getRangeAxis();
        eixoy.setTickUnit(new NumberTickUnit(10));
        eixoy.setRange(piloto.getFastest_sector() - 10, piloto.getSlowest_lap().getLap_duration() + 10);

        ValueMarker sector1 = new ValueMarker(piloto.getFastest_sectors().get(1).getLap_number());
        sector1.setPaint(COR_PRIMARIA);
        sector1.setLabel("Melhor setor 1");
        plot.addDomainMarker(sector1);

        ValueMarker sector2 = new ValueMarker(piloto.getFastest_sectors().get(2).getLap_number());
        sector2.setPaint(COR_PRIMARIA);
        sector2.setLabel("Melhor setor 2");
        plot.addDomainMarker(sector2);

        ValueMarker sector3 = new ValueMarker(piloto.getFastest_sectors().get(3).getLap_number());
        sector3.setPaint(COR_PRIMARIA);
        sector3.setLabel("Melhor setor 3");
        plot.addDomainMarker(sector3);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMinimumSize(new Dimension(400, 200));
        chartPanel.setMaximumSize(new Dimension(1600, 800));
        chartPanel.setPreferredSize(new Dimension(800, 400));
        return chartPanel;
    }

    // RETORNA UM CHARTPANEL PREENCHIDO COM DADOS DE ACELERADOR E FREIOS
    private ChartPanel getLapBrakeThrottleChart(Piloto piloto, int lap_number, int setor) {

        XYSeries brakeSerie = new XYSeries("Freio");
        XYSeries throttleSerie = new XYSeries("Acelerador");
        for (CarData cd : piloto.getVoltas().get(lap_number).getLapCarData().get(setor)) {
            brakeSerie.add(cd.timePassed(piloto.getVoltas().get(lap_number).getDates().get("start_sector_1")), cd.getBrake());
            throttleSerie.add(cd.timePassed(piloto.getVoltas().get(lap_number).getDates().get("start_sector_1")), cd.getThrottle());
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(brakeSerie);
        dataset.addSeries(throttleSerie);
        JFreeChart chart = ChartFactory.createXYLineChart("Dados do setor "+setor +" da volta "+lap_number, "Segundo", "Valor", dataset);
        chart.setBackgroundPaint(Color.WHITE);


        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();
        renderer.setSeriesPaint(1, COR_PRIMARIA);
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));
        renderer.setSeriesStroke(1, new BasicStroke(3.0f));

        XYPlot plot = chart.getXYPlot();

        plot.setBackgroundPaint(new Color(250, 250, 250));
        plot.setDomainGridlinePaint(new Color(230, 230, 230));
        plot.setRangeGridlinePaint(new Color(230, 230, 230));
        plot.getRenderer().setSeriesPaint(0, COR_DESTAQUE);

        NumberAxis eixoy = (NumberAxis) plot.getRangeAxis();
        eixoy.setTickUnit(new NumberTickUnit(10));
        eixoy.setRange(-10, 110);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMinimumSize(new Dimension(400, 200));
        chartPanel.setMaximumSize(new Dimension(1600, 800));
        chartPanel.setPreferredSize(new Dimension(800, 400));
        return chartPanel;
    }

    // RETORNA UM CHARTPANEL PREENCHIDO COM VELOCIDADES DURANTE UM SETOR
    private ChartPanel getLapSpeedChart(Piloto piloto, int lap_number, int setor) {

        XYSeries speedSerie = new XYSeries("Speed");
        for (CarData cd : piloto.getVoltas().get(lap_number).getLapCarData().get(setor)) {
            speedSerie.add(cd.timePassed(piloto.getVoltas().get(lap_number).getDates().get("start_sector_1")), cd.getSpeed());
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(speedSerie);
        JFreeChart chart = ChartFactory.createXYLineChart("Velocidades do setor "+setor +" da volta "+lap_number, "Segundo", "Valor(km/h)", dataset);
        chart.setBackgroundPaint(Color.WHITE);


        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));

        XYPlot plot = chart.getXYPlot();

        plot.setBackgroundPaint(new Color(250, 250, 250));
        plot.setDomainGridlinePaint(new Color(230, 230, 230));
        plot.setRangeGridlinePaint(new Color(230, 230, 230));
        plot.getRenderer().setSeriesPaint(0, COR_DESTAQUE);

        NumberAxis eixoy = (NumberAxis) plot.getRangeAxis();
        eixoy.setTickUnit(new NumberTickUnit(10));
        CarData cd = piloto.calculateHighestSpeed(lap_number);
        eixoy.setRange(-10, cd.getSpeed() +10);

        ValueMarker higherSp = new ValueMarker(cd.timePassed(piloto.getVoltas().get(lap_number).getDates().get("start_sector_1")));
        higherSp.setPaint(COR_PRIMARIA);
        higherSp.setLabel("Maior Velocidade");
        plot.addDomainMarker(higherSp);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMinimumSize(new Dimension(400, 200));
        chartPanel.setMaximumSize(new Dimension(1600, 800));
        chartPanel.setPreferredSize(new Dimension(800, 400));
        return chartPanel;
    }

    // RETORNA UM CHARTPANEL VAZIO
    private ChartPanel getEmptyChart(String nome, String eixoX, String eixoY) {
        XYSeries serie = new XYSeries(nome);
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(serie);
        JFreeChart chart = ChartFactory.createXYLineChart(nome, eixoX, eixoY, dataset);
        chart.setBackgroundPaint(Color.WHITE);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMinimumSize(new Dimension(400, 200));
        chartPanel.setMaximumSize(new Dimension(1600, 800));
        chartPanel.setPreferredSize(new Dimension(800, 400));
        return chartPanel;
    }
}