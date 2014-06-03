#
# Additive Wave
# by Daniel Shiffman. 
# 
# Create a more complex wave by adding two waves together. 
#
 
XSPACING = 8   # How far apart should each horizontal location be spaced
MAXWAVES = 4   # total # of waves to add together

def setup()
  size(640, 360)
  frameRate(30)
  colorMode(RGB, 255, 255, 255, 100)
  w = width + 16

  @theta = 0.0
  @amplitude = Array.new(MAXWAVES)   # Height of wave
  @dx = Array.new(MAXWAVES)          # Value for incrementing X, to be calculated as a function of period and XSPACING
  @yvalues = nil                     # Using an array to store height values for the wave (not entirely necessary)

  MAXWAVES.times do |i|
    @amplitude[i] = random(10,30)
    period = random(100,300) # How many pixels before the wave repeats
    @dx[i] = (TWO_PI / period) * XSPACING;
  end

  @yvalues = Array.new(w/XSPACING) { 0.0 }
end

def draw()
  background(0)
  calcWave()
  renderWave()
end

def calcWave()
  # Increment theta (try different values for 'angular velocity' here
  @theta += 0.02;

  # Set all height values to zero
  @yvalues.length.times do |i|
    @yvalues[i] = 0
  end
 
  # Accumulate wave height values
  MAXWAVES.times do |j|
    x = @theta
    @yvalues.length.times do |i|
      # Every other wave is cosine instead of sine
      if j % 2 == 0
        @yvalues[i] += sin(x)*@amplitude[j]
      else
        @yvalues[i] += cos(x)*@amplitude[j]
      end
      x+=@dx[j]
    end
  end
end

def renderWave()
  # A simple way to draw the wave with an ellipse at each location
  noStroke()
  fill(255,50)
  ellipseMode(CENTER)
  @yvalues.length.times do |x|
    ellipse(x*XSPACING,width/2+@yvalues[x],16,16)
  end
end

