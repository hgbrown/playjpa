package jpa.model;


import com.google.common.base.Objects;

import javax.persistence.*;

@Entity
@Table(name = "company")
@NamedQueries({
        @NamedQuery(name = "Company.getAll", query = "select c from Company c"),
        @NamedQuery(name = "Company.findByName", query = "select c from Company c where c.name like :name")
})
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "company_id_sequence")
    @SequenceGenerator(name = "company_id_sequence", sequenceName = "company_id_sequence", allocationSize = 1)
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "reg_number", unique = true, nullable = false)
    private String registrationNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("registrationNumber", registrationNumber)
                .toString();
    }

}
