package hu.emarsys.ha.holidayplanner.test;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import hu.emarsys.ha.holidayplanner.RoutePlanner;

public class TestRoutePlanner {
	@Test
	public void testRoutePlanner_singleDestination() {
		final Collection<String> destinations = new ArrayList<String>(1);
		destinations.add(createDestinationString("x", null));

		final RoutePlanner routePlanner = new RoutePlanner();
		final String routePlan = routePlanner.planRoute(destinations);
		Assert.assertNotNull(routePlan);
		Assert.assertEquals("x", routePlan);
	}

	@Test
	public void testRoutePlanner_3IndependentDestinations() {
		final Collection<String> destinations = new ArrayList<String>(1);
		destinations.add(createDestinationString("x", null));
		destinations.add(createDestinationString("y", null));
		destinations.add(createDestinationString("z", null));

		final RoutePlanner routePlanner = new RoutePlanner();
		final String routePlan = routePlanner.planRoute(destinations);
		Assert.assertNotNull(routePlan);
		Assert.assertTrue(routePlan.contains("x"));
		Assert.assertTrue(routePlan.contains("y"));
		Assert.assertTrue(routePlan.contains("z"));
	}

	@Test
	public void testRoutePlanner_3DestinationsWith1Dependent() {
		final Collection<String> destinations = new ArrayList<String>(1);
		destinations.add(createDestinationString("x", null));
		destinations.add(createDestinationString("y", "z"));
		destinations.add(createDestinationString("z", null));

		final RoutePlanner routePlanner = new RoutePlanner();
		final String routePlan = routePlanner.planRoute(destinations);
		Assert.assertNotNull(routePlan);
		Assert.assertTrue(routePlan.contains("x"));
		Assert.assertTrue(routePlan.contains("y"));
		Assert.assertTrue(routePlan.contains("z"));
		final String[] ySplit = routePlan.split("y", 1);
		Assert.assertTrue(ySplit[0].contains("z"));
	}

	@Test
	public void testRoutePlanner_3DestinationsWithMultiDependent() {
		final Collection<String> destinations = new ArrayList<String>(1);
		destinations.add(createDestinationString("x", null));
		destinations.add(createDestinationString("y", "z"));
		destinations.add(createDestinationString("y", "x"));
		destinations.add(createDestinationString("z", null));

		final RoutePlanner routePlanner = new RoutePlanner();
		final String routePlan = routePlanner.planRoute(destinations);
		Assert.assertNotNull(routePlan);
		Assert.assertTrue(routePlan.contains("x"));
		Assert.assertTrue(routePlan.contains("y"));
		Assert.assertTrue(routePlan.contains("z"));
		final String[] ySplit = routePlan.split("y", 1);
		Assert.assertTrue(ySplit[0].contains("z"));
		Assert.assertTrue(ySplit[0].contains("x"));
	}

	@Test
	public void testRoutePlanner_6DestinationsWith4Dependent() {
		final Collection<String> destinations = new ArrayList<String>(1);
		destinations.add(createDestinationString("u", null));
		destinations.add(createDestinationString("v", "w"));
		destinations.add(createDestinationString("w", "z"));
		destinations.add(createDestinationString("x", "u"));
		destinations.add(createDestinationString("y", "v"));
		destinations.add(createDestinationString("z", null));

		final RoutePlanner routePlanner = new RoutePlanner();
		final String routePlan = routePlanner.planRoute(destinations);
		Assert.assertNotNull(routePlan);
		Assert.assertTrue(routePlan.contains("u"));
		Assert.assertTrue(routePlan.contains("v"));
		Assert.assertTrue(routePlan.contains("w"));
		Assert.assertTrue(routePlan.contains("x"));
		Assert.assertTrue(routePlan.contains("y"));
		Assert.assertTrue(routePlan.contains("z"));
		final String[] vSplit = routePlan.split("v");
		Assert.assertTrue(vSplit[0].contains("w"));
		final String[] wSplit = routePlan.split("w");
		Assert.assertTrue(wSplit[0].contains("z"));
		final String[] xSplit = routePlan.split("x");
		Assert.assertTrue(xSplit[0].contains("u"));
		final String[] ySplit = routePlan.split("y");
		Assert.assertTrue(ySplit[0].contains("v"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRoutePlannerError_dependentOnItself() {
		final Collection<String> destinations = new ArrayList<String>(1);
		destinations.add(createDestinationString("x", "x"));

		final RoutePlanner routePlanner = new RoutePlanner();
		routePlanner.planRoute(destinations);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRoutePlannerError_cyclicDepedents() {
		final Collection<String> destinations = new ArrayList<String>(1);
		destinations.add(createDestinationString("x", "y"));
		destinations.add(createDestinationString("y", "z"));
		destinations.add(createDestinationString("z", "x"));

		final RoutePlanner routePlanner = new RoutePlanner();
		routePlanner.planRoute(destinations);
	}

	private String createDestinationString(final String name, final String dependentName) {
		final StringBuilder sb = new StringBuilder(name);
		sb.append(" => ");
		if (dependentName != null) {
			sb.append(dependentName);
		}
		return sb.toString();
	}
}
