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
	 */
	private void updateAllParticles (double delta) {
		for (Particle p : _particles) {
			p.update(delta);
		}
	}

	/**
	 * Executes the actual simulation.
	 */
	private void simulate (boolean show) {
		double lastTime = 0;

		// Create initial events, i.e., all the possible
		// collisions between all the particles and each other,
		// and all the particles and the walls.
		for (Particle p : _particles) {
			// Particle-wall collisions
			
			

			//particle-particle collisions
			/*
			 * Throughout the simulation, particles move per their velocities 
			 * and collide with other particles or walls. At the start 
			 * (simulate in ParticleSimulator.java), iterate over all particles
			 *  and enqueue every potential collision (particle–particle and
			 *  particle–wall). For a three-particle start state (p1, p2, p3), 
			 * the event queue would contain entries like these collisions with 
			 * their times. (This list shows queue contents, not the heap’s internal 
			 * tree structure.)
			 */

			for(Particle q : _particles) {
				if(p != q) {
					double collisionTime = p.getCollisionTime(q);
					if(collisionTime > 0 && collisionTime < _duration) {
						_events.add(new Event(collisionTime, 0, p, q));
					}
				}
			}
			
		}
		
		// Add  redraw events so particles move smoothly on screen
		if (show) {
			double redrawInterval = 0.1; // Redraw every 0.1 seconds
			for (double t = redrawInterval; t < _duration; t += redrawInterval) {
				_events.add(new Event(t, 0));
			}
		}
		
		
		_events.add(new TerminationEvent(_duration));
		while (_events.size() > 0) {
			Event event = _events.removeFirst();
			double delta = event._timeOfEvent - lastTime;

			if (event instanceof TerminationEvent) {
				updateAllParticles(delta);
				break;
			}

			// Check if event still valid; if not, then skip this event
			// if (event not valid) {
			//   continue;
			// }


			// Since the event is valid, then pause the simulation for the right
			// amount of time, and then update the screen.
			if (show) {
				try {
					Thread.sleep((long) (delta * 1000));
				} catch (InterruptedException ie) {}
			}

			// Update positions of all particles
			updateAllParticles(delta);

			// Update the velocity of the particle(s) involved in the collision
			// (either for a particle-wall collision or a particle-particle collision).
			if (event._particle1 != null && event._particle2 != null) {
				// Particle-particle collision
				event._particle1.updateAfterCollision(event._timeOfEvent, event._particle2);
				
				// Enqueue new events for the particles that were involved in this collision
				for (Particle p : _particles) {
					if (p == event._particle1 || p == event._particle2) {
						// Create new collision events for the particles that just collided
						for (Particle q : _particles) {
							if (p != q) {
								double collisionTime = p.getCollisionTime(q);
								if (collisionTime > 0) {
									double eventTime = event._timeOfEvent + collisionTime;
									if (eventTime < _duration) {
										_events.add(new Event(eventTime, event._timeOfEvent, p, q));
									}
								}
							}
						}
					}
				}
				
			}

			// Update the time of our simulation
			lastTime = event._timeOfEvent;

			// Redraw the screen
			if (show) {
				repaint();
			}
		}

		// Print out the final state of the simulation
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
