package repository;


import jpa.model.Domain;
import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * A base class to perform basic CRUD operations for all managed entities.
 *
 * @param <ID>              the type of the unique identifier for the entity to be managed by this repository.
 * @param <Entity>          the type of the managed entity to be managed by this repository.
 */
public class JpaRepositoryBase<ID extends Serializable, Entity extends Domain<ID>> {

    protected final Class<ID> idClass;
    protected final Class<Entity> entityClass;

    static final transient Logger.ALogger LOG = Logger.of(JpaRepositoryBase.class);

    /**
     * Construct a new instance of the JPA repository for the specified types.
     *
     * @param idClass           the type of the unique identifier.
     * @param entityClass       the type of the domain entity class.
     * @throws NullPointerException
     *                          if either of the parameters are null.
     */
    protected JpaRepositoryBase(Class<ID> idClass, Class<Entity> entityClass) {
        this.idClass = Objects.requireNonNull(idClass, "idClass cannot be null");
        this.entityClass =  Objects.requireNonNull(entityClass, "entityClass cannot be null");
    }

    /**
     * Get all persisted entities using the a named query which by convension must be the unqualified class
     * name of the entity followed by {@literal .getAll}. For example, for an entity named {@literal Employee}
     * there should be a named query with a name: {@literal Employee.getAll}.
     *
     * @return              all persisted instances.
     * @throws PersistenceException
     *                      if the instances cannot be retrieved for any reason.
     */
    @Transactional(readOnly = true)
    public List<Entity> getAll() {
        LOG.info("Getting all instances of entity: {}", entityClass.getName());

        try {
            final String getAllQueryName = entityClass.getSimpleName() + ".getAll";
            LOG.debug("getAllQueryName=[{}]", getAllQueryName);

            final TypedQuery<Entity> namedQuery = JPA.em().createNamedQuery(getAllQueryName, entityClass);
            LOG.trace("namedQuery=[{}]", namedQuery);

            try {
                final List<Entity> list = namedQuery.getResultList();
                LOG.trace("list=[{}]", list);

                LOG.info("returning all {} entities of type {}", list != null ? list.size() : "null", entityClass.getName());
                return list;
            } catch (IllegalArgumentException e) {
                throw new PersistenceException("Unable to get all instances of entity", e);
            }

        } catch (IllegalArgumentException e) {
            throw new PersistenceException("Unable construct query to get all instances of entity", e);
        }
    }

    /**
     * Fetch the entity with the specified unique identifier.
     *
     * @param id                        the unique identifier of the entity to be fetched.
     * @return                          the entity with the specified unique identifier.
     * @throws PersistenceException
     *                                  if the first argument does not denote an entity type or the second argument is
     *                                  is not a valid type for that entitys primary key or is null
     */
    @Transactional(readOnly = true)
    public Entity get(ID id) {
        LOG.info("Getting instance with id: {} of entity: {}", id, entityClass.getName());

        try {
            final Entity entity = JPA.em().find(entityClass, id);
            LOG.debug("entity=[{}]", entity);

            LOG.info("successfully completed getting entity by id. entity=[{}]", entity);
            return entity;
        } catch(IllegalArgumentException e) {
            throw new PersistenceException("Unable to fetch entity with id: " + id, e);
        }

    }

    /**
     * Make an instance managed and persistent.
     *
     * @param entity            the transient entity to be made persistent.
     * @return                  the generated unique identifier associated with the persisted entity.
     * @throws PersistenceException
     *                          if the instance is not an entity or if the instance already has a persistence
     *                          identifier.
     */
    @Transactional
    public ID create(Entity entity) {
        LOG.info("Make persistent entity=[{}]", entity);

        if (entity.getId() != null) {
            throw new PersistenceException("Cannot create an entity with an existing id");
        }
        LOG.trace("Entity does not already have a persistant identifier");

        try {
            JPA.em().persist(entity);

            LOG.info("Completed persisting entity=[{}]", entity);
            return entity.getId();
        } catch(IllegalArgumentException e) {
            final String errorMessageTemplate = "Unable to make transient instance=[%s] persistent";
            final String errorMessage = String.format(errorMessageTemplate, entity);

            throw new PersistenceException(errorMessage, e);
        }
    }

    /**
     * Merge the state of the given entity into the current persistence context.
     *
     * @param entity            the entity to be merged.
     * @param id                the unique identifier of the entity to be merged.
     * @return                  the managed instance that the state was merged to.
     * @throws PersistenceException
     *                          if the id parameter value does not match the id in the entity to be
     *                          merged or if instance is not an entity or is a removed entity.
     */
    @Transactional
    public Entity update(Entity entity, ID id) {
        LOG.info("Merge entity=[{}] with id=[{}]", entity, id);

        if (!id.equals(entity.getId())) {
            throw new PersistenceException("id of instance to be updated does not match id in entity");
        }
        LOG.trace("ID parameter matches entity id");

        try {
            final Entity merged = JPA.em().merge(entity);
            LOG.debug("merged=[{}]", merged);

            LOG.info("Completed merge of entity=[{}]", merged);
            return merged;
        } catch (IllegalArgumentException e) {
            final String errorMessageTemplate = "Unable to make merge instance=[%s] with id=[%s]";
            final String errorMessage = String.format(errorMessageTemplate, entity, id);
            throw new PersistenceException(errorMessage, e);
        }
    }

    /**
     * Remove the entity instance with the specified unique identifier. Note that failure to remove an
     * instance will not always result in an exception. For example, if no entity with the specified
     * identifier is found, then result is simply {@code false}.
     *
     * @param id                    the unique identifier of the instance to be removed.
     * @return                      {@code true} if an instance was removed, {@code false} otherwise.
     * @throws PersistenceException
     *                               if the instance is not an entity or is a detached entity
     */
    @Transactional
    public boolean remove(ID id) {
        LOG.info("Remove entity with id=[{}]", id);

        final Entity entity = JPA.em().getReference(entityClass, id);
        LOG.debug("entity=[{}]", entity);

        if (entity != null) {
            try {
                JPA.em().remove(entity);
                LOG.info("Completed removal if entity=[{}]", entity);

            } catch (IllegalArgumentException e) {
                throw new PersistenceException("Unable to remove instance with id: " + id, e);
            }
            return true;
        }

        LOG.info("No entity to be removed");
        return false;
    }

}
