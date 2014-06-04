#
# Create Image.
#
# The createImage() function provides a fresh buffer of pixels to play with.
# This example creates an image gradient.
#

def setup()
  size(640, 360)
  @img = create_image(230, 230, ARGB)
  @img.pixels.length.times do |i|
    a = map(i, 0, @img.pixels.length, 255, 0)
    @img.pixels[i] = color(0, 153, 204, a)
  end
end

def draw()
  background(0)
  image(@img, 90, 80)
  image(@img, mouse_x-@img.width/2, mouse_y-@img.height/2)
end
