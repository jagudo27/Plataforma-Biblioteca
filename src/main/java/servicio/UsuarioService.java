package servicio;

import dao.GenericDAO;
import entidades.Usuario;
import util.Validador;

import java.time.LocalDate;
import java.util.List;

public class UsuarioService {
    private GenericDAO<Usuario, Integer> usuarioDAO = new GenericDAO<>(Usuario.class);

    public Usuario registrarUsuario(String dni, String nombre, String email, String password) throws IllegalArgumentException {
        if (!Validador.isFilled(dni, nombre, email, password)) {
            throw new IllegalArgumentException("Todos los campos son obligatorios para registrar un usuario.");
        }
        String tipo = "normal";
        Validador.isEmailValid(email);
        Validador.validarDocumentoIdentidad(dni);
        Validador.isTipoValid(tipo);

        Usuario usuario = new Usuario();
        usuario.setDni(dni);
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword(password);
        usuario.setTipo(tipo);
        usuario.setPenalizacionHasta(null);

        usuarioDAO.create(usuario);
        return usuario;
    }
    public Usuario iniciarSesion(String email, String password) {

        // Validación de campos requeridos
        if (!Validador.isFilled(email, password))
            throw new IllegalArgumentException("Todos los campos son obligatorios");

        // Validación de credenciales
        Usuario usuarioLogin = obtenerUsuarioPorEmail(email);
        if (usuarioLogin == null)
            throw new IllegalArgumentException("El email es incorrecto");
        Validador.isPasswordCorrectForThisUser(usuarioLogin, password);


        // Devolver usuario con sesión activa
        return usuarioLogin;
    }

    public Usuario obtenerUsuarioPorDNI(String dni) {
        return usuarioDAO.findAll().stream()
                .filter(usuario -> usuario.getDni().equals(dni))
                .findFirst()
                .orElse(null);
    }

    public Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioDAO.findAll().stream()
                .filter(usuario -> usuario.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }



    public void bloquearUsuario(Integer id, int diasPenalizacion) {
        Usuario usuario = usuarioDAO.read(id);
        if (usuario == null) {
            throw new IllegalArgumentException("No se encontró el usuario con el DNI proporcionado.");
        }

        usuario.setPenalizacionHasta(LocalDate.now().plusDays(diasPenalizacion));
        usuarioDAO.update(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioDAO.findAll();
    }
}

