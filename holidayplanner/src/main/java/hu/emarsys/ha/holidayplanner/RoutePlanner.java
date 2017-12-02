package hu.emarsys.ha.holidayplanner;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.emarsys.ha.holidayplanner.model.Destination;

public class RoutePlanner {
	/** {@link Logger} instance. */
	private final Logger logger = LoggerFactory.getLogger(RoutePlanner.class);

	public final String planRoute(final Collection<String> destinationDefs) {
		logger.info("Planning route for destinations: {}", destinationDefs);
		if ((destinationDefs == null) || destinationDefs.isEmpty()) {
			throw new IllegalArgumentException("No destinations was present");
		}

		final Collection<Destination> destinations = convertDefsToDestinations(destinationDefs);
		logger.debug("Created destinations definitions: {}", destinations);
		final Set<Destination> destinationsPlan = doDestinationsPlanning(destinations);
		logger.debug("Planned holiday route of destinations is {}", destinationsPlan);
		final String resultString = createResultString(destinationsPlan);
		logger.info("Planned route is: {}", resultString);
		return resultString;
	}

	private static final String DEST_SPLIT_STRING = "=>";

	private Collection<Destination> convertDefsToDestinations(final Collection<String> destinationDefs) {
		final Map<String, Destination> destinationMap = new HashMap<String, Destination>(destinationDefs.size());
		for (final String destinationDef : destinationDefs) {
			final String[] destinationDefVars = destinationDef.split(DEST_SPLIT_STRING);
			final String destinationName = destinationDefVars[0].trim();
			if (!destinationMap.containsKey(destinationName)) {
				destinationMap.put(destinationName, new Destination(destinationName));
			}

			if ((destinationDefVars.length > 1) && StringUtils.isNotBlank(destinationDefVars[1])) {
				final Destination destination = destinationMap.get(destinationName);
				final String dependentDestinationName = destinationDefVars[1].trim();
				if (StringUtils.equals(destinationName, dependentDestinationName)) {
					logger.error("Destination {} depends on itself!", destinationName);
					throw new IllegalArgumentException("Destination [" + destinationName + "] can't depend on itself!");
				}
				if (!destinationMap.containsKey(dependentDestinationName)) {
					destinationMap.put(dependentDestinationName, new Destination(dependentDestinationName));
				}
				destination.getDependentDestinations().add(destinationMap.get(dependentDestinationName));
			}
		}

		return destinationMap.values();
	}

	private Set<Destination> doDestinationsPlanning(final Collection<Destination> destinations) {
		final Set<Destination> route = new LinkedHashSet<Destination>(destinations.size(), 1);
		for (final Destination destination : destinations) {
			final Stack<Destination> dependentStack = new Stack<Destination>();
			insertDestinationToPlan(destination, route, dependentStack);
		}
		return route;
	}

	private void insertDestinationToPlan(final Destination destination, final Set<Destination> currentRoute,
			final Stack<Destination> dependentStack) {
		if (currentRoute.contains(destination)) {
			return;
		}
		if (!destination.getDependentDestinations().isEmpty()) {
			dependentStack.push(destination);
			for (final Destination dependentDestination : destination.getDependentDestinations()) {
				if (dependentStack.contains(dependentDestination)) {
					logger.error("Cyclic dependency on destination [{}]", destination);
					throw new IllegalArgumentException(
							"Cyclic dependency detected on destination (" + destination + ")!");
				}
				insertDestinationToPlan(dependentDestination, currentRoute, dependentStack);
			}
		}
		currentRoute.add(destination);
	}

	private String createResultString(final Collection<Destination> orderedDestinations) {
		final StringBuilder sb = new StringBuilder();
		for (final Destination destination : orderedDestinations) {
			sb.append(destination.getName());
			sb.append(" ");
		}
		return sb.toString().trim();
	}
}
