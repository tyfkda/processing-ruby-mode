#
# Objects
# by hbarragan. 
# 
# Move the cursor across the image to change the speed and positions
# of the geometry. The class MRect defines a group of lines.
#
 
def setup()
  size(640, 360)
  fill(255, 204)
  no_stroke()
  @r1 = MRect.new(1, 134.0, 0.532, 0.1*height, 10.0, 60)
  @r2 = MRect.new(2, 44.0, 0.166, 0.3*height, 5.0, 50)
  @r3 = MRect.new(2, 58.0, 0.332, 0.4*height, 10.0, 35)
  @r4 = MRect.new(1, 120.0, 0.0498, 0.9*height, 15.0, 60)
end
 
def draw()
  background(0)

  @r1.display(height)
  @r2.display(height)
  @r3.display(height)
  @r4.display(height)
 
  @r1.move(mouse_x-(width/2), mouse_y+(height*0.1), 30)
  @r2.move((mouse_x+(width*0.05))%width, mouse_y+(height*0.025), 20)
  @r3.move(mouse_x/4, mouse_y-(height*0.025), 40)
  @r4.move(mouse_x-(width/2), (height-mouse_y), 50)
end
 
class MRect
  def initialize(iw, ixp, ih, iyp, id, it)
    @w = iw   # single bar width
    @xpos = ixp  # rect xposition
    @h = ih  # rect height
    @ypos = iyp  # rect yposition
    @d = id  # single bar distance
    @t = it  # number of bars
  end

  def move(pos_x, pos_y, damping)
    dif = @ypos - pos_y
    if dif.abs > 1
      @ypos -= dif/damping
    end
    dif = @xpos - pos_x
    if dif.abs > 1
      @xpos -= dif/damping
    end
  end

  def display(height)
    @t.times do |i|
      rect(@xpos+(i*(@d+@w)), @ypos, @w, height*@h)
    end
  end
end

