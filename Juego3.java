import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;


public class Juego3 extends JFrame {
    public static final int FILAS = 31;
    public static final int COLUMNAS = 28;
    public int tamanoCelda;
    public char[][] mapa;
    public Pacman3 pacman3;
    public ArrayList<Enemigo3> enemigos;
    public JPanel panelJuego3;
    public JPanel panelInfo;
    public Timer timer;
    public boolean juegoTerminado = false;
    private boolean invulnerable = false;
    private JLabel labelTiempo;
    private JLabel labelpower;
    private int vidas = 3;
    private Image corazonImage;
    private int tiempoTotalJuego;
    private int tiemponashe;
    private int tiempoActual = 0;
    public Image muroImage;
    private String jugadorNombre = "";
    private Clip musicClip;
    private Clip speedBoostSound;
    private Clip deathSound;
    private boolean audioSystemWorking = false;
    private Clip pelletSound;


    public Juego3(int tiempoAcumulado) {
        this.tiempoTotalJuego = tiempoAcumulado;
        mapa = new char[FILAS][COLUMNAS];
        establecerTamanoCelda();
        generarMapa();
        inicializarInterfaz();
        enemigos = new ArrayList<>();
        crearEnemigos(0);
        pacman3 = new Pacman3(13, 15, this);
        iniciarMovimiento();
        iniciarContadorTiempo();
        cargarImagenes();
        actualizarLabelTiempo();
        inicializarMusica();
        cargarEfectosSonido();
    }

    private void cargarEfectosSonido() {
        try {
        	
        	  // Cargar sonido de comer un punto
            File pelletFile = new File("D:\\PacMan\\src\\Imagenes\\Sonidos-Pacman\\archivos wav\\wakka-comer_1.wav");
            if (!pelletFile.exists()) {
                System.out.println("No se encuentra el archivo de sonido de comer un punto");
                return;
            }
            AudioInputStream pelletStream = AudioSystem.getAudioInputStream(pelletFile);
            pelletSound = AudioSystem.getClip();
            pelletSound.open(pelletStream);
            System.out.println("Sonido de comer un punto cargado correctamente");
       
           
        	
            // Cargar sonido de speed boost
            File speedBoostFile = new File("D:\\PacMan\\src\\Imagenes\\Sonidos-Pacman\\archivos wav\\power.wav");
            if (!speedBoostFile.exists()) {
                System.out.println("No se encuentra el archivo de sonido speed boost");
                return;
            }
            AudioInputStream speedBoostStream = AudioSystem.getAudioInputStream(speedBoostFile);
            speedBoostSound = AudioSystem.getClip();
            speedBoostSound.open(speedBoostStream);
            System.out.println("Sonido speed boost cargado correctamente");
           
           
            // Cargar sonido de muerte
            File deathFile = new File("D:\\PacMan\\src\\Imagenes\\Sonidos-Pacman\\archivos wav\\muerte.wav");
            if (!deathFile.exists()) {
                System.out.println("No se encuentra el archivo de sonido death");
                return;
            }
            AudioInputStream deathStream = AudioSystem.getAudioInputStream(deathFile);
            deathSound = AudioSystem.getClip();
            deathSound.open(deathStream);
            System.out.println("Sonido death cargado correctamente");
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error al cargar los efectos de sonido: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void reproducirSonidoSpeedBoost() {
        if (speedBoostSound != null) {
            try {
                if (speedBoostSound.isRunning()) {
                    speedBoostSound.stop();
                }
                speedBoostSound.setFramePosition(0);
                speedBoostSound.start();
                System.out.println("Reproduciendo sonido speed boost");
            } catch (Exception e) {
                System.out.println("Error al reproducir sonido speed boost: " + e.getMessage());
            }
        } else {
            System.out.println("El clip de speed boost es null");
        }
    }
    private void reproducirSonidoMuerte() {
        if (deathSound != null) {
            deathSound.setFramePosition(0);
            deathSound.start();
        }
    }
   
   
    private void inicializarMusica() {
        System.out.println("Iniciando sistema de audio...");
        try {
            File musicFile = new File("D:\\PacMan\\src\\Imagenes\\Sonidos-Pacman\\archivos wav\\cancion-de-fondo-lvl_1.wav");
           
            // Verificar si el archivo existe
            if (!musicFile.exists()) {
                System.out.println("Error: No se encuentra el archivo de música");
                return;
            }
            System.out.println("Archivo de música encontrado correctamente");
            // Intentar obtener el stream de audio
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(musicFile);
            System.out.println("Stream de audio creado exitosamente");
            // Obtener información del formato de audio
            AudioFormat format = audioInputStream.getFormat();
            System.out.println("Formato de audio: " +
                              "\n - Canales: " + format.getChannels() +
                              "\n - Sample Rate: " + format.getSampleRate() +
                              "\n - Bits por Sample: " + format.getSampleSizeInBits());
            // Intentar obtener y abrir el clip
            musicClip = AudioSystem.getClip();
            musicClip.open(audioInputStream);
            System.out.println("Clip de audio abierto correctamente");
            // Configurar el volumen
            if (musicClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl =
                    (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(-10.0f);
                System.out.println("Control de volumen configurado");
            }
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicClip.start();
           
            audioSystemWorking = true;
            System.out.println("Sistema de audio iniciado exitosamente");
           
            // Agregar un LineListener para monitorear el estado del clip
            musicClip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.START) {
                    System.out.println("Reproducción de música iniciada");
                } else if (event.getType() == LineEvent.Type.STOP) {
                    System.out.println("Reproducción de música detenida");
                }
            });
        } catch (UnsupportedAudioFileException e) {
            System.out.println("Error: Formato de audio no soportado - " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error de IO al cargar el archivo - " + e.getMessage());
        } catch (LineUnavailableException e) {
            System.out.println("Error: Line no disponible - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado en el sistema de audio - " + e.getMessage());
        }
    }
    private void detenerMusica() {
        if (musicClip != null) {
            if (musicClip.isRunning()) {
                System.out.println("Deteniendo reproducción de música...");
                musicClip.stop();
                System.out.println("Música detenida");
            }
            musicClip.close();
        }
       
        // Limpiar los efectos de sonido
        if (speedBoostSound != null) {
            speedBoostSound.close();
        }
        if (deathSound != null) {
            deathSound.close();
        }
        System.out.println("Recursos de audio liberados");
    }
    
    private void establecerTamanoCelda() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenHeight = (int)(screenSize.height * 0.9);
        int screenWidth = (int)(screenSize.width * 0.9);
        
        int tamanoBasadoEnAltura = screenHeight / FILAS;
        int tamanoBasadoEnAncho = screenWidth / COLUMNAS;
        
        tamanoCelda = Math.min(tamanoBasadoEnAltura, tamanoBasadoEnAncho);
        tamanoCelda = Math.max(tamanoCelda, 15);
    }

    private void cargarImagenes() {
        corazonImage = new ImageIcon("D:\\PacMan\\src\\Imagenes\\corazon.png").getImage();
        muroImage = new ImageIcon("D:\\PacMan\\src\\Imagenes\\lava11.jpg").getImage();
    }

    public void restarTiempo() {
        tiempoTotalJuego = Math.max(0, tiempoTotalJuego - 5);
        actualizarLabelTiempo();
    }

    private void generarMapa() {
        String[] mapaDiseno = {
                "############################",
                "#..........#....#..........#",
                "#.########.#.##.#.########.#",
                "#M########.#.##.#.########M#",
                "#.#..........##..........#.#",
                "#...###.####.##.####.###...#",
                "###.###.####.##.####.###.###",
                "#...###.####.##.####.###...#",
                "#.#.###..............###.#.#",
                "#.#.#####.########.#####.#.#",
                "#.#.#####.########.#####.#.#",
                "#.#......          ......#.#",
                "#.#####.# ###--### #.#####.#",
                "#.......# #      # #.......#",
                "#.#.##### #      # #####.#.#",
                "#.#.##### #   P  # #####.#.#",
                "#.#.##### ######## #####.#.#",
                "#.#......          ......#.#",
                "#.#####.####.##.####.#####.#",
                "#.#####.####.##.####.#####.#",
                "#.#####.##...##...##.#####.#",
                "#.......##.######.##.......#",
                "#####.#.##.######.##.#.#####",
                "#..M..#..............#.....#",
                "#.#####.####.##.####.#####.#",
                "#.......####.##.####.......#",
                "#.#####.##...##...##.#####.#",
                "#.#####.##.######.##.#####.#",
                "#.#####.##.######.##.#####.#",
                "#.......##........##...M...#",
                "############################"
        };

        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                mapa[i][j] = mapaDiseno[i].charAt(j);
                if (mapa[i][j] == 'P') {
                    pacman3 = new Pacman3(j, i, this);
                    mapa[i][j] = '.';
                }
            }
        }
    }

    private void inicializarInterfaz() {
        setTitle("Pacman3");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        panelJuego3 = new PanelJuego3(this);
        JScrollPane scrollPane = new JScrollPane(panelJuego3);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        
        panelInfo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (int i = 0; i < vidas; i++) {
                    g.drawImage(corazonImage, 30 + i * 50, 10, 30, 30, this);
                }
            }
        };
        panelInfo.setBackground(Color.BLACK);
        panelInfo.setPreferredSize(new Dimension(200, FILAS * tamanoCelda));
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));

        labelTiempo = new JLabel("Tiempo: 0:00");
        labelTiempo.setForeground(Color.WHITE);
        labelTiempo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelInfo.add(Box.createVerticalStrut(50));
        panelInfo.add(labelTiempo);
        
        labelpower = new JLabel("PowerUP resta 5s");
        labelpower.setForeground(Color.RED);
        labelpower.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrar el texto
        
        panelInfo.add(Box.createVerticalStrut(50)); // Espaciador para separar corazones y tiempo
        panelInfo.add(labelpower); // Agregar el label del tiempo

        add(scrollPane, BorderLayout.CENTER);
        add(panelInfo, BorderLayout.EAST);

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                pacman3.cambiarDireccion(e.getKeyCode());
            }
        });

        int width = COLUMNAS * tamanoCelda + 250;
        int height = FILAS * tamanoCelda + 50;
        setSize(width, height);
        
        setMinimumSize(new Dimension(width, height));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void iniciarMovimiento() {
        timer = new Timer(16, e -> {
            if (!juegoTerminado) {
                pacman3.mover(mapa);
                moverEnemigos();
                verificarColision();
                panelJuego3.repaint();
                panelInfo.repaint();
            }
        });
        timer.start();
    }

    private void iniciarContadorTiempo() {
        Timer tiempoTimer = new Timer(1000, e -> {
            tiempoActual++;
            tiempoTotalJuego++;
            actualizarLabelTiempo();
        });
        tiempoTimer.start();
    }
    
    private void actualizarLabelTiempo() {
        int minutos = tiempoTotalJuego / 60;
        int segundos = tiempoTotalJuego % 60;
        labelTiempo.setText(String.format("Tiempo Total: %d:%02d", minutos, segundos));
    }

    private void crearEnemigos(int cantidad) {
        Random random = new Random();
        for (int i = 0; i < cantidad; i++) {
            double enemigoX, enemigoY;
            do {
                enemigoX = random.nextInt(COLUMNAS);
                enemigoY = random.nextInt(FILAS);
            } while (mapa[(int)enemigoY][(int)enemigoX] == '#' || 
                     (Math.abs(enemigoX - pacman3.getX()) < 5 && Math.abs(enemigoY - pacman3.getY()) < 5));
            
            enemigos.add(new Enemigo3(enemigoX, enemigoY));
        }
    }

    private void moverEnemigos() {
        for (Enemigo3 enemigo : enemigos) {
            enemigo.mover(mapa);
        }
    }

    private boolean todaLaComidaFueComida() {
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (mapa[i][j] == '.') {
                    return false;
                }
            }
        }
        return true;
    }
    
    private void reproducirSonidoPunto() {
        if (pelletSound != null) {
            try {
                if (pelletSound.isRunning()) {
                    pelletSound.stop();
                }
                pelletSound.setFramePosition(0);
                pelletSound.start();
                System.out.println("Reproduciendo sonido de comer un punto");
            } catch (Exception e) {
                System.out.println("Error al reproducir sonido de comer un punto: " + e.getMessage());
            }
        } else {
            System.out.println("El clip de sonido de comer un punto es null");
        }
    }

    private void verificarColision() {
    	
   	 int pacmanTileX = (int) Math.floor(pacman3.getX());
     int pacmanTileY = (int) Math.floor(pacman3.getY());
    	
     if (pacmanTileX >= 0 && pacmanTileX < COLUMNAS && pacmanTileY >= 0 && pacmanTileY < FILAS) {
         if (mapa[pacmanTileY][pacmanTileX] == 'M') {
             System.out.println("Speed boost detectado en posición: " + pacmanTileX + ", " + pacmanTileY);
             reproducirSonidoSpeedBoost();
             mapa[pacmanTileY][pacmanTileX] = ' '; // Eliminar el power-up del mapa
             restarTiempo();// Activar el speed boost
         }
        
         if (pacmanTileX >= 0 && pacmanTileX < COLUMNAS && pacmanTileY >= 0 && pacmanTileY < FILAS) {
             if (mapa[pacmanTileY][pacmanTileX] == '.') {
                 mapa[pacmanTileY][pacmanTileX] = ' '; // Eliminar el punto del mapa
                 reproducirSonidoPunto(); // Reproducir el sonido de comer un punto
             }
    	
        for (Enemigo3 enemigo : enemigos) {
            if (Math.abs(enemigo.x - pacman3.getX()) < 0.5 && Math.abs(enemigo.y - pacman3.getY()) < 0.5) {
                if (!invulnerable) {
                    vidas--;
                	reproducirSonidoMuerte();
                    pacman3.x = 13;
                    pacman3.y = 15;
                    if (vidas < 0) vidas = 0;
                    panelInfo.repaint();

                    if (vidas <= 0) {
                        juegoTerminado = true;
                        timer.stop();
                        System.out.println("Juego terminado - Deteniendo sistema de audio");
                        detenerMusica();
                        System.exit(0);
                    } else {
                        activarEscudoTemporal();
                    }
                }
            }
        }

        if (todaLaComidaFueComida()) {
            juegoTerminado = true;
            timer.stop();
            System.out.println("Nivel completado - Deteniendo sistema de audio");
            detenerMusica();
            tiemponashe=tiempoTotalJuego;
            guardarResultado();
            System.exit(0);
        }
         }
         
     }
    }

    private void guardarResultado() {
        jugadorNombre = JOptionPane.showInputDialog(this, 
            "Has ganado!!Felicidades Introduce tu nombre:", 
            "Nombre del Jugador", 
            JOptionPane.PLAIN_MESSAGE);

        if (jugadorNombre == null || jugadorNombre.trim().isEmpty()) {
            jugadorNombre = "Jugador Anónimo";
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("E:\\PacMan\\src\\Ranking.txt", true))) {
            writer.write(jugadorNombre + " - " + formatTiempo(tiemponashe) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String formatTiempo(int segundos) {
        int minutos = segundos / 60;
        int segundosRestantes = segundos % 60;
        return String.format("%d:%02d", minutos, segundosRestantes);
    }

    private void activarEscudoTemporal() {
        invulnerable = true;
        Timer escudoTimer = new Timer(2000, e -> {
            invulnerable = false;
        });
        escudoTimer.setRepeats(false);
        escudoTimer.start();
    }

    public char[][] getMapa() {
        return mapa;
    }

    public int getTamanoCelda() {
        return tamanoCelda;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Juego3(0));
    }
}