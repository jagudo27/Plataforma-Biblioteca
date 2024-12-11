package entidades;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "ejemplar")
public class Ejemplar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "isbn", nullable = false)
    private entidades.Libro libroAsociado;

    @ColumnDefault("'Disponible'")
    @Lob
    @Column(name = "estado")
    private String estado;

    @OneToMany(mappedBy = "ejemplar")
    private Set<entidades.Prestamo> prestamos = new LinkedHashSet<>();

    public Ejemplar(Libro libro, String estado) {
        setLibroAsociado(libro);
        setEstado(estado);
    }

    public Ejemplar() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public entidades.Libro getLibroAsociado() {
        return libroAsociado;
    }

    public void setLibroAsociado(entidades.Libro libro) {

        this.libroAsociado = libro;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Set<entidades.Prestamo> getPrestamos() {
        return prestamos;
    }

    public void setPrestamos(Set<entidades.Prestamo> prestamos) {
        this.prestamos = prestamos;
    }

}