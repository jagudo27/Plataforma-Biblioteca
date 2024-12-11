package servicio;

import dao.GenericDAO;
import entidades.Ejemplar;
import entidades.Libro;
import util.Validador;

import java.util.List;

public class LibroService {
    //Indicamos tipo de clave String para poder usar el metodo del DAO genérico
    private GenericDAO<Libro, String> libroDAO = new GenericDAO<>(Libro.class);

    /**
     * Registra libro desde cuenta administrador
     * @param isbn
     * @param titulo
     * @param autor
     * @throws IllegalArgumentException
     */
    public void registrarLibro(String isbn, String titulo, String autor) throws IllegalArgumentException {
        //Validamos para evitar campos vacíos
        if (!Validador.isFilled(isbn, titulo, autor)) {
            throw new IllegalArgumentException("Todos los campos son obligatorios para registrar un libro.");
        }

        Libro libro = new Libro(isbn, titulo, autor);
        libroDAO.create(libro);
    }

    /**
     * Leer libro por ISBN
     * @param isbn

     */
    public Libro obtenerLibroPorISBN(String isbn) {
        return libroDAO.read(isbn);
    }


    /**
     * Metodo para obtener el stock de un libro mediante su ISBN (Solo libros en estado "Disponible"
     * @param isbn13
     * @return
     */
    public int getStockLibroByIsbn(String isbn13) {
        // Obtener libro por ISBN13
        Libro libroRef = obtenerLibroPorISBN(isbn13);

        // Contador
        int stock = 0;

        //Validar si los libros estan disponibles
        for (Ejemplar ejemplar : libroRef.getEjemplars()) {
            if (ejemplar.getEstado() != null && ejemplar.getEstado().equalsIgnoreCase("Disponible")) {
                stock++;
            }
        }

        return stock;
    }

    public List<Libro> getLibros() {
        return libroDAO.findAll();
    }
}

