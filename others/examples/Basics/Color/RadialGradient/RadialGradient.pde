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
  colorMode(HSB, 360, 100, 100)
  noStroke()
  ellipseMode(RADIUS)
  frameRate(1)
end

def draw()
  background(0)
  0.step(width, @dim) do |x|
    drawGradient(x, height/2);
  end 
end

def drawGradient(x, y)
  radius = @dim/2
  h = random(0, 360)
  radius.downto(1) do |r|
    fill(h, 90, 90)
    ellipse(x, y, r, r)
    h = (h + 1) % 360
  end
end

