package entidades;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "prestamo")
public class Prestamo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "usuario_id", nullable = false)
    private entidades.Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "ejemplar_id", nullable = false)
    private Ejemplar ejemplar;

    @Column(name = "fechaInicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fechaDevolucion")
    private LocalDate fechaDevolucion;

    public Prestamo(Usuario usuario, Ejemplar ejemplar, LocalDate fechaInicio, LocalDate fechaDevolucion) {
        this.usuario = usuario;
        this.ejemplar = ejemplar;
        this.fechaInicio = fechaInicio;
        setFechaDevolucion(fechaDevolucion);
    }

    public Prestamo() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public entidades.Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(entidades.Usuario usuario) {
        this.usuario = usuario;
    }

    public Ejemplar getEjemplar() {
        return ejemplar;
    }

    public void setEjemplar(Ejemplar ejemplar) {
        this.ejemplar = ejemplar;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        if (fechaDevolucion == null){
            this.fechaDevolucion = fechaInicio.plusDays(15);
        }

    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Prestamo{");
        sb.append("usuario=").append(usuario.getNombre());
        sb.append(", ejemplar=").append(ejemplar.getLibroAsociado().getTitulo());
        sb.append(", fechaInicio=").append(fechaInicio.toString());
        sb.append(", fechaDevolucion=").append(fechaDevolucion.toString());
        sb.append('}');
        return sb.toString();
    }
}