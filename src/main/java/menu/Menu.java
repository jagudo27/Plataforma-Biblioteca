package menu;

import entidades.Libro;
import entidades.Prestamo;
import entidades.Usuario;
import servicio.EjemplarService;
import servicio.LibroService;
import servicio.PrestamoService;
import servicio.UsuarioService;

import java.util.List;
import java.util.Scanner;

public class Menu {
    private UsuarioService usuarioService;
    private LibroService libroService;
    private PrestamoService prestamoService;
    private EjemplarService ejemplarService;
    private Scanner sc;
    public Menu(){
         usuarioService = new UsuarioService();
         libroService = new LibroService();

         ejemplarService = new EjemplarService(libroService);
       prestamoService = new PrestamoService(usuarioService,ejemplarService);
       sc = new Scanner(System.in);
       mostrarMenuPrincipal();

    }

    public void mostrarMenuPrincipal() {
        int option;
        do {
            System.out.println("--------------------------------");
            System.out.println("1 - Iniciar sesión");
            System.out.println("2 - Registrarse");
            System.out.println("0 - Salir");
            System.out.println("--------------------------------");

            option = sc.nextInt();
            sc.nextLine();

            switch (option) {
                case 1:
                    mostrarLogin();
                    break;
                case 2:
                    mostrarRegistroUsuario();
                    break;
                case 0:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("ERROR. Intenta de nuevo.");
            }
        } while (option != 0);
    }
    //Login que valida la sesion y detecta el tipo de usuario
    private void mostrarLogin() {
        String email, password;
        System.out.println("Iniciar sesión");
        System.out.println("--------------------------------");

        System.out.println("Email: ");
        email = sc.nextLine();

        System.out.println("Contraseña: ");
        password = sc.nextLine();

        try {
            Usuario usuarioActivo = usuarioService.iniciarSesion(email, password);
            if (usuarioActivo.getTipo().equals("administrador")) {
                mostrarAdminMenu();
            } else {
                mostrarMenuUsuario(usuarioActivo);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    //Menu registro de usuario que al registrar inicia sesion con el usuario registrado
    private void mostrarRegistroUsuario() {
        System.out.println();
        System.out.println("Registro nuevo usuario");
        System.out.println("Por favor, complete los siguientes datos:");
        System.out.println("-----------------------");

        System.out.print("Ingrese su DNI: ");
        String dni = sc.nextLine();

        System.out.print("Ingrese su nombre y apellidos: ");
        String nombre = sc.nextLine();

        System.out.print("Ingrese un email: ");
        String email = sc.nextLine();

        System.out.print("Contraseña: ");
        String password = sc.nextLine();

        try {
            // Registrar usuario y activar sesión
            Usuario nuevoUsuario = usuarioService.registrarUsuario(dni, nombre, email, password);
            System.out.println("¡Registro exitoso! Bienvenido, " + nuevoUsuario.getNombre());

            if (nuevoUsuario.getTipo().equals("administrador")) {
                mostrarAdminMenu();
            } else {
                mostrarMenuUsuario(nuevoUsuario);
            }
        } catch (Exception ex) {
            System.out.println("Error durante el registro: " + ex.getMessage());
        }
    }
    // Menu con los prestamos asociados al usuario
    private void mostrarMenuUsuario(Usuario usuarioActivo) {
        int option;
        mostrarPrestamosUsuario(usuarioActivo);
        do {
            System.out.println("0 - Salir");

            option = sc.nextInt();
            sc.nextLine(); // Limpiar buffer

        } while (option != 0);
    }

    private void mostrarPrestamosUsuario(Usuario usuarioActivo) {
        System.out.println("**************");
        System.out.println("Menú Préstamos");
        System.out.println("**************");

        // Se calcula el número de préstamos activos usando el tamaño de la lista
        List<Prestamo> prestamos = usuarioActivo.getPrestamos().stream().toList();
        try {
            if (prestamos.isEmpty()) {
                throw new Exception("No tienes préstamos activos.");
            }
            int numeroPrestamosActivos = prestamos.size();

            System.out.printf("%s, tienes %d préstamos activos.\n",
                    usuarioActivo.getNombre(), numeroPrestamosActivos);
            System.out.println("**************");

            // Mostrar préstamos activos
            for (int i = 0; i < prestamos.size(); i++) {
                System.out.printf("%d. %s\n", i + 1, prestamos.get(i));
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }


    private  void mostrarAdminMenu() {
        int option;
        do {
            System.out.println("--------------------------------");
            System.out.println("1 - Registrar libro");
            System.out.println("2 - Registrar ejemplar");
            System.out.println("3 - Crear prestamo");
            System.out.println("4 - Devolver prestamo");
            System.out.println("5 - Listar libros y su stock");
            System.out.println("6 - Listar préstamos");
            System.out.println("0 - Salir");
            System.out.println("--------------------------------");

            option = sc.nextInt();
            sc.nextLine();

            switch (option) {
                case 1:
                    registrarLibro();
                    break;
                case 2:
                    registrarEjemplar();
                    break;
                case 3:
                    crearPrestamo();
                    break;
                case 4:
                    devolverPrestamo();
                    break;
                case 5:
                    listarLibrosYStock();
                    break;
                case 6:
                    listarPrestamos();
                    break;
                case 0:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("ERROR. Intenta de nuevo.");
            }
        } while (option != 0);
    }
    public void listarPrestamos() {
        List<Prestamo> prestamos = prestamoService.getPrestamos();
        System.out.println("***************");
        System.out.println("Lista de Préstamos activos");
        System.out.println("***************");

        if (prestamos.isEmpty()) {
            System.out.println("No hay préstamos registrados.");
        } else {
            prestamos.forEach(System.out::println);
        }
    }

    public void listarLibrosYStock() {
        List<Libro> libros = libroService.getLibros();
        System.out.println("~~~~~~~~~~~~~~~");
        System.out.println("Libros y su Stock");
        System.out.println("~~~~~~~~~~~~~~~");

        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
        } else {
            for (Libro libro : libros) {
                int stock = libroService.getStockLibroByIsbn(libro.getIsbn());
                System.out.printf("Título: %s | Stock: %d \n", libro.getTitulo(), stock);
            }
        }
    }

    public void devolverPrestamo() {
        System.out.println("-------------");
        System.out.println("Devolución de Préstamo");
        System.out.println("-------------");

        System.out.print("Introduce el ID del préstamo: ");
        int idPrestamo = sc.nextInt();
        sc.nextLine(); // Limpiar el buffer

        try {
            prestamoService.registrarDevolucion(idPrestamo);
            System.out.println("El préstamo ha sido devuelto correctamente.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void crearPrestamo() {
        System.out.println("***************");
        System.out.println("Nuevo Préstamo");
        System.out.println("***************");

        System.out.print("Introduce el DNI del usuario: ");
        String dniUsuario = sc.nextLine();


        System.out.print("Introduce el ID del ejemplar: ");
        int idEjemplar = sc.nextInt();
        sc.nextLine();

        try {
            prestamoService.registrarPrestamo(dniUsuario, idEjemplar);
            System.out.println("Préstamo creado satisfactoriamente.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void registrarEjemplar() {
        System.out.println("============= ");
        System.out.println("Agregar Ejemplar");
        System.out.println("============= ");

        System.out.print("Introduce el ISBN-13 del libro: ");
        String isbn13 = sc.nextLine();

        System.out.print("Introduce el estado del ejemplar (Disponible, Dañado, ENTER para Disponible): ");
        String estado = sc.nextLine();

        try {
            ejemplarService.registrarEjemplar(isbn13, estado);
            System.out.println("Ejemplar registrado exitosamente.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void registrarLibro() {
        System.out.println("=================");
        System.out.println("Registro de Libro");
        System.out.println("=================");

        System.out.print("Introduce el ISBN-13: ");
        String isbn13 = sc.nextLine();

        System.out.print("Introduce el título del libro: ");
        String titulo = sc.nextLine();

        System.out.print("Introduce el autor del libro: ");
        String autor = sc.nextLine();

        try {
            libroService.registrarLibro(isbn13, titulo, autor);
            System.out.println("Libro registrado correctamente.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
