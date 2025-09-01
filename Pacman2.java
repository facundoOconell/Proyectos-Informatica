import javax.swing.*;
import java.awt.event.KeyEvent;

class Pacman2 {
    private Juego2 juego;  // Referencia al objeto Juego2
    public double x, y;
    public int direccionX, direccionY;
    private boolean speedBoostActive = false;
    private Timer speedBoostTimer;
    private double baseSpeed = 0.15;
    private double currentSpeed = baseSpeed;
    private int lastKeyPressed = -1;
    private int keyPressCount1 = 0;
    private static final int POWERUP_DURATION = 10; // Duración del power-up en segundos
    private int remainingBoostTime = 0; // Tiempo restante del power-up en segundos
    
    // Cargar los GIFs
    private ImageIcon pacmanUp;
    private ImageIcon pacmanDown;
    private ImageIcon pacmanLeft;
    private ImageIcon pacmanRight;

    public Pacman2(double x, double y, Juego2 juego) {
        this.x = x;
        this.y = y;
        this.juego = juego; // Almacenar la referencia de Juego2
        cargarImagenes();
        initializeSpeedBoostTimer();
    }

    // Inicialización del temporizador para el boost de velocidad
    private void initializeSpeedBoostTimer() {
        speedBoostTimer = new Timer(1000, e -> { // Timer que cuenta en segundos
            if (speedBoostActive && remainingBoostTime > 0) {
                remainingBoostTime--; // Decrementar el tiempo restante
                System.out.println("Tiempo restante del power-up: " + remainingBoostTime);
                // Cuando el tiempo se agote, desactivar el boost
                if (remainingBoostTime == 0) {
                    deactivateSpeedBoost();
                }
            }
        });
        speedBoostTimer.setRepeats(true); // Repetir cada segundo
    }

    private void cargarImagenes() {
        pacmanUp = new ImageIcon("D:\\PacMan\\src\\Imagenes\\artronauta-arr.gif");
        pacmanDown = new ImageIcon("D:\\PacMan\\src\\Imagenes\\astronauta-aba.gif");
        pacmanLeft = new ImageIcon("D:\\PacMan\\src\\Imagenes\\astronauta-izq.gif");
        pacmanRight = new ImageIcon("D:\\PacMan\\src\\Imagenes\\astronauta-der.gif");
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
            keyPressCount1++;
        } else {
            keyPressCount1 = 1;
        }
        lastKeyPressed = keyCode;
    }

    public void activateSpeedBoost() {
        speedBoostActive = true;
        currentSpeed = baseSpeed * 1.5; // 50% más rápido
        remainingBoostTime = POWERUP_DURATION; // Establecer el tiempo inicial
        speedBoostTimer.restart(); // Reiniciar el temporizador
        System.out.println("Power-up activado. Tiempo restante: " + remainingBoostTime);
    }

    public void mover(char[][] mapa) {
        double newX = x + direccionX * currentSpeed;
        double newY = y + direccionY * currentSpeed;

        // Verificar límites del mapa y colisiones
        if (newX >= 0 && newX < Juego2.COLUMNAS && newY >= 0 && newY < Juego2.FILAS) {
            int roundedY = (int)Math.round(newY);
            int roundedX = (int)Math.round(newX);
            
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

                // Comprobar si está tocando a algún enemigo
                for (Enemigo2 enemigo : juego.enemigos) {
                    if (Math.abs(enemigo.x - x) < 0.5 && Math.abs(enemigo.y - y) < 0.5) {
                        if (speedBoostActive) {
                            // Si está en modo velocidad activa, eliminar al enemigo
                            juego.enemigos.remove(enemigo); 
                            break; // Salir del bucle para evitar ConcurrentModificationException
                        }
                    }
                }
            }
        }
    }
    
    public boolean isSpeedBoostActive() {
        return speedBoostActive;
    }
    
    // Obtener el tiempo restante del boost de velocidad
    public int getRemainingBoostTime() {
        return remainingBoostTime;
    }

    public void deactivateSpeedBoost() {
        speedBoostActive = false;
        currentSpeed = baseSpeed; // Restablecer la velocidad base
        remainingBoostTime = 0;
        if (speedBoostTimer.isRunning()) {
            speedBoostTimer.stop(); // Detener el temporizador de speed boost si está corriendo
        }
        System.out.println("Power-up desactivado");
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