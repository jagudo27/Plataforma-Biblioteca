package util;

import entidades.Usuario;

import java.util.List;

public class Validador {

    public static boolean isFilled(Object ... objects){
        for (Object o : objects) {
            if (o == null || o.toString().isEmpty() || o.toString().isBlank()) {
                return false;
            }

        }
        return true;
    }


    public static void validarEstadoEjemplar(String estado) throws IllegalArgumentException {
        if (!"Disponible".equalsIgnoreCase(estado) && !"Prestado".equalsIgnoreCase(estado) && !"Dañado".equalsIgnoreCase(estado)) {
            throw new IllegalArgumentException("El estado del ejemplar debe ser 'Disponible' , 'Prestado' o 'Dañado'.");
        }
    }
    public static void isISBN13Valido(String isbn){
        if (isbn == null || isbn.isEmpty() || isbn.isBlank()) {
            throw new NumberFormatException("ISBN vacío");
        }
        if (isbn.length() != 13) {
            throw new NumberFormatException("Longitud del ISBN no valida");
        }
        //Calculo digito de control
        int total = 0;
        for (int i = 0; i < isbn.length() - 1; i++) {
            int digito = Character.getNumericValue(isbn.charAt(i));
            if (i % 2 == 0) {
                total += digito;
            }
            else {
                total += digito *  3;
            }
        }
        int digitoControl = 10 - (total % 10);
        if (digitoControl == 10) {
            digitoControl = 0;
        }
        int digitoControlEntry = Character.getNumericValue(isbn.charAt(isbn.length() - 1));
        if (digitoControl != digitoControlEntry) {
            throw new NumberFormatException("ISBN no valido");
        }
    }
    public static void isEmailValid(String email) {
        // Validación básica del correo electrónico
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        if (!email.matches(regex))
           throw new IllegalArgumentException("Email invalido");

    }
    public static void validarDocumentoIdentidad(String di) {
        // Validar que el documento tiene 9 caracteres
        if (di == null || di.length() != 9) {
            throw new IllegalArgumentException("El documento de identidad debe tener exactamente 9 caracteres.");
        }

        // Lista de Letras
        List<Character> letras = List.of(
                'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D', 'X', 'B',
                'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E'
        );

        int diDigits;
        char letraInicio = di.charAt(0); // Verificar si es extranjero o español

        try {
            // NIE
            if (Character.isLetter(letraInicio)) {
                diDigits = Integer.parseInt(getNumeroEquivalenteNIF(letraInicio) + di.substring(1, 8));
            }
            // DNI
            else {
                diDigits = Integer.parseInt(di.substring(0, 8));
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El documento de identidad contiene caracteres inválidos.", e);
        }

        // Calcular el resto de la división del número por 23
        int resto = diDigits % 23;

        // Obtener la letra válida para ese número
        char letraEsperada = letras.get(resto);

        // Obtener la letra introducida
        char letraIntroducida = di.charAt(8);

        // Validar que coinciden
        if (letraIntroducida != letraEsperada) {
            throw new IllegalArgumentException("La letra del documento de identidad no coincide con la esperada.");
        }

    }

    // Método auxiliar para obtener el equivalente de las letras de NIF
    private static int getNumeroEquivalenteNIF(char letraInicio) {
        return switch (letraInicio) {
            case 'X' -> 0;
            case 'Y' -> 1;
            case 'Z' -> 2;
            default -> throw new IllegalArgumentException("Caracter no válido para NIF: " + letraInicio);
        };
    }

    // Validación Tipo de Usuario
    public static void isTipoValid(String tipo) {
        // Retorna true si el tipo es "administrador" o "normal",
        // false en caso contrario
        if(!tipo.equalsIgnoreCase("administrador") && !tipo.equalsIgnoreCase("normal")){
            throw new IllegalArgumentException("Tipo invalido");
        };
    }

    // Validación Contraseña
    public static void isPasswordCorrectForThisUser(Usuario usuario, String password) {
        // Retorna true si la contraseña es correcta para el usuario,
        // false en caso contrario
        if (!usuario.getPassword().equals(password)){
            throw new IllegalArgumentException("Contraseña incorrecta");
        }
    }


}
