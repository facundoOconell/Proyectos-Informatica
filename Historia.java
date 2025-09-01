import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Historia extends JFrame implements KeyListener {
    private JPanel textoPanel;
    private String textoCompleto;
    private Timer timer;
    private int posY;
    private int panelHeight;
    private Font customFont; // Fuente personalizada
    private Random random; // Para generar posiciones aleatorias de estrellas

    // Cargar imágenes de los GIFs
    private ImageIcon pacmanGif;
    private ImageIcon[] fantasmasGifs;

    public Historia() {
        // Obtener el tamaño de la pantalla
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        int anchoVentana = (int) (pantalla.width * 0.63);
        int altoVentana = (int) (pantalla.height * 0.93);

        // Configurar la ventana
        setTitle("Historia de Pacman");
        setSize(anchoVentana, altoVentana);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Cargar la fuente personalizada
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("E:\\PacMan\\src\\PressStart2P.ttf")).deriveFont(8f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont); // Registrar la fuente
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        // Inicializar el generador de números aleatorios
        random = new Random();

        // Cargar los GIFs
        pacmanGif = new ImageIcon("E:\\PacMan\\src\\Imagenes\\pacman-izq.gif");
        fantasmasGifs = new ImageIcon[] {
            new ImageIcon("E:\\PacMan\\src\\Imagenes\\rojo-izq.gif"),
            new ImageIcon("E:\\PacMan\\src\\Imagenes\\amarillo-izq.gif"),
            new ImageIcon("E:\\PacMan\\src\\Imagenes\\rosa-izq.gif"),
            new ImageIcon("E:\\PacMan\\src\\Imagenes\\verde-izq.gif")
        };

        // Crear un panel para el texto
        textoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Dibujar el fondo con estrellas
                dibujarEstrellas(g);

                g.setColor(Color.WHITE);
                g.setFont(customFont); // Establecer la fuente personalizada

                FontMetrics fm = g.getFontMetrics(); // Obtener métricas de la fuente
                int lineHeight = fm.getHeight(); // Altura de cada línea
                int espacioEntreParrafos = lineHeight; // Espacio adicional entre párrafos

                // Dividir el texto en líneas, respetando saltos de línea
                String[] partesTexto = textoCompleto.split("\r\n");
                int x = 10; // Posición inicial X
                int y = posY; // Posición inicial Y

                for (String parte : partesTexto) {
                    String[] palabras = parte.split(" ");
                    StringBuilder lineaActual = new StringBuilder();

                    for (String palabra : palabras) {
                        // Verificar si la palabra cabe en la línea actual
                        if (fm.stringWidth(lineaActual + palabra) < (getWidth() - 20)) {
                            // Agregar la palabra a la línea actual
                            if (lineaActual.length() > 0) {
                                lineaActual.append(" ");
                            }
                            lineaActual.append(palabra);
                        } else {
                            // Dibuja la línea actual y comienza una nueva línea
                            g.drawString(lineaActual.toString(), x, y);
                            lineaActual = new StringBuilder(palabra);
                            y += lineHeight; // Mover hacia abajo para la siguiente línea
                        }
                    }

                    // Dibuja la última línea y añade espacio para el siguiente párrafo
                    g.drawString(lineaActual.toString(), x, y);
                    y += lineHeight; // Espacio entre líneas
                    y += espacioEntreParrafos; // Espacio adicional entre párrafos
                }

                // Dibuja los GIFs al final del texto, centrados
                y += lineHeight * 2; // Espacio adicional antes de los GIFs
                int gifWidth = 50; // Ancho de cada GIF
                int totalGifWidth = gifWidth + 10; // Ancho total con espacio entre GIFs
                int totalWidth = totalGifWidth * (fantasmasGifs.length + 1) + 10; // Total ancho de todos los GIFs

                int startX = (getWidth() - totalWidth) / 2; // Calcular posición inicial para centrar

                // Dibuja el GIF de Pac-Man
                g.drawImage(pacmanGif.getImage(), startX, y, gifWidth, gifWidth, this);
                startX += totalGifWidth; // Mover posición para los fantasmas

                // Dibuja los GIFs de fantasmas
                for (ImageIcon fantasmaGif : fantasmasGifs) {
                    g.drawImage(fantasmaGif.getImage(), startX, y, gifWidth, gifWidth, this); // Tamaño de cada fantasma
                    startX += totalGifWidth; // Mover posición para el siguiente fantasma
                }
            }

            private void dibujarEstrellas(Graphics g) {
                g.setColor(Color.WHITE); // Color de las estrellas
                for (int i = 0; i < 100; i++) { // Dibujar 100 estrellas
                    int x = random.nextInt(getWidth());
                    int y = random.nextInt(getHeight());
                    g.fillOval(x, y, 2, 2); // Estrellas como pequeños óvalos
                }
            }
        };
        textoPanel.setBackground(Color.BLACK);
        textoPanel.setPreferredSize(new Dimension(anchoVentana, altoVentana));

        // Definir el texto completo
        textoCompleto = "Pac-Man: Dimensiones del Laberinto\r\n"
                + "\r\n"
                + "Después de mucho tiempo… Pac-Man ha vivido felizmente en su laberinto, "
                + "devorando puntos amarillos y esquivando fantasmas en una rutina eterna. Sin embargo, "
                + "una extraña energía comenzó a surgir del corazón de su mundo, creando un portal "
                + "resplandeciente que lo atraía irresistiblemente.\r\n"
                + "\r\n"
                + "Cuando Pac-Man cruzó el umbral, se encontró en un nuevo universo lleno de sorpresas "
                + "y peligros. Ahora, su aventura se extendería a tres dimensiones inesperadas, "
                + "cada una con sus propios desafíos y enemigos. Prepárate para guiar a Pac-Man a "
                + "través de un viaje épico, donde el ingenio y la velocidad serán tus mejores aliados.\r\n"
                + "\r\n"
                + "Nivel 1: El Laberinto Clásico\r\n"
                + "Tu viaje comienza en el laberinto que conoces y amas. Recolecta todos los puntos, "
                + "evita a los fantasmas y utiliza las pastillas de poder para convertirte en un cazador. "
                + "Pero cuidado: ¡los fantasmas han aprendido nuevos trucos y se volverán más astutos "
                + "a medida que avancen los niveles!\r\n"
                + "\r\n"
                + "Nivel 2: En el Espacio\r\n"
                + "Al completar el primer laberinto, un nuevo portal te llevará al espacio. Aquí, "
                + "las estrellas brillan y los enemigos son alienígenas que se transforman y se mueven "
                + "a velocidades increíbles. En este entorno de gravedad cero, recolecta puntos flotantes "
                + "y utiliza las pastillas de poder para desactivar temporalmente a tus perseguidores. "
                + "¡Mantente ágil y observa los agujeros negros que podrían tragarte!\r\n"
                + "\r\n"
                + "Nivel 3: El Inframundo\r\n"
                + "Finalmente, el portal te llevará al inframundo, un laberinto sombrío y aterrador. "
                + "Los fantasmas han mutado en demonios que no se detendrán ante nada para atraparte. "
                + "Reúne cristales para activar habilidades especiales como invisibilidad y velocidad extrema. "
                + "Aquí, deberás usar todo tu ingenio para sobrevivir y devorar cada punto que encuentres.\r\n"
                + "\r\n"
                + "¡El destino de Pac-Man está en tus manos! Acepta el desafío, enfrenta a tus enemigos "
                + "y atraviesa las dimensiones del laberinto. ¿Estás listo para comenzar esta nueva aventura? "
                + "¡El juego está por comenzar!";

        // Inicializar posición Y
        panelHeight = altoVentana;
        posY = panelHeight; // Comenzar desde la parte inferior del panel

        // Configurar el timer para el desplazamiento
        timer = new Timer(75, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                posY -= 2; // Desplaza el texto hacia arriba

                // Si el texto ha salido completamente de la vista, reiniciar
                if (posY < -panelHeight) {
                    posY = panelHeight; // Reiniciar la posición al fondo
                }
                textoPanel.repaint(); // Actualiza el panel
            }
        });
        timer.start(); // Iniciar el timer

        // Agregar el panel al frame
        add(textoPanel);

        // Añadir KeyListener para detectar teclas
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();
    }

    // Método del KeyListener
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            // Volver al menú principal cuando se presiona BACKSPACE
            Inicio ventanaInicio = new Inicio();
            ventanaInicio.setVisible(true);
            dispose(); // Cerrar la ventana de Historia
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Historia ventanaHistoria = new Historia();
            ventanaHistoria.setVisible(true);
        });
    }
}
