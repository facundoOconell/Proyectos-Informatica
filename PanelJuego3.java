import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

class PanelJuego3 extends JPanel {
    private Juego3 juego;
    private ImageIcon enemigoIcon;
    private ImageIcon powerupVelocidadIcon;

    public PanelJuego3(Juego3 juego3) {
        this.juego = juego3;
        setBackground(Color.BLACK);
        cargarImagenes();
    }

    private void cargarImagenes() {
        try {
            enemigoIcon = new ImageIcon("D:\\PacMan\\src\\Imagenes\\amarillo-aba.gif");
            powerupVelocidadIcon = new ImageIcon("D:\\PacMan\\src\\Imagenes\\powerup-velocidad-rojo.gif");
        } catch (Exception e) {
            e.printStackTrace();
            enemigoIcon = null;
            powerupVelocidadIcon = null;
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
                    g2d.drawImage(juego.muroImage, x, y, 
                                juego.getTamanoCelda(), juego.getTamanoCelda(), this);
                } 
                else if (juego.getMapa()[i][j] == '.') {
                    g2d.setColor(Color.RED);
                    int puntoDiametro = Math.max(juego.getTamanoCelda() / 4, 3);
                    g2d.fillOval(x + (juego.getTamanoCelda() - puntoDiametro) / 2,
                                y + (juego.getTamanoCelda() - puntoDiametro) / 2,
                                puntoDiametro, puntoDiametro);
                }
                else if (juego.getMapa()[i][j] == 'M') {
                    // Dibujar el power-up de velocidad más grande
                    if (powerupVelocidadIcon != null) {
                        // Usar el 80% del tamaño de la celda
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

        // Dibuja Pacman
        ImageIcon pacman3Icon = juego.pacman3.getIcono();
        g2d.drawImage(pacman3Icon.getImage(),
                     offsetX + (int)(juego.pacman3.getX() * juego.getTamanoCelda()),
                     offsetY + (int)(juego.pacman3.getY() * juego.getTamanoCelda()),
                     juego.getTamanoCelda(), juego.getTamanoCelda(),
                     this);

        // Dibuja enemigos
        for (Enemigo3 enemigo : juego.enemigos) {
            g2d.drawImage(enemigo.getIcono().getImage(),
                         offsetX + (int)(enemigo.x * juego.getTamanoCelda()),
                         offsetY + (int)(enemigo.y * juego.getTamanoCelda()),
                         juego.getTamanoCelda(), juego.getTamanoCelda(),
                         this);
        }
    }
}