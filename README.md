# POC for Postgres JSON type in Play

This is a POC to see how we can integrate the Postgres JSON type into our existing Play app to be able to utilise the
Postgres JSON column type to represent dynamic data. Specifically, we may want to store the user profile attributes
in a JSON column so that we can add attributes to the profile dynamically without having to change the user structure
every time.

## About the POC

In this POC, I use 2 main entities: `Company` and `Employee` where both have a JSON field.
In the case of a `Company` the JSON field maps to a simple string. In the case of the `Employee`
the JSON field maps to a `Map`.

In order to handle persistence, the JPA framework is used with Hibernate as the persistence provider.
In order to perform the marshalling between the JSON column in the database and the field in the Entity a
Hibernate user type is created that performs the conversion to and from the database.

The POC does not have a front-end, requiring all interactions to be via the rest endpoints exposed directly.
Both entities allow for the standard CRUD behaviour to be performed as well as some additional queries.
Included among the queries are examples, demonstrating how the JSON data can be used alongside the relational
data when querying.

## Persistence Pattern

In the POC, the repository pattern is used to separate the persistence logic from the entity. This is a departure
from the Active Record pattern used where the persistence logic is encapsulated directly in the entity. The thinking 
being that it would be easier to write unit tests with this separation in place. This assumption is still to be tested
as the testing for the POC is still to be implemented. It is possible to combine these approaches by injecting
the repository directly into the entity.
