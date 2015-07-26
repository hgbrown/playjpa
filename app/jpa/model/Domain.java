package jpa.model;

import java.io.Serializable;

/**
 * Represents the common functionality across the entire entity domain model.
 *
 * @param <ID>          the type of the unique persistent identifier for the domain entity instance.
 */
public interface Domain<ID extends Serializable> {

    /**
     * Get the unique persistence identifier for this persistent instance.
     *
     * @return              {@code null} if the instance has not been persisted, otherwise
     *                      the unique identifier that identifies this instance in the database.
     */
    ID getId();

    /**
     * Set the unique persistent identifier for this instance which is used to uniquely identify this
     * instance in the database.
     *
     * @param id            the unique identifier for this instance.
     */
    void setId(ID id);

}
