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

/**
 * Class to plan a holiday route. The route planning is based on the
 * {@link Destination} list given, and considers the dependencies between
 * different {@link Destination} instances when trying to find a valid route.
 *
 * @author Horv�th M�rton
 *
 */
public class RoutePlanner {
	/** {@link Logger} instance. */
	private final Logger logger = LoggerFactory.getLogger(RoutePlanner.class);

	/**
	 * Plans a holiday route based on destination definitions given. A
	 * destination definition starts with a <i>destination name<i> followed by a
	 * whitespace, then the => constant value with a whitespace as well, and
	 * optionally a dependent destination name ends the definition.
	 *
	 * @param destinationDefs
	 *            {@link Collection} of destination definitions
	 * @return Textual representation of a holiday route
	 */
	public final String planRoute(final Collection<String> destinationDefs) {
		logger.info("Planning route for destinations: {}", destinationDefs);
		if ((destinationDefs == null) || destinationDefs.isEmpty()) {
			logger.error("No destination definitions were present ({})", destinationDefs);
			throw new IllegalArgumentException("No destinations were present");
		}

		final Collection<Destination> destinations = convertDefsToDestinations(destinationDefs);
		logger.debug("Created destinations definitions: {}", destinations);
		final Set<Destination> destinationsPlan = doDestinationsPlanning(destinations);
		logger.debug("Planned holiday route of destinations is {}", destinationsPlan);
		final String resultString = createResultString(destinationsPlan);
		logger.info("Planned route is: {}", resultString);
		return resultString;
	}

	/** Constant value used in the middle of a destination definition. */
	private static final String DEST_SPLIT_STRING = "=>";

	/**
	 * Converts textual destination definitions to a {@link Collection} of
	 * {@link Destination} objects.
	 *
	 * @param destinationDefs
	 *            textual destination definitions
	 * @return {@link Destination} objects
	 */
	private Collection<Destination> convertDefsToDestinations(final Collection<String> destinationDefs) {
		final Map<String, Destination> destinationMap = new HashMap<String, Destination>(destinationDefs.size());
		// Process destinations defined
		logger.debug("Processing primary destinations in destination definitions");
		for (final String destinationDef : destinationDefs) {
			logger.trace("Processing destination definition: {}", destinationDef);
			final String[] destinationDefVars = destinationDef.split(DEST_SPLIT_STRING);
			final String destinationName = destinationDefVars[0].trim();
			logger.trace("Found primary destination: {}", destinationName);
			if (!destinationMap.containsKey(destinationName)) {
				destinationMap.put(destinationName, new Destination(destinationName));
			}
		}

		// Process dependent destinations where defined
		logger.debug("Processing dependent destinations in destination definitions");
		for (final String destinationDef : destinationDefs) {
			logger.trace("Processing destination definition: {}", destinationDef);
			final String[] destinationDefVars = destinationDef.split(DEST_SPLIT_STRING);
			final String destinationName = destinationDefVars[0].trim();
			logger.trace("Identified primary destination ({}), looking for dependent destination", destinationName);
			if ((destinationDefVars.length > 1) && StringUtils.isNotBlank(destinationDefVars[1])) {
				final Destination destination = destinationMap.get(destinationName);
				final String dependentDestinationName = destinationDefVars[1].trim();
				logger.trace("Found dependent destination definition ({}) for primary destination ({})",
						dependentDestinationName, destinationName);
				if (!destinationMap.containsKey(dependentDestinationName)) {
					logger.error("Dependent destination ({}) is not a valid destination!", dependentDestinationName);
					throw new IllegalArgumentException(
							"Dependent destination [" + dependentDestinationName + "] is not a valid destination!");
				}
				if (StringUtils.equals(destinationName, dependentDestinationName)) {
					logger.error("Destination {} depends on itself!", destinationName);
					throw new IllegalArgumentException("Destination [" + destinationName + "] can't depend on itself!");
				}
				if (!destinationMap.containsKey(dependentDestinationName)) {
					destinationMap.put(dependentDestinationName, new Destination(dependentDestinationName));
				}
				logger.trace("Add dependent destination ({}) to primary destination ({}) dependent list",
						dependentDestinationName, destinationName);
				destination.getDependentDestinations().add(destinationMap.get(dependentDestinationName));
			}
		}

		return destinationMap.values();
	}

	/**
	 * Performs route planning based on {@link Destination} objects.
	 *
	 * @param destinations
	 *            {@link Destination} objects to be used when planning
	 * @return {@link Set} of {@link Destination} objects representing the
	 *         holiday route
	 */
	private Set<Destination> doDestinationsPlanning(final Collection<Destination> destinations) {
		final Set<Destination> route = new LinkedHashSet<Destination>(destinations.size(), 1);
		logger.debug("Do route planning of destinations: {}", destinations);
		for (final Destination destination : destinations) {
			final Stack<Destination> dependentStack = new Stack<Destination>();
			insertDestinationToPlan(destination, route, dependentStack);
		}
		logger.debug("Route planning resulted in final route:  {}", route);
		return route;
	}

	/**
	 * Checks a {@link Destination} and tries to insert it into the current
	 * route plan. Keeps track of dependent destinations to be able to avoid
	 * cyclic dependencies within the route.
	 *
	 * @param destination
	 *            current {@link Destination} to be checked
	 * @param currentRoute
	 *            current state of the holiday route
	 * @param dependentStack
	 *            {@link Stack} of dependent destination object used
	 */
	private void insertDestinationToPlan(final Destination destination, final Set<Destination> currentRoute,
			final Stack<Destination> dependentStack) {
		logger.debug("Try to find place of destination ({}) in current route ({})", destination, currentRoute);
		if (currentRoute.contains(destination)) {
			logger.debug("Destination ({}) is already in the route, skipping it.", destination);
			return;
		}
		if (!destination.getDependentDestinations().isEmpty()) {
			logger.trace("Destination ({}) has dependent destinations ({}), processing them", destination,
					destination.getDependentDestinations());
			dependentStack.push(destination);
			for (final Destination dependentDestination : destination.getDependentDestinations()) {
				logger.trace("Processing dependent destination ({}) in route planning", dependentDestination);
				if (dependentStack.contains(dependentDestination)) {
					logger.error("Cyclic dependency on destination [{}]", destination);
					throw new IllegalArgumentException(
							"Cyclic dependency detected on destination (" + destination + ")!");
				}
				insertDestinationToPlan(dependentDestination, currentRoute, dependentStack);
			}
		}
		logger.debug("Adding destination ({}) to current route", destination);
		currentRoute.add(destination);
	}

	/**
	 * Creates a textual result from the {@link Destination} objects treated as
	 * final route.
	 *
	 * @param orderedDestinations
	 *            ordered {@link Collection} of {@link Destination} objects
	 * @return textual representation of the route
	 */
	private String createResultString(final Collection<Destination> orderedDestinations) {
		logger.trace("Converting destination route ({}) to string", orderedDestinations);
		final StringBuilder sb = new StringBuilder();
		for (final Destination destination : orderedDestinations) {
			sb.append(destination.getName());
			sb.append(" ");
		}
		return sb.toString().trim();
	}
}
