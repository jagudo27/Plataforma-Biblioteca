package servicio;

import dao.GenericDAO;
import entidades.Ejemplar;
import entidades.Libro;
import util.Validador;

import java.util.List;
import java.util.stream.Collectors;

public class EjemplarService {
    private GenericDAO<Ejemplar, Integer> ejemplarDAO;
    private LibroService libroService;

    public EjemplarService(GenericDAO<Ejemplar, String> ejemplarDAO, LibroService libroService) {
        this.ejemplarDAO = new GenericDAO<>(Ejemplar.class);
        this.libroService = libroService;
    }

    public void registrarEjemplar(String isbn, String estado) throws IllegalArgumentException {
        if (!Validador.isFilled(isbn, estado)) {
            throw new IllegalArgumentException("ISBN y estado son obligatorios para registrar un ejemplar.");
        }
        Validador.isISBN13Valido(isbn);
        Validador.validarEstadoEjemplar(estado);

        Libro libroRef = libroService.obtenerLibroPorISBN(isbn);
        if (libroRef == null)
            throw new IllegalArgumentException("No se ha encontrado el libro para el que quiere registrar un ejemplar. Debe registrar el libro primero.");

        Ejemplar ejemplar = new Ejemplar(libroRef,estado);


        ejemplarDAO.create(ejemplar);
    }
    public Ejemplar obtenerEjemplarPorId(int ejemplarId) {
        return ejemplarDAO.read(ejemplarId);
    }

    public int getStockTotalEjemplares() {
        int stock = 0; // Contador para los ejemplares disponibles
        List<Ejemplar> ejemplares = ejemplarDAO.findAll();
        // Iterar sobre la lista de ejemplares y contar los disponibles
        for (Ejemplar ejemplar : ejemplares) {
            if (ejemplar.getEstado() != null && ejemplar.getEstado().equalsIgnoreCase("Disponible")) {
                stock++;
            }
        }

        return stock;
    }

    public void actualizarEstadoEjemplar(int ejemplarId, String nuevoEstado) throws IllegalArgumentException {
        Ejemplar ejemplar = ejemplarDAO.read(ejemplarId);
        if (ejemplar == null) {
            throw new IllegalArgumentException("No se encontró el ejemplar con el ID proporcionado.");
        }

        ejemplar.setEstado(nuevoEstado);
        ejemplarDAO.update(ejemplar);
    }
}
