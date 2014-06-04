#
# Hue.
#
# Hue is the color reflected from or transmitted through an object
# and is typically referred to as the name of the color (red, blue, yellow, etc.)
# Move the cursor vertically over each bar to alter its hue.
#

BAR_WIDTH = 20

def setup()
  size(640, 360)
  color_mode(HSB, height, height, height)
  no_stroke()
  background(0)
  @last_bar = -1
end

def draw()
  which_bar = mouse_x / BAR_WIDTH
  if which_bar != @last_bar
    barX = which_bar * BAR_WIDTH
    fill(mouse_y, height, height)
    rect(barX, 0, BAR_WIDTH, height)
    @last_bar = which_bar
  end
end
