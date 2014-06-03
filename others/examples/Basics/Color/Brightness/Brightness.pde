#
# Brightness
# by Rusty Robison.
#
# Brightness is the relative lightness or darkness of a color.
# Move the cursor vertically over each bar to alter its brightness.
#

BAR_WIDTH = 20

def setup()
  size(640, 360)
  color_mode(HSB, width, 100, width)
  no_stroke()
  background(0)

  @last_bar = -1
end

def draw()
  which_bar = mouse_x / BAR_WIDTH
  if which_bar != @last_bar
    bar_x = which_bar * BAR_WIDTH
    fill(bar_x, 100, mouse_y)
    rect(bar_x, 0, BAR_WIDTH, height)
    @last_bar = which_bar
  end
end
