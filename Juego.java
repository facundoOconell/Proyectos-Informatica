import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Juego extends JFrame {
    public static final int FILAS = 31;
    public static final int COLUMNAS = 28;
    public int tamanoCelda;
    public char[][] mapa;
    public Pacman pacman;
    public ArrayList<Enemigo> enemigos;
    public JPanel panelJuego;
    public JPanel panelInfo; // Panel negro para la información
    public Timer timer;
    public boolean juegoTerminado = false;
    private boolean invulnerable = false; // Estado de invulnerabilidad
    private JLabel labelTiempo; // Label para mostrar el tiempo
    private JLabel labelpower; 
    private int vidas = 3; // Número de vidas
    private Image corazonImage; // Imagen del corazón
    private int tiempoTranscurrido = 0; // Inicializa el tiempo transcurrido
    public Image muroImage;
    private Clip musicClip;
    private Clip speedBoostSound;
    private Clip deathSound;
    private boolean audioSystemWorking = false;
    private Clip pelletSound;

    
    public Juego() {
        mapa = new char[FILAS][COLUMNAS];
        establecerTamanoCelda();
        generarMapa();
        inicializarInterfaz();
        enemigos = new ArrayList<>();
        crearEnemigos(25);
        iniciarMovimiento();
        iniciarContadorTiempo(); // Iniciar el contador de tiempo
        cargarImagenes(); // Cargar la imagen del corazón
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
        tamanoCelda = Math.max(tamanoCelda, 15); // Mínimo tamaño
    }

    private void cargarImagenes() {
        corazonImage = new ImageIcon("D:\\PacMan\\src\\Imagenes\\corazon.png").getImage();
        muroImage = new ImageIcon("D:\\PacMan\\src\\Imagenes\\muro.png").getImage(); // Cargar la imagen del muro
    }

    private void generarMapa() {
        String[] mapaDiseno = {
            "############################",
            "#............##............#",
            "#.####.#####.##.#####.####.#",
            "#M####.#####.##.#####.####M#",
            "#.####.#####.##.#####.####.#",
            "#..........................#",
            "#.####.##.########.##.####.#",
            "#.####.##.########.##.####.#",
            "#......##....##....##......#",
            "######.##### ## #####.######",
            "######.##### ## #####.######",
            "######.##          ##.######",
            "######.## ###--### ##.######",
            "######.## #      # ##.######",
            "#     .   #  P   #   .     #",
            "######.## #      # ##.######",
            "######.## ######## ##.######",
            "######.##          ##.######",
            "######.## ######## ##.######",
            "######.## ######## ##.######",
            "#............##............#",
            "#.####.#####.##.#####.####.#",
            "#.####.#####.##.#####.####.#",
            "#..M##................##M..#",
            "###.##.##.########.##.##.###",
            "###.##.##.########.##.##.###",
            "#......##....##....##......#",
            "#.##########.##.##########.#",
            "#.##########.##.##########.#",
            "#..........................#",
            "############################"
        };

        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                mapa[i][j] = mapaDiseno[i].charAt(j);
                if (mapa[i][j] == 'P') {
                    pacman = new Pacman(j, i);
                    mapa[i][j] = '.';
                }
            }
        }
    }

    private void inicializarInterfaz() {
        setTitle("Pacman");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        panelJuego = new PanelJuego(this);
        JScrollPane scrollPane = new JScrollPane(panelJuego);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        
        panelInfo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Dibujar corazones
                for (int i = 0; i < vidas; i++) {
                    g.drawImage(corazonImage, 30 + i * 50, 10, 30, 30, this);
                }
            }
        };
        panelInfo.setBackground(Color.BLACK); // Fondo negro
        panelInfo.setPreferredSize(new Dimension(200, FILAS * tamanoCelda)); // Ajustar tamaño del panel negro
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS)); // Usar BoxLayout

        labelTiempo = new JLabel("Tiempo: 0:00");
        labelTiempo.setForeground(Color.WHITE);
        labelTiempo.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrar el texto
        
        panelInfo.add(Box.createVerticalStrut(50)); // Espaciador para separar corazones y tiempo
        panelInfo.add(labelTiempo); // Agregar el label del tiempo
        
        labelpower = new JLabel("Tiempo Power-up: 0");
        labelpower.setForeground(Color.GREEN);
        labelpower.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrar el texto
        
        panelInfo.add(Box.createVerticalStrut(50)); // Espaciador para separar corazones y tiempo
        panelInfo.add(labelpower); // Agregar el label del tiempo

        add(scrollPane, BorderLayout.CENTER);
        add(panelInfo, BorderLayout.EAST); // Agregar el panel negro a la derecha

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                pacman.cambiarDireccion(e.getKeyCode());
            }
        });

        // Establecer un tamaño fijo para la ventana
        int width = COLUMNAS * tamanoCelda + 250; // Aumentar ancho por el panel
        int height = FILAS * tamanoCelda + 50; // Alto con un margen
        setSize(width, height);
        
        setMinimumSize(new Dimension(width, height));
        
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void iniciarMovimiento() {
        timer = new Timer(16, e -> {
            if (!juegoTerminado) {
                pacman.mover(mapa);
                moverEnemigos();
                verificarColision();
                panelJuego.repaint();
                panelInfo.repaint(); // Repintar el panel de información
            }
        });
        timer.start();
    }

    private void iniciarContadorTiempo() {
        Timer tiempoTimer = new Timer(1000, e -> {
            tiempoTranscurrido++; // Aumenta el tiempo transcurrido en 1 segundo
            int minutos = tiempoTranscurrido / 60; // Calcula los minutos
            int segundos = tiempoTranscurrido % 60; // Calcula los segundos
            labelTiempo.setText(String.format("Tiempo: %d:%02d", minutos, segundos)); // Actualiza el label
            
            if (pacman.isSpeedBoostActive()) {
                int remainingTime = pacman.getRemainingBoostTime(); // Obtener el tiempo restante
                labelpower.setText("Tiempo Power-up:  " + remainingTime); // Mostrarlo en el label
            } else {
                labelpower.setText("Tiempo Power-up: 0"); // Si no hay power-up activo
            }
            
        });
        tiempoTimer.start(); // Inicia el timer
    }

    private void crearEnemigos(int cantidad) {
        Random random = new Random();
        for (int i = 0; i < cantidad; i++) {
            double enemigoX, enemigoY;
            do {
                enemigoX = random.nextInt(COLUMNAS);
                enemigoY = random.nextInt(FILAS);
            } while (mapa[(int)enemigoY][(int)enemigoX] == '#' || 
                     (Math.abs(enemigoX - pacman.getX()) < 5 && Math.abs(enemigoY - pacman.getY()) < 5));
            
            enemigos.add(new Enemigo(enemigoX, enemigoY));
        }
    }

    private void moverEnemigos() {
        for (Enemigo enemigo : enemigos) {
            enemigo.mover(mapa);
        }
    }

    private boolean todaLaComidaFueComida() {
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (mapa[i][j] == '.') {
                    return false; // Aún queda comida en el mapa
                }
            }
        }
        return true; // No queda más comida en el mapa
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
    	
    	 int pacmanTileX = (int) Math.floor(pacman.getX());
         int pacmanTileY = (int) Math.floor(pacman.getY());
        
         if (pacmanTileX >= 0 && pacmanTileX < COLUMNAS && pacmanTileY >= 0 && pacmanTileY < FILAS) {
             if (mapa[pacmanTileY][pacmanTileX] == 'M') {
                 System.out.println("Speed boost detectado en posición: " + pacmanTileX + ", " + pacmanTileY);
                 reproducirSonidoSpeedBoost();
                 mapa[pacmanTileY][pacmanTileX] = ' '; // Eliminar el power-up del mapa
                 pacman.activateSpeedBoost(); // Activar el speed boost
             }
            
             if (pacmanTileX >= 0 && pacmanTileX < COLUMNAS && pacmanTileY >= 0 && pacmanTileY < FILAS) {
                 if (mapa[pacmanTileY][pacmanTileX] == '.') {
                     mapa[pacmanTileY][pacmanTileX] = ' '; // Eliminar el punto del mapa
                     reproducirSonidoPunto(); // Reproducir el sonido de comer un punto
                 }

                 
        for (Enemigo enemigo : enemigos) {
            if (Math.abs(enemigo.x - pacman.getX()) < 0.5 && Math.abs(enemigo.y - pacman.getY()) < 0.5) {
                if (!invulnerable) { // Solo perder vida si no está invulnerable
                	reproducirSonidoMuerte();
                	vidas--; // Resta una vida
                    pacman.x = 13; // Restablecer posición de Pacman al centro del mapa
                    pacman.y = 15;
                    
                    if (vidas < 0) vidas = 0; // Evitar que las vidas sean negativas

                    panelInfo.repaint(); // Repintar el panel de información

                    // Si pierde una vida, desactivar el power-up de velocidad si está activo
                    if (pacman.isSpeedBoostActive()) {
                        pacman.deactivateSpeedBoost(); // Desactivar la velocidad extra
                    }

                    if (vidas <= 0) {
                        juegoTerminado = true;
                        timer.stop();
                        System.out.println("Juego terminado - Deteniendo sistema de audio");
                        detenerMusica();
                        JOptionPane.showMessageDialog(this, "¡Game Over!", "Fin del juego", JOptionPane.INFORMATION_MESSAGE);
                        System.exit(0);
                    } else {
                        activarEscudoTemporal(); // Activa el escudo temporal
                    }
                }
            }
        }

        // Nuevos códigos para verificar si se ha comido toda la comida
        if (todaLaComidaFueComida()) {
            juegoTerminado = true;
            timer.stop();
            System.out.println("Nivel completado - Deteniendo sistema de audio");
            detenerMusica();
            this.dispose(); // Cerrar la ventana del juego actual
            Juego2 ventanaJuego2 = new Juego2(tiempoTranscurrido); // Crear una nueva instancia de Juego2
            ventanaJuego2.setVisible(true); // Mostrar la ventana de Juego2
        }
             }
         }
    }


    private void activarEscudoTemporal() {
        invulnerable = true; // Activa invulnerabilidad
        Timer escudoTimer = new Timer(2000, e -> {
            invulnerable = false; // Desactiva invulnerabilidad después de 2 segundos
        });
        escudoTimer.setRepeats(false); // Solo ejecutar una vez
        escudoTimer.start();
    }

    public char[][] getMapa() {
        return mapa;
    }

    public int getTamanoCelda() {
        return tamanoCelda;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Juego::new);
    }
}