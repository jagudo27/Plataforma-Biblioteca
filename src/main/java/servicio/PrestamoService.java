package servicio;

import dao.GenericDAO;
import entidades.Ejemplar;
import entidades.Prestamo;
import entidades.Usuario;
import util.Validador;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class PrestamoService {
    private final GenericDAO<Prestamo, Integer> prestamoDAO;
    private final UsuarioService usuarioService;
    private final EjemplarService ejemplarService;

    public PrestamoService(UsuarioService usuarioService, EjemplarService ejemplarService) {
        this.prestamoDAO = new GenericDAO<>(Prestamo.class);
        this.usuarioService = usuarioService;
        this.ejemplarService = ejemplarService;
    }

    public void registrarPrestamo(String dniUsuario, int ejemplarId) throws IllegalArgumentException, Exception {
        //Validamos para evitar campos vacíos
        if (!Validador.isFilled(dniUsuario, ejemplarId)) {
            throw new IllegalArgumentException("Todos los campos son obligatorios para registrar un préstamo.");
        }
        //Obtenemos un Usuario con el DNI dado para añadirlo al prestamo
        Usuario usuario = usuarioService.obtenerUsuarioPorDNI(dniUsuario);
        if (usuario == null) {
            throw new IllegalArgumentException("No se encontró el usuario con el DNI " + dniUsuario);
        }
        //Obtenemos un Ejemplar con para añadirlo al prestamo
        Ejemplar ejemplar = ejemplarService.obtenerEjemplarPorId(ejemplarId);
        if (ejemplar == null || !ejemplar.getEstado().equals("Disponible")) {
            throw new IllegalArgumentException("El ejemplar no está disponible o no existe.");
        }

        if (usuario.getPrestamos().size() >= 3) {
            throw new Exception("El usuario ya tiene 3 préstamos activos. No puede tener más");
        }

        if (usuario.getPenalizacionHasta() != null && usuario.getPenalizacionHasta().isAfter(LocalDate.now())) {
            throw new Exception("El usuario está penalizado hasta: " + usuario.getPenalizacionHasta().toString());
        }



        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuario);
        prestamo.setEjemplar(ejemplar);
        prestamo.setFechaInicio(LocalDate.now());
        prestamo.setFechaDevolucion(prestamo.getFechaInicio().plusDays(15));

        ejemplarService.actualizarEstadoEjemplar(ejemplarId, "Prestado");
        prestamoDAO.create(prestamo);
    }

    public void registrarDevolucion(int prestamoId) throws Exception {
        Prestamo prestamo = prestamoDAO.read(prestamoId);
        if (prestamo == null) {
            throw new IllegalArgumentException("No se encontró el préstamo con el ID proporcionado.");
        }

        Ejemplar ejemplar = prestamo.getEjemplar();
        Usuario usuario = prestamo.getUsuario();

        if (prestamo.getFechaDevolucion().isBefore(LocalDate.now())) {
            long diasRetraso = prestamo.getFechaDevolucion().until(LocalDate.now()).getDays();
            usuarioService.bloquearUsuario(usuario.getId(), (int) diasRetraso * 15);
        }

        ejemplarService.actualizarEstadoEjemplar(ejemplar.getId(), "Disponible");
        prestamoDAO.delete(prestamo);

    }

    public List<Prestamo> getPrestamos() {
        return prestamoDAO.findAll();
    }
}
