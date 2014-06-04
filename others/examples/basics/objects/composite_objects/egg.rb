class Egg
  # Constructor
  def initialize(xpos, ypos, t, s)
    @x = xpos  # X-coordinate, y-coordinate
    @y = ypos
    @tilt = t  # Left and right angle offset
    @angle = 0.0  # Used to define the tilt
    @scalar = s / 100.0  # Height of the egg
  end

  def wobble()
    @tilt = cos(@angle) / 8
    @angle += 0.1
  end

  def display()
    no_stroke()
    fill(255)
    push_matrix()
    translate(@x, @y)
    rotate(@tilt)
    scale(@scalar)
    begin_shape()
    vertex(0, -100)
    bezier_vertex(25, -100, 40, -65, 40, -40)
    bezier_vertex(40, -15, 25, 0, 0, 0)
    bezier_vertex(-25, 0, -40, -15, -40, -40)
    bezier_vertex(-40, -65, -25, -100, 0, -100)
    end_shape()
    pop_matrix()
  end
end

