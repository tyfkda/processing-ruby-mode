#
# Radial Gradient.
#
# Draws are series of concentric circles to create a gradient
# from one color to another.
#

def setup()
  size(640, 360)
  @dim = width/2
  background(0)
  color_mode(HSB, 360, 100, 100)
  no_stroke()
  ellipse_mode(RADIUS)
  frame_rate(1)
end

def draw()
  background(0)
  0.step(width, @dim) do |x|
    draw_gradient(x, height/2);
  end
end

def draw_gradient(x, y)
  radius = @dim/2
  h = random(0, 360)
  radius.downto(1) do |r|
    fill(h, 90, 90)
    ellipse(x, y, r, r)
    h = (h + 1) % 360
  end
end

