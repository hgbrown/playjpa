package jpa.model;

import com.google.common.base.Objects;

import javax.persistence.*;

@Entity
@Table(name = "employee")
@NamedQueries({
        @NamedQuery(name = "Employee.getAll", query = "select e from Employee e"),
        @NamedQuery(name = "Employee.findByName", query = "select e from Employee e where e.firstName like :name or e.lastName like :name")
})
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_id_sequence")
    @SequenceGenerator(name = "employee_id_sequence", sequenceName = "employee_id_sequence", allocationSize = 1)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("firstName", firstName)
                .add("lastName", lastName)
                .toString();
    }
}
