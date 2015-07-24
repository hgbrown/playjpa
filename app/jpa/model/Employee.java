package jpa.model;

import com.google.common.base.Objects;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import persistence.HashMapJsonUserType;

import javax.persistence.*;
import java.util.Map;

@Entity
@Table(name = "employee")
@NamedQueries({
        @NamedQuery(name = "Employee.getAll", query = "select e from Employee e"),
        @NamedQuery(name = "Employee.findByName", query = "select e from Employee e where e.firstName like :name or e.lastName like :name")
})
@TypeDefs({
        @TypeDef( name= "HashMapJsonObject", typeClass = HashMapJsonUserType.class)
})
public class Employee implements Domain<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_id_sequence")
    @SequenceGenerator(name = "employee_id_sequence", sequenceName = "employee_id_sequence", allocationSize = 1)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Type(type = "HashMapJsonObject")
    private Map<String, Object> profile;

    @Override
    public Long getId() {
        return id;
    }

    @Override
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

    public Map<String, Object> getProfile() {
        return profile;
    }

    public void setProfile(Map<String, Object> profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("firstName", firstName)
                .add("lastName", lastName)
                .add("profile", profile)
                .toString();
    }

}
