# This one has a long lineage:
# It was originally adapted to Shoes in Ruby,
# from a Python example for Nodebox, and then, now
# to Ruby-Processing.

# For fun, try running it via jirb, and 
# playing with the attr_accessors, as 
# well as the background.

# This example now demonstrates the use of the control_panel.

# -- omygawshkenas

load_library :control_panel

attr_accessor :x_wiggle, :y_wiggle, :magnitude, :bluish, :panel, :laf, :hide  

def setup
  size 600, 600  
  control_panel do |c|
    c.look_feel "Nimbus"
    c.slider    :bluish, 0.0..1.0, 0.5
    c.slider    :alpha,  0.0..1.0, 0.5
    c.checkbox  :go_big
    c.button    :reset
    c.menu      :shape, ['oval', 'square']
    @panel = c
  end
  @hide = false
  
  @shape = 'oval'
  @alpha, @bluish = 0.5, 0.5
  @x_wiggle, @y_wiggle = 10.0, 0
  @magnitude = 8.15
  @background = [0.06, 0.03, 0.18]
  color_mode RGB, 1
  ellipse_mode CORNER
  smooth
end

def background=(*args)
  @background = args.flatten
end


def draw_background
  @background[3] = @alpha
  fill *@background if @background[0]
  rect 0, 0, width, height
end

def reset
  @y_wiggle = 0
end

def draw
  # only make control_panel visible once, or again when hide is false
  unless hide
    @hide = true
    panel.setVisible(hide)    
  end
  draw_background
  
  # Seed the random numbers for consistent placement from frame to frame
  srand(0)
  horiz, vert, mag = @x_wiggle, @y_wiggle, @magnitude
  
  if @go_big
    mag  *= 2
    vert /= 2
  end
  
  blu = bluish
  x, y = (self.width / 2), -27
  c = 0.0
  
  64.times do 
    x += cos(horiz)*mag
    y += log10(vert)*mag + sin(vert) * 2
    fill(sin(@y_wiggle + c), rand * 0.2, rand * blu, 0.5)
    s = 42 + cos(vert) * 17
    args = [x-s/2, y-s/2, s, s]
    @shape == 'oval' ? oval(*args) : rect(*args)
    vert += rand * 0.25
    horiz += rand * 0.25
    c += 0.1
  end
  
  @x_wiggle += 0.05
  @y_wiggle += 0.1

end

def mouse_pressed
  @hide = false if hide
end

