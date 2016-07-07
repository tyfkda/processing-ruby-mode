# The Flock (a list of Boid objects)

class Flock
  def initialize()
    @boids = []  # An ArrayList for all the boids
  end

  def run()
    @boids.each do |b|
      b.run(@boids)  # Passing the entire list of boids to each boid individually
    end
  end

  def addBoid(b)
    @boids.push(b)
  end
end

