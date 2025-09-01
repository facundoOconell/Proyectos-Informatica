import javax.swing.*;
import java.awt.event.KeyEvent;

class Pacman3 {
    public double x, y;
    public int direccionX, direccionY;
    private int lastKeyPressed = -1;
    private int keyPressCount = 0;
    private double baseSpeed = 0.15;
    private double currentSpeed = baseSpeed;
    private Juego3 juego;


    // Cargar los GIFs
    private ImageIcon pacmanUp;
    private ImageIcon pacmanDown;
    private ImageIcon pacmanLeft;
    private ImageIcon pacmanRight;

    public Pacman3(double x, double y, Juego3 juego) {
        this.x = x;
        this.y = y;
        this.juego = juego;
        cargarImagenes();
    }
    
 

    private void cargarImagenes() {
        pacmanUp = new ImageIcon("D:\\PacMan\\src\\Imagenes\\pacman-angel-arr.gif");
        pacmanDown = new ImageIcon("D:\\PacMan\\src\\Imagenes\\pacman-angel-aba.gif");
        pacmanLeft = new ImageIcon("D:\\PacMan\\src\\Imagenes\\pacman-angel-izq.gif");
        pacmanRight = new ImageIcon("D:\\PacMan\\src\\Imagenes\\pacman-angel-der.gif");
    }

    public void cambiarDireccion(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_UP:
                direccionX = 0;
                direccionY = -1;
                break;
            case KeyEvent.VK_DOWN:
                direccionX = 0;
                direccionY = 1;
                break;
            case KeyEvent.VK_LEFT:
                direccionX = -1;
                direccionY = 0;
                break;
            case KeyEvent.VK_RIGHT:
                direccionX = 1;
                direccionY = 0;
                break;
        }

        if (keyCode == lastKeyPressed) {
            keyPressCount++;
        } else {
            keyPressCount = 1;
        }
        lastKeyPressed = keyCode;
    }

    public void mover(char[][] mapa) {
        double newX = x + direccionX * currentSpeed;
        double newY = y + direccionY * currentSpeed;

        // Verificar límites del mapa y colisiones
        if (newX >= 0 && newX < Juego3.COLUMNAS && newY >= 0 && newY < Juego3.FILAS) {
            int roundedY = (int)Math.round(newY);
            int roundedX = (int)Math.round(newX);
            
            if (mapa[roundedY][roundedX] != '#') {
                x = newX;
                y = newY;
                
                // Verificar si hay power-up de tiempo
                if (mapa[roundedY][roundedX] == 'M') {
                    mapa[roundedY][roundedX] = ' '; // Eliminar el power-up del mapa
                    juego.restarTiempo(); // Restar 10 segundos al tiempo total
                }
                // Recolectar puntos normales
                else if (mapa[roundedY][roundedX] == '.') {
                    mapa[roundedY][roundedX] = ' ';
                }
            }
        }
    }

    public ImageIcon getIcono() {
        if (direccionY < 0) {
            return pacmanUp;
        } else if (direccionY > 0) {
            return pacmanDown;
        } else if (direccionX < 0) {
            return pacmanLeft;
        } else if (direccionX > 0) {
            return pacmanRight;
        }
        return pacmanRight; // Por defecto
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}

	