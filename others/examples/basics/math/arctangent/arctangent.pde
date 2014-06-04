#
# Arctangent.
#
# Move the mouse to change the direction of the eyes.
# The atan2() function computes the angle from each eye
# to the cursor.
#

def setup()
  size(640, 360)
  no_stroke()
  @e1 = Eye.new(250,  16, 120)
  @e2 = Eye.new(164, 185,  80)
  @e3 = Eye.new(420, 230, 220)
end

def draw()
  background(102)

  @e1.update(mouse_x, mouse_y)
  @e2.update(mouse_x, mouse_y)
  @e3.update(mouse_x, mouse_y)

  @e1.display()
  @e2.display()
  @e3.display()
end

class Eye
  def initialize(tx, ty, ts)
    @x = tx
    @y = ty
    @size = ts
    @angle = 0.0
  end

  def update(mx, my)
    @angle = atan2(my-@y, mx-@x)
  end

  def display()
    push_matrix()
    translate(@x, @y)
    fill(255)
    ellipse(0, 0, @size, @size)
    rotate(@angle)
    fill(153, 204, 0)
    ellipse(@size/4, 0, @size/2, @size/2)
    pop_matrix()
  end
end

