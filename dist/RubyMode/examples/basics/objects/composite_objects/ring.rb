class Ring
  attr_reader :on

  def start(xpos, ypos)
    @x = xpos  # X-coordinate, y-coordinate
    @y = ypos
    @on = true  # Turns the display on and off
    @diameter = 1  # Diameter of the ring
  end

  def grow()
    if @on
      @diameter += 0.5
      if @diameter > $app.width*2
        @diameter = 0.0
      end
    end
  end

  def display()
    if @on
      no_fill()
      stroke_weight(4)
      stroke(155, 153)
      ellipse(@x, @y, @diameter, @diameter)
    end
  end
end

