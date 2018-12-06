package xyz.morphia.example;

import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.junit.Assert;
import xyz.morphia.Datastore;
import xyz.morphia.Morphia;
import xyz.morphia.annotations.Entity;
import xyz.morphia.annotations.Field;
import xyz.morphia.annotations.Id;
import xyz.morphia.annotations.Index;
import xyz.morphia.annotations.Indexes;
import xyz.morphia.annotations.Property;
import xyz.morphia.annotations.Reference;
import xyz.morphia.query.Query;
import xyz.morphia.query.UpdateOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used in the Quick Tour documentation and is used to demonstrate various Morphia features.
 */
@SuppressWarnings("unused")
public final class QuickTour {
    private QuickTour() {
    }

    public static void main(final String[] args) {
        final Datastore datastore = Morphia.createDatastore("morphia_example");

        // tell morphia where to find your classes
        // can be called multiple times with different packages or classes
        datastore.getMapper().mapPackage("xyz.morphia.example");

        // create the Datastore connecting to the database running on the default port on the local host
        datastore.getDatabase().drop();
        datastore.ensureIndexes();

        final Employee elmer = new Employee("Elmer Fudd", 50000.0);
        datastore.save(elmer);

        final Employee daffy = new Employee("Daffy Duck", 40000.0);
        datastore.save(daffy);

        final Employee pepe = new Employee("Pepé Le Pew", 25000.0);
        datastore.save(pepe);

        elmer.getDirectReports().add(daffy);
        elmer.getDirectReports().add(pepe);

        datastore.save(elmer);

        Query<Employee> query = datastore.find(Employee.class);
        final List<Employee> employees = query.asList();

        Assert.assertEquals(3, employees.size());

        List<Employee> underpaid = datastore.find(Employee.class)
                                            .filter("salary <=", 30000)
                                            .asList();
        Assert.assertEquals(1, underpaid.size());

        underpaid = datastore.find(Employee.class)
                             .field("salary").lessThanOrEq(30000)
                             .asList();
        Assert.assertEquals(1, underpaid.size());

        final Query<Employee> underPaidQuery = datastore.find(Employee.class)
                                                        .filter("salary <=", 30000);
        final UpdateOperations<Employee> updateOperations = datastore.createUpdateOperations(Employee.class)
                                                                     .inc("salary", 10000);

        final UpdateResult results = datastore.updateOne(underPaidQuery, updateOperations);

        Assert.assertEquals(1, results.getModifiedCount());

        final Query<Employee> overPaidQuery = datastore.find(Employee.class)
                                                       .filter("salary >", 100000);
        datastore.delete(overPaidQuery);
    }
}

@Entity("employees")
@Indexes(@Index(fields = @Field("salary")))
@SuppressWarnings("unused")
class Employee {
    @Id
    private ObjectId id;
    private String name;
    private Integer age;
    @Reference
    private Employee manager;
    @Reference
    private List<Employee> directReports = new ArrayList<>();
    @Property("wage")
    private Double salary;

    Employee() {
    }

    Employee(final String name, final Double salary) {
        this.name = name;
        this.salary = salary;
    }

    public List<Employee> getDirectReports() {
        return directReports;
    }

    public void setDirectReports(final List<Employee> directReports) {
        this.directReports = directReports;
    }

    public ObjectId getId() {
        return id;
    }

    public Employee getManager() {
        return manager;
    }

    public void setManager(final Employee manager) {
        this.manager = manager;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(final Double salary) {
        this.salary = salary;
    }
}
