import javax.swing.*;
import java.util.Random;

class Enemigo2 {  // Renombrado a Enemigo2
    double x, y;
    private int direccionX, direccionY;
    private Random random;
    private ImageIcon icono;

    private static final String[] IMAGENES = {
        "D:\\PacMan\\src\\Imagenes\\ovni-rojo.gif",
        "D:\\PacMan\\src\\Imagenes\\ovni-verde.gif",
        "D:\\PacMan\\src\\Imagenes\\ovni-azul.gif",
        "D:\\PacMan\\src\\Imagenes\\ovni-amarillo.gif"
    };

    public Enemigo2(double x, double y) {  // Constructor renombrado a Enemigo2
        this.x = x;
        this.y = y;
        this.random = new Random();
        elegirDireccion();
        elegirIcono(); // Asigna un icono al enemigo
    }

    private void elegirDireccion() {
        int dir = random.nextInt(4);
        switch (dir) {
            case 0: direccionX = 0; direccionY = -1; break;
            case 1: direccionX = 0; direccionY = 1; break;
            case 2: direccionX = -1; direccionY = 0; break;
            case 3: direccionX = 1; direccionY = 0; break;
        }
    }

    private void elegirIcono() {
        String ruta = IMAGENES[random.nextInt(IMAGENES.length)];
        icono = new ImageIcon(ruta);
    }

    public void mover(char[][] mapa) {
        double velocidad = 0.08;
        double newX = x + direccionX * velocidad;
        double newY = y + direccionY * velocidad;

        // Asegúrate de que newX y newY estén dentro de los límites del mapa
        if (newX >= 0 && newX < Juego2.COLUMNAS && newY >= 0 && newY < Juego2.FILAS &&
            mapa[(int)Math.round(newY)][(int)Math.round(newX)] != '#') {
            x = newX;
            y = newY;
        } else {
            elegirDireccion(); // Cambiar dirección si hay colisión o fuera de límites
        }
    }

    public ImageIcon getIcono() {
        return icono;
    }
}
