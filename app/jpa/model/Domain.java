package jpa.model;

import java.io.Serializable;


public interface Domain<ID extends Serializable> {

    ID getId();

    void setId(ID id);

}
