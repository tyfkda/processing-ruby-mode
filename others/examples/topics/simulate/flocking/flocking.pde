#
# Flocking
# by Daniel Shiffman.
#
# An implementation of Craig Reynold's Boids program to simulate
# the flocking behavior of birds. Each boid steers itself based on
# rules of avoidance, alignment, and coherence.
#
# Click the mouse to add a new boid.
#

def setup()
  size(640, 360)
  @flock = Flock.new()
  # Add an initial set of boids into the system
  150.times do
    @flock.addBoid(Boid.new(width/2,height/2))
  end
end

def draw()
  background(50)
  @flock.run()
end

# Add a new boid into the System
def mousePressed()
  @flock.addBoid(Boid.new(mouseX,mouseY))
end

