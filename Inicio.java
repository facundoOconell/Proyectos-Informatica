import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Inicio extends JFrame implements KeyListener {

    private JLabel iniciarLabel;
    private JLabel historiaLabel;
    private JLabel rankingLabel;
    private JLabel salirLabel;
    private int seleccion;
    private Timer timerParpadeo;
    private boolean estadoParpadeo;
    private JPanel centerPanel;
    private Random random;
    private Font customFont; // Fuente personalizada

    public Inicio() {
        random = new Random();

        // Cargar la fuente personalizada
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("D:\\PacMan\\src\\PressStart2P.ttf")).deriveFont(15f); // Ajusta el tamaño según sea necesario
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        // Configurar la ventana
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        int anchoVentana = (int) (pantalla.width * 0.63);
        int altoVentana = (int) (pantalla.height * 0.93);
        setTitle("Super Pacman - Grupo 6");
        setSize(anchoVentana, altoVentana);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(350, 400));

        // Crear un panel principal con el fondo de estrellas
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarEstrellas(g); // Método para dibujar estrellas
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.BLACK); // Color de fondo negro

        // Panel central para el contenido
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false); // Mantener el panel central transparente

        // Cargar la imagen del logo
        ImageIcon logo = new ImageIcon("D:\\PacMan\\src\\Imagenes\\logo.png");
        JLabel logoLabel = new JLabel(logo);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Panel contenedor para las opciones
        JPanel opcionesPanel = new JPanel();
        opcionesPanel.setLayout(new GridBagLayout());
        opcionesPanel.setOpaque(false); // Mantener el panel de opciones transparente

        // Configurar GridBagConstraints para el centrado vertical
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Label para iniciar el juego
        iniciarLabel = new JLabel("Iniciar Juego", SwingConstants.CENTER);
        configureLabel(iniciarLabel);
        opcionesPanel.add(iniciarLabel, gbc);

        // Label para historia
        historiaLabel = new JLabel("Historia", SwingConstants.CENTER);
        configureLabel(historiaLabel);
        opcionesPanel.add(historiaLabel, gbc);

        // Label para ranking
        rankingLabel = new JLabel("Ranking", SwingConstants.CENTER);
        configureLabel(rankingLabel);
        opcionesPanel.add(rankingLabel, gbc);

        // Label para salir
        salirLabel = new JLabel("Salir", SwingConstants.CENTER);
        configureLabel(salirLabel);
        opcionesPanel.add(salirLabel, gbc);

        // Agregar componentes al panel central
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(logoLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 50)));
        centerPanel.add(Box.createRigidArea(new Dimension(0, 50)));
        centerPanel.add(opcionesPanel);
        centerPanel.add(Box.createVerticalGlue());

        // Agregar el panel central al panel principal
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Configurar la ventana
        setContentPane(mainPanel);

        // Añadir KeyListener
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();

        // Inicializar la selección y el timer
        seleccion = 0;
        actualizarSeleccion();

        timerParpadeo = new Timer(500, e -> {
            estadoParpadeo = !estadoParpadeo;
            actualizarSeleccion();
        });
        timerParpadeo.start();
    }

    private void configureLabel(JLabel label) {
        label.setFont(customFont); // Usar la fuente personalizada
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void actualizarSeleccion() {
        iniciarLabel.setForeground(seleccion == 0 && estadoParpadeo ? Color.yellow : Color.white);
        historiaLabel.setForeground(seleccion == 1 && estadoParpadeo ? Color.yellow : Color.white);
        rankingLabel.setForeground(seleccion == 2 && estadoParpadeo ? Color.yellow : Color.white);
        salirLabel.setForeground(seleccion == 3 && estadoParpadeo ? Color.yellow : Color.white);
    }

    private void dibujarEstrellas(Graphics g) {
        g.setColor(Color.WHITE); // Color de las estrellas
        for (int i = 0; i < 250; i++) { // Dibujar 100 estrellas
            int x = random.nextInt(getWidth());
            int y = random.nextInt(getHeight());
            g.fillOval(x, y, 2, 2); // Estrellas como pequeños óvalos
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            // Mover hacia abajo
            seleccion++;
            if (seleccion > 3) { // Si la selección supera el máximo (4 opciones: Iniciar, Historia, Ranking, Salir)
                seleccion = 0; // Volver a la primera opción
            }
            actualizarSeleccion();
        } else if (e.getKeyCode() == KeyEvent.VK_UP) {
            // Mover hacia arriba
            seleccion--;
            if (seleccion < 0) { // Si la selección es menor que 0, ir a la última opción
                seleccion = 3; // La última opción es "Salir"
            }
            actualizarSeleccion();
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (seleccion == 0) {
                Juego ventanaJuego = new Juego();
                ventanaJuego.setVisible(true);
                dispose(); // Cerrar la ventana del menú
            } else if (seleccion == 1) {
                Historia ventanaHistoria = new Historia();
                ventanaHistoria.setVisible(true);
                dispose(); // Cerrar la ventana del menú
            } else if (seleccion == 2) {
                Ranking ventanaRanking = new Ranking();  // Nueva instancia de Ranking
                ventanaRanking.setVisible(true);
                dispose(); // Cerrar la ventana del menú
            } else {
                System.exit(0); // Salir del juego
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Inicio ventana = new Inicio();
            ventana.setVisible(true);
        });
    }
}