import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.ArrayList;

public class Ranking extends JFrame {

    public Ranking() {
        setTitle("Ranking de Jugadores");
        setSize(845, 720);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal con fondo negro
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.BLACK);

        // Etiqueta de título
        JLabel titleLabel = new JLabel("HIGH SCORES", SwingConstants.CENTER);
        titleLabel.setFont(cargarFuente("E:\\PacMan\\src\\PressStart2P.ttf", 40));
        titleLabel.setForeground(Color.CYAN);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(40)); // Más espacio arriba del título
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(30)); // Más espacio debajo del título

        // Encabezados de columnas
        JPanel headerPanel = new JPanel(new GridLayout(1, 3));
        headerPanel.setBackground(Color.BLACK);
        headerPanel.setMaximumSize(new Dimension(800, 30));

        JLabel rankHeader = new JLabel("Rank", SwingConstants.CENTER);
        rankHeader.setFont(cargarFuente("E:\\PacMan\\src\\PressStart2P.ttf", 20));
        rankHeader.setForeground(Color.WHITE);
        headerPanel.add(rankHeader);

        JLabel playerHeader = new JLabel("Player", SwingConstants.CENTER);
        playerHeader.setFont(cargarFuente("E:\\PacMan\\src\\PressStart2P.ttf", 20));
        playerHeader.setForeground(Color.WHITE);
        headerPanel.add(playerHeader);

        JLabel timeHeader = new JLabel("Time", SwingConstants.CENTER);
        timeHeader.setFont(cargarFuente("E:\\PacMan\\src\\PressStart2P.ttf", 20));
        timeHeader.setForeground(Color.WHITE);
        headerPanel.add(timeHeader);

        panel.add(headerPanel);
        panel.add(Box.createVerticalStrut(10));

        // Cargar y mostrar los rankings
        List<String> rankings = cargarRankings();
        Color[] colors = {Color.YELLOW, Color.RED, Color.CYAN, Color.MAGENTA, Color.GREEN, Color.ORANGE, Color.PINK, Color.WHITE, Color.LIGHT_GRAY, Color.GRAY};

        if (rankings.isEmpty()) {
            JLabel noRankingLabel = new JLabel("No se encontraron rankings.", SwingConstants.CENTER);
            noRankingLabel.setForeground(Color.WHITE);
            noRankingLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            panel.add(noRankingLabel);
        } else {
            for (int i = 0; i < rankings.size() && i < 10; i++) {
                String[] parts = rankings.get(i).split(" - ");
                JPanel rowPanel = new JPanel(new GridLayout(1, 3));
                rowPanel.setBackground(Color.BLACK);
                rowPanel.setMaximumSize(new Dimension(800, 30));

                JLabel rankLabel = new JLabel((i + 1) + "", SwingConstants.CENTER);
                rankLabel.setFont(cargarFuente("E:\\PacMan\\src\\PressStart2P.ttf", 18));
                rankLabel.setForeground(colors[i % colors.length]);
                rowPanel.add(rankLabel);

                JLabel playerLabel = new JLabel(parts[0], SwingConstants.CENTER);
                playerLabel.setFont(cargarFuente("E:\\PacMan\\src\\PressStart2P.ttf", 18));
                playerLabel.setForeground(colors[i % colors.length]);
                rowPanel.add(playerLabel);

                JLabel timeLabel = new JLabel(parts[1], SwingConstants.CENTER);
                timeLabel.setFont(cargarFuente("E:\\PacMan\\src\\PressStart2P.ttf", 18));
                timeLabel.setForeground(colors[i % colors.length]);
                rowPanel.add(timeLabel);

                panel.add(rowPanel);
                panel.add(Box.createVerticalStrut(10)); // Espacio entre filas
            }
        }

        // Botón para volver al menú
        JButton backButton = new JButton("Volver al Menú");
        backButton.setBackground(Color.GRAY);
        backButton.setForeground(Color.WHITE);
        backButton.setFont(cargarFuente("E:\\PacMan\\src\\PressStart2P.ttf", 18));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> {
            Inicio ventanaInicio = new Inicio();
            ventanaInicio.setVisible(true);
            dispose();
        });
        panel.add(Box.createVerticalStrut(40)); // Más espacio debajo de los rankings y antes del botón
        panel.add(backButton);
        panel.add(Box.createVerticalStrut(30)); // Espacio adicional debajo del botón

        // Configurar el contenido
        setContentPane(panel);
    }

    private Font cargarFuente(String rutaFuente, float tamano) {
        try {
            Font fuente = Font.createFont(Font.TRUETYPE_FONT, new File(rutaFuente));
            return fuente.deriveFont(tamano);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            return new Font("Arial", Font.BOLD, (int) tamano);
        }
    }

    private List<String> cargarRankings() {
        List<String> rankings = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("E:\\PacMan\\src\\Ranking.txt"))) {
            String line;
            List<Player> players = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" - ");
                if (parts.length == 2) {
                    String nombreJugador = parts[0].trim();
                    String tiempoStr = parts[1].trim();
                    int tiempoTotal = parseTiempo(tiempoStr);
                    players.add(new Player(nombreJugador, tiempoTotal));
                }
            }

            players.sort(Comparator.comparingInt(Player::getTiempoTotal));
            for (Player player : players) {
                rankings.add(player.getNombreJugador() + " - " + formatTiempo(player.getTiempoTotal()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rankings;
    }

    private int parseTiempo(String tiempoStr) {
        String[] partes = tiempoStr.split(":");
        int minutos = Integer.parseInt(partes[0].trim());
        int segundos = Integer.parseInt(partes[1].trim());
        return minutos * 60 + segundos;
    }

    private String formatTiempo(int segundos) {
        int minutos = segundos / 60;
        int segsRestantes = segundos % 60;
        return String.format("%d:%02d", minutos, segsRestantes);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ranking ventanaRanking = new Ranking();
            ventanaRanking.setVisible(true);
        });
    }

    static class Player {
        private String nombreJugador;
        private int tiempoTotal;

        public Player(String nombreJugador, int tiempoTotal) {
            this.nombreJugador = nombreJugador;
            this.tiempoTotal = tiempoTotal;
        }

        public String getNombreJugador() {
            return nombreJugador;
        }

        public int getTiempoTotal() {
            return tiempoTotal;
        }
    }
}
