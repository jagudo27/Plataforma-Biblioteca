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

    public PrestamoService(GenericDAO<Prestamo, Integer> prestamoDAO, UsuarioService usuarioService, EjemplarService ejemplarService) {
        this.prestamoDAO = prestamoDAO;
        this.usuarioService = usuarioService;
        this.ejemplarService = ejemplarService;
    }

    public void registrarPrestamo(String dniUsuario, int ejemplarId, String fechaInicio) throws IllegalArgumentException, Exception {
        if (!Validador.isFilled(dniUsuario, fechaInicio)) {
            throw new IllegalArgumentException("Todos los campos son obligatorios para registrar un préstamo.");
        }

        Usuario usuario = usuarioService.obtenerUsuarioPorDNI(dniUsuario);
        if (usuario == null) {
            throw new IllegalArgumentException("No se encontró el usuario con el DNI " + dniUsuario);
        }

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
        LocalDate fechaInicioPrestamo = LocalDate.parse(fechaInicio);


        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuario);
        prestamo.setEjemplar(ejemplar);
        prestamo.setFechaInicio(LocalDate.parse(fechaInicio));
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
            long diasRetraso = LocalDate.now().until(prestamo.getFechaDevolucion()).getDays();
            usuarioService.bloquearUsuario(usuario.getDni(), (int) diasRetraso * 15);
        }

        ejemplarService.actualizarEstadoEjemplar(ejemplar.getId(), "Disponible");
        prestamoDAO.delete(prestamo);
    }

    public List<Prestamo> listarPrestamos() {
        return prestamoDAO.findAll();
    }
}
