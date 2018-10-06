package xyz.morphia.geo;

import com.mongodb.client.model.geojson.GeometryCollection;
import xyz.morphia.annotations.Indexed;
import xyz.morphia.utils.IndexType;

public final class AllTheThings {
    private String name;

    @Indexed(IndexType.GEO2DSPHERE)
    private GeometryCollection everything;

    @SuppressWarnings("UnusedDeclaration")
        // used by morphia
    AllTheThings() {
    }

    public AllTheThings(final String name, final GeometryCollection everything) {
        this.name = name;
        this.everything = everything;
    }

    @Override
    public int hashCode() {
        int result = everything.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AllTheThings that = (AllTheThings) o;

        if (!everything.equals(that.everything)) {
            return false;
        }
        if (!name.equals(that.name)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "AllTheThings{"
               + "everything=" + everything
               + ", name='" + name + '\''
               + '}';
    }
}
