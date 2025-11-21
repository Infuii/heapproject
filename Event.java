/**
 * Represents a collision between a particle and another particle, or a particle and a wall.
 */
public class Event implements Comparable<Event> {
	double _timeOfEvent;
	double _timeEventCreated;
	Particle _particle1;
	Particle _particle2;
	String _wall; // "LEFT", "RIGHT", "TOP", "BOTTOM"

	/**
	 * @param timeOfEvent the time when the collision will take place
	 * @param timeEventCreated the time when the event was first instantiated and added to the queue
	 */
	public Event (double timeOfEvent, double timeEventCreated) {
		_timeOfEvent = timeOfEvent;
		_timeEventCreated = timeEventCreated;
		_particle1 = null;
		_particle2 = null;
		_wall = null;
	}

	/**
	 * Constructor for particle-particle collision events
	 * @param timeOfEvent the time when the collision will take place
	 * @param timeEventCreated the time when the event was first instantiated and added to the queue
	 * @param p1 first particle involved in collision
	 * @param p2 second particle involved in collision
	 */
	public Event (double timeOfEvent, double timeEventCreated, Particle p1, Particle p2, String wall) {
		_timeOfEvent = timeOfEvent;
		_timeEventCreated = timeEventCreated;
		_particle1 = p1;
		_particle2 = p2;
		_wall = wall;
	}

	@Override
	/**
	 * Compares two Events based on their event times. Since you are implementing a maximum heap,
	 * this method assumes that the event with the smaller event time should receive higher priority.
	 */
	public int compareTo (Event e) {
		if (_timeOfEvent < e._timeOfEvent) {
			return +1;
		} else if (_timeOfEvent == e._timeOfEvent) {
			return 0;
		} else {
			return -1;
		}
	}
}
