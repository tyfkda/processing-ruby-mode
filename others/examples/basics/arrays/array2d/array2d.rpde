#
# Array 2D.
#
# Demonstrates the syntax for creating a two-dimensional (2D) array.
# Values in a 2D array are accessed through two index values.
# 2D arrays are useful for storing images. In this example, each dot
# is colored in relation to its distance from the center of the image.
#

def setup()
  size(640, 360)
  maxDistance = dist(width/2, height/2, width, height)
  @distances = (0...width).map do |x|
    (0...height).map do |y|
      distance = dist(width/2, height/2, x, y)
      distance/maxDistance * 255
    end
  end
  @spacer = 10
  no_loop()  # Run once and stop
end

def draw()
  background(0)
  # This embedded loop skips over values in the arrays based on
  # the spacer variable, so there are more values in the array
  # than are drawn here. Change the value of the spacer variable
  # to change the density of the points
  0.step(height - 1, @spacer) do |y|
    0.step(width - 1, @spacer) do |x|
      stroke(@distances[x][y])
      point(x + @spacer/2, y + @spacer/2)
    end
  end
end

