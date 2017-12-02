package hu.emarsys.ha.holidayplanner.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Destination class containing the name of the place to be visited, and a set
 * of dependent destinations which should be used as predecessors in route
 * calculation.
 *
 * @author Horváth Márton
 *
 */
public class Destination implements Serializable {
	/** Serial version UID. */
	private static final long serialVersionUID = 1508045823740000545L;
	/** Name of the destination. */
	private final String name;
	/**
	 * {@link Set} of dependent destinations which should be visited before the
	 * current {@link Destination}.
	 */
	private final Set<Destination> dependentDestinations = new HashSet<Destination>();

	/**
	 * Ctor.
	 *
	 * @param name
	 *            destination name
	 */
	public Destination(final String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Set<Destination> getDependentDestinations() {
		return dependentDestinations;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Destination is ");
		sb.append(name);
		sb.append(" [");
		if (!dependentDestinations.isEmpty()) {
			String prefix = "";
			for (final Destination destination : dependentDestinations) {
				sb.append(prefix);
				prefix = ",";
				sb.append(destination.getName());
			}

		}
		sb.append("]");

		return sb.toString();
	}
}
