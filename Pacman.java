import javax.swing.*;
import java.awt.event.KeyEvent;

public class Pacman {
    public double x, y;
    public int direccionX, direccionY;
    private int lastKeyPressed = -1;
    private int keyPressCount = 0;
    private boolean speedBoostActive = false;
    private Timer speedBoostTimer;
    private double baseSpeed = 0.15;
    private double currentSpeed = baseSpeed;

    private int remainingBoostTime = 0; // Tiempo restante del power-up en segundos

    // Cargar los GIFs
    private ImageIcon pacmanUp;
    private ImageIcon pacmanDown;
    private ImageIcon pacmanLeft;
    private ImageIcon pacmanRight;

    public Pacman(double x, double y) {
        this.x = x;
        this.y = y;
        cargarImagenes();
        initializeSpeedBoostTimer();
    }

    // Inicialización del temporizador para el boost de velocidad
    private void initializeSpeedBoostTimer() {
        speedBoostTimer = new Timer(1000, e -> { // Timer que cuenta en segundos
            if (speedBoostActive && remainingBoostTime > 0) {
                remainingBoostTime--; // Decrementar el tiempo restante
                // Cuando el tiempo se agote, desactivar el boost
                if (remainingBoostTime == 0) {
                    deactivateSpeedBoost();
                }
            }
        });
        speedBoostTimer.setRepeats(true); // Repetir cada segundo
    }

    // Cargar las imágenes de Pacman
    private void cargarImagenes() {
        pacmanUp = new ImageIcon("D:\\PacMan\\src\\Imagenes\\pacman-arr.gif");
        pacmanDown = new ImageIcon("D:\\PacMan\\src\\Imagenes\\pacman-aba.gif");
        pacmanLeft = new ImageIcon("D:\\PacMan\\src\\Imagenes\\pacman-izq.gif");
        pacmanRight = new ImageIcon("D:\\PacMan\\src\\Imagenes\\pacman-der.gif");
    }

    // Cambiar la dirección de Pacman cuando una tecla es presionada
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

    // Activar el boost de velocidad
    public void activateSpeedBoost() {
        speedBoostActive = true;
        currentSpeed = baseSpeed * 1.5; // Aumentar la velocidad en un 50%
        remainingBoostTime = 10; // Duración del boost de velocidad en segundos (ejemplo 10 segundos)
        speedBoostTimer.start(); // Iniciar el temporizador del boost de velocidad
    }

    // Mover a Pacman por el mapa
    public void mover(char[][] mapa) {
        double newX = x + direccionX * currentSpeed;
        double newY = y + direccionY * currentSpeed;

        // Verificar límites del mapa y colisiones
        if (newX >= 0 && newX < Juego.COLUMNAS && newY >= 0 && newY < Juego.FILAS) {
            int roundedY = (int) Math.round(newY);
            int roundedX = (int) Math.round(newX);
            
            if (mapa[roundedY][roundedX] != '#') {
                x = newX;
                y = newY;
                
                // Verificar si hay power-up de velocidad
                if (mapa[roundedY][roundedX] == 'M') {
                    mapa[roundedY][roundedX] = ' '; // Eliminar el power-up del mapa
                    activateSpeedBoost();
                }
                // Recolectar puntos normales
                else if (mapa[roundedY][roundedX] == '.') {
                    mapa[roundedY][roundedX] = ' ';
                }
            }
        }
    }

    // Comprobar si el boost de velocidad está activo
    public boolean isSpeedBoostActive() {
        return speedBoostActive;
    }

    // Obtener el tiempo restante del boost de velocidad
    public int getRemainingBoostTime() {
        return remainingBoostTime; // Retornar el tiempo restante del power-up en segundos
    }

    // Desactivar el boost de velocidad
    public void deactivateSpeedBoost() {
        speedBoostActive = false;
        currentSpeed = baseSpeed; // Restablecer la velocidad base
        if (speedBoostTimer.isRunning()) {
            speedBoostTimer.stop(); // Detener el temporizador de speed boost si está corriendo
        }
    }

    // Obtener el icono de Pacman según la dirección
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

    // Métodos para obtener las coordenadas de Pacman
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
