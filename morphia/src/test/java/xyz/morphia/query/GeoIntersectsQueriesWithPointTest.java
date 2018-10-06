package xyz.morphia.query;

import com.mongodb.client.model.geojson.MultiPoint;
import com.mongodb.client.model.geojson.Point;
import org.junit.Ignore;
import org.junit.Test;
import xyz.morphia.TestBase;
import xyz.morphia.geo.AllTheThings;
import xyz.morphia.geo.Area;
import xyz.morphia.geo.City;
import xyz.morphia.geo.Regions;
import xyz.morphia.geo.Route;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static xyz.morphia.geo.GeoJson.geometryCollection;
import static xyz.morphia.geo.GeoJson.lineString;
import static xyz.morphia.geo.GeoJson.multiPoint;
import static xyz.morphia.geo.GeoJson.multiPolygon;
import static xyz.morphia.geo.GeoJson.point;
import static xyz.morphia.geo.GeoJson.polygon;
import static xyz.morphia.geo.GeoJson.position;

@Ignore("Defer fixing the geo tests until after the core is fixed")
public class GeoIntersectsQueriesWithPointTest extends TestBase {
    @Test
    public void shouldFindAPointThatExactlyMatchesTheQueryPoint() {
        // given
        Point coordsOfManchester = point(53.4722454, -2.2235922);
        City manchester = new City("Manchester", coordsOfManchester);
        getDatastore().save(manchester);
        City london = new City("London", point(51.5286416, -0.1015987));
        getDatastore().save(london);
        City sevilla = new City("Sevilla", point(37.3753708, -5.9550582));
        getDatastore().save(sevilla);

        getDatastore().ensureIndexes();

        // when
        List<City> matchingCity = getDatastore().find(City.class)
                                                .field("location")
                                                .intersects(coordsOfManchester)
                                                .asList();

        // then
        assertThat(matchingCity.size(), is(1));
        assertThat(matchingCity.get(0), is(manchester));
    }

    @Test
    public void shouldFindAreasWhereTheGivenPointIsOnTheBoundary() {
        // given
        Area sevilla = new Area("Spain",
            polygon(position(37.40759155713022, -5.964911067858338),
                position(37.40341208875179, -5.9643941558897495),
                position(37.40297396667302, -5.970452763140202),
                position(37.40759155713022, -5.964911067858338)));
        getDatastore().save(sevilla);
        Area newYork = new Area("New York",
            polygon(position(40.75981395319104, -73.98302106186748),
                position(40.7636824529618, -73.98049869574606),
                position(40.76962974853814, -73.97964206524193),
                position(40.75981395319104, -73.98302106186748)));
        getDatastore().save(newYork);
        Area london = new Area("London",
            polygon(position(51.507780365645885, -0.21786745637655258),
                position(51.50802478194237, -0.21474729292094707),
                position(51.5086863655597, -0.20895397290587425),
                position(51.507780365645885, -0.21786745637655258)));
        getDatastore().save(london);
        getDatastore().ensureIndexes();

        // when
        List<Area> areaContainingPoint = getDatastore().find(Area.class)
                                                       .field("area")
                                                       .intersects(point(51.507780365645885, -0.21786745637655258))
                                                       .asList();

        // then
        assertThat(areaContainingPoint.size(), is(1));
        assertThat(areaContainingPoint.get(0), is(london));
    }

    @Test
    public void shouldFindGeometryCollectionsWhereTheGivenPointIntersectsWithOneOfTheEntities() {
        final MultiPoint multiPoint = multiPoint(position(37.40759155713022, -5.964911067858338),
            position(37.40341208875179, -5.9643941558897495),
            position(37.40297396667302, -5.970452763140202));
        AllTheThings sevilla = new AllTheThings("Spain", geometryCollection(multiPoint,
            polygon(position(37.40759155713022, -5.964911067858338),
                position(37.40341208875179, -5.9643941558897495),
                position(37.40297396667302, -5.970452763140202),
                position(37.40759155713022, -5.964911067858338)),
            polygon(position(37.38744598813355, -6.001141928136349),
                position(37.385990973562, -6.002588979899883),
                position(37.386126928031445, -6.002463921904564),
                position(37.38744598813355, -6.001141928136349))));
        getDatastore().save(sevilla);

        // insert something that's not a geocollection
        Regions usa = new Regions("US", multiPolygon(
            polygon(
                position(40.75981395319104, -73.98302106186748),
                position(40.7636824529618, -73.98049869574606),
                position(40.76962974853814, -73.97964206524193),
                position(40.75981395319104, -73.98302106186748)),
            polygon(
                position(28.326568258926272, -81.60542246885598),
                position(28.327541397884488, -81.6022228449583),
                position(28.32950334995985, -81.60564735531807),
                position(28.326568258926272, -81.60542246885598))));
        getDatastore().save(usa);

        AllTheThings london = new AllTheThings("London", geometryCollection(
            point(53.4722454, -2.2235922),
            lineString(
                position(51.507780365645885, -0.21786745637655258),
                position(51.50802478194237, -0.21474729292094707),
                position(51.5086863655597, -0.20895397290587425)),
            polygon(
                position(51.498216362670064, 0.0074849557131528854),
                position(51.49176875129342, 0.01821178011596203),
                position(51.492886897176504, 0.05523204803466797),
                position(51.49393044412136, 0.06663135252892971),
                position(51.498216362670064, 0.0074849557131528854))));
        getDatastore().save(london);
        getDatastore().ensureIndexes();

        // when
        List<AllTheThings> everythingInTheUK = getDatastore().find(AllTheThings.class)
                                                             .field("everything")
                                                             .intersects(point(51.50802478194237, -0.21474729292094707))
                                                             .asList();

        // then
        assertThat(everythingInTheUK.size(), is(1));
        assertThat(everythingInTheUK.get(0), is(london));
    }

    @Test
    public void shouldFindRegionsWhereTheGivenPointIsOnABoundary() {
        Regions sevilla = new Regions("Spain", multiPolygon(
            polygon(
                position(37.40759155713022, -5.964911067858338),
                position(37.40341208875179, -5.9643941558897495),
                position(37.40297396667302, -5.970452763140202),
                position(37.40759155713022, -5.964911067858338)),
            polygon(
                position(37.38744598813355, -6.001141928136349),
                position(37.385990973562, -6.002588979899883),
                position(37.386126928031445, -6.002463921904564),
                position(37.38744598813355, -6.001141928136349))));
        getDatastore().save(sevilla);

        Regions usa = new Regions("US", multiPolygon(
            polygon(
                position(40.75981395319104, -73.98302106186748),
                position(40.7636824529618, -73.98049869574606),
                position(40.76962974853814, -73.97964206524193),
                position(40.75981395319104, -73.98302106186748)),
            polygon(
                position(28.326568258926272, -81.60542246885598),
                position(28.327541397884488, -81.6022228449583),
                position(28.32950334995985, -81.60564735531807),
                position(28.326568258926272, -81.60542246885598))));
        getDatastore().save(usa);

        Regions london = new Regions("London", multiPolygon(
            polygon(
                position(51.507780365645885, -0.21786745637655258),
                position(51.50802478194237, -0.21474729292094707),
                position(51.5086863655597, -0.20895397290587425),
                position(51.507780365645885, -0.21786745637655258)),
            polygon(
                position(51.498216362670064, 0.0074849557131528854),
                position(51.49176875129342, 0.01821178011596203),
                position(51.492886897176504, 0.05523204803466797),
                position(51.49393044412136, 0.06663135252892971),
                position(51.498216362670064, 0.0074849557131528854))));
        getDatastore().save(london);
        getDatastore().ensureIndexes();

        // when
        List<Regions> regionsInTheUK = getDatastore().find(Regions.class)
                                                     .field("regions")
                                                     .intersects(point(51.498216362670064, 0.0074849557131528854))
                                                     .asList();

        // then
        assertThat(regionsInTheUK.size(), is(1));
        assertThat(regionsInTheUK.get(0), is(london));
    }

    @Test
    public void shouldFindRoutesThatAGivenPointIsOn() {
        // given
        Route sevilla = new Route("Spain", lineString(
            position(37.40759155713022, -5.964911067858338),
            position(37.40341208875179, -5.9643941558897495),
            position(37.40297396667302, -5.970452763140202)));
        getDatastore().save(sevilla);

        Route newYork = new Route("New York", lineString(
            position(40.75981395319104, -73.98302106186748),
            position(40.7636824529618, -73.98049869574606),
            position(40.76962974853814, -73.97964206524193)));
        getDatastore().save(newYork);

        Route london = new Route("London", lineString(
            position(51.507780365645885, -0.21786745637655258),
            position(51.50802478194237, -0.21474729292094707),
            position(51.5086863655597, -0.20895397290587425)));
        getDatastore().save(london);

        Route londonToParis = new Route("London To Paris", lineString(
            position(51.5286416, -0.1015987),
            position(48.858859, 2.3470599)));
        getDatastore().save(londonToParis);

        getDatastore().ensureIndexes();

        // when
        List<Route> routeContainingPoint = getDatastore().find(Route.class)
                                                         .field("route")
                                                         .intersects(point(37.40759155713022, -5.964911067858338))
                                                         .asList();

        // then
        assertThat(routeContainingPoint.size(), is(1));
        assertThat(routeContainingPoint.get(0), is(sevilla));
    }

}
