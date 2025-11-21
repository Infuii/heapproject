import java.util.*;
import java.util.function.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.sound.sampled.*;

public class ParticleSimulator extends JPanel {
	private Heap<Event> _events;
	private java.util.List<Particle> _particles;
	private double _duration;
	private int _width;

	/**
	 * @param filename the name of the file to parse containing the particles
	 */
	public ParticleSimulator (String filename) throws IOException {
		_events = new HeapImpl<>();

		// Parse the specified file and load all the particles.
		Scanner s = new Scanner(new File(filename));
		_width = s.nextInt();
		_duration = s.nextDouble();
		s.nextLine();
		_particles = new ArrayList<>();
		while (s.hasNext()) {
			String line = s.nextLine();
			Particle particle = Particle.build(line);
			_particles.add(particle);
		}

		setPreferredSize(new Dimension(_width, _width));
	}

	@Override
	/**
	 * Draws all the particles on the screen at their current locations
	 * DO NOT MODIFY THIS METHOD
	 */
        public void paintComponent (Graphics g) {
		g.clearRect(0, 0, _width, _width);
		for (Particle p : _particles) {
			p.draw(g);
		}
	}

	// Helper class to signify the final event of the simulation.
	private class TerminationEvent extends Event {
		TerminationEvent (double timeOfEvent) {
			super(timeOfEvent, 0);
		}
	}

	/**
	 * Helper method to update the positions of all the particles based on their current velocities.
	 * @param delta the amount of time to update each particle's position
	 */
	private void updateAllParticles (double delta) {
		for (Particle p : _particles) {
			p.update(delta);
		}
	}

	/**
	 * Helper method to create all future collision events for a given particle.
	 * This includes both wall collisions and particle-particle collisions.
	 * @param p the particle for which to create future events
	 * @param currentTime the current time of the simulation
	 */
	private void createEventsForParticle(Particle p, double currentTime) {
		// Create wall collision events
		if (p._vx < 0) {
			double time = (p._radius - p._x) / p._vx; // Time until collision with left wall
			if (time > 0 && currentTime + time < _duration) { // Check for valid collision time
				_events.add(new Event(currentTime + time, currentTime, p, null, "LEFT")); // Add left wall collision event
			}
		}
		if (p._vx > 0) {
			double time = (_width - p._radius - p._x) / p._vx; // Time until collision with right wall 
			if (time > 0 && currentTime + time < _duration) {
				_events.add(new Event(currentTime + time, currentTime, p, null, "RIGHT")); // Add right wall collision event
			}
		}
		if (p._vy < 0) {
			double time = (p._radius - p._y) / p._vy; // Time until collision with top wall
			if (time > 0 && currentTime + time < _duration) {
				_events.add(new Event(currentTime + time, currentTime, p, null, "TOP")); // Add top wall collision event
			}
		}
		if (p._vy > 0) {
			double time = (_width - p._radius - p._y) / p._vy; // Time until collision with bottom wall
			if (time > 0 && currentTime + time < _duration) {
				_events.add(new Event(currentTime + time, currentTime, p, null, "BOTTOM")); // Add bottom wall collision event
			}
		}
		
		// Create particle-particle collision events
		for (Particle q : _particles) {
			if (p != q) {
				double time = p.getCollisionTime(q); // Get time until collision with particle q
				if (time > 0 && time != Double.POSITIVE_INFINITY) { // Is Valid collision time ? 
					double eventTime = currentTime + time; //then calculate event time
					if (eventTime < _duration) {
						_events.add(new Event(eventTime, currentTime, p, q, null));
					}
				}
			}
		}
	}

	// Executes the actual simulation.
	private void simulate (boolean show) {
		double lastTime = 0;

		// Create initial events, i.e., all the possible collisions between all
		// the particles and each other, and all the particles and the walls.
		
		for (int i = 0; i < _particles.size(); i++) {
			Particle p = _particles.get(i);
			createEventsForParticle(p, lastTime); // Create all collision events for this particle
		}
		
		_events.add(new TerminationEvent(_duration));
		while (_events.size() > 0) {
			Event event = _events.removeFirst();
			double delta = event._timeOfEvent - lastTime;

			if (event instanceof TerminationEvent) {
				updateAllParticles(delta);
				break;
			}

			// Check if the event is still valid
			if (event._particle1 != null && event._timeEventCreated < event._particle1._lastUpdateTime) continue;
			if (event._particle2 != null && event._timeEventCreated < event._particle2._lastUpdateTime) continue;
			
			if (event._timeOfEvent > _duration) {
				updateAllParticles(_duration - lastTime);
				break;
			}

			// Since the event is valid, then pause the simulation for the right amount of time, and then update the screen.
			// if (show) {
			// 	try {
			// 		Thread.sleep((long) (delta * 1000));
			// 	} catch (InterruptedException ie) {}
			// }

			// Update positions of all particles
			updateAllParticles(delta);

			// Update the velocity of the particle(s) involved in the collision
			if (event._wall != null) {
				Particle p = event._particle1;
		
				// Wall collision -- reverse appropriate velocity based on wall type
				if (event._wall.equals("LEFT") || event._wall.equals("RIGHT")) p._vx = -p._vx;
				else p._vy = -p._vy;
				
				p._lastUpdateTime = event._timeOfEvent;
				createEventsForParticle(p, event._timeOfEvent); // Create new collision events for this particle
			}
			else if (event._particle2 != null) {
				event._particle1.updateAfterCollision(event._timeOfEvent, event._particle2); // Particle-particle collision
				// Create new events for BOTH particles that just collided
				createEventsForParticle(event._particle1, event._timeOfEvent);
				createEventsForParticle(event._particle2, event._timeOfEvent);
			}

			lastTime = event._timeOfEvent; // Update the time of our simulation

			if (show) {
				repaint();
			}
		}

		System.out.println(_width);
		System.out.println(_duration);
		for (Particle p : _particles) {
			System.out.println(p);
		}
	}
	public static void main (String[] args) throws IOException {
		if (args.length < 1) {
			System.out.println("Usage: java ParticalSimulator <filename>");
			System.exit(1);
		}
		ParticleSimulator simulator;
		simulator = new ParticleSimulator(args[0]);
		JFrame frame = new JFrame();
		frame.setTitle("Particle Simulator");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(simulator, BorderLayout.CENTER);
		frame.setVisible(true);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		simulator.simulate(true);
	}
}