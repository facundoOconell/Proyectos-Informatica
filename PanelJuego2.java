import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class PanelJuego2 extends JPanel {
    private Juego2 juego;
    private ImageIcon enemigoIcon; // Icono para los enemigos
    private ImageIcon powerupVelocidadIcon;

    public PanelJuego2(Juego2 juego2) {
        this.juego = juego2;
        setBackground(Color.BLACK);
        cargarImagenEnemigo(); // Cargar la imagen del enemigo
    }

    private void cargarImagenEnemigo() {
        try {
            enemigoIcon = new ImageIcon("D:\\PacMan\\src\\Imagenes\\amarillo-aba.gif"); // Cambia la ruta según sea necesario
            powerupVelocidadIcon = new ImageIcon("D:\\PacMan\\src\\Imagenes\\powerup-velocidad-azul.gif");

        } catch (Exception e) {
            e.printStackTrace(); // Maneja el error de carga
            enemigoIcon = null; // Maneja el error de carga
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        dibujarJuego(g);
    }

    private void dibujarJuego(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int offsetX = (getWidth() - (Juego.COLUMNAS * juego.getTamanoCelda())) / 2;
        int offsetY = (getHeight() - (Juego.FILAS * juego.getTamanoCelda())) / 2;

        // Dibujar el mapa, Pacman y power-ups
        for (int i = 0; i < Juego.FILAS; i++) {
            for (int j = 0; j < Juego.COLUMNAS; j++) {
                int x = offsetX + j * juego.getTamanoCelda();
                int y = offsetY + i * juego.getTamanoCelda();

                if (juego.getMapa()[i][j] == '#') {
                    g2d.drawImage(juego.muro2Image, x, y,
                            juego.getTamanoCelda(), juego.getTamanoCelda(), this);
                } else if (juego.getMapa()[i][j] == '.') {
                    g2d.setColor(Color.CYAN);
                    int puntoDiametro = Math.max(juego.getTamanoCelda() / 4, 3);
                    g2d.fillOval(x + (juego.getTamanoCelda() - puntoDiametro) / 2,
                            y + (juego.getTamanoCelda() - puntoDiametro) / 2,
                            puntoDiametro, puntoDiametro);
                } else if (juego.getMapa()[i][j] == 'M') {
                    // Dibujar el power-up de velocidad más grande
                    if (powerupVelocidadIcon != null) {
                        int powerupSize = (int)(juego.getTamanoCelda() * 0.8);
                        int padding = (juego.getTamanoCelda() - powerupSize) / 2;

                        g2d.drawImage(powerupVelocidadIcon.getImage(),
                                x + padding,
                                y + padding,
                                powerupSize,
                                powerupSize,
                                this);
                    }
                }
            }
        }

        // Dibuja Pacman2 usando su icono
        ImageIcon pacman2Icon = juego.pacman2.getIcono();
        g2d.drawImage(pacman2Icon.getImage(),
                offsetX + (int)(juego.pacman2.getX() * juego.getTamanoCelda()),
                offsetY + (int)(juego.pacman2.getY() * juego.getTamanoCelda()),
                juego.getTamanoCelda(), juego.getTamanoCelda(),
                this);

        // Dibuja enemigos usando su icono específico
        for (Enemigo2 enemigo : juego.enemigos) {
            g2d.drawImage(enemigo.getIcono().getImage(),
                    offsetX + (int)(enemigo.x * juego.getTamanoCelda()),
                    offsetY + (int)(enemigo.y * juego.getTamanoCelda()),
                    juego.getTamanoCelda(), juego.getTamanoCelda(),
                    this);
        }
    }
}

